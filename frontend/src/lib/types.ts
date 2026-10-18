export type Location = {
  id: string;
  name: string;
  type: string;
  x: number;
  y: number;
};

export type Road = {
  id: string;
  fromLocationId: string;
  toLocationId: string;
  distanceKm: number;
  isOpen: boolean;
};

export type GraphData = {
  locations: Location[];
  roads: Road[];
};

export type RouteResult = {
  algorithm: string;
  sourceId: string;
  destinationId: string;
  reachable: boolean;
  locationIds: string[];
  roadIds: string[];
  totalDistanceKm: number | null;
};

export type TraversalResult = {
  algorithm: string;
  sourceId: string;
  visitOrder: string[];
};

export type HealthStatus = {
  status: string;
};

export type ApiErrorBody = {
  status?: number;
  error?: string;
  message?: string;
};
