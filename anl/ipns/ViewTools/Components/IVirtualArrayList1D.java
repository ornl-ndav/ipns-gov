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
 *  Revision 1.3  2004/04/16 20:20:06  millermi
 *  - Removed methods that were specific to DataSets.
 *
 *  Revision 1.2  2004/03/15 23:53:50  dennis
 *  Removed unused imports, after factoring out the View components,
 *  Math and other utils.
 *
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
 /**
  * Gets the x values for a line, given the index of the line.
  *
  *  @param  graph_number Index of the graph.
  *  @return The x values for the graph specified.
  */
  public float[] getXValues( int graph_number );
  
 /**
  * Set the title of the virtual array.
  *
  *  @param  title New title of the virtual array.
  */
  public void setTitle( String title );
  
 /**
  * Get the y values of a line, gived the index of the line.
  *
  *  @param  graph_number Index of the graph.
  *  @return The y values for the graph specified.
  */
  public float[] getYValues( int graph_number );
  
 /**
  * Set values for one tabulated function together with it's error
  * estimates.
  *
  *  @param  x_values
  *  @param  y_values
  *  @param  errors
  *  @param  graph_title
  *  @param  graph_num
  */
  public void setXYValues( float[] x_values, 
			   float[] y_values, 
			   float[] errors,
			   String graph_title,
			   int graph_num);
 
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
  *
  *  @param  graph_number Index of the graph.
  *  @return Error values for graph specified.
  */
  public float[] getErrorValues( int graph_number );
  
 /**
  * Set the title for the specified graph.
  *
  *  @param  title Title of the specified graph.
  *  @param  graph_num Index of the graph.
  */
  public String setGraphTitle( String title, int graph_num );
  
 /**
  * Get the title for the specified graph.
  *
  *  @param  graph_num Index of the graph.
  *  @return Title for graph specified.
  */
  public String getGraphTitle( int graph_num );

 /**
  * Return the number of drawn graphs.
  *
  *  @return Number of drawn graphs
  */   
  public int getNumSelectedGraphs();

 /**
  *  Returns the index from the data set of the pointed at graph.
  *
  *  @return The index of the pointed-at graph.
  */
  public int getPointedAtGraph();

 /**
  *  Checks if an index from the data set is selected
  *  and returns the boolean value.
  *
  *  @param  index Index of the graph.
  *  @return True if selected, false if unselected.
  */
  public boolean isSelected(int index);

 /**
  *  Returns the total number of graphs.
  *
  *  @return The total number of selected and unselected graphs.
  */
  public int getNumGraphs();  
}
