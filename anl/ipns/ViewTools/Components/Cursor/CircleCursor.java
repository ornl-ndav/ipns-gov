/* 
 * file: CircleCursor.java
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
 *  Revision 1.4  2004/04/02 20:58:32  millermi
 *  - Fixed javadoc errors
 *
 *  Revision 1.3  2004/03/12 01:33:22  millermi
 *  - Changed package and imports.
 *
 *  Revision 1.2  2003/12/29 20:59:29  millermi
 *  - Reduced imported packages.
 *
 *  Revision 1.1  2003/05/24 17:32:20  dennis
 *  Initial version of cursor selection. (Mike Miller)
 *
 */
 package gov.anl.ipns.ViewTools.Components.Cursor;

 import javax.swing.JPanel;
 import java.io.Serializable;
 import java.awt.Graphics;
 import java.awt.Point;
 
 import gov.anl.ipns.ViewTools.Panels.Cursors.XOR_Cursor;

/** 
 *  This class implements a Rubberband Circle cursor for selecting regions.
 *  Important to note is that this cursor is used to make EllipseRegions,
 *  however, the cursor itself will always be circular. EllipseRegions were
 *  used to generalize because scaling does not always yield a circular region.
 *
 *  @see  gov.anl.ipns.ViewTools.Panels.Cursors.XOR_Cursor
 *  @see  gov.anl.ipns.ViewTools.Components.Region.EllipseRegion
 */
public class CircleCursor extends  XOR_Cursor 
                          implements Serializable
{
 /**
  *  Construct a new CircleCursor to be used on a JPanel.
  *
  *  @param panel The JPanel for this cursor.
  *
  */
  public CircleCursor ( JPanel panel ) 
  {
    super( panel );
  }

 /**
  *  This method draws a circle with two specified points, the center and
  *  a point on the radius.
  *
  *  @param  graphics	The graphics context that the circle will be drawn in.
  *
  *  @param  p1 	Center point of the circle.
  *
  *  @param  p2 	Radius point of the circle.
  */
  public void draw( Graphics graphics, Point p1, Point p2 )
  {
    int x = Math.abs( p1.x - p2.x );
    int y = Math.abs( p1.y - p2.y );
    int r = (int)Math.sqrt( Math.pow(x,2) + Math.pow(y,2) ); 
        	 
    graphics.drawOval( (p1.x - r), (p1.y - r), 2*r, 2*r );
    graphics.drawLine( p1.x - 2, p1.y, p1.x + 2, p1.y );
    graphics.drawLine( p1.x, p1.y - 2, p1.x, p1.y + 2 );
  }


 /**
  *  This method returns the region determined by the starting and
  *  ending point of the circle cursor.
  *
  *  @return  A circle determined by the starting and ending point of 
  *	      the circle cursor
  */
  public Circle region() 
  {
    int x = Math.abs( first_pt.x - last_pt.x );
    int y = Math.abs( first_pt.y - last_pt.y );
    float r = (float)Math.sqrt( Math.pow(x,2) + Math.pow(y,2) );

    return new Circle( first_pt, r );
  }
}
