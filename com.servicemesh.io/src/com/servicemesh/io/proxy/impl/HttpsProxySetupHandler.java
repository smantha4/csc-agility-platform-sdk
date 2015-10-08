/**
 *              COPYRIGHT (C) 2008-2014 SERVICEMESH, INC.
 *                        ALL RIGHTS RESERVED.
 *                   CONFIDENTIAL AND PROPRIETARY.
 *
 *  ALL SOFTWARE, INFORMATION AND ANY OTHER RELATED COMMUNICATIONS
 *  (COLLECTIVELY, "WORKS") ARE CONFIDENTIAL AND PROPRIETARY INFORMATION THAT
 *  ARE THE EXCLUSIVE PROPERTY OF SERVICEMESH.
 *  ALL WORKS ARE PROVIDED UNDER THE APPLICABLE AGREEMENT OR END USER LICENSE
 *  AGREEMENT IN EFFECT BETWEEN YOU AND SERVICEMESH.  UNLESS OTHERWISE SPECIFIED
 *  IN THE APPLICABLE AGREEMENT, ALL WORKS ARE PROVIDED "AS IS" WITHOUT WARRANTY
 *  OF ANY KIND EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 *  IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
 *  ALL USE, DISCLOSURE AND/OR REPRODUCTION OF WORKS NOT EXPRESSLY AUTHORIZED BY
 *  SERVICEMESH IS STRICTLY PROHIBITED.
 */

package com.servicemesh.io.proxy.impl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;
import javax.net.ssl.SSLEngineResult.Status;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.StatusLine;
import org.apache.http.impl.nio.reactor.IOSessionImpl;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.nio.reactor.IOSession;
import org.apache.log4j.Logger;

import com.servicemesh.io.proxy.PipelinedChannel;

public class HttpsProxySetupHandler
    extends HttpProxySetupHandler
{
    private static final Logger _logger = Logger.getLogger(HttpsProxySetupHandler.class);

    private final IOSessionImpl _ioSession;
    private final PipelinedChannel _pipelinedChannel;
    private SSLEngine _sslEngine = null;
    private ByteBuffer _inEncrypted;
    private ByteBuffer _outEncrypted;
    private ByteBuffer _pipelinedOutEncrypted;
    private ByteBuffer _pipelinedInEncrypted;
    private ByteBuffer _inPlain;
    private ByteBuffer _outPlain;
    private boolean _endOfStream = false;
    private SSLEngineResult _engineResult = null;

    public HttpsProxySetupHandler(final ProxyHost proxyHost, final IOSession ioSession, final PipelinedChannel pipelinedChannel)
        throws IOException
    {
        super(proxyHost, ioSession, pipelinedChannel);

        if (!(ioSession instanceof IOSessionImpl)) {
            throw new RuntimeException("Unsupported IOSession type: " + ioSession.getClass().getName());
        }

        _ioSession = (IOSessionImpl)ioSession;
        _pipelinedChannel = pipelinedChannel;
    }

    @Override
    public PipelinedChannel initialize()
        throws IOException
    {
        SelectionKey key = _ioSession.getSelectionKey();
        int ops = key.interestOps();
        Selector selector = key.selector();
        SocketChannel socketChannel = (SocketChannel)_ioSession.channel();

        key.cancel();

        try {
            socketChannel.configureBlocking(true);
            createSSLEngine();

            // Allocate buffers for network (encrypted) data
            final int netBuffersize = _sslEngine.getSession().getPacketBufferSize();
            _inEncrypted = ByteBuffer.allocate(netBuffersize);
            _outEncrypted = ByteBuffer.allocate(netBuffersize);

            // Allocate buffers for application (unencrypted) data
            final int appBuffersize = _sslEngine.getSession().getApplicationBufferSize();
            _inPlain = ByteBuffer.allocate(appBuffersize);
            _outPlain = ByteBuffer.allocate(appBuffersize);

            _sslEngine.beginHandshake();
            doHandshake();

            HttpRequest request = generateConnectRequest();

            if (request != null) {
                writeRequest(request);

                HttpResponse response = readResponse();

                if (response != null) {
                    int status = response.getStatusLine().getStatusCode();

                    if ((status < 200) || (status > 299)) {
                        throw new RuntimeException("Proxy connect failed: " + response.getStatusLine().getReasonPhrase());
                    }
                } else {
                    throw new RuntimeException("Proxy connect failed to return a valid response");
                }
            }
        } catch (Exception ex) {
            _logger.error("Error initializing proxy", ex);
            throw ex;
        } finally {
            socketChannel.configureBlocking(false);

            HandshakeStatus handshakeStatus = _sslEngine.getHandshakeStatus();
            if ((handshakeStatus == HandshakeStatus.NOT_HANDSHAKING) || (handshakeStatus == HandshakeStatus.FINISHED)) {
                selector.select();

                SelectionKey newKey = socketChannel.register(selector, ops);

                newKey.attach(_ioSession);
                _ioSession.setSelectionKey(newKey);
            }
        }

        _inEncrypted.clear();
        _inPlain.clear();
        _inPlain.position(_inPlain.limit());
        _outEncrypted.clear();
        _outPlain.clear();

        return new SSLPipelinedChannel(_sslEngine);
    }

    private void createSSLEngine()
        throws IOException
    {
        SSLEngine engine = null;

        TrustManager easyTrustManager =
            new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType)
                    throws CertificateException
                {}

                @Override
                public void checkServerTrusted(X509Certificate[] chain,String authType)
                    throws CertificateException
                {}

                @Override
                public X509Certificate[] getAcceptedIssuers()
                {
                    return null;
                }
        };

        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            String host = getProxyHost().getHostName();
            int port = getProxyHost().getPort();

            sslContext.init(null, new TrustManager[] { easyTrustManager }, null);
            engine = sslContext.createSSLEngine(host, port);
            engine.setUseClientMode(true);
        } catch (NoSuchAlgorithmException ex) {
            throw new IOException(ex.getMessage(), ex);
        } catch (KeyManagementException ex) {
            throw new IOException(ex.getMessage(), ex);
        }

        _sslEngine = engine;
    }

    private void doHandshake()
        throws SSLException, IOException
    {
        boolean handshaking = true;

        while (handshaking) {
            switch (_sslEngine.getHandshakeStatus()) {
                case NEED_WRAP:
                    sendEncryptedData();
                    int bytesOut = writePlain(ByteBuffer.allocate(0));
                    sendEncryptedData();
                    if (_engineResult.getStatus() != Status.OK) {
                        handshaking = false;
                    }
                    break;
                case NEED_UNWRAP:
                    // Process incoming handshake data
                    ByteBuffer handshakeData = ByteBuffer.allocate(_sslEngine.getSession().getApplicationBufferSize());

                    if (_pipelinedChannel == null) {
                        if (_inEncrypted.position() < 1) {
                            receiveEncryptedData();
                        }

                        decryptData();
                        readPlain(handshakeData);

                        if (!_inEncrypted.hasRemaining() && _engineResult.getHandshakeStatus() == HandshakeStatus.NEED_UNWRAP) {
                            throw new SSLException("Input buffer is full");
                        }
                    } else {
                        if (_inEncrypted.position() < 1) {
                            receiveEncryptedData();
                        } else {
                            // First try to decrypt
                            if (!decryptData()) {
                                if (_engineResult.getHandshakeStatus() == HandshakeStatus.NEED_UNWRAP) {
                                    receiveEncryptedData();
                                }
                            } else {
                                readPlain(handshakeData);
                            }
                        }

                        if (_sslEngine.getHandshakeStatus() == HandshakeStatus.NEED_UNWRAP) {
                            if (decryptData()) {
                                readPlain(handshakeData);
                            }
                        }
                    }

                    /*
                    if (this.status >= IOSession.CLOSING) {
                        this.inPlain.clear();
                    }
                    */

                    Status resultStatus = _engineResult.getStatus();
                    if ((resultStatus != Status.BUFFER_UNDERFLOW) && (_engineResult.getStatus() != Status.OK)) {
                        handshaking = false;
                    }

                    break;
                case NEED_TASK:
                    doRunTask();
                    break;
                case NOT_HANDSHAKING:
                    handshaking = false;
                    break;
                case FINISHED:
                    break;
            }
        }

        _inEncrypted.clear();
        _inPlain.clear();
        _outEncrypted.clear();
        _outPlain.clear();
    }

    // A works-around for exception handling craziness in Sun/Oracle's SSLEngine
    // implementation.
    //
    // sun.security.pkcs11.wrapper.PKCS11Exception is re-thrown as
    // plain RuntimeException in sun.security.ssl.Handshaker#checkThrown
    private SSLException convert(final RuntimeException ex)
    {
        Throwable cause = ex.getCause();

        if (cause == null) {
            cause = ex;
        }

        return new SSLException(cause);
    }

    private SSLEngineResult doWrap(final ByteBuffer src, final ByteBuffer dst)
        throws SSLException
    {
        SSLEngineResult result;

        if (_pipelinedChannel == null) {
            try {
                _engineResult = _sslEngine.wrap(src, dst);
                result = _engineResult;
            } catch (final RuntimeException ex) {
                throw convert(ex);
            }
        } else {
            if (_pipelinedOutEncrypted == null) {
                _pipelinedOutEncrypted = ByteBuffer.allocate(_sslEngine.getSession().getPacketBufferSize()); 
            } else if (_pipelinedOutEncrypted.position() > 0) {
                try {
                    _pipelinedOutEncrypted.flip();
                    _engineResult = _sslEngine.wrap(_pipelinedOutEncrypted, dst);
                    _pipelinedOutEncrypted.compact();
                } catch (final RuntimeException ex) {
                    throw convert(ex);
                }
            }

            _pipelinedChannel.wrap(src, _pipelinedOutEncrypted);
            try {
                _pipelinedOutEncrypted.flip();
                _engineResult = _sslEngine.wrap(_pipelinedOutEncrypted, dst);
                _pipelinedOutEncrypted.compact();
                result = _engineResult;
            } catch (final RuntimeException ex) {
                throw convert(ex);
            }
        }

        return result;
    }

    private SSLEngineResult doUnwrap(final ByteBuffer src, final ByteBuffer dst)
        throws SSLException
    {
        if (_pipelinedChannel == null) {
            try {
                _engineResult =  _sslEngine.unwrap(src, dst);
            } catch (final RuntimeException ex) {
                throw convert(ex);
            }
        } else {
            if (_pipelinedInEncrypted == null) {
                _pipelinedInEncrypted = ByteBuffer.allocate(_sslEngine.getSession().getPacketBufferSize());
            } else if (_pipelinedInEncrypted.position() > 0) {
                _pipelinedInEncrypted.flip();
                _pipelinedChannel.unwrap(_pipelinedInEncrypted, dst);
                _pipelinedInEncrypted.compact();
            }

            boolean needsUnwrap = true;
            boolean isFirst = true;

            while (needsUnwrap) {
                try {
                    SSLEngineResult engineResult =  _sslEngine.unwrap(src, _pipelinedInEncrypted);

                    if (isFirst) {
                        _engineResult = engineResult;
                        isFirst = false;
                    }

                    if (engineResult.getStatus() == Status.OK) {
                        _pipelinedInEncrypted.flip();
                        _pipelinedChannel.unwrap(_pipelinedInEncrypted, dst);
                        _pipelinedInEncrypted.compact();
                        //needsUnwrap = src.remaining() > 0;
                        needsUnwrap = false;
                    } else {
                        needsUnwrap = false;
                    }
                } catch (final RuntimeException ex) {
                    throw convert(ex);
                }
            }
        }

        return _engineResult;
    }

    private void doRunTask()
        throws SSLException
    {
        try {
            final Runnable r = _sslEngine.getDelegatedTask();
            if (r != null) {
                r.run();
            }
        } catch (final RuntimeException ex) {
            throw convert(ex);
        }
    }

    private void writeRequest(final HttpRequest request)
        throws IOException
    {
        StringBuilder builder = new StringBuilder();
        RequestLine requestLine = request.getRequestLine();

        builder.append(requestLine.getMethod());
        builder.append(" ");
        builder.append(requestLine.getUri());
        builder.append(" ");
        builder.append(requestLine.getProtocolVersion().toString());
        builder.append(CRLF);

        for (Header header : request.getAllHeaders()) {
            builder.append(header.toString());
            builder.append(CRLF);
        }

        builder.append(CRLF);

        ByteBuffer byteBuffer = ByteBuffer.wrap(builder.toString().getBytes());

        writePlain(byteBuffer);
        sendEncryptedData();
    }

    private synchronized int writePlain(final ByteBuffer src)
        throws SSLException
    {
        if (src == null) {
            throw new IllegalArgumentException("Null source buffer");
        }

        /*
        if (this.status != ACTIVE) {
            return -1;
        }
        */

        if (_outPlain.position() > 0) {
            _outPlain.flip();
            doWrap(_outPlain, _outEncrypted);
            _outPlain.compact();
        }

        if (_outPlain.position() == 0) {
            final SSLEngineResult result = doWrap(src, _outEncrypted);

            //if (result.getStatus() == Status.CLOSED) {
                //this.status = CLOSED;
            //}

            return result.bytesConsumed();
        } else {
            return 0;
        }
    }

    private synchronized int readPlain(final ByteBuffer dst)
    {
        if (dst == null) {
            throw new IllegalArgumentException("Null source buffer");
        }

        if (_inPlain.position() > 0) {
            _inPlain.flip();

            final int n = Math.min(_inPlain.remaining(), dst.remaining());
            for (int i = 0; i < n; i++) {
                dst.put(_inPlain.get());
            }

            _inPlain.compact();
            return n;
        } else {
            if (_endOfStream) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    private int sendEncryptedData()
        throws IOException
    {
        _outEncrypted.flip();

        final int bytesWritten = _ioSession.channel().write(_outEncrypted);
        _outEncrypted.compact();
        return bytesWritten;
    }

    private int receiveEncryptedData()
        throws IOException
    {
        /*
        if (this.endOfStream) {
            return -1;
        }
        */

        int bytesRead = _ioSession.channel().read(_inEncrypted);
        if (bytesRead == -1) {
            _endOfStream = true;
        }

        return bytesRead;
    }

    private boolean decryptData()
        throws SSLException
    {
        boolean decrypted = false;

        while (_inEncrypted.position() > 0) {
            _inEncrypted.flip();
            final SSLEngineResult result = doUnwrap(_inEncrypted, _inPlain);
            _inEncrypted.compact();
            if (!_inEncrypted.hasRemaining() && result.getHandshakeStatus() == HandshakeStatus.NEED_UNWRAP) {
                throw new SSLException("Input buffer is full");
            }

            if (result.getStatus() == Status.OK) {
                if (_inPlain.position() > 0) {
                    decrypted = true;
                }
            } else {
                break;
            }

            if (result.getHandshakeStatus() != HandshakeStatus.NOT_HANDSHAKING) {
                break;
            }

            if (_endOfStream) {
                break;
            }
        }

        return decrypted;
    }

    private HttpResponse readResponse()
        throws IOException
    {
        final ByteBuffer responseBuffer = ByteBuffer.allocate(_sslEngine.getSession().getApplicationBufferSize());
        StatusLine statusLine = processStatusLine(responseBuffer);
        HttpResponse response = new BasicHttpResponse(statusLine);

        processHeaders(response, responseBuffer);

        return response;
    }

    private StatusLine processStatusLine(final ByteBuffer responseBuffer)
        throws IOException
    {
        String statusLine = readLine(responseBuffer);

        while (statusLine.trim().length() == 0) {
            statusLine = readLine(responseBuffer);
        }
        statusLine = statusLine.trim();

        String[] tokens = statusLine.split(" ");
        ProtocolVersion protocolVersion = processProtocolVersion(tokens[0]);
        int statusCode = Integer.parseInt(tokens[1]);
        String reason = rebuildReason(tokens);

        return new BasicStatusLine(protocolVersion, statusCode, reason);
    }

    private String rebuildReason(final String[] statusTokens)
    {
        StringBuilder builder = new StringBuilder();

        if ((statusTokens != null) && (statusTokens.length > 2)) {
            for (int i = 2; i < statusTokens.length; i++) {
                builder.append(statusTokens[i]);
                builder.append(" ");
            }
        }

        return builder.toString().trim();
    }

    private ProtocolVersion processProtocolVersion(final String httpVersion)
    {
        String[] tokens = httpVersion.split("/");
        String[] versionTokens = tokens[1].split("\\.");
        int major = Integer.parseInt(versionTokens[0]);
        int minor = Integer.parseInt(versionTokens[1]);

        return new ProtocolVersion(tokens[0], major, minor);
    }

    private void processHeaders(final HttpResponse response, final ByteBuffer responseBuffer)
        throws IOException
    {
        String headerLine = readLine(responseBuffer);

        while (headerLine.length() > 0) {
            int index = headerLine.indexOf(":");
            if (index == -1) {
                throw new IOException("Corrupt header-field: '" + headerLine + "'");
            }

            response.addHeader(headerLine.substring(0, index).trim(), headerLine.substring(index + 1).trim());
            headerLine = readLine(responseBuffer);
        }

        // Read last \n
        _inPlain.get();
    }

    private Byte readByte()
        throws IOException
    {
        Byte rc = null;
        ByteBuffer readBuffer = ByteBuffer.allocate(1);
        int plainBytes = readPlain(readBuffer);

        if (plainBytes == 0) {
            boolean needRead = true;

            while (needRead) {
                int bytesRead = receiveEncryptedData();

                if (bytesRead > 0) {
                    needRead = !decryptData();
                } else if (bytesRead == -1) {
                    needRead = false;
                }
            }

            if (!_endOfStream) {
                readPlain(readBuffer);
                readBuffer.flip();
                rc = new Byte(readBuffer.get());
            } else {
                rc = new Byte("-1");
            }
        } else if (plainBytes > 0) {
            readBuffer.flip();
            rc = new Byte(readBuffer.get());
        } else {
            rc = new Byte("-1");
        }

        return rc;
    }

    private String readLine(final ByteBuffer responseBuffer)
        throws IOException
    {
        StringBuilder lineBuilder = new StringBuilder();

        while (true) {
            int c = readByte().byteValue() & 0xff;

            if (c == -1) {
                throw new IOException("HttpResponse corrupt, input stream closed from " + getProxyHost().getHostName());
            }

            if (c == '\n') {
                continue;
            }

            if (c != '\r') {
                lineBuilder.append((char)c);
            } else {
                break;
            }
        }

        return new String(lineBuilder);
    }
}
