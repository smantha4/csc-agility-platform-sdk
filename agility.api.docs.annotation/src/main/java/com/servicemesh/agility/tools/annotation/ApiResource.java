package com.servicemesh.agility.tools.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to document a RESTful resource.
 * 
 * @author henry
 *
 */

@Retention(RetentionPolicy.RUNTIME)
//@Inherited()
@Target(ElementType.TYPE)
public @interface ApiResource {
   String  comment()         default "";
   String  description()     default "";
   String  protocol()        default "http";   // this applies to all actions unless the action overrides
   String  version()         default "";
   String  displayName()     default "";
   String  externalDocLink() default "";
   String  introducedIn()    default "";
}
