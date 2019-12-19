package com.hackorama.cache.policy;

/**
 * An access frequency based cache policy
 */
public class FrequencyPolicy extends Policy {

    @Override
    public void trackReads(String key, String value) {
        incrementPriority(key);
    }

    @Override
    public void trackWrites(String key, String value) {
        // Frequency policy is not tracked on writes
    }

}
