/*
 * File:  Axis.java
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
 * This work was supported by the National Science Foundation under grant
 * number DMR-0218882.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 * $Log$
 * Revision 1.1  2004/07/14 16:29:15  dennis
 * Initial version of 3D calibrated axis.  Further work is required to
 * draw an axis label and to format numeric labels on tick marks.  Some
 * additional options regarding the size and placement of labels may
 * also be needed.
 *
 */

package gov.anl.ipns.ViewTools.Panels.GL_ThreeD.Shapes;

import net.java.games.jogl.*;
import gov.anl.ipns.MathTools.Geometry.*;
import gov.anl.ipns.ViewTools.Components.Transparency.*;
import gov.anl.ipns.ViewTools.Panels.GL_ThreeD.Fonts.*;

public class Axis extends GL_Shape
{
  public static final byte TOP    = 0;
  public static final byte BOTTOM = 1;
  public static final byte LEFT   = 2;
  public static final byte RIGHT  = 3;

  private Vector3D start      = new Vector3D(0,0,0);
  private Vector3D end        = new Vector3D(10,0,0);
  private float    min        = 0;
  private float    max        = 10;
  private Vector3D tick_dir   = new Vector3D( 0, -1, 0 );
  private int      alignment  = RIGHT; 
  private String   label      = "Dummy Label";

  private float[]  div_points = null;
  private Vector3D   p1[]     = null;                // end points p1 & p1 of
  private Vector3D   p2[]     = null;                // axis and tick marks
  private float[]    color    = {1,1,1};
  private StrokeFont font     = new RomanSimplex(); 
  private StrokeText tick_labels[] = null;           // numeric labels for axis

  public Axis()
  {
    set_division_points(); 
    make_axes();
    setColor(color);
  }

  public Axis( Vector3D start_point,
               Vector3D end_point,
               float    min_val,
               float    max_val,
               Vector3D tick_direction,
               int      text_alignment,
               String   axis_label      )
  {
    if ( start_point != null )
      start = new Vector3D( start_point );

    if ( end_point != null )
      end = new Vector3D( end_point );

    min = min_val;
    max = max_val;

    if ( tick_direction != null && tick_direction.length() > 0 )
    {
      tick_dir = new Vector3D( tick_direction );
      tick_dir.normalize();
    }

    if ( text_alignment >= TOP && text_alignment <= RIGHT )
      alignment = (int)text_alignment;

    label = axis_label;

    set_division_points();
    make_axes();
    setColor(color);
  }


  protected void Draw( GLDrawable drawable )
  {
    if ( tick_labels != null )
      for ( int i = 0; i < tick_labels.length; i++ )
      {
        if ( tick_labels[i] != null )
          tick_labels[i].Draw( drawable ); // NOTE: Draw() method can only have
      }                                    //       methods callable inside of
                                           //       glNewList() and glEndList(),
                                           //       so we must call Draw() here,
                                           //       NOT Render()
    GL gl = drawable.getGL();
    if ( p1 != null && p2 != null )
    {
      float pt[];
      gl.glBegin( GL.GL_LINES );
        for ( int i = 0; i < p1.length; i++ )
        {
          pt = p1[i].get();
          gl.glVertex3f( pt[0], pt[1], pt[2] );
          pt = p2[i].get();
          gl.glVertex3f( pt[0], pt[1], pt[2] );
        }
      gl.glEnd();
    }
  }


  private void make_axes()
  { 
     tick_labels = new StrokeText[ div_points.length ];
     p1 = new Vector3D[ 1 + div_points.length ];
     p2 = new Vector3D[ p1.length ];

     p1[0] = new Vector3D( start );   // main axis line
     p2[0] = new Vector3D( end );

     Vector3D axis_dir = new Vector3D( end );
     axis_dir.subtract( start );
     float axis_length = axis_dir.length();
     axis_dir.normalize();

     float tick_length = axis_length/100;
     Vector3D tick_start = new Vector3D( tick_dir );
     Vector3D tick_end   = new Vector3D( tick_dir );
     tick_start.multiply( -tick_length );
     tick_end.multiply( tick_length );
                                                 // make each tick mark
     float label_space = 0;
     float width = 0;
     float char_height = 2*tick_length;
     for ( int i = 0; i < div_points.length; i++ )
     {
       Vector3D tick_position = new Vector3D( start );
       Vector3D step_vec = new Vector3D( axis_dir );
       step_vec.multiply( (div_points[i] - min)/(max-min) * axis_length );
       tick_position.add( step_vec );
       
       p1[i+1] = new Vector3D( tick_position );
       p2[i+1] = new Vector3D( tick_position );
       p1[i+1].add( tick_start );
       p2[i+1].add( tick_end );

       Vector3D text_position = new Vector3D( p2[i+1] );
       text_position.add( tick_end );

       StrokeText text = new StrokeText( ""+div_points[i], font );
       text.setPosition( text_position );
       text.setHeight( char_height );
       if ( alignment == TOP )
       {
         text.setOrientation( axis_dir, tick_start ); 
         text.setAlignment( StrokeText.HORIZ_CENTER, StrokeText.VERT_TOP );
         label_space = char_height;
       }
       else if ( alignment == BOTTOM )
       {
         text.setOrientation( axis_dir, tick_end ); 
         text.setAlignment( StrokeText.HORIZ_CENTER, StrokeText.VERT_BOTTOM );
         label_space = char_height;
       }
       else if ( alignment == LEFT )
       {
         text.setOrientation( tick_end, axis_dir ); 
         text.setAlignment( StrokeText.HORIZ_LEFT, StrokeText.VERT_HALF );
         width = text.StringWidth();
         if ( width > label_space )
           label_space = width;
       }
       else if ( alignment == RIGHT )
       {
         text.setOrientation( tick_start, axis_dir ); 
         text.setAlignment( StrokeText.HORIZ_RIGHT, StrokeText.VERT_HALF );
         width = text.StringWidth();
         if ( width > label_space )
           label_space = width;
       }
       tick_labels[i] = text; 
     }
    
     rebuild_list = true;
  }

  private void set_division_points()
  {
    CalibrationUtil calib = new CalibrationUtil( min, max );
    float params[] = calib.subDivide();
    float step = params[0];
    float start = params[1];
    float val   = start;
    int   count = 1;
    while ( val < max && count < 100 )
    {
      val = start + count * step; 
                                    // leave a little room incase of rounding
      if ( val-start < (1.00001 * (max-start)) )   
        count++;
    }

    div_points = new float[ count ];
    for ( int i = 0; i < count; i++ )
      div_points[i] = start + i * step;
  }

}
