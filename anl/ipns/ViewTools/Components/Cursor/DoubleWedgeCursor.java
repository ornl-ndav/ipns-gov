/* 
 * file: DoubleWedgeCursor.java
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
 * (This follows the format of BoxCursor.java created by Dennis Mikkelson,
 * only this class uses three points.)
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.6  2007/03/23 20:26:56  dennis
 *  Now extends WedgeCursor, so all drawing and delicate calculations
 *  already done in WedgeCursor are not repeated here.  The second
 *  wedge is drawn by reflection points across the center point and
 *  calling the code to draw the single Wedge.
 *
 *  Revision 1.5  2004/05/11 00:56:15  millermi
 *  - Removed unused variables.
 *
 *  Revision 1.4  2004/03/12 01:33:22  millermi
 *  - Changed package and imports.
 *
 *  Revision 1.3  2003/12/16 01:43:41  millermi
 *  - Restructured selection so parameters define region
 *    in a counterclockwise direction.
 *
 *  Revision 1.2  2003/09/02 21:08:10  millermi
 *  - Removed bounding rectangle from cursor.
 *  - Directional vector new is redrawn when the wedge is being
 *    drawn, thus causing the vector to disappear.
 *
 *  Revision 1.1  2003/08/26 03:38:58  millermi
 *  - Initial Version - Makes rubberband cursor in the shape of a symmetric
 *    double wedge.
 *
 */

 package gov.anl.ipns.ViewTools.Components.Cursor;

 import javax.swing.JPanel;
 import java.io.Serializable;
 import java.awt.Point;
 import java.awt.Graphics;

/** 
 *  This class implements a Rubberband Wedge cursor for selecting regions.
 *  The wedge can range from a pie slice to a pie without a slice.
 *
 *  @see  XOR_Cursor3pt
 */

public class DoubleWedgeCursor extends    WedgeCursor
                               implements Serializable
{

 /**
  *  Construct a new DoubleWedgeCursor to be used on a JPanel.
  *
  *  @param panel The JPanel for this cursor.
  *
  */
  public DoubleWedgeCursor ( JPanel panel ) 
  {
    super( panel );
  }


 /**
  *  This method draws a wedge or pie slice and its mirror image. First a line
  *  is drawn, then the double wedge is formed.
  *
  *  @param  graphics	The graphics context that the circle will be drawn in.
  *  @param  p1 	Vertex of wedge.
  *  @param  p2 	Radius point of the wedge.
  *  @param  p3 	Size of wedge arc.
  */
  public void draw( Graphics graphics, Point p1, Point p2, Point p3 )
  {    
    Point reflected_p2 = new Point( 2*p1.x - p2.x, 2*p1.y - p2.y );
    Point reflected_p3 = new Point( 2*p1.x - p3.x, 2*p1.y - p3.y );

    super.draw( graphics, p1, reflected_p2, reflected_p3 );
    super.draw( graphics, p1, p2, p3 );
  }

}
