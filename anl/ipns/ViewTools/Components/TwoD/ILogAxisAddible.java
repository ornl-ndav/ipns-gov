/*
 * File: ILogAxisAddible2D.java
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
 *  Revision 1.1  2003/12/18 22:42:12  millermi
 *  - This file was involved in generalizing AxisInfo2D to
 *    AxisInfo. This change was made so that the AxisInfo
 *    class can be used for more than just 2D axes.
 *
 *  Revision 1.1  2003/07/05 19:50:06  dennis
 *  - Initial version: Only one method, getLogScale(). For components
 *    that need log scaling, this interface adds that capability to
 *    the IAxisAddible2D interface. If log scaling is not required,
 *    implement IAxisAddible2D instead. (Mike Miller)
 *
 */
 
package DataSetTools.components.View.TwoD;

/**
 * This interface is implemented by components that utilize the 
 * the AxisOverlay with logarithmic calibrations. 
 */
public interface ILogAxisAddible extends IAxisAddible
{
  /**
   * This method will get the current log scale value for the region specified
   * by the getRegionInfo().
   */ 
   public double getLogScale();
}
