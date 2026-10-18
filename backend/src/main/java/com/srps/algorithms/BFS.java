package com.srps.algorithms;

import com.srps.datastructures.ArrayQueue;
import com.srps.graph.EdgeNode;
import com.srps.graph.Graph;
import com.srps.model.Location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Breadth-first search over open roads. This finds a minimum-hop path, not a
 * minimum weighted distance. Vertices are marked visited at enqueue time so
 * each vertex enters the custom circular queue at most once.
 */
public final class BFS {

    private BFS() {
    }

    public static VisitResult traverse(Graph graph, String sourceId) {
        int source = graph.indexOfLocation(sourceId);
        boolean[] visited = new boolean[graph.getVertexCount()];
        List<String> order = new ArrayList<>();
        ArrayQueue queue = new ArrayQueue(Math.max(8, graph.getVertexCount()));

        visited[source] = true;
        queue.enqueue(source);

        while (!queue.isEmpty()) {
            int current = queue.dequeue();
            order.add(graph.getLocation(current).getId());
            EdgeNode edge = graph.getAdjacencyHead(current);
            while (edge != null) {
                if (graph.isOpenEdge(edge) && !visited[edge.getDestinationIndex()]) {
                    visited[edge.getDestinationIndex()] = true;
                    queue.enqueue(edge.getDestinationIndex());
                }
                edge = edge.getNext();
            }
        }
        return new VisitResult("BFS", sourceId, List.copyOf(order));
    }

    public static boolean reachable(Graph graph, String sourceId, String destinationId) {
        return minHopPath(graph, sourceId, destinationId).isReachable();
    }

    public static PathResult minHopPath(Graph graph, String sourceId, String destinationId) {
        int source = graph.indexOfLocation(sourceId);
        int destination = graph.indexOfLocation(destinationId);
        if (source == destination) {
            Location location = graph.getLocation(source);
            return new PathResult(
                    "BFS_MIN_HOPS",
                    location.getId(),
                    location.getId(),
                    true,
                    List.of(location.getId()),
                    List.of(),
                    0.0
            );
        }

        int n = graph.getVertexCount();
        boolean[] visited = new boolean[n];
        int[] parent = new int[n];
        int[] parentRoad = new int[n];
        java.util.Arrays.fill(parent, -1);
        java.util.Arrays.fill(parentRoad, -1);

        ArrayQueue queue = new ArrayQueue(Math.max(8, n));
        visited[source] = true;
        queue.enqueue(source);

        while (!queue.isEmpty()) {
            int current = queue.dequeue();
            if (current == destination) {
                break;
            }
            EdgeNode edge = graph.getAdjacencyHead(current);
            while (edge != null) {
                int next = edge.getDestinationIndex();
                if (graph.isOpenEdge(edge) && !visited[next]) {
                    visited[next] = true;
                    parent[next] = current;
                    parentRoad[next] = edge.getRoadIndex();
                    queue.enqueue(next);
                }
                edge = edge.getNext();
            }
        }

        if (!visited[destination]) {
            return PathResult.unreachable("BFS_MIN_HOPS", sourceId, destinationId);
        }
        return reconstruct(graph, "BFS_MIN_HOPS", source, destination, parent, parentRoad);
    }

    static PathResult reconstruct(
            Graph graph,
            String algorithm,
            int source,
            int destination,
            int[] parent,
            int[] parentRoad
    ) {
        List<String> locationIds = new ArrayList<>();
        List<String> roadIds = new ArrayList<>();
        double total = 0.0;
        int cursor = destination;
        while (cursor != source) {
            locationIds.add(graph.getLocation(cursor).getId());
            int roadIndex = parentRoad[cursor];
            roadIds.add(graph.getRoad(roadIndex).getId());
            total += graph.getRoad(roadIndex).getDistanceKm();
            cursor = parent[cursor];
            if (cursor < 0) {
                return PathResult.unreachable(algorithm,
                        graph.getLocation(source).getId(),
                        graph.getLocation(destination).getId());
            }
        }
        locationIds.add(graph.getLocation(source).getId());
        Collections.reverse(locationIds);
        Collections.reverse(roadIds);
        return new PathResult(
                algorithm,
                graph.getLocation(source).getId(),
                graph.getLocation(destination).getId(),
                true,
                List.copyOf(locationIds),
                List.copyOf(roadIds),
                total
        );
    }
}
