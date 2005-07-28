/*
 * File: ArrayGenerator.java
 *
 * Copyright (C) 2005, Mike Miller
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
 * Primary   Mike Miller <millermi@uwstout.edu>
 * Contact:  Student Developer, University of Wisconsin-Stout
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
 *  Revision 1.3  2005/07/28 15:16:44  kramer
 *  Modified the getRegionValues() method.  Previously if the field
 *  'dataArray' was 'null' it was filled with data without being instantiated
 *  (causing a NullPointerException).  Now if 'dataArray' is 'null' a new
 *  float[][] is instantiated.
 *
 *  Revision 1.2  2005/03/23 05:46:55  millermi
 *  - Removed unnecessary semicolons which caused Eclipse warnings.
 *
 *  Revision 1.1  2005/03/07 22:04:58  millermi
 *  - Initial version - Class that implements IVirtualArray2D. Values
 *    are generated dynamically based on a formula instead of stored
 *    physically.
 *
 */
package gov.anl.ipns.ViewTools.Components.TwoD;

import gov.anl.ipns.ViewTools.Components.*;
import gov.anl.ipns.Util.Sys.SharedMessages;

/**
 * This class generates values for an array based on an equation. The data
 * array never actually exists until asked for. This class can be used
 * to test large arrays without actually storing all of the values.
 */

public class ArrayGenerator implements IVirtualArray2D
{
  // data members
  private float[][] dataArray;
  private int num_rows; 	     // In M x N array, stores M
  private int num_columns;	     // In M x N array, stores N
  private AxisInfo rowinfo;
  private AxisInfo colinfo;
  private AxisInfo datainfo;
  private String title;
  private int counter = 0;
   
 /**
  * Constructor that allows the user to enter dimensions M,N and
  * creates a new M x N virtual array containing all zeros.
  *
  *  @param  rows  Specify the number of rows in the virtual array
  *  @param  columns  Specify the number of columns in the virtual array.
  */
  public ArrayGenerator( int rows, int columns )
  {
    num_rows = rows;
    num_columns = columns;
    rowinfo = new AxisInfo(0, 1, AxisInfo.NO_LABEL,
                           AxisInfo.NO_UNITS, AxisInfo.LINEAR);
    colinfo = new AxisInfo(0, 1, AxisInfo.NO_LABEL,
                           AxisInfo.NO_UNITS, AxisInfo.LINEAR);
    datainfo = new AxisInfo( getDataValue(0,0), getDataValue(rows-1,columns-1),
			     AxisInfo.NO_LABEL, AxisInfo.NO_UNITS,
			     AxisInfo.LINEAR);
    title = NO_TITLE;
  }

 /**
  * Returns the attributes of the data array in a AxisInfo wrapper.
  * This method will take in a boolean value to determine for which axis
  * info is being retrieved for. 
  *
  *  @param  axiscode The integer code for this axis.
  *  @return If AxisInfo.X_AXIS, AxisInfo object with X axis info is returned.
  *	     If AxisInfo.Y_AXIS, AxisInfo object with Y axis info is returned.
  *          Else, the data axis info is returned.
  *  @see   gov.anl.ipns.ViewTools.Components.AxisInfo
  */
  public AxisInfo getAxisInfo( int axiscode )
  {
    if( axiscode == AxisInfo.X_AXIS )
      return rowinfo;
    if( axiscode == AxisInfo.Y_AXIS )
      return colinfo;
    return datainfo;
  }
  
 /**
  * Sets the attributes of the data array within a AxisInfo wrapper.
  * This method will take in an integer to determine which axis
  * info is being altered.
  *
  *  @param  axiscode
  *  @param  min
  *  @param  max
  *  @param  label
  *  @param  units
  *  @param  scale
  */
  public void setAxisInfo( int axiscode, float min, float max,
			   String label, String units, int scale)
  {
    if(axiscode == AxisInfo.X_AXIS)
      rowinfo = new AxisInfo(min,max,label,units, scale);
    else if(axiscode == AxisInfo.Y_AXIS)
      colinfo = new AxisInfo(min,max,label,units, scale);
    else
      datainfo = new AxisInfo(min,max,label,units, scale);
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
      rowinfo = info.copy();
    else if(axiscode == AxisInfo.Y_AXIS)
      colinfo = info.copy();
    else
      datainfo = info.copy();
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
  
 /**
  * Get value for a single array element. If row or column exceeds the array,
  * Float.NaN is returned. Use a statement similar to:
  * " Float.isNaN( data.getDataValue(row,col) ) " to check if
  * the value returned is Float.NaN.
  *
  *  @param  row     row number of element
  *  @param  column  column number of element
  *  @return If element is found, the float value for that element is returned.
  *	     If element is not found, Float.NaN is returned.
  */ 
  public float getDataValue( int row, int column )
  {
    // return calculated value, based on a random formula.
    if( row < num_rows && column < num_columns && row >= 0 && column >= 0)
    {
      return ( ((float)(3*row+column))/3000f - 2f );
    }
    else
    {
      //SharedMessages.addmsg("Warning - cell at position (" +
      //  	      row + "," + column + ") exceeds the array bounds." );
      return Float.NaN;
    }
  }  
  
 /**
  * This method is here to satisfy the interface, no setting is done
  * since values are calculated.
  *
  *  @param  row     row number of element
  *  @param  column  column number of element
  *  @param  value   value that element will be set to
  */
  public void setDataValue( int row, int column, float value )
  {
    // Do nothing since values are not stored, but calculated.
  }

 /**
  * Get values for a portion or all of a row.
  * The "from" and "to" values must be direct array reference, i.e.
  * because the array positions start at zero, not one, this must be
  * accounted for. If the array passed in exceeds the bounds of the array, 
  * get values for array elements and ignore extra values.
  *
  *  @param  row   the row number being altered
  *  @param  from  the column number of first element to be altered
  *  @param  to    the column number of the last element to be altered
  *  @return If row, from, and to are valid, an array of floats containing
  *	     the specified section of the row is returned.
  *	     If row, from, or to are invalid, an empty 1-D array is returned.
  */
  public float[] getRowValues( int row, int from, int to )
  {
    if( from > to )
    {
      // swap them
      int temp = 0;
      temp = to;
      to = from;
      from = temp;
    }	   
    if( from >= num_columns || to < 0 )
    {  
      SharedMessages.addmsg("Warning - bound exceeds array in getRowValues()");
      return new float[0];
    }
    if( from < 0 )
      from = 0;
    if( to >= num_columns )
      to = num_columns - 1;
    if( row < num_rows && row >= 0 )
    {
      float[] value = new float[(to - from + 1)];
      int i = 0;
      while( from != to + 1 )
      {
        value[i] = getDataValue(row,from);
        i++;
        from++;
      }
      return value;
    }
    else
    {  
      SharedMessages.addmsg("Warning - invalid row " +
                            "selection in getRowValues()");
      return new float[0];
    }
       
  }	   
  
 /**
  * This method is here to satisfy the interface, no setting is done
  * since values are calculated.
  *
  *  @param values  array of elements to be put into the row
  *  @param row     row number of desired row
  *  @param start   what column number to start at
  */
  public void setRowValues( float[] values, int row, int start )
  {
    // Do nothing since values are calculated.
  }	   
   
 /**
  * Get values for a portion or all of a column.
  * The "from" and "to" values must be direct array reference, i.e.
  * because the array positions start at zero, not one, this must be
  * accounted for. If the array passed in exceeds the bounds of the array, 
  * get values for array elements and ignore extra values.
  *
  *  @param  column  column number of desired column
  *  @param  from    the row number of first element to be altered
  *  @param  to      the row number of the last element to be altered
  *  @return If column, from, and to are valid, an array of floats containing
  *	     the specified section of the row is returned.
  *	     If row, from, or to are invalid, an empty 1-D array is returned.
  */
  public float[] getColumnValues( int column, int from, int to )
  {
    if( from > to )
    {
      // swap them
      int temp = 0;
      temp = to;
      to = from;
      from = temp;
    }
    if( from >= num_rows || to < 0 )
    {  
      SharedMessages.addmsg("Warning - bound exceeds array " +
        				  "in getColumnValues()");
      return new float[0];
    }
    if( from < 0 )
      from = 0;
    if( to >= num_rows )
      to = num_rows - 1;
    if( column < num_columns && column >= 0 )
    {
      float[] value = new float[(to - from + 1)];
      int i = 0;
      while( from != to + 1 )
      {
        value[i] = getDataValue(from,column);
        i++;
        from++;
      }
      return value;
    }
    else
    {  
      SharedMessages.addmsg("Warning - invalid row selection " +
        				  "in setColumnValues()");
      return new float[0];
    }
  }
  
 /**
  * This method is here to satisfy the interface, no setting is done
  * since values are calculated.
  *
  *  @param values  array of elements to be put into the column
  *  @param column  column number of desired column
  *  @param start   what row number to start at
  */
  public void setColumnValues( float[] values, int column, int start )
  {
    // Do nothing since values are calculated.
  }
  
 /**
  * This method is here to satisfy the interface, no setting is done
  * since values are calculated.
  *
  *  @param value  float value that all elements in the array will be set to
  */
  public void setAllValues( float value )
  {
    // Do nothing since values are calculated.
  }
  
 /**
  * Returns the values in the specified region.
  * The vertical dimensions of the region are specified by starting 
  * at first row and ending at the last row. The horizontal dimensions 
  * are determined by the first column and last column.
  *
  * This method also serves as the "getAllofArray". The user may obtain
  * all of the array by specifying getRegionValues(0,END,0,END) where END is 
  * a large integer far exceeding the bounds of the array. The getRegionValues 
  * will adjust the END value to the correct size of the array. Instead of
  * passing a copy of the whole array, a reference to the array will be 
  * passed if all of the array is asked for.
  *
  *  @param  row_start  first row of the region
  *  @param  row_stop	last row of the region
  *  @param  col_start  first column of the region
  *  @param  col_stop	last column of the region
  *  @return If a portion of the array is specified, a 2-D array copy of 
  *	     this portion will be returned. 
  *	     If all of the array is specified, a reference to the actual array
  *	     will be returned.
  */
  public float[][] getRegionValues( int row_start, int row_stop, 
				    int col_start, int col_stop )
  {
    if( row_start > row_stop )
    {
      // swap them
      int temp = 0;
      temp = row_start;
      row_start = row_stop;
      row_stop = temp;
    }
    if( col_start > col_stop )
    {
      // swap them
      int temp = 0;
      temp = col_start;
      col_start = col_stop;
      col_stop = temp;
    }
    if( row_start > num_rows || col_start > num_columns )
    {  
      SharedMessages.addmsg("Warning - bound exceeds array " + 
        				      "in getRegionValues()");
      return new float[0][0];
    }
    if( row_start < 0 )
      row_start = 0;
    if( col_start < 0 )
      col_start = 0;
    if( col_stop >= num_columns )
      col_stop = num_columns - 1;
    if( row_stop >= num_rows )
      row_stop = num_rows - 1;
       
   /*
    * If the following 'if' is true, the user wants the whole array. In this 
    * case give a reference of the private array so another copy is not made.
    * This will prevent overhead when dealing with large arrays.
    * However, this will also give the user access to the array.
    */
    if( col_stop == num_columns - 1 && row_stop == num_rows - 1 && 
        row_start == 0 && col_start == 0 )
    {
      // If dataArray was already created, don't create it. If it has not yet
      // been created, create it.
      if( dataArray != null )
        return dataArray;
      // Build the dataArray.
      float[][] dataArray = new float[num_rows][num_columns];
      for( int row = 0; row < num_rows; row++ )
        for( int col = 0; col < num_columns; col++ )
   	  dataArray[row][col] = getDataValue(row,col);
      return dataArray;
    }
    
   /* 
    * This portion will not be done if the whole array is asked for.
    */
    float[][] region = 
           new float[(row_stop - row_start + 1)][(col_stop - col_start + 1)];
    for( int row = 0; row < (row_stop - row_start + 1); row++ )
      for( int col = 0; col < (col_stop - col_start + 1); col++ )
   	region[row][col] = getDataValue(row+row_start,col+col_start);

    return region;
  }
     
 /**
  * This method is here to satisfy the interface, no setting is done
  * since values are calculated.
  *
  *  @param  values	2-D array of float values 
  *  @param  row_start  first row of the region being altered
  *  @param  col_start  first column of the region being altered
  */
  public void setRegionValues( float[][] values, int row_start, int col_start )
  {
    // Do nothing since values are calculated.
  }
  
 /**
  * Returns number of rows in the array.
  *
  *  @return This returns the number of rows in the array. 
  */ 
  public int getNumRows()
  {
    return num_rows;
  }
  
 /**
  * Returns number of columns in the array.
  *
  *  @return This returns the number of columns in the array. 
  */
  public int getNumColumns()
  {
    return num_columns;
  }
  
 /**
  * Convenience method for getting the dimension of the VirtualArray2D.
  *
  *  @return 2 The array dimension.
  */
  public int getDimension()
  {
    return 2;
  }
  
 /**
  * This will do nothing, square-root errors will be used by default.
  *
  *  @param  error_values The array of error values corresponding to the data.
  *  @return true if data array dimensions match the error array dimensions.
  */
  public boolean setErrors( float[][] error_values )
  {
    // Always use square-root errors
    setSquareRootErrors( true ); // do nothing, use square-root errors.
    return true;
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
  public float[][] getErrors(){ return null; }
  
 /**
  * Use this method to specify whether to use error values that were passed
  * into the setErrors() method or to use the square-root of the data value.
  *
  *  @param  use_sqrt_errs If true, use square-root.
  *                        If false, use set error values if they exist.
  */
  public void setSquareRootErrors( boolean use_sqrt_errs ){}
 
 /**
  * Get an error value for a given row and column. Returns Float.NaN if
  * row or column are invalid.
  *
  *  @param  row Row number.
  *  @param  column Column number.
  *  @return error value for data at [row,column]. If row or column is invalid,
  *          or if setSquareRootErrors() or setErrors is not called,
  *          Float.NaN is returned.
  */
  public float getErrorValue( int row, int column )
  {
    // if not use_sqrt, then return NaN
    return Float.NaN;
  }
  
 /*
  * MAIN - Basic main program to test the VirtualArray2D class
  */
  public static void main( String args[] ) 
  {
    // build test array
    int rows = 10;
    int cols = 8;
    System.out.println("*************************DATA***********************");
    System.out.println("Formula: (3*row+column)/3000 - 2");
    ArrayGenerator test = new ArrayGenerator(rows,cols);
    // create an array with perfect squares.
    for( int i = 0; i < rows; i++ )
    {
      for( int j = 0; j < cols; j++ )
      {
	System.out.print(test.getDataValue(i,j) + "\t");
      }
      System.out.println(); // advance to next line
    }
  } // END OF MAIN  
}
   
