// Remembers "who am I in this game" so a page refresh doesn't lose your seat.
const key = (gameId) => `chess:${gameId.toUpperCase()}`;

export function loadSession(gameId) {
  try {
    return JSON.parse(localStorage.getItem(key(gameId)));
  } catch {
    return null;
  }
}

export function saveSession(session) {
  try {
    localStorage.setItem(key(session.gameId), JSON.stringify(session));
  } catch {
    /* storage unavailable - game still works until refresh */
  }
}

export function setUrlGame(gameId) {
  const url = new URL(window.location.href);
  if (gameId) url.searchParams.set('game', gameId);
  else url.searchParams.delete('game');
  window.history.replaceState({}, '', url);
}

export function getUrlGame() {
  return new URL(window.location.href).searchParams.get('game');
}
