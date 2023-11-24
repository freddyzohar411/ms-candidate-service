package com.avensys.rts.candidate.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.avensys.rts.candidate.enums.Permission;

/**
 * Author: Koh He Xiang
 * This annotation is used to check if the user has any of the
 * permissions specified in the annotation
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresAnyPermission {
    Permission[] value() default {};

}

