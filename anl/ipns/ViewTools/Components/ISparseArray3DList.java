/*
 * File: ISparseArray3DList.java
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
 *  Revision 1.2  2005/06/14 14:20:46  cjones
 *  Added 'Modified' line and package statements.
 * 
 */
 
package gov.anl.ipns.ViewTools.Components; 

import gov.anl.ipns.ViewTools.Components.AxisInfo;
import gov.anl.ipns.MathTools.Geometry.Vector3D;

/**
 * This interface is intended for classes that must store scattered three 
 * dimensional mappings of data that change through frames. The coordinates for
 * each point are assumed to be static, and only the values at those points
 * change with each frame. Thus for each 3d point, there is a list of data
 * values with length equal to the number of frames.
 *
 * For each point that is in ISparseArray3DList, there is an associated integer 
 * index. The range of this index runs from zero to the total number of points, 
 * which should be provided to the constructor.  If no positions are given at 
 * construction, the points are intitalized as Vector3D objects with Float.NaN 
 * for the x, y, and z fields. 
 *
 * For each frame, there is an associated integer index (frame index). The range 
 * of this index runs from zero to the number of frame steps currently in the
 * array.  The number of frames and their values should be set in the 
 * constructor.
 *
 * For each point and frame, a value may be set. The data value is intialized to 
 * Float.NaN.  Also, methods exist to get slices of data values for either one
 * time frame or one point.
 *
 * In addition to data values, the user can set associated error values to
 * each piece of data. The values are initialized to Float.NaN whenever a point
 * is added. For quick error setting, square root error approximation may be 
 * used.
 */
public interface ISparseArray3DList extends IPointList3D
{  
 /**
  * Returns the number of frames. Frames are 
  * ordered by increasing value and each frame has an 
  * associated frame index integer.  All frame indices
  * share the same 3d points but may have different
  * data values.
  *
  *  @return This returns the number of points in the array. 
  */
  public int getNumFrames( );
  
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
  public void set(int point_index, Vector3D point, float values[] );
  
 /**
  * Set value for a single array element at given point and
  * frame.
  *
  *  @param  point_index  Point Index of the element.
  *  @param  frame_index  Frame Index of the element.
  *  @param  value        Value that element will be set to.
  *  @throws IndexOutOfBoundsException If either index is
  *          outside of its respective valid range.
  */
  public void setValue( int point_index, int frame_index, 
                        float value );
  
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
  public void setValues( int point_index, float[] values );
  
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
  public float getValue( int point_index, int frame_index );
  
 /**
  * Returns all data values for given frame index. 
  *
  *  @param  frame_index  Index to frame for data values.
  *  @return A copy of the portion of the array that was
  *          specified.
  *  @throws IndexOutOfBoundsException If index range goes outside 
  *          of valid range of indices: 0...getNumFrames().
  */ 
  public float[] getValuesAtFrame( int frame_index );
  
 /**
  * Returns all data values for given point index. 
  *
  *  @param  point_index  Index to point for data values.
  *  @return A copy of the portion of the array that was
  *          specified.
  *  @throws IndexOutOfBoundsException If index range goes outside 
  *          of valid range of indices: 0...getNumPoints().
  */ 
  public float[] getValuesAtPoint( int point_index );
   
 /**
  * Returns the frame value for given frame index.
  *
  *  @param frame_index Frame index.
  *  @return Frame value.
  */
  public float getFrame( int frame_index );
  
 /**
  * Returns all frame values for array.
  *
  * @return Frame values. 
  */
  public float[] getFrames( );
   
  /* ** */
  /* ERROR METHODS */
  /* ** */
  
 /**
  * Set the error values that correspond to the data. The size of error array
  * should match the size of the data array. ZFloat.NaN will be left for
  * undersized error arrays.s Values that are in an array
  * that exceeds the data array will be ignored.
  *
  *  @param  error_values The array of error values corresponding to the data.
  *  @return True if error array succesfully matched size of data array.
  */
  public boolean setErrors( float[][] error_values );
  
 /**
  * Get the error values corresponding to the data. If no error values have
  * been set, the square-root of the data value will be returned.
  *
  *  @return Copy of error values for the data.
  */
  public float[][] getErrors();
  
 /**
  * Use this method to specify whether to use error values that were passed
  * into the setErrors() method or to use the square-root of the data value.
  *
  *  @param  use_sqrt If true, use square-root.
  *                   If false, use set error values if they exist.
  */
  public void setSquareRootErrors( boolean use_sqrt );
 
 /**
  * Get an error value for given index. Returns Float.NaN if
  * index is  invalid.
  *
  *  @param  point_index Point index corresponding to data.
  *  @param  frame_index Frame index corresponding to data.
  *  @return Error value for data at specified index. If invalid,
  *          Float.NaN is returned.
  *  @throws IndexOutOfBoundsException If index is outside 
  *          of valid range of indices: 0...getNumPoints().
  */
  public float getErrorValue( int point_index, int frame_index ); 
}