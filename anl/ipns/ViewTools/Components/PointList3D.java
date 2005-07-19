/*
 * File: PointList3D.java
 *
 * Copyright (C) 2005, Chad Jones
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
 * Primary   Chad Jones <cjones@cs.utk.edu>
 * Contact:  Student Developer, University of Tennessee
 *
 * Contact : Dennis Mikkelson <mikkelsond@uwstout.edu>
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751, USA
 *
 * This work was supported by the National Science Foundation under grant
 * number DMR-0218882.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 *  Modified:
 *
 *  $Log$
 *  Revision 1.1  2005/07/19 15:48:09  cjones
 *  Added 3D Array implementations.
 * 
 */

package gov.anl.ipns.ViewTools.Components; 

import gov.anl.ipns.MathTools.Geometry.Vector3D;
import java.lang.Float;

/**
 * PointList3D stores scattered three 
 * dimensional points.  The size of a Array must be given by the constructor,
 * but the values may be set later. For each point in the
 * array, there is an associated integer index.  The range of the index runs
 * from zero to the total number of points the array can hold.
 *
 * If no positions are given at construction, the points are intitalized as
 * Vector3D objects with Float.NaN for the x, y, and z fields.  
 */
abstract public class PointList3D implements IPointList3D
{  
 // Data
 private Vector3D[] data_points;       // 3d points in scattered data
 private int        numpoints;         // total number of points
 
 // Data info
 private AxisInfo   x_info;
 private AxisInfo   y_info;
 private AxisInfo   z_info;
 private String     title;


 /**
  * Constructor that intializes array for points. 
  * The number of points is set to size. The AxisInfo and title
  * settings are intitalized.
  *
  *  @param size   Number of points list will hold.
  *  @param points For arrays that are equal to or larger than 'size', it will
  *                only set the first 'size' number of positions. For array
  *                smaller than 'size', it will set the first equal number of
  *                positions.
  */
  public PointList3D( int size, Vector3D[] points ) 
  {
    data_points = new Vector3D[size];
    numpoints = size;
    
    for(int i = 0; i < numpoints; i ++) {
      if(i < points.length)
        data_points[i] = new Vector3D(points[i]);
      else
        data_points[i] = new Vector3D(Float.NaN, Float.NaN, Float.NaN);
    }
    
    x_info = new AxisInfo(0, 1, AxisInfo.NO_LABEL,
                           AxisInfo.NO_UNITS, AxisInfo.LINEAR);
    y_info = new AxisInfo(0, 1, AxisInfo.NO_LABEL,
                           AxisInfo.NO_UNITS, AxisInfo.LINEAR);
    z_info = new AxisInfo(0, 1, AxisInfo.NO_LABEL,
                           AxisInfo.NO_UNITS, AxisInfo.LINEAR);                           
                            
    title = NO_TITLE;
  }

 /**
  * Constructor that intializes array for points. 
  * The number of points is set to size. The AxisInfo and title
  * settings are intitalized.
  *
  * Each point is set to (Float.NaN, Float.NaN, Float.NaN)
  *
  *  @param size Number of points list will hold.
  */
  public PointList3D( int size ) 
  {
    data_points = new Vector3D[size];
    numpoints = size;
    
    for(int i = 0; i < numpoints; i ++)
      data_points[i] = new Vector3D(Float.NaN, Float.NaN, Float.NaN);
    
    x_info = new AxisInfo(0, 1, AxisInfo.NO_LABEL,
                           AxisInfo.NO_UNITS, AxisInfo.LINEAR);
    y_info = new AxisInfo(0, 1, AxisInfo.NO_LABEL,
                           AxisInfo.NO_UNITS, AxisInfo.LINEAR);
    z_info = new AxisInfo(0, 1, AxisInfo.NO_LABEL,
                           AxisInfo.NO_UNITS, AxisInfo.LINEAR);                           
                            
    title = NO_TITLE;
  }

 /**
  * Returns the number of points in the sparse array.
  *
  * @return The number of points in the array. 
  */
  public int getNumPoints( )  
  {
    return numpoints;
  }
  
 /**
  * Sets 3d point in array at specified index of PointsList.
  *
  *  @param  index   The index of point in array.
  *  @param  point   The 3d coordinates of the point.
  *  @throws IndexOutOfBoundsException If index is outside of valid
  *          range of indices: 0...getNumPoints().
  */
  public void set(int index, Vector3D point )
  {
    if(index < 0 || index >= numpoints)
      throw new IndexOutOfBoundsException("Unable to set.");
    
    data_points[index].set(point);
  }
  
 /**
  * Sets 3d points for each index in range, including the start and end
  * indices. The first value in the points array is the starting value
  * to be set at index_start. Points should have at least a point for each
  * index in given range. Any points beyond this size are ignored.
  *
  *  @param  index_start  First index of the range.
  *  @param  index_end    Last index of the range.
  *  @param  points   The 3d coordinates.
  *  @throws IndexOutOfBoundsException If index is outside of valid
  *          range of indices: 0...getNumPoints().
  *  @throws IllegalArgumentException If given points array is 
  *          smaller than specified range.
  */
  public void set(int index_start, int index_end, Vector3D[] points )
  {
    if(index_start < 0 || index_start >= numpoints)
      throw new IndexOutOfBoundsException("Start index out of bounds.");
      
    if(index_end < 0 || index_end >= numpoints)
      throw new IndexOutOfBoundsException("End index out of bounds.");
      
    if(points.length < (index_end-index_start+1))
      throw new IllegalArgumentException("Array of points not large enough.");
      
    for(int i = index_start; i <= index_end; i++) {
      data_points[i].set(points[i-index_start]);
    }
  }
  
 /**
  * Get coordinates for a single array element based on index.
  *
  *  @param  index   Index of the element.
  *  @return If element is found, a copy of coordinates for that
  *          index, (x, y, z). If element is not found, null is returned.
  *  @throws IndexOutOfBoundsException If index is outside of valid
  *          range of indices: 0...getNumPoints().
  */ 
  public Vector3D getPoint( int index )
  {
    if(index < 0 || index >= numpoints)
      throw new IndexOutOfBoundsException("Unable to set.");
      
    return new Vector3D(data_points[index]);
  }
  
    
 /**
  * Returns the 3d coordinates of specified range of indices.
  *
  *  @param  index_start  First index of the range.
  *  @param  index_end    Last index of the range.
  *  @return A copy of the Points within the specified
  *          range of indices.
  *  @throws IndexOutOfBoundsException If index range goes outside 
  *          of valid range of indices: 0...getNumPoints().
  */ 
  public Vector3D[] getPoints( int index_start, int index_end )
  {
    Vector3D[] pts;
    
    if(index_start < 0 || index_start >= numpoints)
      throw new IndexOutOfBoundsException("Start index out of bounds.");
      
    if(index_end < 0 || index_end >= numpoints)
      throw new IndexOutOfBoundsException("End index out of bounds.");
      
    if(index_end < index_start) 
      return null;
    else
      pts = new Vector3D[index_end - index_start + 1];
      
    for(int i = index_start; i <= index_end; i++) 
    {
      pts[i-index_start] = new Vector3D(data_points[i]);
    }
    
    return pts;
  }
  
  
 /**
  * Returns all 3d coordinates in the array.
  *
  *  @return A copy of all the Points.
  */ 
  public Vector3D[] getPoints( )
  {
    Vector3D[] c_pts = new Vector3D[numpoints];
    
    for(int i = 0; i < numpoints; i++) 
    {
      c_pts[i] = new Vector3D(data_points[i]);
    }
    
    return c_pts;
  }
  
  
  /* * */
  /* IVirtualArray
  /* * */
  
 /**
  * Convenience method for getting the dimension.
  *
  *  @return 3 The array dimension.
  */
  public int getDimension()
  {
    return 3;
  }
  
 /**
  * Set all values in the array to a value. This method will usually
  * serve to "initialize" or zero out the array. 
  *
  *  @param  value - single value used to set all other values in the array
  */
  abstract public void setAllValues( float value );
  
  /* 
   * The following methods allow the user to attach meaningful discription
   * to the values stored in the virtual array.
   *
   * An object AxisInfo contains information about a particular axis.
   */
 
 /**
  * Returns the attributes of the data array in a AxisInfo wrapper.
  * This method will take in a boolean value to determine for which axis
  * info is being retrieved for. 
  *
  *  @param  axiscode The integer code for this axis.
  *  @return If true, AxisInfo object with X axis info is returned.
  *          If false, AxisInfo object with Y axis info is returned.
  *  @see   gov.anl.ipns.ViewTools.Components.AxisInfo
  */
  public AxisInfo getAxisInfo( int axiscode )
  {
    if( axiscode == AxisInfo.X_AXIS )
      return x_info;
    if( axiscode == AxisInfo.Y_AXIS )
      return y_info;
    if( axiscode == AxisInfo.Z_AXIS )
      return z_info;
    return null;
  }
  
 /**
  * Sets the attributes of the data array within a AxisInfo wrapper.
  * This method will take in an integer to determine which axis
  * info is being altered.
  *
  *  @param  axiscode
  *  @param  info - axis info
  */
  public void setAxisInfo( int axiscode, AxisInfo info )
  {
    if( axiscode == AxisInfo.X_AXIS )
      x_info = info.copy();
    else if(axiscode == AxisInfo.Y_AXIS)
      y_info = info.copy();
    else if(axiscode == AxisInfo.Z_AXIS)
      z_info = info.copy();
  } 
  
 /**
  * This method will return the title assigned to the data. 
  *
  *  @return Title assigned to the data.
  */
  public String getTitle()
  {
    return title;
  }
  
 /**
  * This method will assign a title to the data. 
  *
  *  @param  ptitle
  */
  public void setTitle( String ptitle ) 
  {
    title = ptitle;
  }

}