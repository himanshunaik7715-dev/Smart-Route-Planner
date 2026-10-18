import type {
  ApiErrorBody,
  GraphData,
  Location,
  Road,
  RouteResult,
  TraversalResult,
} from "./types";

const API_BASE = process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080";

export class ApiError extends Error {
  status: number;

  constructor(status: number, message: string) {
    super(message);
    this.status = status;
  }
}

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  let response: Response;
  try {
    response = await fetch(`${API_BASE}${path}`, {
      ...init,
      headers: {
        "Content-Type": "application/json",
        ...(init?.headers ?? {}),
      },
    });
  } catch {
    throw new ApiError(0, "Cannot reach the Java API. Start the backend on port 8080.");
  }

  if (response.status === 204) {
    return undefined as T;
  }

  const text = await response.text();
  const body = text ? (JSON.parse(text) as T | ApiErrorBody) : undefined;
  if (!response.ok) {
    const message =
      body && typeof body === "object" && "message" in body && body.message
        ? String(body.message)
        : `Request failed (${response.status})`;
    throw new ApiError(response.status, message);
  }
  return body as T;
}

export function getGraph() {
  return request<GraphData>("/api/graph");
}

export function getShortest(sourceId: string, destinationId: string) {
  return request<RouteResult>(
    `/api/routes/shortest?sourceId=${encodeURIComponent(sourceId)}&destinationId=${encodeURIComponent(destinationId)}`,
  );
}

export function getMinHops(sourceId: string, destinationId: string) {
  return request<RouteResult>(
    `/api/routes/min-hops?sourceId=${encodeURIComponent(sourceId)}&destinationId=${encodeURIComponent(destinationId)}`,
  );
}

export function getBfs(sourceId: string) {
  return request<TraversalResult>(`/api/routes/bfs?sourceId=${encodeURIComponent(sourceId)}`);
}

export function getDfs(sourceId: string) {
  return request<TraversalResult>(`/api/routes/dfs?sourceId=${encodeURIComponent(sourceId)}`);
}

export function addLocation(payload: { name: string; type: string; x: number; y: number }) {
  return request<Location>("/api/locations", {
    method: "POST",
    body: JSON.stringify(payload),
  });
}

export function addRoad(payload: { fromLocationId: string; toLocationId: string; distanceKm: number }) {
  return request<Road>("/api/roads", {
    method: "POST",
    body: JSON.stringify(payload),
  });
}

export function setRoadStatus(roadId: string, isOpen: boolean) {
  return request<Road>(`/api/roads/${encodeURIComponent(roadId)}/status`, {
    method: "PATCH",
    body: JSON.stringify({ isOpen }),
  });
}

export function removeRoad(roadId: string) {
  return request<void>(`/api/roads/${encodeURIComponent(roadId)}`, { method: "DELETE" });
}
