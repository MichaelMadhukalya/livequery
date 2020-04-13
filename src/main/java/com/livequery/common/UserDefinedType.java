package com.livequery.common;

import com.google.common.collect.ImmutableMap;
import com.livequery.annotations.Id;
import com.livequery.annotations.Property;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class UserDefinedType extends Object {
    
    @Property(name = "Customer")
    @Id(id = 1)
    public String Customer;
    
    @Property(name = "Marketplace")
    @Id(id = 2)
    public Integer Marketplace;
    
    @Property(name = "RequestId")
    @Id(id = 3)
    public String RequestId;
    
    @Property(name = "Query")
    @Id(id = 4)
    public String Query;
    
    @Property(name = "Cookie")
    @Id(id = 5)
    public String Cookie;
    
    @Property(name = "Referrer")
    @Id(id = 6)
    public String Referrer;
    
    @Property(name = "Operation")
    @Id(id = 7)
    public String Operation;
    
    @Property(name = "UserAgent")
    @Id(id = 8)
    public String UserAgent;
    
    @Property(name = "Program")
    @Id(id = 9)
    public String Program;
    
    @Property(name = "Headers")
    @Id(id = 10)
    public String Headers;
    
    @Property(name = "FormData")
    @Id(id = 11)
    public String FormData;
    
    @Property(name = "StatusCode")
    @Id(id = 12)
    public Number StatusCode;
    
    @Property(name = "Host")
    @Id(id = 13)
    public String Host;
    
    @Property(name = "Path")
    @Id(id = 14)
    public String Path;
    
    @Property(name = "Method")
    @Id(id = 15)
    public String Method;
    
    @Property(name = "IP")
    @Id(id = 16)
    public String IP;
    
    @Property(name = "Time")
    @Id(id = 17)
    public Double Time;
    
    @Property(name = "StartTime")
    @Id(id = 18)
    public Timestamp StartTime;
    
    @Property(name = "EndTime")
    @Id(id = 19)
    public Timestamp TimeStamp;
    
    private final Map<String, Object> values = new HashMap<>();
    
    private static Map<String, Class<?>> TYPE_NAME_MAP;
    
    private void initValues() {
        if (values.size() > 0) {
            return;
        }
        
        Arrays.stream(getClass().getFields()).forEach(f -> {
            try {
                String name = f.getName();
                Object value = f.get(this);
                /** Ignore composite fields inside class e.g. maps */
                if (StringUtils.equals(name, "values") || value instanceof Map) {
                } else if (StringUtils.equals(name, "TYPE_NAME_MAP") || value instanceof Map) {
                } else {
                    values.put(name, value);
                }
            } catch (IllegalAccessException e) {
                /* Should not happen and won't print as logging is not enabled */
            }
        });
    }
    
    public Map<String, Class<?>> getTypeNameMap() {
        if (TYPE_NAME_MAP != null && TYPE_NAME_MAP.size() > 0) {
            return TYPE_NAME_MAP;
        }
        
        Map<String, Class<?>> typeMap = new HashMap<>();
        Arrays.stream(getClass().getFields()).forEach(f -> {
            String name = f.getName();
            Class<?> type = f.getType();
            /* Ignore fields of type map */
            if (StringUtils.equals(name, "values")) {
            } else if (StringUtils.equals(name, "TYPE_NAME_MAP")) {
            } else {
                typeMap.put(name, type);
            }
        });
        
        TYPE_NAME_MAP = ImmutableMap.copyOf(typeMap);
        return TYPE_NAME_MAP;
    }
    
    @Override
    public String toString() {
        if (values.size() == 0) {
            initValues();
        }
        
        ToStringBuilder builder = new ToStringBuilder(this);
        values.entrySet().stream().forEach(e -> builder.append(e.getKey(), e.getValue()));
        return builder.toString();
    }
    
    @Override
    public boolean equals(Object that) {
        if (null == that) {
            return false;
        }
        
        if (!(that instanceof UserDefinedType)) {
            return false;
        }
        
        if (values.size() == 0) {
            initValues();
        }
        
        /* Init model so that all loaded fields become part of values map */
        UserDefinedType arg = (UserDefinedType) that;
        arg.initValues();
        
        EqualsBuilder builder = new EqualsBuilder();
        values.entrySet().stream().forEach(e -> builder.append(e.getValue(), arg.values.get(e.getKey())));
        return builder.isEquals();
    }
    
    @Override
    public int hashCode() {
        if (values.size() == 0) {
            initValues();
        }
        
        HashCodeBuilder builder = new HashCodeBuilder(17, 37);
        values.entrySet().stream().forEach(e -> builder.append(e.getValue()));
        return builder.toHashCode();
    }
}
