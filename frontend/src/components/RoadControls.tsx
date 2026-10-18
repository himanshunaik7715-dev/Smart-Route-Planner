import { FormEvent } from "react";
import type { GraphData } from "@/lib/types";

type Props = {
  graph: GraphData | null;
  busy: boolean;
  onCloseToggle: (roadId: string, isOpen: boolean) => void;
  onRemove: (roadId: string) => void;
  onAddRoad: (fromLocationId: string, toLocationId: string, distanceKm: number) => void;
  onAddLocation: (name: string, type: string, x: number, y: number) => void;
};

export function RoadControls({ graph, busy, onCloseToggle, onRemove, onAddRoad, onAddLocation }: Props) {
  if (!graph) {
    return null;
  }

  function submitRoad(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const form = new FormData(event.currentTarget);
    const fromLocationId = String(form.get("from") ?? "");
    const toLocationId = String(form.get("to") ?? "");
    const distanceKm = Number(form.get("distance"));
    onAddRoad(fromLocationId, toLocationId, distanceKm);
  }

  function submitLocation(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const form = new FormData(event.currentTarget);
    onAddLocation(
      String(form.get("name") ?? ""),
      String(form.get("type") ?? "custom"),
      Number(form.get("x")),
      Number(form.get("y")),
    );
  }

  return (
    <section className="card">
      <h2>Road controls</h2>
      <p className="muted">Closed roads stay on the map as dashed coral lines and are excluded from every algorithm.</p>
      <ul className="steps">
        {graph.roads.map((road) => {
          const from = graph.locations.find((item) => item.id === road.fromLocationId)?.name ?? road.fromLocationId;
          const to = graph.locations.find((item) => item.id === road.toLocationId)?.name ?? road.toLocationId;
          return (
            <li key={road.id}>
              {from} — {to} ({road.distanceKm} km) {road.isOpen ? "Open" : "Closed"}
              <div className="actions" style={{ marginTop: 6 }}>
                <button
                  className={road.isOpen ? "btn btn-coral" : "btn btn-teal"}
                  type="button"
                  disabled={busy}
                  onClick={() => onCloseToggle(road.id, !road.isOpen)}
                >
                  {road.isOpen ? "Close road" : "Reopen road"}
                </button>
                <button className="btn btn-ghost" type="button" disabled={busy} onClick={() => onRemove(road.id)}>
                  Remove
                </button>
              </div>
            </li>
          );
        })}
      </ul>
      <hr className="dashed" />
      <h3>Add road</h3>
      <form onSubmit={submitRoad}>
        <label className="field">
          <span>From</span>
          <select name="from" required defaultValue={graph.locations[0]?.id}>
            {graph.locations.map((location) => (
              <option key={location.id} value={location.id}>
                {location.name}
              </option>
            ))}
          </select>
        </label>
        <label className="field">
          <span>To</span>
          <select name="to" required defaultValue={graph.locations[1]?.id}>
            {graph.locations.map((location) => (
              <option key={`to-${location.id}`} value={location.id}>
                {location.name}
              </option>
            ))}
          </select>
        </label>
        <label className="field">
          <span>Distance (km)</span>
          <input name="distance" type="number" min="0.1" step="0.1" defaultValue="5" required />
        </label>
        <button className="btn btn-gold" type="submit" disabled={busy}>
          Add road
        </button>
      </form>
      <hr className="dashed" />
      <h3>Add location</h3>
      <form onSubmit={submitLocation}>
        <label className="field">
          <span>Name</span>
          <input name="name" required placeholder="Warehouse" />
        </label>
        <label className="field">
          <span>Type</span>
          <input name="type" defaultValue="custom" required />
        </label>
        <label className="field">
          <span>Schematic X</span>
          <input name="x" type="number" defaultValue="80" required />
        </label>
        <label className="field">
          <span>Schematic Y</span>
          <input name="y" type="number" defaultValue="80" required />
        </label>
        <button className="btn btn-teal" type="submit" disabled={busy}>
          Add location
        </button>
      </form>
    </section>
  );
}
