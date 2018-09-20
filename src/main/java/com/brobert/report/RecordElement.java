/**
 *
 */
package com.brobert.report;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author brobert
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RecordElement {
    String defaultFieldName = "Field";

    String name() default defaultFieldName;


    int index() default 0;


    /**
     * @return
     */
    boolean include() default true;
}
