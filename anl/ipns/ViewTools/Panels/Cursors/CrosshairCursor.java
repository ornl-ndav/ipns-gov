/*
 * @(#) CrosshairCursor.java
 *
 * Copyright (C) 1999,2001, Dennis Mikkelson
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
 *           Menomonie, WI. 54751
 *           USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 * $Log$
 * Revision 1.5  2001/06/01 20:47:14  dennis
 * Modified to work with XOR_Cursor, instead of the Rubberband class.
 *
 * Revision 1.4  2001/04/23 21:15:04  dennis
 * Added copyright and GPL info at the start of the file.
 *
 * Revision 1.3  2001/01/29 21:39:12  dennis
 * Now uses CVS version numbers.
 *
 * Revision 1.2  2000/07/10 22:11:48  dennis
 * 7/10/2000 version, many changes and improvements
 *
 *  Revision 1.3  2000/05/11 16:53:19  dennis
 *  Added RCS logging
 *
 */

package DataSetTools.components.image;

import javax.swing.*;
import java.io.*;
import java.awt.*;

/** 
 * A full screen crosshair cursor.
 *
 * @see  XOR_Cursor 
 */
public class CrosshairCursor extends    XOR_Cursor 
                             implements Serializable
{

/**
 *  Construct a new BoxCursor to be used on a JPanel.
 *
 *  @param panel The JPanel for this cursor.
 *
 */
  public CrosshairCursor( JPanel panel ) 
  {
    super( panel );
  }


/**
 *  This method draws a crosshair across the full panel, intersecting at a
 *  specified point.
 *
 *  @param  graphics   The graphics context that the crosshair will be drawn in.
 *
 *  @param  p1         This parameter is not used.
 *
 *  @param  p2         This specifies the coordinates at which the crosshair
 *                     cursor should be drawn.
 */

  public void draw( Graphics graphics, Point p1, Point p2 )
  {
    Rectangle rect = panel.getVisibleRect();
    int min_x = rect.x;
    int max_x = rect.x + rect.width;
    int min_y = rect.y;
    int max_y = rect.y + rect.height;

                                                        // only draw in region
    if ( p2.x >= min_x && p2.x <= max_x )
      graphics.drawLine( p2.x, min_y, p2.x, max_y);

    if ( p2.y >= min_y && p2.y <= max_y )
       graphics.drawLine( min_x, p2.y, max_x, p2.y);
  }

}
