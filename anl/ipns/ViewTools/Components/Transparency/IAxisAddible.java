/*
 * File: IAxisAddible.java
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
 *  Revision 1.4  2004/11/11 19:48:47  millermi
 *  - Now extends IZoomAddible to gain access to local and global bounds to
 *    enable calibrations of logs while zooming.
 *
 *  Revision 1.3  2004/04/02 20:58:33  millermi
 *  - Fixed javadoc errors
 *
 *  Revision 1.2  2004/03/12 02:23:21  serumb
 *  Changed package and imports.
 *
 *  Revision 1.1  2003/12/23 01:55:51  millermi
 *  - Moved from TwoD directory to a more logical directory. Now
 *    in the same directory as the class that it provides
 *    functionality for.
 *
 *  Revision 1.1  2003/12/18 22:42:12  millermi
 *  - This file was involved in generalizing AxisInfo2D to
 *    AxisInfo. This change was made so that the AxisInfo
 *    class can be used for more than just 2D axes.
 *
 *
 ****************************IAxisAddible2D*****************************
 *  Revision 1.4  2003/07/25 14:36:21  dennis
 *  - Removed methods getLocalCoordBounds() and getGlobalCoordBounds()
 *    since these methods are not required by the AxisOverlay2D.
 *  - Moved method getRegionInfo() to IOverlayAddible
 *  - This interface now implements IOverlayAddible (Mike Miller)
 *
 *  Revision 1.3  2003/06/13 14:44:54  dennis
 *  - Added methods getLocalCoordBounds() and getGlobalCoordBounds() to
 *    allow selection and annotation overlays to adjust when a zoom occurs.
 *    (Mike Miller)
 *
 *  Revision 1.2  2003/05/16 14:59:53  dennis
 *  Added acknowlegement of NSF funding.
 *
 */
 
package gov.anl.ipns.ViewTools.Components.Transparency;

import java.awt.Font;

import gov.anl.ipns.ViewTools.Components.AxisInfo;

/**
 * This interface is implemented by view components that utilize the 
 * AxisOverlay2D for displaying axis calibrations.
 *
 *  @see gov.anl.ipns.ViewTools.Components.Transparency.AxisOverlay2D
 */
public interface IAxisAddible extends IZoomAddible
{
 /**
  * The integer code, either zero for x, or one for y, will determine which
  * axis to get information for. The information is wrapped in an
  * AxisInfo object.
  *
  *  @param  axiscode The AxisInfo axis code.
  *  @return Axis information pertaining to the specified axis.
  */
  public AxisInfo getAxisInformation(int axiscode);
  
 /**
  * This method will return the title given to the image as specified by
  * the Virtual Array
  *
  *  @return The title being placed on the AxisOverlay.
  */
  public String getTitle();
  
 /**
  * This method will return the precision specified by the user. Precision
  * will be assumed to be 4 if not specified. The axis overlays will call
  * this method to determine the precision.
  *
  *  @return The precision numbers are being limited to.
  */
  public int getPrecision();   
  
 /**
  * This method will return the font used on by the overlays. The axis overlay
  * will call this to determine what font to use.
  *
  *  @return The display font.
  */
  public Font getFont();
}




   
