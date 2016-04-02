package com.servicemesh.agility.tools.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to document an action parameter.  It gathers much of the information from the 
 * '@PathParam' and '@QueryParam' annotations.  If those annotations are not used, the '@ApiOtherParam' can
 * be used to describe the parameter manually.
 * 
 * @author henry
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface ApiParam {
   String comment() default "";
}
