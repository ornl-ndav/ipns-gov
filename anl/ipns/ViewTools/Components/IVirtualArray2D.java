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
 *  Revision 1.13  2006/03/30 23:57:55  dennis
 *  Modified to not require the use of mutator methods for the
 *  virtual arrays.  These changes were required since the concept
 *  of a "mutable" virtual array was separated from the concept of
 *  a virtual array.
 *
 *  Revision 1.12  2005/06/06 14:33:05  dennis
 *  Removed method declaration, setAxisInfo( i, axis_info ), since that
 *  method was moved to the IVirtualArray class.
 *
 *  Revision 1.11  2005/03/12 18:09:52  millermi
 *  - Updated javadocs for getDataValue().
 *
 *  Revision 1.10  2004/09/15 21:55:44  millermi
 *  - Updated LINEAR, TRU_LOG, and PSEUDO_LOG setting for AxisInfo class.
 *    Adding a second log required the boolean parameter to be changed
 *    to an int. These changes may affect any ObjectState saved configurations
 *    made prior to this version.
 *
 *  Revision 1.9  2004/03/17 20:26:50  dennis
 *  Fixed @see tag that was broken when view components, math and
 *  util were moved to gov package.
 *
 *  Revision 1.8  2004/03/15 23:53:50  dennis
 *  Removed unused imports, after factoring out the View components,
 *  Math and other utils.
 *
 *  Revision 1.7  2004/03/12 02:07:59  millermi
 *  - Changed SharedData to SharedMessages.
 *  - Changed package and fixed imports.
 *
 *  Revision 1.6  2004/02/16 05:21:38  millermi
 *  - Added methods getErrors(), setErrors(), setSquareRootErrors(),
 *    and getErrorValue() which allow an array of errors to be
 *    associated with the data in that array.
 *
 *  Revision 1.5  2003/12/18 22:33:50  millermi
 *  - Removed getAxisInfoVA(), now done in IVirtualArray.
 *  - All references to AxisInfo2D changed to AxisInfo.
 *
 *  Revision 1.4  2003/10/22 20:15:08  millermi
 *  - Removed methods now defined by IVirtualArray.
 *  - Added java docs where needed.
 *
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
 
package gov.anl.ipns.ViewTools.Components;

/**
 * This interface is implemented by classes that can produce a "logical"
 * 2-D array of floats and is used to pass data to viewers and view components.
 * Along with the data, some information, such as at title, and axis labels 
 * and units, are kept with the virtual array.  An IVirtualArray2D has the 
 * same logical format as a typical 2D array.
 * Below is an example of an M x N virtual array.<br><br>
 *
 * | (0,0)    (0,1)   (0,2)  ...  (0,N-1)  |<br>
 * | (1,0)    (1,1)   (1,2)  ...  (1,N-1)  |<br>
 * | (2,0)    (2,1)   (2,2)  ...  (2,N-1)  |<br>
 * |  ...   ...   ...  ...   ...           |<br>
 * | (M-1,0) (M-1,1) (M-1,2) ... (M-1,N-1) |<br>
 *
 * All references to rows and columns are interpretted
 * to mean row number and column number where an
 * M x N array has M rows, and N columns. The row numbers
 * start at zero and go to M-1 and the column numbers
 * start at zero and go to N-1. 
 *
 * In the current version of IVirtualArray2D, the values of the array cannot 
 * be set.  This is to allow the interface to be implemented by classes that
 * calculate or extract a regular array of values from some more complicated
 * underlying data structures.  In such cases, it is not meaningiful to set
 * the values, just to get them.  IF the values must be set, then the 
 * IMutableVirtualArray2D interface should be implemented.
 *
 *  @see gov.anl.ipns.ViewTools.Components.IMutableVirtualArray2D
 *  @see gov.anl.ipns.ViewTools.Components.AxisInfo
 */

public interface IVirtualArray2D extends IVirtualArray
{  

 /**
  * Sets the attributes of the data array within a AxisInfo wrapper.
  * This method will take in an integer to determine which axis
  * info is being altered.
  *
  *  @param  axis Use AxisInfo.X_AXIS (0) or AxisInfo.Y_AXIS (1).
  *  @param  min Minimum value for this axis.
  *  @param  max Maximum value for this axis.
  *  @param  label Label associated with the axis.
  *  @param  units Units associated with the values for this axis.
  *  @param  scale Is axis LINEAR, TRU_LOG, PSEUDO_LOG?
  */
  public void setAxisInfo( int axis, float min, float max,
			   String label, String units, int scale ); 
  

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
  *
  *  @param  row   the row number being altered
  *  @param  from  the column number of first element to be altered
  *  @param  to    the column number of the last element to be altered
  *  @return If row, from, and to are valid, an array of floats containing
  *	     the specified section of the row is returned.
  *	     If row, from, or to are invalid, an empty 1-D array is returned.
  */
  public float[] getRowValues( int row, int from, int to );
  

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
  public float[] getColumnValues( int column, int from, int to );
  
  
 /**
  * Get value for a single array element.
  *
  *  @param  row     row number of element
  *  @param  column  column number of element
  *  @return If element is found, the float value for that element is returned.
  *	     If element is not found, Float.NaN is returned.
  */ 
  public float getDataValue( int row, int column );
  
  
 /**
  * Returns the values in the specified region.
  * The vertical dimensions of the region are specified by starting 
  * at first row and ending at the last row. The horizontal dimensions 
  * are determined by the first column and last column. 
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
                                    int col_start, int col_stop );


 /**
  * Returns number of rows in the array.
  *
  *  @return This returns the number of rows in the array. 
  */ 
  public int getNumRows();


 /**
  * Returns number of columns in the array.
  *
  *  @return This returns the number of columns in the array. 
  */
  public int getNumColumns();

  
 /**
  * Get the error values corresponding to the data. If no error values have
  * been set, the square-root of the data value will be returned.
  *
  *  @return error values of the data.
  */
  public float[][] getErrors();

  
 /**
  * Get an error value for a given row and column. Returns Float.NaN if
  * row or column are invalid.
  *
  *  @param  row Row number.
  *  @param  column Column number.
  *  @return error value for data at [row,column]. If row or column is invalid,
  *          Float.NaN is returned.
  */
  public float getErrorValue( int row, int column ); 

}
