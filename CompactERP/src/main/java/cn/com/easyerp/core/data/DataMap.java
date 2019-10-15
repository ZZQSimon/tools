package cn.com.easyerp.core.data;

import java.util.HashMap;

public class DataMap extends HashMap<String, Object> {
    private static final long serialVersionUID = -8077589425374694228L;

    public DataMap(int initialCapacity) {
        super(initialCapacity);
    }

    public DataMap() {
    }
}
