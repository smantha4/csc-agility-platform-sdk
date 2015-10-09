package com.servicemesh.io.http;

public class HttpsContext extends UrlContext {
   private static final long serialVersionUID = 20131116;

   private static final String DEFAULT_PROTOCOL    = "https";
   private static final int    DEFAULT_SECURE_PORT = 443;
   
   
   public HttpsContext() {
      super();
      
      this.setProtocol("https");
      this.setPort(DEFAULT_SECURE_PORT);
   }

   public HttpsContext(String host, int port, String path, QueryParams queryParams) {
      super(DEFAULT_PROTOCOL, host, port, path, queryParams);
   }

   public HttpsContext(String host, int port, String path, QueryParam queryParam) {
      super(DEFAULT_PROTOCOL, host, port, path, new QueryParams(queryParam));
   }

   public HttpsContext(String host, int port, String path) {
      super(DEFAULT_PROTOCOL, host, port, path);
   }

   public HttpsContext(String host, String path, QueryParams queryParams) {
      super(DEFAULT_PROTOCOL, host, path, queryParams);
   }

   public HttpsContext(String host, String path) {
      super(DEFAULT_PROTOCOL, host, path);
   }

   public HttpsContext(String host) {
      super(DEFAULT_PROTOCOL, host);
   }

   public HttpsContext(String host, int port) {
      super(DEFAULT_PROTOCOL, host, port);
   }

   @Override
   public boolean isSecure() {
      return true;
   }
   
   @Override
   public int getDefaultPort() {
      return DEFAULT_SECURE_PORT;
   }

}
