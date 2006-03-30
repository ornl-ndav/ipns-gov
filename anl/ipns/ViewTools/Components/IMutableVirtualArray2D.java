/*
 * File:  IMutableVirtualArray2D.java
 *
 * Copyright (C) 2006, Dennis Mikkelson
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
 * Contact : Dennis Mikkelson <mikkelsond@uwstout.edu>
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751, USA
 *
 * This work was supported by the National Science Foundation under grant
 * number DMR-0426797, and by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 * $Log$
 * Revision 1.1  2006/03/30 23:54:35  dennis
 * New interfaces that define the methods for altering the values
 * in a VirtualArray.  These methods were previously part of the
 * IVirtualArray interfaces.  The mutator methods are now being
 * factored out of the IVirtualArray classes, so that the values
 * in an IVirtualArray class are not changeable.  This change has
 * a number of advantages.
 * 1. The basic IVirtualArray concept was intended for passing
 *    information to viewers.  The viewers should not alter the
 *    data, so we now remove methods that could be used to alter
 *    data.
 * 2. The intention was to implement the IVirtualArray concept with
 *    classes that "extract" a regular grid of values from an
 *    underlying more complicated data structure.  It makes no
 *    sense to set values in that context, and it was previously
 *    necessary to include "stub" methods for the mutator methods.
 * 3. Using an immutable virtual array saves memory in the following
 *    way.  A virtual array can be wrapped around a DataSet and the
 *    values used from the underlying DataSet tables of values.
 *    Since the interface doesn't provide mutator methods, there
 *    is no need to make copies of the data.
 *
 */


package gov.anl.ipns.ViewTools.Components;

/**
 * This interface defines methods to set values and error values for elements
 * of an IVirtualArray2D.
 */

public interface IMutableVirtualArray2D extends IVirtualArray2D
{

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
  public void setRowValues( float[] values, int row, int start );


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
  public void setColumnValues( float[] values, int column, int start );


 /**
  * Set value for a single array element.
  *
  *  @param  row     row number of element
  *  @param  column  column number of element
  *  @param  value   value that element will be set to
  */
  public void setDataValue( int row, int column, float value );


 /**  
  * Sets values for a specified rectangular region. This method takes 
  * in a 2D array that is already organized into rows and columns
  * corresponding to a portion of the virtual array that will be altered.
  *
  *  @param  values     2-D array of float values 
  *  @param  row_start  first row of the region being altered
  *  @param  col_start  first column of the region being altered
  */
  public void setRegionValues( float[][] values,
                               int row_start,
                               int col_start );

 /**
  * Set the error values that correspond to the data. The dimensions of the
  * error values array should match the dimensions of the data array. Zeroes
  * will be used to fill undersized error arrays. Values that are in an array
  * that exceeds the data array will be ignored.
  *
  *  @param  error_values The array of error values corresponding to the data.
  *  @return true if data array dimensions match the error array dimensions.
  */
  public boolean setErrors( float[][] error_values );


 /**
  * Use this method to specify whether to use error values that were passed
  * into the setErrors() method or to use the square-root of the data value.
  *
  *  @param  use_sqrt If true, use square-root.
  *                   If false, use set error values if they exist.
  */
  public void setSquareRootErrors( boolean use_sqrt );
 

}
