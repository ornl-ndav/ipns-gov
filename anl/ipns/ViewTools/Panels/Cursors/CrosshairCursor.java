/*
 * @(#) CrosshairCursor.java
 *
 * Copyright (C) 1999, Dennis Mikkelson
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
import java.awt.Graphics;
import java.awt.Rectangle;

/** 
 * A Rubberband that draws a crosshair cursor.
 *
 * @see     Rubberband
 */
public class CrosshairCursor extends    Rubberband
                             implements Serializable
{

    public CrosshairCursor(JPanel component) {
        super(component);
    }

    public void drawLast(Graphics graphics) {
        Rectangle rect = component.getVisibleRect();
        int min_x = rect.x;
        int max_x = rect.x + rect.width;
        int min_y = rect.y;
        int max_y = rect.y + rect.height;
                                                    // only draw in region 
        if ( last.x >= min_x && last.x <= max_x )
          graphics.drawLine(last.x, min_y, last.x, max_y);

        if ( last.y >= min_y && last.y <= max_y )
          graphics.drawLine( min_x, last.y, max_x, last.y);
    }

    public void drawNext(Graphics graphics) {
        Rectangle rect = component.getVisibleRect();
        int min_x = rect.x;
        int max_x = rect.x + rect.width;
        int min_y = rect.y;
        int max_y = rect.y + rect.height;
                                                    // only draw in region
        if ( stretched.x >= min_x && stretched.x <= max_x )
          graphics.drawLine( stretched.x, min_y, stretched.x, max_y );

        if ( stretched.y >= min_y && stretched.y <= max_y )
          graphics.drawLine( min_x, stretched.y, max_x, stretched.y );
    }

}
