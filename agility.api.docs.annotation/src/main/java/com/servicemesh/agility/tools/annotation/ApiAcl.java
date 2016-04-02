package com.servicemesh.agility.tools.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used by the ApiAction annotation.  It identifies any credentials or access
 * control properties required by the action.
 * 
 * @author henry
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface ApiAcl {
   String name();
   String description() default "";
}
