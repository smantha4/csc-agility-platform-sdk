package com.servicemesh.agility.tools.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to document a Java class.  It is also used in schema definitions to document
 * ComplexType statements.
 * 
 * @author henry
 *
 */
@Retention(RetentionPolicy.RUNTIME)
//@Inherited()
@Target(ElementType.TYPE)
public @interface ApiModel {
   String  comment()         default "";
   String  description()     default "";
   String  externalDocLink() default "";
   String  introducedIn()    default "";
}
