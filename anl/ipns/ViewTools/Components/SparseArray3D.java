/*
 * File: SparseArray3D.java
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
 * This object stores scattered three 
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
public class SparseArray3D extends PointList3D implements ISparseArray3D 
{
 // Data
 private float[]    data_values;       // values for each 3d point
 
 // Errors
 private float[]    errors;            // vector of error values.
 private boolean    errors_set;        // has setErrors() method been called.
 private boolean    use_sqrt;          // should square-root errors be used.


 /**
  * Constructor that intializes array for data points,
  * data values, and error values. The AxisInfo, titles, and errors
  * settings are intitalized.
  *
  *  @param size The number of points.
  */
  public SparseArray3D( int size ) 
  {
    super( size );
    data_values = new float[size];
    errors = new float[size];
    
    for(int i = 0; i < size; i++)
      data_values[i] = errors[i] = Float.NaN;
    
    errors_set = false;
    use_sqrt = false;
  }
  
 /**
  * Constructor that intializes array for data points,
  * data values, and error values. The AxisInfo, titles, and errors
  * settings are intitalized.
  *
  *  @param size The number of points.
  *  @param points For arrays that are equal to or larger than 'size', it will
  *                only set the first 'size' number of positions. For array
  *                smaller than 'size', it will set the first equal number of
  *                positions.
  */
  public SparseArray3D( int size, Vector3D[] points ) 
  {
    super( size, points );
    data_values = new float[size];
    errors = new float[size];
    
    for(int i = 0; i < size; i++)
      data_values[i] = errors[i] = Float.NaN;
    
    errors_set = false;
    use_sqrt = false;
  }
  
 /**
  * Constructor that intializes array for data points,
  * data values, and error values. The AxisInfo, titles, and errors
  * settings are intitalized.
  *
  *  @param size The number of points.
  *  @param points For arrays that are equal to or larger than 'size', it will
  *                only set the first 'size' number of positions. For array
  *                smaller than 'size', it will set the first equal number of
  *                positions.
  *  @param values For arrays that are equal to or larger than 'size', it will
  *                only set the first 'size' number of values. For array
  *                smaller than 'size', it will set the first equal number of
  *                values.
  */
  public SparseArray3D( int size, Vector3D[] points, float[] values ) 
  {
    super( size, points );
    data_values = new float[size];
    errors = new float[size];
    
    for(int i = 0; i < size; i ++) {
      if(i < values.length)
        data_values[i] = values[i];
      else 
        data_values[i] = Float.NaN;
      
      errors[i] = Float.NaN;
    }
    
    errors_set = false;
    use_sqrt = false;
  }

 /**
  * Set a point with data value for single array element.
  *
  *  @param  index   The index of point in array.
  *  @param  point   The 3d coordinates of the point.
  *  @param  value   Value that element will be set to.
  *  @throws IndexOutOfBoundsException If index is outside of valid
  *          range of indices: 0...getNumPoints().
  */
  public void set( int index, Vector3D point, float value )
  {
    // Super's set will throw exception.
    super.set(index, point);
    
    data_values[index] = value;
  }
  
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
                  Vector3D[] points, float[] values )
  {  
    if(values.length < (index_end-index_start+1))
      throw new IllegalArgumentException("Array of values not large enough.");
      
    // Super's set will throw IndexOutOfBoundsException
    super.set(index_start, index_end, points);
      
    for(int i = index_start; i <= index_end; i++) {
      data_values[i] = values[i-index_start];
    }
  }
  
 /**
  * Set value for a single array element at given index.
  *
  *  @param  index   Index of the element.
  *  @param  value   Value that element will be set to.
  *  @throws IndexOutOfBoundsException If index is outside of valid
  *          range of indices: 0...getNumPoints().
  */
  public void setValue( int index, float value )
  {
    if(index < 0 || index >= getNumPoints())
      throw new IndexOutOfBoundsException("Unable to set.");
    
    data_values[index] = value;
  }
  
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
                         float[] values )
  {
    if(index_start < 0 || index_start >= getNumPoints())
      throw new IndexOutOfBoundsException("Start index out of bounds.");
      
    if(index_end < 0 || index_end >= getNumPoints())
      throw new IndexOutOfBoundsException("End index out of bounds.");
      
    if(values.length < (index_end-index_start+1))
      throw new IllegalArgumentException("Array of values not large enough.");
      
    for(int i = index_start; i <= index_end; i++) {
      data_values[i] = values[i-index_start];
    }
  }
  
 /**
  * Set all values in the array to a value. This method will usually
  * serve to "initialize" or zero out the array. 
  *
  *  @param value  float value that all elements in the array will be set to
  */
  public void setAllValues( float value )
  {
    for( int i = 0; i < getNumPoints(); i++ )
      setValue(i, value);
  }
  
 /**
  * Get value for a single array element based on index.
  *
  *  @param  index   Index of the element.
  *  @return If element is found, the float value for that element
  *          is returned. If element is not found, Float.NaN is returned.
  *  @throws IndexOutOfBoundsException If index is outside of valid
  *          range of indices: 0...getNumPoints().
  */ 
  public float getValue( int index )
  {
    if(index < 0 || index >= getNumPoints())
      throw new IndexOutOfBoundsException("Unable to set.");
      
    return data_values[index];
  }
  
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
  public float[] getValues( int index_start, int index_end )
  {
    float[] vals;
    
    if(index_start < 0 || index_start >= getNumPoints())
      throw new IndexOutOfBoundsException("Start index out of bounds.");
      
    if(index_end < 0 || index_end >= getNumPoints())
      throw new IndexOutOfBoundsException("End index out of bounds.");
      
    if(index_end < index_start) 
      return null;
      
    vals = new float[index_end - index_start + 1];
    
    for(int i = index_start; i <= index_end; i++) 
    {
      vals[i-index_start] = data_values[i];
    }
    
    return vals;
  }
  
 /**
  * Returns all values in the array.
  *
  *  @return A copy of all the data values in the array.
  */ 
  public float[] getValues( )
  {
    float[] c_vals = new float[getNumPoints()];
    
    for(int i = 0; i < getNumPoints(); i++) 
    {
      c_vals[i] = data_values[i];
    }
    
    return c_vals;
  }
  
 /**
  * Set the error values that correspond to the data. The size of error array
  * should match the size of the data array. Float.NaN will be left for
  * undersized error arrays. Values that are in an array
  * that exceeds the data array will be ignored.
  *
  *  @param  error_values The array of error values corresponding to the data.
  *  @return True if error array succesfully matched size of data array.
  */
  public boolean setErrors( float[] error_values )
  {
    errors_set = true;
    // by setting these values, do not use the calculated
    // square-root errors
    setSquareRootErrors( false );
    
    int end = 0;
    
    if(error_values.length < getNumPoints()) end = error_values.length;
    else end = getNumPoints();
    
    for(int i = 0; i < end; i++)
      errors[i] = error_values[i];
      
    if(error_values.length == getNumPoints()) return true;
    else return false;
  }
  
 /**
  * Get the error values corresponding to the data. setSquareRootErrors(true)
  * or setErrors(array) must be called to have meaningful values returned.
  * By default, null will be returned. If square-root values are
  * desired and the data value is negative, the square-root of the positive
  * value will be returned. If setErrors() was called, then the error array
  * passed in will be returned (this array will be always have the same
  * dimensions as the data, it will be modified if the dimensions are
  * different).
  *
  *  @return error values of the data.
  */
  public float[] getErrors()
  {
    // if setSquareRootErrors(true) was called
    if( use_sqrt )
    {
      float[] sqrt_errors = new float[getNumPoints()];
      for( int i = 0; i < getNumPoints(); i++ ) 
      {
        sqrt_errors[i] = (float)
              Math.sqrt( (double)Math.abs( getValue(i) ) );
      }
      return sqrt_errors;
    }
    
    // if the errors were set using the setErrors() method
    if( errors_set ) 
    {
      float[] f_errors = new float[getNumPoints()];
      for( int i = 0; i < getNumPoints(); i++ ) 
      {
        f_errors[i] = errors[i];
      }
      return f_errors;
    }
      
    // if neither use_sqrt nor errors_set, return null.
    return null;
  }
  
 /**
  * Use this method to specify whether to use error values that were passed
  * into the setErrors() method or to use the square-root of the data value.
  *
  *  @param  use_sqrt If true, use square-root.
  *                   If false, use set error values if they exist.
  */
  public void setSquareRootErrors( boolean use_sqrt_errs )
  {
    use_sqrt = use_sqrt_errs;
  }
 
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
  public float getErrorValue( int index )
  {
    if(index < 0 || index >= getNumPoints())
      throw new IndexOutOfBoundsException("Unable to get error.");
      
    // return sqrt error value if specified.
    if( use_sqrt )
      return (float)Math.sqrt( (double)Math.abs( getValue(index) ) );
    // if the errors were set using the setErrors() method, return them
    
    if( errors_set )
      return errors[index];
      
    // if neither use_sqrt or errors_set, then return NaN
    return Float.NaN;
  }
  
 /*
  * MAIN - Basic main program to test the SparseArray3D class
  */
  public static void main( String args[] ) 
  {
    Vector3D[] mypoints = new Vector3D[10];
    float[]    mydata   = new float[10];
    float[]    myerrors = new float[10];
    float[]    tmp;
    float x, y, z, val;
    int index;
    
    
    for(int i = 0; i < 10; i++)
    {
      x = (float)((i*i)*1.0); 
      y = (float)((i*i+1)*1.0);
      z = (float)((i*i+2)*1.0);
      mypoints[i] = new Vector3D(x, y, z);
      
      val = (float)-1.0*(i*i);
      mydata[i]   = val;
      myerrors[i]    = (float)2.2;
    }
    
    SparseArray3D test = new SparseArray3D( 10 );
    
    System.out.println("*********************ADD DATA********************");

    test.set(0, 7, mypoints, mydata);

    test.set(8, mypoints[8]);
    test.setValue(8, mydata[8]);
    
    test.set(9, mypoints[9], mydata[9]);
    
    test.testPrintArray(test);
    
    tmp = test.getErrors();
    System.out.println("Are errors set? " + tmp);
    
    System.out.println("*********************SET ERRORS********************");
    
    test.setErrors(myerrors);
    
    test.testPrintArray(test);
    
    System.out.println("*********************SQRT ERRORS********************");
    
    test.setSquareRootErrors(true);
    
    test.testPrintArray(test);
    
    System.out.println("*********************EXCEPTIONS********************");
    try {
      test.setValue(100, (float)1.0);
    } catch(IndexOutOfBoundsException e) {
      System.out.println("Exception caught: " + e);
    }
    try {
      test.set(100, mypoints[0]);
    } catch(IndexOutOfBoundsException e) {
      System.out.println("Exception caught: " + e);
    }
    try {
      float[] badarray = new float[2];
      badarray[0] = badarray[1] = 0;
      
      test.setValues(2, 5, badarray);
      
    } catch(IllegalArgumentException e) {
      System.out.println("Exception caught: " + e);
    }
    
  } // END OF MAIN  

  /*------------Print for Testing Purposes----------*/
  private void testPrintArray(SparseArray3D array)
  {
    float[] xyz;
    float[] values;
    float[] errs;
    Vector3D point;
    Vector3D[] pts;
    
    pts = array.getPoints();
    values = array.getValues();
    
    for(int i = 0; i < array.getNumPoints(); i++)
    {
      point = pts[i];
      xyz = point.get();
      System.out.print( xyz[0] + "\t" + xyz[1] + "\t" + xyz[2] + "\t" );
      System.out.print( values[i] + "\t" + array.getErrorValue(i) );
      System.out.println( );
      
      // THIS SHOULD NOT CHANGE THE ARRAY DATA
      point.set(xyz[0]*xyz[1], xyz[1]*xyz[2], xyz[0]);
      values[i] = 0;
    }
  }
}