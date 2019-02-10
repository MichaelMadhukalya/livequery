package com.livequery.annotations;

public @interface RunContinuously {

    boolean runContinuously() default true;
}
