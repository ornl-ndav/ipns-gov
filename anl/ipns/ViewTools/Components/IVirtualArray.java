/*
 * File: IVirtualArray.java
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
 *  Revision 1.5  2005/06/01 22:20:52  dennis
 *  Moved setAxisInfo( axis, info ) to this interface, from the
 *  IVirtualArray2D interface, since all virtual arrays should
 *  implement this.
 *
 *  Revision 1.4  2004/03/17 20:26:50  dennis
 *  Fixed @see tag that was broken when view components, math and
 *  util were moved to gov package.
 *
 *  Revision 1.3  2004/03/12 00:05:00  rmikk
 *  Fixed Package Names
 *
 *  Revision 1.2  2003/12/18 22:30:55  millermi
 *  - Added getAxisInfo() method.
 *  - Moved NO_X/YUNITS and NO_X/YLABEL to AxisInfo
 *
 *  Revision 1.1  2003/10/22 20:07:52  millermi
 *  - Initial Version - Top level interface that factors out
 *    common methods from all IVirtualArrayxD's.
 *
 */
 
 package gov.anl.ipns.ViewTools.Components;

/**
 * This interface defines all other IVirtualArrayxD interfaces. Common
 * functionality is grouped here to make polymorphism possible. 
 */
public interface IVirtualArray
{  
 /**
  * "No Title" - This String is used to specify that no title was assigned
  * to the data. 
  */
  public static final String NO_TITLE  = "No Title";
     
 /**
  * This method will return the title assigned to the data. 
  *
  *  @return title assigned to the data.
  */
  public String getTitle();
  
 /**
  * This method will assign a title to the data. 
  *
  *  @param  title - title describing the data
  */
  public void setTitle( String title );
      
 /**
  * Set all values in the array to a value. This method will usually
  * serve to "initialize" or zero out the array. 
  *
  *  @param  value - single value used to set all other values in the array
  */
  public void setAllValues( float value );
 
 /**
  * Gets the dimension of the VirtualArray. For example, IVirtualArray1D = 1,
  * IVirtualArray2D = 2.
  *
  *  @return dimension of VirtualArray. This value is an primative integer
  *          not a Dimension.
  */
  public int getDimension();
  
 /**
  * Get detailed information about this axis.
  *
  *  @param  axis The integer code for the axis, starting at 0.
  *  @return The axis info for the axis specified.
  *  @see    gov.anl.ipns.ViewTools.Components.AxisInfo
  */
  public AxisInfo getAxisInfo( int axis );


 /**
  * Sets the attributes of the data array within a AxisInfo wrapper.
  * This method will take in an integer to determine which axis
  * info is being altered.
  * 
  *  @param  axis Use AxisInfo.X_AXIS (0), AxisInfo.Y_AXIS (1),
                      AxisInfo.Z_AXIS (3), AxisInfo.W_Axis (4),
                  as appropriate, depending on the dimensionality of
                  the data for the specific implementing class.
  *  @param  info The axis info object associated with the axis specified.
  */
  public void setAxisInfo( int axis, AxisInfo info );

}
