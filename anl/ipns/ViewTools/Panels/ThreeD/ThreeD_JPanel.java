/*
 * File:  ThreeD_JPanel.java
 *
 * Copyright (C) 2001, Dennis Mikkelson
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
 *           Menomonie, WI. 54751
 *           USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 * $Log$
 * Revision 1.8  2001/07/12 16:29:22  dennis
 * build_object_list() is no only called during painting, if
 * items have been added to or removed from the list.
 *
 * Revision 1.7  2001/07/12 14:43:39  dennis
 * Added clear() method to remove all objects.
 * Private method build_object_list no longer calls repaint()
 * when the list is emptied.  It now sets tran3D_used to null
 * so that the objects will be projected and depth sorted when
 * the scene is painted.
 *
 * Revision 1.6  2001/07/10 16:31:36  dennis
 * Now uses Hashtable to keep the list of named ThreeD objects, instead
 * of using a Vector.
 *
 * Revision 1.5  2001/06/28 20:23:24  dennis
 * Now maintains separate named lists of 3D objects, that can
 * be added, removed and colored independently.
 *
 * Revision 1.4  2001/05/29 14:55:39  dennis
 * setVirtualScreen() now takes a third parameter to specify
 * whether or not to reset the local transform as well as
 * the global transform.
 *
 * Revision 1.3  2001/05/23 17:33:51  dennis
 * Now calculates front clipping plane between COP and VRP.
 * Also improved error checking for null transforms.
 *
 * Revision 1.2  2001/05/09 21:10:28  dennis
 * Added method pickID( x, y, pick_radius ) to get the ID of the
 * 3D object closest to the specified pixel (x,y).
 * Added method project( point ) to calculate the pixel coordinates
 * of the projection of the specified 3D point.
 * Added code to paint() to disable the cursors while repainting.
 *
 * Revision 1.1  2001/05/08 21:06:29  dennis
 * JPanel for drawing lists of 3D objects.
 *
 */

package DataSetTools.components.ThreeD;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;
import DataSetTools.util.*;
import DataSetTools.math.*;
import DataSetTools.components.image.*;

/**
 *  A ThreeD_JPanel object maintains and draws a list of ThreeD objects.
 */ 

public class ThreeD_JPanel extends    CoordJPanel 
                           implements Serializable
{
  private  Hashtable       obj_lists     = null;  // Hastable storing, object[]
                                                  // lists referenced by name 
                                                  
  private  IThreeD_Object  all_objects[] = null;  // array of all current 
                                                  // objects
  private  int             index[]       = null;  // depth sorted array of
                                                  // indices into all_objects[]
  private  Tran3D          tran;
  private  Tran3D          tran3D_used  = null;
  private  CoordTransform  tran2D_used  = null;

  private  volatile boolean data_painted    = false;
  private  volatile boolean obj_lists_valid = false;

  private  float            clip_factor  = 0.9f;   // relative location of the
                                                   // front clipping plane as 
                                                   // fraction of distance from
                                                   // VRP to COP

/* --------------------- Default Constructor ------------------------------ */
/**
 *  Construct a default ThreeD_JPanel with an empty list of ThreeD objects.
 */
  public ThreeD_JPanel()
  { 
    super();

    obj_lists = new Hashtable();
    setVirtualScreenSize( 1, 1, true );
    setBackground( Color.black );
  }

/* ------------------------ request_painting --------------------------- */
/**
 *  Call repaint and wait for the system to actually paint the list of
 *  of 3D objects.  This can be used to draw a sequence of "frames", where
 *  each frame has different colors assigned to the objects.  In such 
 *  cases, it is important to wait until the objects are actually drawn 
 *  before changing the colors again.  This method sleeps for the specified
 *  time interval before checking again whether or not the objects have been
 *  completely painted.
 *
 *  @param  time_ms  The time to sleep each time through the wait loop.  This
 *                   should be 30 milliseconds or more in most cases.
 */
  public void request_painting( int time_ms )
  {
      if ( data_painted )               // nothing to do
        return;

      repaint();
      while ( !data_painted )           // wait till it's done painting
      try
      {
        Thread.sleep( 100 );
      }
      catch( Exception e )
      {
        System.out.println("Exception while sleeping in request_painting "+e);
      }
  }


/* --------------------------------- paint ------------------------------- */
/**
 *  Draw all of the graphs.  This function should not be called directly by
 *  the application.  To request drawing the 3D scene, call repaint() if the
 *  drawing can be done asyncronously or call request_painting() if the
 *  application needs to wait for the drawing to complete before advancing.
 *
 *  @param  g   The graphics context to use when painting the scene.
 */
  public void paint( Graphics g )
  {
    data_painted = true;
    if ( isDoingCrosshair() )                  // if the system redraws this
      stop_crosshair( current_point );         // without our knowledge, 
                                               // we've got to get rid
    if ( isDoingBox() )                        // of the cursors, or the 
      stop_box( current_point, false );        // old position will be drawn
                                               // instead of erased when the
                                               // user moves the cursor (due
                                               // to XOR drawing).
    super.paint(g);
    build_object_list();
    if ( all_objects == null )
    {
      g.setColor( Color.white );
      g.drawString( "No 3D Objects", getWidth()/3, getHeight()/2 );
      return;
    }

    project();
    for ( int i = 0; i < all_objects.length; i++ )
      all_objects[ index[i] ].Draw(g);
  }

/* --------------------------- setViewTran --------------------------- */
/**
 *  Set the viewing/projection transformation to be used to project the
 *  3D objects onto the viewing screen.  The view_tran can be constructed
 *  using Tran3D.setViewMatrix().  Note: The default size of the virtual
 *  screen is 1 unit square.  If the projections of the objects on the 
 *  virtual screen will cover a larger region, or a region with a different
 *  shape, it will be necessary to call setVirtualScreenSize() to set an
 *  appropriate size for the virtual screen. 
 *
 *  NOTE: The application must call repaint() or request_painting() to project 
 *        and show the objects using the new view transform.
 *
 *  @param  view_tran  the transformation that translates objects into
 *                     the observers coordinate system and projects them
 *                     onto the 3D viewing screen.
 *
 *  @see DataSetTools.math.Tran3D
 */
  public void setViewTran( Tran3D view_tran )
  {
    tran = new Tran3D( view_tran );
    data_painted = false; 
  } 


/* ----------------------- setVirtualScreenSize ---------------------- */
/**
 *  Set the size of the virtual screen onto which the objects are projected.
 *  The virtual screen is a 2D rectangle centered at the "view reference point"
 *  perpendicular to the line of sight, with the specified width and height.
 *
 *  NOTE: The application must call repaint() or request_painting() to show
 *        the objects on the new virtual screen.
 *
 *  @param  width       The width of the virtual viewing screen.
 *  @param  height      The height of the virtual viewing screen.
 *  @param  reset_zoom  Flag indicating whether or not to reset the local
 *                      "zoomed" transform as well as the global transform.
 *
 */
  public void setVirtualScreenSize( float   width, 
                                    float   height, 
                                    boolean reset_zoom )
  {
     width  =  Math.abs(width);
     height = Math.abs(height);

     if ( width == 0 )
       width = 1;

     if ( height == 0 )
       height = 1;

     if ( reset_zoom )
       initializeWorldCoords( new CoordBounds( -width/2,  height/2, 
                                                width/2, -height/2 ) );
     else
       setGlobalWorldCoords( new CoordBounds( -width/2,  height/2, 
                                               width/2, -height/2 ) );
     data_painted = false;
  }
  

/* ----------------------------- setColors --------------------------- */
/**
 *  Change the the colors for the specified ThreeD objects.
 *
 *  NOTE: The application must call repaint() or request_painting() to show
 *        the new colors.
 * 
 *  @param  name     Name of the list of objects whose colors are to be 
 *                   changed.
 *  @param  colors   Array of colors to use for the named list of objects.
 */
  public void setColors( String name, Color colors[] )
  {
    IThreeD_Object objects[] = (IThreeD_Object[])obj_lists.get(name);

    if ( objects != null )
    {
      int n_colors = colors.length;
      if ( n_colors > objects.length )
        n_colors = objects.length;

      for ( int j = 0; j < n_colors; j++ )
        objects[j].setColor( colors[j] );

      data_painted = false; 
    }
  }


/* ----------------------------- setColor --------------------------- */
/**
 *  Change the the color for all objects in the specified list of
 *  ThreeD objects.
 *
 *  NOTE: The application must call repaint() or request_painting() to show
 *        the new colors.
 *
 *  @param  name     Name of the list of objects whose colors are to be
 *                   changed.
 *  @param  color    New color to use for the named list of objects.
 */
  public void setColors( String name, Color color )
  {
    IThreeD_Object objects[] = (IThreeD_Object[])obj_lists.get(name);

    if ( objects != null )
    {
      for ( int j = 0; j < objects.length; j++ )
        objects[j].setColor( color );

      data_painted = false;
    }
  }



/* ----------------------------- setObjects ----------------------------- */
/**
 *  Set a named list of ThreeD objects in the list of objects to be 
 *  handled by this panel, if the name is already used, the new objects
 *  will replace the old objects.
 *
 *  NOTE: The application must call repaint() or request_painting() to show
 *        the new list of objects.
 *
 *  @param  name  String identifer the array of objects being set for 
 *                this panel.
 *  @param  obj   Array of ThreeD objects to be set for this panel.
 */
 public void setObjects( String name, IThreeD_Object obj[] )
 {
                                                     // ignore degenerate cases
   if ( name == null || obj == null || obj.length <= 0 )  
     return;

   obj_lists.put( name, obj );
   obj_lists_valid = false; 
 }


/* ----------------------------- removeObjects ----------------------------- */
/**
 *  Remove a named list of ThreeD objects from the objects to be handled by
 *  this panel.  If the named list is not present, this method has no
 *  effect.
 *
 *  NOTE: The application must call repaint() or request_painting() to show
 *        the new list of objects.
 *
 *  @param  name  Unique string identifer to be used for the new array
 *                of objects being removed from this panel
 */
 public void removeObjects( String name )
 {
   obj_lists.remove( name );
   obj_lists_valid = false; 
 }


/* --------------------------------- clear -------------------------------- */
/**
 *  Remove all of the ThreeD objects from the objects to be handled by
 *  this panel.  
 *
 *  NOTE: The application must call repaint() or request_painting() to 
 *        redraw the panel after removing the objects.
 */
 public void removeObjects()
 {
   obj_lists.clear();
   obj_lists_valid = false; 
 }



/* ----------------------------- pickID ----------------------------- */
/*
 *  Return the Pick ID of the object whose projection is closest to
 *  the specified pixel, provided it is within the specified pick radius.
 *
 *  @param  x            The x coordinate of the specified pixel
 *  @param  y            The y coordinate of the specified pixel
 *  @param  pick_radius  Objects that are further away from the specified
 *                       point than the pick_radius are ignored.
 *
 *  @return  The Pick ID of the first object found that is closest to 
 *           pixel (x,y), provided it is within the pick_radius.  
 *           If no such object is found, this returns INVALID_PICK_ID. 
 */
 public int pickID( int x, int y, int pick_radius )
 {
   if ( all_objects == null || all_objects.length < 1 )
     return IThreeD_Object.INVALID_PICK_ID;

   float distance;
   float min_distance = all_objects[0].distance_to( x, y );
   int   min_index = 0;
   
   for ( int i = 1; i < all_objects.length; i++ )
   {
     distance = all_objects[i].distance_to( x, y );
     if ( distance < min_distance )
     {
       min_distance = distance;
       min_index    = i;
     }     
   }

   if ( min_distance < pick_radius )
     return all_objects[ min_index ].getPickID();
   else
     return IThreeD_Object.INVALID_PICK_ID;
 }

/* ------------------------------- project ------------------------------ */
/**
 *  Calculate the pixel coordinates of the projection of the specified 
 *  3D point, using the current viewing matrix and window transform.  This
 *  is useful for determining where to place the crosshair cursor, based on 
 *  3D positions.
 *
 *  @param  point  The 3D point that is to be mapped to the window.
 *
 *  @return  The 2D pixel coordinates on the JPanel of the projection of
 *           the specified 3D point.
 */

 public Point project( Vector3D point )
 {
   if ( tran3D_used == null || tran2D_used == null || point == null )
   {
     System.out.println("WARNING: transform null in ThreeD_JPanel.project()");
     return new Point( 0, 0 );
   }

   Vector3D proj_point = new Vector3D();
   tran3D_used.apply_to( point, proj_point );
   proj_point.standardize();

   floatPoint2D window_point = new floatPoint2D( proj_point.get()[0],
                                                 proj_point.get()[1] );
   window_point = tran2D_used.MapTo( window_point );
   return new Point( (int)window_point.x, (int)window_point.y );
 }


/* -------------------------------------------------------------------------
 *
 *  PRIVATE METHODS
 *
 */


/* ------------------------- build_object_list --------------------------- */
/**
 *  Make a single array with references to all of the objects from all of the
 *  named object lists for purposes of depth-sorting, projecting and drawing.
 */
 private void build_object_list()
 {
   if ( obj_lists_valid )                   // no need to rebuild
     return;

   if ( obj_lists.isEmpty() )               // no more objects, so clean up
   {
     index = null;
     all_objects = null;
     data_painted = false;
     return;
   }

   int n_objects = 0;
   Enumeration e = obj_lists.elements();
   while ( e.hasMoreElements() ) 
     n_objects += ( (IThreeD_Object[])(e.nextElement()) ).length;

   all_objects = new IThreeD_Object[n_objects];
   IThreeD_Object list[];
   int place = 0;
   e = obj_lists.elements();
   while ( e.hasMoreElements() ) 
   {
     list = (IThreeD_Object[])e.nextElement();
     for ( int j = 0; j < list.length; j++ )
     {
       all_objects[ place ] = list[j];
       place++;
     }
   }

   index = new int[ all_objects.length ];

   for ( int i = 0; i < index.length; i++ )
     index[i] = i;

   data_painted = false;

   obj_lists_valid = true;
   tran3D_used = null;                     // data will have to be projected
                                           // and depth sorted again. 
 }

/* ----------------------------- project ------------------------------ */

  private void project()
  {
    if ( all_objects == null )         // nothing to project
      return;
                                   // can't project using null transforms
    if ( tran == null || local_transform == null )
    {
      System.out.println("transforms null in ThreeD_JPanel");
      return;
    }
                                   // no need to recalculate projections and
                                   // do the depth sort if the same transforms
                                   // are used.
    if ( tran3D_used != null  &&  tran3D_used.equals( tran )       &&
         tran2D_used != null  &&  tran2D_used.equals( local_transform ) )
      return;

                                   // The observer to vrp distance is given by
                                   // 1/length of the vector with components
                                   // from the last row of the view tran.
    float a[][] = tran.get();
    float vrp_to_cop_dist = 1 / (float)Math.sqrt( a[3][0] * a[3][0] +
                                                  a[3][1] * a[3][1] +
                                                  a[3][2] * a[3][2]  );
    float clip_distance = clip_factor * vrp_to_cop_dist;

    for ( int i = 0; i < all_objects.length; i++ )
      all_objects[i].Project( tran, local_transform, clip_distance );

    q_sort( all_objects, index, 0, all_objects.length-1 );

    tran3D_used = new Tran3D( tran );
    tran2D_used = new CoordTransform( local_transform );
  }


/* ------------------------------- swap ---------------------------------- */

private static void swap( int index[], int i, int j )
{
   int  temp = index[i];
   index[i]  = index[j];
   index[j]  = temp;
}


/* ------------------------------ q_sort --------------------------------- */

private static void q_sort( IThreeD_Object list[], 
                            int            index[], 
                            int            start, 
                            int            end    )
{
   int   i = start;
   int   j = end;
   float key;

   if ( i >= j )                      // at most one element, so we're
     return;                          // done with this sublist

   swap( index, start, (i+j)/2 );

   key = list[ index[start] ].depth();
   while ( i < j )
   {
     while ( list[ index[i] ].depth() <= key && i < end )
       i++;
     while ( list[ index[j] ].depth() > key )
       j--;
     if ( i < j )
       swap( index, i, j );
   }
   swap( index, start, j );

   q_sort( list, index, start, j-1 );
   q_sort( list, index, j+1, end );
}


/* -------------------------------- Main ------------------------------- */
/** 
 *
 * Basic main program for testing purposes only. 
 *
 */
  public static void main(String[] args)
  {
    JFrame f = new JFrame("Test for ThreeD_JPanel");
    f.setBounds(0,0,500,500);
    ThreeD_JPanel test = new ThreeD_JPanel();
    f.getContentPane().add( test );
    f.setVisible( true );
                                                  // set up a viewing transform,
                                                  // looking at the origin, from
                                                  // (5,4,10) with the up 
                                                  // direction along the y-axis

    ViewController v_control = new ViewController();
//    v_control.setVirtualScreenSize( 10, 10 );
    v_control.setViewAngle( 50 );
    v_control.addControlledPanel( test );
                                                   // make a list of objects
                                                   // and give them to the
                                                   // 3D JPanel
    int n_objects  = 512;
    IThreeD_Object objs[] = new IThreeD_Object[n_objects];
    Vector3D       pts[]  = new Vector3D[4];
    for ( int p = 0; p < n_objects; p++ )
    {
      for ( int i = 0; i < pts.length; i++ )
        pts[i] = new Vector3D();

      float x = (float)( 4 * Math.cos( p * 2 * Math.PI / n_objects ) );
      float z = (float)( 4 * Math.sin( p * 2 * Math.PI / n_objects ) );

      pts[0].set( x+.02f,  .4f, z );
      pts[1].set( x+.02f, -.4f, z );
      pts[2].set( x-.02f, -.4f, z );
      pts[3].set( x-.02f,  .4f, z );

//    objs[p] = new Polyline( pts, Color.red );
//    objs[p] = new Polygon( pts, Color.red );
      objs[p] = new Polymarker( pts, Color.red );
      ((Polymarker)objs[p]).setSize(2);
      ((Polymarker)objs[p]).setType(Polymarker.STAR);
    }
    test.setObjects( "SAMPLE_OBJECTS", objs );
    v_control.apply( true );
                                                     // test showing frames
                                                     // with different colors
    Color colors[] = new Color[ n_objects ];
    int N_REPS = 25;  
    for ( int count = 0; count < N_REPS; count++ )
    {
      if ( count % 3 == 0 )
        colors = IndexColorMaker.getDualColorTable( 
                     IndexColorMaker.HEATED_OBJECT_SCALE, 128 ); 

      else if ( count % 3 == 1 )
        colors = IndexColorMaker.getDualColorTable( 
                     IndexColorMaker.GRAY_SCALE, 128 ); 

      else
        colors = IndexColorMaker.getDualColorTable( 
                     IndexColorMaker.NEGATIVE_GRAY_SCALE, 128 ); 

      test.setColors( "SAMPLE_OBJECTS", colors );
      test.request_painting( 200 );
    }
                                                    // test showing scene
                                                    // from different locations
    for ( int count = 0; count < N_REPS; count++ )
    {
      v_control.setCOP( new Vector3D(5+count/5.0f,
                                     4+count/5.0f,
                                     10.0f - count/5.0f) );
      v_control.apply( false );
    }
  }

}
