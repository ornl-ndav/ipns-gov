/*
 * File: IVirtualArrayList1D.java
 *
 *  Copyright (C) 2003, Brent Serum
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
 * 
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
 *  Revision 1.1  2004/03/12 22:54:39  serumb
 *  Added file to replace IVirtualArray1D.
 *
 *  Revision 1.8  2004/03/12 21:06:56  serumb
 *  Class now extends IVirtualArray, added setAxisInfo method.
 *
 *  Revision 1.7  2004/03/12 00:06:23  rmikk
 *  Fixed Package Names
 *
 *  Revision 1.6  2004/01/06 23:30:44  serumb
 *  Added documentation.
 *
 *  Revision 1.5  2003/12/18 22:42:12  millermi
 *  - This file was involved in generalizing AxisInfo2D to
 *    AxisInfo. This change was made so that the AxisInfo
 *    class can be used for more than just 2D axes.
 *
 *
 */
 
package gov.anl.ipns.ViewTools.Components;

//import DataSetTools.components.View.OneD.*;
 import javax.swing.*;
 import java.awt.event.*;
/**
 * This interface is implemented by classes that can produce a virtual function 
 * list containing a 3Darray of floats and is used to pass data to viewers and 
 * view components.Along with the data, some data attributes are kept in the 
 * virtual FunctionList. 
 * the x dimension is either 0 or 1. 0 for the x values 1 for the y values
 * the y dimension contains a number of x and y values 
 * the z dimension is represents the line numbers.

 */

public interface IVirtualArrayList1D extends IVirtualArray
{
   public static final String NO_XLABEL = "No X Label";
   public static final String NO_YLABEL = "No Y Label";
   public static final String NO_TITLE  = "No Title";
   public static final String NO_XUNITS = "No X Units";
   public static final String NO_YUNITS = "No X Units";
  /**
   * Returns the attributes of the data array in a AxisInfo2D wrapper.
   * This method will take in a boolean value to determine for which axis
   * info is being retrieved for.    true = X axis, false = Y axis.
   */
  
   
  /**
   * Gets the x values for a line, given the index of the line. 
   */
   public float[] getXValues( int line_number );
   
   
  /**
   * Get the y values of a line, gived the index of the line.
   */
   public float[] getYValues( int line_number );
   
  /**
   * Set values for one tabulated function together with it's error
   * estimates.
   */
   public void setXYValues( float[] x_values, 
                            float[] y_values, 
                            float[] errors,
                            int group_id,
                            int line_num);
  
 /**
  * Sets the attributes of the data array within a AxisInfo wrapper.
  * This method will take in an integer to determine which axis
  * info is being altered.
  *
  *  @param  axis Use AxisInfo.X_AXIS (0) or AxisInfo.Y_AXIS (1).
  *  @param  min Minimum value for this axis.
  *  @param  max Maximum value for this axis.
  *  @param  label Label associated with the axis.
  *  @param  units Units associated with the values for this axis.
  *  @param  islinear Is axis linear (true) or logarithmic (false)
  */
  public void setAxisInfo( int axis, float min, float max,
                           String label, String units, boolean islinear );
 
  /**
   * Get vertical error values for a line in the graph..
   * The "line_number" is the index for the selected lines.
   * The "index" is the index for the data set. 
   */
   public float[] getErrorValues( int line_number );
   public float[] getErrorVals_ofIndex( int index );
  /**
   * Get the Group ID number for the line label.
   */
   public int getGroupID( int line_number );

  /**
   *  Returns the number of points in the line.
   */ 
   public int getNumPoints( int line_number );

  /**
   * Returns number of lines in the array.
   */   
   public int getNumlines();

  /**
   *  Returns the index from the data set of the pointed at graph.
   */
   public int getPointedAtGraph();

  /**
   *  Returns the array of selected indexes.
   */ 
   public int[] getSelectedGraphs();

  /**
   *  Checks if an index from the data set is selected
   *  and returns the boolean value.
   */
   public boolean isSelected(int index);

  /**
   *  Returns the number of graphs in the data set
   */
   public int getNumGraphs();

  /**
   *  Gets the x values for an index for a data set.
   */
   public float[] getXVals_ofIndex(int index);
 
  /**
   *  Gets the y values for an index for a data set.
   */
   public float[] getYVals_ofIndex(int index);
   
  /**
   *  Methods for adding and removing action listeners.
   */
   public void addActionListener( ActionListener listener);
   public void removeActionListener( ActionListener listener);
   public void removeAllActionListeners();
   
}
