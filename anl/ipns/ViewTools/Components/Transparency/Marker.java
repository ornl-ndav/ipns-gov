/*
 * File: Marker.java
 *
 * Copyright (C) 2004, Mike Miller
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
 * This work was supported by the National Science Foundation under grant
 * number DMR-0218882, and by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.4  2004/04/07 20:41:47  millermi
 *  - Constructed parallel arrays for String and int code. Also
 *    added methods to easily convert from one to the other.
 *
 *  Revision 1.3  2004/04/07 01:19:51  millermi
 *  - Added get/set methods.
 *  - Added toString()
 *
 *  Revision 1.2  2004/04/02 20:58:33  millermi
 *  - Fixed javadoc errors
 *
 *  Revision 1.1  2004/03/26 21:28:59  millermi
 *  - Initial Check in - Allows users to programmatically
 *    place markers at points of interest.
 *
 */
package gov.anl.ipns.ViewTools.Components.Transparency;

import java.awt.*;

import gov.anl.ipns.ViewTools.Panels.Transforms.*;
import gov.anl.ipns.Util.Numeric.floatPoint2D;

/**
 * This class is a datastructure for grouping together all of the information
 * necessary for making a marker.
 *
 *  @see gov.anl.ipns.ViewTools.Components.Transparency.MarkerOverlay
 */
public class Marker implements java.io.Serializable
{
 //*************** Static variables for Marker characteristics *****************
 /**
  * 0 - If you want the marker to resize when zooming takes place,
  * use this variable.
  */
  public static final int RESIZEABLE = 0;
  
 /**
  * If you want the marker to maintain a consistent size when zooming
  * takes place, use this variable.
  */
  public static final int STATIC = 1;
  
 //******************* Static variables for Marker Types ***********************
 /**
  * 0 - This is the marker code for drawing a plus symbol (+) at a specified
  * point.
  */
  public static final int PLUS   = 0;
  
 /**
  * 1 - This is the marker code for drawing an x at a specified
  * point.
  */
  public static final int X      = 1;
  
 /**
  * 2 - This is the marker code for drawing a * at a specified
  * point.
  */
  public static final int STAR   = 2;
  
 /**
  * 3 - This is the marker code for drawing a box centered at a specified
  * point.
  */
  public static final int BOX    = 3;
  
 /**
  * 4 - This is the marker code for drawing a circle centered at a specified
  * point.
  */
  public static final int CIRCLE = 4;
  
 /**
  * 5 - This is the marker code for drawing a vertical dash centered at
  * a specified point.
  */
  public static final int VDASH  = 5;
  
 /**
  * 6 - This is the marker code for drawing a horizontal dash centered at
  * a specified point.
  */
  public static final int HDASH  = 6;
  
 /**
  * 7 - This is the marker code for drawing a negative sloped dash centered at
  * a specified point.
  */
  public static final int NDASH  = 7;
  
 /**
  * 8 - This is the marker code for drawing a positive sloped dash centered at
  * a specified point.
  */
  public static final int PDASH  = 8;
  
 /**
  * 10 - This is the marker code for drawing a vertical line spanning the
  * vertical extents of the image and passing through the specified point.
  * The size specified in the draw() has no affect on this marker.
  */
  public static final int VLINE  = 10;
  
 /**
  * 11 - This is the marker code for drawing a horizontal line spanning the
  * horizontal extents of the image and passing through the specified point.
  * The size specified in the draw() has no affect on this marker.
  */
  public static final int HLINE  = 11;
  
 /**
  * 12 - This is the marker code for drawing a line angled at 45 degrees with
  * a negative slope (\). The line extends to the bounds of the image.
  * The size specified in the draw() has no affect on this marker.
  */
  public static final int NLINE  = 12;
  
 /**
  * 13 - This is the marker code for drawing a line angled at 45 degrees with
  * a positive slope (/). The line extends to the bounds of the image.
  * The size specified in the draw() has no affect on this marker.
  */
  public static final int PLINE  = 13;
  //************************* Private Data Members *****************************
  private int markertype = PLUS;     // initialize the markertype to be +
  private floatPoint2D[] locations;  // marker locations in world coords.
  private Color markercolor = Color.black;
  private float radius = -1f;
  private int markerscaling = RESIZEABLE;
  private CoordTransform pixel_to_world = new CoordTransform();
  private boolean transform_set = false;
  private int size = -1;  // this will be the radius in pixel coords.
  // string_list and int_list are parallel arrays,
  // THEY MUST BE KEPT SYNCHORNIZED.
  private static String[] string_list = { "Plus(+)",
                                   	  "X(x)",
				   	  "Star(*)",
				   	  "Box([])",
                                   	  "Circle(o)",
				   	  "Vertical Dash(|)",
				   	  "Horizontal Dash(-)",
				   	  "Negative Slope Dash(\\)",
				   	  "Positive Slope Dash(/)",
				   	  "Vertical Line(|)",
				   	  "Horizontal Line(-)",
				   	  "Negative Slope Diagonal Line(\\)",
				   	  "Positive Slope Diagonal Line(/)" };
  private static int[] int_list = { PLUS,
                           	    X,
			   	    STAR,
			   	    BOX,
			   	    CIRCLE,
			   	    VDASH,
			   	    HDASH,
			   	    NDASH,
			   	    PDASH,
			   	    VLINE,
			   	    HLINE,
			   	    NLINE,
			   	    PLINE };
 
 /**
  * Constructor for marker class, instance for multiple similar markers.
  *
  *  @param  type The type of marker desired.
  *  @param  locales The world coord. location for each marker.
  *  @param  color The color of all the markers specified by this instance.
  *  @param  radius The radius of the circle circumscribing the marker in
  *                 world coordinates.
  *  @param  behave_on_zoom How the cursor reacts to zooming, either resizes
  *                         or maintains the same size.
  */ 
  public Marker( int type, floatPoint2D[] locales, Color color,
                 float radius, int behave_on_zoom )
  {
    init( type, locales, color, radius, behave_on_zoom );
  }
 
 /**
  * Constructor for marker class, instance for one marker.
  *
  *  @param  type The type of marker desired.
  *  @param  locale The world coord. location for each marker.
  *  @param  color The color of all the markers specified by this instance.
  *  @param  radius The radius of the circle circumscribing the marker in
  *                 world coordinates.
  *  @param  behave_on_zoom How the cursor reacts to zooming, either resizes
  *                         or maintains the same size.
  */ 
  public Marker( int type, floatPoint2D locale, Color color,
                 float radius, int behave_on_zoom )
  {
    floatPoint2D[] locArray = new floatPoint2D[1];
    locArray[0] = locale;
    init( type, locArray, color, radius, behave_on_zoom );
  }
 
 /*
  * Factor out the functionality of both constructors.
  */ 
  private void init( int type, floatPoint2D[] locales, Color color,
                     float radius, int behave_on_zoom  )
  {
    setMarkerType(type);
    locations = locales;
    setColor(color);
    resize(radius);
    setBehavior(behave_on_zoom);
  } // end of init.
  
 /**
  * This method must be called at least once before the draw() routine
  * is used. This transform has pixel bounds as it's source and world bounds
  * as it's destination.
  */ 
  public void setCurrentTransform( CoordTransform trans )
  {
    pixel_to_world = trans;
    transform_set = true;
  }
  
 /**
  * Set the color of all the markers specified by this instance.
  *
  *  @return The color of the marker.
  */ 
  public Color getColor()
  {
    return markercolor;
  }
  
 /**
  * Set the color of all the markers specified by this instance.
  *
  *  @param  color The color of the markers.
  */ 
  public void setColor( Color color )
  {
    if( color != null )
      markercolor = color;
  }
  
 /**
  * The size refers to the radius of the circle circumscribing the marker.
  * This size should be in world coordinates.
  *
  *  @return The radius of the circle circumscribing the marker in
  *          world coordinates.
  */
  public float getSize()
  {
    return radius;
  }
  
 /**
  * The size refers to the radius of the circle circumscribing the marker.
  * This size should be in world coordinates.
  *
  *  @param  new_size The radius of the circle circumscribing the marker in
  *                   world coordinates.
  */
  public void resize( float new_size )
  {
    // make sure radius is valid.
    if( new_size < 0 )
      return;
    radius = new_size;
    // convert size to pixel coordinates since it is in world coords.
    floatPoint2D base = pixel_to_world.MapFrom( 
    			  new floatPoint2D(0,0) );
    floatPoint2D extent = pixel_to_world.MapFrom( 
    			  new floatPoint2D(radius,radius) );
    // since distortion may happen during zooming, take the radius
    // from a 45 degree angle to help keep aspect ratio of the marker.
    float xpix_extent = extent.x - base.x;
    float ypix_extent = extent.y - base.y;
    // Following formula: r=sqrt( x^2 + y^2 )
    float pixradius = (float)Math.sqrt( Math.pow(xpix_extent,2) + 
    				     Math.pow(ypix_extent,2) );
    size = Math.round(pixradius);
  }
  
 /**
  * Get the visual display type of the marker. The marker types are
  * specified by static variables defined in this class.
  *
  *  @return The type of marker to be diplayed.
  */
  public int getMarkerType()
  {
    return markertype;
  }
  
 /**
  * Set the visual display type of the marker. The marker types are
  * specified by static variables defined in this class.
  *
  *  @param  type The type of marker to be diplayed.
  */
  public void setMarkerType( int type )
  {
    // make sure type is a valid choice.
    if( isValidCode(type) )
    {
      markertype = type;
    }
  }
  
 /**
  * Get how the marker reacts to zooming. Two options are currently supported,
  * resize on zoom (RESIZEABLE) and maintain size on zoom (STATIC).
  *
  *  @return How the marker reacts to zooming.
  */
  public int getBehavior()
  {
    return markerscaling;
  }
  
 /**
  * Set how the marker reacts to zooming. Two options are currently supported,
  * resize on zoom (RESIZEABLE) and maintain size on zoom (STATIC).
  *
  *  @param  behave_on_zoom  How the marker reacts to zooming.
  */
  public void setBehavior( int behave_on_zoom )
  {
    if( behave_on_zoom >= 0 && behave_on_zoom < 2 )
      markerscaling = behave_on_zoom;
  }
  
 /**
  * This method is used to quickly convert a marker type int code to its
  * corresponding String representation. Null is returned if 
  *
  *  @param  code The marker type int code, specified by static variables
  *               in this class.
  *  @return The String representation of the specified code, null if not found.
  */
  public static String intCodeToString( int code )
  {
    // check to make sure it is a valid code
    if( !isValidCode(code) )
      return null;
    int index = 0;
    while( index < int_list.length && int_list[index] != code )
      index++;
    return string_list[index];
  }
  
 /**
  * This method is used to quickly convert a marker type String returned
  * by toString() or intCodeToString() to its int code specified by static
  * variables in this class. If string_rep is not found, -1 will be returned.
  *
  *  @param  string_rep The marker type String, specific to this class.
  *  @return The int code for the marker, -1 if not found.
  */
  public static int stringToIntCode( String string_rep )
  {
    int index = 0;
    while(index < string_list.length && !string_list[index].equals(string_rep))
      index++;
    // if not found, return -1
    if( index == string_list.length )
      return -1;
    return int_list[index];
  }
  
 /**
  * Give the list of possible marker types, represented by Strings.
  * This list is synchronized with the array of marker type int codes,
  * so the index from the string type array can be used to get the int code.
  *
  *  @return The array of marker types expressed as Strings.
  */
  public static String[] getStringArray()
  {
    return string_list;
  }
  
 /**
  * Give the list of possible marker types, represented by int codes.
  * This list is synchronized with the array of marker type Strings,
  * so the index from the int code array can be used to get the String.
  *
  *  @return The array of marker types expressed as an int.
  */
  public static int[] getIntCodeArray()
  {
    return int_list;
  }
  
 /**
  * Use this command to draw the markers on the overlay.
  *
  *  @param  g2d The graphics object where the marker will be drawn..
  */
  public void draw( Graphics2D g2d )
  {
    //System.out.println("Type: " + toString());
    //System.out.println("MarkerScaling: " + getBehavior());
    //System.out.println("Color: " + getColor());
    //System.out.println("Size: " + radius);
    
    // make sure the pixel_to_world transform has been set before drawing.
    if( !transform_set )
    {
      System.out.println("Error in Marker.draw() - Marker." +
                         "setCurrentTransform() must be called at least " +
                         "once prior to the draw() method call.");
      return;
    }
    // set color of Graphics2D object
    Color original_color = g2d.getColor(); // preserve the original color.
    g2d.setColor(markercolor);
    // convert size to pixel coordinates since it is in world coords.
    floatPoint2D base = pixel_to_world.MapFrom( 
    			  new floatPoint2D(0,0) );
    floatPoint2D extent = pixel_to_world.MapFrom( 
    			  new floatPoint2D(radius,radius) );
    // since distortion may happen during zooming, take the radius
    // from a 45 degree angle to help keep aspect ratio of the marker.
    float xpix_extent = extent.x - base.x;
    float ypix_extent = extent.y - base.y;
    // Following formula: r=sqrt( x^2 + y^2 )
    float pixradius = (float)Math.sqrt( Math.pow(xpix_extent,2) + 
    				     Math.pow(ypix_extent,2) );
    // If size has already been set and the marker is to be static,
    // do not reset the size.
    if( !(size > 0 && markerscaling == STATIC) )
      size = Math.round(pixradius);
    
    if( markertype == PLUS )
    {
      Point pix_locale = new Point();
      for( int i = 0; i < locations.length; i++ )
      {
        pix_locale = (pixel_to_world.MapFrom(locations[i])).toPoint();
	// draw horizontal and vertical component of PLUS
        g2d.drawLine( pix_locale.x - size, pix_locale.y,
	              pix_locale.x + size, pix_locale.y );
        g2d.drawLine( pix_locale.x, pix_locale.y - size,
	              pix_locale.x, pix_locale.y + size );
      }
    }
    else if( markertype == X )
    {
      Point pix_locale = new Point();
      for( int i = 0; i < locations.length; i++ )
      {
        pix_locale = (pixel_to_world.MapFrom(locations[i])).toPoint();
	// draw X
        g2d.drawLine( pix_locale.x - size, pix_locale.y - size,
	              pix_locale.x + size, pix_locale.y + size );
        g2d.drawLine( pix_locale.x - size, pix_locale.y + size,
	              pix_locale.x + size, pix_locale.y - size );
      }    
    }
    else if( markertype == STAR )
    {
      Point pix_locale = new Point();
      // STAR is combo of PLUS and X
      for( int i = 0; i < locations.length; i++ )
      {
        pix_locale = (pixel_to_world.MapFrom(locations[i])).toPoint();
	// draw PLUS
        g2d.drawLine( pix_locale.x - size, pix_locale.y,
	              pix_locale.x + size, pix_locale.y );
        g2d.drawLine( pix_locale.x, pix_locale.y - size,
	              pix_locale.x, pix_locale.y + size );
	// draw X
        g2d.drawLine( pix_locale.x - size, pix_locale.y - size,
	              pix_locale.x + size, pix_locale.y + size );
        g2d.drawLine( pix_locale.x - size, pix_locale.y + size,
	              pix_locale.x + size, pix_locale.y - size );
      }
    }
    else if( markertype == BOX )
    {
      Point pix_locale = new Point();
      for( int i = 0; i < locations.length; i++ )
      {
        pix_locale = (pixel_to_world.MapFrom(locations[i])).toPoint();
	// draw X
        g2d.drawRect( pix_locale.x - size, pix_locale.y - size,
	              2 * size, 2 * size );
      }    
    }
    else if( markertype == CIRCLE )
    {
      Point pix_locale = new Point();
      for( int i = 0; i < locations.length; i++ )
      {
        pix_locale = (pixel_to_world.MapFrom(locations[i])).toPoint();
	// draw X
        g2d.drawOval( pix_locale.x - size, pix_locale.y - size,
	              2 * size, 2 * size );
      } 
    }
    else if( markertype == VDASH )
    {
      Point pix_locale = new Point();
      for( int i = 0; i < locations.length; i++ )
      {
        pix_locale = (pixel_to_world.MapFrom(locations[i])).toPoint();
	// draw vertical dash
        g2d.drawLine( pix_locale.x, pix_locale.y - size,
	              pix_locale.x, pix_locale.y + size );
      }
    }
    else if( markertype == HDASH )
    {
      Point pix_locale = new Point();
      for( int i = 0; i < locations.length; i++ )
      {
        pix_locale = (pixel_to_world.MapFrom(locations[i])).toPoint();
	// draw horizontal dash
        g2d.drawLine( pix_locale.x - size, pix_locale.y,
	              pix_locale.x + size, pix_locale.y );
      }
    }
    else if( markertype == NDASH )
    {
      Point pix_locale = new Point();
      for( int i = 0; i < locations.length; i++ )
      {
        pix_locale = (pixel_to_world.MapFrom(locations[i])).toPoint();
	// draw negative sloping dash
        g2d.drawLine( pix_locale.x - size, pix_locale.y - size,
	              pix_locale.x + size, pix_locale.y + size);
      }
    }
    else if( markertype == PDASH )
    {
      Point pix_locale = new Point();
      for( int i = 0; i < locations.length; i++ )
      {
        pix_locale = (pixel_to_world.MapFrom(locations[i])).toPoint();
	// draw positive sloping dash
        g2d.drawLine( pix_locale.x - size, pix_locale.y + size,
	              pix_locale.x + size, pix_locale.y - size);
      }
    }
    else if( markertype == VLINE )
    {
      Point pix_locale = new Point();
      CoordBounds pixel_bounds = pixel_to_world.getSource();
      for( int i = 0; i < locations.length; i++ )
      {
        pix_locale = (pixel_to_world.MapFrom(locations[i])).toPoint();
	// draw vertical line independent of the size constraint
        g2d.drawLine( pix_locale.x, (int)pixel_bounds.getY1(),
	              pix_locale.x, (int)pixel_bounds.getY2() );
      } 
    }
    else if( markertype == HLINE )
    {
      Point pix_locale = new Point();
      CoordBounds pixel_bounds = pixel_to_world.getSource();
      for( int i = 0; i < locations.length; i++ )
      {
        pix_locale = (pixel_to_world.MapFrom(locations[i])).toPoint();
	// draw vertical line independent of the size constraint
        g2d.drawLine( (int)pixel_bounds.getX1(), pix_locale.y,
	              (int)pixel_bounds.getX2(), pix_locale.y );
      } 
    }
    else if( markertype == NLINE )
    {
      Point pix_locale = new Point();
      CoordBounds pixel_bounds = pixel_to_world.getSource();
      int dist_to_top_left = 0;
      int dist_to_bottom_right = 0;
      for( int i = 0; i < locations.length; i++ )
      {
        pix_locale = (pixel_to_world.MapFrom(locations[i])).toPoint();
	
	// Figure out if top or left side is bound of line. Assume
	// that left side is closer to point than top.
	dist_to_top_left = pix_locale.x - (int)pixel_bounds.getX1();
	// Is top closer than left side? If so, change dist value.
	if( dist_to_top_left > (pix_locale.y - (int)pixel_bounds.getY1()) )
	  dist_to_top_left = pix_locale.y - (int)pixel_bounds.getY1();
	
	// Figure out if bottom or right side is bound of line. Assume
	// that right side is closer to point than bottom.
	dist_to_bottom_right = (int)pixel_bounds.getX2() - pix_locale.x;
	// Is bottom closer than right side? If so, change dist value.
	if( dist_to_bottom_right > ((int)pixel_bounds.getY2() - pix_locale.y) )
	  dist_to_bottom_right = (int)pixel_bounds.getY2() - pix_locale.y;
	
	// Draw line at 45 degrees, sloping downward.
        g2d.drawLine( pix_locale.x - dist_to_top_left, 
	              pix_locale.y - dist_to_top_left,
	              pix_locale.x + dist_to_bottom_right,
		      pix_locale.y + dist_to_bottom_right );
      } 
    }
    else if( markertype == PLINE )
    {
      Point pix_locale = new Point();
      CoordBounds pixel_bounds = pixel_to_world.getSource();
      int dist_to_top_right = 0;
      int dist_to_bottom_left = 0;
      for( int i = 0; i < locations.length; i++ )
      {
        pix_locale = (pixel_to_world.MapFrom(locations[i])).toPoint();
	
	// Figure out if top or right side is bound of line. Assume
	// that right side is closer to point than top.
	dist_to_top_right = (int)pixel_bounds.getX2() - pix_locale.x;
	// Is top closer than right side? If so, change dist value.
	if( dist_to_top_right > (pix_locale.y - (int)pixel_bounds.getY1()) )
	  dist_to_top_right = pix_locale.y - (int)pixel_bounds.getY1();
	
	// Figure out if bottom or right side is bound of line. Assume
	// that right side is closer to point than bottom.
	dist_to_bottom_left = pix_locale.x - (int)pixel_bounds.getX1();
	// Is bottom closer than right side? If so, change dist value.
	if( dist_to_bottom_left > ((int)pixel_bounds.getY2() - pix_locale.y) )
	  dist_to_bottom_left = (int)pixel_bounds.getY2() - pix_locale.y;
	
	// Draw line at 45 degrees, sloping downward.
        g2d.drawLine( pix_locale.x - dist_to_bottom_left, 
	              pix_locale.y + dist_to_bottom_left,
	              pix_locale.x + dist_to_top_right,
		      pix_locale.y - dist_to_top_right );
      } 
    }
    // reset the color of Graphics2D object to its original color
    g2d.setColor(original_color);
  }
  
 /**
  * Display the name of the marker.
  *
  *  @return The String name of the marker.
  */
  public String toString()
  {    
    return intCodeToString(markertype);
  }
  
  private static boolean isValidCode( int type )
  {
    // make sure type is a valid choice.
    if( type >= 0 && type < 14 )
    {
      // there is no 9th marker
      if( type != 9 )
      {
        return true;
      }
    }
    return false;
  }
} // end of Marker

