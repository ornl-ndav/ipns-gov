/* 
 * file: PointCursor.java
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
 *  Revision 1.1  2003/05/24 17:32:20  dennis
 *  Initial version of cursor selection. (Mike Miller)
 *
 */

 package DataSetTools.components.View.Cursor;

 import javax.swing.*;
 import java.io.*;
 import java.awt.*;
 
 import DataSetTools.components.image.*;

/** 
 *  This class implements a Rubberband Circle cursor for selecting regions.
 *
 *  @see  XOR_Cursor
 */

public class PointCursor extends  XOR_Cursor 
                                implements Serializable
{
  /**
   *  Construct a new PointCursor to be used on a JPanel.
   *
   *  @param  panel The JPanel for this cursor.
   *
   */
   public PointCursor ( JPanel panel ) 
   {
      super( panel );
   }

  /**
   *  This method draws a circle with two specified points, the center and
   *  a point on the radius.
   *
   *  @param  graphics   The graphics context that the circle will be drawn in.
   *
   *  @param  p1         Center point of the circle.
   *
   *  @param  p2         Radius point of the circle.
   */
   public void draw( Graphics graphics, Point p1, Point p2 )
   {
      graphics.drawLine( p1.x - 5, p1.y, p1.x + 5, p1.y );
      graphics.drawLine( p1.x, p1.y - 5, p1.x, p1.y + 5 );
   }


  /**
   *  This method returns the region determined by the starting and
   *  ending point of the circle cursor.
   *
   *  @return  A circle determined by the starting and ending point of 
   *           the circle cursor
   */

   public Point region() 
   {
      return first_pt;
   }
}
