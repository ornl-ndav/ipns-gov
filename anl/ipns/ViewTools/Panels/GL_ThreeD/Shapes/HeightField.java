/*
 * File:  HeightField.java
 *
 * Copyright (C) 2004, Dennis Mikkelson
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
 * number DMR-0218882.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 * $Log$
 * Revision 1.1  2004/06/18 19:21:27  dennis
 * Moved to Shapes package.
 *
 * Revision 1.3  2004/06/15 22:15:33  dennis
 * Commented out some debug prints.
 *
 * Revision 1.2  2004/06/15 16:48:23  dennis
 * Now uses 1-D texture map based on z value.
 *
 * Revision 1.1  2004/05/28 20:51:13  dennis
 * Initial (test) version of classes for displaying and picking
 * 3D objects using OpenGL from Java, built on the "jogl" system.
 *
 *
 */

package gov.anl.ipns.ViewTools.Panels.GL_ThreeD.Shapes;

import gov.anl.ipns.MathTools.Geometry.*;
import net.java.games.jogl.*;

/**
 *  This class represents a surface z = f(x,y), above a rectangular
 *  region of the x,y plane, with x in [ -depth/2, depth/2 ] and
 *  y in [ -width/2, width/2 ].  The values are specified in a two
 *  dimensional array heights[][].  This array MUST be a full rectangular
 *  array (not a ragged array) with at least two rows and at least two columns.
 *  Row zero of this array corresponds to the values f( -depth/2, * ) and 
 *  column zero corresponds to the values f( *, -width/2 ).  The rectangular 
 *  region in the x,y plane is divided evenly in x and y.  
 *  Surface normals are generated on the +z side of the surface.
 *  If a two dimensional texture has been specified for this object,
 *  texture coordinates will be generated.  Texture coordinate s=0 corresponds
 *  to the first row, and texture coordinate s=1 corresponds the last row.
 *  Similarly Texture coordinate t=0 corresponds to the first column and
 *  texture coordinate t=1 corresponds the last column.
 */
public class HeightField extends GeometryDisplayListObject 
{
  private float width = -1,
                depth = -1;

  private float z[][]     = null;
  private float range_min = 0;
  private float range_max = 1;


  public HeightField()
  {
    float heights[][] = { {1, 2}, {3, 4} };    // default values
    setHeights( heights, 2, 2, 1, 4 );
  }


  /**
   *  
   */
  public HeightField( float heights[][],
                      float x_size, 
                      float y_size,
                      float range_min,
                      float range_max )
  {
    setHeights( heights, x_size, y_size, range_min, range_max );
  }


  public boolean setHeights( float heights[][], 
                             float x_size, 
                             float y_size,
                             float range_min, 
                             float range_max )
  {
     if ( x_size <= 0 || y_size <= 0 )
     {
        System.out.println("WARNING: invalid size in Suface " + x_size + 
                           ", " + y_size );
        return false;
     }
     if ( heights == null || heights.length <= 1 ||
          heights[0] == null || heights[0].length <= 1 )
     {
        System.out.println("WARNING: invalid height array in Suface " );
        return false;
     }

     this.width = x_size;
     this.depth = y_size;

     z = new float[heights.length][];
     for ( int row = 0; row < heights.length; row++ )
     {
       z[row] = new float[ heights[0].length ];
       System.arraycopy( heights[row], 0, z[row], 0, z[row].length );
     }

     if ( range_max > range_min )
     {
       this.range_min = range_min;
       this.range_max = range_max;
     }

     rebuild_list = true;
     return true;
  }


  protected void Clear()
  {
    z = null;
    width = -1;
    depth = -1;
  }


  protected boolean newData()
  {
    if ( z != null )
      return true;
    else
      return false;
  }


  synchronized protected void DrawGeometry( GLDrawable drawable )
  {
     GL gl = drawable.getGL();

     float x,
           y;
     float x_min = -depth/2;
     float y_min = -width/2;
     float dx    = depth/(z.length - 1); 
     float dy    = width/(z[0].length - 1);
 
     float    n[];
     float    h;
     float    tex_coord;

     for ( int row = 0; row < z.length - 1; row++ )
     {
       x = x_min + row * dx;

//       gl.glBegin( GL.GL_TRIANGLE_STRIP );
       gl.glBegin( GL.GL_QUAD_STRIP );
       for ( int col = 0; col < z[0].length; col++ )
       {
         y = y_min + col * dy;
 
         n = ave_normal( row, col, x, y, dx, dy );
         h = z[row][col];
         tex_coord = (h - range_min)/(range_max - range_min);
         gl.glTexCoord1f( tex_coord );
         gl.glNormal3f( n[0], n[1], n[2] );
         gl.glVertex3f( x, y, h );

         n = ave_normal( row+1, col, x+dx, y, dx, dy );
         h = z[row+1][col];
         tex_coord = (h - range_min)/(range_max - range_min);
         gl.glTexCoord1f( tex_coord );
         gl.glNormal3f( n[0], n[1], n[2] ); 
         gl.glVertex3f( x+dx, y, h );
       }
       gl.glEnd();
     }
  }

  /**
   *  Calculate the average of four normal vectors to four triangles 
   *  with common vertex *  at the point (x,y) with data at z(row,col).
   *
   *  @param  row  The row number for the data point where normal is calculated
   *  @param  col  The col number for the data point where normal is calculated
   *  @param  x    The x coordinate for the data point
   *  @param  y    The y coordinate for the data point
   *  @param  dx   The change in x coordinate for a change of 1 in the row
   *  @param  dy   The change in y coordinate for a change of 1 in the column 
   *
   *  @return the average normal vector in an array.
   */

  private float[] ave_normal( int   row, int   col, 
                              float x,   float y, 
                              float dx,  float dy )
  {
    if ( row == 0 || row == z.length-1 ||
         col == 0 || col == z[0].length-1 )
      return (new Vector3D( 0,0,1 )).get();

    Vector3D v10 = new Vector3D( x, y - dy, z[row][col-1] );
    Vector3D v11 = new Vector3D( x, y,      z[row][col] );
    Vector3D v12 = new Vector3D( x, y + dy, z[row][col+1] );
    Vector3D v01 = new Vector3D( x - dx, y, z[row-1][col] );
    Vector3D v21 = new Vector3D( x + dx, y, z[row+1][col] );
   
    Vector3D n1 = normal( v10, v11, v21 );
    Vector3D n2 = normal( v01, v11, v10 );
    Vector3D n3 = normal( v12, v11, v01 );
    Vector3D n4 = normal( v21, v11, v12 );

    n1.add( n2 );
    n1.add( n3 );
    n1.add( n4 );
    n1.normalize();
    
    return n1.get();
  }


  /**
   *  Calculate the normal vector to the triangle formed by vertices
   *  v0, v1 and v2, listed in clockwise order when looking at the outside
   *  of the triangle. 
   *
   *  @param   v0   First of three vertices of a triangle, in clockwize order
   *  @param   v1   First of three vertices of a triangle, in clockwize order
   *  @param   v2   First of three vertices of a triangle, in clockwize order
   *
   *  @return  The cross product (v0-v1)X((v2-v1)
   * 
   */
  public static Vector3D normal( Vector3D v0, Vector3D v1, Vector3D v2 )
  {
    Vector3D e1 = new Vector3D( v0 );
    e1.subtract( v1 );

    Vector3D e2 = new Vector3D( v2 );
    e2.subtract( v1 );

    Vector3D n = new Vector3D();
    n.cross( e1, e2 );
    
    n.normalize();

    return n;
  }

}
