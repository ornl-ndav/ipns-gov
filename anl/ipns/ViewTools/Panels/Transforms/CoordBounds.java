/*
 * @(#) CoordBounds.java
 *
 * Programmer: Dennis Mikkelson
 *
 * $Log$
 * Revision 1.3  2001/01/29 21:39:00  dennis
 * Now uses CVS version numbers.
 *
 * Revision 1.2  2000/07/10 22:11:45  dennis
 * 7/10/2000 version, many changes and improvements
 *
 * Revision 1.4  2000/05/11 16:53:19  dennis
 * Added RCS logging
 *
 */
package DataSetTools.components.image;

import java.io.*;


public class CoordBounds implements Serializable
{
  private float x1, x2, y1, y2;

  public CoordBounds()
  {
    setBounds( 0, 0, 1, 1 );
  };

  public CoordBounds( float x1, float y1, 
                      float x2, float y2 )
  {
    setBounds( x1, y1, x2, y2 );
  }; 

  public void setBounds( float x1, float y1,
                         float x2, float y2 )
  {
    this.x1 = x1;
    this.y1 = y1;
    this.x2 = x2;
    this.y2 = y2;
  };

  public void setBounds( float x_vals[], float y_vals[] )
  {
    float max_x = Float.NEGATIVE_INFINITY;
    float min_x = Float.POSITIVE_INFINITY;
    for ( int i = 0; i < x_vals.length; i++ )
    {
      if ( x_vals[i] > max_x )
        max_x = x_vals[i]; 
      if ( x_vals[i] < min_x )
        min_x = x_vals[i]; 
    }
    if ( min_x == max_x )    // avoid division by 0 when scaling data
      max_x = min_x + 1;
 
    float max_y = Float.NEGATIVE_INFINITY;
    float min_y = Float.POSITIVE_INFINITY;
    for ( int i = 0; i < y_vals.length; i++ )
    {
      if ( y_vals[i] > max_y )
        max_y = y_vals[i];
      if ( y_vals[i] < min_y )
        min_y = y_vals[i];
    }
    if ( min_y == max_y )    // avoid division by 0 when scaling data
      max_y = min_y + 1;

    setBounds( min_x, min_y, max_x, max_y );
  }

  public void growBounds( float x_vals[], float y_vals[] )
  {
     CoordBounds temp = new CoordBounds();
     temp.setBounds( x_vals, y_vals ); 
     if ( this.x1 > temp.x1 )
       this.x1 = temp.x1;
     if ( this.x2 < temp.x2 )
       this.x2 = temp.x2;
     if ( this.y1 > temp.y1 )
       this.y1 = temp.y1;
     if ( this.y2 < temp.y2 )
       this.y2 = temp.y2;
  }

  public void invertBounds( )
  {                                //swap y1, y2 for "upside down coords"
    float temp = this.y1;
    this.y1 = this.y2;
    this.y2 = temp; 
  }

  public CoordBounds MakeCopy( )
  {
    return( new CoordBounds( this.getX1(), this.getY1(), 
                             this.getX2(), this.getY2() ) );
  }

  public float getX1()
  {
    return( x1 );
  }

  public float getX2()
  {
    return( x2 );
  }

  public float getY1()
  {
    return( y1 );
  }

  public float getY2()
  {
    return( y2 );
  }

  public String toString() {
      return "[x1=" + x1 + ", x2=" + x2 + ", y1=" + y1 + ", y2=" + y2 +"]";
  }
}
