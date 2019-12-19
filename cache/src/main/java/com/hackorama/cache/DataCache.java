package com.hackorama.cache;

import java.util.HashMap;
import java.util.Map;

import com.hackorama.cache.policy.FrequencyPolicy;
import com.hackorama.cache.policy.Policy;

public class DataCache implements Cache {

    private Map<String, String> map = new HashMap<String, String>();
    private Policy policy = new FrequencyPolicy();
    private long size = 10;

    public DataCache() {

    }

    public DataCache(long size) {
        this.size = size;
    }

    @Override
    public void clear() {
        policy.clear();
        map.clear();
    }

    @Override
    public String get(String key) {
        String value = map.get(key);
        policy.trackReads(key, value);
        return value;
    }

    @Override
    public void put(String key, String value) {
        policy.trackWrites(key, value);
        if (map.size() >= size) {
            // Find a valid key to remove based on eviction policy,
            // also lazy remove any removed keys from policy tracking
            String evict = policy.evict();
            while (!map.containsKey(evict)) {
                evict = policy.evict();
            }
            map.remove(evict);
        }
        map.put(key, value);
    }

    @Override
    public void remove(String key) {
        map.remove(key);
    }

    public DataCache size(long size) {
        this.size = size;
        return this;
    }

    public DataCache with(Policy evictionStrategy) {
        this.policy = evictionStrategy;
        return this;
    }

}
