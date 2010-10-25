/* 
 * File: ImageFilled3DRectangle.java
 *
 * Copyright (C) 2010, Ruth Mikkelson
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
 * Contact : Ruth Mikkelson <mikkelsonr@uwstout.edu>
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751, USA
 *
 * This work was supported by the Spallation Neutron Source Division
 * of Oak Ridge National Laboratory, Oak Ridge, TN, USA.
 *
 *  Last Modified:
 * 
 *  $Author:$
 *  $Date:$            
 *  $Rev:$
 */
package gov.anl.ipns.ViewTools.Panels.ThreeD;

import gov.anl.ipns.MathTools.Geometry.*;
import gov.anl.ipns.Util.Numeric.floatPoint2D;
import gov.anl.ipns.Util.Sys.*;
import gov.anl.ipns.ViewTools.Components.IVirtualArray2D;
import gov.anl.ipns.ViewTools.Components.VirtualArray2D;
import gov.anl.ipns.ViewTools.Panels.Image.IndexColorMaker;
import gov.anl.ipns.ViewTools.Panels.Transforms.CoordBounds;
import gov.anl.ipns.ViewTools.Panels.Transforms.CoordJPanel;
import gov.anl.ipns.ViewTools.Panels.Transforms.CoordTransform;
import gov.anl.ipns.ViewTools.UI.ImageFilledRectangle;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;

import javax.swing.JComponent;
import javax.swing.JFrame;


public class ImageFilled3DRectangle extends ThreeD_Object implements MouseListener
{

   Vector3D center; 
   Vector3D xvec;
   Vector3D yvec; 

   Vector3D center_orig; 
   Vector3D xvec_orig;
   Vector3D yvec_orig; 

   float width;
   float height;
   int nrows_rect; 
   int ncols_rect;
   float ProjWidth;
   float ProjHeight;
   IVirtualArray2D image;
   IndexColorModel colorModel;
   JComponent Comp;
   ImageFilledRectangle Rect;
   CoordTransform window_tran;
   
   boolean changed = true;// false will not recalculate
   boolean imageChanged = true;//false will not recalculate
   /**
    *  Constructor
    * @param center      The 3D center of the rectangle
    * 
    * @param xvec        The direction from "upper left" corner to "upper right" corner
    *                    of the rectangle
    *                    
    * @param yvec        The direction from "upper left" corner to "lower left" corner
    *                    of the rectangle
    *                    
    * @param width       The width of the rectangle( xvec direction)
    * 
    * @param height      The height of the rectangle( yvec direction)
    * 
    * @param nrows_rect  number of rows of the rectangle. row 1 col 1 is 
    *                    in the "lower left" corner
    *                    
    * @param ncols_rect number of cols of the rectangle. row 1 col 1 is 
    *                    in the lower left corner. row= 1 col= ncol is the upper
    *                    "right" corner.  
    *                    
    * @param image       The image data. Assume row 1, col 1 of data is mapped to
    *                    the "upper left corner" of the Detector.
    */
   public ImageFilled3DRectangle(Vector3D center, 
                                 Vector3D xvec,
                                 Vector3D yvec, 
                                 float width, 
                                 float height, 
                                 int nrows_rect, 
                                 int ncols_rect,
                                 IVirtualArray2D image,
                                 IndexColorModel colorModel,
                                 JComponent Comp)
   {

      super( get4Points(center,xvec,yvec,width,height) , Color.white);
      this.center = center; 
      this.xvec = xvec;
      this.yvec = yvec ; 
      this.width = width;
      this.height = height;
      this.nrows_rect = nrows_rect; 
      this.ncols_rect = ncols_rect;
      this.image = image;
      this.colorModel = colorModel;
      this.Comp = Comp;
      this.center_orig = new Vector3D(center); 
      this.xvec_orig = new Vector3D(xvec);
      this.yvec_orig = new Vector3D( yvec); 
      ProjWidth = width;
      ProjHeight = height;

      
      //have to convert width, height to pixels
      Rect = new ImageFilledRectangle(image, 
                                      colorModel,
                                      new Dimension( (int)width, (int)height), 
                                      center,
                                      xvec, 
                                      yvec);
      
      
      // TODO Auto-generated constructor stub
   }
   
   public void setMaxMin( float Max, float Min)
   {
      Rect.setMaxMin( Max , Min );
   }
   /**
    * Should only be done after construction
    * @param mult
    */
   public void setScale( float mult)
   {
     Dimension D = Rect.getRectangle( );  
     width = ProjWidth = mult * ProjWidth;
     height = ProjHeight = mult *ProjHeight;
     Rect.setRectangle( new Dimension( (int)(.5+width), (int)(height+.5)) );
   
     for( int i=0; i<4; i++)
     {
        x[i] *=mult;
        y[i] *= mult;
        vertices[i].multiply( mult );
     }
     center_orig.multiply( mult );
     center = new Vector3D(center_orig);
     Rect.setPlane( xvec_orig , yvec_orig , center_orig );
   }
   
   
   private static Vector3D[] get4Points( Vector3D center,
         Vector3D xvec,
         Vector3D  yvec,
         float     width,
         float    height)
   {
      Vector3D[] vertices = new Vector3D[4];
      
      Vector3D halfWidth = new Vector3D( xvec);
      halfWidth.multiply(  width/2 );
      Vector3D halfHeight = new Vector3D(yvec);
      halfHeight.multiply( height/2 );
      
      vertices[0] = new Vector3D(center);
      vertices[0].subtract(halfWidth);
      vertices[0].subtract( halfHeight );
      

      vertices[1] = new Vector3D(center);
      vertices[1].subtract(halfWidth);
      vertices[1].add( halfHeight );

      vertices[2] = new Vector3D(center);
      vertices[2].add(halfWidth);
      vertices[2].add( halfHeight );

      vertices[3] = new Vector3D(center);
      vertices[3].add(halfWidth);
      vertices[3].subtract( halfHeight );
      
      return vertices;
   }
   
   public void setColorInfo( ColorModel colormodel,float[] logScale, int ZERO_COLOR_INDEX)
   {
      Rect.setColorInfo( colormodel, logScale, ZERO_COLOR_INDEX);
   }
   
   
   public float distance_to(float pix_x,
         float pix_y)
   {
      floatPoint2D[] Edges = Rect.getEdges( );
      float d =Float.POSITIVE_INFINITY;
      int[]xs = new int[4];
      int[]ys = new int[4];
      for( int i=0; i< 4; i++)
      {
         xs[i]= (int)(.5 + Edges[i].x);
         ys[i] = (int)(.5 + Edges[i].y);
      }
      
      java.awt.Polygon P = new java.awt.Polygon( xs,ys,4);
      if( P.contains( (int)(.5+pix_x),
                                (int)(.5+pix_y)))
         return 0;
      
      
      for( int i=0; i< 4; i++)
      {
         floatPoint2D P1 = Edges[i];
         floatPoint2D P2 = Edges[(i+1)%3];
         if( Math.abs(P1.x - P2.x)<=2)//Vertical line
         {
            int d1 = (int)(.5+Math.abs(  pix_x -P1.x ));
            int d2 =(int)(.5+Math.abs(  pix_x -P2.x ));
            d1 = (int)((d1+d2)/2);
            d2=0;
            if( pix_y < Math.min( P1.y , P2.y ))
               
               d2 = (int)(.5 + Math.min( P1.y,P2.y  )-pix_y);
            
            else if(   pix_y > Math.max(P1.y , P2.y ))
               d2 = (int)(.5 + pix_y- Math.max( P1.y,P2.y ));
            
           d1 =(int)(.5 + Math.sqrt(d1*d1+d2*d2));
           if( d1 < d)
              d = d1;
               
         }else
         {
         
         float m = (P2.y-P1.y)/(P2.x-P1.x)   ;
         float cp = -m*P1.x- P1.y;
         float d1 = Math.abs( m*pix_x+pix_y +cp)/(float)Math.sqrt( m*m+1);
         if( d1 < d)
            d = d1;
         
         }
         
         
      }
      
     return d; 
      
   }

   @Override
   public void Draw(Graphics g)
   {
    Point[]TopLeft = Rect.getTopLeftImagePosition( );
    CoordBounds tr =window_tran.getDestination( );
    if( TopLeft == null )
       return;
    
    if( TopLeft[0].x > tr.getX1( ) &&  TopLeft[0].x > tr.getX2( ))
       return;
    
    if( TopLeft[0].y > tr.getY1( ) &&  TopLeft[0].y > tr.getY2( ))
          return;
    
    if( TopLeft[1].y <tr.getY1( ) &&  TopLeft[1].y < tr.getY2( ))
       return;

    if( TopLeft[1].x <tr.getX1( ) &&  TopLeft[1].x < tr.getX2( ))
       return;
   //TODO: get subimage that just fits the window.
    
    BufferedImage img = Rect.getImage( );
    
    if( img == null)
       return;
    
   
   
    g.drawImage( img , TopLeft[0].x, TopLeft[0].y, Comp);

   }
   
   
   
/**
 * Calculates the row/col in this rectangle of the given  point 
 * 
 * @param x   The x coordinate of the point reported by CoordJPanel's 
 *              getCurrentPixel method
 *              
 * @param y   The y coordinate  of the point reported by CoordJPanel's 
 *               getCurrentPixel method
 *               
 * @return   an int array with the row,col in this rectangle of the given  
 *             point, starts at row 0 and col 0 
 */
   public int[] getRectRowCol(int x, int y)
   {

      floatPoint2D ff = Rect.getRectPos( x , y );

      if ( ff == null )
         return null;
      
      Dimension R = Rect.getRectangle( );
      int row = ( int )Math.floor ( ( nrows_rect / 2 ) - ff.y * nrows_rect
            / ( float ) R.height );
      
      int col = (int)Math.floor (  ( ncols_rect / 2 ) + ff.x * ncols_rect
            / ( float ) R.width );

      int[] res = new int[ 2 ];
      res[0] = row;
      res[1] = col;
      
      return res;
   }
   
   private Vector3D ChangeYsign(Vector3D X)
   {
      return new Vector3D( X.getX( ),-X.getY( ),X.getZ());
   }
   
   /**
    * Projects a la AltAzi controller used in the threeD view.
    */
   @Override
   public void Project(Tran3D projection, CoordTransform windowTran,
         float frontClip)
   {

      
      super.Project( projection , windowTran , frontClip );
      if ( projection == null || windowTran == null )
      {
        
        return;
        
      }
      
      // There seemed to be a coordinate system mismatch with the
      // AltAzt controller and ...  When the center was "behind" which should mean
      //                           y>0(IPNS) and in window coord's z<0. This was not
      //                           the case.
      //       Also, had to subsequently negate xvec to get left right to correspond.
      //       Did all this experimentally. Did not find basis( though picture had y axis
      //                  pointing in opposite direction than it should have)
      projection.apply_to( ChangeYsign(center_orig), center );

      projection.apply_to( (xvec_orig), xvec );

      projection.apply_to( (yvec_orig), yvec );
      xvec= new Vector3D( xvec.getX( ),xvec.getY( ),-xvec.getZ());
    
      /*if( this.pick_id %100 ==6 )
      {
         System.out.println("xvec,yvec="+xvec+","+yvec);
         System.out.println( "tran3D="+projection);
         Vector3D r = new Vector3D();
         Vector3D xvecEnd = ChangeYsign(center_orig);
         xvecEnd.add( ( xvec_orig));
         projection.apply_to( xvecEnd, r );
         r.subtract( center );
         System.out.println( "Xformed XvecEnd="+r);
         
      }*/
      float[] coords = center.get();

      if ( coords[2] >= frontClip )
        clipped = true;

      center.standardize();

    
      this.window_tran = windowTran;
      
      float[] centerx = new float[]{center.getX()} ;
      float[] centery = new float[]{center.getY()} ;
      
      window_tran.MapTo( centerx, centery);
      
      center = new Vector3D( centerx[0],centery[0], center.getZ());
      
      CoordBounds Cbounds = window_tran.getSource( );
      CoordBounds Dbounds = window_tran.getDestination( );
      float multx = Math.abs( (float)(Cbounds.getX2()-Cbounds.getX1())/(Dbounds.getX2()-Dbounds.getX1()) );
      float multy=Math.abs( (float)(Cbounds.getY2()-Cbounds.getY1())/(Dbounds.getY2()-Dbounds.getY1()) );
      multx = Math.min( 1/multx, 1/multy);
      ProjWidth = multx*width;
      ProjHeight = multx*height;
      Rect.setRectangle( new Dimension((int)(.5+ProjWidth), (int)(.5+ProjHeight)) );
      yvec.standardize();
     // xvec = new Vector3D(-xvec.getX( ),-xvec.getY( ),-xvec.getZ( ));
      
      xvec.standardize();
      xvec.normalize( );
      yvec.normalize( );
      xvec.multiply(-1);
      Rect.setPlane( xvec , yvec , center );
      changed = true;
     
   }

   
   @Override
   public void mouseClicked(MouseEvent arg0)
   {

      CoordJPanel jpan = (CoordJPanel)(arg0.getSource());
     
      Point fp = jpan.getCurrent_pixel_point( );
      floatPoint2D ff =Rect.getRectPos( fp.x , fp.y );
      System.out.println("   xxx-"+ ff);
      System.out.println("   YYY"+jpan.getCurrent_pixel_point( ));
      if( ff == null )
         return;
      Dimension R = Rect.getRectangle( );
      int row = (int)(.5+(nrows_rect/2)-ff.y*nrows_rect/(float)R.height);
      int col = (int)(.5+(ncols_rect/2)+ff.x*ncols_rect/(float)R.width);
      System.out.println("row/col="+row+","+col);
   }

   @Override
   public void mouseEntered(MouseEvent arg0)
   {

      // TODO Auto-generated method stub
      
   }

   @Override
   public void mouseExited(MouseEvent arg0)
   {

      // TODO Auto-generated method stub
      
   }

   @Override
   public void mousePressed(MouseEvent arg0)
   {

      // TODO Auto-generated method stub
      
   }

   @Override
   public void mouseReleased(MouseEvent arg0)
   {

      // TODO Auto-generated method stub
      
   }

   /**
    * @param args
    */
   public static void main(String[] args)
   {

      float[][] imageData = new float[80][80];
      int N = 200;
      for( int r=0; r<80;r++)
         for( int c=0;c<80;c++)
            imageData[r][c] = r*c/(float)6400*N;
      
     VirtualArray2D image = new VirtualArray2D( imageData);
     FinishJFrame jf = new FinishJFrame("Test");
     
     IndexColorModel colorModel = IndexColorMaker.getColorModel( 
                    IndexColorMaker.HEATED_OBJECT_SCALE_2 , 201 );
     
     jf.getContentPane( ).setLayout(  new BorderLayout() );
     jf.setSize(  600,800 );
     ThreeD_JPanel leftPanel = new ThreeD_JPanel();
     ImageFilled3DRectangle rect = new ImageFilled3DRectangle
                     (new Vector3D(-200,0,0),//(200,300,-100), 
                      new  Vector3D(0,1,0),
                      new  Vector3D(0,0,1), 
                           100, 
                           150, 
                           200, 
                           200,
                           image,
                           colorModel,
                           leftPanel
                           );
     
     leftPanel.addMouseListener( rect);
     leftPanel.setVirtualScreenSize(600 , 800 , true );
     leftPanel.setGlobalWorldCoords(  new CoordBounds(-200,-300,200,300));//(0,0,300,500) );
     IThreeD_Object[] objects = new IThreeD_Object[1];
     objects[0] = rect;
     leftPanel.setObjects( "rect" , objects );
     jf.getContentPane( ).add(  leftPanel, BorderLayout.CENTER );
//---------------Azimuthal controller------------------     
     JFrame f = new JFrame("Test for AltAzController");
     f.setBounds(0,0,200,200);
     AltAzController controller = new AltAzController(0,0,200,3000,1000);//79.2f,46.2f,600,1500,805);
    // controller.setPerspective( true);
     
     //controller.setVirtualScreenSize( 600,800 );
     f.getContentPane().add( controller );
     f.setVisible( true );
     
     controller.addControlledPanel( leftPanel);
     controller.apply( true );
   
     WindowShower.show( jf );
     leftPanel.repaint( );

   }

}
