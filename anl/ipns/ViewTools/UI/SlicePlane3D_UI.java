/*
 * File:  SlicePlane3D_UI.java
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
 * Revision 1.3  2004/01/26 21:15:25  dennis
 * Now has four tabbed panes for selecting slice plane.
 * Added beep if plane is improperly specified.
 * Added listener in main test program.
 *
 * Revision 1.2  2004/01/26 18:20:38  dennis
 * Added ThreePointSliceSelector to tabbed pane.
 * Added getPlane() method.
 *
 * Revision 1.1  2004/01/24 23:34:57  dennis
 * Panel with a tabbed pane, providing different ways
 * of specifying a plane.  Currently only supports
 * one way, using an HKL_SliceSelector.(not complete)
 *
 */

package DataSetTools.components.ui;

import DataSetTools.math.*;
import DataSetTools.util.*;
import java.awt.*;
import java.awt.event.*;
//import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.io.*;

/**
 *  This class provides a user interface for specifying a plane in 3D 
 *  as a point that is the origin of a coordinate system on the plane 
 *  and two orthogonal basis vectors that serve as the coordinate axes.
 *  The plane information can be provided in various ways, including 
 *  specifying three points, specifying the plane normal and the first 
 *  basis vector, etc.
 */

public class SlicePlane3D_UI extends    ActiveJPanel
                             implements Serializable 
{
  private SlicePlane3D old_slice_plane;
  private JTabbedPane  tabbed_pane;
  private HKL_SliceSelector              hkl_selector;
  private ThreePointSliceSelector        three_point_selector;
  private CenterNormalSliceSelector      center_normal_selector;
  private CenterNormalPointSliceSelector center_normal_point_selector;

  /*-------------------------- default constructor ----------------------- */
  /**
   *  Construct a SlicePlane3D_UI with default values.
   */
  public SlicePlane3D_UI( String title )
  {
    hkl_selector = new HKL_SliceSelector();
    hkl_selector.addActionListener( new PaneListener() );
    SlicePlane3D slice_plane = hkl_selector.getPlane();

    three_point_selector = new ThreePointSliceSelector();
    three_point_selector.addActionListener( new PaneListener() );
    three_point_selector.setPlane( slice_plane );
     
    center_normal_selector = new CenterNormalSliceSelector();
    center_normal_selector.addActionListener( new PaneListener() );
    center_normal_selector.setPlane( slice_plane );

    center_normal_point_selector = new CenterNormalPointSliceSelector();
    center_normal_point_selector.addActionListener( new PaneListener() );
    center_normal_point_selector.setPlane( slice_plane );
     
    tabbed_pane = new JTabbedPane();
    tabbed_pane.setFont( FontUtil.LABEL_FONT );
    tabbed_pane.addTab( "CN",  center_normal_selector );
    tabbed_pane.addTab( "CPP", three_point_selector );
    tabbed_pane.addTab( "CNP", center_normal_point_selector );
    tabbed_pane.addTab( "CHKL", hkl_selector );
    tabbed_pane.setSelectedIndex( 3 );
    tabbed_pane.addChangeListener( new TabListener() );

    TitledBorder border =
                 new TitledBorder(LineBorder.createBlackLineBorder(), title );
    border.setTitleFont( FontUtil.BORDER_FONT );
    setBorder( border );

    setLayout( new GridLayout(1,1) );
    add( tabbed_pane );
    old_slice_plane = getPlane();
  }

  /* ---------------------------- getPlane ------------------------------ */
  /**
   *  Get the currently selected plane, or null if no plane is properly
   *  specified.
   *
   *  @return the currently selected plane, or null if not properly specified.
   */
  public SlicePlane3D getPlane()
  {
    ISlicePlaneSelector selector =
                      (ISlicePlaneSelector)tabbed_pane.getSelectedComponent();
    return selector.getPlane();
  }

  /* ----------------------------- toString ------------------------------ */
  /**
   *  Return a string form of this plane.
   */
  public String toString()
  {
    SlicePlane3D plane = getPlane();
    if ( plane != null )
      return plane.toString();
    else
      return "NO PLANE SELECTED";
  }

  /* -----------------------------------------------------------------------
   *
   *  PRIVATE CLASSES
   *
   */
  /* ------------------------ TabListener ------------------------------ */
  /*
   *  Listen for change to a new selected pane.  Update the values from the
   *  current plane, if possible.
   */ 
  private class TabListener implements ChangeListener
  {
    public void stateChanged(ChangeEvent e)
    {
      ISlicePlaneSelector selector =
                       (ISlicePlaneSelector)tabbed_pane.getSelectedComponent();
      selector.setPlane( old_slice_plane ); 
    }
  }

  /* -------------------------- PaneListener --------------------------- */
  /*
   *  Listen to the individual panes, and get the new plane selected by
   *  the current tabbed pane.
   */
  private class PaneListener implements ActionListener,
                                        Serializable
  {
    public void actionPerformed( ActionEvent e )
    {
      ISlicePlaneSelector selector = 
                      (ISlicePlaneSelector)tabbed_pane.getSelectedComponent();

      SlicePlane3D new_plane = selector.getPlane();

      if ( new_plane == null )              // invalid, so just restore old one
      {                                        
        Toolkit.getDefaultToolkit().beep();
        selector.setPlane( old_slice_plane );
        return;
      }

      old_slice_plane = new_plane;
      send_message( ISlicePlaneSelector.PLANE_CHANGED );
    }
  }


  /**
   *  Main program providing basic functionality test.
   */
  public static void main( String args[] )
  {
    JFrame  f = new JFrame("Test for SlicePlane3D_UI");
    f.setBounds( 0, 0, 200, 200 ); 

    final SlicePlane3D_UI test = new SlicePlane3D_UI("Select Plane");

    test.addActionListener( new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        System.out.println("New Plane ----------------------------" );
        System.out.println("" + test.getPlane() );
        System.out.println("--------------------------------------" );
      }
    });

    f.getContentPane().setLayout( new GridLayout(1,1) );
    f.getContentPane().add( test );
    f.setVisible(true);
  }

}
