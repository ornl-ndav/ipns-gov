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
 *  Revision 1.1  2003/06/18 13:38:33  dennis
 *  (Mike Miller)
 *  - Initial version of interface that extends IAxisAddible2D to also
 *    allow adding a color scale "legend using the method getColorScale().
 *
 */
 
package DataSetTools.components.View.TwoD;

import java.awt.event.ActionListener;

/**
 * This interface is implemented by view components that utilize the 
 * ControlColorScale with calibrations. 
 */
public interface IColorScaleAddible extends IAxisAddible2D
{
  /**
   * This method returns the color scale of the center image. 
   * Possible scales are listed in file IndexColorScale.java.
   */
   public String getColorScale();
   
  /**
   * This method adds an action listener to this component.
   */ 
   public void addActionListener( ActionListener a );
   
  /**
   * This method will get the current log scale value for the imagejpanel.
   */ 
   public double getLogScale();
}




   
