import type { GraphData } from "./types";

/**
 * Built-in canonical schematic network, mirroring the backend seed data
 * (com.srps.config.SeedData). Used to render the map immediately on load and
 * whenever the Java API is unavailable, so the schematic is never blank.
 */
export const DEFAULT_GRAPH: GraphData = {
  locations: [
    { id: "college", name: "College", type: "campus", x: 120, y: 420 },
    { id: "station", name: "Station", type: "transit", x: 280, y: 280 },
    { id: "mall", name: "Mall", type: "commercial", x: 480, y: 180 },
    { id: "hospital", name: "Hospital", type: "medical", x: 500, y: 420 },
    { id: "bus_stop", name: "Bus Stop", type: "transit", x: 280, y: 540 },
    { id: "park", name: "Park", type: "park", x: 700, y: 300 },
    { id: "hotel", name: "Hotel", type: "lodging", x: 680, y: 520 },
    { id: "airport", name: "Airport", type: "airport", x: 900, y: 200 },
    { id: "library", name: "Library", type: "library", x: 140, y: 180 },
    { id: "restaurant", name: "Restaurant", type: "dining", x: 860, y: 480 },
  ],
  roads: [
    { id: "r1", fromLocationId: "college", toLocationId: "station", distanceKm: 3.0, isOpen: true },
    { id: "r2", fromLocationId: "college", toLocationId: "bus_stop", distanceKm: 2.5, isOpen: true },
    { id: "r3", fromLocationId: "college", toLocationId: "library", distanceKm: 2.0, isOpen: true },
    { id: "r4", fromLocationId: "station", toLocationId: "mall", distanceKm: 4.0, isOpen: true },
    { id: "r5", fromLocationId: "station", toLocationId: "hospital", distanceKm: 4.0, isOpen: true },
    { id: "r6", fromLocationId: "bus_stop", toLocationId: "hospital", distanceKm: 3.5, isOpen: true },
    { id: "r7", fromLocationId: "mall", toLocationId: "park", distanceKm: 6.0, isOpen: true },
    { id: "r8", fromLocationId: "hospital", toLocationId: "park", distanceKm: 3.0, isOpen: true },
    { id: "r9", fromLocationId: "hospital", toLocationId: "hotel", distanceKm: 4.5, isOpen: true },
    { id: "r10", fromLocationId: "park", toLocationId: "airport", distanceKm: 5.0, isOpen: true },
    { id: "r11", fromLocationId: "hotel", toLocationId: "airport", distanceKm: 8.0, isOpen: true },
    { id: "r12", fromLocationId: "hotel", toLocationId: "restaurant", distanceKm: 2.0, isOpen: true },
    { id: "r13", fromLocationId: "library", toLocationId: "mall", distanceKm: 5.5, isOpen: true },
    { id: "r14", fromLocationId: "college", toLocationId: "airport", distanceKm: 40.0, isOpen: true },
    { id: "r15", fromLocationId: "mall", toLocationId: "airport", distanceKm: 18.0, isOpen: true },
    { id: "r16", fromLocationId: "bus_stop", toLocationId: "hotel", distanceKm: 9.0, isOpen: true },
  ],
};
