/*
 * File: ITruLogAxisAddible.java
 *
 * Copyright (C) 2004, Mike Miller
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
 *  Revision 1.2  2004/12/05 05:39:01  millermi
 *  - Fixed Eclipse warning.
 *
 *  Revision 1.1  2004/11/05 22:00:10  millermi
 *  - Interface to be implemented when tru log axes are desired
 *    by a component.
 *
 */
 
package gov.anl.ipns.ViewTools.Components.Transparency;

/**
 * This interface is implemented by components that utilize the 
 * the AxisOverlay with true logarithmic calibrations, that which do not
 * contain values less than or equal to zero.
 *
 *  @see gov.anl.ipns.ViewTools.Components.Transparency.AxisOverlay2D
 */
public interface ITruLogAxisAddible extends IAxisAddible
{
 /**
  * 0 - This static int specifies the x-axis for getPositiveMin().
  */
  public static int X_AXIS = 0;
  
 /**
  * 1 - This static int specifies the y-axis for getPositiveMin().
  */
  public static int Y_AXIS = 1;
  
 /**
  * This method will get the minimum positive value for the x and y axis.
  * The value returned must be GREATER THAN ZERO.
  *
  *  @param  axis The axis to get the positive minimum. Use the static ints
  *               provided by the ITruLogAxisAddible interface.
  *  @return floatPoint2D containing the smallest positive float value for each
  *          axis.
  */ 
  public float getPositiveMin( int axis );

 /**
  * This method will get the scale factor used, if any, to alter the size of
  * the bounds. A scale factor of 1.05 will increase the bounds by 5% while
  * a scale factor of .8 will decrease the bounds by 20%. One example of why 
  * scale factors are used is in the GraphJPanel, where the y-axis is
  * scaled so a "border" appears above/below the graph. 
  */
  public float getBoundScaleFactor( int axis );
}
