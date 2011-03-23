/*
 * Coordinate.java
 * 
 * Created on Apr 3, 2008, 10:50:49 AM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lakesh.core;

import android.graphics.Point;

/**
 *
 * @author lakesh
 */

/*
 * This class stores the four coordinates of each  each cell
 */
public class Coordinate {
    private Point point[];
    
    public Coordinate(Point a,Point b,Point c,Point d) {
        point = new Point[4];
        point[0] = a;
        point[1] = b;
        point[2] = c;
        point[3] = d;
    }
    
    public Point getPoint(int index) {
        return point[index];
    }
    
}
