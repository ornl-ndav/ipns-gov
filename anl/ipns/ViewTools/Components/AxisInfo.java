/*
 * File: AxisInfo.java
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
 *  Revision 1.9  2005/06/02 13:51:08  dennis
 *  Removed construction of new String object in copy() method.
 *  Since Strings are immutable, they can be shared, so it is not
 *  necessary to construct a new String.
 *
 *  Revision 1.8  2004/09/15 21:55:45  millermi
 *  - Updated LINEAR, TRU_LOG, and PSEUDO_LOG setting for AxisInfo class.
 *    Adding a second log required the boolean parameter to be changed
 *    to an int. These changes may affect any ObjectState saved configurations
 *    made prior to this version.
 *
 *  Revision 1.7  2004/06/11 17:54:01  dennis
 *  Changed from dos to unix text format.
 *
 *  Revision 1.6  2004/04/09 03:06:56  millermi
 *  - Added default constructor.
 *
 *  Revision 1.5  2004/03/17 20:26:50  dennis
 *  Fixed @see tag that was broken when view components, math and
 *  util were moved to gov package.
 *
 *  Revision 1.4  2004/03/12 00:05:22  rmikk
 *  Fixed Package Names
 *
 *  Revision 1.3  2004/01/29 23:50:25  millermi
 *  - Added toString() (Dennis)
 *  - Removed all references to AxisInfo2D
 *
 *  Revision 1.2  2003/12/20 19:15:59  millermi
 *  - Corrected java docs statements.
 *
 *  Revision 1.1  2003/12/18 22:29:58  millermi
 *  - new general version of AxisInfo2D
 *  - Added static variables for LINEAR/LOG, X/Y/Z/W_AXIS,
 *    and NO_LABEL/UNITS
 *
 *
 ************************Revised AxisInfo2D*****************************
 *  Revision 1.4  2003/10/22 20:06:23  millermi
 *  - Added java docs.
 *
 *  Revision 1.3  2003/08/07 15:57:43  dennis
 *  - Added copy() method to make a copy of an AxisInfo2D object.
 *    (Mike Miller)
 *
 *  Revision 1.2  2003/05/16 15:02:41  dennis
 *  Added acknowledgement of NSF funding.
 *
 */

package gov.anl.ipns.ViewTools.Components;

import java.io.Serializable;

/**
 * This class groups all of the information about an axis into one object.
 * The class contains the "world coordinates" minimum and maximum, the
 * label, and units. This simple class has no set methods, 
 * all info must be set by the constructor.
 *
 *  @see gov.anl.ipns.ViewTools.Components.IVirtualArray
 */
public class AxisInfo  implements Serializable
{  
 /**
  * 0 - this int variable defines the use of the x axis.
  */ 
  public static final int X_AXIS = 0;
    
 /**
  * 1 - this int variable defines the use of the y axis.
  */ 
  public static final int Y_AXIS = 1;
    
 /**
  * 2 - this int variable defines the use of the z axis.
  */ 
  public static final int Z_AXIS = 2;
    
 /**
  * 3 - this int variable defines the use of the w axis.
  */ 
  public static final int W_AXIS = 3;
 
 /**
  * "Default Label" - This String is used to specify that no label was
  * assigned to the axis. 
  */
  public static final String NO_LABEL = "Default Label";  
  
 /**
  * "Default Units" - This String is used to specify that no units were
  * assigned to the values. 
  */
  public static final String NO_UNITS = "Default Units";
  
 /**
  * 0 - This constant int selects linear display mode
  * for the axis specified.
  */
  public static final int LINEAR = 0;
  
 /**
  * 1 - This constant in selects tru-logarythmic display mode
  * for the axis specified. Tru-log scaling follows the rules of traditional
  * logarythmic scaling.
  */
  public static final int TRU_LOG    = 1;
  
 /**
  * 2 - This constant in selects pseudo-logarithmic display mode
  * for the axis specified. Pseudo-log maps a linear region to a logarythmic
  * scale where the lowest linear value is mapped to one on the log scale.
  */
  public static final int PSEUDO_LOG    = 2;
   
  private float axismin;
  private float axismax; 
  private String axislabel;
  private String axisunits;  
  private int scale;
  
 /**
  * Default Constructor: initializes axis information
  */
  public AxisInfo()
  {	 
    axismin = 0;
    axismax = 1f;
    axislabel = NO_LABEL;
    axisunits = NO_UNITS;
    scale = LINEAR;
  }
  
 /**
  * Constructor: initializes axis information
  *
  *  @param  min
  *  @param  max
  *  @param  label
  *  @param  units
  *  @param  scale
  */
  public AxisInfo( float min, float max, String label, 
		   String units, int scale )
  {	 
    axismin = min;
    axismax = max;
    axislabel = label;
    axisunits = units;
    this.scale = scale;
  }
  
 /**
  * Returns the minimum value of the axis.
  *
  *  @return the minumum value of the axis
  */ 
  public float getMin()
  {
    return axismin;
  }
 
 /**
  * Returns the maximum value of the axis.
  *
  *  @return the maximum value of the axis
  */ 
  public float getMax()
  {
    return axismax;
  }
    
 /**
  * Returns the label of the axis.
  *
  *  @return the label of the axis
  */
  public String getLabel()
  {
    return axislabel;
  }
 
 /**
  * Returns the units of the axis.
  *
  *  @return the units of the axis
  */ 
  public String getUnits()
  {
    return axisunits;
  }
  
 /**
  * Returns scale type of the axis. This value will be either LINEAR,
  * TRU_LOG, or PSEUDO_LOG.
  *
  *  @return Scale type, use static ints to determine type.
  */
  public int getScale()
  {
    return scale;
  } 
  
 /**
  * This method returns a copy of the AxisInfo object.
  *
  *  @return copy of the AxisInfo object.
  */
  public AxisInfo copy()
  {
    return new AxisInfo( axismin, axismax, axislabel, axisunits, scale );
  }

 /**
  *  Get a String containg the basic axis information.
  *
  *  @return a String form of the axis info.
  */
  public String toString()
  {
     return "Label  = " + axislabel + "\n" +
            "Units  = " + axisunits + "\n" +
            "Min    = " + axismin   + "\n" +
            "Max    = " + axismax   + "\n" +
            "Scale  = " + scale;
  }
  
}
