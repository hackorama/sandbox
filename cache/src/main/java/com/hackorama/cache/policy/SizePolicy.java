package com.hackorama.cache.policy;

/**
 * A size based cache policy
 */
public class SizePolicy extends Policy {

    @Override
    public void trackReads(String key, String value) {
        // Size policy is not tracked on reads
    }

    @Override
    public void trackWrites(String key, String value) {
        setPriority(key, new Long(value.length()));
    }

}
