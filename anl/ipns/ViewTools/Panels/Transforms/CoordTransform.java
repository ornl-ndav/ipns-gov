/*
 * File:   CoordTransform.java
 *
 * Copyright (C) 1999, Dennis Mikkelson
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
 *  $Log$
 *  Revision 1.9  2003/06/19 15:03:32  serumb
 *  Added methods MapXListTo, MapYListTo, MapXListFrom, and MapYListFrom.
 *
 *  Revision 1.8  2002/11/27 23:13:18  pfpeterson
 *  standardized header
 *
 *  Revision 1.7  2002/11/26 19:21:21  dennis
 *  Added option to restrict the transformation to preserve aspect ratio.
 *  Changed getSource() and getDestination() routines to return copies of
 *  the source & destination regions rather than references to them.
 *  Added java docs for API.
 *
 */

package DataSetTools.components.image;

import java.io.*;
import DataSetTools.util.*;

/**
 *  Class CoordTransform represents a separable transformation from a 
 *  rectangular source region to a rectangular destination region.  The 
 *  rectangular regions are aligned with the coordinate axes.  Currently,
 *  the only transformation type supported is linear in x and linear in y.
 *  This could be extended to deal with other commonly used transforms 
 *  such as linear-log, log-linear or log-log.
 */
public class CoordTransform implements Serializable
{
  private CoordBounds  from;
  private CoordBounds  to;
  private boolean      preserve_aspect_ratio;

  /**
   *  Construct a default transform from [0,1]x[0,1] to [0,1]x[0,1].
   */
  public CoordTransform( )
  {
    from = new CoordBounds( 0, 0, 1, 1 );
    to   = new CoordBounds( 0, 0, 1, 1 );
    preserve_aspect_ratio = false;
  } 

  /**
   *  Construct a transform from the specified source rectangle to the 
   *  specified destination rectangle. 
   */
  public CoordTransform( CoordBounds source_region, 
                         CoordBounds destination_region )
  {
    from = source_region;
    to = destination_region;
    preserve_aspect_ratio = false;
  } 

  /**
   *  Construct a transform that is a copy of the specified transform.
   */
  public CoordTransform( CoordTransform tran )
  {
    from = tran.from.MakeCopy();
    to   = tran.to.MakeCopy();
    preserve_aspect_ratio = false;
  }


  /**
   *  Set a flag to determine whether or not future calls to setSource will
   *  adjust the specified source rectangle to have the same aspect ratio as the
   *  current destination rectangle. 
   *
   *  @param  flag  If true, subsequent calls to setSource() will adjust the
   *                aspect ratio of the source region to the aspect ratio of
   *                the destination region.  In this case, the destination
   *                region MUST be set to meaningful values before setting
   *                the source region.  If false, the region passed to
   *                setSource() will be used without modification.
   */
  public void setPreserveAspectRatio( boolean flag )
  {
    preserve_aspect_ratio = flag;
  }
  

  /**
   *  Check whether or not the current transform maps between the same 
   *  regions as the specified transform.
   *
   *  @param  tran   The transform object to compare with the current transform 
   *                 object.
   * 
   *  @return Returns true if the current transform object maps between exactly
   *  the same regions as the specified transform object.
   */
  public boolean equals( CoordTransform tran )
  {
    if ( from.equals( tran.from ) && to.equals( tran.to ) ) 
      return true;
    else
      return false;
  }


  /**
   *  Transform lists of x, y values from the current source region to the
   *  current destination region.
   *
   *  @param  x   Array of x values to transform.
   *  @param  y   Array of y values to transform.
   */
  public void MapTo( float x[], float y[] )
  {
    transformXList( x, from, to );
    transformYList( y, from, to );
  }

  /**
   *  Transform lists of x, y values from the current destination region 
   *  back to the current source region.
   *
   *  @param  x   Array of x values to transform.
   *  @param  y   Array of y values to transform.
   */
  public void MapFrom( float x[], float y[] )
  {
    transformXList( x, to, from );
    transformYList( y, to, from );
  }

  /**
   *  Transform a list of x values from the current source region to the
   *  current destination region.
   *
   *  @param  x   Array of x values to transform.
   */
  public void MapXListTo( float x[] )
  {
    transformXList( x, from, to );
  }

  /**
   *  Transform a list of x values from the current destination region 
   *  back to the current source region.
   *
   *  @param  x   Array of x values to transform.
   * 
   */
  public void MapXListFrom( float x[] )
  {
    transformXList( x, to, from );
  }

  /**
   *  Transform a list of y values from the current source region to the
   *  current destination region.
   *
   *  @param  y   Array of y values to transform.
   */
  public void MapYListTo( float y[] )
  {
    transformYList( y, from, to );
  }

  /**
   *  Transform a list of y values from the current destination region 
   *  back to the current source region.
   *
   *  @param  y   Array of y values to transform.
   * 
   */
  public void MapYListFrom( float y[] )
  {
    transformYList( y, to, from );
  }

  /**
   *  Map one point from the current source region to the current destination 
   *  region.
   *
   *  @param  p1  The two-dimensional point to be transformed.
   *
   *  @return  The point in the destination region that corresponds to p1. 
   */
  public floatPoint2D MapTo( floatPoint2D p1 )
  {
    floatPoint2D p2 = new floatPoint2D();
    p2.x = transformX( p1.x, from, to );
    p2.y = transformY( p1.y, from, to );
    return p2;
  }

  /**
   *  Map one point from the current destiantion region to the current source 
   *  region.
   *
   *  @param  p1  The two-dimensional point to be transformed.  
   *
   *  @return  The point in the source region that corresponds to p1.
   */
  public floatPoint2D MapFrom( floatPoint2D p1 )
  {
    floatPoint2D p2 = new floatPoint2D();
    p2.x = transformX( p1.x, to, from );
    p2.y = transformY( p1.y, to, from );
    return p2;
  }

  /**
   *  Map a rectangular region from the current source region to the 
   *  current destination region.
   *
   *  @param  b  The bounds of a rectangular region being mapped to the 
   *             destination region. 
   *
   *  @return  The rectangular region in the destination region that 
   *           corresponds to b.
   */
  public CoordBounds MapTo( CoordBounds b )
  {
    float x1 = transformX( b.getX1(), from, to ); 
    float x2 = transformX( b.getX2(), from, to ); 
    float y1 = transformY( b.getY1(), from, to ); 
    float y2 = transformY( b.getY2(), from, to ); 
    return new CoordBounds( x1, y1, x2, y2 );
  }
 
  /**
   *  Map a rectangular region from the current destination region to the 
   *  current source region.
   *
   *  @param  b  The bounds of a rectangular region being mapped from the 
   *             destination region.
   *
   *  @return  The rectangular region in the source region that 
   *           corresponds to b.
   */
  public CoordBounds MapFrom( CoordBounds b )
  {
    float x1 = transformX( b.getX1(), to, from );
    float x2 = transformX( b.getX2(), to, from );
    float y1 = transformY( b.getY1(), to, from );
    float y2 = transformY( b.getY2(), to, from );
    return new CoordBounds( x1, y1, x2, y2 );
  }

  /**
   *  Map a single x value from the current source region to the current 
   *  destination region.
   *
   *  @param  x  The x-value to be transformed to the destination region.
   *
   *  @return  The x-value in the destination region that corresponds to x.
   */
  public float MapXTo( float x )
  {
    return( transformX( x, from, to ) );
  }

  /**
   *  Map a single x value from the current destination region back to the 
   *  current source region.
   *
   *  @param  x  The x-value to be transformed from the destination region.
   *
   *  @return  The x-value in the source region that corresponds to x.
   */
  public float MapXFrom( float x )
  {
    return( transformX( x, to, from ) );
  }

  /**
   *  Map a single y value from the current source region to the current 
   *  destination region.
   *
   *  @param  y  The y-value to be transformed to the destination region.
   *
   *  @return  The y-value in the destination region that corresponds to x.
   */
  public float MapYTo( float y )
  {
    return( transformY( y, from, to ) );
  }

  /**
   *  Map a single y value from the current destination region back to the 
   *  current source region.
   *
   *  @param  y  The y-value to be transformed from the destination region.
   *
   *  @return  The y-value in the source region that corresponds to x.
   */
  public float MapYFrom( float y )
  {
    return( transformY( y, to, from ) );
  }


  /**
   *  The the source region that will be mapped to the destination region.
   *  If the preserve aspect ratio flag has been set, the specified bounds
   *  will be adjusted to match the aspect ratio of the destination region.
   *  The destination region MUST be set first, in this case.
   *
   *  @param b  The bounds to use for the source region.
   */
  public void setSource( CoordBounds b )
  {
    if ( preserve_aspect_ratio )
      b = fix_aspect_ratio( b.getX1(), b.getY1(), b.getX2(), b.getY2() );
    
    from.setBounds( b.getX1(), b.getY1(), b.getX2(), b.getY2() );
  }

  /**
   *  The the source region that will be mapped to the destination region.
   *  If the preserve aspect ratio flag has been set, the specified region 
   *  will be adjusted to match the aspect ratio of the destination region.
   *  The destination region MUST be set first, in this case.
   *
   *  @param x1  The left edge of the region.
   *  @param y1  The lower edge of the region.
   *  @param x2  The right edge of the region.
   *  @param y2  The upper edge of the region.
   */
  public void setSource( float x1, float y1, float x2, float y2 )
  {
    if ( preserve_aspect_ratio )
    {
      CoordBounds b = fix_aspect_ratio( x1, y1, x2, y2 );
      from.setBounds( b.getX1(), b.getY1(), b.getX2(), b.getY2() );
    }
    else
      from.setBounds( x1, y1, x2, y2 );
  }

  /**
   *  The the Destination region that the source region will be mapped to.
   *  If the preserve aspect ratio flag has been set, setDestination MUST 
   *  be called before calling setSource().
   *
   *  @param b  The bounds to use for the destination region.
   */
  public void setDestination( CoordBounds b )
  {
    to.setBounds( b.getX1(), b.getY1(), b.getX2(), b.getY2() );
  }

  /**
   *  The the Destination region that the source region will be mapped to. 
   *  If the preserve aspect ratio flag has been set, setDestination MUST 
   *  be called before calling setSource().
   *
   *  @param x1  The left edge of the region.
   *  @param y1  The lower edge of the region.
   *  @param x2  The right edge of the region.
   *  @param y2  The upper edge of the region.
   */
  public void setDestination( float x1, float y1, float x2, float y2 )
  {
    to.setBounds( x1, y1, x2, y2 );
  }

  /**
   *  Get a copy of the current source region.
   *
   *  @return a new CoordBounds object that contains the source region.
   */
  public CoordBounds getSource( )
  {
    return( from.MakeCopy() );
  }


  /**
   *  Get a copy of the current destination region.
   *
   *  @return a new CoordBounds object that contains the destination region.
   */
  public CoordBounds getDestination( )
  {
    return( to.MakeCopy() );
  }

  /**
   *  Get a string form for this CoordTransform object.
   *
   *  @return A string containing the source and destination regions
   *          for this transform.
   */
  public String toString() 
  {
    return "Source Region: " + from + "\n" +
           "Dest   Region: " + to;
  }



/* -----------------------------------------------------------------------
 *
 * PRIVATE METHODS
 *
 */

  private CoordBounds fix_aspect_ratio( float x1, float y1, float x2, float y2 )
  {
    float source_height_to_width =  Math.abs( (y2 - y1) / (x2 - x1 ) );
    float dest_height_to_width   =  Math.abs( (to.getY2() - to.getY1()) /
                                              (to.getX2() - to.getX1())  );
    if ( source_height_to_width > dest_height_to_width )
    {
      float mid = (x1 + x2)/2;
      float width = Math.abs( (y2 - y1) / dest_height_to_width );
      x1 = mid - width/2;
      x2 = mid + width/2;
    }
    else if ( source_height_to_width < dest_height_to_width )
    {
      float mid = (y1 + y2) / 2;
      float height = Math.abs( dest_height_to_width * ( x2 - x1 ) );
      if ( y1 < y2 )
      {
        y1 = mid - height/2;
        y2 = mid + height/2;
      }
      else                                   // upside down coords
      {
        y2 = mid - height/2;
        y1 = mid + height/2;
      }
    }

    return new CoordBounds( x1, y1, x2, y2 );
  }


  private float transformX( float x, CoordBounds A, CoordBounds B )
  {
    float A1 = A.getX1();
    float A2 = A.getX2();
    float B1 = B.getX1();
    float B2 = B.getX2();
    if ( A2 == A1 )
      return( B1 );
    else
      return( (x - A1) * ( B2 - B1 ) / ( A2 - A1 ) + B1 );
  }

  private float transformY( float y, CoordBounds A, CoordBounds B )
  {
    float A1 = A.getY1();
    float A2 = A.getY2();
    float B1 = B.getY1();
    float B2 = B.getY2();
    if ( A2 == A1 )
      return( B1 );
    else
      return( (y - A1) * ( B2 - B1 ) / ( A2 - A1 ) + B1 );
  }


  private void transformXList( float list[], CoordBounds A, CoordBounds B )
  {
    float A1 = A.getX1();
    float A2 = A.getX2();
    float B1 = B.getX1();
    float B2 = B.getX2();
    if ( A2 == A1 )                    // if degenerate interval, use 
      for ( int i = 0; i < list.length; i++ ) // smallest target X
        list[i] = B1;
    else
    {
      float slope     = ( B2 - B1 ) / ( A2 - A1 );
      float intercept = -A1 * slope + B1;
      for ( int i = 0; i < list.length; i++ )
       list[i] = slope * list[i] + intercept;
    }
  }

  private void transformYList( float list[], CoordBounds A, CoordBounds B )
  {
    float A1 = A.getY1();
    float A2 = A.getY2();
    float B1 = B.getY1();
    float B2 = B.getY2();
    if ( A2 == A1 )                           // if degenerate interval, use
      for ( int i = 0; i < list.length; i++ ) // smallest target X
        list[i] = B1;
    else
    {
      float slope     = ( B2 - B1 ) / ( A2 - A1 );
      float intercept = -A1 * slope + B1;
      for ( int i = 0; i < list.length; i++ )
        list[i] = slope * list[i] + intercept;
    }
  }

}
