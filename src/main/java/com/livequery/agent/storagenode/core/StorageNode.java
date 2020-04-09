package com.livequery.agent.storagenode.core;

import com.livequery.common.AbstractNode;
import com.livequery.common.UserDefinedType;
import java.util.Map;
import org.apache.log4j.Logger;

public final class StorageNode extends AbstractNode {
    
    /**
     * Logger
     */
    private final Logger logger = Logger.getLogger(getClass().getCanonicalName());
    
    /**
     * UserDefinedType class name
     */
    private final static String USER_DEFINED_TYPE_CLASS = "com.livequery.common.UserDefinedType";
    
    public StorageNode() {
    }
    
    @Override
    protected void pre() {
    }
    
    @Override
    protected void post() {
    }
    
    @Override
    public void run() {
        /* Get user defined fields from codec mapper file */
        CodecMapper mapper = new CodecMapper();
        Map<String, Object> schema = mapper.getSchema();
        logger.info(String.format("Number of User Defined Type(UDT) fields found in codec: {%d}", schema.size()));
        
        /* Transform UDT class based on user provided fields in codec file */
        ClassTransformer transformer = new ClassTransformer(UserDefinedType.class);
        ClassModifier modifier = new ClassModifier(UserDefinedType.class);
        modifier.write(transformer.transform(schema));
        
        try {
            /* Reload class */
            ClassLoader loader = new AppClassLoader(USER_DEFINED_TYPE_CLASS);
            Class<?> klass = loader.loadClass(USER_DEFINED_TYPE_CLASS);
            if (null == klass) {
                throw new ClassNotFoundException(
                    String.format("Unable to load class %s from class path using App class loader", USER_DEFINED_TYPE_CLASS));
            }
            
            /* Verify loaded class */
            IClassVerify verifier = new ClassVerify(klass);
            verifier.verify();
        } catch (IllegalAccessException e) {
            logger.error(String.format("Exception trying to access class %s : {%s}", USER_DEFINED_TYPE_CLASS, e));
        } catch (InstantiationException e) {
            logger.error(String.format("Exception trying to instantiate class %s : {%s}", USER_DEFINED_TYPE_CLASS, e));
        } catch (Exception e) {
            logger
                .error(String.format("Exception re-loading class %s using app class loader: {%s}", USER_DEFINED_TYPE_CLASS, e));
        }
    }
}
