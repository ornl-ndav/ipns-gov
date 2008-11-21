/*
 * File: VirtualArray2D.java
 *
 * Copyright (C) 2003, Mike Miller
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
 *  Revision 1.16  2006/03/30 23:56:35  dennis
 *  Made some improvements to the clarity of the code.
 *  Getting or setting a region is now done using calls to
 *  methods to get or set partial rows.
 *  This is now a bit more robust if used with ragged arrays.
 *  Ragged arrays are NOT fully supported yet, but don't
 *  immediatlely break things.
 *
 *  Revision 1.15  2004/09/15 21:55:44  millermi
 *  - Updated LINEAR, TRU_LOG, and PSEUDO_LOG setting for AxisInfo class.
 *    Adding a second log required the boolean parameter to be changed
 *    to an int. These changes may affect any ObjectState saved configurations
 *    made prior to this version.
 *
 *  Revision 1.14  2004/05/11 00:53:10  millermi
 *  - Removed unused variables.
 *
 *  Revision 1.13  2004/03/17 20:26:50  dennis
 *  Fixed @see tag that was broken when view components, math and
 *  util were moved to gov package.
 *
 *  Revision 1.12  2004/03/15 23:53:50  dennis
 *  Removed unused imports, after factoring out the View components,
 *  Math and other utils.
 *
 *  Revision 1.11  2004/03/12 02:07:59  millermi
 *  - Changed SharedData to SharedMessages.
 *  _ Changed package and fixed imports.
 *
 *  Revision 1.10  2004/02/16 05:21:37  millermi
 *  - Added methods getErrors(), setErrors(), setSquareRootErrors(),
 *    and getErrorValue() which allow an array of errors to be
 *    associated with the data in that array.
 *
 *  Revision 1.9  2003/12/20 19:37:33  millermi
 *  - Added axisinfo for value axis.
 *
 *  Revision 1.8  2003/12/20 03:35:41  millermi
 *  - changed comments and code on how Float.NaN is checked.
 *
 *  Revision 1.7  2003/12/18 22:34:33  millermi
 *  - get/setAxisInfoVA() changed to get/setAxisInfo() with first parameter
 *    int instead of boolean to make more general.
 *  - All references to AxisInfo2D changed to AxisInfo.
 *
 *  Revision 1.6  2003/12/12 18:33:04  millermi
 *  - Changed initialization of AxisInfo max from 1 to 10,
 *    the value of 1 interferred with the axisoverlay.
 *
 *  Revision 1.5  2003/10/22 20:16:16  millermi
 *  - Added method getDimension() which is now required
 *    since the IVirtualArray2D now extends IVirtualArray.
 *
 *  Revision 1.4  2003/10/16 05:00:03  millermi
 *  - Fixed java docs errors.
 *
 *  Revision 1.3  2003/08/07 15:57:04  dennis
 *  - Added method and implementation of setAxisInfoVA() with
 *    alternate parameters.  Since getAxisInfoVA() returns an
 *    AxisInfo2D object, this new method takes in an AxisInfo2D
 *    object.
 *    (Mike Miller)
 *
 *  Revision 1.2  2003/05/16 15:01:54  dennis
 *  Minor fix to java doc comments and added acknowledgement of NSF funding.
 *
 */

package gov.anl.ipns.ViewTools.Components;

import java.io.Serializable;

import gov.anl.ipns.Util.Sys.SharedMessages;

/**
 * This class puts a wrapper around a 2-D array of floats. It can be 
 * used to pass data to viewers and view components. Along with the data,
 * the data attributes are kept within the virtual array.
 *
 * Assumptions: All row/column values passed are assumed to be in array
 * format. That is, an M x N array has M rows and N columns, but the rows
 * are numbered 0 - M-1 and the columns are numbered 0 - N-1. 
 */

public class VirtualArray2D implements IMutableVirtualArray2D, Serializable
{
  // data members
  private float[][] dataArray;
  private int num_rows; 	     // In M x N array, stores M
  private int num_columns;	     // In M x N array, stores N
  private AxisInfo rowinfo;
  private AxisInfo colinfo;
  private AxisInfo datainfo;
  private String title;
  private float[][] errorArray;      // array of error values.
  private boolean errors_set;        // has setErrors() method been called.
  private boolean use_sqrt;          // should square-root errors be used.

   
 /**
  * Constructor that allows the user to enter dimensions M,N and
  * creates a new M x N virtual array containing all zeros.
  *
  *  @param  rows  Specify the number of rows in the virtual array
  *  @param  columns  Specify the number of columns in the virtual array.
  */
  public VirtualArray2D( int rows, int columns )
  {
    dataArray = new float[rows][columns];
    errors_set = false;
    use_sqrt = false;
    /* Initialize Array to all zeros */
    this.setAllValues(0);
      
    num_rows = rows;
    num_columns = columns;
    rowinfo = new AxisInfo(0, 1, AxisInfo.NO_LABEL,
                           AxisInfo.NO_UNITS, AxisInfo.LINEAR);
    colinfo = new AxisInfo(0, 1, AxisInfo.NO_LABEL,
                           AxisInfo.NO_UNITS, AxisInfo.LINEAR);
    datainfo = new AxisInfo(0, 1, AxisInfo.NO_LABEL,
                            AxisInfo.NO_UNITS, AxisInfo.LINEAR);
    title = NO_TITLE;
  }


 /**
  * Constructor that allows the user to pass in a 2D array of float
  * to be made into a new virtual array.
  *
  *  @param  array2d  2-D array of floats put into a virtual array shell
  */  
  public VirtualArray2D( float[][] array2d )
  {
    if ( array2d           == null  ||
         array2d.length    <= 0     ||
         array2d[0]        == null  || 
         array2d[0].length <= 0      )
    {
      dataArray = new float[0][0];
      num_rows = 0;
      num_columns = 0;
    }
    else
    {
      dataArray = array2d;
      num_columns = array2d[0].length;
      num_rows = array2d.length;
    }

    errors_set = false;
    use_sqrt = false;
    rowinfo = new AxisInfo(0, 1, AxisInfo.NO_LABEL,
                           AxisInfo.NO_UNITS, AxisInfo.LINEAR);
    colinfo = new AxisInfo(0, 1, AxisInfo.NO_LABEL,
                           AxisInfo.NO_UNITS, AxisInfo.LINEAR);
    datainfo = new AxisInfo(0, 1, AxisInfo.NO_LABEL,
                            AxisInfo.NO_UNITS, AxisInfo.LINEAR);
    title = NO_TITLE;
  }


 /**
  * Constructor that allows the user to pass in a 2D array of float
  * to be made into a new virtual array and a 2D array of error values
  * corresponding to the values in the first parameter.
  *
  *  @param  array2d  2-D array of floats put into a virtual array shell
  *  @param  errs     2-D array of error values. If the dimension of this
  *                   array is different from the values, the dimension
  *                   of this array will be modified to equal size.  
  */  
  public VirtualArray2D( float[][] array2d, float[][] errs )
  {
    this(array2d);
    setErrors(errs);
  }	 


/* 
 * The following methods allow the user to attach meaningful discription
 * to the values stored in the virtual array.
 * An object AxisInfo contains information about a particular axis.
 */

 /**
  * Returns the attributes of the data array in a AxisInfo wrapper.
  * This method will take in a boolean value to determine for which axis
  * info is being retrieved for. 
  *
  *  @param  axiscode The integer code for this axis.
  *  @return If true, AxisInfo object with X axis info is returned.
  *	         If false, AxisInfo object with Y axis info is returned.
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
    if(    row >= 0  &&     row < num_rows   && 
        column >= 0  &&  column < dataArray[row].length )
      return dataArray[row][column];
    else
    {
      //SharedMessages.addmsg("Warning - cell at position (" +
      //  	      row + "," + column + ") exceeds the array bounds." );
      return Float.NaN;
    }
  }  

  
 /**
  * Set value for a single array element.
  *
  *  @param  row     row number of element
  *  @param  column  column number of element
  *  @param  value   value that element will be set to
  */
  public void setDataValue( int row, int column, float value )
  {
    if(    row >= 0  &&     row < num_rows   &&
        column >= 0  &&  column < dataArray[row].length )
      dataArray[row][column] = value;
    else
    {
      SharedMessages.addmsg("Warning - cell at position (" +
        		 row + "," + column + ") exceeds the array bounds." );
      return;
    }
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
    if ( row < 0 || row >= num_rows )
    {
      SharedMessages.addmsg("Warning - invalid row " +
                            "selection in getRowValues()");
      return new float[0];
    }  

    if( from > to )
    {
      // swap them
      int temp = 0;
      temp = to;
      to = from;
      from = temp;
    }	   

    if( from >= dataArray[row].length || to < 0 )
    {  
      SharedMessages.addmsg("Warning - bound exceeds array in getRowValues()");
      return new float[0];
    }

    if( from < 0 )
      from = 0;

    if( to >= dataArray[row].length )
      to = dataArray[row].length - 1;

    float[] value = new float[(to - from + 1)];
    int i = 0;
    while( from <= to )
    {
      value[i] = dataArray[row][from];
      i++;
      from++;
    }
    return value;
  }	   
  

 /**
  * Set values for a portion or all of a row.
  * The "from" and "to" values must be direct array reference, i.e.
  * because the array positions start at zero, not one, this must be
  * accounted for. If the array passed in exceeds the bounds of the array, 
  * set values for array elements and ignore extra values.
  *
  *  @param values  array of elements to be put into the row
  *  @param row     row number of desired row
  *  @param start   what column number to start at
  */
  public void setRowValues( float[] values, int row, int start )
  {
    if ( row < 0 || row >= num_rows )
    {
      SharedMessages.addmsg("Warning - invalid row " +
                            "selection in setRowValues()");
      return;
    }

    if( (start + values.length) > dataArray[row].length )  
      SharedMessages.addmsg("Warning - bound exceeds array in setRowValues()");

    int i = 0;
    while( i < values.length && start < dataArray[row].length )
    {
      dataArray[row][start] = values[i];
      i++;
      start++;
    }
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
        value[i] = dataArray[from][column];
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
  * Set values for a portion or all of a column.
  * The "from" and "to" values must be direct array reference, i.e.
  * because the array positions start at zero, not one, this must be
  * accounted for. If the array passed in exceeds the bounds of the array, 
  * set values for array elements and ignore extra values.
  *
  *  @param values  array of elements to be put into the column
  *  @param column  column number of desired column
  *  @param start   what row number to start at
  */
  public void setColumnValues( float[] values, int column, int start )
  {
    if( (start + values.length) > num_rows )
    {
      SharedMessages.addmsg("Warning - bound exceeds array " +
   					    "in setColumnValues()");
    }
    if( column < num_columns && column >= 0 )
    {
      int i = 0;
      while( i < values.length && start < num_rows )
      {
        dataArray[start][column] = values[i];
        i++;
        start++;
      }
    }
    else
    {  
      SharedMessages.addmsg("Warning - invalid row selection " +
        				    "in setColumnValues()");
      return;
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
    for( int row = 0; row < num_rows; row++ )
      for( int col = 0; col < dataArray[row].length; col++ )
        dataArray[row][col] = value;
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
     /*  Unneeded warning message
      SharedMessages.addmsg("Warning - You have been given a reference to the "+
      "array, not a copy. Altering values in this array will permanently " +
      "change these values.");
      */
      return dataArray;
    }
    
   /* 
    * This portion will not be done if the whole array is asked for.
    */
    float[][] region = new float[(row_stop - row_start + 1)][];
    for( int row = 0; row < region.length; row++ )
      region[row] = getRowValues( row + row_start, col_start, col_stop );

    return region;
  }

     
 /**  
  * Sets values for a specified rectangular region. This method takes 
  * in a 2D array that is already organized into rows and columns
  * corresponding to a portion of the virtual array that will be altered.
  *
  *  @param  values	2-D array of float values 
  *  @param  row_start  first row of the region being altered
  *  @param  col_start  first column of the region being altered
  */
  public void setRegionValues( float[][] values, int row_start, int col_start )
  {
    if( col_start + values[0].length > num_columns || 
        row_start + values.length > num_rows )
    {  
      SharedMessages.addmsg("Warning - bound exceeds array " + 
    					       "in getColumnValues()");
    }

    if( row_start < 0 )
      row_start = 0;

    if( col_start < 0 )
      col_start = 0;

    // allows user to choose values exceeding the array bounds, but only
    // changes values of legal positions

    int row = 0;
    while( row < values.length && (row+row_start) < num_rows )
    {
      setRowValues( values[row], row + row_start, col_start );
      row++;
    }
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
  * Set the error values that correspond to the data. The dimensions of the
  * error values array should match the dimensions of the data array. Zeroes
  * will be used to fill undersized error arrays. Values that are in an array
  * that exceeds the data array will be ignored.
  *
  *  @param  error_values The array of error values corresponding to the data.
  *  @return true if data array dimensions match the error array dimensions.
  */
  public boolean setErrors( float[][] error_values )
  {
    errors_set = true;
    // by setting these values, do not use the calculated
    // square-root errors
    setSquareRootErrors( false );
    
    // Check to see if error values array is same size as data array.
    // If so, reference the array passed in.
    if( error_values.length == getNumRows() &&
        error_values[0].length == getNumColumns() )
    {
      errorArray = error_values;
      return true;
    }
    // If dimensions are not equal, copy values that are valid into an array
    // the same size as the data.
    else
    {
      errorArray = new float[getNumRows()][getNumColumns()];
      // If error_values is too large, the extra values are ignored
      // by these "for" loops. If too small, the zeroes are inserted.
      for( int row = 0; row < getNumRows(); row++ )
      {
        for( int col = 0; col < getNumColumns(); col++ )
	{
	  if( row >= error_values.length || col >= error_values[0].length )
	  {
	    errorArray[row][col] = 0;
	  }
	  else
	  {
	    errorArray[row][col] = error_values[row][col];
	  }
	}
      }
      return false;
    }
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
  public float[][] getErrors()
  {
    // if setSquareRootErrors(true) was called
    if( use_sqrt )
    {
      float[][] sqrt_errors = new float[getNumRows()][getNumColumns()];
      for( int row = 0; row < getNumRows(); row++ )
      {
        for( int col = 0; col < getNumColumns(); col++ )
        {
          sqrt_errors[row][col] = (float)
	            Math.sqrt( (double)Math.abs( getDataValue(row,col) ) );
        }
      }
      return sqrt_errors;
    }
    // if the errors were set using the setErrors() method
    if( errors_set )
      return errorArray;
    // if neither use_sqrt nor errors_set, return null.
    return null;
  }

  
 /**
  * Use this method to specify whether to use error values that were passed
  * into the setErrors() method or to use the square-root of the data value.
  *
  *  @param  use_sqrt_errs If true, use square-root.
  *                        If false, use set error values if they exist.
  */
  public void setSquareRootErrors( boolean use_sqrt_errs )
  {
    use_sqrt = use_sqrt_errs;
  }

 
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
    // make sure row/column are valid values.
    if( row >= getNumRows() || column >= getNumColumns() )
      return Float.NaN;
    // return sqrt error value if specified.
    if( use_sqrt )
      return (float)Math.sqrt( (double)Math.abs( getDataValue(row,column) ) );
    // if the errors were set using the setErrors() method, return them
    if( errors_set )
      return errorArray[row][column];
    // if neither use_sqrt or errors_set, then return NaN
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
    float[][] array = new float[rows][cols];
    System.out.println("*************************DATA***********************");
    // create an array with perfect squares.
    for( int i = 0; i < rows; i++ )
    {
      for( int j = 0; j < cols; j++ )
      {
        array[i][j] = (float)(i*i);
        // negate some values.
	if( i == j )
	  array[i][j] = -array[i][j];
	System.out.print(array[i][j] + "\t");
      }
      System.out.println(); // advance to next line
    }
    VirtualArray2D test = new VirtualArray2D(array);
    System.out.println("************************ERRORS**********************");
    // null since neither setErrors() nor setSquareRootErrors() were called.
    float[][] errs = test.getErrors();
    System.out.println(errs);
    System.out.println("*********************SET ERRORS*********************");
    float[][] new_errs = new float[test.getNumRows()+2][test.getNumColumns()-2];
    for( int i = 0; i < rows+2; i++ ) // too many rows
    {
      for( int j = 0; j < cols-2; j++ ) // too few columns
      {
        new_errs[i][j] = 1;  // make all errors the same.
      }
    }
    test.setErrors(new_errs);
    errs = test.getErrors();
    for( int i = 0; i < rows; i++ )
    {
      for( int j = 0; j < cols; j++ )
      {
	System.out.print(errs[i][j] + "\t");
      }
      System.out.println(); // advance to next line
    }
    // test setSquareRootErrors()
    test.setSquareRootErrors(true);
    System.out.println("*********************SQRT ERRORS********************");
    errs = test.getErrors();
    for( int i = 0; i < rows; i++ )
    {
      for( int j = 0; j < cols; j++ )
      {
	System.out.print(errs[i][j] + "\t");
      }
      System.out.println(); // advance to next line
    }
  } // END OF MAIN  
}
   
