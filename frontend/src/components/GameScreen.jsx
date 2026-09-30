import { useCallback, useEffect, useRef, useState } from 'react';
import { getMoves, getState, makeMove } from '../api.js';
import Board from './Board.jsx';

const POLL_MS = 1000;
const NAME = { LIGHT: 'White', DARK: 'Black' };

export default function GameScreen({ session, onLeave }) {
  const { gameId, playerId, color } = session;
  const [game, setGame] = useState(null);
  const [selected, setSelected] = useState(null);
  const [legalMoves, setLegalMoves] = useState([]);
  const [message, setMessage] = useState('');
  const [offline, setOffline] = useState(false);
  const [copied, setCopied] = useState(false);
  const selectedRef = useRef(null);

  const clearSelection = useCallback(() => {
    selectedRef.current = null;
    setSelected(null);
    setLegalMoves([]);
  }, []);

  // Poll the server so we see the opponent's moves (and them joining).
  useEffect(() => {
    let cancelled = false;
    async function poll() {
      try {
        const state = await getState(gameId);
        if (cancelled) return;
        setGame(state);
        setOffline(false);
      } catch (e) {
        if (!cancelled) {
          setOffline(true);
          if (e.message.includes('not found')) setMessage('This game no longer exists (the server may have restarted).');
        }
      }
    }
    poll();
    const id = setInterval(poll, POLL_MS);
    return () => {
      cancelled = true;
      clearInterval(id);
    };
  }, [gameId]);

  // A new move (ours or theirs) invalidates whatever was selected.
  const moveCount = game?.moveCount;
  useEffect(() => {
    clearSelection();
  }, [moveCount, clearSelection]);

  async function handleSquareClick(square) {
    if (!game || game.status !== 'IN_PROGRESS' || game.turn !== color) return;
    setMessage('');

    if (selected && legalMoves.includes(square)) {
      try {
        const next = await makeMove(gameId, playerId, selected, square);
        setGame(next);
        clearSelection();
      } catch (e) {
        setMessage(e.message);
        clearSelection();
      }
      return;
    }

    const piece = game.pieces[square];
    if (piece && piece.color === color && square !== selected) {
      selectedRef.current = square;
      setSelected(square);
      setLegalMoves([]);
      try {
        const res = await getMoves(gameId, playerId, square);
        if (selectedRef.current === square) setLegalMoves(res.moves); // ignore stale answers
      } catch (e) {
        setMessage(e.message);
      }
    } else {
      clearSelection();
    }
  }

  async function copyLink() {
    const link = `${window.location.origin}${window.location.pathname}?game=${gameId}`;
    try {
      await navigator.clipboard.writeText(link);
      setCopied(true);
      setTimeout(() => setCopied(false), 1500);
    } catch {
      window.prompt('Copy this link and send it to your friend:', link);
    }
  }

  if (!game) {
    return (
      <main className="game">
        <p className="status">{message || 'Loading game…'}</p>
        <button onClick={onLeave}>Back</button>
      </main>
    );
  }

  const finished = game.status === 'CHECKMATE' || game.status === 'STALEMATE';
  const myTurn = game.status === 'IN_PROGRESS' && game.turn === color;
  const checkedKing =
    game.check && game.status !== 'WAITING_FOR_OPPONENT'
      ? Object.entries(game.pieces).find(([, p]) => p.type === 'KING' && p.color === game.turn)?.[0]
      : null;

  let statusText;
  let statusClass = '';
  if (game.status === 'WAITING_FOR_OPPONENT') {
    statusText = 'Waiting for your opponent to join…';
  } else if (game.status === 'CHECKMATE') {
    const won = game.winner === color;
    statusText = won ? 'Checkmate — you win! 🎉' : 'Checkmate — you lose.';
    statusClass = won ? 'win' : 'lose';
  } else if (game.status === 'STALEMATE') {
    statusText = 'Stalemate — it’s a draw.';
  } else {
    statusText = myTurn ? 'Your turn' : `Waiting for ${NAME[game.turn]}…`;
    if (game.check) statusText += ' — Check!';
    statusClass = myTurn ? 'mine' : '';
  }

  return (
    <main className="game">
      <header className="game-header">
        <span>
          You are <strong>{NAME[color]}</strong>
        </span>
        <span className="code">Code: {gameId}</span>
      </header>

      {game.status === 'WAITING_FOR_OPPONENT' && (
        <div className="invite">
          <p>
            Send this code <strong>{gameId}</strong> or the link to your friend:
          </p>
          <button className="primary" onClick={copyLink}>
            {copied ? 'Copied!' : 'Copy invite link'}
          </button>
        </div>
      )}

      <p className={`status ${statusClass}`}>{statusText}</p>

      <Board
        pieces={game.pieces}
        perspective={color}
        selected={selected}
        legalMoves={legalMoves}
        lastFrom={game.lastFrom}
        lastTo={game.lastTo}
        checkedKing={checkedKing}
        onSquareClick={handleSquareClick}
      />

      {message && <p className="error">{message}</p>}
      {offline && <p className="error">Connection lost — retrying…</p>}

      <div className="actions">
        <button onClick={onLeave}>{finished ? 'New game' : 'Leave game'}</button>
      </div>
    </main>
  );
}
