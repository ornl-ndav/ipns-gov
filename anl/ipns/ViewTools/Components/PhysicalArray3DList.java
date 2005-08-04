/*
 * File: PhysicalArray3DList.java
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
 * This work was supported by the University of Tennessee Knoxville and 
 * the Spallation Neutron Source at Oak Ridge National Laboratory under: 
 *   Support of HFIR/SNS Analysis Software Development 
 *   UT-Battelle contract #:   4000036212
 *   Date:   Oct. 1, 2004 - Sept. 30, 2006
 *
 *  Modified:
 *
 *  $Log$
 *  Revision 1.3  2005/08/04 22:42:33  cjones
 *  Updated comment header and javadocs
 *
 *  Revision 1.2  2005/07/19 19:23:24  cjones
 *  Added methods for setting ArrayID
 *
 *  Revision 1.1  2005/07/19 15:48:08  cjones
 *  Added 3D Array implementations.
 * 
 */

package gov.anl.ipns.ViewTools.Components; 

import gov.anl.ipns.ViewTools.Components.AxisInfo;
import gov.anl.ipns.MathTools.Geometry.Vector3D;
import DataSetTools.dataset.XScale;
import DataSetTools.dataset.VariableXScale;

/**
 * This interface extends the ISparseArray3DList and IBounds3DList interfaces
 * to add information about each point's three dimensional volume surrounding
 * the point. The extents and orientation, like the 3d points of 
 * ISparseArray3DList, do not change with frames.
 * 
 * For each scattered point in the array, IPhysicalArray3DList will hold an 
 * extension width for each direction in 3d space.  The point will be
 * the center of each extent, making it the center of a rectangular
 * prism. The intial values of the extensions will be set to zero
 * (making it a single point in space) if extent data is not given to the
 * constructor.
 * 
 * To orient the volume surrounding the point, two vectors are kept along
 * with the extension data. These vectors define the "rotated" x and y
 * axes (The third z axis will be calculated with cross product). The
 * x, y, z extensions will expand in these user defined directions. 
 * The intial values for the orientation will be set to <1,0,0> and
 * <0,1,0> (the normal x and y axes directions) if orientation data is not
 * given to the constructor. 
 * 
 * As extensions are added to the array, information on the 
 * maximum and minimum extent values and the maximum and minimum area for 
 * the entire 3D volume (point + exents) are kept. If bounds data is reset 
 * and the max and min edges change, the method recalcMaxMinEdges() 
 * should be called to linearly determine the volume edges.
 * This allows the user of the array to define a 3d volume
 * large enough to accommodate all the rectangular prisms. 
 * 
 * It is assumed that none of the volumes will overlap, ie the
 * extensions of one point will not go into the extension of 
 * another point. 
 */
public class PhysicalArray3DList implements IPhysicalArray3DList
{
  // Data
  private SparseArray3DList array;
  private BoundsList3D  bounds;
  
  // Edges
  private float[] maxedges;
  private float[] minedges;

  private int array_id = 0;
 
 /**
  * Constructor that intializes array for data points,
  * data values, and error values. The AxisInfo, titles, and errors
  * settings are intitalized.
  *
  *  @param size     The number of points.
  *  @param timevals  Holds the time values. Values should be
  *                 in increasing order.
  */
  public PhysicalArray3DList( int size, XScale timevals ) 
  {
    array = new SparseArray3DList( size, timevals );

    bounds = new BoundsList3D(size);
    
    minedges = new float[3];
    maxedges = new float[3];
    for(int i = 0; i < 3; i++) {
      minedges[i] = Float.POSITIVE_INFINITY;
      maxedges[i] = Float.NEGATIVE_INFINITY; 
    }
  }
  
 /**
  * Constructor that intializes array for data points,
  * data values, and error values. The AxisInfo, titles, and errors
  * settings are intitalized. For arrays that are equal to or larger than 
  * 'size', it will only set the first 'size' number of positions. For array
  *  smaller than 'size', it will set the first equal number of positions.
  *
  *  @param size The number of points.
  *  @param points 3D Positions
  *  @param values Data values up to values[pointsize][timesize]
  *  @param timevals  Holds the time values. Values should be
  *                 in increasing order.
  */
  public PhysicalArray3DList( int size, Vector3D[] points, float[][] values,
                          XScale timevals ) 
  {
    array = new SparseArray3DList( size, points, values, timevals );

    bounds = new BoundsList3D( size );
    
    minedges = new float[3];
    maxedges = new float[3];
    for(int i = 0; i < 3; i++) {
      minedges[i] = Float.MAX_VALUE;
      maxedges[i] = Float.MIN_VALUE; 
    }
    
    for(int i = 0; i < points.length; i++) {
      updateMaxMinEdges(i);
    }
  }
  
 /**
  * Constructor that intializes array for data points,
  * data values, and error values. The AxisInfo, titles, and errors
  * settings are intitalized. For arrays that are equal to or larger than 
  * 'size', it will only set the first 'size' number of positions. For array
  *  smaller than 'size', it will set the first equal number of positions.
  *
  *  @param size The number of points.
  *  @param points 3D Positions
  *  @param values Data values up to values[pointsize][timesize]
  *  @param extents Extent data.
  *  @param xaxes   X Axis direction data.
  *  @param yaxes   Y Axis direction data.
  *  @param timevals  Holds the time values. Values should be
  *                 in increasing order.
  */
  public PhysicalArray3DList( int size, Vector3D[] points, float[][] values,
                          Vector3D[] extents, Vector3D[] xaxes, 
                          Vector3D[] yaxes, XScale timevals ) 
  {
    array = new SparseArray3DList( size, points, values, timevals );

    bounds = new BoundsList3D(size, extents, xaxes, yaxes);
    
    minedges = new float[3];
    maxedges = new float[3];
    for(int i = 0; i < 3; i++) {
      minedges[i] = Float.MAX_VALUE;
      maxedges[i] = Float.MIN_VALUE; 
    }
    
    for(int i = 0; i < points.length; i++) {
      updateMaxMinEdges(i);
    }
  }
  
 /** 
  * Set data values over all frames with given point.
  *
  *  @param  point_index   The index of point in array.
  *  @param  point   The 3d coordinates of the point.
  *  @param  extents The extents for x, y, and z.
  *  @param  x_axis  The direction of rotated X axis.
  *  @param  y_axis  The direction of rotated Y axis.
  *  @param  values  Values over all frames for
  *                  given point.
  *  @throws IndexOutOfBoundsException If index range goes outside 
  *          of valid range of indices: 0...getNumPoints().
  */
  public void set( int point_index, Vector3D point,
                  Vector3D extents, 
                  Vector3D x_axis, 
                  Vector3D y_axis, 
                  float[] values)
  {
    array.set(point_index, point, values);
    bounds.setExtents(point_index, extents);
    bounds.setOrientation(point_index, x_axis, y_axis);
    
    updateMaxMinEdges(point_index);
  }
    
 /**
  * Add 3d point to array. The value of point is intialized
  * to be Float.Nan
  *
  *  @param point_index   The index of point in array.
  *  @param  point   The 3d coordinates of the point.
  *  @param  extents The extents for x, y, and z.
  *  @param  x_axis  The direction of rotated X axis.
  *  @param  y_axis  The direction of rotated Y axis.
  *  @throws IndexOutOfBoundsException If index range goes outside 
  *          of valid range of indices: 0...getNumPoints().
  */
  public void set( int point_index, Vector3D point,
                  Vector3D extents, 
                  Vector3D x_axis, 
                  Vector3D y_axis)
  {
    array.set(point_index, point);
    bounds.setExtents(point_index, extents);
    bounds.setOrientation(point_index, x_axis, y_axis);
    
    updateMaxMinEdges(point_index);
  }

 /**
  * Set the Array ID. This is a user-specified indentification
  * number for the entire data array.
  *
  *	@param	id Array ID number.
  */ 
  public void setArrayID( int id )
  {
    array_id = id;
  }
  
 /**
  * Get the Array ID. This is a user-specified indentification
  * number for the entire data array.
  *
  *	@return	The array ID number.
  */ 
  public int getArrayID( )
  {
    return array_id;
  }
  
 /**
  * Get minimum distance from the origin for each
  * direction x, y, and z. The distance is deterimined
  * to be the minimum of position of each coordinate plus the
  * extension in the direction for that point.  Along
  * with get getMaxEdges, the "volume" that the data
  * points and extensions will need can be determined.
  * This is calculated internally when bounds information is set. 
  *
  *  @return The minimum edges for x, y, z directions.
  */ 
  public Vector3D getMinEdges( )
  {
    return new Vector3D(minedges);
  }

 /**
  * Get maximum distance from the origin for each
  * direction x, y, and z. The distance is deterimined
  * to be the maximum of position on of each coordinate plus the
  * extension in the direction for that point. Along
  * with get getMinEdges, the "volume" that the data
  * points and extensions will need can be determined.
  * This is calculated internally when bounds information is set.
  *
  *  @return The maximum edges for x, y, z directions.
  */ 
  public Vector3D getMaxEdges( )
  {
    return new Vector3D(maxedges);
  }
  
 /**
  * This should only be called if bounds information
  * is reset, and the values for edges need to be updated. Max and Min
  * edges are not recalculated automatically if the outer edge is shrunk
  * by a set call.
  */ 
  public void recalcMaxMinEdges( )
  {
    for(int i = 0; i < 3; i++) {
      minedges[i] = Float.MAX_VALUE;
      maxedges[i] = Float.MIN_VALUE; 
    }
    
    for(int i = 0; i < array.getNumPoints(); i++)
    {
      updateMaxMinEdges(i);
    }
  }
 
/* * *
 * VirtualArray
 * * */
 
 /**
  * This method will return the title assigned to the data. 
  *
  *  @return title assigned to the data.
  */
  public String getTitle()
  {
    return array.getTitle();
  }
  
 /**
  * This method will assign a title to the data. 
  *
  *  @param  title - title describing the data
  */
  public void setTitle( String title )
  {
    array.setTitle(title);
  }
      
 /**
  * Set all values in the array to a value. This method will usually
  * serve to "initialize" or zero out the array. 
  *
  *  @param  value - single value used to set all other values in the array
  */
  public void setAllValues( float value )
  {
    array.setAllValues(value);
  }
 
 /**
  * Gets the dimension of the VirtualArray. For example, IVirtualArray1D = 1,
  * IVirtualArray2D = 2.
  *
  *  @return dimension of VirtualArray. This value is an primative integer
  *          not a Dimension.
  */
  public int getDimension()
  {
    return array.getDimension();
  }
  
 /**
  * Get detailed information about this axis.
  *
  *  @param  axis The integer code for the axis, starting at 0.
  *  @return The axis info for the axis specified.
  *  @see    gov.anl.ipns.ViewTools.Components.AxisInfo
  */
  public AxisInfo getAxisInfo( int axis )
  {
    return array.getAxisInfo(axis);
  }


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
  public void setAxisInfo( int axis, AxisInfo info )
  {
    array.setAxisInfo(axis, info);
  }

  
/* * *
 * PointList3D
 * * */

 /**
  * Returns the number of points in the sparse array.
  *
  * @return The number of points in the array. 
  */
  public int getNumPoints( )
  {
    return array.getNumPoints();
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
    array.set(index, point);
    updateMaxMinEdges(index);
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
    array.set(index_start, index_end, points);
    for(int i = index_start; i <= index_end; i++)
      updateMaxMinEdges(i);
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
    return array.getPoint(index);
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
    return array.getPoints(index_start, index_end);
  }
  
 /**
  * Returns all 3d coordinates in the array.
  *
  *  @return A copy of all the Points.
  */ 
  public Vector3D[] getPoints( )
  {
    return array.getPoints();
  }
  
/* * *
 * SparseArray3DList
 * * */
 
 /**
  * Returns the number of frames. Frames are 
  * ordered by increasing value and each frame has an 
  * associated frame index integer.  All frame indices
  * share the same 3d points but may have different
  * data values.
  *
  *  @return This returns the number of points in the array. 
  */
  public int getNumFrames( )
  {
    return array.getNumFrames();
  }
 
 /**
  * Set values at given point for all frame indices.
  * The errors over all frames are set to Float.NaN 
  *
  *  @param  point_index   The index of point in array.
  *  @param  point         The 3d coordinates of the point.
  *  @param  values        Values for point for each frame index.
  *  @throws IllegalArgumentException If given values array is 
  *          smaller than number of frames.
  */
  public void set( int point_index, Vector3D point, float[] values )
  {
    array.set(point_index, point, values);
    updateMaxMinEdges(point_index);
  }

 /**
  * Set value for a single array element at given point and
  * time.
  *
  *  @param  point_index  Point Index of the element.
  *  @param  frame_index   Time Index of the element.
  *  @param  value        Value that element will be set to.
  *  @throws IndexOutOfBoundsException If either index is
  *          outside of its respective valid range.
  */
  public void setValue( int point_index, int frame_index, 
                        float value )
  {
    array.setValue(point_index, frame_index, value);
  }
  
 /**
  * Sets values for every frame at given point.
  *
  *  @param  point_index  Index to point that value be set for.
  *  @param  values       Array of float values
  *  @throws IndexOutOfBoundsException If index range goes outside 
  *          of valid range of indices: 0...getNumPoints().
  *  @throws IllegalArgumentException If given values array is 
  *          smaller than specified range.
  */ 
  public void setValues( int point_index, float[] values )
  {
    array.setValues(point_index, values);
  }
  
 /**
  * Get value for a single array element based on point and frame
  * indices.
  *
  *  @param  point_index   Index of point for element.
  *  @param  frame_index   Index of frame for element.
  *  @return If element is found, the float value for that element
  *          is returned. If element is not found, Float.NaN is returned.
  *  @throws IndexOutOfBoundsException If index is outside of valid
  *          range of indices: 0...getNumPoints().
  */ 
  public float getValue( int point_index, int frame_index )
  {
    return array.getValue(point_index, frame_index);
  }
  
 /**
  * Returns all data values for given frame index. 
  *
  *  @param  frame_index  Index to frame for data values.
  *  @return A copy of the portion of the array that was
  *          specified.
  *  @throws IndexOutOfBoundsException If index range goes outside 
  *          of valid range of indices: 0...getNumFrames().
  */ 
  public float[] getValuesAtFrame( int frame_index )
  {
    return array.getValuesAtFrame(frame_index);
  }
  
 /**
  * Returns all data values for given point index. 
  *
  *  @param  point_index  Index to point for data values.
  *  @return A copy of the portion of the array that was
  *          specified.
  *  @throws IndexOutOfBoundsException If index range goes outside 
  *          of valid range of indices: 0...getNumPoints().
  */ 
  public float[] getValuesAtPoint( int point_index ) 
  {
    return array.getValuesAtPoint(point_index);
  }
  
 /**
  * Returns the frame value for given frame index.
  *
  *  @param frame_index Frame index.
  *  @return Frame value.
  */
  public float getFrame( int frame_index )
  {
    return array.getFrame(frame_index);
  }
  
 /**
  * Returns all frame values for array.
  *
  * @return Frame values. 
  */
  public float[] getFrames( )
  {
    return array.getFrames();
  }
   
  
  /* ** */
  /* ERROR METHODS */
  /* ** */
 
 /**
  * Set the error values that correspond to the data. The size of error array
  * should match the size of the data array. Float.NaN will be left for
  * undersized error arrays. Values that are in an array
  * that exceeds the data array will be ignored.
  *
  *  @param  error_values The array of error values corresponding to the data.
  *  @return True if error array succesfully matched size of data array.
  */
  public boolean setErrors( float[][] error_values )
  {
    return array.setErrors(error_values);
  }
  
 /**
  * Get the error values corresponding to the data. If no error values have
  * been set, the square-root of the data value will be returned.
  *
  *  @return Copy of error values for the data.
  */
  public float[][] getErrors()
  {
    return array.getErrors();
  }
   
 /**
  * Use this method to specify whether to use error values that were passed
  * into the setErrors() method or to use the square-root of the data value.
  *
  *  @param  use_sqrt If true, use square-root.
  *                   If false, use set error values if they exist.
  */
  public void setSquareRootErrors( boolean use_sqrt )
  {
    array.setSquareRootErrors(use_sqrt);
  }
 
 /**
  * Get an error value for given index. Returns Float.NaN if
  * index is  invalid.
  *
  *  @param  point_index Point index corresponding to data.
  *  @param  frame_index  Frame index corresponding to data.
  *  @return Error value for data at specified index. If invalid,
  *          Float.NaN is returned.
  *  @throws IndexOutOfBoundsException If index is outside 
  *          of valid range of indices: 0...getNumPoints().
  */
  public float getErrorValue( int point_index, int frame_index )
  {
    return array.getErrorValue(point_index, frame_index);
  }
  
  
/* * *
 * BoundsList3D
 * * */
  
 /**
  * Get minimum of all x, y, z extents (the smallest
  * extension for each direction.) This is calculated
  * internally as extents are set.  
  *
  *  @return The minimum extents for x, y, z directions.
  */ 
  public Vector3D getMinExtents( )
  {
    return bounds.getMinExtents();
  }
  
 /**
  * Get maximum of all x, y, z extents (the largest
  * extension for each direction.) This is calculated
  * internally as extents are set. 
  *
  *  @return The maximum extents for x, y, z directions.
  */ 
  public Vector3D getMaxExtents( )
  {
    return bounds.getMaxExtents();
  }
  
 /**
  * This should only be called if the bounds information
  * is reset, and the values for max/min extents needs to be updated. 
  * Max and Min extents are not recalculated automatically when the 
  * max/min extents are changed by a set call.
  */
  public void recalcMaxMinExtents( )
  {
    bounds.recalcMaxMinExtents( );
  }
  
 /**
  * Set extents for a single array element at given index.
  * There is an extension for each direction in 3d space.
  *
  *  @param  index   Point Index of the element.
  *  @param  extents The extents for x, y, and z.
  *  @throws IndexOutOfBoundsException If index is outside of valid
  *          range of indices: 0...NumElements.
  *  @throws IllegalArgumentException If any of the extents are nonnegative.
  */
  public void setExtents( int index, Vector3D extents )
  {
    bounds.setExtents( index, extents );
    updateMaxMinEdges(index);
  }
  
 /**
  * Sets extents over specified range of indices.
  * There is an extension for each direction in 3d space.
  *
  *  @param  index_start  First Point index of the range.
  *  @param  index_end    Last Point index of the range.
  *  @param  extents      Array of x, y, z extents
  *  @throws IndexOutOfBoundsException If index range goes 
  *          outside of valid  range of indices: 0...NumElements.
  *  @throws IllegalArgumentException If given extents array is 
  *          smaller than specified range or any of the extents are nonnegative.
  */ 
  public void setExtents( int index_start, int index_end,
                          Vector3D[] extents )
  {
    bounds.setExtents( index_start, index_end, extents );
    for(int i = index_start; i < index_end; i++)
      updateMaxMinEdges(i);
  }
  
 /**
  * Set orientation for a single array element at given index.
  * Orientation is set by giving the direction of oriented X
  * axis and direction of oriented Y axis.
  *
  *  @param  index      Point Index of the element.
  *  @param  x_axis     The direction of rotated X axis.
  *  @param  y_axis     The direction of rotated Y axis.
  *  @throws IndexOutOfBoundsException If index is outside of valid
  *          range of indices: 0...NumElements.
  */
  public void setOrientation( int index, 
                              Vector3D x_axis, 
                              Vector3D y_axis )
  {
    bounds.setOrientation( index, x_axis, y_axis);
  }
  
 /**
  * Sets orientation over specified range of indices by 
  * providing the new directions for x and y axes.
  *
  *  @param  index_start  First Point index of the range.
  *  @param  index_end    Last Point index of the range.
  *  @param  x_axes       Array of directions for adjusted
  *                       X axes
  *  @param  y_axes       Array of directions for adjusted
  *                       Y axes
  *  @throws IndexOutOfBoundsException If index range goes 
  *          outside of valid  range of indices: 0...NumElements.
  *  @throws IllegalArgumentException If given extents array is 
  *          smaller than specified range.
  */ 
  public void setOrientations( int index_start, int index_end,
                               Vector3D[] x_axes, 
                               Vector3D[] y_axes )
  {
    bounds.setOrientations( index_start, index_end, x_axes, y_axes);
  }
   
 /**
  * Get extents for a single array element based on index.
  * There is an extension for each direction in 3d space.
  *
  *  @param  index   Point Index of the element.
  *  @return If element is found, a copy containing 
  *      the x, y, and z extents.
  *          If element is not found, null is returned.
  *  @throws IndexOutOfBoundsException If index is outside of valid
  *          range of indices: 0...NumElements.
  */ 
  public Vector3D getExtents( int index )
  {
    return bounds.getExtents( index );
  }
  
 /**
  * Returns extents of specified range of indices.
  * There is an extension for each direction in 3d space.
  *
  *  @param  index_start  First Point index of the range.
  *  @param  index_end    Last Point index of the range.
  *  @return Copy of portion of array representing
  *      extents for range of indices.
  *  @throws IndexOutOfBoundsException If index range goes 
  *          outside of valid  range of indices: 0...NumElements.
  */ 
  public Vector3D[] getExtents( int index_start, int index_end )
  {
    return bounds.getExtents( index_start, index_end );
  }

 /**
  * Get orientation for a single array element based on index.
  * It gives the direction of the x and y axes.
  * Example:
  *  Vector3D[] newAxes = getOrientation(index);
  * In above, newAxes[0] represents the rotated X axis
  * vector and newAxes[1] represents the rotated Y axis.
  * The Z axis can be found by cross product of X and Y vectors.
  *
  *  @param  index   Point Index of the element.
  *  @return If element is found, a copy vectors representing the 
  *      orientation of x and y axes for that index. 
  *          If element is not found, null is returned.
  *  @throws IndexOutOfBoundsException If index is outside of valid
  *          range of indices: 0...NumElements.
  */ 
  public Vector3D[] getOrientation( int index )
  {
     return bounds.getOrientation( index );
  }
   
 /**
  * Returns orientation vectors of specified range of indices.
  * For each index in range specified, there is an X
  * axis direction and an Y axis direction.
  * Example:
  *  Vector3D[][] newAxes = getOrientation(index);
  *   newAxes[index_start][0] is adjusted X axis
  *   newAxes[index_start][1] is adjusted Y axis
  *   ..
  *   newAxes[index_end][0] is adjusted X axis
  *   newAxes[index_end][1] is adjusted Y axis
  * 
  *  @param  index_start  First Point index of the range.
  *  @param  index_end    Last Point index of the range.
  *  @return A copy of the portion of Orientation array within the
  *          specified range of indices.
  *  @throws IndexOutOfBoundsException If index range goes 
  *          outside of valid  range of indices: 0...NumElements.
  */ 
  public Vector3D[][] getOrientations( int index_start, int index_end )
  {
     return bounds.getOrientations( index_start, index_end);
  }

 /**
  * Returns X direction vectors of specified range of indices.
  * 
  *  @param  index_start  First Point index of the range.
  *  @param  index_end    Last Point index of the range.
  *  @return A copy of the portion of XOrientation array within the
  *          specified range of indices.
  *  @throws IndexOutOfBoundsException If index range goes 
  *          outside of valid  range of indices: 0...NumElements.
  */ 
  public Vector3D[] getXOrientations( int index_start, int index_end )
  {
    return bounds.getXOrientations( index_start, index_end);
  }
  
  /**
   * Returns Y direction vectors of specified range of indices.
   * 
   *  @param  index_start  First Point index of the range.
   *  @param  index_end    Last Point index of the range.
   *  @return A copy of the portion of YOrientation array within the
   *          specified range of indices.
   *  @throws IndexOutOfBoundsException If index range goes 
   *          outside of valid  range of indices: 0...NumElements.
   */ 
   public Vector3D[] getYOrientations( int index_start, int index_end )
   {
     return bounds.getYOrientations( index_start, index_end);
   }
  
  
 /*-----------------PRIVATE METHODS--------------------*/

  private void updateMaxMinEdges( int index ) 
  { 
    float max_ext = 0;
  
    float[] cur_ext = bounds.getExtents(index).get();
    
    if(cur_ext[0] > max_ext)  max_ext = cur_ext[0];
    if(cur_ext[1] > max_ext)  max_ext = cur_ext[1];
    if(cur_ext[2] > max_ext)  max_ext = cur_ext[2];
    max_ext = max_ext/2;

    float[] cur_point = array.getPoint(index).get();
    
    if(-1 > Float.MIN_VALUE) System.out.println("YES!");
    
    if(max_ext + cur_point[0] > maxedges[0]) 
      maxedges[0] = max_ext + cur_point[0];
    if(max_ext + cur_point[1] > maxedges[1]) 
      maxedges[1] = max_ext + cur_point[1];
    if(max_ext + cur_point[2] > maxedges[2]) 
      maxedges[2] = max_ext + cur_point[2];
      
    if(cur_point[0] - max_ext < minedges[0]) 
      minedges[0] = cur_point[0] - max_ext;
    if(cur_point[1] - max_ext < minedges[1]) 
      minedges[1] = cur_point[1] - max_ext;
    if(cur_point[2] - max_ext < minedges[2]) 
      minedges[2] = cur_point[2] - max_ext;

  }
    
 /*
  * MAIN - Basic main program to test the class
  */
  public static void main( String args[] )
  { 
    Vector3D[] my_extents = new Vector3D[10];
    Vector3D[] my_xaxes   = new Vector3D[10];
    Vector3D[] my_yaxes   = new Vector3D[10];
    Vector3D[] mypoints = new Vector3D[10];
    float[][]  mydata   = new float[10][3];
    float[][]  myerrors = new float[10][3];
    float[]    mytimes  = new float[3];
    float[]    tmp;
    float x, y, z, val;
    XScale times;
    
    
    for(int i = 0; i < 3; i++) mytimes[i] = (float)3*(i+1);
    
    times = new VariableXScale(mytimes);
    
    for(int i = 0; i < 10; i++)
    {
      x = (float)((i*i)*1.0); 
      y = (float)((i*i+1)*1.0);
      z = (float)((i*i+2)*1.0);
      mypoints[i] = new Vector3D(x, y, z);
      
      for(int j = 0; j < 3; j++)
      {
        val = (float)-1.0*((i+1)*(j+1));
        mydata[i][j]   = val;
        myerrors[i][j] = (float)(j+1+i+0.2); 
      }
    }
    
    for(int i = 0; i < 10; i++) {
      my_extents[i] = new Vector3D(i*1.0f, i*2.0f, i*3.0f);
      my_xaxes[i] = new Vector3D(i*4.0f, i*5.0f, i*6.0f);
      my_yaxes[i] = new Vector3D(i*7.0f, i*8.0f, i*9.0f);
    }
    
    PhysicalArray3DList test = new PhysicalArray3DList(10, times);
    
    test.set(0, mypoints[0]);
    test.setExtents(0, my_extents[0]);
    System.out.println("Max Edges: " + test.getMaxEdges().toString());
    System.out.println("Min Edges: " + test.getMinEdges().toString());
    
    test = new PhysicalArray3DList(10, mypoints, mydata, my_extents, my_xaxes,
                               my_yaxes, times);
    System.out.println("Max Edges: " + test.getMaxEdges().toString());
    System.out.println("Min Edges: " + test.getMinEdges().toString());
    
    try{
      test.set(11, mypoints[0], my_extents[0], my_xaxes[0], my_yaxes[0], mydata[0]);
    } catch(IndexOutOfBoundsException e) {
      System.out.println("Caught Exception: " + e);
    }

    
  } 
}
