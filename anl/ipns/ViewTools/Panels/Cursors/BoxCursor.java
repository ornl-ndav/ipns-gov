/*
 *  File:  BoxCursor.java 
 *
 * Copyright (C) 2001, Dennis Mikkelson
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307, USA.
 *
 * Contact : Dennis Mikkelson <mikkelsond@uwstout.edu>
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751, USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * ( The basic technique used here was adapted from an example in
 *   "graphic JAVA", David M. Geary, Alan L. McClellan, SunSoft Press
 *   Prentice Hall, 1997 )
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.2  2002/11/27 23:13:18  pfpeterson
 *  standardized header
 *
 */

package DataSetTools.components.image;

import javax.swing.*;
import java.io.*;
import java.awt.*;

/** 
 *
 *  This class implements a Rubberband Box cursor for selecting regions.
 *
 *  @see  XOR_Cursor
 *
 */

public class BoxCursor extends  XOR_Cursor 
                                implements Serializable
{

/**
 *  Construct a new BoxCursor to be used on a JPanel.
 *
 *  @param panel The JPanel for this cursor.
 *
 */
  public BoxCursor ( JPanel panel ) 
  {
    super( panel );
  }

/**
 *  This method draws a rectangular box with the two corners at two specified
 *  points.
 *
 *  @param  graphics   The graphics context that the box will be drawn in.
 *
 *  @param  p1         One corner point of the box.
 *
 *  @param  p2         The other corner point of the box.
 */
  public void draw( Graphics graphics, Point p1, Point p2 )
  {
    int x = Math.min( p1.x, p2.x );
    int y = Math.min( p1.y, p2.y );
    int w = Math.abs( p1.x - p2.x );
    int h = Math.abs( p1.y - p2.y );
    graphics.drawRect( x, y, w, h );
  }


/**
 *  This method returns the region determined by the starting and
 *  ending point of the box cursor.
 *
 *  @return  A rectangle determined by the starting and ending point of 
 *           the box cursor
 */

  public Rectangle region() 
  {
    int x = Math.min( first_pt.x, last_pt.x );
    int y = Math.min( first_pt.y, last_pt.y );
    int w = Math.abs( first_pt.x - last_pt.x );
    int h = Math.abs( first_pt.y - last_pt.y );
    return new Rectangle( x, y, w, h );
  }
}
