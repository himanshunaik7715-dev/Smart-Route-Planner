package com.srps.datastructures;

/**
 * Indexed binary min-heap keyed by tentative Dijkstra distance.
 *
 * <p>Each heap slot stores a vertex index. {@code position[v]} is the heap
 * index of vertex {@code v}, or {@code -1} if {@code v} is not in the heap.
 * Swaps must update both the heap array and {@code position} so
 * {@link #decreaseKey} can bubble from the current slot in O(log V).
 *
 * <p>This class is the Dijkstra priority queue. Do not replace it with
 * {@code java.util.PriorityQueue}.
 */
public final class MinHeapPriorityQueue {

    private int[] heap;
    private double[] keys;
    private final int[] position;
    private int size;

    public MinHeapPriorityQueue(int vertexCapacity) {
        if (vertexCapacity < 0) {
            throw new IllegalArgumentException("Vertex capacity must be non-negative.");
        }
        this.heap = new int[Math.max(8, vertexCapacity)];
        this.keys = new double[Math.max(8, vertexCapacity)];
        this.position = new int[vertexCapacity];
        java.util.Arrays.fill(this.position, -1);
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public boolean contains(int vertexIndex) {
        return vertexIndex >= 0 && vertexIndex < position.length && position[vertexIndex] != -1;
    }

    public void insert(int vertexIndex, double key) {
        ensureVertex(vertexIndex);
        if (contains(vertexIndex)) {
            throw new IllegalArgumentException("Vertex already in heap: " + vertexIndex);
        }
        ensureHeapCapacity(size + 1);
        heap[size] = vertexIndex;
        keys[size] = key;
        position[vertexIndex] = size;
        size++;
        siftUp(size - 1);
    }

    public int extractMin() {
        if (isEmpty()) {
            throw new IllegalStateException("Cannot extract from an empty heap.");
        }
        int minVertex = heap[0];
        swap(0, size - 1);
        size--;
        position[minVertex] = -1;
        if (size > 0) {
            siftDown(0);
        }
        return minVertex;
    }

    public void decreaseKey(int vertexIndex, double newKey) {
        if (!contains(vertexIndex)) {
            throw new IllegalArgumentException("Vertex is not in the heap: " + vertexIndex);
        }
        int index = position[vertexIndex];
        if (newKey > keys[index]) {
            throw new IllegalArgumentException("decreaseKey requires a strictly smaller or equal key.");
        }
        keys[index] = newKey;
        siftUp(index);
    }

    public double keyOf(int vertexIndex) {
        if (!contains(vertexIndex)) {
            throw new IllegalArgumentException("Vertex is not in the heap: " + vertexIndex);
        }
        return keys[position[vertexIndex]];
    }

    private void siftUp(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;
            if (keys[index] >= keys[parent]) {
                break;
            }
            swap(index, parent);
            index = parent;
        }
    }

    private void siftDown(int index) {
        while (true) {
            int left = index * 2 + 1;
            int right = left + 1;
            int smallest = index;
            if (left < size && keys[left] < keys[smallest]) {
                smallest = left;
            }
            if (right < size && keys[right] < keys[smallest]) {
                smallest = right;
            }
            if (smallest == index) {
                break;
            }
            swap(index, smallest);
            index = smallest;
        }
    }

    private void swap(int i, int j) {
        int vertexI = heap[i];
        int vertexJ = heap[j];
        heap[i] = vertexJ;
        heap[j] = vertexI;
        double keyI = keys[i];
        keys[i] = keys[j];
        keys[j] = keyI;
        position[vertexI] = j;
        position[vertexJ] = i;
    }

    private void ensureHeapCapacity(int needed) {
        if (needed <= heap.length) {
            return;
        }
        int cap = Math.max(needed, heap.length * 2);
        heap = java.util.Arrays.copyOf(heap, cap);
        keys = java.util.Arrays.copyOf(keys, cap);
    }

    private void ensureVertex(int vertexIndex) {
        if (vertexIndex < 0 || vertexIndex >= position.length) {
            throw new IllegalArgumentException("Vertex index out of range: " + vertexIndex);
        }
    }
}
