package com.servicemesh.io.http;

/**
 * This class supports the concept of query parameters.  There are two structures; the first is
 * used to map the query parameter, the key, to the query parameter object.  The key is the name
 * from the query parameter object.  The second structure maps positional keys to the object key.
 * The positional structure is needed when the client wants the parameters in the order they
 * were added.
 * 
 * Example
 * -------
 * Query Parameter Pair: name=henry
 * params item ==> [name, QueryParam(name, henry)]
 * orderMap item ==> [0001:name, name]
 * 
 * The value can be retrieved by the key or by the indexed key:
 *    key ==> params.get(key)
 *    indexed key ==> params.get(orderMap.get(indexKey))
 * 
 * @author henry
 *
 */

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

public class QueryParams implements Serializable {
   private static final Logger logger           = Logger.getLogger(QueryParams.class);
   private static final long   serialVersionUID = 20131116;

   private static final NumberFormat NUMBER_FORMATTER = new DecimalFormat("000");

   private HashMap<String, QueryParam> params;                 // used to map keys to objects
   private HashMap<String, String>     orderMap;               // used to map indexed keys to keys
   private HashMap<String, String>     caseMap;                // used to map lowercase keys to keys
   private int                         index;                  // used to keep order of entry - this may be important to some clients such as amazon signatures
   private boolean                     maintainOrder = false;  // defaults to alphabetic order
   private boolean                     caseSensitive = true;   // true will sort the params case sensitive
   
   public QueryParams() {
      params = new HashMap<String, QueryParam>();
      orderMap = new HashMap<String, String>();
      caseMap = new HashMap<String, String>();
      index = 1;
   }
   
   public QueryParams(QueryParam param) {
      this();
      add(param);
   }
   
   public QueryParams(List<QueryParam> params) {
      this();
      addAll(params);
   }
   
   public QueryParams(QueryParam[] params) {
      this();
      addAll(Arrays.asList(params));
   }

   public void addAll(List<QueryParam> params) {
      if ((params != null) && !params.isEmpty()) {
         for (QueryParam qp : params) {
            add(qp);
         }
      }
   }
   
   public void add(QueryParam param) {
      if (param != null) {
         String key = param.getName();
         
         params.put(key, param);
         orderMap.put(buildIndexKey(key), key);
         caseMap.put(key.toLowerCase(), key);
      }
   }
   
   public void remove(String key) {
      if (key != null) {
         QueryParam param = params.remove(key);
         
         // remove from order and case maps if it was found
         if (param != null) {
            if (caseMap.remove(key.toLowerCase()) == null) {
               logger.warn("The parameter key '" + key.toLowerCase() + "' was not found in the case map.  This was unexpected and nothing was removed.");
            }
            
            String removeKey = null;
            for (String s : orderMap.keySet()) {
               String indexedKeySuffix = s.substring(s.indexOf(":") + 1);
               
               if (indexedKeySuffix.equals(key)) {
                  removeKey = s;
                  break;
               }
            }
            
            if (removeKey != null) {
               orderMap.remove(removeKey);
            }
            else {
               logger.warn("The parameter key '" + key + "' was not found as a valud of the order map.  This was unexpected and nothing was removed from the ordermap.");
            }
         }
         else {
            logger.warn("Key '" + key + "' was not found in the params map.  Nothing was deleted.");
         }
      }
   }
   
   private String buildIndexKey(String name) {
      return (NUMBER_FORMATTER.format(index++) + ":" + name);
   }
   
   public String packEncoded() {
      return (asQueryString().replaceAll("=", "").replaceAll("&", "").replaceAll("\\?",  ""));
   }

   public String pack() {
      return (toString().replaceAll("=", "").replaceAll(";", ""));
   }

   public String toString() {
      return buildString(false, false);
   }
   
   public String asQueryString(boolean encode) {
      return buildString(encode, true);
   }

   public String asQueryString() {
      return buildString(true, true);
   }

   private String buildString(boolean encode, boolean buildQueryString) {
      String startToken = "";
      String seperator  = ";";
      
      if (buildQueryString) {
         startToken = "?";
         seperator = "&";
      }

      StringBuilder sb = new StringBuilder(startToken);
      
      if ((params != null) && !params.isEmpty()) {
         StringBuilder buf = new StringBuilder();
         
         if (isMaintainOrder()) {
            String[] keyArray = orderMap.keySet().toArray(new String[orderMap.size()]);
            
            Arrays.sort(keyArray);
            
            for (String key : keyArray) {
               buf.append(params.get(orderMap.get(key)).toString(encode) + seperator);
            }
         }
         else if (!isCaseSensitive()) {
            String[] keyArray = caseMap.keySet().toArray(new String[caseMap.size()]);
            
            Arrays.sort(keyArray);
            
            for (String key : keyArray) {
               buf.append(params.get(caseMap.get(key)).toString(encode) + seperator);
            }
         }
         else {
            String[] keyArray = params.keySet().toArray(new String[params.size()]);
            
            Arrays.sort(keyArray);
            
            for (String key : keyArray) {
               buf.append(params.get(key).toString(encode) + seperator);
            }
         }
         
         // remove extra semicolon
         sb.append(buf.substring(0, buf.length() - 1));
      }
      
      return sb.toString();
   }

   public boolean isMaintainOrder() {
      return maintainOrder;
   }

   public void setMaintainOrder(boolean maintainOrder) {
      this.maintainOrder = maintainOrder;
   }

   public boolean isCaseSensitive() {
      return caseSensitive;
   }

   public void setCaseSensitive(boolean caseSensitive) {
      this.caseSensitive = caseSensitive;
   }

}
