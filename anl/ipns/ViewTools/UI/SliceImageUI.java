/*
 * File:  SliceImageUI.java
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
 * Revision 1.1  2004/01/26 23:53:39  dennis
 * Initial version of user interface for specifying the size
 * and resolution of a rectangular slab in 3D.
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

public class SliceImageUI extends    ActiveJPanel
                          implements Serializable 
{
  public static final String VALUE_CHANGED = "Value Changed";

  private TextValueUI  step_ui;
  private TextValueUI  width_ui;
  private TextValueUI  height_ui;
  private TextValueUI  thickness_ui;

  /*-------------------------- default constructor ----------------------- */
  /**
   *  Construct a SliceImageUI with default values.
   */
  public SliceImageUI( String title )
  {
    step_ui      = new TextValueUI( "Step Size ", 0.02f );
    width_ui     = new TextValueUI( "Width "    , 2     );
    height_ui    = new TextValueUI( "Height "   , 2     );
    thickness_ui = new TextValueUI( "Thickness ", 0.01f );

    TitledBorder border =
                 new TitledBorder(LineBorder.createBlackLineBorder(), title );
    border.setTitleFont( FontUtil.BORDER_FONT );
    setBorder( border );

    setLayout( new GridLayout(4,1) );
    add( step_ui );
    add( width_ui );
    add( height_ui );
    add( thickness_ui );

    ValueListener value_listener = new ValueListener();
    step_ui.addActionListener( value_listener );
    width_ui.addActionListener( value_listener );
    height_ui.addActionListener( value_listener );
    thickness_ui.addActionListener( value_listener );
  }

  /* ------------------------- getStepSize --------------------------- */
  /**
   *  Get the currently selected step size.
   *
   *  @return the currently selected steps/unit.
   */
  public float getStepSize()
  {
    return step_ui.getValue();
  }


  /* --------------------------- getSliceWidth --------------------------- */
  /**
   *  Get the currently selected width.
   *
   *  @return the currently selected width.
   */
  public float getSliceWidth()
  {
    return width_ui.getValue();
  }


  /* -------------------------- getSliceHeight --------------------------- */
  /**
   *  Get the currently selected height.
   *
   *  @return the currently selected height.
   */
  public float getSliceHeight()
  {
    return height_ui.getValue();
  }


  /* ------------------------- getSliceThickness ------------------------- */
  /**
   *  Get the currently selected thickness.
   *
   *  @return the currently selected thickness.
   */
  public float getSliceThickness()
  {
    return thickness_ui.getValue();
  }


  /* ----------------------------- toString ------------------------------ */
  /**
   *  Return a string form of this plane.
   */
  public String toString()
  {
    return step_ui.getLabel()      + ": " + step_ui.getValue() +
           width_ui.getLabel()     + ": " + width_ui.getValue() +
           height_ui.getLabel()    + ": " + height_ui.getValue() +
           thickness_ui.getLabel() + ": " + thickness_ui.getValue();
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
    JFrame  f = new JFrame("Test for SliceImageUI");
    f.setBounds( 0, 0, 200, 200 ); 

    final SliceImageUI test = new SliceImageUI("Image Size");

    test.addActionListener( new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        System.out.println("New Values ----------------------------" );
        System.out.println("" + test.getStepSize() );
        System.out.println("" + test.getSliceWidth() );
        System.out.println("" + test.getSliceHeight() );
        System.out.println("" + test.getSliceThickness() );
        System.out.println("--------------------------------------" );
      }
    });

    f.getContentPane().setLayout( new GridLayout(1,1) );
    f.getContentPane().add( test );
    f.setVisible(true);
  }

}
