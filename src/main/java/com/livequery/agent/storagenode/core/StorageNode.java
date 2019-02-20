package com.livequery.agent.storagenode.core;

import com.livequery.common.AbstractNode;
import com.livequery.common.Schema;
import java.util.Map;
import org.apache.log4j.Logger;

public final class StorageNode extends AbstractNode {

    /**
     * Logger
     */
    private final Logger logger = Logger.getLogger(getClass().getName());

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
        logger.info(String.format("Schema fields : {%d}", schema.size()));

        /* Transform class based on user provided schematization and reload using app class loader*/
        ClassTransformer transformer = new ClassTransformer(Schema.class);
        ClassModifier modifier = new ClassModifier(Schema.class);
        modifier.write(transformer.transform(schema));
    }
}
