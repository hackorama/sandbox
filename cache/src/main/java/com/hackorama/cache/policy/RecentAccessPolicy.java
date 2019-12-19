package com.hackorama.cache.policy;

/**
 * Access time based cache policy.
 */
public class RecentAccessPolicy extends Policy {

    @Override
    public void trackReads(String key, String value) {
        replacePriority(key, System.nanoTime());
    }

    @Override
    public void trackWrites(String key, String value) {
        // Recently accessed policy does not track writes
    }

}
