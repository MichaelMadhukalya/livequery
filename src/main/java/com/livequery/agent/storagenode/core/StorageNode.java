package com.livequery.agent.storagenode.core;

import com.livequery.common.AbstractNode;
import com.livequery.common.Schema;
import java.util.Map;
import org.apache.log4j.Logger;

public final class StorageNode extends AbstractNode {

    /**
     * Logger
     */
    private final Logger logger = Logger.getLogger(getClass().getCanonicalName());

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
        /* Get user provided codec mapper for schematization */
        CodecMapper mapper = new CodecMapper();
        Map<String, Object> schema = mapper.getSchema();
        logger.info(String.format("Schema fields found in codec file : %d", schema.size()));

        /* Transform class based on user provided schema */
        ClassTransformer transformer = new ClassTransformer(Schema.class);
        ClassModifier modifier = new ClassModifier(Schema.class);
        modifier.write(transformer.transform(schema));

        String className = "com.livequery.common.Schema";
        Class<?> klass;
        try {
            /* Reload class */
            ClassLoader loader = new AppClassLoader(className);
            klass = loader.loadClass(className);
            if (null == klass) {
                throw new ClassNotFoundException(String
                    .format("Unable to load class %s from class path using App class loader",
                        className));
            }

            /* Verify loaded class */
            IClassVerify verifier = new ClassVerify(klass);
            verifier.verify();
        } catch (IllegalAccessException e) {
            logger.error(String.format("Exception trying to access class %s : {%s}", className, e));
        } catch (InstantiationException e) {
            logger.error(
                String.format("Exception trying to instantiate class %s : {%s}", className, e));
        } catch (Exception e) {
            logger.error(String
                .format("Exception re-loading class %s using app class loader: {%s}", className,
                    e));
        }
    }
}
