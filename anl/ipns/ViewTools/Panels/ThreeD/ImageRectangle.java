/*
 * File:  ImageRectangle.java
 *
 * Copyright (C) 2011, Dennis Mikkelson
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
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 * $Log$
 */

package gov.anl.ipns.ViewTools.Panels.ThreeD;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.io.*;

import javax.swing.JComponent;

import gov.anl.ipns.MathTools.Geometry.*;
import gov.anl.ipns.Util.Numeric.floatPoint2D;
import gov.anl.ipns.ViewTools.Components.IVirtualArray2D;

/**
 *  This class represents a rectangular image in 3D.
 */
public class ImageRectangle  extends     ThreeD_Object
                             implements  Serializable
{
  Vector3D   center;
  Vector3D   x_vector;
  Vector3D   y_vector;
  Vector3D   normal_vector;
  BufferedImage image= null;

  int[][]  data;
  IndexColorModel  model;  
  JComponent  panel;
  float      width;
  float      height;
  int[]      xtemp = new int[4];
  int[]      ytemp = new int[4];

  /** 
   *  Construct an ImageRectangle using the specified location and orienation
   *  and image.  NOTE: The image data is assumed to correspond to points on
   *  the rectangle in such a way that row numbers increase in the direction 
   *  of the specified y_vector and column numbers increase in the direction
   *  of the specified x_vector.  Looking at the front of the image rectangle
   *  with the x_vector pointing to the right  and the y_vector 
   *  pointing upward, the [0,0] data entry will be in the lower left
   *  corner, the [0,n_cols-1] data entry will be in the lower right corner,
   *  the [n_rows-1,0] data entry will be in the upper left corner and the
   *  [n_rows-1,n_cols-1] data entry will be in the upper right corner.
   *
   *  @param  center     The position of the center of the Image rectangle
   *  @param  x_vector   Vector in the "x" direction of the local coordinate
   *                     system for the rectangle.
   *  @param  y_vector   Vector in the "y" direction of the local coordinate
   *                     system for the rectangle.
   *  @param  width      Overall width of the whole rectangle in the local
   *                     "x" direction.  The width must be positive.
   *  @param  height     Overall height of the whole rectangle in the "y" 
   *                     direction.  The height must be positive.
   *  @param data        "2D" data of values.  The data at row 0 and col 0 is assumed
   *                     to be the lower left point of the rectangle
   *                            ( center-xvec*width/3,center-yvec*height/2)
   *  @param model       An indexColorModel where index=0 is completely transparent and supports dual color.
   *                     The colors corresponding to non-negative values start at zeroValIndex
   *                    
   *  @param zeroValIndex   The index in the ColorModel corresponding to the value zero.Positive values
   *                         have indicies greater than zeroValIndex.
   *                        
   *  @param logscale    Transformation of indicies to highlight different ranges of values.
   *  
   *  @param MaxAbsVal   The maximum of the absolute values in the data. 
   */
  
  public ImageRectangle( Vector3D  center,
                         Vector3D  x_vector,
                         Vector3D  y_vector,
                         float     width,
                         float     height,
                         int[][]  data,
                         IndexColorModel  model, 
                         JComponent  panel)
  {
    super( null, Color.BLACK );

    this.center   = new Vector3D( center );
    this.x_vector = new Vector3D( x_vector );
    this.y_vector = new Vector3D( y_vector );
    this.width    = width;
    this.height   = height;
    create_verts();
    this.data = data;
    this.model = model;
    this.panel = panel;
    this.x_vector.normalize();
    this.y_vector.normalize();
    this.normal_vector = new Vector3D();
    normal_vector.cross( x_vector, y_vector );
    normal_vector.normalize();
  }
  
  public void setColorModel( int[][] data,IndexColorModel model)
  {
     
     this.model = model;
     this.data = data;    
  }
  
 
  /**
   *  Create the array of vertices in the super class, containing the corners
   *  of the rectangle.
   */ 
  private void create_verts()
  {
    vertices = new Vector3D[ 4 ];  

    Vector3D half_x = new Vector3D( x_vector );
    half_x.normalize();
    half_x.multiply( width/2 );

    Vector3D half_y = new Vector3D( y_vector );
    half_y.normalize();
    half_y.multiply( height/2 );

    Vector3D upper_left = new Vector3D( center );
    upper_left.subtract( half_x );
    upper_left.add( half_y );

    Vector3D upper_right = new Vector3D( center );
    upper_right.add( half_x );
    upper_right.add( half_y );

    Vector3D lower_right = new Vector3D( center );
    lower_right.add( half_x );
    lower_right.subtract( half_y );

    Vector3D lower_left = new Vector3D( center );
    lower_left.subtract( half_x );
    lower_left.subtract( half_y );

    Vector3D[] verts = { upper_left, upper_right, lower_right, lower_left };
    vertices = verts;
    x = new float [ vertices.length ];
    y = new float [ vertices.length ];
  }

 

  /**
   *  Draw this ImageRectangle using the projected 2D points in the specified
   *  graphics context g.
   *
   *  @param  g   The graphics object into which the ImageRectangle is to 
   *              be drawn.
   */
  public void Draw( Graphics g )
  {
    if ( projection == null || 
         window_tran == null || 
       !(projection instanceof ViewingTran3D) )
       throw new IllegalArgumentException( 
          "Image Rectange requires a ViewingTran3D, NOT Tran3D, to draw" );
     
     //int max_dist = 0;          // find the maximum x,y distance to x[0],y[0]
     //int dx,                    // so that if the polygon is extremely small
     //    dy;                    // we can draw as a point, or just the border

     int min_x = Math.round(x[0]);
     int min_y = Math.round(y[0]);
     int max_x = Math.round(x[0]);
     int max_y = Math.round(y[0]);
     for ( int i = 0; i < 4; i++ )
     {
       xtemp[i] = Math.round(x[i]);
       ytemp[i] = Math.round(y[i]);
       if ( xtemp[i] < min_x )
         min_x = xtemp[i];
       if ( xtemp[i] > max_x )
         max_x = xtemp[i];
       if ( ytemp[i] < min_y )
         min_y = ytemp[i];
       if ( ytemp[i] > max_y )
         max_y = ytemp[i];
     }
                                          // restrict to drawing area 
     Rectangle region = g.getClipRect();    
     if ( min_x < 0 )
       min_x = -1;
     if ( max_x > region.width )
       max_x = region.width;
     if ( min_y < 0 )
       min_y = -1;
     if ( max_y > region.height )
       max_y = region.height;
                                         // set getColRowAndDistance() method
                                         // for more documentation on this
                                         // calculation.  The calculation is
                                         // re-implemented here to avoid 
                                         // repeatedly calculating the basic
                                         // information that is used for all
   
    if( max_x-min_x+1 <=0)
       return;
    if( max_y-min_y+1 <=0)
       return;
    image = new BufferedImage(max_x-min_x+1, max_y-min_y+1, 
             BufferedImage.TYPE_BYTE_INDEXED, model );
    
    WritableRaster rast = model.createCompatibleWritableRaster( 
          max_x-min_x+1 ,  max_y-min_y+1 );
    
    // pixels.
    ViewingTran3D view_tran = (ViewingTran3D)projection;

    Vector3D cop = view_tran.getCOP();
    Vector3D vrp = view_tran.getVRP();

    Vector3D diff = new Vector3D( center );
    diff.subtract( cop );
    float numerator = diff.dot( normal_vector );
    float denominator;
    float t;
    float alpha, beta;
    float col, row;

    Vector3D u = view_tran.getU();
    Vector3D v = view_tran.getV();
    u.normalize();
    v.normalize();

    int n_rows = data.length;
    int n_cols = data[0].length;

    Vector3D ray_dir = new Vector3D();
    Vector3D virtual_screen_pt = new Vector3D();
    Vector3D[] vectors = { vrp, u, v };
    Vector3D image_point = new Vector3D();
    float[] coeff = { 1, 0, 0 };

    floatPoint2D pixel = new floatPoint2D();
    floatPoint2D plane_pt;
    
   // long start =System.currentTimeMillis( );
    for ( int i = min_x; i <= max_x; i++ )
      for ( int j = min_y; j <= max_y; j++ )
      {
        pixel.setLocation( i,j );
        plane_pt = window_tran.MapFrom( pixel );
        coeff[1] = plane_pt.x;
        coeff[2] = plane_pt.y;
        virtual_screen_pt.linear_combination( coeff, vectors );
        ray_dir.set( virtual_screen_pt );
        ray_dir.subtract( cop );
        ray_dir.normalize();
        denominator = ray_dir.dot( normal_vector );
        if ( denominator == 0 )         // looking at edge so just draw line
        {
          g.setColor( Color.BLACK );
          g.drawLine( min_x, min_y, max_x, max_y ); 
          return;
        }
        t = numerator / denominator;
        if ( t > 0 )                    // image is in front of the observer
        {
          image_point.set( ray_dir );
          image_point.multiply( t );
          image_point.add( cop );
          image_point.subtract( center );
          alpha = image_point.dot( x_vector );
          beta  = image_point.dot( y_vector );

          col = ((alpha + width/2 )/width * n_cols);
          row = ((beta  + height/2)/height * n_rows);

          if ( col >= 0 && col < n_cols &&
               row >= 0 && row < n_rows )
          {
             int index = data[(int)row][(int)col];
            
            // int index =128;// (int)(NColors*.8 );//+10*Math.random()-5);
             //System.out.println("r,c,i,j,val,index ="+row+","+col+","+","+i+","+j+","+val+","+index);
            rast.setPixel(i-min_x , j-min_y , new int[]{index} );
        
          }else
          {
             rast.setPixel( i-min_x , j-min_y , new int[]{0} ); 
          }
            
        }
      }
   
    image.setData( rast );
    g.setColor( Color.red);
    for( int i=0;i<3;i++)
    g.drawLine( (int)x[i],(int)y[i],(int)x[i+1],(int)y[i+1]);
    g.drawLine((int)x[0],(int)y[0], (int)x[3],(int)y[3]);
    
    g.drawImage( image , min_x , min_y , panel );
      
  }

 
 /**
  *  Given the coordinates of a pixel on the actual screen find the 
  *  corresponding column and row in the image, and the distance from the
  *  observer to that point on the ImageRectangle.  The distance value can
  *  be used to determine which ImageRectangle is closest to the observer, if
  *  two ImageRectangles overlap.  This is needed when picking points on 
  *  the ImageRectangles.
  *
  *  @param pix_x         The x coordinate of the screen pixel
  *  @param pix_y         The y coordinate of the screen pixel
  *  @param col_row_dist  The calling code MUST provide an array with room 
  *                       for three floats.  If the specified pixel
  *                       corresponds to a point on this ImageRectangle, the
  *                       the first two positions of this array will be filled
  *                       with the col and row numbers in the image for that
  *                       point on the screen.  The third entry will be set
  *                       to the distance from the observer to that point on
  *                       the ImageRectangle.
  *  @return true if the specified pixel corresponds to a point on the
  *               ImageRectangle.  This will return false if the specified
  *               pixel does not correspond to a point on this ImageRectangle,
  *               of if the ImageRectangle is viewed exactly on edge.  If
  *               this returns false, the information in the col_row_dist
  *               array is not valid.
  */
  public boolean getColRowAndDistance( float pix_x, float pix_y, 
                                       float[] col_row_dist )
  {
    if ( projection == null || window_tran == null )
      return false;

    if ( ! (projection instanceof ViewingTran3D) )
      return false;
                                         
    ViewingTran3D view_tran = (ViewingTran3D)projection;

    Vector3D cop = view_tran.getCOP();      // the "center of projection" is
                                            // the location of the observer
    Vector3D vrp = view_tran.getVRP();      // the "view reference point" is
                                            // the center point of the virtual
                                            // screen at which the objerver is
                                            // looking.

    Vector3D u = view_tran.getU();          // the u and v vectors are local
    Vector3D v = view_tran.getV();          // "x" and "y" vectors on the 
    u.normalize();                          // virtual viewing screen.
    v.normalize();
                                            // first find the 3D point on the
                                            // virtual viewing screen that 
                                            // corresponds to the given pixel
    floatPoint2D pixel = new floatPoint2D( pix_x, pix_y );
    floatPoint2D plane_pt = window_tran.MapFrom( pixel );
    Vector3D[] vectors = { vrp, u, v };
    float[] coeff = { 1, plane_pt.x, plane_pt.y };

    Vector3D virtual_screen_pt = new Vector3D();
    virtual_screen_pt.linear_combination( coeff, vectors );

                                            // next construct the ray in 3D
                                            // pointing from the observer to
                                            // the point on the virtual viewing
                                            // screen
    Vector3D ray_dir = new Vector3D( virtual_screen_pt );
    ray_dir.subtract( cop );
    ray_dir.normalize();
                                            // now find the intersection of
                                            // the ray with the plane of this
                                            // IageRectangle.  The intersection
                                            // is at P(t) = cop + t * ray_dir
    float denominator = ray_dir.dot( normal_vector );
    if ( denominator == 0 )
      return false;

    Vector3D diff = new Vector3D( center ); 
    diff.subtract( cop );
    float t = diff.dot( normal_vector ) / denominator;

    if ( t <= 0 )                            // if t is not positive, then
      return false;                          // point is at or behind observer

                                             // now find the components of the
                                             // point on the ImageRectangle
                                             // plane, relative to the local
                                             // x, and y basis vectors on the
                                             // face of the ImageRectangle
    Vector3D image_point = new Vector3D( ray_dir );
    image_point.multiply( t );
    image_point.add( cop );

    image_point.subtract( center );
    float alpha = image_point.dot( x_vector );
    float beta  = image_point.dot( y_vector );
                                              // finally, calculate the col
                                              // and row numbers in the image
                                              // array corresponding to the
                                              // intersection point
    int n_rows = data.length;
    int n_cols = data[0].length;

    float col = (alpha + width/2 )/width * n_cols;
    float row = (beta  + height/2)/height * n_rows;

    if ( col < 0 || col >= n_cols || row < 0 || row >= n_rows ) 
      return false;

    col_row_dist[0] = col;
    col_row_dist[1] = row;
    col_row_dist[2] = t;

    return true;    
  }

}
