package com.servicemesh.io.http;

public class HttpContext extends UrlContext {
   private static final long serialVersionUID = 20131116;

   private static final String DEFAULT_PROTOCOL = "http";
   
   public HttpContext() {
      super();
      
      this.setProtocol("http");
   }

   public HttpContext(String host, int port, String path, QueryParams queryParams) {
      super(DEFAULT_PROTOCOL, host, port, path, queryParams);
   }

   public HttpContext(String host, int port, String path, QueryParam queryParam) {
      super(DEFAULT_PROTOCOL, host, port, path, new QueryParams(queryParam));
   }

   public HttpContext(String host, int port, String path) {
      super(DEFAULT_PROTOCOL, host, port, path);
   }

   public HttpContext(String host, String path, QueryParams queryParams) {
      super(DEFAULT_PROTOCOL, host, path, queryParams);
   }

   public HttpContext(String host, String path) {
      super(DEFAULT_PROTOCOL, host, path);
   }

   public HttpContext(String host) {
      super(DEFAULT_PROTOCOL, host);
   }

   @Override
   public boolean isSecure() {
      return false;
   }
}
