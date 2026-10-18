"use client";

import { useCallback, useState } from "react";
import { AlgorithmInfo } from "@/components/AlgorithmInfo";
import { AppHeader } from "@/components/AppHeader";
import { ErrorNotice } from "@/components/ErrorNotice";
import { MapCanvas } from "@/components/MapCanvas";
import { RoadControls } from "@/components/RoadControls";
import { RouteControls } from "@/components/RouteControls";
import { RouteSummary } from "@/components/RouteSummary";
import { TraversalPanel } from "@/components/TraversalPanel";
import {
  ApiError,
  addLocation,
  addRoad,
  getBfs,
  getDfs,
  getGraph,
  getMinHops,
  getShortest,
  removeRoad,
  setRoadStatus,
} from "@/lib/api";
import { DEFAULT_GRAPH } from "@/lib/defaultGraph";
import type { GraphData, RouteResult, TraversalResult } from "@/lib/types";

export default function HomePage() {
  const [graph, setGraph] = useState<GraphData>(DEFAULT_GRAPH);
  const [sourceId, setSourceId] = useState("college");
  const [destinationId, setDestinationId] = useState("airport");
  const [route, setRoute] = useState<RouteResult | null>(null);
  const [traversal, setTraversal] = useState<TraversalResult | null>(null);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // The built-in default network is the single authoritative graph on load.
  // It is intentionally NOT overwritten by an asynchronous GET /api/graph:
  // that response would replace the default shortly after mount. The graph is
  // only refreshed by explicit user actions (road/location edits) below.
  const refreshGraph = useCallback(async () => {
    const data = await getGraph();
    // Keep the built-in network if the API returns an empty or malformed payload.
    if (data && Array.isArray(data.locations) && Array.isArray(data.roads)) {
      setGraph(data);
    }
  }, []);

  async function run(action: () => Promise<void>) {
    setBusy(true);
    setError(null);
    try {
      await action();
    } catch (err) {
      setError(err instanceof ApiError ? err.message : "Request failed.");
    } finally {
      setBusy(false);
    }
  }

  return (
    <div className="app-shell">
      <AppHeader />
      {error ? <ErrorNotice message={error} /> : null}

      <div className="layout">
        <div>
          <RouteControls
            locations={graph?.locations ?? []}
            sourceId={sourceId}
            destinationId={destinationId}
            busy={busy}
            onSource={(id) => {
              setSourceId(id);
              setRoute(null);
              setTraversal(null);
            }}
            onDestination={(id) => {
              setDestinationId(id);
              setRoute(null);
              setTraversal(null);
            }}
            onShortest={() =>
              run(async () => {
                setRoute(await getShortest(sourceId, destinationId));
              })
            }
            onMinHops={() =>
              run(async () => {
                setRoute(await getMinHops(sourceId, destinationId));
              })
            }
            onBfs={() =>
              run(async () => {
                setTraversal(await getBfs(sourceId));
              })
            }
            onDfs={() =>
              run(async () => {
                setTraversal(await getDfs(sourceId));
              })
            }
          />
          <div style={{ height: 16 }} />
          <AlgorithmInfo route={route} traversal={traversal} />
        </div>

        <MapCanvas
          graph={graph}
          sourceId={sourceId}
          destinationId={destinationId}
          routeLocationIds={route?.locationIds ?? []}
          routeRoadIds={route?.roadIds ?? []}
        />

        <div>
          <RouteSummary graph={graph} route={route} />
          <div style={{ height: 16 }} />
          <TraversalPanel graph={graph} traversal={traversal} />
          <div style={{ height: 16 }} />
          <RoadControls
            graph={graph}
            busy={busy}
            onCloseToggle={(roadId, isOpen) =>
              run(async () => {
                await setRoadStatus(roadId, isOpen);
                await refreshGraph();
                if (route) {
                  if (route.algorithm === "BFS_MIN_HOPS") {
                    setRoute(await getMinHops(sourceId, destinationId));
                  } else {
                    setRoute(await getShortest(sourceId, destinationId));
                  }
                }
              })
            }
            onRemove={(roadId) =>
              run(async () => {
                await removeRoad(roadId);
                await refreshGraph();
              })
            }
            onAddRoad={(fromLocationId, toLocationId, distanceKm) =>
              run(async () => {
                await addRoad({ fromLocationId, toLocationId, distanceKm });
                await refreshGraph();
              })
            }
            onAddLocation={(name, type, x, y) =>
              run(async () => {
                await addLocation({ name, type, x, y });
                await refreshGraph();
              })
            }
          />
        </div>
      </div>
    </div>
  );
}
