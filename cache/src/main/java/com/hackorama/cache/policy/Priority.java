package com.hackorama.cache.policy;


/**
 * Stores element key and its policy priority
 */
class Priority implements Comparable<Priority> {

    private String key;
    private boolean order = true; // true for natural order, false for reverse order
    private Long priority;

    public Priority(String key, long priority) {
        this.key = key;
        this.priority = new Long(priority);
    }

    public Priority(String key, long priority, boolean order) {
        this(key, priority);
        this.order = order;
    }

    @Override
    public int compareTo(Priority other) {
        return order ? this.priority.compareTo(other.priority) : this.priority.compareTo(other.priority) * -1;
    }

    @Override
    public boolean equals(Object other) {
        return this.key.equals(((Priority) other).key) && this.priority.equals(((Priority) other).priority);
    }

    public String getKey() {
        return key;
    }

    public Long getPriority() {
        return priority;
    }

};
