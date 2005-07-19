/*
 * File: BoundsList3D.java
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
 *  Revision 1.1  2005/07/19 15:48:04  cjones
 *  Added 3D Array implementations.
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
public class BoundsList3D implements IBoundsList3D
{
  // Data
  private Vector3D[] extents_list;  // Extent information. Vector3D for each.
  private Vector3D[] x_axes_list;   // Direction of x axis. Vector3D for each.
  private Vector3D[] y_axes_list;   // Direction of y axis. Vector3D for each.

  private int numbounds;            // Total number of bounds info, Extents & Orient
  
  // Data info
  private float[] minextents;
  private float[] maxextents;
  
  
 /**
  * Constructor that takes number of bounds information. 
  * All data is intialized with default values, and has to be
  * set later.
  *
  *  @param totalbounds Number of bounds object will hold.
  */
  public BoundsList3D( int totalbounds )
  {
    numbounds = totalbounds;
    
    extents_list = new Vector3D[numbounds];
    x_axes_list = new Vector3D[numbounds];
    y_axes_list = new Vector3D[numbounds];
    
    for(int i = 0; i < numbounds; i++)
    {
      extents_list[i] = new Vector3D(0.0f, 0.0f, 0.0f);
      x_axes_list[i] = new Vector3D(1.0f, 0.0f, 0.0f);
      y_axes_list[i] = new Vector3D(0.0f, 1.0f, 0.0f); 
    }
    
    minextents = new float[3];
    maxextents = new float[3];
    for(int i = 0; i < 3; i++) {
      minextents[i] = Float.MAX_VALUE;
      maxextents[i] = 0f; 
    }
  }
  
 /**
  * Constructor that takes number of bounds information. 
  * All data is intialized with default values, and has to be
  * set later. For arrays that are equal to or larger than 'totalbounds', it will
  * only set the first 'totalbounds' number of values. For array
  * smaller than 'totalbounds', it will set the first equal number of
  * values.
  *
  *  @param totalbounds Number of bounds object will hold.
  *  @param extents Extent data.
  *  @param xaxes   X Axis direction data.
  *  @param yaxes   Y Axis direction data.
  */
  public BoundsList3D( int totalbounds, Vector3D[] extents, Vector3D[] xaxes,
                       Vector3D[] yaxes )
  {
    numbounds = totalbounds;
    
    extents_list = new Vector3D[numbounds];
    x_axes_list = new Vector3D[numbounds];
    y_axes_list = new Vector3D[numbounds];
    
    minextents = new float[3];
    maxextents = new float[3];
    for(int i = 0; i < 3; i++) {
      minextents[i] = Float.MAX_VALUE;
      maxextents[i] = 0f; 
    }
    
    for(int i = 0; i < numbounds; i ++) {
      if(i < extents.length) {
        extents_list[i] = new Vector3D(extents[i]);
        updateMaxMinExtents( extents_list[i] );
      }
      else 
        extents_list[i] = new Vector3D(0.0f, 0.0f, 0.0f);
      
      if(i < xaxes.length)
        x_axes_list[i] = new Vector3D(xaxes[i]);
      else 
        x_axes_list[i] = new Vector3D(1.0f, 0.0f, 0.0f);
        
      if(i < yaxes.length)
        y_axes_list[i] = new Vector3D(yaxes[i]);
      else 
        y_axes_list[i] = new Vector3D(0.0f, 1.0f, 0.0f);
    }
  }
  
 /**
  * Get size of bounds list. 
  *
  *  @return The number of bounds information
  */ 
  public int getNumBounds( )
  {
    return numbounds;
  }
  
 /**
  * Get minimum of all x, y, z extents (the smallest
  * extension for each direction.) This is calculated
  * internally as extents are added/set.  
  *
  *  @return The minimum extents for x, y, z directions.
  */ 
  public Vector3D getMinExtents( )
  {
    return new Vector3D(minextents);
  }
  
 /**
  * Get maximum of all x, y, z extents (the largest
  * extension for each direction.) This is calculated
  * internally as extents are added/set. 
  *
  *  @return The maximum extents for x, y, z directions.
  */ 
  public Vector3D getMaxExtents( )
  {
    return new Vector3D(maxextents);
  }
  
  
 /**
  * This should only be called if points, and thus bounds information,
  * is removed, and the values for max/min extents needs to be updated. 
  * Max and Min extents are not recalculated automatically when items are 
  * removed.
  */
  public void recalcMaxMinExtents( )
  {
    for(int i = 0; i < 3; i++) {
      minextents[i] = Float.MAX_VALUE;
      maxextents[i] = 0f; 
    }
    
    for(int i=0; i < numbounds; i++)
    {
      updateMaxMinExtents( extents_list[i] );
    }
  }
  
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
  public void setExtents( int index, Vector3D extents )
  {
    if(index < 0 || index >= numbounds)
      throw new IndexOutOfBoundsException("Unable to set.");
    
    float[] ext_array = extents.get();
    if(ext_array[0] < 0f || ext_array[1] < 0f || ext_array[2] < 0f)
      throw new IllegalArgumentException("All extents must be nonnegative.");
      
    extents_list[index] = new Vector3D(extents);
    updateMaxMinExtents( extents_list[index] );
  }
  
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
                          Vector3D[] extents )
  {
    if(index_start < 0 || index_start >= numbounds)
      throw new IndexOutOfBoundsException("Start index out of bounds.");
      
    if(index_end < 0 || index_end >= numbounds)
      throw new IndexOutOfBoundsException("End index out of bounds.");
      
    if(extents.length < (index_end-index_start+1))
      throw new IllegalArgumentException("Array of extents not large enough.");
      
    float[] ext_array;
    for(int i = index_start; i <= index_end; i++) {
      ext_array = extents[i-index_start].get();
      if(ext_array[0] < 0f || ext_array[1] < 0f || ext_array[2] < 0f)
        throw new IllegalArgumentException("All extents must be nonnegative.");
        
      extents_list[i] = new Vector3D(extents[i-index_start]);
      updateMaxMinExtents( extents_list[i] );
    }
  }
  
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
                              Vector3D y_axis )
  {
    if(index < 0 || index >= numbounds)
      throw new IndexOutOfBoundsException("Unable to set.");
    
    x_axes_list[index] = new Vector3D(x_axis);
    y_axes_list[index] = new Vector3D(y_axis);
  }
  
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
                               Vector3D[] y_axes )
  {
    if(index_start < 0 || index_start >= numbounds)
      throw new IndexOutOfBoundsException("Start index out of bounds.");
      
    if(index_end < 0 || index_end >= numbounds)
      throw new IndexOutOfBoundsException("End index out of bounds.");
      
    if(x_axes.length < (index_end-index_start+1) || 
       y_axes.length < (index_end-index_start+1))
      throw new IllegalArgumentException("Array of axes not large enough.");
      
    for(int i = index_start; i <= index_end; i++) {
      x_axes_list[i] = new Vector3D(x_axes[i-index_start]);
      y_axes_list[i] = new Vector3D(y_axes[i-index_start]);
    }
  }
   
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
  public Vector3D getExtents( int index )
  {
    if(index < 0 || index >= numbounds)
      throw new IndexOutOfBoundsException("Unable to set.");
      
    return new Vector3D(extents_list[index]);
  }
  
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
  public Vector3D[] getExtents( int index_start, int index_end )
  {
    if(index_start < 0 || index_start >= numbounds)
      throw new IndexOutOfBoundsException("Start index out of bounds.");
      
    if(index_end < 0 || index_end >= numbounds)
      throw new IndexOutOfBoundsException("End index out of bounds.");
      
    if(index_end < index_start) 
      return null;
      
    Vector3D[] ext_copy = new Vector3D[(index_end - index_start + 1)];
      
    for(int i = index_start; i <= index_end; i++)
    {
      ext_copy[i-index_start] = new Vector3D(extents_list[i]);
    }
    
    return ext_copy;
  }

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
  public Vector3D[] getOrientation( int index )
  {
    if(index < 0 || index >= numbounds)
      throw new IndexOutOfBoundsException("Unable to set.");
      
    Vector3D[] axes = new Vector3D[2];
    
    axes[0] = new Vector3D(x_axes_list[index]);
    axes[1] = new Vector3D(y_axes_list[index]);
    
    return axes;
  }
   
 /**
  * Returns orientation vectors of specified range of indices.
  * For each index in range specified, there is an oriented X
  * axis direction and an oriented Y axis direction.
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
  public Vector3D[][] getOrientations( int index_start, int index_end )
  {
    if(index_start < 0 || index_start >= numbounds)
      throw new IndexOutOfBoundsException("Start index out of bounds.");
      
    if(index_end < 0 || index_end >= numbounds)
      throw new IndexOutOfBoundsException("End index out of bounds.");
      
    if(index_end < index_start) 
      return null;
      
    Vector3D[][] orient_copy = new Vector3D[2][(index_end - index_start + 1)];
      
    for(int i = index_start; i <= index_end; i++)
    {
      orient_copy[0][i-index_start] = new Vector3D(x_axes_list[i]);
      orient_copy[1][i-index_start] = new Vector3D(y_axes_list[i]);
    }
    
    return orient_copy;
  }

 /**
  * Returns X orientation vectors of specified range of indices.
  * 
  *  @param  index_start  First index of the range.
  *  @param  index_end    Last index of the range.
  *  @return A copy of the portion of XOrientation array within the
  *          specified range of indices.
  *  @throws IndexOutOfBoundsException If index range goes 
  *          outside of valid  range of indices: 0...NumElements.
  */ 
  public Vector3D[] getXOrientations( int index_start, int index_end )
  {
    if(index_start < 0 || index_start >= numbounds)
      throw new IndexOutOfBoundsException("Start index out of bounds.");
      
    if(index_end < 0 || index_end >= numbounds)
      throw new IndexOutOfBoundsException("End index out of bounds.");
      
    if(index_end < index_start) 
      return null;
      
    Vector3D[] orient_copy = new Vector3D[(index_end - index_start + 1)];
      
    for(int i = index_start; i <= index_end; i++)
    {
      orient_copy[i-index_start] = new Vector3D(x_axes_list[i]);
    }
    
    return orient_copy;
  }
  
  /**
   * Returns Y orientation vectors of specified range of indices.
   * 
   *  @param  index_start  First index of the range.
   *  @param  index_end    Last index of the range.
   *  @return A copy of the portion of XOrientation array within the
   *          specified range of indices.
   *  @throws IndexOutOfBoundsException If index range goes 
   *          outside of valid  range of indices: 0...NumElements.
   */ 
   public Vector3D[] getYOrientations( int index_start, int index_end )
   {
    if(index_start < 0 || index_start >= numbounds)
      throw new IndexOutOfBoundsException("Start index out of bounds.");
      
    if(index_end < 0 || index_end >= numbounds)
      throw new IndexOutOfBoundsException("End index out of bounds.");
      
    if(index_end < index_start) 
      return null;
      
    Vector3D[] orient_copy = new Vector3D[(index_end - index_start + 1)];
      
    for(int i = index_start; i <= index_end; i++)
    {
      orient_copy[i-index_start] = new Vector3D(y_axes_list[i]);
    }
    
    return orient_copy;
  }
   
 /*-----------------PRIVATE METHODS--------------------*/
 
  private void updateMaxMinExtents( Vector3D ext )
  {
    float[] ext_array = ext.get();
    
    if(ext_array[0] > maxextents[0]) maxextents[0] = ext_array[0];
    if(ext_array[1] > maxextents[1]) maxextents[1] = ext_array[1];
    if(ext_array[2] > maxextents[2]) maxextents[2] = ext_array[2];
    
    if(ext_array[0] < minextents[0]) minextents[0] = ext_array[0];
    if(ext_array[1] < minextents[1]) minextents[1] = ext_array[1];
    if(ext_array[2] < minextents[2]) minextents[2] = ext_array[2];
  }
  
 /*
  * MAIN - Basic main program to test the SparseArray3D class
  */
  public static void main( String args[] ) 
  {
    Vector3D[] my_extents = new Vector3D[10];
    Vector3D[] my_xaxes   = new Vector3D[10];
    Vector3D[] my_yaxes   = new Vector3D[10];
    
    for(int i = 0; i < 10; i++) {
      my_extents[i] = new Vector3D(i*1.0f, i*2.0f, i*3.0f);
      my_xaxes[i] = new Vector3D(i*4.0f, i*5.0f, i*6.0f);
      my_yaxes[i] = new Vector3D(i*7.0f, i*8.0f, i*9.0f);
    }
    
    System.out.println("*********************CREATE********************");
    BoundsList3D testc = new BoundsList3D(10, my_extents, my_xaxes, my_yaxes);
    
    testc.testPrintBounds(testc);
    
    System.out.println("*********************CREATE********************");
    BoundsList3D test = new BoundsList3D(10);
    
    test.testPrintBounds(test);
    
    System.out.println("*********************SET********************");
    test.setExtents(0,8, my_extents);
    test.setOrientations(0,8, my_xaxes, my_yaxes);
    
    test.setExtents(9, my_extents[9]);
    test.setOrientation(9, my_xaxes[9], my_yaxes[9]);
    
    test.testPrintBounds(test);
    
    System.out.println("*********************EXCEPTIONS********************");
    try {
      test.setExtents(100, my_extents[0]);
    } catch(IndexOutOfBoundsException e) {
      System.out.println("Exception caught: " + e);
    }
    try {
      Vector3D[] badarray = new Vector3D[2];
      badarray[0] = badarray[1] = my_extents[0];
      
      test.setExtents(2, 5, badarray);
      
    } catch(IllegalArgumentException e) {
      System.out.println("Exception caught: " + e);
    }
    
  } // END OF MAIN  
  
  /*------------Print for Testing Purposes----------*/
  private void testPrintBounds(BoundsList3D bounds)
  {    
    Vector3D[] exts;
    Vector3D[] xaxes;
    Vector3D[] yaxes;
    
    exts = bounds.getExtents(0, bounds.getNumBounds()-1);
    xaxes = bounds.getXOrientations(0, bounds.getNumBounds()-1);
    yaxes = bounds.getYOrientations(0, bounds.getNumBounds()-1);
    
    System.out.println("Max Extents: " + bounds.getMaxExtents().toString());
    System.out.println("Min Extents: " + bounds.getMinExtents().toString());
    for(int i = 0; i < bounds.getNumBounds(); i++)
    {
      System.out.print( exts[i].toString() );
      System.out.print( " " + xaxes[i].toString() + " " 
                             + yaxes[i].toString() );
      System.out.println( );
      
    }
  }
}