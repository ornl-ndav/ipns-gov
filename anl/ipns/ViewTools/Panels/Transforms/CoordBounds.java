/*
 * File:  CoordBounds.java
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
 * Revision 1.6  2001/04/23 21:19:18  dennis
 * Added copyright and GPL info at the start of the file.
 * Also, modified setBounds( x_vals[], y_vals[] ) to use
 * default regions if the arrays are degenerate.
 * Fixed bug in growBound( x_vals[], y_vals[] ) that would
 * cause a problem if the bounds didn't satisfy:
 * x1 < x2  and  y1 < y2.
 *
 * Revision 1.5  2001/03/30 19:17:54  dennis
 * Added method  intersect( bounds )
 *
 * Revision 1.4  2001/03/01 23:20:04  dennis
 * Now implements clone() method.
 *
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
import DataSetTools.util.*;

/**
 *  Objects of this class maintain bounds on a rectangular region of the
 *  x,y plane using floating point values.  
 */
public class CoordBounds implements Serializable
{
  private float x1, x2, y1, y2;

  /**
   *  Construct bounds initialized to the unit square, [0,1] x [0,1].
   */
  public CoordBounds()
  {
    setBounds( 0, 0, 1, 1 );
  };

  /**
   *  Construct bounds initialized to the rectangle, [x1,y1] x [x2,y2].
   *
   *  @param   x1   left bound
   *  @param   x2   right bound
   *  @param   y1   lower bound
   *  @param   y2   upper bound
   */
  public CoordBounds( float x1, float y1, 
                      float x2, float y2 )
  {
    setBounds( x1, y1, x2, y2 );
  }; 

  /**
   *  Set the current bounds to the rectangle, [x1,y1] x [x2,y2].
   *
   *  @param   x1   left bound
   *  @param   x2   right bound
   *  @param   y1   lower bound
   *  @param   y2   upper bound
   */
  public void setBounds( float x1, float y1,
                         float x2, float y2 )
  {
    this.x1 = x1;
    this.y1 = y1;
    this.x2 = x2;
    this.y2 = y2;
  };

  /**
   *  Set the current bounds to a rectangle, [x1,y1] x [x2,y2], for which
   *  x_vals[i] is in [x1, x2] and y_vals[i] is in [y1, y2]  for each i.
   *  If the size of the rectangle degenerates to zero in either direction,
   *  it is replaced by a non-degenerate rectangle.  Specifically, if
   *  x1==x2, we use [x1, x1+1] and if y1==y2, we use [y1, y1+1].
   *
   *  @param   x_vals[]   The x values that must be contained within the bounds.
   *  @param   y_vals[]   The y values that must be contained within the bounds.
   */
  public void setBounds( float x_vals[], float y_vals[] )
  {
    float max_x;
    float min_x;
    float max_y;
    float min_y;

    if ( x_vals == null || x_vals.length == 0 )    // use unit interval by
    {                                              // default, if the list
       min_x = 0;                                  // is empty.
       max_x = 1;
    }
    else
    {
      max_x = x_vals[0];
      min_x = x_vals[0];
      for ( int i = 1; i < x_vals.length; i++ )
      {
        if ( x_vals[i] > max_x )
          max_x = x_vals[i]; 
        if ( x_vals[i] < min_x )
          min_x = x_vals[i]; 
      }
    }
    if ( min_x == max_x )    // avoid division by 0 when scaling data
      max_x = min_x + 1;
 
    if ( y_vals == null || y_vals.length == 0 )    // use unit interval by
    {                                              // default, if the list
       min_y = 0;                                  // is empty.
       max_y = 1;
    }
    else
    { 
      max_y = y_vals[0];
      min_y = y_vals[0];
      for ( int i = 0; i < y_vals.length; i++ )
      {
        if ( y_vals[i] > max_y )
          max_y = y_vals[i];
        if ( y_vals[i] < min_y )
          min_y = y_vals[i];
      }
    }
    if ( min_y == max_y )    // avoid division by 0 when scaling data
      max_y = min_y + 1;

    setBounds( min_x, min_y, max_x, max_y );
  }

  /**
   *  Grow the current bounds to a rectangle, [x1,y1] x [x2,y2], for which
   *  x_vals[i] will be in [x1, x2] and y_vals[i] will be in [y1, y2]  
   *  for each i.
   *
   *  @param   x_vals[]   The x values that must be contained within the bounds.
   *  @param   y_vals[]   The y values that must be contained within the bounds.
   */
  public void growBounds( float x_vals[], float y_vals[] )
  {
     CoordBounds temp = new CoordBounds();
     temp.setBounds( x_vals, y_vals ); 

     if ( this.x1 < temp.x1 )              // make temp.x1 the smallest x value
       temp.x1 = this.x1;
     if ( this.x2 < temp.x1 )
       temp.x1 = this.x2;

     if ( this.x1 > temp.x2 )              // make temp.x2 the largest x value
       temp.x2 = this.x1;
     if ( this.x2 > temp.x2 )
       temp.x2 = this.x2;

     if ( this.y1 < temp.y1 )              // make temp.y1 the smallest y value
       temp.y1 = this.y1;
     if ( this.y2 < temp.y1 )
       temp.y1 = this.y2;

     if ( this.y1 > temp.y2 )              // make temp.y2 the largest y value
       temp.y2 = this.y1;
     if ( this.y2 > temp.y2 )
       temp.y2 = this.y2;

     this.x1 = temp.x1;
     this.x2 = temp.x2;
     this.y1 = temp.y1;
     this.y2 = temp.y2;
  }

  /**
   *  Swap the y values of the current bounds.  That is, interchange
   *  y1 <--> y2.  This is useful for representing upside down coordinate
   *  systems, such as the pixel coordinates in a window.
   */
  public void invertBounds( )
  {                                //swap y1, y2 for "upside down coords"
    float temp = this.y1;
    this.y1 = this.y2;
    this.y2 = temp; 
  }


  /**
   *  Intersect the current region with the specified region to produce a
   *  new region.
   *  
   *  @param  bounds  The region to intersect with the current region.
   *
   *  @return Return the bounds for the intersection of the current region
   *          with the specified region.  If the intersection is empty,
   *          return null.
   */
  public CoordBounds intersect( CoordBounds bounds )
  {
    ClosedInterval  interval1,
                    interval2,
                    interval;

    interval1 = new ClosedInterval( x1, x2 ); 
    interval2 = new ClosedInterval( bounds.x1, bounds.x2 ); 
    interval  = interval1.intersect( interval2 );

    if ( interval == null )                      // bounds don't intersect in x
      return null;

    float new_x1,
          new_x2;

    if ( x1 < x2 )                               // keep the same order as the
    {                                            // current bounds
      new_x1 = interval.getStart_x();
      new_x2 = interval.getEnd_x();
    }
    else                                  
    {
      new_x1 = interval.getEnd_x();
      new_x2 = interval.getStart_x();
    }
    
    interval1 = new ClosedInterval( y1, y2 );
    interval2 = new ClosedInterval( bounds.y1, bounds.y2 );
    interval  = interval1.intersect( interval2 );

    if ( interval == null )                      // bounds don't intersect in y
      return null;

    float new_y1,
          new_y2;

    if ( y1 < y2 )                               // keep the same order as the
    {                                            // current bounds
      new_y1 = interval.getStart_x();
      new_y2 = interval.getEnd_x();
    }
    else                                  
    {
      new_y1 = interval.getEnd_x();
      new_y2 = interval.getStart_x();
    }

    return new CoordBounds( new_x1, new_y1, new_x2, new_y2 );
  }

  /**
   *  Make a copy of the current CoordBounds object.
   *
   *  @return  A new CoordBounds object with the same bounds as the current
   *           object
   */
  public CoordBounds MakeCopy( )
  {
    return new CoordBounds( x1, y1, x2, y2 );
  }

  /**
   *  Get the first "x" bound for this CoordBounds object.
   *
   *  @return Returns the x1 value for this bound.
   */
  public float getX1()
  {
    return( x1 );
  }

  /**
   *  Get the second "x" bound for this CoordBounds object.
   *
   *  @return Returns the x2 value for this bound.
   */
  public float getX2()
  {
    return( x2 );
  }

  /**
   *  Get the first "y" bound for this CoordBounds object.
   *
   *  @return Returns the y1 value for this bound.
   */
  public float getY1()
  {
    return( y1 );
  }

  /**
   *  Get the second "y" bound for this CoordBounds object.
   *
   *  @return Returns the y2 value for this bound.
   */
  public float getY2()
  {
    return( y2 );
  }

  /**
   *  Get a string form for this CoordBound object.
   *
   *  @return A string containing the x and y bounds for this object.
   */
  public String toString() {
      return "[x1=" + x1 + ", x2=" + x2 + ", y1=" + y1 + ", y2=" + y2 +"]";
  }

  /**
   *  Make a copy of the current CoordBounds object.
   *
   *  @return  A generic Object, that is a CoordBounds object with the 
   *           same bounds as the current object.
   */
  public Object clone()
  {
    return new CoordBounds( x1, y1, x2, y2 );
  }

 
  /* ----------------------------- main -------------------------------- */
  /*
   *  Main program for testing purposes.
   */
  public static void main( String args[] )
  {
    CoordBounds b1 = new CoordBounds( 0, 0, 10, 10 );
    CoordBounds b2 = new CoordBounds( 5, 8, 15, 12 );

    System.out.println( "Intersecting b1 = " + b1 );
    System.out.println( "with b2 = " + b2 );
    System.out.println( "gives " + b1.intersect( b2 ) );

    CoordBounds b3 = new CoordBounds( 10, 10, 0, 0 );
    CoordBounds b4 = new CoordBounds( 15, 12, 5, 8 );

    System.out.println( "Intersecting b3 = " + b3 );
    System.out.println( "with b4 = " + b4 );
    System.out.println( "gives " + b3.intersect( b4 ) );

  }

}
