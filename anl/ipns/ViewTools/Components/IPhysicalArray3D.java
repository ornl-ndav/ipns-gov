/*
 * File: IPhysicalArray3D.java
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
 *  Revision 1.2  2005/06/14 14:20:40  cjones
 *  Added 'Modified' line and package statements.
 * 
 */

package gov.anl.ipns.ViewTools.Components; 

import gov.anl.ipns.ViewTools.Components.AxisInfo;
import gov.anl.ipns.MathTools.Geometry.Vector3D;

/**
 * This interface extends the ISparseArray3D and IBoundsList3D interfaces 
 * to add information about each point's three dimensional volume surrounding
 * the point. 
 * 
 * For each scattered point in the array, IPhysicalArray3D will hold an 
 * extension width for each direction in 3d space.  The point will be
 * the center of each extent, making it the center of a rectangular
 * prism. The intial values of the extensions will be set to zero
 * (making it a single point in space) if extent data is not given to the
 * constructor.
 * 
 * To orient the volume surrounding the point, two vectors are kept along
 * with the extension data. These vectors define the "rotated" x and y
 * axes (The third z axis will be calculated with cross product). The
 * x, y, z extensions will expand in these user defined directions. 
 * The intial values for the orientation will be set to <1,0,0> and
 * <0,1,0> (the normal x and y axes directions) if orientation data is not
 * given to the constructor. 
 * 
 * As extensions are set in the array, information on the 
 * maximum and minimum extent values is kept and the maximum
 * and minimum area for the entire 3D volume (point + exents). If
 * bound data is reset and the max and min edges change, the method
 * recalcMaxMinEdges() should be called to linearly determine the volume edges.
 * This allows the user of the array to define a 3d volume
 * large enough to accommodate all the rectangular prisms. 
 * 
 * It is assumed that none of the volumes will overlap, ie the
 * extensions of one point will not go into the extension of 
 * another point. 
 */
public interface IPhysicalArray3D extends ISparseArray3D, IBoundsList3D 
{
 /** 
  * Set value for a single array element with given point.
  *
  *  @param  index   The index of point in array.
  *  @param  point   The 3d coordinates of the point.
  *  @param  extents The extents for x, y, and z.
  *  @param  x_axis  The direction of rotated X axis.
  *  @param  y_axis  The direction of rotated Y axis.
  *  @param  value   Value that element will be set to.
  *  @throws IndexOutOfBoundsException If index range goes outside 
  *          of valid range of indices: 0...getNumPoints().
  */
  public void set( int index, Vector3D point,
                  Vector3D extents, 
                  Vector3D x_axis, 
                  Vector3D y_axis, 
                  float value);
	  
 /**
  * Add 3d point to array. The value of point is intialized
  * to be Float.Nan
  *
  *  @param  index   The index of point in array.
  *  @param  point   The 3d coordinates of the point.
  *  @param  extents The extents for x, y, and z.
  *  @param  x_axis  The direction of rotated X axis.
  *  @param  y_axis  The direction of rotated Y axis.
  *  @throws IndexOutOfBoundsException If index range goes outside 
  *          of valid range of indices: 0...getNumPoints().
  */
  public void set( int index, Vector3D point,
                  Vector3D extents, 
                  Vector3D x_axis, 
                  Vector3D y_axis);
	
 /**
  * Get minimum distance from the origin for each
  * direction x, y, and z. The distance is deterimined
  * to be the minimum of position of each coordinate plus the
  * extension in the direction for that point.  Along
  * with get getMaxEdges, the "volume" that the data
  * points and extensions will need can be determined.
  * This is calculated internally when bounds information is set. 
  *
  *  @return The minimum edges for x, y, z directions.
  */ 
  public Vector3D getMinEdges( );

 /**
  * Get maximum distance from the origin for each
  * direction x, y, and z. The distance is deterimined
  * to be the maximum of position on of each coordinate plus the
  * extension in the direction for that point. Along
  * with get getMinEdges, the "volume" that the data
  * points and extensions will need can be determined.
  * This is calculated internally when bounds information is set.
  *
  *  @return The maximum edges for x, y, z directions.
  */ 
  public Vector3D getMaxEdges( );	
  
 /**
  * This should only be called if bounds information
  * is reset, and the values for edges need to be updated. Max and Min
  * edges are not recalculated automatically if the outer edge is shrunk
  * by a set call.
  */ 
  public void recalcMaxMinEdges( );
}