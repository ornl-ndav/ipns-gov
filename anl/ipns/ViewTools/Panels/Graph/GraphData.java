/*
 * File: GraphData.java
 *
 * Copyright (C) 2000, Dennis Mikkelson
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
 * Modified:
 *
 * $Log$
 * Revision 1.6  2003/06/06 14:37:15  serumb
 * added a variable of type BasicStroke for different line styles
 * added a variable, markcolor, to determine point marker color
 *
 * Revision 1.5  2002/11/27 23:13:18  pfpeterson
 * standardized header
 *
 */

package DataSetTools.components.image;

import java.io.*;
import java.awt.*;
import javax.swing.*;

public class GraphData implements Serializable 
{
  float  x_vals[]  = { 0, 1 };
  float  y_vals[]  = { 0, 1 };
  Color  color     = Color.black;
  int    linetype  = 1;
  float  linewidth = 1;
  int    marktype  = 0;
  Color  markcolor = Color.black;
  BasicStroke Stroke = new BasicStroke();
}

