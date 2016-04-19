package com.servicemesh.agility.tools.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The ServiceMesh RESTful service actions may have parameters annotated with '@Context' but desires
 * the information to be documented.  The Parser application reflects on '@PathParam' and '@QueryParam'
 * annotations to gather much of the information about a parameter.  If those annotations are not going
 * to be there, this annotation can be used to provide the Parser with the information it needs.
 * 
 * @author henry
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface ApiOtherParam {
   String name();
   String dataType();
   String paramType() default "internal";
   String comment()   default "";
}
