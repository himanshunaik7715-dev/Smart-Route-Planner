package com.srps.algorithms;

import com.srps.graph.EdgeNode;
import com.srps.graph.Graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Recursive depth-first traversal over open roads. Neighbours are visited in
 * adjacency-list insertion order so tests and viva demos are repeatable.
 * A visited array prevents infinite walks on cycles.
 */
public final class DFS {

    private DFS() {
    }

    public static VisitResult traverse(Graph graph, String sourceId) {
        int source = graph.indexOfLocation(sourceId);
        boolean[] visited = new boolean[graph.getVertexCount()];
        List<String> order = new ArrayList<>();
        visit(graph, source, visited, order);
        return new VisitResult("DFS", sourceId, List.copyOf(order));
    }

    private static void visit(Graph graph, int vertex, boolean[] visited, List<String> order) {
        visited[vertex] = true;
        order.add(graph.getLocation(vertex).getId());
        EdgeNode edge = graph.getAdjacencyHead(vertex);
        while (edge != null) {
            if (graph.isOpenEdge(edge) && !visited[edge.getDestinationIndex()]) {
                visit(graph, edge.getDestinationIndex(), visited, order);
            }
            edge = edge.getNext();
        }
    }
}
