package com.servicemesh.agility.tools.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to create a dictionary or lookup structure for the Parser application.  Entries
 * can be created once and the token used multiple times during the annotation process.  This reduces typing
 * and consolidates information in a single place.
 * 
 * @author henry
 *
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PACKAGE, ElementType.TYPE})
public @interface ApiDictionary {
   ApiDictionaryEntry[] entries() default {};
}
