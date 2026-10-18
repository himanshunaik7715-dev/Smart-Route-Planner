import type { GraphData, RouteResult } from "@/lib/types";

type Props = {
  graph: GraphData | null;
  route: RouteResult | null;
};

export function RouteSummary({ graph, route }: Props) {
  if (!route) {
    return (
      <section className="card">
        <h2>Route result</h2>
        <p className="muted">Choose a start and destination, then run Dijkstra or BFS minimum hops.</p>
      </section>
    );
  }

  const names = new Map((graph?.locations ?? []).map((item) => [item.id, item.name]));
  const hopCount = route.roadIds.length;

  if (!route.reachable) {
    return (
      <section className="card">
        <h2>Route result</h2>
        <div className="status status-warn" role="status">
          No open route from {names.get(route.sourceId) ?? route.sourceId} to{" "}
          {names.get(route.destinationId) ?? route.destinationId}. The destination is disconnected under the current
          road status.
        </div>
        <p className="muted">Algorithm: {labelFor(route.algorithm)}. This is a valid result, not a server error.</p>
      </section>
    );
  }

  return (
    <section className="card">
      <h2>Route result</h2>
      <p>
        <strong>{labelFor(route.algorithm)}</strong>
      </p>
      <p>
        Total distance: <strong>{route.totalDistanceKm ?? 0} km</strong>
      </p>
      <p>
        Road segments: <strong>{hopCount}</strong>
      </p>
      <h3>Ordered steps</h3>
      <ol className="steps">
        {route.locationIds.map((id, index) => (
          <li key={`${id}-${index}`}>
            {names.get(id) ?? id}
            {index < route.roadIds.length ? ` → ${route.roadIds[index]}` : ""}
          </li>
        ))}
      </ol>
    </section>
  );
}

function labelFor(algorithm: string) {
  if (algorithm === "DIJKSTRA") {
    return "Dijkstra — minimum weighted distance";
  }
  if (algorithm === "BFS_MIN_HOPS") {
    return "BFS — minimum hops (not minimum distance)";
  }
  return algorithm;
}
