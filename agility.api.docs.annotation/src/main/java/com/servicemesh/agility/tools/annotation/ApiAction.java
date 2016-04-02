package com.servicemesh.agility.tools.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to mark an HTTP method of a RESTful service.
 * 
 * @author henry
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ApiAction {
   String        comment()         default "";
   String        description()     default "";
   String        protocol()        default "";       // overrides resource value if need be
   String        displayName()     default "";       // if empty default to uri
   String        authRequired()    default "true";
   String        externalDocLink() default "";
   String        internalUse()     default "false";
   String        introducedIn()    default "";
   ApiRequest[]  requests()        default {};
   ApiResponse[] responses()       default {};
   ApiAcl[]      acls()            default {};
}
