/*
 * File:  SliceSelectorUI.java
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
 * Revision 1.1  2004/01/26 23:55:12  dennis
 * Initial version of user interface for selecting
 * a plane, image size and stepping a
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
 *  This class provides a user interface for specifying the plane, size and
 *  resolution of an image of a rectangular slab in 3D.
 */

public class SliceSelectorUI extends    ActiveJPanel
                             implements Serializable 
{
  public static final String VALUE_CHANGED = "Value Changed";
  
  private SlicePlane3D_UI plane_selector;
  private SliceImageUI    image_selector;
  private SliceStepperUI  stepper;


  /*-------------------------- default constructor ----------------------- */
  /**
   *  Construct a SliceSelectorUI with default values.
   */
  public SliceSelectorUI( String title )
  {
    plane_selector = new SlicePlane3D_UI( "Slice Plane" );
    image_selector = new SliceImageUI( "Slice Image" );
    stepper        = new SliceStepperUI( "Step In/Out" );

    TitledBorder border =
                 new TitledBorder(LineBorder.createBlackLineBorder(), title );
    border.setTitleFont( FontUtil.BORDER_FONT );
    setBorder( border );

    Box box = new Box( BoxLayout.Y_AXIS );
    box.add( plane_selector );
    box.add( image_selector );
    box.add( stepper );

    setLayout( new GridLayout(1,1) );
    add(box);

    ValueListener value_listener = new ValueListener();
    plane_selector.addActionListener( value_listener );
    image_selector.addActionListener( value_listener );
    stepper.addActionListener( value_listener );
  }


  /* ----------------------------- toString ------------------------------ */
  /**
   *  Return a string form of this plane.
   */
  public String toString()
  {
    return "" + plane_selector.toString() + "\n" 
              + image_selector.toString() + "\n" 
              + stepper.toString() + "\n";
  }

  /* -----------------------------------------------------------------------
   *
   *  PRIVATE CLASSES
   *
   */
  /* ------------------------ ValueListener ------------------------------ */
  /*
   *  Listen for a new value.
   */ 
  private class ValueListener implements ActionListener
  {
    public void actionPerformed( ActionEvent e )
    {
      System.out.println("Value changed");
      send_message( VALUE_CHANGED );
    }
  }

  /* ------------------------------ main --------------------------------- */
  /**
   *  Main program providing basic functionality test.
   */
  public static void main( String args[] )
  {
    JFrame  f = new JFrame("Test for SliceSelectorUI");
    f.setBounds( 0, 0, 210, 300 ); 

    final SliceSelectorUI test = new SliceSelectorUI("HKL Slice");

    test.addActionListener( new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        System.out.println("New Values ----------------------------" );
        System.out.println("" + test );
        System.out.println("--------------------------------------" );
      }
    });

    f.getContentPane().setLayout( new GridLayout(1,1) );
    f.getContentPane().add( test );
    f.setVisible(true);
  }

}
