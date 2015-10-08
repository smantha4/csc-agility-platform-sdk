package com.servicemesh.io.http;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.log4j.Logger;
import org.junit.Test;

public class ServiceTest {
   private static final Logger logger = Logger.getLogger(ServiceTest.class);

   public static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");

   @Test
   public void testQueryParam() throws Exception {
      QueryParam qp = new QueryParam("timestamp", DATE_FMT.format(Calendar.getInstance().getTime()));
      
      assertNotNull(qp);
      
      String s = qp.toString();   // defaults to encoded
      
      assertTrue(isValued(s));
      assertFalse(s.contains(":"));
      
      s = qp.toString(true);   // encoded
      
      assertTrue(isValued(s));
      assertFalse(s.contains(":"));
      
      s = qp.toString(false);
      
      assertTrue(isValued(s));
      assertTrue(s.contains(":"));
   }

   @Test
   public void testQueryParamOrder() throws Exception {
      QueryParams params = new QueryParams();
      
      params.add(new QueryParam("v", "1"));
      params.add(new QueryParam("r", "1"));
      params.add(new QueryParam("a", "1"));
      params.add(new QueryParam("d", "1"));
      
      String expected = "?a=1&d=1&r=1&v=1";
      
      assertEquals(expected, params.asQueryString());
      
      params.setMaintainOrder(true);
      expected = "?v=1&r=1&a=1&d=1";
      
      assertEquals(expected, params.asQueryString());
   }

   @Test
   public void testCaseSensitive() throws Exception {
      QueryParams params = new QueryParams();
      
      params.add(new QueryParam("AWSAccessKeyId", "1"));
      params.add(new QueryParam("Action", "1"));
      
      String expected = "?AWSAccessKeyId=1&Action=1";
      
      assertEquals(expected, params.asQueryString());
      
      params.setCaseSensitive(false);
      expected = "?Action=1&AWSAccessKeyId=1";
      
      assertEquals(expected, params.asQueryString());
   }

   @Test
   public void testRemoveQueryParam() throws Exception {
      QueryParams params = new QueryParams();
      
      params.add(new QueryParam("v", "1"));
      params.add(new QueryParam("r", "1"));
      params.add(new QueryParam("a", "1"));
      params.add(new QueryParam("d", "1"));
      
      String expected = "?a=1&d=1&r=1&v=1";
      
      assertEquals(expected, params.asQueryString());
      
      params.setMaintainOrder(true);
      expected = "?v=1&r=1&a=1&d=1";
      
      assertEquals(expected, params.asQueryString());
      
      params.remove("r");
      expected = "?v=1&a=1&d=1";
      assertEquals(expected, params.asQueryString());
      
      params.setMaintainOrder(false);
      expected = "?a=1&d=1&v=1";
      
      assertEquals(expected, params.asQueryString());
   }

   @Test
   public void testPack() throws Exception {
      QueryParams params = new QueryParams();
      
      params.add(new QueryParam("v", "1"));
      params.add(new QueryParam("r", "1"));
      params.add(new QueryParam("a", "1"));
      params.add(new QueryParam("d", "1=&?"));
      
      String expected = "a1d1%3D%26%3Fr1v1";
      
      assertEquals(expected, params.packEncoded());
      
   }

   private static boolean isValued(String s) {
      return (s != null && !s.isEmpty());
   }
}
