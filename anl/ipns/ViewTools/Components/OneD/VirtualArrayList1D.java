/*
 * File: VirtualArrayList1D.java
 *
 *  Copyright (C) 2004, Brent Serum, Mike Miller
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
 *  Revision 1.12  2007/07/17 15:56:31  rmikk
 *  Made the setGraphTitle method set the title, not return the previous title
 *
 *  Added a lot of error checking to this method
 *
 *  Revision 1.11  2007/06/08 14:34:45  dennis
 *  setPointedAtGraph method now also checks for index < 0, when
 *  checking for a valid index.
 *
 *  Revision 1.10  2005/06/06 20:13:52  kramer
 *
 *  Fixed indentation problems.
 *
 *  Revision 1.9  2005/06/01 22:18:58  dennis
 *  Now implements setAxisInfo( axis, info ).
 *
 *  Revision 1.8  2005/01/10 16:16:50  dennis
 *  Removed empty statement(s).
 *
 *  Revision 1.7  2004/09/15 21:55:45  millermi
 *  - Updated LINEAR, TRU_LOG, and PSEUDO_LOG setting for AxisInfo class.
 *    Adding a second log required the boolean parameter to be changed
 *    to an int. These changes may affect any ObjectState saved configurations
 *    made prior to this version.
 *
 *  Revision 1.6  2004/07/06 19:43:14  serumb
 *  Added a check for a valid index in the get errors method.
 *
 *  Revision 1.5  2004/06/10 23:25:10  serumb
 *  Added method to get selected indexes.
 *
 *  Revision 1.4  2004/05/11 01:01:50  millermi
 *  - Removed unused variables.
 *
 *  Revision 1.3  2004/04/21 02:32:26  millermi
 *  - Added index validation to getXvalues() and getYvalues().
 *
 *  Revision 1.2  2004/04/20 05:03:11  millermi
 *  - Added init() method to group functionality of constructors.
 *  - Added constructor that takes in one DataArray1D object.
 *
 *  Revision 1.1  2004/04/16 20:23:02  millermi
 *  - Initial Version - Implementation of IVirtualArrayList1D. This
 *    class is used by the FunctionViewComponent.
 *
 */
 
package gov.anl.ipns.ViewTools.Components.OneD;

import java.util.Vector;

import gov.anl.ipns.ViewTools.Components.IVirtualArrayList1D;
import gov.anl.ipns.ViewTools.Components.AxisInfo;
/**
 * 
 */

public class VirtualArrayList1D implements IVirtualArrayList1D
{
  private Vector graphs = new Vector();
  private AxisInfo xinfo;
  private AxisInfo yinfo;
  private int pointed_at_index = 0;
  private String vtitle;
  
 /**
  * Constructor - for one graph
  *
  *  @param  graph_data A DataArray1D object containing graph information.
  */ 
  public VirtualArrayList1D( DataArray1D graph_data )
  {
    Vector temp = new Vector();
    temp.add(graph_data);
    init(temp);
  }
  
 /**
  * Constructor - for multiple graphs
  *
  *  @param  graph_data A Vector of DataArray1D objects.
  */ 
  public VirtualArrayList1D( Vector graph_data )
  {
    init(graph_data);
  }
  
 /*
  * This method is used by the contructors to initialize an instance of this
  * class.
  */
  private void init( Vector graph_data )
  {
    xinfo = new AxisInfo();
    yinfo = new AxisInfo();
    // check for valid graph_data
    if( graph_data != null && graph_data.size() > 0 )
    {
      // true only if pointed-at exists
      boolean pointed_at_specified = false;
      // used to find the min and max of the selected graphs
      float xmin = 0;
      float xmax = 1;
      float ymin = 0;
      float ymax = 1;
      DataArray1D temp_array;
      for( int i = 0; i < graph_data.size(); i++ )
      {
        // Remove any element that is not a DataArray1D
        if( !(graph_data.elementAt(i) instanceof DataArray1D) )
          graph_data.remove(i);
        else
        {
          temp_array = (DataArray1D)graph_data.elementAt(i);
          // set the pointed-at index if none have been set
          if( temp_array.isPointedAt() )
          {
             if( !pointed_at_specified )
             {
                pointed_at_index = i;
                pointed_at_specified = true;
             }
             else
                temp_array.setPointedAt(false);
          }
          // find x and y range
          if( temp_array.isSelected() )
          {
             xmin = Float.POSITIVE_INFINITY;
             xmax = Float.NEGATIVE_INFINITY;
             ymin = Float.POSITIVE_INFINITY;
             ymax = Float.NEGATIVE_INFINITY;
             float[] xtemp = temp_array.getXArray();
             float[] ytemp = temp_array.getYArray();
             for( int index = 0; index < ytemp.length; index++ )
             {
                // find x range
                if( xtemp[index] > xmax )
                   xmax = xtemp[index];
                if( xtemp[index] < xmin )
                   xmin = xtemp[index];
                // find y range
                if( ytemp[index] > ymax )
                   ymax = ytemp[index];
                if( ytemp[index] < ymin )
                   ymin = ytemp[index];
             }
             // check for histogram case (x length = y length + 1)
             if( xtemp.length > ytemp.length )
             {
                // find x range
                if( xtemp[xtemp.length - 1] > xmax )
                   xmax = xtemp[xtemp.length - 1];
                if( xtemp[xtemp.length - 1] < xmin )
                   xmin = xtemp[xtemp.length - 1];
             } // end if
          } // end if
        } // end else
      } // end for
      graphs = graph_data;
      xinfo = new AxisInfo( xmin, xmax, AxisInfo.NO_LABEL, AxisInfo.NO_UNITS,
                            AxisInfo.LINEAR );
      yinfo = new AxisInfo( ymin, ymax, AxisInfo.NO_LABEL, AxisInfo.NO_UNITS,
                            AxisInfo.LINEAR );
    }
    vtitle = "";
  }
  
 /**
  * Set the title of the virtual array.
  *
  *  @param  title New title of the virtual array.
  */
  public void setTitle( String title )
  {
    vtitle = title;
  }
  
 /**
  * Get the title of the virtual array.
  *
  *  @return The title of the virtual array.
  */
  public String getTitle()
  {
    return vtitle;
  }
  
 /**
  * Gets the x values for a line, given the index of the line.
  *
  *  @param  graph_number Index of the graph.
  *  @return The x values for the graph specified.
  */
  public float[] getXValues( int graph_number )
  {
    // check for valid index
    if( graph_number >= 0 && graph_number < graphs.size() )
      return ((DataArray1D)graphs.elementAt(graph_number)).getXArray();
    return null;
  }
  
  
 /**
  * Get the y values of a line, gived the index of the line.
  *
  *  @param  graph_number Index of the graph.
  *  @return The y values for the graph specified.
  */
  public float[] getYValues( int graph_number )
  {
    // check for valid index
    if( graph_number >= 0 && graph_number < graphs.size() )
      return ((DataArray1D)graphs.elementAt(graph_number)).getYArray();
    return null;
  }

 /**
  * Get vertical error values for a line in the graph..
  * The "line_number" is the index for the selected lines.
  * The "index" is the index for the data set.
  *
  *  @param  graph_number Index of the graph.
  *  @return Error values for graph specified.
  */
  public float[] getErrorValues( int graph_number )
  { 
    // check for valid index
    if( graph_number >= 0 && graph_number < graphs.size() )
      return ((DataArray1D)graphs.elementAt(graph_number)).getErrorArray();
    return null;
  }
  
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
                           int graph_num )
  {
    // create new entry with new arrays but old selected/pointed-at info
    DataArray1D temp = ((DataArray1D)graphs.elementAt(graph_num));
    // add new entry
    graphs.insertElementAt( new DataArray1D( x_values, y_values, errors,
                                             graph_title, temp.isSelected(),
                                             temp.isPointedAt() ),
                                            graph_num + 1 );
    // remove old entry
    graphs.remove(graph_num);
  }
 
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
  *  @param  scale Is axis LINEAR, TRU_LOG, PSEUDO_LOG?
  */
  public void setAxisInfo( int axis, float min, float max,
                           String label, String units, int scale )
  {
    if(axis == AxisInfo.X_AXIS)
      xinfo = new AxisInfo(min,max,label,units, scale);
    else if(axis == AxisInfo.Y_AXIS)
      yinfo = new AxisInfo(min,max,label,units, scale);
  }

 /**
  * Sets the attributes of the data array within a AxisInfo wrapper.
  * This method will take in an integer to determine which axis
  * info is being altered.
  * 
  *  @param  axis Must be AxisInfo.X_AXIS (0), AxisInfo.Y_AXIS (1).
  *  @param  info The axis info object associated with the axis specified.
  */
  public void setAxisInfo( int axis, AxisInfo info )
  {
    if(axis == AxisInfo.X_AXIS)
      xinfo = info.copy();
    else if(axis == AxisInfo.Y_AXIS)
      yinfo = info.copy();
  }

 
 /**
  * Gets the attributes of the data array within an AxisInfo wrapper.
  * This method will take in an integer to determine which axis
  * info is desired.
  *
  *  @param  axis Use AxisInfo.X_AXIS (0) or AxisInfo.Y_AXIS (1).
  *  @return AxisInfo for the specified axis.
  */
  public AxisInfo getAxisInfo( int axis )
  {
    if(axis == AxisInfo.X_AXIS)
      return xinfo;
    else if(axis == AxisInfo.Y_AXIS)
      return yinfo;
    // if neither x nor y axis.
    return null;
  }
  
 /**
  * Set the title for the specified graph.
  *
  *  @param  title Title for graph specified.
  *  @param  graph_num Index of the graph.
  *  
  *  @return the previous title or null if unable to change
  */
  public String setGraphTitle( String title, int graph_num )
  {
    if( graph_num <  0)
       return null;
    if( graphs == null )
       return null;
    if( graph_num >= graphs.size())
       return null;
    if( title == null )
       return null;
    
    String Res = ((DataArray1D)graphs.elementAt(graph_num)).getTitle();
    ((DataArray1D)graphs.elementAt(graph_num)).setTitle( title );
    
    return Res;
  }
  
 /**
  * Get the title for the specified graph.
  *
  *  @param  graph_num Index of the graph.
  *  @return Title for graph specified.
  */
  public String getGraphTitle( int graph_num )
  {
    return ((DataArray1D)graphs.elementAt(graph_num)).getTitle();
  }

 /**
  * Return the number of drawn graphs.
  *
  *  @return Number of drawn graphs
  */   
  public int getNumSelectedGraphs()
  {
    int counter = 0;
    for( int i = 0; i < graphs.size(); i++ )
    {
      if( ((DataArray1D)graphs.elementAt(i)).isSelected() )
        counter++;
    }
    return counter;
  }

 /**
  *  Returns the indexes of the selected graphs
  *
  *  @return an array of selected indexes
  */
  public int[] getSelectedIndexes()
  {
    int count = 0;
    int[] indexes = new int[getNumSelectedGraphs()];
    for (int i = 0; i < getNumGraphs(); i++)
    {
      if( isSelected(i) )
      {
        indexes[count] = i;
        count++;
      }
    }
    return indexes;   
  }  

 /**
  *  Returns the index from the data set of the pointed at graph.
  *
  *  @return The index of the pointed-at graph.
  */
  public int getPointedAtGraph()
  {
    return pointed_at_index;
  }

 /**
  *  Sets the index of the pointed at graph from the data set.
  *
  *  @param index The index of the pointed at graph.
  */

  public void setPointedAtGraph(int index)
  {
    // make sure index is valid
    if( index < 0 || index >= graphs.size() )
      return;

    ((DataArray1D)graphs.elementAt(pointed_at_index)).setPointedAt(false);
    
    ((DataArray1D)graphs.elementAt(index)).setPointedAt(true);

    pointed_at_index = index;
  }

 /**
  *  Checks if an index from the data set is selected
  *  and returns the boolean value.
  *
  *  @param  index Index of the graph.
  *  @return True if selected, false if unselected.
  */
  public boolean isSelected(int index)
  {
    return ((DataArray1D)graphs.elementAt(index)).isSelected();
  }

 /**
  *  Returns the total number of graphs.
  *
  *  @return The total number of selected and unselected graphs.
  */
  public int getNumGraphs()
  {
    return graphs.size();
  }
  
 /**
  * Get the dimension of this virtual array.
  *
  *  @return The dimension of this array, 1.
  */
  public int getDimension()
  {
    return 1;
  }
  
 /**
  * Currently a stub, has no functional value. Implemented to satisfy the
  * interface.
  *
  *  @param  value Value to be set.
  */
  public void setAllValues( float value )
  {
  }
}
