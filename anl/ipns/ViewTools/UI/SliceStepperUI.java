/*
 * File:  SliceStepperUI.java
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
 * Revision 1.2  2004/01/27 20:36:06  dennis
 * Changing step size no longer sends message.
 * Changed getStepSize() to getStep() which returns the last
 * requested step amount as a signed value.
 *
 * Revision 1.1  2004/01/26 23:54:32  dennis
 * Initial version of user interface for stepping a
 * rectangular slab, forward and backward in 3D.
 *
 */

package DataSetTools.components.ui;

import DataSetTools.util.*;
import java.awt.*;
import java.awt.event.*;
//import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.io.*;

/**
 *  This class provides a user interface for specifying the size and
 *  resolution of an image of a rectangular slab in 3D.
 */

public class SliceStepperUI extends    ActiveJPanel
                            implements Serializable 
{
  public static final String STEP = "Step";

  private TextValueUI  step_ui;
  private float        sign = 1;

  /*-------------------------- default constructor ----------------------- */
  /**
   *  Construct a SliceStepperUI with default values.
   */
  public SliceStepperUI( String title )
  {
    step_ui      = new TextValueUI( "Step Depth ", 0.02f );

    JButton backward_button = new JButton( "<" );
    JButton forward_button = new JButton( ">" );
    JPanel  button_panel = new JPanel();
    button_panel.setLayout( new GridLayout( 1, 2 ) );
    button_panel.add( backward_button );
    button_panel.add( forward_button );

    TitledBorder border =
                 new TitledBorder(LineBorder.createBlackLineBorder(), title );
    border.setTitleFont( FontUtil.BORDER_FONT );
    setBorder( border );

    setLayout( new GridLayout(2,1) );
    add( step_ui );
    add( button_panel );

    ButtonListener button_listener = new ButtonListener();
    backward_button.addActionListener( button_listener );
    forward_button.addActionListener( button_listener );
  }

  /* ----------------------------- getStep ------------------------------ */
  /**
   *  Get the current step amount, either the step size or minus step 
   *  size, depending on whether the forward or backward button was 
   *  pressed last. 
   *
   *  @return  plus or minus the step size, depending on what button was
   *           pressed last.
   */
  public float getStep()
  {
    return sign * step_ui.getValue();
  }

  /* ----------------------------- toString ------------------------------ */
  /**
   *  Return a string form of this plane.
   */
  public String toString()
  {
    return step_ui.getLabel() + ": " + getStep();
  }

  /* -----------------------------------------------------------------------
   *
   *  PRIVATE CLASSES
   *
   */
  /* ------------------------ ButtonListener ------------------------------ */
  /*
   *  Listen for a button press on the "<" and ">" buttons.
   */ 
  private class ButtonListener implements ActionListener
  {
    public void actionPerformed( ActionEvent e )
    {
      String command = e.getActionCommand();
      if ( command.equals( "<" ) )
        sign = -1;
      else if  ( command.equals( ">" ) )
        sign = +1;
      else 
        System.out.println("Invalid action command in SliceSteperUI");
         
      send_message( ISlicePlaneSelector.PLANE_CHANGED );
    }
  }

  /* ------------------------------ main --------------------------------- */
  /**
   *  Main program providing basic functionality test.
   */
  public static void main( String args[] )
  {
    JFrame  f = new JFrame("Test for SliceStepperUI");
    f.setBounds( 0, 0, 200, 100 ); 

    final SliceStepperUI test = new SliceStepperUI("Move Slice");

    test.addActionListener( new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        System.out.println("New Values ----------------------------" );
        System.out.println("" + test.getStep() );
        System.out.println("--------------------------------------" );
      }
    });

    f.getContentPane().setLayout( new GridLayout(1,1) );
    f.getContentPane().add( test );
    f.setVisible(true);
  }

}
