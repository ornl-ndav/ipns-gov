/*
 * File:  XOR_PanCursor.java  
 *
 * Copyright (C) 2003, Mike Miller
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
 * Primary   Mike Miller <millermi@uwstout.edu>
 * Contact:  Student Developer, University of Wisconsin-Stout
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
 * This code closely follows XOR_Cursor.java created by Dennis Mikkelson.
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.4  2004/04/02 20:58:33  millermi
 *  - Fixed javadoc errors
 *
 *  Revision 1.3  2004/03/12 01:33:24  millermi
 *  - Changed package and imports.
 *
 *  Revision 1.2  2003/10/29 20:35:46  millermi
 *  -Added moveEdge() and public variables for use by this method. moveEdge()
 *   allows for stretching bounds.
 *
 *  Revision 1.1  2003/10/27 08:47:48  millermi
 *  - Initial Version - This class was created to enable users
 *    panning options for images too large to view in the
 *    viewport.
 *
 */

package gov.anl.ipns.ViewTools.Components.Cursor;

import java.awt.Point;
import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;
import javax.swing.JPanel;

/** 
 * This class is an abstract base class for cursors that translate but do
 * not change size. 
 *
 * @see  gov.anl.ipns.ViewTools.Components.Cursor.BoxPanCursor 
 */


abstract public class XOR_PanCursor implements Serializable
{
  public static final int NORTH = 0;
  public static final int EAST  = 1;
  public static final int SOUTH = 2;
  public static final int WEST  = 3;
  
  protected JPanel    panel;
  protected Point     first_pt     = new Point(0,0); 
  protected Point     last_pt	   = new Point(0,0); 

  private   Color     color	   = Color.gray;

  private   boolean   firstStretch = true;


/**
 *  Abstract method to draw the cursor shape, determined by two points, the 
 *  starting point and a second designated point.  Since XOR drawing is used,
 *  this method will be called twice for each cursor move.  The first call
 *  will be made to erase the previous cursor by drawing it again.  The
 *  second call will be made to draw a new cursor position.  Derived classes
 *  should implement this to actually draw the desired cursor shape.
 *
 *  @param  graphics   The graphics context that the shape should be
 *                     drawn in.
 *
 *  @param  p1         The starting point for this cursor action, such as
 *                     the first corner of a box cursor.
 *
 *  @param  p2         The second point for this cursor action, such as
 *                     the other corner of a box cursor.
 *
 */
    abstract public void draw( Graphics graphics, Point p1, Point p2 );


 /**
  *  Construct a new XOR_PanCursor to be used on a JPanel.
  *
  *  @param  panel The JPanel for this cursor. 
  *
  */
  public XOR_PanCursor( JPanel panel ) 
  {
    this.panel = panel;
  }
 /**
  *  Initialize the bounds of the box. This must be done any time the
  *  size of the box changes.
  *
  *  @param  p1 first defining point of the region.
  *  @param  p2 second defining point of the region. 
  */
  public boolean init( Point p1, Point p2 )
  {
    first_pt = new Point(p1);
    last_pt = new Point(p2);    
    //firstStretch = false;
    //translate(first_pt);   
    //firstStretch = true;
    return true;
  }
 
 /**
  * Move the first defining point of the cursor to the new point. All other
  * points are translated the same distance.
  *
  *  @param  p The new defining point the cursor is being translated to.
  */ 
  public boolean translate( Point p )
  {       
    Point old_first = new Point(first_pt);
    Point old_last  = new Point(last_pt);
    int x_tran = p.x - first_pt.x;
    int y_tran = p.y - first_pt.y;
    first_pt.y   += y_tran;
    last_pt.y	 += y_tran;

    first_pt.x   += x_tran;
    last_pt.x	 += x_tran;

    Graphics graphics = panel.getGraphics();
    if ( graphics == null )
      return false;

    graphics.setXORMode( color );
    
    if(firstStretch == true) 
      firstStretch = false;
    else                     
      draw( graphics, old_first, old_last );

    draw( graphics, first_pt, last_pt );
    return true;
  }
  
  public boolean moveEdge( int edge, int amount )
  {
    Point old_first = new Point(first_pt);
    Point old_last  = new Point(last_pt);
    final int SENSITIVITY = 4;
  
    if( edge == NORTH )
    {
      if( (first_pt.y - amount) < last_pt.y - SENSITIVITY )
        first_pt.y -= amount;
    }
    else if( edge == EAST )
    {
      if( last_pt.x + amount > first_pt.x + SENSITIVITY )
        last_pt.x += amount;
    }
    else if( edge == SOUTH )
    {
      if( last_pt.y + amount > first_pt.y + SENSITIVITY )
        last_pt.y += amount;
    }
    else if( edge == WEST )
    {
      if( first_pt.x - amount < last_pt.x - SENSITIVITY )
        first_pt.x -= amount; 
    }
          
    Graphics graphics = panel.getGraphics();
    if ( graphics == null )
      return false;

    graphics.setXORMode( color );
    
    if(firstStretch == true) 
      firstStretch = false;
    else                     
      draw( graphics, old_first, old_last );

    draw( graphics, first_pt, last_pt );
    return true;
  
  }

}
