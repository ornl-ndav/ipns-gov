/*
 * File:  IThreeD_Panel.java
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
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA, and by
 * the National Science Foundation under grant number DMR-0218882.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 * $Log$
 * Revision 1.2  2007/06/11 15:03:48  dennis
 * Now uses general Objects, instead of just Strings to identify
 * specific lists of 3D objects to be drawn.  In particular, this
 * allows Integer objects to be used to identify such lists.
 *
 * Revision 1.1  2004/05/03 18:12:47  dennis
 * Initial version of interface for panels that display lists
 * of 3D objects.
 *
 *
 */

package gov.anl.ipns.ViewTools.Panels.ThreeD;

import java.awt.*;
import gov.anl.ipns.MathTools.Geometry.*;


/**
 *  Defines the interface for a ThreeD_Panel that maintains and draws a 
 *  list of named ThreeD objects.
 */ 

public interface IThreeD_Panel 
{

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
  public void setViewTran( Tran3D view_tran );


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
                                    boolean reset_zoom );
  

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
  public void setColors( Object name, Color colors[] );


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
  public void setColors( Object name, Color color );


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
 public void setObjects( Object name, IThreeD_Object obj[] );


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
 public IThreeD_Object[] getObjects( Object name );


/* ----------------------------- getAllObjects ----------------------------- */
/**
 *  Get list of all of the ThreeD objects handled by this panel.  
 *
 *  @return  Array of ThreeD objects or null if no objects have been added
 *           to the panel. 
 */
 public IThreeD_Object[] getAllObjects();


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
 public void removeObjects( Object name );


/* --------------------------------- clear -------------------------------- */
/**
 *  Remove all of the ThreeD objects from the objects to be handled by
 *  this panel.  
 *
 *  NOTE: The application must call repaint() or request_painting() to 
 *        redraw the panel after removing the objects.
 */
 public void removeObjects();


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
 public int pickID( int x, int y, int pick_radius );


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
 public IThreeD_Object pickedObject();

}
