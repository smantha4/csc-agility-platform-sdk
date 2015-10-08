package com.servicemesh.io.http;

public class HttpException extends RuntimeException {

   private static final long serialVersionUID = 20131015;

   public HttpException() {
   }

   public HttpException(String msg) {
      super(msg);
   }

   public HttpException(Throwable t) {
      super(t);
   }

   public HttpException(String msg, Throwable t) {
      super(msg, t);
   }

}
