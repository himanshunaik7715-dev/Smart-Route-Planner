import type { GraphData, Location, Road } from "@/lib/types";
import { MapLegend } from "./MapLegend";

type Props = {
  graph: GraphData | null;
  sourceId: string;
  destinationId: string;
  routeLocationIds: string[];
  routeRoadIds: string[];
};

function locationById(locations: Location[], id: string) {
  return locations.find((item) => item.id === id);
}

export function MapCanvas({ graph, sourceId, destinationId, routeLocationIds, routeRoadIds }: Props) {
  if (!graph) {
    return (
      <section className="card map-card">
        <h2>Schematic map</h2>
        <p className="muted">Waiting for network data from the Java API.</p>
      </section>
    );
  }

  const routeSet = new Set(routeRoadIds);
  const stepIndex = new Map(routeLocationIds.map((id, index) => [id, index + 1]));

  return (
    <section className="card map-card">
      <h2>Schematic map</h2>
      <p className="muted">
        Positions are schematic demonstration coordinates, not real-world geography. Distances are in kilometres.
      </p>
      <div className="map-wrap">
        <svg className="map-svg" viewBox="0 0 1000 700" role="img" aria-label="Schematic road network">
          {graph.roads.map((road) => (
            <RoadLine
              key={road.id}
              road={road}
              graph={graph}
              selected={routeSet.has(road.id)}
            />
          ))}
          {graph.locations.map((location) => {
            const isSource = location.id === sourceId;
            const isDest = location.id === destinationId;
            const fill = isSource ? "#2BA8A2" : isDest ? "#FFD23F" : "#183536";
            const label = isSource ? `${location.name} (start)` : isDest ? `${location.name} (destination)` : location.name;
            const step = stepIndex.get(location.id);
            return (
              <g key={location.id}>
                <circle cx={location.x} cy={location.y} r={12} fill={fill} />
                {step ? (
                  <text x={location.x} y={location.y + 4} textAnchor="middle" fontSize="11" fill={isDest ? "#183536" : "#fff"} fontWeight="700">
                    {step}
                  </text>
                ) : null}
                <text x={location.x + 16} y={location.y - 10} fontSize="13" fontWeight="700" fill="#183536">
                  {label}
                </text>
              </g>
            );
          })}
        </svg>
      </div>
      <MapLegend />
    </section>
  );
}

function RoadLine({
  road,
  graph,
  selected,
}: {
  road: Road;
  graph: GraphData;
  selected: boolean;
}) {
  const from = locationById(graph.locations, road.fromLocationId);
  const to = locationById(graph.locations, road.toLocationId);
  if (!from || !to) {
    return null;
  }
  const mx = (from.x + to.x) / 2;
  const my = (from.y + to.y) / 2;
  const closed = !road.isOpen;
  const stroke = closed ? "#EF6C4A" : selected ? "#1E8C86" : "#B8CDCC";
  const width = selected ? 7 : 3;
  return (
    <g>
      <line
        x1={from.x}
        y1={from.y}
        x2={to.x}
        y2={to.y}
        stroke={stroke}
        strokeWidth={width}
        strokeDasharray={closed ? "8 6" : undefined}
        strokeLinecap="round"
      />
      <text x={mx} y={my - 8} textAnchor="middle" fontSize="12" fill="#607778">
        {road.distanceKm} km{closed ? " · Closed" : ""}
      </text>
    </g>
  );
}
