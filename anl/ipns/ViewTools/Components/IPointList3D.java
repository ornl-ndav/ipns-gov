/*
 * File: IPointList3D.java
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
 *  Revision 1.5  2005/08/04 22:43:57  cjones
 *  Fixed error in comment header
 *
 *  Revision 1.4  2005/08/04 22:42:31  cjones
 *  Updated comment header and javadocs
 *
 *  Revision 1.3  2005/07/19 15:48:06  cjones
 *  Added 3D Array implementations.
 * 
 */
 
package gov.anl.ipns.ViewTools.Components; 

import gov.anl.ipns.ViewTools.Components.IVirtualArray;
import gov.anl.ipns.MathTools.Geometry.Vector3D;

/**
 * This interface is intended for classes that must store scattered three 
 * dimensional points.  The size of a Array must be given by the constructor,
 * but the values may be set later. For each point in the
 * array, there is an associated integer index.  The range of the index runs
 * from zero to the total number of points the array can hold.
 *
 * If no positions are given at construction, the points are intitalized as
 * Vector3D objects with Float.NaN for the x, y, and z fields. 
 */
public interface IPointList3D extends IVirtualArray 
{  

 /**
  * Returns the number of points in the sparse array.
  *
  * @return The number of points in the array. 
  */
  public int getNumPoints( );
  
 /**
  * Sets 3d point in array at specified index of PointsList.
  *
  *  @param  index   The index of point in array.
  *  @param  point   The 3d coordinates of the point.
  *  @throws IndexOutOfBoundsException If index is outside of valid
  *          range of indices: 0...getNumPoints().
  */
  public void set(int index, Vector3D point );
  
 /**
  * Sets 3d points for each index in range, including the start and end
  * indices. The first value in the points array is the starting value
  * to be set at index_start. Points should have at least a point for each
  * index in given range. Any points beyond this size are ignored.
  *
  *  @param  index_start  First index of the range.
  *  @param  index_end    Last index of the range.
  *  @param  points   The 3d coordinates.
  *  @throws IndexOutOfBoundsException If index is outside of valid
  *          range of indices: 0...getNumPoints().
  *  @throws IllegalArgumentException If given points array is 
  *          smaller than specified range.
  */
  public void set(int index_start, int index_end, Vector3D[] points );
  
 /**
  * Get coordinates for a single array element based on index.
  *
  *  @param  index   Index of the element.
  *  @return If element is found, a copy of coordinates for that
  *          index, (x, y, z). If element is not found, null is returned.
  *  @throws IndexOutOfBoundsException If index is outside of valid
  *          range of indices: 0...getNumPoints().
  */ 
  public Vector3D getPoint( int index );
    
 /**
  * Returns the 3d coordinates of specified range of indices.
  *
  *  @param  index_start  First index of the range.
  *  @param  index_end    Last index of the range.
  *  @return A copy of the Points within the specified
  *          range of indices.
  *  @throws IndexOutOfBoundsException If index range goes outside 
  *          of valid range of indices: 0...getNumPoints().
  */ 
  public Vector3D[] getPoints( int index_start, int index_end );
  
 /**
  * Returns all 3d coordinates in the array.
  *
  *  @return A copy of all the Points.
  */ 
  public Vector3D[] getPoints( );
}
