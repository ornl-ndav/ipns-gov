/* 
 * file: EllipseCursor.java
 *
 * Copyright (C) 2003, Mike Miller
 *               2007, Dennis Mikkelson
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
 * This work was supported by the National Science Foundation under grant
 * number DMR-0218882, and by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * (This follows the format of BoxCursor.java created by Dennis Mikkelson)
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.1  2007/03/17 04:24:36  dennis
 *  Class for ellipse, similar to class for circle.
 *
 *
 */
 package gov.anl.ipns.ViewTools.Components.Cursor;

 import javax.swing.JPanel;
 import java.io.Serializable;
 import java.awt.Graphics;
 import java.awt.Point;
 
 import gov.anl.ipns.ViewTools.Panels.Cursors.XOR_Cursor;

/** 
 *  This class implements a Rubberband ellipse cursor for selecting regions.
 *
 *  @see  gov.anl.ipns.ViewTools.Panels.Cursors.XOR_Cursor
 *  @see  gov.anl.ipns.ViewTools.Components.Region.EllipseRegion
 */
public class EllipseCursor extends  XOR_Cursor 
                           implements Serializable
{
 /**
  *  Construct a new EllipseCursor to be used on a JPanel.
  *
  *  @param panel The JPanel for this cursor.
  *
  */
  public EllipseCursor ( JPanel panel ) 
  {
    super( panel );
  }

 /**
  *  This method draws an ellipse with two specified points, the center and
  *  a point on the radius.
  *
  *  @param  graphics	The graphics context that the ellipse will be drawn in.
  *
  *  @param  center     Center point of the ellipse.
  *
  *  @param  corner     Corner point of bounding box for the ellipse.
  */
  public void draw( Graphics graphics, Point center, Point corner )
  {
    int dx = Math.abs( corner.x - center.x );
    int dy = Math.abs( corner.y - center.y );
        	 
    graphics.drawOval( center.x - dx, center.y - dy, 2*dx, 2*dy );
    graphics.drawLine( center.x - 3, center.y,center.x + 3, center.y );
    graphics.drawLine( center.x, center.y - 3, center.x, center.y + 3 );
  }


 /**
  *  This method returns the region determined by the starting and
  *  ending point of the circle cursor.
  *
  *  @return  An Ellipse determined by the starting and ending point of 
  *	      the Ellipse cursor
  */
  public Ellipse region() 
  {
    int dx = Math.abs( first_pt.x - last_pt.x );
    int dy = Math.abs( first_pt.y - last_pt.y );

    return new Ellipse( first_pt.x, first_pt.y, dx, dy );
  }
}
