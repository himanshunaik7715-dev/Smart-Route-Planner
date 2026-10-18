package com.srps.datastructures;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArrayQueueTest {

    @Test
    void enqueueDequeueFifoAndGrow() {
        ArrayQueue queue = new ArrayQueue(2);
        assertTrue(queue.isEmpty());
        for (int i = 0; i < 20; i++) {
            queue.enqueue(i);
        }
        assertEquals(20, queue.size());
        for (int i = 0; i < 20; i++) {
            assertEquals(i, queue.dequeue());
        }
        assertTrue(queue.isEmpty());
    }

    @Test
    void wrapAroundStillFifo() {
        ArrayQueue queue = new ArrayQueue(4);
        queue.enqueue(1);
        queue.enqueue(2);
        queue.enqueue(3);
        assertEquals(1, queue.dequeue());
        queue.enqueue(4);
        queue.enqueue(5);
        assertEquals(2, queue.dequeue());
        assertEquals(3, queue.dequeue());
        assertEquals(4, queue.dequeue());
        assertEquals(5, queue.dequeue());
    }

    @Test
    void dequeueEmptyThrows() {
        assertThrows(IllegalStateException.class, () -> new ArrayQueue().dequeue());
    }
}
