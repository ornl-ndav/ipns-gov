/*
 * File:  XOR_Cursor.java  
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
 *  $Log$
 *  Revision 1.5  2007/06/15 22:37:12  oakgrovej
 *  Added cursor tag
 *
 *  Revision 1.4  2006/07/31 01:59:18  dennis
 *  Fixed bug in start() method.  The y-coordinates were
 *  improperly set.  This is a partial fix to problems with
 *  cursors being drawn erratically.
 *
 *  Revision 1.3  2004/03/12 00:14:09  rmikk
 *  Fixed Package names
 *
 *  Revision 1.2  2002/11/27 23:13:18  pfpeterson
 *  standardized header
 *
 */

package gov.anl.ipns.ViewTools.Panels.Cursors;

import gov.anl.ipns.ViewTools.Components.Cursor.CursorTag;

import java.awt.*;
import java.io.*;
import javax.swing.*;

/** 
 * This class is an abstract base class for cursors such as a rubber band 
 * box and a full screen crosshair cursor. 
 *
 * @see    CrosshairCursor 
 * @see    BoxCursor 
 */


abstract public class XOR_Cursor implements Serializable, CursorTag
{
    protected JPanel    panel;
    protected Point     first_pt     = new Point(0,0); 
    private   Point     current_pt   = new Point(0,0);
    protected Point     last_pt      = new Point(0,0); 

    private   Color     color        = Color.gray;

    private   boolean   firstStretch = true;
    private   boolean   active       = false;


/**
 *  Abstract method to draw the cursor shape, determined by two points, the 
 *  starting point a second designated point.  Since XOR drawing is used,
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
 *  Construct a new XOR_Cursor to be used on a JPanel.
 *
 *  @param panel The JPanel for this cursor. 
 *
 */
  public XOR_Cursor( JPanel panel ) 
  {
    this.panel = panel;
  }


/**
 *  Start the XOR_Cursor action at the specified point.
 *
 *  @param  p starting point for the XOR_Cursor, eg. the first corner or
 *            of a rubber band box.
 */
  public void start( Point p ) 
  {
    if ( !active )                          // only set first point if we have 
    {                                       // not already started the cursor 
      active = true;
      firstStretch = true;

      first_pt.y   = p.y;
      current_pt.y = p.y;
      last_pt.y    = p.y;

      first_pt.x   = p.x;
      current_pt.x = p.x;
      last_pt.x    = p.x;
    }
  }


/**
 *  Erase the previous XOR_Cursor by drawing it again and then draw it in
 *  the new position using a new point.
 *
 *  @param  p  The point to use as the current point of the cursor.
 */
  public boolean redraw( Point p ) 
  {
    if ( !active )                               // don't  redraw if we haven't
      return( false );                           // started yet. D.M.

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
      draw( graphics, first_pt, last_pt );

    draw( graphics, first_pt, current_pt );

    return ( true ); 
  }


/**
 *  Erase the previous XOR_Cursor by drawing it again and then draw it in
 *  a new position that is displaced from the current point by a specified
 *  increment.
 *
 *  @param increment  The vector to use when shifting the current point to
 *                    obtain the new position of the cursor.
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
 *  Stop the current XOR_Cursor action and erase the cursor.  Record the
 *  last position of the cursor.
 *
 *  @param p  The point to be recorded as the last position of the cursor.
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
      draw( graphics, first_pt, last_pt );
    }

    active = false;
    return ( true );
  }

}
