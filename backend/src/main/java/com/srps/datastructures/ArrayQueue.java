package com.srps.datastructures;

/**
 * Circular FIFO queue of vertex indices for BFS.
 *
 * <p>Invariant: {@code size} is the number of stored items. Head is the next
 * dequeue slot; tail is the next enqueue slot. When {@code size == data.length}
 * the buffer doubles and elements are laid out linearly from index 0.
 */
public final class ArrayQueue {

    private int[] data;
    private int head;
    private int tail;
    private int size;

    public ArrayQueue() {
        this(8);
    }

    public ArrayQueue(int initialCapacity) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException("Queue capacity must be at least 1.");
        }
        this.data = new int[initialCapacity];
    }

    public void enqueue(int value) {
        if (size == data.length) {
            grow();
        }
        data[tail] = value;
        tail = (tail + 1) % data.length;
        size++;
    }

    public int dequeue() {
        if (isEmpty()) {
            throw new IllegalStateException("Cannot dequeue from an empty queue.");
        }
        int value = data[head];
        head = (head + 1) % data.length;
        size--;
        return value;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    private void grow() {
        int[] grown = new int[data.length * 2];
        for (int i = 0; i < size; i++) {
            grown[i] = data[(head + i) % data.length];
        }
        data = grown;
        head = 0;
        tail = size;
    }
}
