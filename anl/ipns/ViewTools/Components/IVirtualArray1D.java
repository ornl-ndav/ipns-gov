/*
 * File: IVirtualArray1D.java
 *
 * 
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
 *  Revision 1.5  2003/12/18 22:42:12  millermi
 *  - This file was involved in generalizing AxisInfo2D to
 *    AxisInfo. This change was made so that the AxisInfo
 *    class can be used for more than just 2D axes.
 *
 *
 */
 
package DataSetTools.components.View;

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

public interface IVirtualArray1D
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
   * This method will return the title assigned to the data. 
   */
   public String getTitle();
   
  /**
   * This method will assign a title to the data. 
   */
   public void setTitle( String title );
  
  /*
   ***************************************************************************
   * The following methods must include implementation to prevent
   * the user from exceeding the initial array size determined
   * at creation of the array. If an M x N array is specified,
   * the parameters must not exceed (M-1,N-1). 
   ***************************************************************************
   */
   
  /**
   * Get values for a portion or all of a row.
   * The "from" and "to" values must be direct array reference, i.e.
   * because the array positions start at zero, not one, this must be
   * accounted for. If the array passed in exceeds the bounds of the array, 
   * get values for array elements and ignore extra values.
   */
   public float[] getXValues( int line_number );
   
   
  /**
   * Get values for a portion or all of a column.
   * The "from" and "to" values must be direct array reference, i.e.
   * because the array positions start at zero, not one, this must be
   * accounted for. If the array passed in exceeds the bounds of the array, 
   * get values for array elements and ignore extra values.
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
   * Get vertical error values for a line in the graph..
   */
   public float[] getErrorValues( int line_number );
   public float[] getErrorVals_ofIndex( int index );
  /**
   * Get the Group ID number for the line label.
   */
   public int getGroupID( int line_number );

   //**/get axisinfo
   public AxisInfo  getAxisInfo( int axiscode );
     
  /**
   * Set all values in the array to a value. This method will usually
   * serve to "initialize" or zero out the array. 
   */
   public void setAllValues( float value );

   public int getNumPoints( int line_number );

  /**
   * Returns number of lines in the array.
   */   
   public int getNumlines();

   public int getPointedAtGraph();
   public int[] getSelectedGraphs();
   public boolean isSelected(int index);
   public int getNumGraphs();

   public float[] getXVals_ofIndex(int index);
   public float[] getYVals_ofIndex(int index);
   
   public void addActionListener( ActionListener listener);
   public void removeActionListener( ActionListener listener);
   public void removeAllActionListeners();
   
}
