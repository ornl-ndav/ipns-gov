/*
 * File: ISparseArray3D.java
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
 *  Revision 1.4  2005/08/04 22:42:31  cjones
 *  Updated comment header and javadocs
 *
 *  Revision 1.3  2005/07/19 15:48:06  cjones
 *  Added 3D Array implementations.
 * 
 */
 
package gov.anl.ipns.ViewTools.Components; 

import gov.anl.ipns.MathTools.Geometry.Vector3D;

/**
 * This interface extends IPointList3D to include values for scattered three 
 * dimensional points.  The size of a Array must be given by the constructor,
 * but the values may be set later. For each point in the
 * array, there is an associated integer index.  The range of the index runs
 * from zero to the total number of points the array can hold.
 *
 * If no positions are given at construction, the points are intitalized as
 * Vector3D objects with Float.NaN for the x, y, and z fields. 
 * If no data values are given, they are intialized to  Float.NaN.
 *
 * ISparseArray3D is used to hold a collection of three dimensional
 * coordinates with data values that are easy to access. The order of the points
 * may not be important for the code using ISparseArray3D. For example, a 3D
 * Rendering program might use the array to plot points and color them according
 * to their values, and the order of plotting makes no difference on the end
 * result.
 *
 * In addition to data values, the user can set associated error values to
 * each piece of data. The values are initialized to Float.NaN. For quick error
 * setting, square root error approximation may be used.
 */
public interface ISparseArray3D extends IPointList3D 
{	 
 /**
  * Set a point with data value for single array element.
  *
  *  @param  index   The index of point in array.
  *  @param  point   The 3d coordinates of the point.
  *  @param  value   Value that element will be set to.
  *  @throws IndexOutOfBoundsException If index is outside of valid
  *          range of indices: 0...getNumPoints().
  */
  public void set( int index, Vector3D point, float value );
  
 /**
  * Sets points with their respective values to array.
  *
  *  @param  index_start  First index of the range.
  *  @param  index_end    Last index of the range.
  *  @param  points   The 3d coordinates.
  *  @param  values   Values for each point.
  *  @throws IndexOutOfBoundsException If index is outside of valid
  *          range of indices: 0...getNumPoints().
  *  @throws IllegalArgumentException If given points or values array is 
  *          smaller than specified range.
  */
  public void set( int index_start, int index_end, 
                  Vector3D[] points, float[] values );

 /**
  * Set value for a single array element at given index.
  *
  *  @param  index   Index of the element.
  *  @param  value   Value that element will be set to.
  *  @throws IndexOutOfBoundsException If index is outside of valid
  *          range of indices: 0...getNumPoints().
  */
  public void setValue( int index, float value );
  
 /**
  * Sets values of specified range of indices.
  *
  *  @param  index_start  First index of the range.
  *  @param  index_end    Last index of the range.
  *  @param  values       Array of float values
  *  @throws IndexOutOfBoundsException If index range goes outside 
  *          of valid range of indices: 0...getNumPoints().
  *  @throws IllegalArgumentException If given values array is 
  *          smaller than specified range.
  */ 
  public void setValues( int index_start, int index_end, 
                         float[] values );
  
 /**
  * Get value for a single array element based on index.
  *
  *  @param  index   Index of the element.
  *  @return If element is found, the float value for that element
  *          is returned. If element is not found, Float.NaN is returned.
  *  @throws IndexOutOfBoundsException If index is outside of valid
  *          range of indices: 0...getNumPoints().
  */ 
  public float getValue( int index );
  
 /**
  * Returns values of specified range of indices.
  *
  *  @param  index_start  First index of the range.
  *  @param  index_end    Last index of the range.
  *  @return A copy of the portion of the array that was
  *          specified.
  *  @throws IndexOutOfBoundsException If index range goes outside 
  *          of valid range of indices: 0...getNumPoints().
  */ 
  public float[] getValues( int index_start, int index_end );
  
 /**
  * Returns all values in the array.
  *
  *  @return A copy of all the data values in the array.
  */ 
  public float[] getValues( );
  
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
  public boolean setErrors( float[] error_values );
  
 /**
  * Get the error values corresponding to the data. If no error values have
  * been set, the square-root of the data value will be returned.
  *
  *  @return Copy of error values for the data.
  */
  public float[] getErrors();
   
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
  *  @param  index Index number corresponding to data.
  *  @return Error value for data at specified index. If invalid,
  *          Float.NaN is returned.
  *  @throws IndexOutOfBoundsException If index is outside 
  *          of valid range of indices: 0...getNumPoints().
  */
  public float getErrorValue( int index ); 
}