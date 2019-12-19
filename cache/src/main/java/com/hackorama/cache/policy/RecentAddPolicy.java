package com.hackorama.cache.policy;

/**
 * Add time based cache policy.
 */
public class RecentAddPolicy extends Policy {

    @Override
    public void trackReads(String key, String value) {
        // Recently added policy does not track reads
    }

    @Override
    public void trackWrites(String key, String value) {
        replacePriority(key, System.nanoTime());
    }

}
