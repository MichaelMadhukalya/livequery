package com.livequery.agent.storagenode.core;

import java.lang.reflect.Field;
import java.util.Arrays;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * <code>ClassVerify</code> will verify that a class can be loaded and its fields, methods and
 * annotations are accessible to caller. The input to this class will be a type token parameterized
 * using a type class whose byte codes were modified by <code>livequery</code> application. A
 * typical caller will call verify after the parameterized type class has been re-loaded using the
 * application class loader: <code>AppClassLoader</code>
 */
class ClassVerify implements IClassVerify {

    /**
     * Logger
     */
    private final Logger logger = LogManager.getLogger(getClass().getCanonicalName());

    /**
     * Type token
     */
    private final Class<?> klass;

    public ClassVerify(Class<?> klass) {
        this.klass = klass;
    }

    @Override
    public boolean verify() throws IllegalAccessException, InstantiationException {
        boolean verified = false;

        Object object = klass.newInstance();

        StringBuffer buffer = new StringBuffer();

        /* Print field names and data types inside for the class type */
        Field[] fields = klass.getFields();
        Arrays.asList(fields).stream()
            .forEach(e -> {
                buffer.append(String.format("Name=%s, Type=%s ; ", e.getName(), e.getType()));
            });

        verified = true;
        logger.info(String.format(buffer.toString()));

        /* TODO: implement logic to do method and annotation verification */

        return verified;
    }
}
