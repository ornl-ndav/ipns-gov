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
 * Revision 1.5  2004/03/03 23:20:03  dennis
 * Increased default values for the slice dimensions and step size.
 *
 * Revision 1.4  2004/02/02 23:53:05  dennis
 * Default width and height of plane is now 5 units.
 *
 * Revision 1.3  2004/01/27 23:27:15  dennis
 * Added min/max bounds on values that can be entered.
 *
 * Revision 1.2  2004/01/27 20:40:47  dennis
 * Added method to set the slice width, height, thickness, etc.
 * Improved format of String returned by toString().
 *
 * Revision 1.1  2004/01/26 23:53:39  dennis
 * Initial version of user interface for specifying the size
 * and resolution of a rectangular slab in 3D.
 */

package DataSetTools.components.ui;

import DataSetTools.util.*;
import java.awt.*;
import java.awt.event.*;
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

  /**
   *  Smallest value allowed for width, height, thickness or step.
   */
  public static final float  MIN_VAL = 1.0E-10f;

  /**
   *  Largest value allowed for width, height, thickness or step.
   */
  public static final float  MAX_VAL = 1.0E+20f;

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
    step_ui      = new TextValueUI( "Step Size ", 0.05f );
    width_ui     = new TextValueUI( "Width "    , 10    );
    height_ui    = new TextValueUI( "Height "   , 10    );
    thickness_ui = new TextValueUI( "Thickness ", 0.0f  );

    step_ui.setLimits( MIN_VAL, MAX_VAL );
    width_ui.setLimits( MIN_VAL, MAX_VAL );
    height_ui.setLimits( MIN_VAL, MAX_VAL );
    thickness_ui.setLimits( MIN_VAL, MAX_VAL );

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


  /* ------------------------- setStepSize --------------------------- */
  /**
   *  Set the current step size, MUST be positive.
   *
   *  @param new_step  The new step size to use, MUST be positive.
   */
  public void setStepSize( float new_step )
  {
    if ( new_step <= 0 )
    {
      System.out.println("ERROR: new_step <= 0, ignored in setStepSize()" );
      return;
    }
    step_ui.setValue( new_step );
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


  /* --------------------------- setSliceWidth --------------------------- */
  /**
   *  Set the current width, MUST be positive.
   *
   *  @param  new_width  The new width to use, MUST be positive.
   */
  public void setSliceWidth( float new_width )
  {
    if ( new_width <= 0 )
    {
      System.out.println("ERROR: new_width <= 0, ignored in setSliceWidth()" );
      return;
    }
    width_ui.setValue( new_width );
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


  /* -------------------------- setSliceHeight --------------------------- */
  /**
   *  Set the current height, MUST be positive.
   *
   *  @param  new_height  The new height to use, MUST be positive.
   */
  public void setSliceHeight( float new_height )
  {
    if ( new_height <= 0 )
    {
      System.out.println("ERROR: new_height <= 0, ignored in setSliceHeight()");
      return;
    }
    height_ui.setValue( new_height );
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


  /* ------------------------- setSliceThickness ------------------------- */
  /**
   *  Set the current thickness, MUST be positive.
   *
   *  @param  new_thickness  The new thickness to use, MUST be positive.
   */
  public void setSliceThickness( float new_thickness )
  {
    if ( new_thickness <= 0 )
    {
      System.out.println("ERROR: new_thickness <= 0, " + 
                         "ignored in setSliceThickness()");
      return;
    }
    thickness_ui.setValue( new_thickness );
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
    return step_ui.getLabel()      + ": " + step_ui.getValue() + "  " +
           width_ui.getLabel()     + ": " + width_ui.getValue() + "  " +
           height_ui.getLabel()    + ": " + height_ui.getValue() + "  " +
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
