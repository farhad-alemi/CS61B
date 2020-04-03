/* Skeleton Copyright (C) 2015, 2020 Paul N. Hilfinger and the Regents of the
 * University of California.  All rights reserved. */
package loa;

import java.util.List;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static loa.Piece.*;

/** An automated Player.
 *  @author Farhad Alemi
 */
class MachinePlayer extends Player {

    /** A position-score magnitude indicating a win (for white if positive,
     *  black if negative). */
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 20;
    /** A magnitude greater than a normal value. */
    private static final int INFTY = Integer.MAX_VALUE;

    /** A new MachinePlayer with no piece or controller (intended to produce
     *  a template). */
    MachinePlayer() {
        this(null, null);
    }

    /** A MachinePlayer that plays the SIDE pieces in GAME. */
    MachinePlayer(Piece side, Game game) {
        super(side, game);
    }

    @Override
    String getMove() {
        Move choice;
        assert side() == getGame().getBoard().turn();

        _depth = 3;
        choice = searchForMove();
        getGame().reportMove(choice);
        return choice.toString();
    }

    @Override
    Player create(Piece piece, Game game) {
        return new MachinePlayer(piece, game);
    }

    @Override
    boolean isManual() {
        return false;
    }

    /** Return a move after searching the game tree to DEPTH>0 moves
     *  from the current position. Assumes the game is not over. */
    private Move searchForMove() {
        Board work = new Board(getBoard());
        int value;
        assert side() == work.turn();
        _foundMove = null;
        if (side() == WP) {
            value = findMove(work, chooseDepth(), true, 1, -INFTY, INFTY);
        } else {
            value = findMove(work, chooseDepth(), true, -1, -INFTY, INFTY);
        }
        return _foundMove;
    }

    /** The function performs heuristics on the board BOARD and returns a
     * heuristic score. */
    private int doHeuristics(Board board) {
        List<Integer> whiteRegions = board.getRegionSizes(WP);
        List<Integer> blackRegions = board.getRegionSizes(BP);
        int numWhite, numBlack, score = 0;

        numWhite = board.countPiece(board.generateVisited(WP));
        numBlack = board.countPiece(board.generateVisited(BP));

        if (numWhite == 1 || whiteRegions.size() == 1) {
            score += 100;
        } else if (numWhite <= 3) {
            score += 10;
        }
        if (numBlack == 1 || blackRegions.size() == 1) {
            score -= 100;
        } else if (numBlack <= 3) {
            score -= 10;
        }
        score += (blackRegions.size() - whiteRegions.size());
        return score;
    }

    /** Find a move from position BOARD and return its value, recording
     *  the move found in _foundMove iff SAVEMOVE. The move
     *  should have maximal value or have value > BETA if SENSE==1,
     *  and minimal value or value < ALPHA if SENSE==-1. Searches up to
     *  DEPTH levels.  Searching at level 0 simply returns a static estimate
     *  of the board value and does not set _foundMove. If the game is over
     *  on BOARD, does not set _foundMove. */
    private int findMove(Board board, int depth, boolean saveMove, int sense,
                         Integer alpha, Integer beta) {
        if (depth == 0 || board.gameOver()) {
            return doHeuristics(board);
        }
        List<Move> legalMoves = board.legalMoves();
        int bestScore = (sense == 1) ? -INFTY : INFTY;

        for (Move move : legalMoves) {
            board.makeMove(move);
            int score = findMove(board, depth - 1,  saveMove, -1 * sense,
                    alpha, beta);
            if (sense == 1) {
                if (score > bestScore && saveMove) {
                    _foundMove = move;
                }
                bestScore = max(bestScore, score);
                alpha = max(alpha, score);
            } else if (sense == -1) {
                if (score < bestScore && saveMove) {
                    _foundMove = move;
                }
                bestScore = min(bestScore, score);
                beta = min(beta, score);
            } else {
                throw new IllegalArgumentException("Incorrect value for SENSE");
            }
            board.retract();
            if (beta <= alpha) {
                break;
            }
        }
        return bestScore;
    }

    /** Return a search depth for the current position. */
    private int chooseDepth() {
        return _depth;
    }

    /** Current search depth for AI. */
    private int _depth;

    /** Used to convey moves discovered by findMove. */
    private Move _foundMove;
}
