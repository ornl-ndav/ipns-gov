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
 * Revision 1.26  2007/06/13 15:37:29  dennis
 * Added some documentation to private methods.
 *
 * Revision 1.25  2007/06/12 19:15:05  dennis
 * Significant simplification.
 * 1. No longer attempts to do it's own double buffering, but just uses
 *    Swing's built in double buffering (the default).
 * 2. No longer calls super.paint().  The call to super.paint() caused
 *    some odd flickering of other components!
 * 3. Now overides paintComponent() instead of paint().  Recent documentation
 *    states that derived classes should NOT overide paint(), but should
 *    override paintComponent().  This change avoided some corruption
 *    of the underlying data when many updates were done by asynchronously
 *    merging redraws due new frame requests from the frame controller
 *    and redraws due to the user adjusting the alt-az slider controls in
 *    the ThreeDView.
 * 4. Switched back from using TreeMap to using Hashtable to store the list
 *    of named lists of 3D objects, since thare was no performance
 *    advantage to the TreeMap in this case.
 *
 * Revision 1.24  2007/06/11 15:05:10  dennis
 * Now uses general Objects, instead of just Strings to identify
 * specific lists of 3D objects to be drawn.  In particular, this
 * allows Integer objects to be used to identify such lists.
 * Also, replaced Hashtable that stored "named" lists of
 * 3D objects with Tree of 3D objects.
 *
 * Revision 1.23  2006/11/04 20:14:04  dennis
 * Minor efficiency improvement for new non-array form of Vector3D
 *
 * Revision 1.22  2005/06/14 23:35:17  dennis
 * Now checks that the graphics object is not null in update()
 * and paint() methods.
 *
 * Revision 1.21  2004/05/06 18:58:48  dennis
 * The setObject() method now makes a new list containing references
 * to the 3D objects and stores the new list in it's hash table.
 *
 * Revision 1.20  2004/05/03 18:13:54  dennis
 * Now implements interface IThreeD_Panel.
 * Removed private method swap(,) that is no longer used.
 *
 * Revision 1.19  2004/03/17 20:26:51  dennis
 * Fixed @see tag that was broken when view components, math and
 * util were moved to gov package.
 *
 * Revision 1.18  2004/03/15 23:53:55  dennis
 * Removed unused imports, after factoring out the View components,
 * Math and other utils.
 *
 * Revision 1.17  2004/03/12 01:33:21  dennis
 * Moved to ViewTools.panels.ThreeD package
 *
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

package gov.anl.ipns.ViewTools.Panels.ThreeD;

import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import gov.anl.ipns.ViewTools.Panels.Transforms.*;
import gov.anl.ipns.ViewTools.Panels.Image.*;
import gov.anl.ipns.MathTools.Geometry.*;
import gov.anl.ipns.Util.Numeric.*;


/**
 *  A ThreeD_JPanel object maintains and draws a list of ThreeD objects.
 */ 

public class ThreeD_JPanel extends    CoordJPanel 
                           implements IThreeD_Panel,
                                      Serializable
{
  private  Hashtable       obj_lists     = null;  // Hashtable storing, object[]
                                                  // lists referenced by name 
                                                  
  private  IThreeD_Object  picked_object = null;  // last object picked
  private  IThreeD_Object  all_objects[] = null;  // array of all current 
                                                  // objects
  private  Integer         index[]       = null;  // depth sorted array of
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
  }


/* ---------------------------- paintComponent --------------------------- */
/**
 *  Draw all of the 3D objects.  This function should not be called directly by
 *  the application.  To request drawing the 3D scene, call repaint() if the
 *  drawing can be done asyncronously or call request_painting() if the
 *  application needs to wait for the drawing to complete before advancing.
 *
 *  @param  g   The graphics context to use when painting the scene.
 */
  public void paintComponent( Graphics g )
  {
    if ( !isShowing() || g == null )
      return;

    int width = (int)getWidth();
    int height = (int)getHeight();

    if ( width <= 10 )
      return;

    if ( height <= 10 )
      return;

    Graphics2D g2d = (Graphics2D)g.create();  // get copy of Graphics object
/*
    if ( isDoingCrosshair() )                 // if the system redraws this
      stop_crosshair( current_point );        // without our knowledge, this
                                              // gets rid of the cursors other-
    if ( isDoingBox() )                       // wise the old position will be
      stop_box( current_point, false );       // drawn instead of erased when
                                              // the user moves the cusor
*/                                            // (due to XOR drawing). 
     build_object_list();

    g2d.setColor( getBackground() );
    g2d.fillRect( 0, 0, width, height );

    if ( all_objects == null )
    {
      System.out.println("Setting color to WHITE");
      g2d.setColor( Color.WHITE );
      g2d.drawString( "No 3D Objects", getWidth()/3, getHeight()/2 );
    }
    else
    {
      project();
      for ( int i = 0; i < all_objects.length; i++ )
        all_objects[ index[i] ].Draw(g2d);
    }
    g2d.dispose();
    data_painted = true;
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
 *  @see gov.anl.ipns.MathTools.Geometry.Tran3D
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
  public void setColors( Object name, Color colors[] )
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
  public void setColors( Object name, Color color )
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
 public void setObjects( Object name, IThreeD_Object obj[] )
 {
                                                     // ignore degenerate cases
   if ( name == null || obj == null || obj.length <= 0 )  
     return;

   IThreeD_Object new_obj[] = new IThreeD_Object[ obj.length ];
   for ( int i = 0; i < obj.length; i++ )
     new_obj[i] = obj[i];

   obj_lists.put( name, new_obj );
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
 public IThreeD_Object[] getObjects( Object name )
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
 public void removeObjects( Object name )
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

   float temp[] = proj_point.get();
   floatPoint2D window_point = new floatPoint2D( temp[0], temp[1] );
   window_point = tran2D_used.MapTo( window_point );
   return new Point( (int)window_point.x, (int)window_point.y );
 }


/* -------------------------------------------------------------------------
 *
 *  PRIVATE METHODS
 *
 */

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
  private void request_painting( int time_ms )
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

   index = new Integer[ all_objects.length ];

   for ( int i = 0; i < index.length; i++ )
     index[i] = new Integer(i);

   data_painted = false;

   obj_lists_valid = true;
   tran3D_used = null;                     // data will have to be projected
                                           // and depth sorted again. 
 }


/* ----------------------------- project ------------------------------ */
/**
 *  This method will project all 3D objects onto the virtual 2D viewing 
 *  screen so that their projections can be drawn.  
 */
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

    Arrays.sort( index, new DepthComparator( all_objects ) );

    tran3D_used = new Tran3D( tran );
    tran2D_used = new CoordTransform( local_transform );
  }


/* ------------------------ DepthComparator ---------------------------- */
/**
 *  This class is used by the Arrays.sort() method to order the objects
 *  to be drawn based on their depth in the scene.  After sorting, the
 *  objects will be drawn from back to front so that objects in front will
 *  overwrite objects in back.
 */
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
    int i1 = (Integer)o1;
    int i2 = (Integer)o2;
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
