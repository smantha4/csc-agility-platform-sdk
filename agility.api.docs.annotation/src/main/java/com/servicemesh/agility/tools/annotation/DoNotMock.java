package com.servicemesh.agility.tools.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this annotation if a class or property should be excluded from data generation by the Mock class.
 * If there is a cycle in a class structure, i.e. A contains B and B contains A, a stack overflow exception
 * would be thrown by the Mock class.  This annotation can be used to tell Mock to ignore an entire class
 * or a specific property.
 * 
 * @author henry
 *
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface DoNotMock {
   boolean value() default true;
}
