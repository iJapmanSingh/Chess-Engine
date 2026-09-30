// All calls to the Spring Boot backend live here.
const BASE = import.meta.env.VITE_API_URL || '';

async function request(path, options = {}) {
  let res;
  try {
    res = await fetch(BASE + path, { headers: { 'Content-Type': 'application/json' }, ...options });
  } catch {
    throw new Error('Cannot reach the server.');
  }
  const data = await res.json().catch(() => null);
  if (!res.ok) throw new Error(data?.message || `Request failed (${res.status})`);
  return data;
}

export const createGame = () => request('/api/games', { method: 'POST' });
export const joinGame = (gameId) => request(`/api/games/${encodeURIComponent(gameId)}/join`, { method: 'POST' });
export const getState = (gameId) => request(`/api/games/${gameId}`);
export const getMoves = (gameId, playerId, from) =>
  request(`/api/games/${gameId}/moves?from=${from}&playerId=${playerId}`);
export const makeMove = (gameId, playerId, from, to) =>
  request(`/api/games/${gameId}/move`, { method: 'POST', body: JSON.stringify({ playerId, from, to }) });
