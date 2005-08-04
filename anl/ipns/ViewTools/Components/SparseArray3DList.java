/*
 * File: SparseArray3DList.java
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
 *  Revision 1.2  2005/08/04 22:42:34  cjones
 *  Updated comment header and javadocs
 *
 *  Revision 1.1  2005/07/19 15:48:10  cjones
 *  Added 3D Array implementations.
 * 
 */

package gov.anl.ipns.ViewTools.Components; 

import gov.anl.ipns.MathTools.Geometry.Vector3D;
import DataSetTools.dataset.XScale;
import DataSetTools.dataset.VariableXScale;
import java.lang.Float;

/**
 * This object stores scattered three 
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
 
public class SparseArray3DList extends PointList3D implements ISparseArray3DList
{
 // Data
 private float[][]  data_values;       // array of values for each 3d point
 
 // Time
 private XScale     frames;             // Holds time values, set by constructor.
 
 // Errors
 private float[][]  errors;            // vector of error values.
 private boolean    errors_set;        // has setErrors() method been called.
 private boolean    use_sqrt;          // should square-root errors be used.
  
  
 /**
  * Constructor that intializes arrays for data points,
  * data values, and error values. The number of points
  * is set to size. The AxisInfo, titles, and errors
  * settings are intitalized. The time values are passed in as
  * an XScale object.
  * 
  *  @param pointsize Number of points.
  *  @param timevals  Holds the time values. Values should be
  *                 in increasing order.
  */
  public SparseArray3DList( int pointsize, XScale timevals ) 
  {
    super( pointsize );
    
    frames = timevals;
    
    data_values = new float[pointsize][frames.getNum_x()];
    errors = new float[pointsize][frames.getNum_x()];
    
    for(int i = 0; i < pointsize; i++ )
      for(int j = 0; j < frames.getNum_x(); j++)
        data_values[i][j] = errors[i][j] = Float.NaN;
    
    errors_set = false;
    use_sqrt = false;
  }
  
  
 /**
  * Constructor that intializes arrays for data points,
  * data values, and error values. The number of points
  * is set to size. The AxisInfo, titles, and errors
  * settings are intitalized. The time values are passed in as
  * an XScale object.
  *
  *  @param pointsize The number of points.
  *  @param points For arrays that are equal to or larger than 'size', it will
  *                only set the first 'size' number of positions. For array
  *                smaller than 'size', it will set the first equal number of
  *                positions.
  *  @param timevals Holds the time values. Values should be
  *                 in increasing order.
  */
  public SparseArray3DList( int pointsize, Vector3D[] points,  XScale timevals ) 
  {
    super( pointsize, points );

    frames = timevals;

    data_values = new float[pointsize][frames.getNum_x()];
    errors = new float[pointsize][frames.getNum_x()];
    
    for(int i = 0; i < pointsize; i++ )
      for(int j = 0; j < frames.getNum_x(); j++)
        data_values[i][j] = errors[i][j] = Float.NaN;
    
    errors_set = false;
    use_sqrt = false;
  }
  
 /**
  * Constructor that intializes Vectors for data points,
  * data values, and error values. The number of points
  * is set to size. The AxisInfo, titles, and errors
  * settings are intitalized. The time values are passed in as
  * an XScale object.
  *
  *  @param pointsize The number of points.
  *  @param points For arrays that are equal to or larger than 'size', it will
  *                only set the first 'size' number of positions. For array
  *                smaller than 'size', it will set the first equal number of
  *                positions.
  *  @param values  Values up to values[pointsize][timesize]
  *  @param timevals Holds the time values. Values should be
  *                 in increasing order.
  */
  public SparseArray3DList( int pointsize, Vector3D[] points, float[][] values, 
                            XScale timevals ) 
  {
    super( pointsize, points );
    
    frames = timevals;

    data_values = new float[pointsize][frames.getNum_x()];
    errors = new float[pointsize][frames.getNum_x()];
    
    for(int i = 0; i < pointsize; i++ ) {
      for(int j = 0; j < frames.getNum_x(); j++) {
        if(i < points.length) 
        {
          if( j < values[i].length )
            data_values[i][j] = values[i][j];
          else
            data_values[i][j] = Float.NaN;
        }
        else 
          data_values[i][j] = Float.NaN;

        errors[i][j] = Float.NaN;
      }
    }
    
    errors_set = false;
    use_sqrt = false;
  }
  
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
    return frames.getNum_x();
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
  public void set(int point_index, Vector3D point, float values[] )
  { 
    if(values.length < frames.getNum_x())
      throw new IllegalArgumentException("Values do not span all frames.");
      
    // Super's set will throw index exceptions.
    super.set(point_index, point);
    
    for(int i = 0; i < frames.getNum_x(); i++)
      data_values[point_index][i] = values[i];
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
    if(frame_index < 0 || frame_index >= frames.getNum_x())
      throw new IndexOutOfBoundsException("Unable to set. Time index invalid.");
    if(point_index < 0 || point_index >= getNumPoints())
      throw new IndexOutOfBoundsException("Unable to set. Point index invalid.");
    
    data_values[point_index][frame_index] = value;
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
    if(point_index < 0 || point_index >= getNumPoints())
      throw new IndexOutOfBoundsException("Unable to set. Point index invalid.");
    if(values.length < frames.getNum_x())
      throw new IllegalArgumentException("Unable to set. Too few values.");
      
    for(int i = 0; i < frames.getNum_x(); i++)
      data_values[point_index][i] = values[i];
  }
  
 /**
  * Set all values in the array to a value. This method will usually
  * serve to "initialize" or zero out the array. 
  *
  *  @param value  float value that all elements in the array will be set to
  */
  public void setAllValues( float value )
  {
    for(int p_i=0; p_i < getNumPoints(); p_i++)
    {
      for(int t_i = 0; t_i < frames.getNum_x(); t_i++)
        data_values[p_i][t_i] = value;  
    }
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
    if(point_index < 0 || point_index >= getNumPoints())
      throw new IndexOutOfBoundsException("Unable to get. Point index invalid.");
    if(frame_index < 0 || frame_index >= frames.getNum_x())
      throw new IndexOutOfBoundsException("Unable to get. Time index invalid.");
      
    return data_values[point_index][frame_index];
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
    if(frame_index < 0 || frame_index >= frames.getNum_x())
      throw new IndexOutOfBoundsException("Unable to get. Time index invalid.");
      
    float[] data_list = new float[getNumPoints()];
    
    for(int p_i=0; p_i < getNumPoints(); p_i++)
      data_list[p_i] = data_values[p_i][frame_index];
    
    return data_list;
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
    if(point_index < 0 || point_index >= getNumPoints())
      throw new IndexOutOfBoundsException("Unable to get. Time index invalid.");
      
    float[] data_list = new float[frames.getNum_x()];
    float[] cur_data = data_values[point_index];
    
    System.arraycopy(cur_data, 0, data_list, 0, frames.getNum_x());
    
    return data_list;
  }
   
 /**
  * Returns the frame value for given frame index.
  *
  *  @param frame_index Frame index.
  *  @return Frame value.
  */
  public float getFrame( int frame_index )
  {
    if(frame_index < 0 || frame_index >= frames.getNum_x())
      throw new IndexOutOfBoundsException("Unable to get time.");
      
    return frames.getX( frame_index );
  }
  
 /**
  * Returns all frame values for array.
  *
  * @return Frame values. 
  */
  public float[] getFrames( )
  {
    return frames.getXs();
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
    boolean match_size = true;
    
    this.errors_set = true;
    // by setting these values, do not use the calculated
    // square-root errors
    setSquareRootErrors( false );
    
    int p_end = 0;
    
    if(error_values.length < getNumPoints()) 
    {
      p_end = error_values.length;
      match_size = false;
    }
    else p_end = getNumPoints();
    
    float[] err_array;
    for(int i = 0; i < p_end; i++)
    {
      err_array = errors[i];
      if(error_values[i].length < frames.getNum_x())
      {
        System.arraycopy(error_values[i], 0, 
                         err_array, 0, error_values[i].length);
        match_size = false;        
      }
      else 
      {
        System.arraycopy(error_values[i], 0, 
                         err_array, 0, frames.getNum_x());
      }

    }
      
    return match_size;
  }
  
 /**
  * Get the error values corresponding to the data. If no error values have
  * been set, the square-root of the data value will be returned.
  *
  *  @return Copy of error values for the data.
  */
  public float[][] getErrors()
  { 
    // if setSquareRootErrors(true) was called
    if( use_sqrt )
    {
      float[][] sqrt_errors = new float[getNumPoints()][frames.getNum_x()];
      for( int i = 0; i < getNumPoints(); i++ ) 
      {
        for(int j =0; i < frames.getNum_x(); i++)
        {
          sqrt_errors[i][j] = (float)
               Math.sqrt( (double)Math.abs( getValue(i,j) ) ); 
        }
      }
      return sqrt_errors;
    }
    
    // if the errors were set using the setErrors() method
    if( errors_set ) 
    {
      float[][] f_errors = new float[getNumPoints()][frames.getNum_x()];
      for( int i = 0; i < getNumPoints(); i++ ) 
      {
        System.arraycopy(errors[i], 0, 
                         f_errors[i], 0, frames.getNum_x());
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
  public void setSquareRootErrors( boolean use_sqrt )
  {
    this.use_sqrt = use_sqrt;
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
    if(point_index < 0 || point_index >= getNumPoints())
      throw new IndexOutOfBoundsException("Unable to get. Point index invalid.");
    if(frame_index < 0 || frame_index >= frames.getNum_x())
      throw new IndexOutOfBoundsException("Unable to get. Time index invalid.");
      
    if( use_sqrt )
      return (float)Math.sqrt( 
                   (double)Math.abs( getValue(point_index, frame_index) ) );
    // if the errors were set using the setErrors() method, return them
    
    if( errors_set )
      return errors[point_index][frame_index];
      
    // if neither use_sqrt or errors_set, then return NaN
    return Float.NaN;
  }
  
  
 /*
  * MAIN - Basic main program to test the SparseArray3D class
  */
  public static void main( String args[] ) 
  {
    Vector3D[] mypoints = new Vector3D[10];
    float[][]  mydata   = new float[10][3];
    float[][]  myerrors = new float[10][3];
    float[]    mytimes  = new float[3];
    float[][]  tmp;
    VariableXScale times;
    float x, y, z, val;
    int index;
    
    // Make all the data
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
    
    System.out.println("*****************Create***************");
    // Make array for 10 elements.
    SparseArray3DList test = new SparseArray3DList(10, times);
    
    test.testPrintArray(test);
    
    System.out.println("*****************All zeros***************");
    // Zero all data values
    test.setAllValues((float)0);
    test.testPrintArray(test);
    
    System.out.println("***************Set Data***************");

    test.set(0, 6, mypoints);
    for(int i = 0; i < 7; i++)
      test.setValues(i, mydata[i]);
      
    test.set(7, mypoints[7]);
    for(int i = 0; i < test.getNumFrames(); i++)
      test.setValue(7, i, mydata[7][i]);
    
    test.set(8, mypoints[8], mydata[8]);
    
    test.set(9, mypoints[9]);
    test.setValues(9, mydata[9]);
    
    test.testPrintArray(test);
    
    // Ensure errors not set
    tmp = test.getErrors();
    System.out.println("Are errors set? " + tmp);
    
    System.out.println("*********************SET ERRORS********************");
    
    // Set errors
    test.setErrors(myerrors);
    
    test.testPrintArray(test);
    
    // Test return of getErrors
    myerrors = test.getErrors();
    
    System.out.println("FIRST ERROR: " + myerrors[0][0]);
    
    System.out.println("*********************SQRT ERRORS********************");
    
    // Check sqrt errors
    test.setSquareRootErrors(true);
    
    test.testPrintArray(test);
    
    System.out.println("*********************EXCEPTIONS********************");
    try {
      test.setValue(100, 50, (float)1.0);
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
      
      test.setValues(2, badarray);
      
    } catch(IllegalArgumentException e) {
      System.out.println("Exception caught: " + e);
    }
    System.out.println("********************VALUES BY POINT******************");
    
    // Get all slices by point
    for(int i = 0; i < test.getNumPoints(); i++)
    {   
      mydata[0] = test.getValuesAtPoint(i);
      for(int j = 0; j < test.getNumFrames(); j++)
        System.out.print(mydata[0][j] + "\t");
      System.out.println();
    }
    
    System.out.println("********************VALUES BY TIME******************");
    
    // Get all slices by frames
    float[] data_in_time;
    for(int i = 0; i < test.getNumFrames(); i++)
    {   
      data_in_time = test.getValuesAtFrame(i);
      for(int j = 0; j < test.getNumPoints(); j++)
        System.out.print(data_in_time[j] + "\t");
      System.out.println();
    }
    
    System.out.println("********************Construct******************");
    
    // Build an object by passing all data
    SparseArray3DList test2 = new SparseArray3DList(13, mypoints, mydata, times);
    test2.testPrintArray(test2);
    
    
  } // END OF MAIN  
  
  /*------------Print for Testing Purposes----------*/
  private void testPrintArray(SparseArray3DList array)
  {
    float[] xyz;
    Vector3D point;

    System.out.print("Times: \t\t");
    for(int j = 0; j < array.getNumFrames(); j++)
      System.out.print( array.getFrame(j) + "\t" );
    System.out.println();

    for(int i = 0; i < array.getNumPoints(); i++)
    {
      point = (Vector3D)array.getPoint(i);
      xyz = point.get();
      System.out.println( "(" + xyz[0] + ", " + xyz[1] + ", " + xyz[2] + ")\t" );

      System.out.print("\tValues:\t");
      for(int j = 0; j < array.getNumFrames(); j++)
        System.out.print( array.getValue(i, j) + "\t" );
      System.out.println( );
      
      System.out.print("\tErrors:\t");
      for(int j = 0; j < array.getNumFrames(); j++)
        System.out.print( array.getErrorValue(i, j) + "\t" );
      
      System.out.println( );
    }
  }
  
} 