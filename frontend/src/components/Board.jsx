const FILES = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'];

// \uFE0E forces text (not emoji) rendering so the pawn looks the same on phones.
const GLYPHS = {
  KING: '♚\uFE0E',
  QUEEN: '♛\uFE0E',
  ROOK: '♜\uFE0E',
  BISHOP: '♝\uFE0E',
  KNIGHT: '♞\uFE0E',
  PAWN: '♟\uFE0E',
};

export default function Board({ pieces, perspective, selected, legalMoves, lastFrom, lastTo, checkedKing, onSquareClick }) {
  // Each player sees their own pieces at the bottom.
  const ranks = perspective === 'LIGHT' ? [8, 7, 6, 5, 4, 3, 2, 1] : [1, 2, 3, 4, 5, 6, 7, 8];
  const files = perspective === 'LIGHT' ? FILES : [...FILES].reverse();

  return (
    <div className="board">
      {ranks.map((rank) =>
        files.map((file) => {
          const square = `${file}${rank}`;
          const piece = pieces[square];
          const isDarkSquare = (FILES.indexOf(file) + rank) % 2 === 1;
          const classes = ['square', isDarkSquare ? 'dark' : 'light'];
          if (square === selected) classes.push('selected');
          if (square === lastFrom || square === lastTo) classes.push('last-move');
          if (square === checkedKing) classes.push('in-check');
          const isTarget = legalMoves.includes(square);

          return (
            <button key={square} className={classes.join(' ')} onClick={() => onSquareClick(square)} aria-label={square}>
              {piece && <span className={`piece ${piece.color.toLowerCase()}`}>{GLYPHS[piece.type]}</span>}
              {isTarget && <span className={piece ? 'target capture' : 'target'} />}
              {square[0] === files[0] && <span className="coord rank">{rank}</span>}
              {rank === ranks[7] && <span className="coord file">{file.toLowerCase()}</span>}
            </button>
          );
        })
      )}
    </div>
  );
}
