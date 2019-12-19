package com.hackorama.cache;

/**
 * A cache with configurable {@link com.hackorama.cache.policy.PolicyInterface}
 */
public interface Cache {
    /**
     * Removes all elements from this cache
     */
    public void clear();

    /**
     * Returns the value for the specified key from this cache, or null if this
     * cache do not contain a value for the key.
     *
     * @param key the key whose value to be returned
     * @return the value for the key
     */
    public String get(String key);

    /**
     * Adds the specified key and value to this cache, any old value for the same key
     * will be replaced.
     *
     * @param key   the key to be added
     * @param value the value to be added
     */
    public void put(String key, String value);

    /**
     * Removes the key and its value from this cache.
     *
     * @param key the key whose value should be removed
     */
    public void remove(String key);
}
