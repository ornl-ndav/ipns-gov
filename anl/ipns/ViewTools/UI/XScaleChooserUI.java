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
 *  Revision 1.1  2001/07/20 16:36:15  dennis
 *  GUI device for choosing X-Scales.
 *
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

public class XScaleChooserUI extends    JPanel
                             implements Serializable 
{
  public static final String N_STEPS_CHANGED = "N Steps Changed";
  public static final String X_RANGE_CHANGED = "X Range Changed";

  private String      border_label = "X Scale";
  private TextValueUI n_steps_ui   = null;
  private TextRangeUI x_range_ui   = null;
  private Vector      listeners    = null;
 
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
    listeners = new Vector();

    TitledBorder border = 
             new TitledBorder(LineBorder.createBlackLineBorder(), border_label);
    border.setTitleFont( FontUtil.BORDER_FONT );
    setBorder( border );

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
  *  @return  an X scale generated from the x range and number of steps
  *           specified by the user.
  *
  */
 public XScale getXScale()
  {
//    System.out.println("Start XScaleChooser.getXScale()......");
    int num_steps = (int)n_steps_ui.getValue();
    float x_min = x_range_ui.getMin();
    float x_max = x_range_ui.getMax();
    int num_x;

    if ( num_steps <= 0 )                 // just use a single point, the
    {                                     // center of the interval
      x_min = (x_min + x_max) / 2;
      x_max = x_min;
      num_x = 1;
    }
    else
      num_x = num_steps + 1;

//    System.out.println("x_min, x max = " + x_min + ", " + x_max );
//    System.out.println("num_steps, num_x = " + num_steps + ", " + num_x );
    return ( new UniformXScale( x_min, x_max, num_x ) );
  }


 /* ------------------------ addActionListener -------------------------- */
 /**
  *  Add an ActionListener for this XScaleChooserUI.  Whenever the user
  *  changes the interval or number of steps, an ActionEvent will be sent 
  *  to all of the ActionListeners.
  *
  *  @param listener  An ActionListener whose ActionPerformed() method is
  *                   to be called when the user changes the interval or
  *                   number of bins.
  */

  public void addActionListener( ActionListener listener )
  {
    for ( int i = 0; i < listeners.size(); i++ )       // don't add it if it's
      if ( listeners.elementAt(i).equals( listener ) ) // already there
        return;
    listeners.add( listener );
  }


 /* ------------------------ removeActionListener ------------------------ */
 /**
  *  Remove the specified ActionListener from this AnimationController.  If
  *  the specified ActionListener is not in the list of ActionListeners for
  *  for this controller this method has no effect.
  *  NOTE: This method is NOT thread safe.  It should NOT be called when
  *        the controller is being activated either by the user, or if it is
  *        running forward or backward.
  *
  *  @param listener  The ActionListener to be removed.
  */

  public void removeActionListener( ActionListener listener )
  {
    listeners.remove( listener );
  }

/* -------------------------------------------------------------------------
 *
 *  PRIVATE METHODS
 *
 */

/* -------------------------- send_message ------------------------------- */
/**
 *  Send a message to all of the action listeners for the XScaleChooserUI
 */
 private void send_message( String message )
 {
   for ( int i = 0; i < listeners.size(); i++ )
   {
     ActionListener listener = (ActionListener)listeners.elementAt(i);
     listener.actionPerformed( new ActionEvent( this, 0, message ) );
   }
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
/*
      value_ui.addActionListener( new ActionListener()
       {
         public void actionPerformed(ActionEvent e)
         {
           System.out.println("Entered: " + value_ui.getText() );
           System.out.println("Value = " + value_ui.getValue() );
         }
       });
*/

      f.setVisible(true);
    }
}
