/*
 * File: IColorScaleAddible.java
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
 *  Revision 1.4  2004/08/06 18:48:52  millermi
 *  - Added public messaging String COLORSCALE_CHANGED which is to be
 *    sent out whenever the colorscale is changed.
 *
 *  Revision 1.3  2004/03/17 20:26:51  dennis
 *  Fixed @see tag that was broken when view components, math and
 *  util were moved to gov package.
 *
 *  Revision 1.2  2004/03/12 02:33:05  rmikk
 *  Fixed Package names
 *
 *  Revision 1.1  2003/12/23 01:55:51  millermi
 *  - Moved from TwoD directory to a more logical directory. Now
 *    in the same directory as the class that it provides
 *    functionality for.
 *
 *  Revision 1.4  2003/12/20 20:07:03  millermi
 *  - Replaced getDataMin() and getDataMax() with getValueAxisInfo()
 *    which will return an AxisInfo object containing the min, max,
 *    and more.
 *
 *  Revision 1.3  2003/12/18 22:42:13  millermi
 *  - This file was involved in generalizing AxisInfo2D to
 *    AxisInfo. This change was made so that the AxisInfo
 *    class can be used for more than just 2D axes.
 *
 *  Revision 1.2  2003/07/05 19:48:40  dennis
 *  - Now implements ILogAxisAddible2D
 *  - Added methods getDataMin() and getDataMax().
 *  (Mike Miller)
 *
 *  Revision 1.1  2003/06/18 13:38:33  dennis
 *  (Mike Miller)
 *  - Initial version of interface that extends IAxisAddible2D to also
 *    allow adding a color scale "legend using the method getColorScale().
 *
 */
 
package gov.anl.ipns.ViewTools.Components.ViewControls;

import java.awt.event.ActionListener;

import gov.anl.ipns.ViewTools.Components.AxisInfo;
import gov.anl.ipns.ViewTools.Components.Transparency.ILogAxisAddible;

/**
 * This interface is implemented by view components that utilize the 
 * ControlColorScale with calibrations. 
 */
public interface IColorScaleAddible extends ILogAxisAddible
{
  /**
   * "ColorScale Changed" - This messaging String is to be sent out by all
   * IColorScaleAddible objects when the colorscale is changed.
   */
   public static final String COLORSCALE_CHANGED = "ColorScale Changed";
   
  /**
   * This method returns the color scale of the center image. 
   * Possible scales are listed in file IndexColorMaker.java.
   *
   *  @return IndexColorScale string code
   *  @see gov.anl.ipns.ViewTools.Panels.Image.IndexColorMaker
   */
   public String getColorScale();
   
  /**
   * This method adds an action listener to this component.
   *
   *  @param  actionlistener A listener of this component.
   */ 
   public void addActionListener( ActionListener actionlistener );
   
  /**
   * This method will get the current AxisInfo about the data from the
   * component.
   *
   *  @return AxisInfo of the data, 
   *          Including: datamin, datamax, units, and label
   */    
   public AxisInfo getValueAxisInfo();
}




   
