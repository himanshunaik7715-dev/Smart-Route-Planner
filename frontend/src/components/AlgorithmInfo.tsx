import type { RouteResult, TraversalResult } from "@/lib/types";

type Props = {
  route: RouteResult | null;
  traversal: TraversalResult | null;
};

export function AlgorithmInfo({ route, traversal }: Props) {
  const active = route?.algorithm ?? traversal?.algorithm ?? "NONE";
  const copy = explanation(active);
  return (
    <section className="card">
      <h2>Algorithm panel</h2>
      <p>{copy.title}</p>
      <p>{copy.body}</p>
      <p className="complexity">{copy.complexity}</p>
    </section>
  );
}

function explanation(algorithm: string) {
  if (algorithm === "DIJKSTRA") {
    return {
      title: "Dijkstra with a custom indexed min-heap",
      body: "Each vertex stores a tentative distance. The heap always extracts the unfinished vertex with the smallest distance, then relaxes open neighbours. Parent pointers reconstruct the path.",
      complexity: "Time O((V + E) log V). Space O(V). Requires non-negative weights.",
    };
  }
  if (algorithm === "BFS_MIN_HOPS") {
    return {
      title: "BFS minimum-hop routing",
      body: "Breadth-first search uses a custom circular queue. The first time the destination is discovered, the path has the fewest roads. That is not the same as the shortest kilometre distance.",
      complexity: "Time O(V + E). Auxiliary space O(V).",
    };
  }
  if (algorithm === "BFS") {
    return {
      title: "BFS traversal",
      body: "Vertices are visited level by level from the source. Closed roads are skipped. Each vertex is recorded once.",
      complexity: "Time O(V + E). Auxiliary space O(V).",
    };
  }
  if (algorithm === "DFS") {
    return {
      title: "DFS traversal",
      body: "Depth-first search walks as far as possible along open roads before backtracking. A visited array prevents cycles from looping forever.",
      complexity: "Time O(V + E). Auxiliary space O(V) including the recursion stack.",
    };
  }
  return {
    title: "Choose an algorithm action",
    body: "Dijkstra answers shortest distance. BFS answers fewest hops or a level-order traversal. DFS shows a depth-first visit order.",
    complexity: "Graph storage is O(V + E) using an adjacency list.",
  };
}
