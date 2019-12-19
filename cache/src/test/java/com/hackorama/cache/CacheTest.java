package com.hackorama.cache;

import static org.junit.Assert.*;

import org.junit.Test;

import com.hackorama.cache.policy.RecentAddPolicy;
import com.hackorama.cache.policy.FrequencyPolicy;
import com.hackorama.cache.policy.RecentAccessPolicy;
import com.hackorama.cache.policy.SizePolicy;

public class CacheTest {

    @Test
    public void testCacheClearing() {
        Cache cache = new DataCache().size(3);
        cache.put("2", "TWO");
        cache.put("1", "ONE");
        cache.put("3", "THREE");
        assertEquals("ONE", cache.get("1"));
        assertEquals("TWO", cache.get("2"));
        assertEquals("THREE", cache.get("3"));
        cache.clear();
        assertEquals("Verify clear removed all elements", null, cache.get("1"));
        assertEquals("Verify clear removed all elements", null, cache.get("2"));
        assertEquals("Verify clear removed all elements", null, cache.get("3"));
        testLeastFrequency(cache); // and works normally after clear
    }

    @Test
    public void testCacheRemovalWorksWithFrequencyPolicy() {
        Cache cache = new DataCache().size(3);
        cache.put("2", "TWO");
        cache.put("1", "ONE");
        cache.put("3", "THREE");
        assertEquals("ONE", cache.get("1")); // least frequent
        assertEquals("TWO", cache.get("2"));
        assertEquals("TWO", cache.get("2"));
        assertEquals("TWO", cache.get("2"));
        assertEquals("THREE", cache.get("3")); // next least frequent
        cache.remove("1");
        cache.put("4", "FOUR"); // takes removed elements spot
        assertEquals("FOUR", cache.get("4"));
        assertEquals("FOUR", cache.get("4"));
        assertEquals("FOUR", cache.get("4")); //make not least frequent
        cache.put("5", "FIVE"); // takes removed elements spot
        assertEquals("Verify removed element", null, cache.get("1"));
        assertEquals("TWO", cache.get("2"));
        assertEquals("Verify after removing most frequent next least frequent is evicted", null, cache.get("3"));
        assertEquals("FOUR", cache.get("4"));
        assertEquals("FIVE", cache.get("5"));
    }

    @Test
    public void testCacheWithDefaultFrequencyPolicy() {
        // All three cache initialization modes uses the default least frequent policy
        testLeastFrequency(new DataCache().size(3));
        testLeastFrequency(new DataCache().size(3).with(new FrequencyPolicy()));
        testLeastFrequency(new DataCache().size(3).with(new FrequencyPolicy().least()));
    }

    @Test
    public void testCacheWithDefaultSizePolicy() {
        testLeastSize(new DataCache().size(3).with(new SizePolicy()));
        testLeastSize(new DataCache().size(3).with(new SizePolicy().least()));
    }

    @Test
    public void testCacheWithLeastRecentPolicy() {
        testLeastRecentAdd(new DataCache().size(3).with(new RecentAddPolicy()));
        testLeastRecentAccess(new DataCache().size(3).with(new RecentAccessPolicy()));
    }

    @Test
    public void testCacheWithMaxSizePolicy() {
        testMostSize(new DataCache().size(3).with(new SizePolicy().most()));
    }

    @Test
    public void testCacheWithMostFrequencyPolicy() {
        testMostFrequency(new DataCache().size(3).with(new FrequencyPolicy().most()));
    }

    @Test
    public void testCacheWithMostRecentPolicy() throws InterruptedException {
        testMostRecentAdd(new DataCache().size(3).with(new RecentAddPolicy().most()));
        testMostRecentAccess(new DataCache().size(3).with(new RecentAccessPolicy().most()));
    }

    private void testLeastFrequency(Cache cache) {
        cache.put("2", "TWO");
        cache.put("1", "ONE");
        cache.put("3", "THREE");
        assertEquals("ONE", cache.get("1"));
        assertEquals("ONE", cache.get("1"));
        assertEquals("ONE", cache.get("1"));
        assertEquals("TWO", cache.get("2"));
        assertEquals("TWO", cache.get("2"));
        assertEquals("THREE", cache.get("3")); // least frequent
        cache.put("4", "FOUR");
        assertEquals("ONE", cache.get("1"));
        assertEquals("TWO", cache.get("2"));
        assertEquals("Check least frequent element is evicted from cache", null, cache.get("3"));
        assertEquals("Check the new element took the evicted spot in cache", "FOUR", cache.get("4"));
    }

    private void testLeastRecentAccess(Cache cache) {
        cache.put("1", "ONE");
        cache.put("3", "THREE");
        cache.put("2", "TWO");
        assertEquals("TWO", cache.get("2")); // least recently accessed
        assertEquals("ONE", cache.get("1"));
        assertEquals("THREE", cache.get("3"));
        cache.put("4", "FOUR");
        assertEquals("ONE", cache.get("1"));
        assertEquals("Check least recently accessed element is evicted", null, cache.get("2"));
        assertEquals("THREE", cache.get("3"));
        assertEquals("Check the new element took the evicted spot in cache", "FOUR", cache.get("4"));
    }

    private void testLeastRecentAdd(Cache cache) {
        cache.put("2", "TWO"); // least recently added
        cache.put("1", "ONE");
        cache.put("3", "THREE");
        assertEquals("ONE", cache.get("1"));
        assertEquals("TWO", cache.get("2"));
        assertEquals("THREE", cache.get("3"));
        cache.put("4", "FOUR");
        assertEquals("ONE", cache.get("1"));
        assertEquals("Check least recent is evicted", null, cache.get("2"));
        assertEquals("THREE", cache.get("3"));
        assertEquals("Check the new element took the evicted spot in cache", "FOUR", cache.get("4"));
    }

    private void testLeastSize(Cache cache) {
        cache.put("2", "XX");
        cache.put("1", "X");
        cache.put("3", "XXX");
        assertEquals("X", cache.get("1")); // least size
        assertEquals("XX", cache.get("2"));
        assertEquals("XXX", cache.get("3"));
        cache.put("4", "XXXX");
        assertEquals("Check least sized element is evicted from cache", null, cache.get("1"));
        assertEquals("XX", cache.get("2"));
        assertEquals("XXX", cache.get("3"));
        assertEquals("Check the new element took the evicted spot in cache", "XXXX", cache.get("4"));
    }

    private void testMostFrequency(Cache cache) {
        cache.put("1", "ONE");
        cache.put("2", "TWO");
        cache.put("3", "THREE");
        assertEquals("ONE", cache.get("1"));
        assertEquals("ONE", cache.get("1"));
        assertEquals("ONE", cache.get("1")); // most frequent
        assertEquals("TWO", cache.get("2"));
        assertEquals("TWO", cache.get("2"));
        assertEquals("THREE", cache.get("3"));
        cache.put("4", "FOUR");
        assertEquals("Check most frequent element is evicted", null, cache.get("1"));
        assertEquals("TWO", cache.get("2"));
        assertEquals("THREE", cache.get("3"));
        assertEquals("Check the new element took the evicted spot in cache", "FOUR", cache.get("4"));
    }

    private void testMostRecentAccess(Cache cache) {
        cache.put("1", "ONE");
        cache.put("3", "THREE");
        cache.put("2", "TWO");
        assertEquals("ONE", cache.get("1"));
        assertEquals("THREE", cache.get("3"));
        assertEquals("TWO", cache.get("2")); // most recently accessed
        cache.put("4", "FOUR");
        assertEquals("ONE", cache.get("1"));
        assertEquals("Check most recently accessed element is evicted", null, cache.get("2"));
        assertEquals("THREE", cache.get("3"));
        assertEquals("Check the new element took the evicted spot in cache", "FOUR", cache.get("4"));
    }

    private void testMostRecentAdd(Cache cache) {
        cache.put("1", "ONE");
        cache.put("3", "THREE");
        cache.put("2", "TWO"); // most recently added
        assertEquals("ONE", cache.get("1"));
        assertEquals("TWO", cache.get("2"));
        assertEquals("THREE", cache.get("3"));
        cache.put("4", "FOUR");
        assertEquals("ONE", cache.get("1"));
        assertEquals("Check most recently added element is evicted", null, cache.get("2"));
        assertEquals("THREE", cache.get("3"));
        assertEquals("Check the new element took the evicted spot in cache", "FOUR", cache.get("4"));
    }

    private void testMostSize(Cache cache) {
        cache.put("2", "XX");
        cache.put("1", "X");
        cache.put("3", "XXX");
        assertEquals("X", cache.get("1"));
        assertEquals("XX", cache.get("2"));
        assertEquals("XXX", cache.get("3"));  // most size
        cache.put("4", "XXXX");
        assertEquals("X", cache.get("1"));
        assertEquals("XX", cache.get("2"));
        assertEquals("Check most sized element is evicted from cache", null, cache.get("3"));
        assertEquals("Check the new element took the evicted spot in cache", "XXXX", cache.get("4"));
    }

}
