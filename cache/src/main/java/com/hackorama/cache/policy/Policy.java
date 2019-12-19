package com.hackorama.cache.policy;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Defines base policy for ({@link com.hackorama.cache.Cache}
 */
public abstract class Policy {

    protected boolean order = true; // true natural order, false reverse order
    protected Map<String, Long> priorities = new HashMap<>();
    PriorityQueue<Priority> priorityQueue = new PriorityQueue<>();

    public void clear() {
        priorities.clear();
        priorityQueue.clear();
    }

    public String evict() {
        Priority counter = priorityQueue.poll();
        if(counter == null) {
            return null;
        }
        priorities.remove(counter.getKey());
        return  counter.getKey();
    }

    public Policy least() {
        this.order = true;
        return this;
    }

    public Policy most() {
        this.order = false;
        return this;
    }

    protected void setPriority(String key, Long value) {
        priorities.put(key, new Long(value));
        priorityQueue.add(new Priority(key, value, order));
    }

    protected void replacePriority(String key, Long value) {
        Long priority = priorities.get(key);
        if (priority == null) {
            priority = value;
        } else {
            priorityQueue.remove(new Priority(key, priority, order));
        }
        priorities.put(key, priority);
        priorityQueue.add(new Priority(key, priority, order));

    }

    protected void incrementPriority(String key) {
        Long priority = priorities.get(key);
        if (priority == null) {
            priority = (long) 1;
        } else {
            priorityQueue.remove(new Priority(key, priority, order));
            priority++;
        }
        priorities.put(key, priority);
        priorityQueue.add(new Priority(key, priority, order));

    }

    public abstract void trackReads(String key, String value);

    public abstract void trackWrites(String key, String value);

}
