package com.servicemesh.agility.tools.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used by the ApiDictionary annotation.  This is the structure used to add information
 * to the dictionary.  A token is created and a definition assigned.  The token can be used as many times
 * as needed during the annotation process.  The token will be replaced with the definition by the Parser
 * application.
 * 
 * @author henry
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface ApiDictionaryEntry {
   String token();
   String definition();
}
