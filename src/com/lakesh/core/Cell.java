/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lakesh.core;

/**
 *
 * @author lakesh
 */

/*
 * This class represents each cell of the board
 */
public class Cell {
    private int x;
    private int y;
    
    
    public Cell(int x,int y){
        this.x = x;
        this.y = y;
    }
    
    public int getx() {
        return x;
    }
    
    public int gety() {
        return y;
    }
    
    public void setx(int x) {
        this.x = x;
    }
    
    public void sety(int y){
        this.y = y;
    }

}
