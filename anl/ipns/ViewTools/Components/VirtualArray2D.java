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

package DataSetTools.components.View;

import DataSetTools.components.View.TwoD.*;
import DataSetTools.util.*;

/**
 * This class puts a wrapper around a 2-D array of floats. It can be 
 * used to pass data to viewers and view components. Along with the data,
 * the data attributes are kept within the virtual array.
 *
 * Assumptions: All row/column values passed are assumed to be in array
 * format. That is, an M x N array has M rows and N columns, but the rows
 * are numbered 0 - M-1 and the columns are numbered 0 - N-1. 
 */

public class VirtualArray2D implements IVirtualArray2D
{
   // data members
   private float[][] dataArray;
   private int num_rows;              // In M x N array, stores M				      				     
   private int num_columns;           // In M x N array, stores N
   private AxisInfo2D rowinfo;
   private AxisInfo2D colinfo;
   private String title;
    
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
      
      /* Initialize Array to all zeros */
      this.setAllValues(0);
	    
      num_rows = rows;
      num_columns = columns;
      rowinfo = new AxisInfo2D(0, 1, NO_XLABEL, NO_XUNITS, true);
      colinfo = new AxisInfo2D(0, 1, NO_YLABEL, NO_YUNITS, true);
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
      dataArray = array2d;
      num_columns = array2d[0].length;
      num_rows = array2d.length;
      rowinfo = new AxisInfo2D(0,1, NO_XLABEL, NO_XUNITS, true);
      colinfo = new AxisInfo2D(0,1, NO_YLABEL, NO_YUNITS, true);
      title = NO_TITLE;
   }      

/* 
 * The following methods allow the user to attach meaningful discription
 * to the values stored in the virtual array.
 * An object AxisInfo2D contains information about a particular axis.
 */
 
  /**
   * Returns the attributes of the data array in a AxisInfo2D wrapper.
   * This method will take in a boolean value to determine for which axis
   * info is being retrieved for. 
   *
   *  @param  isX
   *  @return If true, AxisInfo2D object with X axis info is returned.
   ,          If false, AxisInfo2D object with Y axis info is returned.
   */
   public AxisInfo2D getAxisInfoVA( boolean isX )
   {
      if(isX)
         return rowinfo;
      return colinfo;
   }
   
  /**
   * Sets the attributes of the data array within a AxisInfo2D wrapper.
   * This method will take in a boolean value to determine for which axis
   * info is being altered.          true = X axis, false = Y axis.
   *
   *  @param  isX
   *  @param  min
   *  @param  max
   *  @param  label
   *  @param  units
   *  @param  islinear
   */
   public void setAxisInfoVA( boolean isX, float min, float max,
                              String label, String units, boolean islinear)
   {
      if(isX)
         rowinfo = new AxisInfo2D(min,max,label,units, islinear);
      else
         colinfo = new AxisInfo2D(min,max,label,units, islinear);
   } 
   
  /**
   * Sets the attributes of the data array within a AxisInfo2D wrapper.
   * This method will take in a boolean value to determine for which axis
   * info is being altered.          true = X axis, false = Y axis.
   *
   *  @param  isX
   *  @param  info - axis info
   */
   public void setAxisInfoVA( boolean isX, AxisInfo2D info )
   {
      if(isX)
         rowinfo = info.copy();
      else
         colinfo = info.copy();
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
   * Get value for a single array element.
   *
   *  @param  row     row number of element
   *  @param  column  column number of element
   *  @return If element is found, the float value for that element is returned.
   *          If element is not found, zero is returned.
   */ 
   public float getDataValue( int row, int column )
   {
      if( row < num_rows && column < num_columns && row >= 0 && column >= 0)
         return dataArray[row][column];
      else
      {
         SharedData.addmsg("Warning - cell at position (" + row + "," + column +
	                                   ") exceeds the array bounds." );
         return 0f;
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
      if( row < num_rows && column < num_columns && row >= 0 && column >= 0)
         dataArray[row][column] = value;
      else
      {
         SharedData.addmsg("Warning - cell at position (" + row + "," + column +
	                                    ") exceeds the array bounds." );
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
   *          the specified section of the row is returned.
   *          If row, from, or to are invalid, an empty 1-D array is returned.
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
         SharedData.addmsg("Warning - bound exceeds array in getRowValues()");
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
	    value[i] = dataArray[row][from];
	    i++;
	    from++;
	 }
	 return value;
      }
      else
      {  
         SharedData.addmsg("Warning - invalid row selection in getRowValues()");
         return new float[0];
      }
	 
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
      if( (start + values.length) > num_columns )  
         SharedData.addmsg("Warning - bound exceeds array in setRowValues()");
      if( row < num_rows && row >= 0 )
      {
	 int i = 0;
	 while( i < values.length && start < num_columns)
	 {
	    dataArray[row][start] = values[i];
	    i++;
	    start++;
	 }
      }
      else
      {  
         SharedData.addmsg("Warning - invalid row selection in setRowValues()");
         return;
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
   *          the specified section of the row is returned.
   *          If row, from, or to are invalid, an empty 1-D array is returned.
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
         SharedData.addmsg("Warning - bound exceeds array " +
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
         SharedData.addmsg("Warning - invalid row selection " +
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
         SharedData.addmsg("Warning - bound exceeds array " +
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
         SharedData.addmsg("Warning - invalid row selection " +
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
      for( int col = 0; col < num_rows; col++ )
         for( int row = 0; row < num_columns; row++ )
	    dataArray[col][row] = value;
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
   *  @param  row_stop   last row of the region
   *  @param  col_start  first column of the region
   *  @param  col_stop   last column of the region
   *  @return If a portion of the array is specified, a 2-D array copy of 
   *          this portion will be returned. 
   *          If all of the array is specified, a reference to the actual array
   *          will be returned.
   */
   public float[][] getRegionValues( int row_start, int row_stop, 
                                     int col_start, int col_stop )
   {
      boolean isAll = false;
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
         SharedData.addmsg("Warning - bound exceeds array " + 
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
         SharedData.addmsg("Warning - You have been given a reference to the " +
	 "array, not a copy. Altering values in this array will permanently " +
	 "change these values.");
	 */
         return dataArray;
      }
      
     /* 
      * This portion will not be done if the whole array is asked for.
      */
      float[][] region = 
             new float[(row_stop - row_start + 1)][(col_stop - col_start + 1)];
      for( int row = 0; row < (row_stop - row_start + 1); row++ )
         for( int col = 0; col < (col_stop - col_start + 1); col++ )
	    region[row][col] = dataArray[row+row_start][col+col_start];
	
      return region;
   }
      
  /**  
   * Sets values for a specified rectangular region. This method takes 
   * in a 2D array that is already organized into rows and columns
   * corresponding to a portion of the virtual array that will be altered.
   *
   *  @param  values     2-D array of float values 
   *  @param  row_start  first row of the region being altered
   *  @param  col_start  first column of the region being altered
   */
   public void setRegionValues( float[][] values, int row_start, int col_start )
   {
      if( col_start + values[0].length > num_columns || 
          row_start + values.length > num_rows )
      {  
         SharedData.addmsg("Warning - bound exceeds array " + 
	                                         "in getColumnValues()");
      }
      if( row_start < 0 )
         row_start = 0;
      if( col_start < 0 )
         col_start = 0;
      int row = 0;
      int col = 0;
      // allows user to choose values exceeding the array bounds, but only
      // changes values of legal positions
      while( row < values.length && (row+row_start) < num_rows )
      {
         while( col < values[0].length && (col+col_start) < num_columns )
	 {
	    dataArray[row+row_start][col+col_start] = values[row][col];
	    col++;
	 }
	 col = 0;
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
   
}
   
