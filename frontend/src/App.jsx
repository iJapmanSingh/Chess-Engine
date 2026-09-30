import { useEffect, useState } from 'react';
import { createGame, joinGame } from './api.js';
import { getUrlGame, loadSession, saveSession, setUrlGame } from './session.js';
import Home from './components/Home.jsx';
import GameScreen from './components/GameScreen.jsx';

export default function App() {
  const [session, setSession] = useState(null); // { gameId, playerId, color }
  const [error, setError] = useState('');
  const [busy, setBusy] = useState(false);

  // Opened via a shared link (?game=ABC123): resume our seat, or take the open one.
  useEffect(() => {
    const gameId = getUrlGame();
    if (!gameId) return;
    const existing = loadSession(gameId);
    if (existing) {
      setSession(existing);
      return;
    }
    enter(() => joinGame(gameId));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  async function enter(action) {
    setBusy(true);
    setError('');
    try {
      const joined = await action();
      saveSession(joined);
      setUrlGame(joined.gameId);
      setSession(joined);
    } catch (e) {
      setError(e.message);
      setUrlGame(null);
    } finally {
      setBusy(false);
    }
  }

  function leave() {
    setUrlGame(null);
    setSession(null);
  }

  if (session) return <GameScreen session={session} onLeave={leave} />;

  return (
    <Home
      busy={busy}
      error={error}
      onCreate={() => enter(createGame)}
      onJoin={(code) => {
        const existing = loadSession(code);
        if (existing) {
          setUrlGame(existing.gameId);
          setSession(existing);
        } else {
          enter(() => joinGame(code.trim()));
        }
      }}
    />
  );
}
