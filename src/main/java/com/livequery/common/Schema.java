package com.livequery.common;

import com.livequery.annotations.Id;
import com.livequery.annotations.Property;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Schema extends Object {

    @Property(name = "Customer")
    @Id(id = 1)
    public String Customer;

    @Property
    @Id(id = 2)
    public Integer Marketplace;

    @Property
    @Id(id = 3)
    public String RequestId;

    @Property
    @Id(id = 4)
    public String Query;

    @Property
    @Id(id = 5)
    public String Cookie;

    @Property
    @Id(id = 6)
    public String Referrer;

    @Property
    @Id(id = 7)
    public String Operation;

    @Property
    @Id(id = 8)
    public String UserAgent;

    @Property
    @Id(id = 9)
    public String Program;

    @Property
    @Id(id = 10)
    public String Headers;

    @Property
    @Id(id = 11)
    public String FormData;

    @Property
    @Id(id = 12)
    public Number StatusCode;

    @Property
    @Id(id = 13)
    public String Host;

    @Property
    @Id(id = 14)
    public String Path;

    @Property
    @Id(id = 15)
    public String Method;

    @Property
    @Id(id = 16)
    public String IP;

    @Property
    @Id(id = 17)
    public Double Time;

    @Property
    @Id(id = 18)
    public Timestamp StartTime;

    @Property
    @Id(id = 19)
    public Timestamp TimeStamp;

    /**
     * Schema object fields along with default values will be stored in this map. Every time we want
     * to compute the hashCode of an object of this class we will need to first ensure that this map
     * has loaded all the dynamically created fields of this object. This is important since based
     * on user provided codec file fields can be dynamically added/deleted from this class. However,
     * re-computing this map can be expensive and this can add prohibitively large cost to methods
     * such as <code>equals</code> or <code>hashCode</code>. Hence, this map should be pre-computed
     * after this class has been fully loaded by application class loader after taking into
     * consideration user fields provided inside the codec file.
     */
    private final Map<String, Object> __FIELDS__ = new HashMap<>();

    private void init() {
        if (__FIELDS__.size() > 0) {
            return;
        }

        Class<?> klass = getClass();
        List<Field> fields = Arrays.asList(klass.getFields());

        fields.stream().forEach(f -> {
            try {
                String name = f.getName();
                Object value = f.get(this);

                /* Do not put a reference to itself */
                if (StringUtils.equals(f.getName(), "__FIELDS__") || value instanceof Map) {
                } else {
                    __FIELDS__.put(name, value);
                }
            } catch (IllegalAccessException e) {
                /* This should never happen since all instance fields of model object are public */
            }
        });
    }

    @Override
    public String toString() {
        if (__FIELDS__.size() == 0) {
            init();
        }

        ToStringBuilder builder = new ToStringBuilder(this);
        __FIELDS__.entrySet().stream().forEach(e -> builder.append(e.getKey(), e.getValue()));
        return builder.toString();
    }

    @Override
    public boolean equals(Object that) {
        Predicate<Object> isNull = object -> object == null;
        if (isNull.test(that)) {
            return false;
        }

        if (!(that instanceof Schema)) {
            return false;
        }

        if (__FIELDS__.size() == 0) {
            init();
        }

        /* Init model so that all loaded fields become part of __FIELDS__ map */
        Schema arg = (Schema) that;
        arg.init();

        EqualsBuilder builder = new EqualsBuilder();
        __FIELDS__.entrySet().stream()
            .forEach(e -> builder.append(e.getValue(), arg.__FIELDS__.get(e.getKey())));
        return builder.isEquals();
    }

    @Override
    public int hashCode() {
        if (__FIELDS__.size() == 0) {
            init();
        }

        HashCodeBuilder builder = new HashCodeBuilder(17, 37);
        __FIELDS__.entrySet().stream().forEach(e -> builder.append(e.getValue()));
        return builder.toHashCode();
    }
}
