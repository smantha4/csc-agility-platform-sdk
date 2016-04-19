package com.servicemesh.agility.tools.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this annotation to mark an entity to be deprecated.
 * 
 * @author henry
 *
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
public @interface ApiDeprecated {
   String asOfRelease() default "";   // release that announced the deprecation
   String comment()     default "";
   String alternative() default "";   // any alternative to the entity
   String eolRelease()  default "";   // end of life release
}
