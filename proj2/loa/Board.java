/* Skeleton Copyright (C) 2015, 2020 Paul N. Hilfinger and the Regents of the
 * University of California.  All rights reserved. */
package loa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Formatter;
import java.util.List;

import java.util.regex.Pattern;

import static loa.Piece.*;
import static loa.Square.*;
import static org.junit.Assert.assertNotEquals;

/** Represents the state of a game of Lines of Action.
 *  @author Farhad Alemi
 */
class Board {

    /** Default number of moves for each side that results in a draw. */
    static final int DEFAULT_MOVE_LIMIT = 60;

    /** Pattern describing a valid square designator (cr). */
    static final Pattern ROW_COL = Pattern.compile("^[a-h][1-8]$");

    /** A Board whose initial contents are taken from INITIALCONTENTS
     *  and in which the player playing TURN is to move. The resulting
     *  Board has
     *        get(col, row) == INITIALCONTENTS[row][col]
     *  Assumes that PLAYER is not null and INITIALCONTENTS is 8x8.
     *
     *  CAUTION: The natural written notation for arrays initializers puts
     *  the BOTTOM row of INITIALCONTENTS at the top.
     */
    Board(Piece[][] initialContents, Piece turn) {
        initialize(initialContents, turn);
    }

    /** A new board in the standard initial position. */
    Board() {
        this(INITIAL_PIECES, BP);
    }

    /** A Board whose initial contents and state are copied from
     *  BOARD. */
    Board(Board board) {
        this();
        copyFrom(board);
    }

    /** Set my state to CONTENTS with SIDE to move. */
    void initialize(Piece[][] contents, Piece side) {
        for (int i = 0; i < contents.length; ++i) {
            System.arraycopy(contents[i], 0, _board, i * contents[i].length,
                    contents[i].length);
        }
        _turn = side;
        _moveLimit = DEFAULT_MOVE_LIMIT;
    }

    /** Set me to the initial configuration. */
    void clear() {
        initialize(INITIAL_PIECES, BP);
    }

    /** Set my state to a copy of BOARD. */
    void copyFrom(Board board) {
        if (board == this) {
            return;
        }
        System.arraycopy(board._board, 0, _board, 0, board._board.length);
        _moves = new ArrayList<>(board._moves);
        _turn = board._turn;
        _moveLimit = board._moveLimit;
        _winnerKnown = board._winnerKnown;
        _winner = board._winner;
        _subsetsInitialized = board._subsetsInitialized;
        _whiteRegionSizes = new ArrayList<>(board._whiteRegionSizes);
        _blackRegionSizes = new ArrayList<>(board._blackRegionSizes);
    }

    /** Return the contents of the square at SQ. */
    Piece get(Square sq) {
        return _board[sq.index()];
    }

    /** Set the square at SQ to V and set the side that is to move next
     *  to NEXT, if NEXT is not null. */
    void set(Square sq, Piece v, Piece next) {
        _turn = (next != null) ? next : _turn;
        _board[sq.index()] = v;
    }

    /** Set the square at SQ to V, without modifying the side that
     *  moves next. */
    void set(Square sq, Piece v) {
        set(sq, v, null);
    }

    /** Set limit on number of moves by each side that results in a tie to
     *  LIMIT, where 2 * LIMIT > movesMade(). */
    void setMoveLimit(int limit) {
        if (2 * limit <= movesMade()) {
            throw new IllegalArgumentException("move limit too small");
        }
        _moveLimit = 2 * limit;
    }

    /** Assuming isLegal(MOVE), make MOVE. This function assumes that
     *  MOVE.isCapture() will return false.  If it saves the move for
     *  later retraction, makeMove itself uses MOVE.captureMove() to produce
     *  the capturing move. */
    void makeMove(Move move) {
        assert isLegal(move);

        Piece fromPiece = get(move.getFrom()),
                toPiece = get(move.getTo());
        if (!toPiece.fullName().equals("-")
                && toPiece.fullName().equals(fromPiece.opposite()
                .fullName())) {
            move = move.captureMove();
        }
        set(move.getTo(), fromPiece, turn().opposite());
        set(move.getFrom(), EMP);

        _moves.add(move);
        _subsetsInitialized = false;
        _winnerKnown = false;
    }

    /** Retract (unmake) one move, returning to the state immediately before
     *  that move.  Requires that movesMade () > 0. */
    void retract() {
        assert movesMade() > 0;
        Move move = _moves.remove(_moves.size() - 1);

        set(move.getFrom(), get(move.getTo()), turn().opposite());
        if (move.isCapture()) {
            set(move.getTo(), get(move.getFrom()).opposite());
        } else {
            set(move.getTo(), EMP);
        }
        _subsetsInitialized = false;
        _winnerKnown = false;
    }

    /** Return the Piece representing who is next to move. */
    Piece turn() {
        return _turn;
    }

    /** Return true iff FROM - TO is a legal move for the player currently on
     *  move. */
    boolean isLegal(Square from, Square to) {
        if (!from.isValidMove(to) || blocked(from, to) || get(from)
                != turn()) {
            return false;
        } else {
            int direction = from.direction(to);
            int numSquaresInLOA = squaresInLine(from, direction).size()
                    + squaresInLine(from, (direction + 4) % BOARD_SIZE).size();
            return from.moveDest(direction, numSquaresInLOA + 1) == to;
        }
    }

    /** Returns the list of squares in the line of action from FROM
     * in direction DIR. */
    ArrayList<Square> squaresInLine(Square from, int dir) {
        return squaresInLine(from, dir, false);
    }

    /** Returns the list of squares in the line of action from FROM
     * in direction DIR taking into account INCLUDEEMP for empty squares. */
    ArrayList<Square> squaresInLine(Square from, int dir, boolean includeEMP) {
        ArrayList<Square> sqList = new ArrayList<>();

        for (int step = 1; step < BOARD_SIZE; ++step) {
            Square sq = from.moveDest(dir, step);
            if (sq == null) {
                break;
            } else if (!get(sq).fullName().equals("-") || includeEMP) {
                sqList.add(sq);
            }
        }
        return sqList;
    }

    /** Return true iff MOVE is legal for the player currently on move.
     *  The isCapture() property is ignored. */
    boolean isLegal(Move move) {
        return isLegal(move.getFrom(), move.getTo());
    }

    /** Return a sequence of all legal moves from this position. */
    List<Move> legalMoves() {
        ArrayList<Move> moves = new ArrayList<>();

        for (int i = 0; i < _board.length; ++i) {
            if (_board[i].fullName().equals(turn().fullName())) {
                Square from = Square.sq(i % BOARD_SIZE, i / BOARD_SIZE);

                for (int dir = 0; dir < 8; ++dir) {
                    ArrayList<Square> sqList = squaresInLine(from, dir, true);
                    for (Square sq : sqList) {
                        if (isLegal(from, sq)) {
                            moves.add(Move.mv(from, sq));
                        }
                    }
                }
            }
        }

        return moves;
    }

    /** Return true iff the game is over (either player has all his
     *  pieces contiguous or there is a tie). */
    boolean gameOver() {
        return winner() != null;
    }

    /** Return true iff SIDE's pieces are contiguous. */
    boolean piecesContiguous(Piece side) {
        return getRegionSizes(side).size() == 1;
    }

    /** Return the winning side, if any. If the game is not over, result is
     *  null.  If the game has ended in a tie, returns EMP. */
    Piece winner() {
        computeRegions();
        if (!_winnerKnown) {
            if (_whiteRegionSizes.size() == 1 && _blackRegionSizes.size()
                    == 1) {
                _winner = turn().opposite();
            } else if (_whiteRegionSizes.size() == 1) {
                _winner = WP;
            } else if (_blackRegionSizes.size() == 1) {
                _winner = BP;
            } else if (movesMade() >= DEFAULT_MOVE_LIMIT) {
                _winner = EMP;
            }
            _winnerKnown = _winner != null;
        }
        return _winner;
    }

    /** Return the total number of moves that have been made (and not
     *  retracted).  Each valid call to makeMove with a normal move increases
     *  this number by 1. */
    int movesMade() {
        return _moves.size();
    }

    @Override
    public boolean equals(Object obj) {
        Board b = (Board) obj;
        return Arrays.deepEquals(_board, b._board) && _turn == b._turn;
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(_board) * 2 + _turn.hashCode();
    }

    @Override
    public String toString() {
        Formatter out = new Formatter();
        out.format("===\n");
        for (int r = BOARD_SIZE - 1; r >= 0; r -= 1) {
            out.format("    ");
            for (int c = 0; c < BOARD_SIZE; c += 1) {
                out.format("%s ", get(sq(c, r)).abbrev());
            }
            out.format("\n");
        }
        out.format("Next move: %s\n===", turn().fullName());
        return out.toString();
    }

    /** Return true if a move from FROM to TO is blocked by an opposing
     *  piece or by a friendly piece on the target square. */
    private boolean blocked(Square from, Square to) {
        if (get(to).fullName().equals(get(from).fullName())) {
            return true;
        } else {
            ArrayList<Square> sqList = squaresInLine(from, from.direction(to));
            String opposite = get(from).opposite().fullName();

            if (sqList.size() == 1 && get(sqList.get(0)) == get(from).opposite()
                    && from.distance(sqList.get(0)) == 1) {
                return true;
            }

            for (int i = 0; i < sqList.size() - 1; ++i) {
                String piece = get(sqList.get(i)).fullName();
                if (piece.equals(opposite) && from.distance(sqList.get(i))
                        < sqList.size() + 1) {
                    return true;
                }
            }
            return false;
        }
    }

    /** Return the size of the as-yet unvisited cluster of squares
     *  containing P at and adjacent to SQ.  VISITED indicates squares that
     *  have already been processed or are in different clusters.  Update
     *  VISITED to reflect squares counted. */
    private int numContig(Square sq, boolean[][] visited, Piece p) {
        if (p == EMP || get(sq) == EMP || visited[sq.row()][sq.col()]) {
            return 0;
        } else {
            int counter = 0;
            for (Square s1 : sq.adjacent()) {
                visited[sq.row()][sq.col()] = true;
                counter += numContig(s1, visited, p);
            }
            return counter + 1;
        }
    }

    /** Generates and returns the visited boolean array for piece P. */
    boolean[][] generateVisited(Piece p) {
        assertNotEquals(p, EMP);
        boolean[][] visited = new boolean[BOARD_SIZE][BOARD_SIZE];

        for (int i = 0; i < _board.length; ++i) {
            visited[i / BOARD_SIZE][i % BOARD_SIZE] = _board[i] != p;
        }
        return visited;
    }

    /** Counts and returns the number of current piece using boolean
     * map array VISITED. */
    int countPiece(boolean[][] visited) {
        int counter = 0;
        for (int row = 0; row < visited.length; ++row) {
            for (int col = 0; col < visited[row].length; ++col) {
                if (!visited[row][col]) {
                    ++counter;
                }
            }
        }
        return counter;
    }

    /** Calculates the region sizes for P using VISITED and modifies
     *  REGIONSIZE and VISITED. */
    private void calcRegions(ArrayList<Integer> regionSize,
                             boolean[][] visited, Piece p) {
        for (int row = 0; row < visited.length; ++row) {
            for (int col = 0; col < visited[row].length; ++col) {
                if (!visited[row][col]) {
                    regionSize.add(numContig(Square.sq(col, row), visited, p));
                }
            }
        }
    }

    /** Set the values of _whiteRegionSizes and _blackRegionSizes. */
    private void computeRegions() {
        if (_subsetsInitialized) {
            return;
        }
        _whiteRegionSizes.clear();
        _blackRegionSizes.clear();

        boolean[][] blackVisited = generateVisited(BP),
                whiteVisited = generateVisited(WP);

        calcRegions(_blackRegionSizes, blackVisited, BP);
        calcRegions(_whiteRegionSizes, whiteVisited, WP);

        Collections.sort(_whiteRegionSizes, Collections.reverseOrder());
        Collections.sort(_blackRegionSizes, Collections.reverseOrder());
        _subsetsInitialized = true;
    }

    /** Return the sizes of all the regions in the current union-find
     *  structure for side S. */
    List<Integer> getRegionSizes(Piece s) {
        computeRegions();
        if (s == WP) {
            return _whiteRegionSizes;
        } else if (s == BP) {
            return _blackRegionSizes;
        } else {
            throw new IllegalArgumentException("Not a valid piece.");
        }
    }

    /** Getter method which returns the moves made on board. */
    ArrayList<Move> getMoves() {
        return _moves;
    }

    /** Getter method which returns the board. */
    Piece[] getBoard() {
        return _board;
    }


    /** The standard initial configuration for Lines of Action (bottom row
     *  first). */
    static final Piece[][] INITIAL_PIECES = {
        { EMP, BP,  BP,  BP,  BP,  BP,  BP,  EMP },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { EMP, BP,  BP,  BP,  BP,  BP,  BP,  EMP }
    };

    /** Current contents of the board.  Square S is at _board[S.index()]. */
    private final Piece[] _board = new Piece[BOARD_SIZE  * BOARD_SIZE];

    /** List of all unretracted moves on this board, in order. */
    private ArrayList<Move> _moves = new ArrayList<>();

    /** Current side on move. */
    private Piece _turn;

    /** Limit on number of moves before tie is declared.  */
    private int _moveLimit;

    /** True iff the value of _winner is known to be valid. */
    private boolean _winnerKnown;

    /** Cached value of the winner (BP, WP, EMP (for tie), or null (game still
     *  in progress).  Use only if _winnerKnown. */
    private Piece _winner;

    /** True iff subsets computation is up-to-date. */
    private boolean _subsetsInitialized;

    /** List of the sizes of continguous clusters of pieces, by color. */
    private ArrayList<Integer>
        _whiteRegionSizes = new ArrayList<>(),
        _blackRegionSizes = new ArrayList<>();
}
