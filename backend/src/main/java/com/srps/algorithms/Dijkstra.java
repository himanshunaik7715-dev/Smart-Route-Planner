package com.srps.algorithms;

import com.srps.datastructures.MinHeapPriorityQueue;
import com.srps.graph.EdgeNode;
import com.srps.graph.Graph;
import com.srps.model.Location;

import java.util.Arrays;
import java.util.List;

/**
 * Dijkstra shortest-distance routing using {@link MinHeapPriorityQueue}.
 *
 * <p>Valid only for strictly positive road weights. Distances start at
 * {@link Double#POSITIVE_INFINITY}; the source is 0. Parent and parent-road
 * arrays reconstruct the unique chosen path after the destination is
 * dequeued (finalized) or the heap empties.
 */
public final class Dijkstra {

    private Dijkstra() {
    }

    public static PathResult shortestPath(Graph graph, String sourceId, String destinationId) {
        int source = graph.indexOfLocation(sourceId);
        int destination = graph.indexOfLocation(destinationId);
        if (source == destination) {
            Location location = graph.getLocation(source);
            return new PathResult(
                    "DIJKSTRA",
                    location.getId(),
                    location.getId(),
                    true,
                    List.of(location.getId()),
                    List.of(),
                    0.0
            );
        }

        int n = graph.getVertexCount();
        double[] distance = new double[n];
        int[] parent = new int[n];
        int[] parentRoad = new int[n];
        boolean[] finalized = new boolean[n];
        Arrays.fill(distance, Double.POSITIVE_INFINITY);
        Arrays.fill(parent, -1);
        Arrays.fill(parentRoad, -1);

        MinHeapPriorityQueue heap = new MinHeapPriorityQueue(n);
        distance[source] = 0.0;
        heap.insert(source, 0.0);

        while (!heap.isEmpty()) {
            int u = heap.extractMin();
            if (finalized[u]) {
                continue;
            }
            finalized[u] = true;
            if (u == destination) {
                break;
            }

            EdgeNode edge = graph.getAdjacencyHead(u);
            while (edge != null) {
                if (graph.isOpenEdge(edge)) {
                    int v = edge.getDestinationIndex();
                    if (!finalized[v]) {
                        double candidate = distance[u] + edge.getDistanceKm();
                        if (candidate < distance[v]) {
                            distance[v] = candidate;
                            parent[v] = u;
                            parentRoad[v] = edge.getRoadIndex();
                            if (heap.contains(v)) {
                                heap.decreaseKey(v, candidate);
                            } else {
                                heap.insert(v, candidate);
                            }
                        }
                    }
                }
                edge = edge.getNext();
            }
        }

        if (!finalized[destination] || Double.isInfinite(distance[destination])) {
            return PathResult.unreachable("DIJKSTRA", sourceId, destinationId);
        }
        PathResult path = BFS.reconstruct(graph, "DIJKSTRA", source, destination, parent, parentRoad);
        return path;
    }
}
