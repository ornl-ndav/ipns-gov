/*
 * @(#)  CoordTransform.java
 *
 *  Programmer: Dennis Mikkelson
 *
 *  $Log$
 *  Revision 1.3  2001/01/29 21:39:05  dennis
 *  Now uses CVS version numbers.
 *
 *  Revision 1.2  2000/07/10 22:11:47  dennis
 *  7/10/2000 version, many changes and improvements
 *
 *  Revision 1.4  2000/05/11 16:53:19  dennis
 *  Added RCS logging
 *
 */

package DataSetTools.components.image;

import java.io.*;
import DataSetTools.util.*;

public class CoordTransform implements Serializable
{
  private CoordBounds  from;
  private CoordBounds  to;

  public CoordTransform( )
  {
    from = new CoordBounds( 0, 0, 1, 1 );
    to   = new CoordBounds( 0, 0, 1, 1 );
  } 

  public CoordTransform( CoordBounds source_region, 
                         CoordBounds destination_region )
  {
    from = source_region;
    to = destination_region;
  } 

  public void MapTo( float x[], float y[] )
  {
    transformXList( x, from, to );
    transformYList( y, from, to );
  }

  public void MapFrom( float x[], float y[] )
  {
    transformXList( x, to, from );
    transformYList( y, to, from );
  }

  public floatPoint2D MapTo( floatPoint2D p1 )
  {
    floatPoint2D p2 = new floatPoint2D();
    p2.x = transformX( p1.x, from, to );
    p2.y = transformY( p1.y, from, to );
    return p2;
  }

  public floatPoint2D MapFrom( floatPoint2D p1 )
  {
    floatPoint2D p2 = new floatPoint2D();
    p2.x = transformX( p1.x, to, from );
    p2.y = transformY( p1.y, to, from );
    return p2;
  }

  public CoordBounds MapTo( CoordBounds b )
  {
    float x1 = transformX( b.getX1(), from, to ); 
    float x2 = transformX( b.getX2(), from, to ); 
    float y1 = transformY( b.getY1(), from, to ); 
    float y2 = transformY( b.getY2(), from, to ); 
    return new CoordBounds( x1, y1, x2, y2 );
  }
 
  public CoordBounds MapFrom( CoordBounds b )
  {
    float x1 = transformX( b.getX1(), to, from );
    float x2 = transformX( b.getX2(), to, from );
    float y1 = transformY( b.getY1(), to, from );
    float y2 = transformY( b.getY2(), to, from );
    return new CoordBounds( x1, y1, x2, y2 );
  }

  public float MapXTo( float x )
  {
    return( transformX( x, from, to ) );
  }

  public float MapXFrom( float x )
  {
    return( transformX( x, to, from ) );
  }

  public float MapYTo( float x )
  {
    return( transformY( x, from, to ) );
  }

  public float MapYFrom( float x )
  {
    return( transformY( x, to, from ) );
  }

  public void setSource( CoordBounds b )
  {
    from.setBounds( b.getX1(), b.getY1(), b.getX2(), b.getY2() );
  }

  public void setSource( float x1, float y1, float x2, float y2 )
  {
    from.setBounds( x1, y1, x2, y2 );
  }

  public void setDestination( CoordBounds b )
  {
    to.setBounds( b.getX1(), b.getY1(), b.getX2(), b.getY2() );
  }

  public void setDestination( float x1, float y1, float x2, float y2 )
  {
    to.setBounds( x1, y1, x2, y2 );
  }

  public CoordBounds getSource( )
  {
    return( from );
  }

  public CoordBounds getDestination( )
  {
    return( to );
  }

/* -----------------------------------------------------------------------
 *
 * PRIVATE METHODS
 *
 */

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
