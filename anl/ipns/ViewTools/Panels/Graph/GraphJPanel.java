package DataSetTools.components.image;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;
//import javax.swing.*;

public class GraphJPanel extends CoordJPanel 
{
  private float           x_vals[] = { 0, 1 };
  private float           y_vals[] = { 0, 1 };

  private boolean         h_scroll = false;
  private boolean         v_scroll = false;
  
  private boolean         y_bound_set = false;
  private boolean         x_bound_set = false;

  public GraphJPanel()
  { 
    h_scroll = false;
    v_scroll = false;

    y_bound_set = false;
    x_bound_set = false;
  }


/* ------------------------------- setData -------------------------------- */

  public void setData( float x_vals[], float y_vals[] )
  {
    float x1, y1, x2, y2;

    this.x_vals = x_vals;
    this.y_vals = y_vals;
                                       // get both the currently set WC bounds
                                       // and the automatically scaled bounds
    CoordBounds current_bound   = getGlobalWorldCoords();
    current_bound.invertBounds();

    CoordBounds auto_data_bound = new CoordBounds();
    auto_data_bound.setBounds( x_vals, y_vals );

                                      // choose new y_bounds based on flag
    if ( y_bound_set )
    {
      y1 = current_bound.getY1();
      y2 = current_bound.getY2();
    }
    else
    {
      y1 = auto_data_bound.getY1();
      y2 = auto_data_bound.getY2();
    }

                                      // choose new x_bounds based on flag
    if ( x_bound_set )
    {
      x1 = current_bound.getX1();
      x2 = current_bound.getX2();
    }
    else
    {
      x1 = auto_data_bound.getX1();
      x2 = auto_data_bound.getX2();
    }

                                              // "grow" the bound slightly to
                                              // avoid problem with zooming
//    float size = (float) Math.abs( y2 - y1 );
//    y1         = y1 - 0.01f * size;
//    y2         = y2 + 0.01f * size;
    CoordBounds data_bound =  new CoordBounds( x1, y1, x2, y2 );

    data_bound.invertBounds();               // needed for "upside down" pixel 
    setGlobalWorldCoords( data_bound );      // coordinates
    setLocalWorldCoords( data_bound );
    SetTransformsToWindowSize();

    repaint();
  }

/* ------------------------------ setY_bounds ------------------------------- */

public void setY_bounds( float y_min, float y_max )
{
  CoordBounds data_bound = getGlobalWorldCoords();

  data_bound.setBounds( data_bound.getX1(), y_min,     // change and "lock" the
                        data_bound.getX2(), y_max );   // new y_min, y_max

  data_bound.invertBounds();               // needed for "upside down" pixel 
  setGlobalWorldCoords( data_bound );      // coordinates
  setLocalWorldCoords( data_bound );
  SetTransformsToWindowSize();

  repaint();
  y_bound_set = true;
}


/* ----------------------------- autoY_bounds ------------------------------ */

public void autoY_bounds( )
{
  y_bound_set = false;
  setData( x_vals, y_vals );       // to recalculate bounds and redraw
}


/* -------------------------- LocalTransformChanged ------------------------ */
public void LocalTransformChanged()
{
  repaint();
}


/* --------------------------------- paint ------------------------------- */
  public void paint( Graphics g )
  {
    SetTransformsToWindowSize();
    super.paint(g);
    int n_points = Math.min( x_vals.length, y_vals.length );

    float x_copy[] = (float[])x_vals.clone();
    float y_copy[] = (float[])y_vals.clone();

    local_transform.MapTo( x_copy, y_copy );

    if ( x_vals.length == y_vals.length )            // Function data
    { 
      int x_int[] = new int[ n_points ];
      int y_int[] = new int[ n_points ];
      for ( int i = 0; i < n_points; i++ )
      {
        x_int[i] = (int)( x_copy[i] );
        y_int[i] = (int)( y_copy[i] );
      }
      g.setColor( Color.black );
      g.drawPolyline( x_int, y_int, n_points );
    }
    else if ( x_vals.length == y_vals.length + 1 )  // Histogram data
    {
      int x_int[] = new int[ 2*y_vals.length ];
      int y_int[] = new int[ 2*y_vals.length ];
      for ( int i = 0; i < 2*y_vals.length; i++ )
      {
        x_int[i] = (int)( x_copy[(i+1)/2] );
        y_int[i] = (int)( y_copy[i/2] );
      }
      g.setColor( Color.black );
      g.drawPolyline( x_int, y_int, 2*y_vals.length );
    }
    else 
      System.out.println("ERROR: x&y arrays don't match in GraphJPanel.paint");
 
  }


/* ---------------------------- getPreferredSize ------------------------- */

public Dimension getPreferredSize()
{
    int rows, cols;

/*    if ( v_scroll )
      rows = data.length;
    else
*/
      rows = 0;

    if ( h_scroll )
      cols = x_vals.length-1;
    else
      cols = 0;

    return new Dimension( cols, rows );
}


/* -------------------------------- Main ------------------------------- */

  /* Basic main program for testing purposes only. */
  public static void main(String[] args)
  {
    JFrame f = new JFrame("Test for ImageJPanel");
    f.setBounds(0,0,500,500);
    GraphJPanel panel = new GraphJPanel();
    f.getContentPane().add(panel);
    f.setVisible(true);
  }
}
