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
 * Revision 1.3  2004/01/28 21:59:38  dennis
 * Removed debug print.
 *
 * Revision 1.2  2004/01/27 20:37:58  dennis
 * Added methods to get/set all relevant paramters, such as the
 * plane, width, height, depth and step size.
 * Added a StepListener, which calculates and sets a new plane
 * when the < or > buttons are pressed.
 *
 * Revision 1.1  2004/01/26 23:55:12  dennis
 * Initial version of user interface for selecting
 * a plane, image size and stepping a
 * rectangular slab, forward and backward in 3D.
 *
 */

package DataSetTools.components.ui;

import DataSetTools.util.*;
import DataSetTools.math.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.io.*;

/**
 *  This class provides a user interface for specifying the plane, size and
 *  resolution of an image of a rectangular slab in 3D.
 */

public class SliceSelectorUI extends    ActiveJPanel
                             implements ISlicePlaneSelector,
                                        Serializable 
{
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

    stepper.addActionListener( new StepListener() );
  }


  /* ---------------------------- setPlane ----------------------------- */
  /**
   *  Set the plane whose parameters are to be displayed.
   *
   *  @param new_plane  The plane to display.
   */
  public void setPlane( SlicePlane3D new_plane )
  {
    plane_selector.setPlane( new_plane );
  }


  /* ---------------------------- getPlane ----------------------------- */
  /**
   *  Get the currently specified plane, which may be null if the user
   *  supplied values don't specifiy a valid plane.
   *
   *  @return the currently specified plane, or null if there is no valid
   *          plane specified.
   */
  public SlicePlane3D getPlane()
  {
    return plane_selector.getPlane();
  }


  /* ------------------------- setStepSize --------------------------- */
  /**
   *  Set the current step size, MUST be positive.
   *
   *  @param new_step  The new step size to use, MUST be positive.
   */
  public void setStepSize( float new_step )
  {
    image_selector.setStepSize( new_step );
  }


  /* ---------------------------- getStepSize ---------------------------- */
  /**
   *  Get the currently selected step size.
   *
   *  @return the currently selected steps/unit.
   */
  public float getStepSize()
  {
    return image_selector.getStepSize();
  }


  /* --------------------------- setSliceWidth --------------------------- */
  /**
   *  Set the current width, MUST be positive.
   *
   *  @param  new_width  The new width to use, MUST be positive.
   */
  public void setSliceWidth( float new_width )
  {
    image_selector.setSliceWidth( new_width );
  }


  /* --------------------------- getSliceWidth --------------------------- */
  /**
   *  Get the currently selected width.
   *
   *  @return the currently selected width.
   */
  public float getSliceWidth()
  {
    return image_selector.getSliceWidth();
  }


  /* -------------------------- setSliceHeight --------------------------- */
  /**
   *  Set the current height, MUST be positive.
   *
   *  @param  new_height  The new height to use, MUST be positive.
   */
  public void setSliceHeight( float new_height )
  {
     image_selector.setSliceHeight( new_height );
  }


  /* -------------------------- getSliceHeight --------------------------- */
  /**
   *  Get the currently selected height.
   *
   *  @return the currently selected height.
   */
  public float getSliceHeight()
  {
    return image_selector.getSliceHeight();
  }


  /* ------------------------- setSliceThickness ------------------------- */
  /**
   *  Set the current thickness, MUST be positive.
   *
   *  @param  new_thickness  The new thickness to use, MUST be positive.
   */
  public void setSliceThickness( float new_thickness )
  {

    image_selector.setSliceThickness( new_thickness );
  }


  /* ------------------------- getSliceThickness ------------------------- */
  /**
   *  Get the currently selected thickness.
   *
   *  @return the currently selected thickness.
   */
  public float getSliceThickness()
  {
    return image_selector.getSliceThickness();
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
      send_message( ISlicePlaneSelector.PLANE_CHANGED );
    }
  }


  /* ------------------------ StepListener ------------------------------ */
  /*
   *  Listen for a new value.
   */
  private class StepListener implements ActionListener
  {
    public void actionPerformed( ActionEvent e )
    {
                                      // move the plane based on specified step
      SlicePlane3D plane = plane_selector.getPlane();
      Vector3D origin = plane.getOrigin();
      Vector3D normal = plane.getNormal();
      float step = stepper.getStep(); 
      normal.normalize();
      normal.multiply( step );
      origin.add( normal );
      plane.setOrigin( origin );
      plane_selector.setPlane( plane );

      send_message( ISlicePlaneSelector.PLANE_CHANGED );
    }
  }


  /* ------------------------------ main --------------------------------- */
  /**
   *  Main program providing basic functionality test.
   */
  public static void main( String args[] )
  {
    JFrame  f = new JFrame("Test for SliceSelectorUI");
    f.setBounds( 0, 0, 210, 350 ); 

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
