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
 *  Revision 1.1  2003/10/22 20:07:52  millermi
 *  - Initial Version - Top level interface that factors out
 *    common methods from all IVirtualArrayxD's.
 *
 */
 
 package DataSetTools.components.View;

/**
 * This interface defines all other IVirtualArrayxD interfaces. Common
 * functionality is grouped here to make polymorphism possible. 
 */
public interface IVirtualArray
{
 /**
  * "No X Label" - This String is used to specify that no label was assigned
  * to the x axis. 
  */
  public static final String NO_XLABEL = "No X Label";
  
 /**
  * "No Y Label" - This String is used to specify that no label was assigned
  * to the y axis. 
  */
  public static final String NO_YLABEL = "No Y Label";
  
 /**
  * "No Title" - This String is used to specify that no title was assigned
  * to the data. 
  */
  public static final String NO_TITLE  = "No Title";
  
 /**
  * "No X Units" - This String is used to specify that no units were assigned
  * to the x values. 
  */
  public static final String NO_XUNITS = "No X Units";
  
 /**
  * "No Y Units" - This String is used to specify that no units were assigned
  * to the y values.  
  */
  public static final String NO_YUNITS = "No Y Units"; 
     
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
}
