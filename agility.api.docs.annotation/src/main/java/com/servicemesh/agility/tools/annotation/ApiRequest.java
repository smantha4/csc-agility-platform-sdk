package com.servicemesh.agility.tools.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to document the Request or input data to the action.  It identifies the data type
 * as well as any fields that will be required by the action.
 * 
 * The optionalFields and requiredFields stores a comma separated list of Java property names for the class
 * listed in the dataType property.  The asterisk ('*') can be used to refer to all the properties defined in
 * the class.
 * 
 * The containerType property is used document that multiple objects are needed.  Valid values represent Java
 * types such as ARRAY, LIST, SET.
 * 
 * There is only one Request object honored by the Parser.
 * 
 * @author henry
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface ApiRequest {
   String comment()        default "";
   String dataType();
   String containerType()  default "";
   String optionalFields() default "";   // allow "*"
   String requiredFields() default "";   // allow "*"
}
