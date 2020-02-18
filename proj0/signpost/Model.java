package signpost;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Formatter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Arrays;

import static signpost.Place.pl;
import static signpost.Place.PlaceList;
import static signpost.Utils.*;

/** The state of a Signpost puzzle.  Each cell has coordinates (x, y),
 *  where 0 <= x < width(), 0 <= y < height().  The upper-left corner
 *  of the puzzle has coordinates (0, height() - 1), and the lower-right
 *  corner is at (width() - 1, 0).
 *
 *  A constructor initializes the squares according to a particular
 *  solution.  A solution is an assignment of sequence numbers from 1
 *  to size() == width() * height() to square positions so that
 *  squares with adjacent numbers are separated by queen moves. A
 *  queen move is a move from one square to another horizontally,
 *  vertically, or diagonally. The effect is to give each square whose
 *  number in the solution is less than size() an <i>arrow
 *  direction</i>, 1 <= d <= 8, indicating the direction of the next
 *  higher numbered square in the solution: d * 45 degrees clockwise
 *  from straight up (i.e., toward higher y coordinates).  Thus,
 *  direction 1 is "northeast", 2 is "east", ..., and 8 is "north".
 *  The highest-numbered square has direction 0.  Certain squares can
 *  have their values <i>fixed</i> to those in the solution.
 *  Initially, the only two squares with fixed values are those with
 *  the lowest and highest sequence numbers in the solution.  Before
 *  the puzzle is presented to the user, however, the program fixes
 *  other numbers so as to make the solution unique for the given
 *  arrows.
 *
 *  At any given time after initialization, a square whose value is
 *  not fixed may have an unknown value, represented as 0, or a
 *  tentative number (not necessarily that of the solution) between 1
 *  and size(). Squares may be connected together, indicating that
 *  their sequence numbers (unknown or not) are consecutive.
 *
 *  When square S0 is connected to S1, we say that S1 is the
 *  <i>successor</i> of S0, and S0 is the <i>predecessor</i> of S1.
 *  Sequences of connected squares with as-yet unknown values (denoted
 *  by 0) form a <i>group</i>, identified by a unique <i>group
 *  number</i>.  Numbered cells (whether linked or not) are in group
 *  0.  Unnumbered, unlinked cells are in group -1.  On the board displayed
 *  to the user, cells in the same group indicate their grouping and sequence
 *  with labels such as "a", "a+1", "a+2", etc., with a different letter
 *  for each different group.  The first square in a group is called the
 *  <i>head</i> of the group.  Each unnumbered square points to the head
 *  of its group (if the square is unlinked, it points to itself).
 *
 *  Squares are represented as objects of the inner class Sq
 *  (Model.Sq).  A Model object is itself iterable. That is, if M is
 *  of type Model, one can write
 *       for (Model.Sq s : M) { ... }
 *  to sequence through all its squares in unspecified order.
 *
 *  The puzzle is solved when all cells are contained in a single
 *  sequence of consecutively numbered cells (therefore all in group
 *  0) and all cells with fixed sequence numbers appear at the
 *  corresponding position in that sequence.
 *
 *  @author Farhad Alemi
 */
class Model implements Iterable<Model.Sq> {

    /** A Model whose solution is SOLUTION, initialized to its
     *  starting, unsolved state (where only cells with fixed numbers
     *  currently have sequence numbers and no unnumbered cells are
     *  connected).  SOLUTION must be a proper solution:
     *    1. It must have dimensions w x h such that w * h >= 2.
     *    2. There must be a sequence of chess-queen moves such that the
     *       sequence of values in the cells reached is 1, 2, ... w * h.
     *       The contents of SOLUTION are copied into a fresh array in
     *       this Model, so that subsequent changes to SOLUTION have no
     *       effect on the Model.  */
    Model(int[][] solution) {
        if (solution.length == 0 || solution.length * solution[0].length < 2) {
            throw badArgs("must have at least 2 squares");
        }
        _width = solution.length; _height = solution[0].length;
        int last = _width * _height;
        BitSet allNums = new BitSet();

        _allSuccessors = Place.successorCells(_width, _height);
        _solution = new int[_width][_height];
        deepCopy(solution, _solution);

        for (int num = 1; num <= width() * height(); ++num) {
            if (!linSearch(_solution, num)) {
                throw badArgs("Bad Solution Sequence!");
            }
        }

        _board = new Sq[width()][height()];
        _solnNumToPlace = new Place[width() * height() + 1];
        for (int x0 = 0; x0 < width(); ++x0) {
            for (int y0 = 0; y0 < height(); ++y0) {
                Sq square = new Sq(x0, y0, 0, false, 0, -1);
                square._predecessor = square._successor = null;
                _board[x0][y0] = square;
                _allSquares.add(square);
                _solnNumToPlace[_solution[x0][y0]] = square.pl;
                square._successors = allSuccessors(x0, y0, square.direction());
            }
        }

        for (int i = 0; i < width(); ++i) {
            for (int j = 0; j < height(); ++j) {
                Sq square = _board[i][j];
                square._dir = arrowDirection(i, j);
                for (Place place0 : square._successors) {
                    Sq successorSq = _board[place0.x][place0.y];
                    if (successorSq._predecessors == null) {
                        successorSq._predecessors = new PlaceList();
                    }
                    successorSq._predecessors.add(pl(square.x, square.y));
                }
            }
        }

        Sq firstSq = _board[_solnNumToPlace[1].x][_solnNumToPlace[1].y];
        firstSq._sequenceNum = 1;
        firstSq._hasFixedNum = true;
        firstSq._group = 0;

        Sq lastSq = _board[_solnNumToPlace[width() * height()].x]
                [_solnNumToPlace[width() * height()].y];
        lastSq._sequenceNum = width() * height();
        lastSq._hasFixedNum = true;
        lastSq._group = 0;
        _unconnected = last - 1;
    }

    /** Initializes a copy of MODEL. */
    Model(Model model) {
        _width = model.width(); _height = model.height();
        _unconnected = model._unconnected;
        _solnNumToPlace = model._solnNumToPlace;
        _solution = model._solution;
        _usedGroups.addAll(model._usedGroups);
        _allSuccessors = model._allSuccessors;

        _board = new Sq[width()][height()];
        for (int x0 = 0; x0 < width(); ++x0) {
            for (int y0 = 0; y0 < height(); ++y0) {
                Sq origSquare = model._board[x0][y0];
                Sq square = new Sq(x0, y0, origSquare.sequenceNum(),
                        origSquare.hasFixedNum(), origSquare.direction(),
                        origSquare.group());
                square._predecessor = square._successor = null;
                _board[x0][y0] = square;
                _allSquares.add(square);
                square._successors = allSuccessors(x0, y0, square.direction());
            }
        }

        for (int x0 = 0; x0 < width(); ++x0) {
            for (int y0 = 0; y0 < height(); ++y0) {
                Sq origSquare = model._board[x0][y0];
                Sq newSquare = _board[x0][y0];
                if (origSquare.predecessor() != null) {
                    newSquare._predecessor = _board[origSquare.predecessor().x]
                            [origSquare.predecessor().y];
                }
                if (origSquare.successor() != null) {
                    newSquare._successor = _board[origSquare.successor().x]
                            [origSquare.successor().y];
                }
                if (origSquare.head() != null) {
                    newSquare._head = _board[origSquare.head().x][origSquare.head().y];
                }
                for (Place place0 : newSquare._successors) {
                    Sq successorSq = _board[place0.x][place0.y];
                    if (successorSq._predecessors == null) {
                        successorSq._predecessors = new PlaceList();
                    }
                    successorSq._predecessors.add(pl(newSquare.x, newSquare.y));
                }
            }
        }
        Sq firstSq = _board[_solnNumToPlace[1].x][_solnNumToPlace[1].y];
        Sq lastSq = _board[_solnNumToPlace[width() * height()].x]
                [_solnNumToPlace[width() * height()].y];
        firstSq._sequenceNum = 1;
        lastSq._sequenceNum = width() * height();
        firstSq._hasFixedNum = true;
        lastSq._hasFixedNum = true;
    }

    /**
     * Helper method that performs linear search on a 2D
     * int array for the element ELEM.
     */
    private boolean linSearch(int[][] arr, int elem) {
        for (int i = 0; i < width(); ++i) {
            for (int j = 0; j < height(); ++j) {
                if (_solution[i][j] == elem) {
                    return true;
                }
            }
        }
        return false;
    }

    /** Returns the width (number of columns of cells) of the board. */
    final int width() {
        return _width;
    }

    /** Returns the height (number of rows of cells) of the board. */
    final int height() {
        return _height;
    }

    /** Returns the number of cells (and thus, the sequence number of the
     *  final cell). */
    final int size() {
        return _width * _height;
    }

    /** Returns true iff (X, Y) is a valid cell location. */
    final boolean isCell(int x, int y) {
        return 0 <= x && x < width() && 0 <= y && y < height();
    }

    /** Returns true iff P is a valid cell location. */
    final boolean isCell(Place p) {
        return isCell(p.x, p.y);
    }

    /** Returns all cell locations that are a queen move from (X, Y)
     *  in direction DIR, or all queen moves in any direction if DIR = 0. */
    final PlaceList allSuccessors(int x, int y, int dir) {
        return _allSuccessors[x][y][dir];
    }

    /** Returns all cell locations that are a queen move from P in direction
     *  DIR, or all queen moves in any direction if DIR = 0. */
    final PlaceList allSuccessors(Place p, int dir) {
        return _allSuccessors[p.x][p.y][dir];
    }

    /** Remove all connections and non-fixed sequence numbers. */
    void restart() {
        for (Sq sq : this) {
            sq.disconnect();
        }
        assert _unconnected == _width * _height - 1;
    }

    /** Return the number array that solves the current puzzle (the argument
     *  the constructor.  The result must not be subsequently modified.  */
    final int[][] solution() {
        return _solution;
    }

    /** Return the position of the cell with sequence number N in this board's
     *  solution. */
    Place solnNumToPlace(int n) {
        return _solnNumToPlace[n];
    }

    /** Return the Sq with sequence number N in this board's solution. */
    Sq solnNumToSq(int n) {
        return get(solnNumToPlace(n));
    }

    /** Return the current number of unconnected cells. */
    final int unconnected() {
        return _unconnected;
    }

    /** Returns true iff the puzzle is solved. */
    final boolean solved() {
        return _unconnected == 0;
    }

    /** Return the cell at (X, Y). */
    final Sq get(int x, int y) {
        return _board[x][y];
    }

    /** Return the cell at P. */
    final Sq get(Place p) {
        return p == null ? null : _board[p.x][p.y];
    }

    /** Return the cell at the same position as SQ (generally from another
     *  board), or null if SQ is null. */
    final Sq get(Sq sq) {
        return sq == null ? null : _board[sq.x][sq.y];
    }

    /** Connect all numbered cells with successive numbers that as yet are
     *  unconnected and are separated by a queen move.  Returns true iff
     *  any changes were made. */
    boolean autoconnect() {
        Sq[] squareArr = new Sq[width() * height() + 1];
        int arrIndex = 0;
        for (int num = 1; num <= width() * height(); ++num) {
            Sq square = _board[solnNumToPlace(num).x][solnNumToPlace(num).y];
            if (square.sequenceNum() != 0) {
                squareArr[arrIndex] = square;
                ++arrIndex;
            }
        }
        boolean atLeastOne = false;
        for (int i = 0; i < arrIndex - 1; ++i) {
            if (squareArr[i].sequenceNum() + 1 == squareArr[i + 1].sequenceNum()) {
                atLeastOne = atLeastOne || squareArr[i].connect(squareArr[i + 1]);
            }
        }
        return atLeastOne;
    }

    /** Sets the numbers in this board's squares to the solution from which
     *  this board was last initialized by the constructor. */
    void solve() {
        for (int num = 1; num <= width() * height(); ++num) {
            _board[solnNumToPlace(num).x][solnNumToPlace(num).y]._sequenceNum = num;
        }
        autoconnect();
        _unconnected = 0;
    }

    /** Return the direction from cell (X, Y) in the solution to its
     *  successor, or 0 if it has none. */
    public int arrowDirection(int x, int y) {
        int seq0 = _solution[x][y];

        if (seq0 != width() * height()) {
            Sq s1 = solnNumToSq(seq0 + 1);
            return Place.dirOf(x, y, s1.x, s1.y);
        }
        return 0;
    }

    /** Return a new, currently unused group number > 0.  Selects the
     *  lowest not currently in use. */
    private int newGroup() {
        for (int i = 1; true; i += 1) {
            if (_usedGroups.add(i)) {
                return i;
            }
        }
    }

    /** Indicate that group number GROUP is no longer in use. */
    private void releaseGroup(int group) {
        _usedGroups.remove(group);
    }

    /** Combine the groups G1 and G2, returning the resulting group. Assumes
     *  G1 != 0 != G2 and G1 != G2. */
    private int joinGroups(int g1, int g2) {
        assert (g1 != 0 && g2 != 0);
        if (g1 == -1 && g2 == -1) {
            return newGroup();
        } else if (g1 == -1) {
            return g2;
        } else if (g2 == -1) {
            return g1;
        } else if (g1 < g2) {
            releaseGroup(g2);
            return g1;
        } else {
            releaseGroup(g1);
            return g2;
        }
    }

    @Override
    public Iterator<Sq> iterator() {
        return _allSquares.iterator();
    }

    @Override
    public String toString() {
        String hline;
        hline = "+";
        for (int x = 0; x < _width; x += 1) {
            hline += "------+";
        }

        Formatter out = new Formatter();
        for (int y = _height - 1; y >= 0; y -= 1) {
            out.format("%s%n", hline);
            out.format("|");
            for (int x = 0; x < _width; x += 1) {
                Sq sq = get(x, y);
                if (sq.hasFixedNum()) {
                    out.format("+%-5s|", sq.seqText());
                } else {
                    out.format("%-6s|", sq.seqText());
                }
            }
            out.format("%n|");
            for (int x = 0; x < _width; x += 1) {
                Sq sq = get(x, y);
                if (sq.predecessor() == null && sq.sequenceNum() != 1) {
                    out.format(".");
                } else {
                    out.format(" ");
                }
                if (sq.successor() == null
                    && sq.sequenceNum() != size()) {
                    out.format("o ");
                } else {
                    out.format("  ");
                }
                out.format("%s |", ARROWS[sq.direction()]);
            }
            out.format("%n");
        }
        out.format(hline);
        return out.toString();
    }

    @Override
    public boolean equals(Object obj) {
        Model model = (Model) obj;
        return (_unconnected == model._unconnected
                && _width == model._width && _height == model._height
                && Arrays.deepEquals(_solution, model._solution)
                && Arrays.deepEquals(_board, model._board));
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(_solution) * Arrays.deepHashCode(_board);
    }

    /**
     * Returns the _board variable
     */
    Sq[][] getBoard() {
        return _board;
    }

    /** Represents a square on the board. */
    final class Sq {
        /** A square at (X0, Y0) with arrow in direction DIR (0 if not
         *  set), group number GROUP, sequence number SEQUENCENUM (0
         *  if none initially assigned), which is fixed iff FIXED. */
        Sq(int x0, int y0, int sequenceNum, boolean fixed, int dir, int group) {
            x = x0; y = y0;
            pl = pl(x, y);
            _hasFixedNum = fixed;
            _sequenceNum = sequenceNum;
            _dir = dir;
            _head = this;
            _group = group;
        }

        /** A copy of OTHER, excluding head, successor, and predecessor. */
        Sq(Sq other) {
            this(other.x, other.y, other._sequenceNum, other._hasFixedNum,
                 other._dir, other._group);
            _successor = _predecessor = null;
            _head = this;
            _successors = other._successors;
            _predecessors = other._predecessors;
        }

        /** Return this square's current sequence number, or 0 if
         *  none assigned. */
        int sequenceNum() {
            return _sequenceNum;
        }

        /** Fix this square's current sequence number at N>0.  It is
         *  an error if this square's number is not initially 0 or N. */
        void setFixedNum(int n) {
            if (n == 0 || (_sequenceNum != 0 && _sequenceNum != n)) {
                throw badArgs("sequence number may not be fixed");
            }
            _hasFixedNum = true;
            if (_sequenceNum == n) {
                return;
            } else {
                releaseGroup(_head._group);
            }
            _sequenceNum = n;
            for (Sq sq = this; sq._successor != null; sq = sq._successor) {
                sq._successor._sequenceNum = sq._sequenceNum + 1;
            }
            for (Sq sq = this; sq._predecessor != null; sq = sq._predecessor) {
                sq._predecessor._sequenceNum = sq._sequenceNum - 1;
            }
        }

        /** Unfix this square's sequence number if it is currently fixed;
         *  otherwise do nothing. */
        void unfixNum() {
            Sq next = _successor, pred = _predecessor;
            _hasFixedNum = false;
            disconnect();
            if (pred != null) {
                pred.disconnect();
            }
            _sequenceNum = 0;
            if (next != null) {
                connect(next);
            }
            if (pred != null) {
                pred.connect(this);
            }
        }

        /** Return true iff this square's sequence number is fixed. */
        boolean hasFixedNum() {
            return _hasFixedNum;
        }

        /** Returns direction of this square's arrow (0 if no arrow). */
        int direction() {
            return _dir;
        }

        /** Return this square's current predecessor. */
        Sq predecessor() {
            return _predecessor;
        }

        /** Return this square's current successor. */
        Sq successor() {
            return _successor;
        }

        /** Return the head of the connected sequence this square
         * is currently in. */
        Sq head() {
            return _head;
        }

        /** Return the group number of this square's group.  It is
         *  0 if this square is numbered, and-1 if it is alone in its group. */
        int group() {
            if (_sequenceNum != 0) {
                return 0;
            } else {
                return _head._group;
            }
        }

        /** Size of alphabet. */
        static final int ALPHA_SIZE = 26;

        /** Return a textual representation of this square's sequence number or
         *  group/position. */
        String seqText() {
            if (_sequenceNum != 0) {
                return String.format("%d", _sequenceNum);
            }
            int g = group() - 1;
            if (g < 0) {
                return "";
            }

            String groupName =
                String.format("%s%s",
                              g < ALPHA_SIZE ? ""
                              : Character.toString((char) (g / ALPHA_SIZE
                                                           + 'a')),
                              Character.toString((char) (g % ALPHA_SIZE
                                                         + 'a')));
            if (this == _head) {
                return groupName;
            }
            int n;
            n = 0;
            for (Sq p = this; p != _head; p = p._predecessor) {
                n += 1;
            }
            return String.format("%s%+d", groupName, n);
        }

        /** Return locations of this square's potential successors. */
        PlaceList successors() {
            return _successors;
        }

        /** Return locations of this square's potential predecessors. */
        PlaceList predecessors() {
            return _predecessors;
        }

        /** Returns true iff this square may be connected to square S1, that is:
         *  + S1 is in the correct direction from this square.
         *  + S1 does not have a current predecessor, this square does not
         *    have a current successor, S1 is not the first cell in sequence,
         *    and this square is not the last.
         *  + If S1 and this square both have sequence numbers, then
         *    this square's is sequenceNum() == S1.sequenceNum() - 1.
         *  + If neither S1 nor this square have sequence numbers, then
         *    they are not part of the same connected sequence.
         */
        boolean connectable(Sq s1) {
            int tempDir = Place.dirOf(this.x, this.y, s1.x, s1.y);
            boolean incorrectDir = (tempDir == 0 || tempDir != _dir);
            boolean firstOrLast = this.sequenceNum() == width() * height() || s1.sequenceNum() == 1;
            boolean hasPredecessorOrSuccessor = this._successor != null || s1._predecessor != null;
            boolean numberedAndNotConsec = (this.sequenceNum() != 0 && s1.sequenceNum() != 0)
                    && !(sequenceNum() == s1.sequenceNum() - 1);
            boolean unnumberedAndSameGroup = this.sequenceNum() == 0 && s1.sequenceNum() == 0
                    && group() == s1.group() && group() != -1;

            if (incorrectDir || firstOrLast || hasPredecessorOrSuccessor || numberedAndNotConsec
                    || unnumberedAndSameGroup) {
                return false;
            }
            return true;
        }

        /** Connect this square to S1, if both are connectable; otherwise do
         *  nothing. Returns true iff this square and S1 were connectable.
         *  Assumes S1 is in the proper arrow direction from this square. */
        boolean connect(Sq s1) {
            if (!connectable(s1)) {
                return false;
            }
            int sgroup = s1.group();

            _unconnected -= 1;
            s1._predecessor = this;
            this._successor = s1;

            Sq temp;
            int thisNum = sequenceNum(),
                s1Num = s1.sequenceNum();

            if (thisNum != 0 && s1Num == 0) {
                int seqNum = thisNum;
                temp = this.successor();
                ++seqNum;
                while (temp != null) {
                    temp._sequenceNum = seqNum;
                    temp = temp.successor();
                    ++seqNum;
                }
            } else if (s1Num != 0 && thisNum == 0) {
                int seqNum = s1Num;
                temp = s1.predecessor();
                --seqNum;
                while (temp != null) {
                    temp._sequenceNum = seqNum;
                    --seqNum;
                    temp = temp.predecessor();
                }
            }

            temp = successor();
            while (temp != null) {
                temp._head = _head;
                temp = temp.successor();
            }

            if (thisNum == 0) {
                releaseGroup(group());
            }
            if (s1Num == 0) {
                releaseGroup(sgroup);
            }
            if (sequenceNum() == 0 && s1.sequenceNum() == 0) {
                _head._group = joinGroups(group(), sgroup);
            }
            return true;
        }

        /** Disconnect this square from its current successor, if any. */
        void disconnect() {
            Sq next = _successor;
            if (next == null) {
                return;
            }
            Sq iter1, iter2, iter3, iter4, iter5;
            _unconnected += 1;
            next._predecessor = _successor = null;
            if (_sequenceNum == 0) {
                if (predecessor() == null && next.successor() == null) {
                    releaseGroup(group());
                    _group = next._group = -1;
                } else if (predecessor() != null && next.successor() != null) {
                    next._group = newGroup();
                } else if (predecessor() != null) {
                    next._group = -1;
                } else {
                    _group = -1;
                }
            } else {
                iter1 = this;
                boolean preFixedNum = false;
                boolean postFixedNum = false;

                while (iter1 != null) {
                    if (iter1.hasFixedNum()) {
                        preFixedNum = true;
                        break;
                    }
                    iter1 = iter1.predecessor();
                }
                if (!preFixedNum) {
                    iter2 = this;
                    while (iter2 != null) {
                        iter2._sequenceNum = 0;
                        iter2 = iter2.predecessor();
                    }
                    if (predecessor() != null) {
                        _head._group = newGroup();
                    }
                } else {
                    _group = -1;
                }

                iter3 = next;
                while (iter3 != null) {
                    if (iter3.hasFixedNum()) {
                        postFixedNum = true;
                        break;
                    }
                    iter3 = iter3.successor();
                }
                if (!postFixedNum) {
                    iter4 = next;
                    while (iter4 != null) {
                        iter4._sequenceNum = 0;
                        iter4 = iter4.successor();
                    }
                    if (next.successor() != null) {
                        next._group = newGroup();
                    }
                } else {
                    next._group = -1;
                }
            }
            iter5 = next;
            while (iter5 != null) {
                iter5._head = next;
                iter5 = iter5.successor();
            }
        }

        @Override
        public boolean equals(Object obj) {
            Sq sq = (Sq) obj;
            return sq != null
                && pl == sq.pl
                && _hasFixedNum == sq._hasFixedNum
                && _sequenceNum == sq._sequenceNum
                && _dir == sq._dir
                && (_predecessor == null) == (sq._predecessor == null)
                && (_predecessor == null
                    || _predecessor.pl == sq._predecessor.pl)
                && (_successor == null || _successor.pl == sq._successor.pl);
        }

        @Override
        public int hashCode() {
            return (x + 1) * (y + 1) * (_dir + 1)
                * (_hasFixedNum ? 3 : 1) * (_sequenceNum + 1);
        }

        @Override
        public String toString() {
            return String.format("<Sq@%s, dir: %d>", pl, direction());
        }

        /** The coordinates of this square in the board. */
        protected final int x, y;
        /** The coordinates of this square as a Place. */
        protected final Place pl;
        /** The first in the currently connected sequence of cells ("group")
         *  that includes this one. */
        private Sq _head;
        /** The group number of the group of which this is a member, if
         *  head == this.  Numbered sequences have a group number of 0,
         *  regardless of the value of _group. Unnumbered one-member groups
         *  have a group number of -1.  If _head != this and the square is
         *  unnumbered, then _group is undefined and the square's group
         *  number is maintained in _head._group. */
        private int _group;
        /** True iff assigned a fixed sequence number. */
        private boolean _hasFixedNum;
        /** The current imputed or fixed sequence number,
         *  numbering from 1, or 0 if there currently is none. */
        private int _sequenceNum;
        /** The arrow direction. The possible values are 0 (for unset),
         *  1 for northeast, 2 for east, 3 for southeast, 4 for south,
         *  5 for southwest, 6 for west, 7 for northwest, and 8 for north. */
        private int _dir;
        /** The current predecessor of this square, or null if there is
         *  currently no predecessor. */
        private Sq _predecessor;
        /** The current successor of this square, or null if there is
         *  currently no successor. */
        private Sq _successor;
        /** Locations of the possible predecessors of this square. */
        private PlaceList _predecessors;
        /** Locations of the possible successors of this square. */
        private PlaceList _successors;
    }

    /** ASCII denotations of arrows, indexed by direction. */
    private static final String[] ARROWS = {
        " *", "NE", "E ", "SE", "S ", "SW", "W ", "NW", "N "
    };

    /** Number of squares that haven't been connected. */
    private int _unconnected;
    /** Dimensions of board. */
    private int _width, _height;
    /** Contents of board, indexed by position. */
    private Sq[][] _board;
    /** Contents of board as a sequence of squares for convenient iteration. */
    private ArrayList<Sq> _allSquares = new ArrayList<>();
    /** _allSuccessors[x][y][dir] is a sequence of all queen moves possible
     *  on the board of in direction dir from (x, y).  If dir == 0,
     *  this is all places that are a queen move from (x, y) in any
     *  direction. */
    private PlaceList[][][] _allSuccessors;
    /** The solution from which this Model was built. */
    private int[][] _solution;
    /** Inverse mapping from sequence numbers to board positions. */
    private Place[] _solnNumToPlace;
    /** The set of positive group numbers currently in use. */
    private HashSet<Integer> _usedGroups = new HashSet<>();
}
