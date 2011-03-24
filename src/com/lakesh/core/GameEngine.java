/*
 * Main.java

 * 
 * Created on Mar 31, 2008, 7:25:13 PM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lakesh.core;

import com.lakesh.constants.*;

import java.util.Iterator;
import java.util.ArrayList;
import android.util.Log;

/**
 *
 * @author lakesh
 * TODO:- Clean the code and remove the redundancy of functions
 */
public class GameEngine {

    /**
     * @param args the command line arguments     */    
    
    
    final int INFINITY = 600;
    int depth = 0;
    //static int turn;
    public int board[][];    
    private int noOfMoves = 0;
    
    
    /*
     * This array is used during the evaluation function 
     * Each cell is given a weightage according to its position
     */
    private int score[][] = {
            {0,  0,  0,  0,  0,  0,  0,  0,  0,  0},
            {0, 65, -3,  6,  4,  4,  6, -3, 65,  0},
            {0, -3, -29, 3,  1,  1,  3, -29,-3,  0},
            {0,  6,  3,  5,  3,  3,  5,  3,  6,  0},
            {0,  4,  1,  3,  1,  1,  3,  1,  4,  0},
            {0,  4,  1,  3,  1,  1,  3,  1,  4,  0},
            {0,  6,  3,  5,  3,  3,  5,  3,  6,  0},
            {0, -3, -29, 3,  1,  1,  3, -29,-3,  0},
            {0, 65, -3,  6,  4,  4,  6, -3, 65,  0},
            {0,  0,  0,  0,  0,  0,  0,  0,  0,  0}
    };
    

    public GameEngine() {
        board = new int[10][10];        
        resetBoard();        
    }
    
    
    public void increaseNoOfMoves() {
        noOfMoves++;
    }
    
    
    public void resetBoard() {        
        int i, j;
        for (i = 0; i < 10; i++) {
            board[0][i] = -1;
            board[9][i] = -1;
        }

        for (i = 1; i < 9; i++) {
            board[i][0] = -1;
            board[i][9] = -1;
        }

        for (i = 1; i < 9; i++) {
            for (j = 1; j < 9; j++) {
                board[i][j] = 0;
            }
        }
        board[4][4] = Coin.BLACK;
        board[5][5] = Coin.BLACK;
        board[4][5] = Coin.WHITE;
        board[5][4] = Coin.WHITE;        
        noOfMoves = 0;
    }

   
    
    public int getScore(int x,int y) {
        return score[x][y];
    }

    public boolean checkDraw() {
        if (validMoves(board, Coin.WHITE).isEmpty() && validMoves(board, Coin.BLACK).isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkDepth() {
        if (depth == Engine.DEPTH) {
            return true;
        } else {
            return false;
        }
    }

    public int countWhite() {
        int count = 0;
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                if (board[i][j] == Coin.WHITE) {
                    count++;
                }
            }

        }
        return count;
    }

    public int countBlack() {
        int count = 0;
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                if (board[i][j] == Coin.BLACK) {
                    count++;
                }
            }

        }
        return count;
    }

    private int opposite(int player) {
        if (player == Coin.BLACK) {
            return Coin.WHITE;
        }
        if (player == Coin.WHITE) {
            return Coin.BLACK;
        }
        return 0;
    }

    public Cell computerMove() {        
        ArrayList validMoves = validMoves(board, Coin.BLACK);
        if (validMoves.isEmpty() == true) {
            return null;
        }
        Iterator iter = validMoves.iterator();
        int best = -INFINITY;
        int value;
        Cell bestmove = new Cell(-1, -1);
        while (iter.hasNext()) {
            Cell move = (Cell) iter.next();
            int tempboard[][] = new int[10][10];
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    tempboard[i][j] = board[i][j];
                }
            }
            tempboard[move.getx()][move.gety()] = Coin.BLACK;
            flipcells(tempboard,move.getx(),move.gety(),Coin.BLACK);
            value = -negamax(tempboard, opposite(Coin.BLACK), -INFINITY, +INFINITY);
            if (value > best) {
                bestmove = move;
                best = value;
            }
        }
        depth = 0;        
        return new Cell(bestmove.getx(), bestmove.gety());
    }

    public int getWinner() {
        int countWhite = 0;
        int countBlack = 0;
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (board[i][j] == Coin.WHITE) {
                    countWhite++;
                } else if (board[i][j] == Coin.BLACK) {
                    countBlack++;
                }
            }
        }
        if (countWhite > countBlack) {
            return Coin.WHITE;
        } else if (countBlack > countWhite) {
            return Coin.BLACK;
        } else {
            return 0;
        }
    }
    
    public int getDifference(int player) {
        int countPlayer = 0;
        int countOppositePlayer = 0;
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (board[i][j] == player) {
                    countPlayer++;
                } else if (board[i][j] == opposite(player)) {
                    countOppositePlayer++;
                }
            }
        }
        return countPlayer-countOppositePlayer;
    }

    private int negamax(int negamaxboard[][], int player, int alpha, int beta) {
        /**
         * If more than 58 moves have been made then only 64-58 that is 6 moves are left
         * Therefore we can trace to the end of the game and return the evaluated value
         * However there are chances that both players may have no moves left which may be after the end of the game
         * or before that so we have to evaluate the position and then return the value.
         * Since it is almost the end of the game difference in the number of coins will matter
         * 
         * 
         * 
         * In case the moves are less than 58 then we cannot trace to the end of the game. In this situation 
         * we have to specify the depth up to which we can carry on the search. Also in case both the players have no valid moves 
         * left then we can return the evaluated value
         */
        if(noOfMoves >= 58) {
            
            if ((validMoves(negamaxboard,player) == null) && (validMoves(negamaxboard,opposite(player)) == null)) {
                return getDifference(player)*200;
            }             
        } else {
            if (checkDepth() == true) {
                return eval(negamaxboard, player);
            }  
            if ((validMoves(negamaxboard,player) == null) && (validMoves(negamaxboard,opposite(player)) == null)) {
                //return getDifference(player)*200;
                return eval(negamaxboard,player);
            }   
        }
        int best = -INFINITY;
        int value;
        ArrayList validMoves = validMoves(negamaxboard, player);
        java.util.Collections.shuffle(validMoves);      
        
        /**
         * If there is no valid move left for a player then simply evaluate the position and return;
         */
        if(validMoves.isEmpty()) {
            /*int tempboard[][] = new int[10][10];
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    tempboard[i][j] = negamaxboard[i][j];
                }

            }
            value = -negamax(negamaxboard, opposite(player), -beta, -alpha);
            if (value > best) {
                 best = value;
            }
            if (best >= beta) {
                return best;
            }
            if (best > alpha) {
                alpha = best;
            }*/
            return eval(negamaxboard,player);
        } else {
            Iterator iter = validMoves.iterator();

            while (iter.hasNext()) {
                Cell move = (Cell) iter.next();

                int tempboard[][] = new int[10][10];
                for (int i = 0; i < 10; i++) {
                    for (int j = 0; j < 10; j++) {
                        tempboard[i][j] = negamaxboard[i][j];
                    }

                }
                tempboard[move.getx()][move.gety()] = player;
                flipcells(tempboard,move.getx(),move.gety(),player);
                depth++;
                value = -negamax(tempboard, opposite(player), -beta, -alpha);
                depth--;
                if (value > best) {
                    best = value;
                }
                if (best >= beta) {
                    break;
                }
                if (best > alpha) {
                    alpha = best;
                }

            }
            return best;
        }
    }

    private int eval(int tempboard[][], int player) {
        int count = 0;
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                if (tempboard[i][j] == player) {                    
                    count += getScore(i,j);                
                } 
                else if(tempboard[i][j] == opposite(player)){
                    count -= getScore(i,j);
                }
                    
            }
        }
        count += validMoves(tempboard, player).size() - validMoves(tempboard, opposite(player)).size();
        count += getDifference(player);        
        return count;

    }

    public void display() {
        
    }

    public void flipcells(int tempboard[][],int x, int y, int player) {       
        int i, j;
        i = x;
        j = y;

        if (tempboard[i][j + 1] != player) {
            j++;
            while (tempboard[i][j] != -1 && tempboard[i][j] != player) {
                if (tempboard[i][j] == 0) {
                    break;
                }
                j++;
            }
            if (tempboard[i][j] == player) {
                while (j != y) {
                    j--;
                    tempboard[i][j] = player;
                }
            }
        }

        i = x;
        j = y;
        if (tempboard[i][j - 1] != player) {
            j--;
            while (tempboard[i][j] != -1 && tempboard[i][j] != player) {
                if (tempboard[i][j] == 0) {
                    break;
                }
                j--;
            }

            if (tempboard[i][j] == player) {
                while (j != y) {
                    j++;
                    tempboard[i][j] = player;
                }
            }


        }
        i = x;
        j = y;
        if (tempboard[i + 1][j - 1] != player) {
            i++;
            j--;
            while (tempboard[i][j] != -1 && tempboard[i][j] != player) {
                if (tempboard[i][j] == 0) {
                    break;
                }
                i++;
                j--;
            }
            if (tempboard[i][j] == player) {
                while (i != x && j != y) {
                    j++;
                    i--;
                    tempboard[i][j] = player;
                }
            }


        }
        i = x;
        j = y;
        if (tempboard[i - 1][j - 1] != player) {
            i--;
            j--;
            while (tempboard[i][j] != -1 && tempboard[i][j] != player) {
                if (tempboard[i][j] == 0) {
                    break;
                }
                i--;
                j--;
            }
            if (tempboard[i][j] == player) {
                while (i != x && j != y) {
                    j++;
                    i++;
                    tempboard[i][j] = player;
                }
            }


        }
        i = x;
        j = y;
        if (tempboard[i + 1][j + 1] != player) {
            i++;
            j++;
            while (tempboard[i][j] != -1 && tempboard[i][j] != player) {
                if (tempboard[i][j] == 0) {
                    break;
                }
                i++;
                j++;
            }
            if (tempboard[i][j] == player) {
                while (i != x && j != y) {
                    j--;
                    i--;
                    tempboard[i][j] = player;
                }
            }

        }
        i = x;
        j = y;
        if (tempboard[i - 1][j + 1] != player) {
            i--;
            j++;
            while (tempboard[i][j] != -1 && tempboard[i][j] != player) {
                if (tempboard[i][j] == 0) {
                    break;
                }
                i--;
                j++;
            }
            if (tempboard[i][j] == player) {
                while (i != x && j != y) {
                    j--;
                    i++;
                    tempboard[i][j] = player;
                }
            }
        }
        i = x;
        j = y;
        if (tempboard[i - 1][j] != player) {
            i--;
            while (tempboard[i][j] != -1 && tempboard[i][j] != player) {
                if (tempboard[i][j] == 0) {
                    break;
                }
                i--;
            }
            if (tempboard[i][j] == player) {
                while (i != x) {
                    i++;
                    tempboard[i][j] = player;
                }
            }
        }

        i = x;
        j = y;
        if (tempboard[i + 1][j] != player) {
            i++;
            if (x == 3 && y == 6) {
            }
            while (tempboard[i][j] != -1 && tempboard[i][j] != player) {
                if (tempboard[i][j] == 0) {
                    break;
                }
                i++;
            }
            if (tempboard[i][j] == player) {                
                while (i != x) {
                    i--;
                    tempboard[i][j] = player;
                }
            }
        }
    }


   
    public ArrayList validMoves(int tempboard[][], int player) {
        ArrayList validcells = new ArrayList();
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                if (checkmovevalidity(tempboard,i, j, player) == true) {
                    validcells.add(new Cell(i, j));
                }
            }

        }
        return validcells;
    }

    public boolean ispassallowed() {
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                if (board[i][j] == 0) {
                    if ((checkmovevalidity(board,i, j, Coin.WHITE)) == true) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public boolean checkmovevalidity(int tempboard[][],int x, int y, int player) {
        boolean moveisvalid = false;
        int i, j;
        if (tempboard[x][y] == 0) {
            i = x;
            j = y;

            if (tempboard[i][j + 1] != player) {
                while (tempboard[i][j] != -1 && tempboard[i][j] != player) {
                    j++;
                    if (tempboard[i][j] == 0) {                        
                        break;
                    }
                }
                if (tempboard[i][j] == player) {
                    moveisvalid = true;
                    return moveisvalid;
                }
            }
            i = x;
            j = y;
            if (tempboard[i][j - 1] != player) {
                while (tempboard[i][j] != -1 && tempboard[i][j] != player) {
                    j--;
                    if (tempboard[i][j] == 0) {                        
                        break;
                    }
                }
                if (tempboard[i][j] == player) {
                    moveisvalid = true;
                    return moveisvalid;
                }
            }

            i = x;
            j = y;
            if (tempboard[i - 1][j] != player) {
                while (tempboard[i][j] != -1 && tempboard[i][j] != player) {
                    i--;
                    if (tempboard[i][j] == 0) {                        
                        break;
                    }
                }
                if (tempboard[i][j] == player) {
                    moveisvalid = true;
                    return moveisvalid;
                }
            }
            i = x;
            j = y;
            if (tempboard[i + 1][j] != player) {                
                while (tempboard[i][j] != -1 && tempboard[i][j] != player) {
                    i++;
                    if (tempboard[i][j] == 0) {                        
                        break;
                    }
                }
                if (tempboard[i][j] == player) {              
                    moveisvalid = true;                   
                    return moveisvalid;
                }
            }
            i = x;
            j = y;
            if (tempboard[i + 1][j - 1] != player) {
                while (tempboard[i][j] != -1 && tempboard[i][j] != player) {
                    i++;
                    j--;
                    if (tempboard[i][j] == 0) {                        
                        break;
                    }
                }
                if (tempboard[i][j] == player) {
                    moveisvalid = true;
                    return moveisvalid;
                }
            }
            i = x;
            j = y;
            if (tempboard[i - 1][j - 1] != player) {
                while (tempboard[i][j] != -1 && tempboard[i][j] != player) {
                    i--;
                    j--;
                    if (tempboard[i][j] == 0) {
                        break;
                    }
                }
                if (tempboard[i][j] == player) {
                    moveisvalid = true;
                    return moveisvalid;
                }
            }
            i = x;
            j = y;
            if (tempboard[i + 1][j + 1] != player) {
                while (tempboard[i][j] != -1 && tempboard[i][j] != player) {
                    i++;
                    j++;
                    if (tempboard[i][j] == 0) {                        
                        break;
                    }
                }
                if (tempboard[i][j] == player) {
                    moveisvalid = true;
                    return moveisvalid;
                }
            }
            i = x;
            j = y;
            if (tempboard[i - 1][j + 1] != player) {
                while (tempboard[i][j] != -1 && tempboard[i][j] != player) {
                    i--;
                    j++;
                    if (tempboard[i][j] == 0) {                        
                        break;
                    }
                }
                if (tempboard[i][j] == player) {
                    moveisvalid = true;
                    return moveisvalid;
                }
            }
            return moveisvalid;

        } else {
            return moveisvalid;
        }


    }
}

