/*
 * @(#)XScaleChooserUI.java
 *
 * Copyright (C) 2001, Dennis Mikkelson
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
 *  $Log$
 *  Revision 1.4  2002/07/12 18:36:17  dennis
 *  Now returns NULL if users specifies <= 0 steps on the XScale.
 *  Viewers using this must trap this and use a default XScale
 *  appropriate to the viewer.
 *
 *  Revision 1.3  2002/03/18 21:40:16  dennis
 *  Constructor now checks that the min and max values are valid.
 *
 *  Revision 1.2  2001/07/26 19:56:59  dennis
 *  Now extends ActiveJPanel instead of JPanel, so that it doesn't
 *  have to maintain the list of listeners itself.
 *
 *  Revision 1.1  2001/07/20 16:36:15  dennis
 *  GUI device for choosing X-Scales.
 *
 */

package DataSetTools.components.ui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;
import DataSetTools.dataset.*;
import DataSetTools.util.*;

/**
 * An XScaleChooserUI bject allows the user to specify an XScale by specifying
 * an interval and a number of bins.  Currently only uniform XScales are
 * supported.  Future versions may include options for VariableXScales. 
 */

public class XScaleChooserUI extends    ActiveJPanel
                             implements Serializable 
{
  public static final String N_STEPS_CHANGED = "N Steps Changed";
  public static final String X_RANGE_CHANGED = "X Range Changed";

  private String      border_label = "X Scale";
  private TextValueUI n_steps_ui   = null;
  private TextRangeUI x_range_ui   = null;
 
 /* ------------------------------ CONSTRUCTOR ---------------------------- */
 /** 
  *  Construct a TextValueUI object with the specifed range, default number
  *  of bins and units label.
  *
  *  @param  border_label  String to be used for the title on the border.
  *  @param  units_label   String to be used for the label for x range control.
  *  @param  x_min         Left endpoint for the x range control.
  *  @param  x_max         Right endpoint for the x range control.
  *  @param  n_steps       Initial number of steps to be used. 
  *
  */
  public XScaleChooserUI( String border_label,
                          String units_label, 
                          float  x_min, 
                          float  x_max, 
                          int    n_steps )
  { 
    TitledBorder border = 
             new TitledBorder(LineBorder.createBlackLineBorder(), border_label);
    border.setTitleFont( FontUtil.BORDER_FONT );
    setBorder( border );

    if ( Float.isNaN(x_min) || Float.isInfinite(x_min)  ||
         Float.isNaN(x_max) || Float.isInfinite(x_max)  ||
         x_min >= x_max )
    {
      System.out.println("Error: x_min, x_max invalid in XScaleChooserUI");
      System.out.println("x_min = " + x_min + ", x_max = " + x_max);
      System.out.println("using default interval [0,1]");
      x_min = 0;
      x_max = 1; 
    } 

    if ( n_steps <= 1 )
    {
      System.out.println("Error: n_steps invalid in XScaleChooserUI");
      System.out.println("n_steps = " + n_steps);
      System.out.println("using default, 1 ");
      n_steps = 1;
    }

    setLayout( new GridLayout(2,1) );
    x_range_ui = new TextRangeUI(units_label, x_min, x_max );
    x_range_ui.addActionListener( new X_Range_Listener() );
    add( x_range_ui );

    n_steps_ui = new TextValueUI( "Num Steps", n_steps );
    n_steps_ui.addActionListener( new NumSteps_Listener() );
    add( n_steps_ui );
  }


/* ------------------------------ getXScale ------------------------------ */
 /**
  *  Return an X scale specified by the user.
  *
  *  @return  If the number of steps specified is > 0, return an X scale 
  *           generated from the x range and number of steps specified by 
  *           the user, otherwise, return null.
  *
  */
 public XScale getXScale()
  {
    int num_steps = (int)n_steps_ui.getValue();
    float x_min = x_range_ui.getMin();
    float x_max = x_range_ui.getMax();
    int num_x;

    if ( num_steps <= 0 )                  // invalid, application should use
      return null;                         // it's default XScale

    num_x = num_steps + 1;                 // build uniform XScale as specified
    return ( new UniformXScale( x_min, x_max, num_x ) );
  }


/* -------------------------------------------------------------------------
 *
 * INTERNAL CLASSES 
 *
 */

/* ------------------------ X_Range_Listener ----------------------------- */

  private class X_Range_Listener implements ActionListener,
                                            Serializable
  {
     float last_x_min;
     float last_x_max;

     public X_Range_Listener()
     {
       last_x_min = x_range_ui.getMin();
       last_x_max = x_range_ui.getMax();
     }

     public void actionPerformed(ActionEvent e)
     {
       float x_min = x_range_ui.getMin();
       float x_max = x_range_ui.getMax();

       if ( last_x_min != x_min || last_x_max != x_max )
       {
         last_x_min = x_min;
         last_x_max = x_max;
         send_message( X_RANGE_CHANGED );
       }
     }
  }


/* ------------------------ NumSteps_Listener ----------------------------- */

  private class NumSteps_Listener implements ActionListener,
                                             Serializable
  {
     int last_num_steps;

     public NumSteps_Listener()
     {
       last_num_steps = (int)n_steps_ui.getValue();
     }

     public void actionPerformed(ActionEvent e)
     {
       int num_steps = (int)n_steps_ui.getValue();
       if ( num_steps != last_num_steps )
       { 
         if ( num_steps >= 0 )                     // only allow num_steps >= 0
           last_num_steps = num_steps;
         
         n_steps_ui.setValue( last_num_steps );    // set to integer value
         send_message( N_STEPS_CHANGED );
       }
     }
  }
  


/* -------------------------------------------------------------------------
 *
 * MAIN  ( Basic main program for testing purposes only. )
 *
 */
    public static void main(String[] args)
    {
      JFrame f = new JFrame("Test for XScaleChooserUI");
      f.setBounds(0,0,200,150);
      final XScaleChooserUI x_scale_ui = 
            new XScaleChooserUI( "X Scale", "TOF", 1000, 10000, 500);

      f.getContentPane().setLayout( new GridLayout(1,1) );
      f.getContentPane().add( x_scale_ui );

      x_scale_ui.addActionListener( new ActionListener()
       {
         public void actionPerformed(ActionEvent e)
         {
           System.out.println("Chose: " + x_scale_ui.getXScale() );
         }
       });


      f.setVisible(true);
    }
}
