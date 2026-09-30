import { useState } from 'react';

export default function Home({ busy, error, onCreate, onJoin }) {
  const [code, setCode] = useState('');

  return (
    <main className="home">
      <h1>♞ Chess</h1>
      <p className="subtitle">Play a friend from any device. No sign-up.</p>

      <button className="primary" disabled={busy} onClick={onCreate}>
        Start a new game
      </button>

      <div className="divider">or join a friend's game</div>

      <form
        className="join-row"
        onSubmit={(e) => {
          e.preventDefault();
          if (code.trim()) onJoin(code);
        }}
      >
        <input
          value={code}
          onChange={(e) => setCode(e.target.value.toUpperCase())}
          placeholder="Game code"
          maxLength={6}
          autoCapitalize="characters"
          spellCheck={false}
        />
        <button type="submit" disabled={busy || !code.trim()}>
          Join
        </button>
      </form>

      {error && <p className="error">{error}</p>}
    </main>
  );
}
