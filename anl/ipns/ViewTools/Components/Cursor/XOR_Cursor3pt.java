/*
 * File:  XOR_Cursor3pt.java  
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
 * ( Extends XOR_Cursor.java to regions specified by 3 points. )
 * ( The basic technique used here was adapted from an example in  
 *   "graphic JAVA", David M. Geary, Alan L. McClellan, SunSoft Press
 *   Prentice Hall, 1997 )
 *
 *  $Log$
 *  Revision 1.6  2007/06/15 22:30:40  oakgrovej
 *  Added cursor tag & get panel method
 *
 *  Revision 1.5  2004/04/02 20:58:33  millermi
 *  - Fixed javadoc errors
 *
 *  Revision 1.4  2004/03/12 01:33:23  millermi
 *  - Changed package and imports.
 *
 *  Revision 1.3  2003/12/29 22:13:32  millermi
 *  - Fixed bug that did not erase the portion of the cursor
 *    drawn before midpoint() was called.
 *
 *  Revision 1.2  2003/10/16 05:00:05  millermi
 *  - Fixed java docs errors.
 *
 *  Revision 1.1  2003/08/21 18:20:44  millermi
 *  - Initial Version, similar to XOR_Cursor.java, except this cursor
 *    handles regions specified by 3 points.
 *
 */

package gov.anl.ipns.ViewTools.Components.Cursor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.io.Serializable;
import javax.swing.JPanel;

/** 
 * This class is an abstract base class for cursors that require three points
 * for specification. Examples would be WedgeCursor and DoubleWedgeCursor.
 *
 * @see gov.anl.ipns.ViewTools.Components.Cursor.WedgeCursor 
 * @see gov.anl.ipns.ViewTools.Components.Cursor.DoubleWedgeCursor
 */
abstract public class XOR_Cursor3pt implements Serializable, CursorTag
{
    protected JPanel    panel;
    protected Point     first_pt     = new Point(0,0); 
    protected Point     mid_pt       = new Point(0,0);
    private   Point     current_pt   = new Point(0,0);
    protected Point     last_pt      = new Point(0,0); 

    private   Color     color        = Color.gray;

    private   boolean   firstStretch = true;
    private   boolean   active       = false;
    private   boolean   mpset        = false;  // true if midpoint is set


/**
 *  Abstract method to draw the cursor shape, determined by three points, the 
 *  starting point, a mid point, and a third designated point. Since XOR 
 *  drawing is used, this method will be called twice for each cursor move.
 *  The first call will be made to erase the previous cursor by drawing it 
 *  again. The second call will be made to draw a new cursor position. 
 *  Derived classes should implement this to actually draw the desired cursor
 *  shape.
 *
 *  @param  graphics   The graphics context that the shape should be
 *                     drawn in.
 *
 *  @param  p1         The starting point for this cursor action, the initial
 *                     point of the cursor.
 *
 *  @param  p2         The second point for this cursor action, the midpoint
 *                     of the cursor.
 *
 *  @param  p3         The third point for this cursor action, the final 
 *                     point of the cursor.
 *
 */
 abstract public void draw( Graphics graphics, Point p1, Point p2, Point p3 );

/**
 *  Construct a new XOR_Cursor3pt to be used on a JPanel.
 *
 *  @param panel The JPanel for this cursor. 
 *
 */
  public XOR_Cursor3pt( JPanel panel ) 
  {
    this.panel = panel;
  }

/**
 *  Start the XOR_Cursor3pt action at the specified point.
 *
 *  @param  p starting point for the XOR_Cursor3pt, eg. the vertex
 *            of a rubber band wedge.
 */
  public void start( Point p ) 
  {
    if ( !active )                          // only set first point if we have 
    {                                       // not already started the cursor 
      active = true;
      firstStretch = true;

      first_pt.y   = p.y;
      mid_pt.y     = p.x;
      current_pt.y = p.x;
      last_pt.y    = p.x;

      first_pt.x   = p.x;
      mid_pt.x     = p.x;
      current_pt.x = p.x;
      last_pt.x    = p.x;
    }
  }

/**
 * Store the value of the second point used to define the cursor region.
 *
 *  @param  p second point
 *  @return successful or not
 */
 public boolean midpoint( Point p )
 {
   if ( !active )                                // don't  redraw if we haven't
     return( false );                            // started yet. D.M.
  
   if ( !firstStretch ) 	   // only redraw to erase last cursor IF
   {				   // we've already drawn something

     Graphics graphics = panel.getGraphics();
     if ( graphics == null )
       return false;

     mid_pt.x = p.x;
     mid_pt.y = p.y;
     last_pt.x = p.x;
     last_pt.y = p.y;
     mpset = true;

     graphics.setXORMode( color );
     draw( graphics, first_pt, mid_pt, last_pt );
   }
   return ( true );
 }

/**
 *  Erase the previous XOR_Cursor3pt by drawing it again and then draw it in
 *  the new position using a new point.
 *
 *  @param  p  The point to use as the current point of the cursor.
 *  @return successful or not
 */
  public boolean redraw( Point p ) 
  {
    if ( !active )                               // don't  redraw if we haven't
      return( false );                           // started yet. D.M.
    if( !mpset )
    {
      mid_pt.x     = current_pt.x;
      mid_pt.y     = current_pt.y;
    }
    last_pt.x    = current_pt.x;
    last_pt.y    = current_pt.y;
    current_pt.x = p.x;
    current_pt.y = p.y;

    Graphics graphics = panel.getGraphics();
    if ( graphics == null )
      return false;

    graphics.setXORMode( color );

    if(firstStretch == true) 
      firstStretch = false;
    else                     
      draw( graphics, first_pt, mid_pt, last_pt );
    
    if( !mpset )
      draw( graphics, first_pt, current_pt, current_pt );
    else
      draw( graphics, first_pt, mid_pt, current_pt );

    return ( true ); 
  }


/**
 *  Erase the previous XOR_Cursor3pt by drawing it again and then draw it in
 *  a new position that is displaced from the current point by a specified
 *  increment. The increment may affect the second point if p2 is not yet set,
 *  or else it will affect p3 if p2 has already been stored.
 *
 *  @param  increment  The vector to use when shifting the current point to
 *                     obtain the new position of the cursor.
 *  @return successful or not
 */
  public boolean move( Point increment ) 
  {
    if ( !active )                           // don't do the move if we haven't
      return( false );                       // started yet. D.M.

    Point temp = new Point(0,0);

    temp.x = current_pt.x + increment.x;
    temp.y = current_pt.y + increment.y;
      
    return( redraw(temp) );
  }


/**
 *  Stop the current XOR_Cursor3pt action and erase the cursor.  Record the
 *  last position of the cursor.
 *
 *  @param  p  The point to be recorded as the last position of the cursor.
 *  @return successful or not
 */
  public boolean stop( Point p ) 
  {
    if ( !active )                          // ignore ending request if cursor 
      return ( false );                     // not active. 

    if ( !firstStretch )            // only redraw to erase last cursor IF
    {                               // we've already drawn something

      Graphics graphics = panel.getGraphics();
      if ( graphics == null )
        return false;

      last_pt.x = p.x;
      last_pt.y = p.y;

      graphics.setXORMode( color );
      
      // this will erase the portion of the cursor drawn before the midpoint
      // was set.
      draw( graphics, first_pt, mid_pt, mid_pt );
      // this will erase the portion of the cursor drawn after the midpoint
      // was set.
      draw( graphics, first_pt, mid_pt, last_pt );
    }

    active = false;
    mpset = false;
    return ( true );
  }
  
  public JPanel getPanel()
  {
    return panel;
  }

}
