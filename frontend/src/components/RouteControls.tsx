import type { Location } from "@/lib/types";

type Props = {
  locations: Location[];
  sourceId: string;
  destinationId: string;
  busy: boolean;
  onSource: (id: string) => void;
  onDestination: (id: string) => void;
  onShortest: () => void;
  onMinHops: () => void;
  onBfs: () => void;
  onDfs: () => void;
};

export function RouteControls({
  locations,
  sourceId,
  destinationId,
  busy,
  onSource,
  onDestination,
  onShortest,
  onMinHops,
  onBfs,
  onDfs,
}: Props) {
  const ready = Boolean(sourceId && destinationId) && !busy;

  return (
    <section className="card">
      <h2>Route controls</h2>
      <label className="field">
        <span>Start location</span>
        <select value={sourceId} onChange={(event) => onSource(event.target.value)} aria-label="Start location">
          <option value="">Select start</option>
          {locations.map((location) => (
            <option key={location.id} value={location.id}>
              {location.name}
            </option>
          ))}
        </select>
      </label>
      <label className="field">
        <span>Destination</span>
        <select
          value={destinationId}
          onChange={(event) => onDestination(event.target.value)}
          aria-label="Destination"
        >
          <option value="">Select destination</option>
          {locations.map((location) => (
            <option key={location.id} value={location.id}>
              {location.name}
            </option>
          ))}
        </select>
      </label>
      <div className="actions">
        <button className="btn btn-gold" type="button" disabled={!ready} onClick={onShortest}>
          Find shortest distance
        </button>
        <button className="btn btn-teal" type="button" disabled={!ready} onClick={onMinHops}>
          Find minimum hops
        </button>
        <button className="btn btn-teal" type="button" disabled={!sourceId || busy} onClick={onBfs}>
          Run BFS
        </button>
        <button className="btn btn-ghost" type="button" disabled={!sourceId || busy} onClick={onDfs}>
          Run DFS
        </button>
      </div>
    </section>
  );
}
