package com.hackorama.cache;

import static org.junit.Assert.*;

import org.junit.Test;

import com.hackorama.cache.policy.Policy;
import com.hackorama.cache.policy.FrequencyPolicy;

public class PolicyTest {

    @Test
    public void testDefaultFrequenctlyUsedEvictionPolicy() {
        testLeastPolicy(new FrequencyPolicy());
        testLeastPolicy(new FrequencyPolicy().least());
        testMostPolicy(new FrequencyPolicy().most());
    }

     private void testLeastPolicy(Policy policy) {
        policy.trackReads("1", "");
        policy.trackReads("1", "");
        policy.trackReads("1", "");
        policy.trackReads("2", "");
        policy.trackReads("3", "");
        policy.trackReads("3", "");
        assertEquals( "2", policy.evict());
        assertEquals( "3", policy.evict());
        assertEquals( "1", policy.evict());
        assertEquals( null, policy.evict());
     }

     private void testMostPolicy(Policy policy) {
        policy.trackReads("1", "");
        policy.trackReads("1", "");
        policy.trackReads("1", "");
        policy.trackReads("2", "");
        policy.trackReads("3", "");
        policy.trackReads("3", "");
        assertEquals( "1", policy.evict());
        assertEquals( "3", policy.evict());
        assertEquals( "2", policy.evict());
        assertEquals( null, policy.evict());
     }

}
