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
 * Revision 1.3  2004/07/16 14:24:02  dennis
 * The Draw() method now calls gl.glEnable( GL.GL_NORMALIZE ) to
 * have OpenGL change all normals to unit length after scaling and
 * other transformations.  This fixes a problem where the color of
 * the text would change when moving to different viewpoints.
 *
 * Revision 1.2  2004/07/15 19:27:26  dennis
 * Added axis label and methods to individually change the character size,
 * axis position, tick mark direction, text alignment, etc.  Also added
 * convenience method: getInstance() that will provide a properly configured
 * 3D axis in many common cases.  Some more work may be needed to refine the
 * formatting of the numbers, or to refine the orientation of the text relative
 * to the axis, but it should usable now.
 *
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

/**
 *    This class represents an axis in 3D.  The axis is automatically 
 *  subdivided and tick marks drawn.  Numeric labels are placed on the
 *  tick marks and can be oriented in one of four ways.  Specifically,  
 *  either the the TOP, BOTTOM, LEFT or RIGHT side of the numeric label
 *  will be placed at the end of the tic mark.  The tick direcion can
 *  also be specified in 3D.  The numeric values that are associated
 *  with the start and end of the axis are separately specified.  This
 *  allows an arbitrary scale to be placed at an arbitrary position 
 *  in space, much like an ordinary ruler can be placed along side objects
 *  in 3D to measure them.  The calibrations along the "ruler" can be
 *  in arbitrary units.
 */

public class Axis extends GL_Shape
{
  /**
   *  TOP, BOTTOM, LEFT and RIGHT may be used to specify which part of 
   *  the numeric label is placed at the end of the tick mark.
   */
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
  private float    skip_value = Float.NaN;

  private float[]  div_points = null;
  private Vector3D   p1[]     = null;                // end points p1 & p1 of
  private Vector3D   p2[]     = null;                // axis and tick marks
  private StrokeFont font     = new RomanSimplex(); 
  private StrokeText tick_labels[] = null;           // numeric labels for axis
  private StrokeText axis_label    = null;           // label for whole axis
  private float      user_height   = Float.NaN;

  private boolean    ignore_make_axis_request = false;

  /* ------------------------ default constructor ------------------------- */
  /**
   *  Make an axis with default vaues for testing purposes.
   */
  public Axis()
  {
    make_axis();
  }


  /* ----------------------------- constructor ---------------------------- */
  /**
   *  Make an axis with the specified characteristics.
   *
   *  @param  start_point    The location in 3D where the min_val end of the
   *                         axis is drawn.
   *  @param  end_point      The location in 3D where the max_val end of the
   *                         axis is drawn.
   *  @param  min_val        The value along the axis that is associated with
   *                         the start_point.
   *  @param  max_val        The value along the axis that is associated with
   *                         the end_point.
   *  @param  tick_direction The direction in which the tick marks should
   *                         be drawn.  This also specifies which "side" of
   *                         of the axis the label should be drawn on.  The
   *                         tick_direction should typically be perpendicular
   *                         to the axis direction, but atleast must NOT
   *                         be collinear with the axis direcion.
   *  @param  text_alignment Specifies which side of the numeric label (TOP,
   *                         BOTTOM, LEFT, RIGHT) should be located at the
   *                         end of a tick mark.
   *  @param  axis_label     Label to place along the axis direction, beyond
   *                         the numeric labels.
   */
  public Axis( Vector3D start_point,
               Vector3D end_point,
               float    min_val,
               float    max_val,
               Vector3D tick_direction,
               int      text_alignment,
               String   axis_label      )
  {
    ignore_make_axis_request = true;   // The "set" methods all call make_axis
                                       // to recalculate the axis info after
                                       // setting parameters separately.  
    setMinMax( min_val, max_val );

    setAxisEndpoints( start_point, end_point );

    setTickDirection( tick_direction );

    setTextAlignment( text_alignment );

    label = axis_label;

    ignore_make_axis_request = false;
    make_axis();
  }


  /* --------------------------- getInstance ---------------------------- */
  /**
   *  Convenience method to construct an Axis object for some commonly used
   *  cases, by choosing appropriate default values for the other parameters.
   *  The start and end points will be used to determine whether the axis
   *  is basically in the x, y or z direction, by comparing dot products with
   *  the i,j,k vectors.  If the axis is "basically" in the x direction,
   *  the tick direction, and text alignment will be chosen as though it were
   *  in the x direction.  Also, the min and max value for the axis will be
   *  the x-values of the projections of the start_point and end_point on the
   *  i-direction.  
   *
   *  @param  start_point    The location in 3D where the min_val end of the
   *                         axis is drawn.
   *  @param  end_point      The location in 3D where the max_val end of the
   *                         axis is drawn.
   *  @param  axis_label     Label to place along the axis direction, beyond
   *                         the numeric labels.
   *
   *  @return An axis configured with useful default values.
   */
  public static Axis getInstance( Vector3D  start_point, 
                                  Vector3D  end_point, 
                                  String    axis_label )
  {
    if ( start_point == null ||                      
         end_point   == null || 
         start_point.distance(end_point) == 0 )
      return new Axis();
 
    Vector3D i = new Vector3D( 1, 0, 0 );
    Vector3D j = new Vector3D( 0, 1, 0 );
    Vector3D k = new Vector3D( 0, 0, 1 );
                                                   // find "direction" of axis 
    Vector3D axis_vec = new Vector3D( end_point );
    axis_vec.subtract( start_point );
    axis_vec.normalize();
    float max_dot = axis_vec.dot( i );
    int   max_index = 0;
    if ( axis_vec.dot( j ) > max_dot )
    {
      max_dot = axis_vec.dot( j );
      max_index = 1;
    }
    if ( axis_vec.dot( k ) > max_dot )
    {
      max_dot = axis_vec.dot( k );
      max_index = 2;
    }
                                                  // construct the axis 
    Vector3D tick_dir;
    Axis     axis = null;
    float    min_val,
             max_val;
    if ( max_index == 0 )
    {
      min_val  = start_point.dot( i );
      max_val  = end_point.dot( i );
      tick_dir = new Vector3D(0,-1,0);
      axis = new Axis( start_point, end_point, min_val, max_val,
                       tick_dir, Axis.TOP, axis_label );
    }
    else if ( max_index == 1 )
    {
      min_val  = start_point.dot( j );
      max_val  = end_point.dot( j );
      tick_dir = new Vector3D(-1,0,0);
      axis = new Axis( start_point, end_point, min_val, max_val,
                       tick_dir, Axis.BOTTOM, axis_label );
    }
    else
    {
      min_val  = start_point.dot( k );
      max_val  = end_point.dot( k );
      if ( max_dot > 0.99 )                    // axis basically in Z direction 
        tick_dir = new Vector3D(0,-1,0);       // so put labels in x,z plane
      else                                     // other wize axis is tilted
        tick_dir = new Vector3D(0,0,1);        // so put in z,axis_dir plane
      axis = new Axis( start_point, end_point, min_val, max_val,
                       tick_dir, Axis.RIGHT, axis_label );
    }
    
    return axis;
  }

  /* ---------------------------- setSkipValue --------------------------- */
  /**
   *  Specify a value that should be omitted from the axis.  This is useful
   *  since when combining several axes to form a coordiate system, the
   *  labels at the intersection point may collide.  To reset the default
   *  behavior of not skipping any values, pass in Float.NaN.
   *
   *  @param  skip   The value of the numeric label that should be omitted
   *                 when drawing this axis.
   */
  public void setSkipValue( float skip )
  {
    skip_value = skip;
    make_axis();
  }


  /* --------------------------- setCharHeight --------------------------- */
  /**
   *  Specify the height of characters to be used for the labels, instead
   *  of the default height.
   *
   *  @param  height   The height in world coordinates for the numeric labels
   *                   or Float.NaN if the default height should be used.
   */
  public void setCharHeight( float height )
  {
    if ( Float.isNaN( user_height ) || user_height > 0 )
    {
      user_height = height;
      make_axis();
    }
  }


  /* --------------------------- setMinMax --------------------------- */
  /**
   *  Specify the scale values associated with the ends of the axis.  The
   *  min_val must be less than the max_val, or the old values will be
   *  retained.
   *
   *  @param  min_val        The value along the axis that is associated with
   *                         the start_point.
   *  @param  max_val        The value along the axis that is associated with
   *                         the end_point.
   */
  public void setMinMax( float min_val, float max_val )
  {
    if ( min_val < max_val )
    {
      min = min_val;
      max = max_val;
      make_axis();
    }
  }


  /* -------------------------- setAxisEndpoints ----------------------- */
  /**
   *  Set new points for the start and end of the axis in 3D.  If either of
   *  the points are null, or if the distance between the points is 0, no
   *  action will be taken.
   *
   *  @param  start_point    The location in 3D where the min_val end of the
   *                         axis is drawn.
   *  @param  end_point      The location in 3D where the max_val end of the
   *                         axis is drawn.
   */
   public void setAxisEndpoints( Vector3D start_point, Vector3D end_point )
   {
     if ( start_point == null )
       return;

     if ( end_point == null )
       return;

     if ( start_point.distance( end_point ) == 0 )
       return;

     start = new Vector3D( start_point );
     end   = new Vector3D( end_point );
     make_axis();
   }


  /* -------------------------- setTickDirection ------------------------ */
  /**
   *  Set the tick direction to be used for the tick marks along the axis.
   *  This also determines where the labels are place.  If the tick direction
   *  is NOT perpendicular to the axis, it will be adjusted to be perpendicular.
   *  In most cases, if control of the tick direction is needed,
   *  this method should be called AFTER calling setAxisEndpoints(). 
   *  If the tick direction is along the X-axis, a random tick direction will
   *  be used.  If tick direction is null or of zero length, no action is 
   *  taken. 
   *
   *  @param  tick_direction The direction in which the tick marks should
   *                         be drawn.  This also specifies which "side" of
   *                         of the axis the label should be drawn on.  The
   *                         tick_direction should typically be perpendicular
   *                         to the axis direction, but atleast must NOT
   *                         be collinear with the axis direcion.
   */
  public void setTickDirection( Vector3D tick_direction )
  {
    if ( tick_direction != null && tick_direction.length() > 0 )
    {
      tick_dir = new Vector3D( tick_direction );
      tick_dir.normalize();
      Vector3D axis_dir = new Vector3D( end );
      axis_dir.subtract( start );
      axis_dir.normalize();
      if ( tick_dir.dot( axis_dir ) > 0.01 )    // adjust tick to be
      {                                         // perpendicular to the axis.
        Vector3D cross = new Vector3D();
        cross.cross( axis_dir, tick_dir );
        while ( cross.length() == 0 )           // tick co-linear with axis
        {                                       // try a random direction
          tick_dir = new Vector3D( (float)Math.random(),
                                   (float)Math.random(),
                                   (float)Math.random() );
          tick_dir.normalize();
          cross.cross( axis_dir, tick_dir );
        }
        cross.normalize();
        tick_dir.cross( cross, axis_dir );
      }
      make_axis();
    }
  }


  /* -------------------------- setTextAlignment -------------------------- */
  /**
   *  Set flag indicating how the numeric labels should be aligned along the
   *  axis.
   *
   *  @param  text_alignment Specifies which side of the numeric label (TOP,
   *                         BOTTOM, LEFT, RIGHT) should be located at the
   *                         end of a tick mark.
   */
  public void setTextAlignment( int text_alignment )
  {
    if ( text_alignment >= TOP && text_alignment <= RIGHT )
    {
      alignment = (int)text_alignment;
      make_axis();
    }
  }

  /* ---------------------------- setAxisLabel --------------------------- */
  /**
   *  Set the text to be placed along the axis direction.
   *
   *  @param  axis_label     Label to place along the axis direction, beyond
   *                         the numeric labels.
   */
  public void setAxisLabel( String axis_label )
  {
     label = axis_label;
     make_axis();
  }


  /* ------------------------------- Draw -------------------------------- */
  /**
   *  Method that is called "automatically" by the system to actually 
   *  Draw the axis, if the axis was added to a ThreeD_GL_Panel.
   */
  protected void Draw( GLDrawable drawable )
  {
    GL gl = drawable.getGL();
    gl.glEnable( GL.GL_NORMALIZE );
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

    if ( tick_labels != null )
      for ( int i = 0; i < tick_labels.length; i++ )
      {
        if ( tick_labels[i] != null )
          tick_labels[i].Draw( drawable ); // NOTE: Draw() method can only have
      }                                    //       methods callable inside of
                                           //       glNewList() and glEndList(),
                                           //       so we must call Draw() here,
                                           //       NOT Render()
    if ( axis_label != null )
      axis_label.Draw( drawable );
    gl.glDisable( GL.GL_NORMALIZE );
  }


  /* ----------------------------------------------------------------------
   *
   *  PRIVATE METHODS:
   *
   */

  /* ----------------------------- make_axis ------------------------------ */
  /*
   *  Calculate and store the lines and 3D text that will be drawn to 
   *  draw the axis.
   */
  private void make_axis()
  { 
    if ( ignore_make_axis_request )
      return;

    set_division_points();
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
                                               // make each tick mark and label
    float label_space = 0;
    float width = 0;
    float char_height;

    if ( Float.isNaN( user_height ) )          // use default char height
      char_height = 1.5f*tick_length;
    else
      char_height = user_height;

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
      float yellow[] = { 0.7f, 0.7f, 0 };
      text.setColor( yellow );
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
                                        // omit label for skip_value if any
      if ( !Float.isNaN( skip_value ) && 
           Math.abs( skip_value - div_points[i] ) < 0.001 * (max-min) ) 
        tick_labels[i] = null;
      else
        tick_labels[i] = text; 
    }
    
    if ( label != null && label.length() > 0 )
    { 
      Vector3D label_position = new Vector3D( axis_dir );
      label_position.multiply( axis_length/2 );
      label_position.add( start );
      tick_end.multiply( ( 3*tick_length + label_space ) / tick_length );
      label_position.add( tick_end );
    
      axis_label = new StrokeText( label, font );
      axis_label.setPosition( label_position );
      axis_label.setHeight( 1.5f * char_height );
      if ( alignment == TOP || alignment == LEFT  )
      {
        axis_label.setOrientation( axis_dir, tick_start );
        axis_label.setAlignment( StrokeText.HORIZ_CENTER, StrokeText.VERT_TOP );
      }
      else
      {
        axis_label.setOrientation( axis_dir, tick_end );
        axis_label.setAlignment(StrokeText.HORIZ_CENTER,StrokeText.VERT_BOTTOM);
      }
    }
        
    rebuild_list = true;
  }

  /* ------------------------ set_division_points ------------------------- */
  /*
   *  Calculate and store the values along the axis that will be labeled
   *  with numeric values. 
   */
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
