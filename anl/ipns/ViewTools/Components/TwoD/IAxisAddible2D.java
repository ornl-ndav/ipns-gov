/*
 * File: IAxisAddible2D.java
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
 *  Revision 1.2  2003/05/16 14:59:53  dennis
 *  Added acknowlegement of NSF funding.
 *
 */
 
package DataSetTools.components.View.TwoD;

import java.awt.*;
import java.awt.Rectangle.*;
import java.lang.*;
import DataSetTools.components.View.*;

/**
 * This interface is implemented by view components that utilize the 
 * AxisOverlay2D for displaying axis calibrations.
 */
public interface IAxisAddible2D
{
  /**
   * The boolean, either true for x, or false for y, will determine which axis
   * to get information for. The information is wrapped in an AxisInfo2D object.
   */
   public AxisInfo2D getAxisInfo(boolean isX);
   
  /**
   * This method will return a rectangle with pixel coordinates corresponding
   * to the desired region.
   */ 
   public Rectangle getRegionInfo();
   
  /**
   * This method will return the title given to the image as specified by
   * the Virtual Array
   */
   public String getTitle();
   
  /**
   * This method will return the precision specified by the user. Precision
   * will be assumed to be 4 if not specified. The axis overlays will call
   * this method to determine the precision.
   */
   public int getPrecision();   
   
  /**
   * This method will return the font used on by the overlays. The axis overlay
   * will call this to determine what font to use.
   */
   public Font getFont();
}




   
