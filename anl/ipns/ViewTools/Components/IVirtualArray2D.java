/*
 * File: IVirtualArray2D.java
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
 *  Revision 1.3  2003/08/07 15:56:14  dennis
 *  - Added method setAxisInfoVA() with alternate parameters.
 *    Since getAxisInfoVA() returns an AxisInfo2D object, this
 *    new method takes in an AxisInfo2D object.
 *    (Mike Miller)
 *
 *  Revision 1.2  2003/05/16 15:01:54  dennis
 *  Minor fix to java doc comments and added acknowledgement of NSF funding.
 *
 */
 
package DataSetTools.components.View;

import DataSetTools.components.View.TwoD.*;

/**
 * This interface is implemented by classes that can produce a "logical"
 * 2-D array of floats and is used to pass data to viewers and view components.
 * Along with the data, some data attributes are kept in the virtual 
 * array.  An IVirtualArray2D has the same logical format as a typical 2D array.
 * Below is an example of an M x N virtual array.
 *
 * | (0,0)    (0,1)   (0,2)  ...  (0,N-1)  |
 * | (1,0)    (1,1)   (1,2)  ...  (1,N-1)  |
 * | (2,0)    (2,1)   (2,2)  ...  (2,N-1)  |
 * |  ...   ...   ...  ...   ...           |
 * | (M-1,0) (M-1,1) (M-1,2) ... (M-1,N-1) |
 *
 * All references to rows and columns are interpretted
 * to mean row number and column number where an
 * M x N array has M rows, and N columns. The row numbers
 * start at zero and go to M-1 and the column numbers
 * start at zero and go to N-1. 
 */

public interface IVirtualArray2D
{
   public static final String NO_XLABEL = "No X Label";
   public static final String NO_YLABEL = "No Y Label";
   public static final String NO_TITLE  = "No Title";
   public static final String NO_XUNITS = "No X Units";
   public static final String NO_YUNITS = "No X Units";
  /**
   * Returns the attributes of the data array in a AxisInfo2D wrapper.
   * This method will take in a boolean value to determine for which axis
   * info is being retrieved for.    true = X axis, false = Y axis.
   */
   public AxisInfo2D getAxisInfoVA( boolean isX );
   
  /**
   * Sets the attributes of the data array within a AxisInfo2D wrapper.
   * This method will take in a boolean value to determine for which axis
   * info is being altered.          true = X axis, false = Y axis.
   */
   public void setAxisInfoVA( boolean isX, float min, float max,
                              String label, String units, boolean islinear ); 
   
  /**
   * Sets the attributes of the data array within a AxisInfo2D wrapper.
   * This method will take in a boolean value to determine for which axis
   * info is being altered.          true = X axis, false = Y axis.
   */
   public void setAxisInfoVA( boolean isX, AxisInfo2D info );
     
  /**
   * This method will return the title assigned to the data. 
   */
   public String getTitle();
   
  /**
   * This method will assign a title to the data. 
   */
   public void setTitle( String title );
  
  /*
   ***************************************************************************
   * The following methods must include implementation to prevent
   * the user from exceeding the initial array size determined
   * at creation of the array. If an M x N array is specified,
   * the parameters must not exceed (M-1,N-1). 
   ***************************************************************************
   */
   
  /**
   * Get values for a portion or all of a row.
   * The "from" and "to" values must be direct array reference, i.e.
   * because the array positions start at zero, not one, this must be
   * accounted for. If the array passed in exceeds the bounds of the array, 
   * get values for array elements and ignore extra values.
   */
   public float[] getRowValues( int row_number, int from, int to );
   
  /**
   * Set values for a portion or all of a row.
   * The "from" and "to" values must be direct array reference, i.e.
   * because the array positions start at zero, not one, this must be
   * accounted for. If the array passed in exceeds the bounds of the array, 
   * set values for array elements and ignore extra values.
   */
   public void setRowValues( float[] values, int row_number, int start );
   
  /**
   * Get values for a portion or all of a column.
   * The "from" and "to" values must be direct array reference, i.e.
   * because the array positions start at zero, not one, this must be
   * accounted for. If the array passed in exceeds the bounds of the array, 
   * get values for array elements and ignore extra values.
   */
   public float[] getColumnValues( int column_number, int from, int to );
   
  /**
   * Set values for a portion or all of a column.
   * The "from" and "to" values must be direct array reference, i.e.
   * because the array positions start at zero, not one, this must be
   * accounted for. If the array passed in exceeds the bounds of the array, 
   * set values for array elements and ignore extra values.
   */
   public void setColumnValues( float[] values, int column_number, int start );
   
  /**
   * Get value for a single array element.
   */
   public float getDataValue( int row_number, int column_number );
   
  /**
   * Set value for a single array element.
   */
   public void setDataValue( int row_number, int column_number, float value );
      
  /**
   * Set all values in the array to a value. This method will usually
   * serve to "initialize" or zero out the array. 
   */
   public void setAllValues( float value );
   
  /**
   * Returns the values in the specified region.
   * The vertical dimensions of the region are specified by starting 
   * at first row and ending at the last row. The horizontal dimensions 
   * are determined by the first column and last column. 
   */ 
   public float[][] getRegionValues( int first_row, int last_row,
                                     int first_column, int last_column );
  /**  
   * Sets values for a specified rectangular region. This method takes 
   * in a 2D array that is already organized into rows and columns
   * corresponding to a portion of the virtual array that will be altered.
   */
   public void setRegionValues( float[][] values, 
                                int row_number,
				int column_number );
				
  /**
   * Returns number of rows in the array.
   */
   public int getNumRows();

  /**
   * Returns number of columns in the array.
   */   
   public int getNumColumns();
      
}
