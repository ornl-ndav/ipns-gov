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
 *           Menomonie, WI 54751, USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 * $Log$
 * Revision 1.16  2003/10/08 22:24:31  dennis
 * Put two debug prints inside of "if (debug) " statements.
 *
 * Revision 1.15  2003/10/02 20:27:09  dennis
 * Now draws objects to an offscreen buffer first, then copies
 * the offscreen buffer to the panel.  If the panel is exposed,
 * the offscreen buffer is used to redraw the panel, rather
 * than recalculating and redrawing the 3D objects.  In addition
 * to speeding up the redraw in the case of expose events,
 * this also fixes the synchonization problem with the 3D viewer.
 * When stepping through a sequence of frames, no frames are
 * missed.
 *
 * Revision 1.14  2003/03/27 22:10:40  dennis
 * Added methods getObjects() and getAllObjects() to get references
 * to the list of objects displayed in a ThreeD_JPanel.
 *
 * Revision 1.13  2002/11/27 23:12:53  pfpeterson
 * standardized header
 *
 * Revision 1.12  2002/11/26 19:51:34  dennis
 * Now preserves aspect ratio by using the setPreserveAspectRatio()
 * method on the underlying CoordJPanel.
 *
 * Revision 1.11  2002/10/29 23:48:06  dennis
 * Added method, pickedObject(), to get the last object that was picked.
 *
 * Revision 1.10  2002/07/31 16:42:31  dennis
 * Now uses Java's built in sort instead of customized Q-Sort
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
                                                  
  private  IThreeD_Object  picked_object = null;  // last object picked
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

  private Image buffer;                            // use an off-screen buffer
  private int   buffer_width  = -1;                // to draw to and then
  private int   buffer_height = -1;                // paing by copying the 
  private Graphics graphics_buffer;                // off-screen buffer to the
                                                   // CoordJPanel 

  private boolean debug = false;


/* --------------------- Default Constructor ------------------------------ */
/**
 *  Construct a default ThreeD_JPanel with an empty list of ThreeD objects.
 */
  public ThreeD_JPanel()
  { 
    super();

    obj_lists = new Hashtable();

    setPreserveAspectRatio( true );
    setVirtualScreenSize( 1, 1, true );
    if ( isVisible() )          // try to preserve aspect ratio, assuming size>0
    {
      Dimension size = getSize();
      if ( size.width > 0 && size.height > size.width )
        setVirtualScreenSize( 1, size.height/(float)size.width, true );
              
      if ( size.height > 0 && size.width > size.height )
        setVirtualScreenSize( size.width/(float)size.height, 1, true );
    }

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
      if ( !isVisible() )
        return;

      if ( data_painted )               // nothing to do
        return;

      repaint();

      while ( !data_painted )           // wait till it's done painting
      try
      {
        Thread.sleep( time_ms );
      }
      catch( Exception e )
      {
        System.out.println("Exception while sleeping in request_painting "+e);
      }
  }


   /**
    *  This repaint method just calls update() so that the offscreen buffer
    *  is used for drawing.
    */
   public void repaint()
   {
     if ( debug )
       System.out.println("START REPAINT");

     update( getGraphics() );
   }

   /**
    *  This update method creates a new off screen buffer, if the buffer 
    *  doesn't exist, or is of the wrong size, then calls paint(), passing
    *  in the off screen buffer, and finally draws the off screen buffer
    *  image to the specified Graphics object g.
    */
   public void update( Graphics g )
   {
      ElapsedTime timer = null;
      if ( debug )
      {
        timer = new ElapsedTime();
        timer.reset();
      }

      if ( !isVisible() )
        return;

      int width = (int)getWidth();
      int height = (int)getHeight();

      if ( width <= 10 )
        return;

      if ( height <= 10 )
        return;

      // Create an offscreen image and then get its
      // graphics context
      if ( buffer == null || width != buffer_width || height != buffer_height )
      {
        buffer = createImage( width, height );
        buffer_width  = width;
        buffer_height = height;
        graphics_buffer = buffer.getGraphics();
      }

      paint( graphics_buffer );          // paint to the off screen buffer 
                                         // then just draw the offscreen 
                                         // image onto the screen
      g.drawImage(buffer, 0, 0, this);

      if ( debug && timer != null )
        System.out.println("Update screen took: " + timer.elapsed() );
   }



/* --------------------------------- paint ------------------------------- */
/**
 *  Draw all of the 3D objects.  This function should not be called directly by
 *  the application.  To request drawing the 3D scene, call repaint() if the
 *  drawing can be done asyncronously or call request_painting() if the
 *  application needs to wait for the drawing to complete before advancing.
 *
 *  @param  g   The graphics context to use when painting the scene.
 *              If g is the graphics buffer, the objects will be projected
 *              and drawn onto the buffer.  If g is the graphics context for
 *              this ThreeD_JPanel, the offscreen buffer will be drawn to
 *              the panel, provided the buffer exists and is valid. 
 *              If there is no valid buffer, the objects will be projected
 *              and drawn directly.
 */
  public void paint( Graphics g )
  {
    if ( !isVisible() )
      return;

    int width = (int)getWidth();
    int height = (int)getHeight();

    if ( width <= 10 )                      
      return;

    if ( height <= 10 )
      return;

    ElapsedTime timer = null;

    if ( (Object)g != (Object)graphics_buffer )
    {
                                              // the system sometimes calls
                                              // paint directly, if for example 
                                              // a window is exposed.  Use our
                                              // buffer, if possible.
      if ( graphics_buffer != null         && 
           width           == buffer_width && 
           height          == buffer_height )
      {
        g.drawImage(buffer, 0, 0, this); 
        return;
      }
      else
        System.out.println("3D JPANEL NOT USING BUFFER!!!!!!" );
    }

    if ( debug )
    {
      if ( (Object)g != (Object)graphics_buffer )
        System.out.println("paint() called for JPanel, not graphics_buffer");
      else
        System.out.println("paint() called for graphics_buffer");

      timer = new ElapsedTime();
      timer.reset();
    }

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

    if ( debug && timer != null )
      System.out.println("actually painting took: " + timer.elapsed() );
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


/* ----------------------------- getObjects ----------------------------- */
/**
 *  Get a named list of ThreeD objects from the list of objects to be
 *  handled by this panel.  If the named set of objects is not in the list,
 *  this returns null.
 *
 *  @param  name  String identifer the array of objects being requested from
 *                this panel.
 *
 *  @return  Array of ThreeD objects or null if the named objects don't
 *           exit.
 */
 public IThreeD_Object[] getObjects( String name )
 {                                                // ignore degenerate cases
   if ( name == null )
     return null;

   IThreeD_Object objects[] = (IThreeD_Object[])obj_lists.get(name);
   if ( objects == null || objects.length == 0 )
     return objects; 
                                                  // if there is a valid list
                                                  // return a copy of it
   IThreeD_Object result[] = new IThreeD_Object[ objects.length ];
   System.arraycopy( objects, 0, result, 0, objects.length );
   return result;
 }


/* ----------------------------- getAllObjects ----------------------------- */
/**
 *  Get list of all of the ThreeD objects handled by this panel.  
 *
 *  @return  Array of ThreeD objects or null if no objects have been added
 *           to the panel. 
 */
 public IThreeD_Object[] getAllObjects()
 {
   IThreeD_Object result[] = null;

   if ( !obj_lists_valid )
     build_object_list();

   if ( obj_lists_valid && all_objects != null )
   {
     result = new IThreeD_Object[ all_objects.length ];
     System.arraycopy( all_objects, 0, result, 0, result.length );
     return result;
   }
   else
     return null;   
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
   {
     picked_object = all_objects[ min_index ];
     return all_objects[ min_index ].getPickID();
   }
   else
   {
     picked_object = null;
     return IThreeD_Object.INVALID_PICK_ID;
   }
 }


/* ---------------------------- pickedObject ----------------------------- */
/*
 *  Return a reference to the object that was last picked by a call to
 *  pickID(,,).  The pickID(,,) method must have been previously called and
 *  returned a valid pick ID for this to be valid.  Otherwise it will return
 *  null.
 *
 *  @return  The IThreeD_Object that was last picked by pickID(,,) or null
 *           if the last attempt at picking returned INVALID_PICK_ID.
 *           If no such object is found, this returns INVALID_PICK_ID.
 */
 public IThreeD_Object pickedObject()
 {
   return picked_object;
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
   if ( debug )
     System.out.println("Doing projection ...." );

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
    if ( debug )
      System.out.println("PROJECT()");

    if ( all_objects == null )         // nothing to project
      return;
                                   // can't project using null transforms
    if ( tran == null || local_transform == null )
    {
      if ( debug )
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

    JavaSort( all_objects, index );

    tran3D_used = new Tran3D( tran );
    tran2D_used = new CoordTransform( local_transform );
  }


/* ------------------------------- swap ---------------------------------- */

private void swap( int index[], int i, int j )
{
   int  temp = index[i];
   index[i]  = index[j];
   index[j]  = temp;
}


/* -------------------------- JavaSort -------------------------------- */

private void JavaSort( IThreeD_Object list[],
                       int            index[] )
{
  Integer Index[] = new Integer[ index.length ];   // make list of Integer
  for ( int i = 0; i < index.length; i++ )         // objects
    Index[i] = new Integer(i);

  Arrays.sort( Index, new DepthComparator( list ) );

  for ( int i = 0; i < index.length; i++ )         // copy back to int[] list 
    index[i] = Index[i].intValue();
}


/* ------------------------ DepthComparator ---------------------------- */

private class DepthComparator implements Serializable, 
                                         Comparator 
{
  IThreeD_Object list[];

  public DepthComparator( IThreeD_Object list[] )
  {
    this.list = list;
  }

  public int compare( Object o1, Object o2 )
  {
    int i1 = ((Integer)o1).intValue();
    int i2 = ((Integer)o2).intValue();
    float d1 = list[ i1 ].depth();
    float d2 = list[ i2 ].depth();
    if ( d1 < d2 ) 
      return -1;
    else if ( d1 > d2 )
      return 1;
    else
      return 0;
  }
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
    v_control.setViewAngle( 120 );
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
      test.repaint();
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
