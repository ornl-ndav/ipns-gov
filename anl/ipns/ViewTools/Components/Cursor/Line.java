/* 
 * file: Line.java
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
 * Modified:
 *
 *  $Log$
 *  Revision 1.4  2004/03/12 01:33:22  millermi
 *  - Changed package and imports.
 *
 *  Revision 1.3  2003/11/18 01:03:29  millermi
 *  - Now implement serializable to allow saving of state.
 *
 *  Revision 1.2  2003/10/16 05:00:04  millermi
 *  - Fixed java docs errors.
 *
 *  Revision 1.1  2003/06/05 22:10:09  dennis
 *   - Initial Version, data structure used by LineCursor (Mike Miller)
 *
 *
 */

 package gov.anl.ipns.ViewTools.Components.Cursor;

 import java.awt.Point;

/**
 * This class is a datastructure containing two points that are 
 * used to create a line.
 */   
public class Line implements java.io.Serializable
{
   private Point p1;
   private Point p2;
  
  /**
   * Constructor saves the two points.
   *
   *  @param  point1
   *  @param  point2
   */ 
   public Line( Point point1, Point point2 )
   {
      p1 = point1;
      p2 = point2;
   }
   
  /**
   * Get the first point of the line.
   *
   *  @return p1
   */ 
   public Point getP1()
   {
      return p1;
   }
  
  /**
   * Get the last point of the line.
   *
   *  @return p2
   */ 
   public Point getP2()
   {
      return p2;
   }       
}
