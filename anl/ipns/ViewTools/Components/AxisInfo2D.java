/*
 * File: AxisInfo2D.java
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

package DataSetTools.components.View;

/**
 * This class groups all of the information about an axis into one object.
 * The class contains the "world coordinates" minimum and maximum, the
 * label, and units. This simple class has no set methods, 
 * all info must be set by the constructor.
 *
 *  @see DataSetTools.components.View.IVirtualArray2D
 */
public class AxisInfo2D
{  
 /**
  * true - this boolean variable defines the use of the x axis.
  */ 
  public static final boolean XAXIS = true;
   
 /**
  * false - this boolean variable defines the use of the y axis.
  */ 
  public static final boolean YAXIS = false;
   
  private float axismin;
  private float axismax; 
  private String axislabel;
  private String axisunits;  
  private boolean islinear;
  
 /**
  * Constructor: initializes axis information
  *
  *  @param  min
  *  @param  max
  *  @param  label
  *  @param  units
  *  @param  bool_lin
  */
  public AxisInfo2D( float min, float max, String label, 
		     String units, boolean bool_lin )
  {	 
    axismin = min;
    axismax = max;
    axislabel = label;
    axisunits = units;
    islinear = bool_lin;
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
  * Returns boolean answer to question "Is Linear?". This value
  * will be true if the calibration for the axis is linear. 
  *
  *  @return true if linear, false if logarithmic
  */
  public boolean getIsLinear()
  {
    return islinear;
  } 
  
 /**
  * This method returns a copy of the AxisInfo2D object.
  *
  *  @return copy of the AxisInfo2D object.
  */
  public AxisInfo2D copy()
  {
    return new AxisInfo2D( axismin, axismax, new String(axislabel), 
			   new String(axisunits), islinear );
  }
}
