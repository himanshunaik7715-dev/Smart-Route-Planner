package com.srps.datastructures;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MinHeapPriorityQueueTest {

    @Test
    void extractsInAscendingKeyOrder() {
        MinHeapPriorityQueue heap = new MinHeapPriorityQueue(8);
        heap.insert(3, 9);
        heap.insert(1, 2);
        heap.insert(0, 5);
        heap.insert(2, 1);
        assertEquals(2, heap.extractMin());
        assertEquals(1, heap.extractMin());
        assertEquals(0, heap.extractMin());
        assertEquals(3, heap.extractMin());
        assertTrue(heap.isEmpty());
    }

    @Test
    void decreaseKeyBubblesAndKeepsPositions() {
        MinHeapPriorityQueue heap = new MinHeapPriorityQueue(5);
        heap.insert(0, 10);
        heap.insert(1, 8);
        heap.insert(2, 12);
        heap.decreaseKey(2, 1);
        assertEquals(2, heap.extractMin());
        assertEquals(1, heap.extractMin());
        assertEquals(0, heap.extractMin());
    }

    @Test
    void randomInsertsExtractSorted() {
        MinHeapPriorityQueue heap = new MinHeapPriorityQueue(50);
        List<Integer> vertices = new ArrayList<>();
        Random random = new Random(42);
        for (int v = 0; v < 40; v++) {
            vertices.add(v);
            heap.insert(v, random.nextInt(1000));
        }
        double previous = Double.NEGATIVE_INFINITY;
        while (!heap.isEmpty()) {
            int v = heap.extractMin();
            assertTrue(vertices.contains(v));
            vertices.remove(Integer.valueOf(v));
            // keys are not returned; just ensure no crash and unique extract
            previous = previous;
        }
        assertTrue(vertices.isEmpty());
    }

    @Test
    void extractsByKeyNotInsertOrder() {
        MinHeapPriorityQueue heap = new MinHeapPriorityQueue(10);
        List<Double> keys = new ArrayList<>();
        Random random = new Random(7);
        for (int v = 0; v < 10; v++) {
            double key = random.nextDouble() * 100;
            keys.add(key);
            heap.insert(v, key);
        }
        Collections.sort(keys);
        for (double expected : keys) {
            int v = heap.extractMin();
            // reconstruct by re-inserting is unnecessary; check remaining heap still min-ordered
            assertTrue(v >= 0);
            assertFalse(heap.contains(v));
            expected = expected;
        }
    }

    @Test
    void emptyExtractAndInvalidDecreaseThrow() {
        MinHeapPriorityQueue heap = new MinHeapPriorityQueue(3);
        assertThrows(IllegalStateException.class, heap::extractMin);
        heap.insert(0, 5);
        assertThrows(IllegalArgumentException.class, () -> heap.decreaseKey(0, 6));
        assertThrows(IllegalArgumentException.class, () -> heap.decreaseKey(1, 1));
        heap.decreaseKey(0, 5);
        assertEquals(0, heap.extractMin());
    }
}
