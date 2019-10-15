package cn.com.easyerp.core.cache;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

public abstract class CacheLoader<T> extends Object {
    @Autowired
    protected CacheService cacheService;

    public abstract String getKey();

    public abstract Map<String, T> reload();

    public Object export(Map<String, T> cache) {
        return cache;
    }

    public T getEntry(String key) {
        return (T) this.cacheService.getModuleCacheEntry(this, key);
    }
}
