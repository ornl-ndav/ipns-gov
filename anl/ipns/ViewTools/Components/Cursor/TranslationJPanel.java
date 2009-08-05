/*
 * File:  TranslationJPanel.java
 *
 * Copyright (C) 2003, Mike Miller
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
 * Primary   Mike Miller <millermi@uwstout.edu>
 * Contact:  Student Developer, University of Wisconsin-Stout
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
 *  $Log$
 *  Revision 1.16  2007/07/29 20:45:14  dennis
 *  Changed local_transform and global_transform to be private
 *  in CoordJPanel class, to keep better control over who can
 *  change them, and how they can be changed.
 *
 *  Revision 1.15  2007/07/20 03:03:31  dennis
 *  Simplified logic in setGlobalPanelBounds().  Setting the global
 *  panel bounds is only needed when new data has been set, so the
 *  local viewport is also reset.
 *
 *  Revision 1.14  2007/07/13 14:20:42  dennis
 *  Added comment to clarify why a BOUNDS_CHANGED message was sent.
 *
 *  Revision 1.13  2005/06/17 20:18:40  kramer
 *
 *  Added 'requestFocus()' at the end of each mouseEntered(), mouseMoved(), ..
 *  methods.  This allows the pan view to be moved using the arrow keys.
 *  However, a better solution should be found.  For some reason at the
 *  start of these methods the PanViewControl has focus but by the end it
 *  has lost focus (possibly the drawing of the large image steals focus).
 *
 *  Revision 1.12  2005/03/21 23:26:29  millermi
 *  - Added ObjectState key STRETCH_ENABLED which required
 *    getObjectState to be added to the class to override the
 *    method from the super class.
 *
 *  Revision 1.11  2004/08/04 18:52:30  millermi
 *  - Added enableStretch() and isStretchEnabled() to turn resizing
 *    of the viewport on/off.
 *  - Added code to update local_bounds when keyboard events were used
 *    to translate the viewport.
 *  - Added BOUNDS_MOVED and BOUNDS_RESIZED messaging Strings to
 *    clarify the generic BOUNDS_CHANGED message. The BOUNDS_CHANGED
 *    message is now used when the bounds are set by a method or when
 *    neither BOUNDS_MOVED or BOUNDS_RESIZED applies.
 *
 *  Revision 1.10  2004/05/11 00:58:04  millermi
 *  - Removed unused variables.
 *
 *  Revision 1.9  2004/05/05 04:26:26  millermi
 *  - Added functionality for cursor to change when moving or
 *    stretching is allowed. This behavior is immediately visible
 *    when using the PanViewControl.
 *
 *  Revision 1.8  2004/03/12 01:33:23  millermi
 *  - Changed package and imports.
 *
 *  Revision 1.7  2003/12/15 20:49:54  millermi
 *  - changed setViewPort() parameters from ints to floats.
 *  - Now swaps bounds if passed in local bounds are not
 *    consistent with global bounds. Ex: interval 0-1 vs 1-0.
 *  - These two additions fix the initialization bug
 *    found by Dennis Mikkelson.
 *
 *  Revision 1.6  2003/11/25 23:31:27  millermi
 *  - convert...() methods now use global_transform directly
 *    instead of accessing it through a method.
 *
 *  Revision 1.5  2003/11/25 00:50:53  millermi
 *  - setViewPort() now clips the local bounds if they exceed
 *    the global bounds.
 *
 *  Revision 1.4  2003/11/21 00:12:24  millermi
 *  - Fixed bug that did not restrict stretching to only the
 *    cursor region.
 *  - Reduced sensitivity to +/- 2 pixels.
 *
 *  Revision 1.3  2003/11/18 01:02:01  millermi
 *  - Fixed bug that assumed global bounds x1 < x2 and y1 < y2,
 *    which may not always be true.
 *
 *  Revision 1.2  2003/10/29 20:31:45  millermi
 *  -Fixed java docs
 *  -Added ability to stretch
 *  -Added arrow key controls.
 *  -Added restore bounds, called by double-clicking.
 *  -Added ObjectState info
 *
 *  Revision 1.1  2003/10/27 08:47:48  millermi
 *  - Initial Version - This class was created to enable users
 *    panning options for images too large to view in the
 *    viewport.
 *
 */

package gov.anl.ipns.ViewTools.Components.Cursor;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import gov.anl.ipns.ViewTools.Panels.Transforms.*;
import gov.anl.ipns.ViewTools.Components.ObjectState;
import gov.anl.ipns.Util.Numeric.floatPoint2D;

/**
 * This class allows for the drawing of a translating box cursor.
 * It is used by the TranslationOverlay to view different regions within
 * an image too large for the viewport.
 */
public class TranslationJPanel extends CoordJPanel
{
 /**
  * "Bounds Changed" - This message String is used by ActionListeners
  * to tell listeners that the viewport or local bounds have changed.
  */
  public static final String BOUNDS_CHANGED = "Bounds Changed";
 
 /**
  * "Bounds Moved" - This message String is used to tell listeners that
  * the "viewport" or local bounds have been translated.
  */ 
  public static final String BOUNDS_MOVED = "Bounds Moved";
  
 /**
  * "Bounds Resized" - This message String is used to tell listeners that
  * the "viewport" or local bounds have been resized.
  */ 
  public static final String BOUNDS_RESIZED = "Bounds Resized";
  
 /**
  * "Cursor Changed" - This message String is used to tell listeners that
  * the mouse cursor has been changed due to boundry dragging.
  */
  public static final String CURSOR_CHANGED = "Cursor Changed";
 /* ----------------------------ObjectState Keys--------------------------*/
 /**
  * "Stretch Enabled" - This String key refers to whether or not the
  * viewport can be resized through mouse events. This ObjectState key
  * references a Boolean.
  */
  public static final String STRETCH_ENABLED = "Stretch Enabled";
  
  private BoxPanCursor box;
  private CoordBounds restore = new CoordBounds(0,0,0,0);
  private TranslationJPanel this_panel;
  private boolean stable_bounds = true;    // true as long as bounds aren't
                                           //  stretched
  private boolean stretching    = false;   // is stretching occuring...
  private boolean ignore_stretch = false;  // Should stretching be allowed.
  private boolean north_stretch = false;   // What edge is being stretched.
  private boolean east_stretch  = false;   //  By the nature of stretching,
  private boolean south_stretch = false;   //  opposite sides cannot stretch
  private boolean west_stretch  = false;   //  at the same time.
  private boolean translated = false;      // flag if the box was translated
  private Point differ = new Point(0,0);   // the x,y distance from the current
                                           // mouse point to the topleft corner
					   // of the rectangle. This allows
					   // users to translate without the 
					   // box jumping to the current point.
  
 /**
  * Constructor adds listeners to this TranslationJPanel. This JPanel is
  * also contructed to receive keyboard events.
  */ 
  public TranslationJPanel()
  {    
    super(); 
    setEventListening(false);
    this_panel = this;
    box = new BoxPanCursor(this);
    requestFocus();
    setViewPort( new CoordBounds(0,0,0,0) ); 
    addMouseListener( new SelectMouseAdapter() );
    addMouseMotionListener( new SelectMouseMotionAdapter() );
    addComponentListener( new ResizedListener() );
    addKeyListener( new TranslateKeyAdapter() );
  }
  
 /**
  * This method must override the parent method inorder to incorporate
  * additional state information.
  *
  *  @param  is_default Is desired state for project or preferences?
  *  @return The state of the TranslationJPanel.
  */ 
  public ObjectState getObjectState( boolean is_default )
  {
    ObjectState state = super.getObjectState(is_default);
    state.insert( STRETCH_ENABLED, new Boolean( isStretchEnabled() ) );
    return state;
  }
  
 /**
  * This method must override the parent method inorder to incorporate
  * sending messages after the object state has been set.
  *
  *  @param  state The new state of the JPanel
  */ 
  public void setObjectState( ObjectState state )
  {
    // Do nothing if state is null.
    if( state == null )
      return;
    super.setObjectState(state);
    Object value = state.get(STRETCH_ENABLED);
    if( value != null )
    {
      enableStretch( ((Boolean)value).booleanValue() );
    }
    send_message(BOUNDS_CHANGED);
  }
  
 /**
  * Use this method to enable/disable the stretching ability of the viewport.
  *
  *  @param  can_stretch True to enable stretching, false to disable.
  */ 
  public void enableStretch( boolean can_stretch )
  {
    ignore_stretch = !can_stretch;
  }
  
 /**
  * Use this method to find out if stretching is enabled.
  *
  *  @return True is enabled, false if disabled.
  */
  public boolean isStretchEnabled()
  {
    return !ignore_stretch;
  }
  /**
   * Set the size of the viewport. This method must be called whenever the
   * local bounds of the CoordJPanel are changed. Also call this method to
   * initialize a viewport size.
   *
   *  @param  viewport CoordBounds representing the viewable area.
   */ 
   public void setViewPort( CoordBounds viewport )
   {
      setViewPort( viewport,false);
   }
 /**
  * Set the size of the viewport. This method must be called whenever the
  * local bounds of the CoordJPanel are changed. Also call this method to
  * initialize a viewport size.
  *
  *  @param  viewport CoordBounds representing the viewable area.
  *  @param notify  if true will notify all listeners of this action
  */ 
  public void setViewPort( CoordBounds viewport, boolean notify )
  {
    setViewPort( new floatPoint2D( viewport.getX1(),viewport.getY1()), 
                 new floatPoint2D( viewport.getX2(),viewport.getY2() ), notify ); 
  }
  
  /**
   * Set the size of the viewport. This method must be called whenever the
   * local bounds of the CoordJPanel are changed. Also call this method to
   * initialize a viewport size.
   *
   *  @param  vp1 Top-left corner of the viewable area.
   *  @param  vp2 Bottom-right corner of the viewable area.
   */ 
   public void setViewPort( floatPoint2D vp1, floatPoint2D vp2 )
   {
      setViewPort( vp1,vp2,false);
   }
 /**
  * Set the size of the viewport. This method must be called whenever the
  * local bounds of the CoordJPanel are changed. Also call this method to
  * initialize a viewport size.
  *
  *  @param  vp1 Top-left corner of the viewable area.
  *  @param  vp2 Bottom-right corner of the viewable area.
  *  @param notify  if true will notify all listeners of this action
  */ 
  public void setViewPort( floatPoint2D vp1, floatPoint2D vp2, boolean notify )
  {
    //System.out.println("In tjp setViewport, vp1 = " + vp1 + " vp2 = " + vp2 );
    // Check to make sure new local bounds are within the global bounds.
    // Since it is possible for x1 > x2 and/or y1 > y2, must check this.
    CoordBounds global = getGlobalWorldCoords();
    boolean reverse_x = false;
    boolean reverse_y = false;
    if( global.getX1() > global.getX2() )
    {
      reverse_x = true;
      // swap bounds so it is consistent with global bounds
      if( vp1.x < vp2.x )
      {
        float temp = 0;
	temp = vp1.x;
	vp1.x = vp2.x;
	vp2.x = temp;
      }
    }
    if( global.getY1() > global.getY2() )
    {
      reverse_y = true;
      // swap bounds so it is consistent with global bounds
      if( vp1.y < vp2.y )
      {
        float temp = 0;
	temp = vp1.y;
	vp1.y = vp2.y;
	vp2.y = temp;
      }
    }
    
    // if local bounds are larger than new global bounds, clip local bounds
    // x range is x2 to x1
    if( reverse_x )
    {
      if( global.getX1() < vp1.x )
        vp1.x = (int)global.getX1();
      if( global.getX2() > vp2.x )
        vp2.x = (int)global.getX2();
    }
    // x range is x1 to x2
    else
    {
      if( global.getX1() > vp1.x )
        vp1.x = (int)global.getX1();
      if( global.getX2() < vp2.x )
        vp2.x = (int)global.getX2();
    
    }
    // y range is y2 to y1
    if( reverse_y )
    {
      if( global.getY1() < vp1.y )
        vp1.y = (int)global.getY1();
      if( global.getY2() > vp2.y )
        vp2.y = (int)global.getY2();
    }
    // y range is y1 to y2
    else
    {
      if( global.getY1() > vp1.y )
        vp1.y = (int)global.getY1();
      if( global.getY2() < vp2.y )
        vp2.y = (int)global.getY2(); 
    }
    setLocalWorldCoords( new CoordBounds( vp1.x, vp1.y, vp2.x, vp2.y ) );
    // bounds are assumed to be stable when set here. Set the restore value.
    restoreBounds(true);
    floatPoint2D wctopleft  = new floatPoint2D( vp1.x,
                                                vp1.y );
    floatPoint2D wcbotright = new floatPoint2D( vp2.x,
                                                vp2.y );
    // convert from wc to pixel
    Point pixeltopleft = convertToPixelPoint(wctopleft);
    Point pixelbotright = convertToPixelPoint(wcbotright);
    
    box.init( pixeltopleft, pixelbotright );
    if( notify)
       send_message(BOUNDS_CHANGED);             // NOTE: This sends the box info
                                              //       back up to the overlay
  }                                           //       so it gets drawn
  
 /**
  * Set the bounds for the entire image AND reset the local bounds to the
  * Global bounds. This method should only be called when the global bounds 
  * of the CoordJPanel are changed, or the ZOOM region is to be reset. 
  * Also call this method to initialize the global and local bounds.
  *
  *  @param  global CoordBounds representing the entire possible viewable area.
  */ 
  public void setGlobalPanelBounds( CoordBounds global )
  {
    CoordBounds copy = global.MakeCopy();
    setGlobalWorldCoords( copy );
    setViewPort( copy,false );
  }
  
 /**
  * Currently the only type of pan cursor is the box pan cursor, so this method
  * will return that cursor.
  *
  *  @return box The XOR_PanCursor responsible for the visual transformation.
  */ 
  public XOR_PanCursor getBoxCursor()
  {
    return box;
  }

 /*
  * Converts from world coordinates to a pixel point
  */
  private Point convertToPixelPoint( floatPoint2D fp )
  {
    floatPoint2D fp2d = getGlobal_transform().MapTo( fp );
    return new Point( (int)fp2d.x, (int)fp2d.y );
  }
 
 /*
  * Converts from pixel coordinates to world coordinates.
  */
  private floatPoint2D convertToWorldPoint( Point p )
  {
    return getGlobal_transform().MapFrom( new floatPoint2D(p.x, p.y) );
  }
 
 /*
  * This method will restore the bounds to the last "stable" ratio. 
  * If the bounds are stretched, the bounds are no longer considered stable.
  * This method only sets the restore value, it does not change the local_bounds
  */ 
  private void restoreBounds(boolean uselocal)
  {
    // if true, use local bounds
    if( uselocal )
      restore = getLocalWorldCoords().MakeCopy();
    // else keep dimension the same as the last stable bounds, just put it
    // around the center of the currently selected bounds.
    else
    {
      CoordBounds local = getLocalWorldCoords();
      // find center point of cursor
      floatPoint2D local_center = new floatPoint2D( 
                ( local.getX1() + (local.getX2() - local.getX1())/2 ),
                ( local.getY1() + (local.getY2() - local.getY1())/2 ) );
      // find center point of last restore
      floatPoint2D restore_center = new floatPoint2D(
                ( restore.getX1() + (restore.getX2() - restore.getX1())/2 ),
                ( restore.getY1() + (restore.getY2() - restore.getY1())/2 ) );
      // translate the center of the restore to the center of the cursor
      float x1 = restore.getX1() + (local_center.x - restore_center.x);
      float x2 = restore.getX2() + (local_center.x - restore_center.x);
      float y1 = restore.getY1() + (local_center.y - restore_center.y);
      float y2 = restore.getY2() + (local_center.y - restore_center.y);
      // let restore be the stable bounds around the center point of the cursor.
      restore = new CoordBounds( x1, y1, x2, y2 );
    }
    stable_bounds = true;
  }
  
 /*
  * Use this method to see if the cursor is on the boundary.
  */ 
  private boolean[] onBoundary( Point current )
  {
    // Array for specifying if a boundary has been entered.
    // Format:
    // boundary[0] - Is any boundary in activated?
    // boundary[1] - North
    // boundary[2] - East
    // boundary[3] - South
    // boundary[4] - West
    boolean[] boundary = new boolean[5];
    // This code will check to see if the user clicks near the boundry
    // of the cursor. If so, the user wants to grow the cursor instead of
    // translating it.
    Rectangle cursor_bounds = box.region();
    Point location = cursor_bounds.getLocation();
    // grow contains the distance in the x & y direction that the
    // cursor should "grow", either growing larger or smaller.
    Point grow = new Point(location);
    grow.x -= current.x;
    grow.y -= current.y;
    
    final int SENSITIVITY = 2;
    
    // west side
    if( Math.abs(grow.x) < SENSITIVITY )
    {
      // is the current point on the actual cursor, or just on the same
      // line.
      if( current.y > location.y - SENSITIVITY && 
          current.y < (location.y + cursor_bounds.getHeight() + SENSITIVITY) )
      {
        boundary[4] = true;
	boundary[0] = true;
      }
    }
    // north side
    if( Math.abs(grow.y) < SENSITIVITY )
    {
      // is the current point on the actual cursor, or just on the same
      // line.
      if( current.x > location.x - SENSITIVITY && 
          current.x < (location.x + cursor_bounds.getWidth() + SENSITIVITY) )
      {
        boundary[1] = true;
	boundary[0] = true;
      }
    }
    // east side
    if( Math.abs(current.x - (cursor_bounds.getWidth() + location.x) ) 
  	< SENSITIVITY )
    { 
      // is the current point on the actual cursor, or just on the same
      // line.
      if( current.y > location.y - SENSITIVITY && 
          current.y < (location.y + cursor_bounds.getHeight() + SENSITIVITY) )
      {
        boundary[2] = true;
	boundary[0] = true;
      }
    }
    // south side
    if( Math.abs(current.y - (cursor_bounds.getHeight() + location.y) ) 
  	< SENSITIVITY )
    {
      // is the current point on the actual cursor, or just on the same
      // line.
      if( current.x > location.x - SENSITIVITY && 
          current.x < (location.x + cursor_bounds.getWidth() + SENSITIVITY) )
      {
        boundary[3] = true;
	boundary[0] = true;
      }
    }
    // else not on the boundary
    return boundary;
  }

 /*
  * This class handles the "dirty" work before and after translations.
  */
  private class SelectMouseAdapter extends MouseAdapter
  {
    public void mouseClicked (MouseEvent e)
    {
      if ( e.getClickCount() == 2 )    // reset zoom region to whole array
      {
        restoreBounds(false);
        setViewPort(restore);
      }
      
      requestFocus();
    }
    
    public void mousePressed (MouseEvent e)
    {
      // This information will be used to prevent the viewport from "jumping"
      // to the current point.
      Point topcorner = box.getP1();
      differ = e.getPoint();
      differ.x -= topcorner.x;
      differ.y -= topcorner.y;
      
      // check to see if the mouse is on the boundary
      boolean[] boundary_check = onBoundary(e.getPoint());
      // if none are selected, do nothing
      if( !boundary_check[0] )
        return;
      
      // This code will check to see if the user clicks near the boundry
      // of the cursor. If so, the user wants to grow the cursor instead of
      // translating it.
      Point current = e.getPoint(); 
      Rectangle cursor_bounds = box.region();
      Point location = cursor_bounds.getLocation();
      // grow contains the distance in the x & y direction that the
      // cursor should "grow", either growing larger or smaller.
      Point grow = new Point(location);
      grow.x -= current.x;
      grow.y -= current.y;
      
      if( boundary_check[4] )
      {
	west_stretch = true;
	stretching = true;
	if( east_stretch )
	  east_stretch = false;
      }
      if( boundary_check[1] )
      {
        north_stretch = true;
	stretching = true;
	if( south_stretch )
	  south_stretch = false;
      }
      if( boundary_check[2] )
      { 
        east_stretch = true;
	stretching = true;
	if( west_stretch )
	  west_stretch = false;
      }
      if( boundary_check[3] )
      {
        south_stretch = true;
	stretching = true;
	if( north_stretch )
	  north_stretch = false;
      }
      
      // since stretching may "screw up" the bounds, save the bounds before
      // allowing for stretching. These bounds will now be used by the user
      // to get back to the last stable bounds.
      if( stretching && stable_bounds )
      {
        restoreBounds(true);
	stable_bounds = false;
      }
    }
    
    public void mouseReleased (MouseEvent e)
    {
      if( stretching && !ignore_stretch )
        send_message(BOUNDS_RESIZED);
      else if( translated )
        send_message(BOUNDS_MOVED);
      else
        send_message(BOUNDS_CHANGED);
      stretching = false;
      translated = false;
      north_stretch = false;
      east_stretch  = false;
      south_stretch = false;
      west_stretch  = false;
    }

    public void mouseEntered (MouseEvent e)
    {
      requestFocus(); 
    }
  } 
  
 /*
  * Redraw the specified cursor, giving the translating effect.
  */ 
  private class SelectMouseMotionAdapter extends MouseMotionAdapter
  {
    public void mouseDragged(MouseEvent e)
    {
      Point current = e.getPoint();
      
      Rectangle cursor_bounds = box.region(); // bounds of cursor
      Rectangle this_bounds = getBounds();    // bounds of entire jpanel
      Point grow = cursor_bounds.getLocation();
      Point location = new Point(grow);
      grow.x -= current.x;
      grow.y -= current.y;
      
      if( this_bounds.contains(current) )
      {
        //System.out.println("Pixel: " + current.toString() + "  WC: " +
        //                   convertToWorldPoint(current).toString() ); 
        if( stretching && !ignore_stretch )
        {
          if( north_stretch )
	    box.moveEdge( XOR_PanCursor.NORTH, grow.y );
          if( east_stretch )
            box.moveEdge( XOR_PanCursor.EAST, (int)( current.x -
	                  cursor_bounds.getWidth() - location.x ) );
          if( south_stretch )
            box.moveEdge( XOR_PanCursor.SOUTH, (int)( current.y -
	                  cursor_bounds.getHeight() - location.y ) );
          if( west_stretch )
            box.moveEdge( XOR_PanCursor.WEST, grow.x );
        }
        else  // if not stretching, then translating
        {      
          // this will prevent the viewport from "jumping" to the current point
          current.x -= differ.x;
          current.y -= differ.y;
              
          // IF x coordinate of current point is between the xmin and max
          // of pixel bounds, translate the box by the current point.
          // ELSE translate it to the nearest side.
          if( current.x >= this_bounds.getX() )
          {
            if( current.x + cursor_bounds.getWidth() <=
	        this_bounds.getX() + this_bounds.getWidth() )
	      translated = true;
	    else
	      current.x = (int)( this_bounds.getX() + this_bounds.getWidth() - 
	                         (cursor_bounds.getWidth() + 1) );
          }
          else
            current.x = (int)this_bounds.getX();
              
          // IF y coordinate of current point is between the y min and max
          // of pixel bounds, translate the box by the current point.
          // ELSE translate it to the nearest side.
          if( current.y >= this_bounds.getY() )
          {
            if( current.y + cursor_bounds.getHeight() <=
	        this_bounds.getY() + this_bounds.getHeight() )
	      translated = true;
	    else
	      current.y = (int)( this_bounds.getY() + this_bounds.getHeight() - 
	                         (cursor_bounds.getHeight() + 1) );
          }
          else
            current.y = (int)this_bounds.getY();
      
          // At this point, current has been adjusted to stay within the bounds.
          box.translate(current); 
	  
        } // end else !stretching
	      
        // if the translation was successful, then update the local coords.
        if( translated || stretching )
        {
          // now update the local bounds of this transjpanel after
          // the translation.
          Rectangle region = box.region();
          Point p1 = region.getLocation();
	  p1.x += 1; // correct off by one error
	  p1.y += 1;
          Point p2 = new Point(p1);
          p2.x += region.getWidth();
          p2.y += region.getHeight();
          floatPoint2D wcp1 = convertToWorldPoint(p1);
          floatPoint2D wcp2 = convertToWorldPoint(p2);
          setLocalWorldCoords( new CoordBounds( wcp1.x, wcp1.y,
                				wcp2.x, wcp2.y ) );
	  // if bounds are still consistent, reset the restore bounds.
	  if( stable_bounds )
          {
            restoreBounds(true);
          }
        } // end if translated
	if( ignore_stretch )
	  translated = true;
      } // end if contains point
      
      requestFocus();
    } // end mouseDragged()
    
   /*
    * This method is responsible for changing the cursor types when the
    * cursor is ran across the boundary.
    */ 
    public void mouseMoved( MouseEvent me )
    {
      boolean[] on_bounds = onBoundary(me.getPoint());
      if( !on_bounds[0] )
      {
        // Set the cursor to the arrow if the "panner" is the size of the
	// control. In this situation, motion is not possible/noticeable.
	if( this_panel.getGlobalWorldCoords().equals(
	    this_panel.getLocalWorldCoords() ) )
        {
	  this_panel.setCursor( Cursor.getDefaultCursor() );
	}
	// If bounds are not equal, set the cursor to the move cursor.
	else
	  this_panel.setCursor( new Cursor(Cursor.MOVE_CURSOR) );
        return;
      }
      // If stretching should be ignored, do not change the cursor.
      if( ignore_stretch )
        return;
      // If on north bound, change cursor.
      if( on_bounds[1] )
      {
        // If also on east bound, change to NE stretch cursor.
        if( on_bounds[2] )
	{
	  this_panel.setCursor( new Cursor(Cursor.NE_RESIZE_CURSOR) );
	}
        // If also on west bound, change to NW stretch cursor.
        else if( on_bounds[4] )
	{
	  this_panel.setCursor( new Cursor(Cursor.NW_RESIZE_CURSOR) );
	}
        // Else, just change to N stretch cursor.
        else
	{
	  this_panel.setCursor( new Cursor(Cursor.N_RESIZE_CURSOR) );
	}
      }
      // If on south bound, change cursor.
      else if( on_bounds[3] )
      {
        // If also on east bound, change to SE stretch cursor.
        if( on_bounds[2] )
	{
	  this_panel.setCursor( new Cursor(Cursor.SE_RESIZE_CURSOR) );
	}
        // If also on west bound, change to SW stretch cursor.
        else if( on_bounds[4] )
	{
	  this_panel.setCursor( new Cursor(Cursor.SW_RESIZE_CURSOR) );
	}
        // Else, just change to S stretch cursor.
        else
	{
	  this_panel.setCursor( new Cursor(Cursor.S_RESIZE_CURSOR) );
	}
      }
      // If only on east bound, change cursor.
      else if( on_bounds[2] )
      {
        this_panel.setCursor( new Cursor(Cursor.E_RESIZE_CURSOR) );
      }
      // If only on west bound, change cursor.
      else if( on_bounds[4] )
      {
        this_panel.setCursor( new Cursor(Cursor.W_RESIZE_CURSOR) );
      }
      
      requestFocus();
    } // end of mouseMoved()
  } // end class

 /*
  * Move the viewport up/down/left/right according to key events.
  */
  private class TranslateKeyAdapter extends KeyAdapter
  {
    public void keyPressed( KeyEvent e )
    {
      int code = e.getKeyCode();
      
      Rectangle cursor_bounds = box.region(); // bounds of cursor
      Rectangle this_bounds = getBounds();    // bounds of entire jpanel
      Point corner = cursor_bounds.getLocation();
      Point corner2 = new Point(corner);
      corner2.x += (int)cursor_bounds.getWidth();
      corner2.y += (int)cursor_bounds.getHeight();
       
      translated = true;
      if( code == KeyEvent.VK_UP )
      {
        if( this_bounds.contains( new Point( corner.x, corner.y - 1 ) ) )
	  corner.y -= 1;
      }
      else if( code == KeyEvent.VK_DOWN )
      {  
        if( this_bounds.contains( new Point( corner2.x, corner2.y + 1 ) ) )
	  corner.y += 1;   
      }
      else if( code == KeyEvent.VK_LEFT )
      {
        if( this_bounds.contains( new Point( corner.x - 1, corner.y ) ) )
	  corner.x -= 1;    
      }
      else if( code == KeyEvent.VK_RIGHT )
      {
        if( this_bounds.contains( new Point( corner2.x + 1, corner2.y ) ) )
	  corner.x += 1;
      }
      else
        translated = false;
      box.translate( corner );
            
      // if the translation was successful, then update the local coords.
      if( translated )
      {
        // now update the local bounds of this transjpanel after
        // the translation.
        Rectangle region = box.region();
        Point p1 = region.getLocation();
        p1.x += 1; // correct off by one error
        p1.y += 1;
        Point p2 = new Point(p1);
        p2.x += region.getWidth();
        p2.y += region.getHeight();
        floatPoint2D wcp1 = convertToWorldPoint(p1);
        floatPoint2D wcp2 = convertToWorldPoint(p2);
        setLocalWorldCoords( new CoordBounds( wcp1.x, wcp1.y,
        				      wcp2.x, wcp2.y ) );
        // if bounds are still consistent, reset the restore bounds.
        if( stable_bounds )
        {
          restoreBounds(true);
        }
        send_message(BOUNDS_MOVED);
      }
      
      requestFocus();
    }
  }
 
 /*
  * If the component is resized, the bounds of the cursor need to be adjusted.
  * This listener will move and resize the cursor after the component is
  * resized. 
  */ 
  private class ResizedListener extends ComponentAdapter
  {
    public void componentResized( ComponentEvent ce )
    {
      CoordBounds vp = getLocalWorldCoords();
      floatPoint2D wctopleft  = new floatPoint2D( vp.getX1(),
                                                  vp.getY1() );
      floatPoint2D wcbotright = new floatPoint2D( vp.getX2(),
                                                  vp.getY2() );
      // convert from wc to pixel
      Point pixeltopleft = convertToPixelPoint(wctopleft);
      Point pixelbotright = convertToPixelPoint(wcbotright);
    
      box.init( pixeltopleft, pixelbotright );
      send_message(BOUNDS_CHANGED);
    }  // end componentResized()   
  } // end ResizedListener  
  
/* -----------------------------------------------------------------------
 *
 * MAIN PROGRAM FOR TEST PURPOSES
 *
 */

  public static void main(String[] args)
  {
    JFrame f = new JFrame("Test for TranslationJPanel");
    f.setBounds(0,0,500,500);
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    TranslationJPanel panel = new TranslationJPanel();
    f.getContentPane().add(panel);
    f.setVisible(true);
    panel.setViewPort( new CoordBounds(50,50,100,100) );
  }  
}
