/*
 * File: IBoundsList3D.java
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
 *  Revision 1.2  2005/06/14 14:20:38  cjones
 *  Added 'Modified' line and package statements.
 * 
 */
 
package gov.anl.ipns.ViewTools.Components; 

import gov.anl.ipns.MathTools.Geometry.Vector3D;

/**
 * IBoundsList3D provides bounding information for three dimensional points.
 * Each element of IBoundsList3D will have an extension length for each 
 * direction in 3d space. The default values of the extensions should be zero,
 * making it a single point in space.
 * 
 * To orient the volume surrounding the point, two vectors are kept along
 * with the extension data. These vectors define the newly oriented x and y
 * axes (The third z axis can be calculated with cross product, and the three
 * new axes can be used to construct a transformation matrix). The
 * x, y, z extensions will expand in these user defined directions. 
 * The defaults values for the orientation should be <1,0,0> and
 * <0,1,0>, the normal x and y axes directions for right-handed coordinate
 * system.
 *
 * The implementation must define how values are added or given to the object.
 * 
 * As extensions are added to the array, information on the 
 * maximum and minimum extent values is kept. If a point is removed, the
 * method recalcMaxMinExtents() should be called to lineraly deterimine the
 * max and min extents. If no extentsion information is given on construction,
 * the max extents are set to zero and min extents are set to Float.MAX_VALUE.
 */
public interface IBoundsList3D
{
 /**
  * Get minimum of all x, y, z extents (the smallest
  * extension for each direction.) This is calculated
  * internally as extents are set.  
  *
  *  @return The minimum extents for x, y, z directions.
  */ 
  public Vector3D getMinExtents( );
  
 /**
  * Get maximum of all x, y, z extents (the largest
  * extension for each direction.) This is calculated
  * internally as extents are set. 
  *
  *  @return The maximum extents for x, y, z directions.
  */ 
  public Vector3D getMaxExtents( );
  
 /**
  * This should only be called if the bounds information
  * is reset, and the values for max/min extents needs to be updated. 
  * Max and Min extents are not recalculated automatically when the 
  * max/min extents are changed by a set call.
  */
  public void recalcMaxMinExtents( );
  
 /**
  * Set extents for a single array element at given index.
  * There is an extension for each direction in 3d space.
  *
  *  @param  index   Index of the element.
  *  @param  extents The extents for x, y, and z.
  *  @throws IndexOutOfBoundsException If index is outside of valid
  *          range of indices: 0...NumElements.
  *  @throws IllegalArgumentException If any of the extents are nonnegative.
  */
  public void setExtents( int index, Vector3D extents );
  
 /**
  * Sets extents over specified range of indices.
  * There is an extension for each direction in 3d space.
  *
  *  @param  index_start  First index of the range.
  *  @param  index_end    Last index of the range.
  *  @param  extents      Array of x, y, z extents
  *  @throws IndexOutOfBoundsException If index range goes 
  *          outside of valid  range of indices: 0...NumElements.
  *  @throws IllegalArgumentException If given extents array is 
  *          smaller than specified range or any of the extents are nonnegative.
  */ 
  public void setExtents( int index_start, int index_end,
                          Vector3D[] extents );
  
 /**
  * Set orientation for a single array element at given index.
  * Orientation is set by giving the direction of oriented X
  * axis and direction of oriented Y axis.
  *
  *  @param  index      Index of the element.
  *  @param  x_axis     The direction of rotated X axis.
  *  @param  y_axis     The direction of rotated Y axis.
  *  @throws IndexOutOfBoundsException If index is outside of valid
  *          range of indices: 0...NumElements.
  */
  public void setOrientation( int index, 
                              Vector3D x_axis, 
                              Vector3D y_axis );
  
 /**
  * Sets orientation over specified range of indices by 
  * providing the new directions for x and y axes.
  *
  *  @param  index_start  First index of the range.
  *  @param  index_end    Last index of the range.
  *  @param  x_axes       Array of directions for adjusted
  *                       X axes
  *  @param  y_axes       Array of directions for adjusted
  *                       Y axes
  *  @throws IndexOutOfBoundsException If index range goes 
  *          outside of valid  range of indices: 0...NumElements.
  *  @throws IllegalArgumentException If given extents array is 
  *          smaller than specified range.
  */ 
  public void setOrientations( int index_start, int index_end,
                               Vector3D[] x_axes, 
                               Vector3D[] y_axes );
   
 /**
  * Get extents for a single array element based on index.
  * There is an extension for each direction in 3d space.
  *
  *  @param  index   Index of the element.
  *  @return If element is found, a copy containing 
  *      the x, y, and z extents.
  *          If element is not found, null is returned.
  *  @throws IndexOutOfBoundsException If index is outside of valid
  *          range of indices: 0...NumElements.
  */ 
  public Vector3D getExtents( int index );
  
 /**
  * Returns extents of specified range of indices.
  * There is an extension for each direction in 3d space.
  *
  *  @param  index_start  First index of the range.
  *  @param  index_end    Last index of the range.
  *  @return Copy of portion of array representing
  *      extents for range of indices.
  *  @throws IndexOutOfBoundsException If index range goes 
  *          outside of valid  range of indices: 0...NumElements.
  */ 
  public Vector3D[] getExtents( int index_start, int index_end );

 /**
  * Get orientation for a single array element based on index.
  * It gives the direction of the x and y axes.
  * Example:
  *  Vector3D[] newAxes = getOrientation(index);
  * In above, newAxes[0] represents the rotated X axis
  * vector and newAxes[1] represents the rotated Y axis.
  * The Z axis can be found by cross product of X and Y vectors.
  *
  *  @param  index   Index of the element.
  *  @return If element is found, a copy vectors representing the 
  *      orientation of x and y axes for that index. 
  *          If element is not found, null is returned.
  *  @throws IndexOutOfBoundsException If index is outside of valid
  *          range of indices: 0...NumElements.
  */ 
  public Vector3D[] getOrientation( int index );
   
 /**
  * Returns orientation vectors of specified range of indices.
  * For each index in range specified, there is an X
  * axis direction and an Y axis direction.
  * Example:
  *  Vector3D[][] newAxes = getOrientation(index);
  *   newAxes[index_start][0] is adjusted X axis
  *   newAxes[index_start][1] is adjusted Y axis
  *   ..
  *   newAxes[index_end][0] is adjusted X axis
  *   newAxes[index_end][1] is adjusted Y axis
  * 
  *  @param  index_start  First index of the range.
  *  @param  index_end    Last index of the range.
  *  @return A copy of the portion of Orientation array within the
  *          specified range of indices.
  *  @throws IndexOutOfBoundsException If index range goes 
  *          outside of valid  range of indices: 0...NumElements.
  */ 
  public Vector3D[][] getOrientations( int index_start, int index_end );

 /**
  * Returns X direction vectors of specified range of indices.
  * 
  *  @param  index_start  First index of the range.
  *  @param  index_end    Last index of the range.
  *  @return A copy of the portion of XOrientation array within the
  *          specified range of indices.
  *  @throws IndexOutOfBoundsException If index range goes 
  *          outside of valid  range of indices: 0...NumElements.
  */ 
  public Vector3D[] getXOrientations( int index_start, int index_end );
  
  /**
   * Returns Y direction vectors of specified range of indices.
   * 
   *  @param  index_start  First index of the range.
   *  @param  index_end    Last index of the range.
   *  @return A copy of the portion of YOrientation array within the
   *          specified range of indices.
   *  @throws IndexOutOfBoundsException If index range goes 
   *          outside of valid  range of indices: 0...NumElements.
   */ 
   public Vector3D[] getYOrientations( int index_start, int index_end );
}