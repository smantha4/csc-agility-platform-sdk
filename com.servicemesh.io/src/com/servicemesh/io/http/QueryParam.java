package com.servicemesh.io.http;

import java.io.Serializable;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

public class QueryParam implements Serializable {
   private static final long   serialVersionUID = 20131116;
   private static final Logger logger           = Logger.getLogger(QueryParam.class);
   
   private final String            name;
   private       ArrayList<String> values;
   
   public QueryParam(String name) throws IllegalArgumentException {
      if (!hasValue(name)) {
         throw new IllegalArgumentException("The parameter name is required.");
      }
      
      this.name = name;
      this.values = new ArrayList<String>();
      
   }
   
   public QueryParam(String name, String value) throws IllegalArgumentException {
      this(name);
      add(value);
   }
   
   public QueryParam(String name, String[] values) throws IllegalArgumentException {
      this(name);
      addAll(values);
   }

   public QueryParam(String name, List<String> values) throws IllegalArgumentException {
      this(name);
      addAll(values);
   }

   public boolean add(String value) {
      boolean retval = false;
      
      if (hasValue(value)) {
         retval = values.add(value);
      }
      
      return retval;
   }
   
   public boolean addAll(String[] values) {
      boolean retval = false;
      
      if ((values != null) && (values.length > 0)) {
         retval = this.values.addAll(Arrays.asList(values));
      }
      
      return retval;
   }
   
   public boolean addAll(List<String> values) {
      boolean retval = false;
      
      if ((values != null) && !values.isEmpty()) {
         retval = this.values.addAll(values);
      }
      
      return retval;
   }

   public boolean remove(String value) {
      boolean retval = false;
      
      if (hasValue(value)) {
         retval = values.remove(value);
      }
      
      return retval;
   }
   
   public void removeAll() {
      values = new ArrayList<String>();
   }
   
   public String getName() {
      return name;
   }
   
   public String[] getValues() {
      if ((values == null) || values.isEmpty()) {
         return new String[0];
      }
      else {
         return values.toArray(new String[values.size()]);
      }
   }
   
   public String toString() {
      return toString(true);
   }
   
   public String toString(boolean encode) {
      return toString(false, encode);
   }
   
   public String toString(boolean asToken, boolean encode) {
      StringBuilder sb   = new StringBuilder();
      StringBuilder vals = new StringBuilder();
      
      if (values != null) {
         StringBuilder tVals = new StringBuilder();
         
         for (String v : values) {
            tVals.append((encode ? encode(v) : v) + ",");
         }
         
         // remove extra comma
         if (hasValue(tVals)) {
            vals.append(tVals.substring(0, tVals.length() - 1));
         }
      }
      
      String nameValue = (encode ? encode(name) : name);
      if (asToken && !hasValue(vals.toString())) {
         sb.append(nameValue);
      }
      else {
         sb.append(nameValue + "=" + vals);
      }
      
      return sb.toString();
   }
   
   private static boolean hasValue(String v) {
      return ((v != null) && !v.isEmpty());
   }

   private static boolean hasValue(StringBuilder v) {
      return ((v != null) && (v.length() > 0));
   }

   private static String encode(String s) {
      try {
         return URLEncoder.encode(s, "UTF-8");
      }
      catch (Exception e) {
         logger.warn("URL encoding the value '" + s + "' failed.  The raw string will be used.");
         return s;
      }
   }
}
