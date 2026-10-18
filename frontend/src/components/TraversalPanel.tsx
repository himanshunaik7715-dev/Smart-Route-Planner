import type { GraphData, TraversalResult } from "@/lib/types";

type Props = {
  graph: GraphData | null;
  traversal: TraversalResult | null;
};

export function TraversalPanel({ graph, traversal }: Props) {
  const names = new Map((graph?.locations ?? []).map((item) => [item.id, item.name]));
  return (
    <section className="card">
      <h2>Traversal order</h2>
      {!traversal ? (
        <p className="muted">Run BFS or DFS from a start location to inspect visit order.</p>
      ) : (
        <>
          <p>
            <strong>{traversal.algorithm}</strong> from {names.get(traversal.sourceId) ?? traversal.sourceId}
          </p>
          <ol className="steps">
            {traversal.visitOrder.map((id, index) => (
              <li key={`${id}-${index}`}>{names.get(id) ?? id}</li>
            ))}
          </ol>
        </>
      )}
    </section>
  );
}
