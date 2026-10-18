export function MapLegend() {
  return (
    <div className="legend" aria-label="Map legend">
      <span className="legend-item">
        <span className="swatch node" /> Location
      </span>
      <span className="legend-item">
        <span className="swatch" /> Road
      </span>
      <span className="legend-item">
        <span className="swatch route" /> Selected route
      </span>
      <span className="legend-item">
        <span className="swatch closed" /> Closed road
      </span>
    </div>
  );
}
