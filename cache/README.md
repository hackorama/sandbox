# Cache

Simple String cache with extensible eviction policy and fluid composition.

```
Cache leastFrequentlyUsedCache = new DataCache().size(64).with(new FrequencyPolicy());
Cache mostFrequenctlyUsedCache = new DataCache().size(64).with(new FrequencyPolicy().most());

Cache leastSizedCache = new DataCache().size(64).with(new SizePolicy());
Cache mostSizedCache = new DataCache().size(64).with(new SizePolicy().most());

Cache leastRecenlyAddedCache = new DataCache().size(64).with(new RecentAddPolicy());
Cache mostRecentlyAddedCache = new DataCache().size(64).with(new RecentAddPolicy().most());

Cache leastRecentlyAccessedCache = new DataCache().size(64).with(new RecentAccessPolicy());
Cache mostRecenclyAccessedCache = new DataCache().size(64).with(new RecentAccessPolicy().most());
```

## Cache

[Cache](src/main/java/com/hackorama/cache/Cache.java) interface
```
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
```

## Policy

New eviction policies cab be added by extending [Policy](src/main/java/com/hackorama/cache/policy/Policy.java) and implementing  the specific priority (size, age, frequency) tracking during cache access.

```
public class FrequencyPolicy extends Policy {
    ...
    @Override
    public void trackReads(String key, String value) {
        incrementPriority(key);
    }
    ...

public class SizePolicy extends Policy {
    ...
    @Override
    public void trackWrites(String key, String value) {
        setPriority(key, new Long(value.length()));
    }
    ...

public class RecentAccessPolicy extends Policy {
    ...
    @Override
    public void trackReads(String key, String value) {
        replacePriority(key, System.nanoTime());
    }
    ...
```






