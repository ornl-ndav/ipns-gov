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
 * Revision 1.6  2004/02/10 05:33:03  bouzekc
 * Now uses IsawToolkit.beep().
 *
 * Revision 1.5  2004/02/02 23:52:19  dennis
 * Added setMode() method to allow switching between HKL
 * and Qxyz plane selections.
 *
 * Revision 1.4  2004/01/27 20:39:28  dennis
 * Added method setPlane().
 * Now sets a PreferredSize on the hkl_selector to help keep
 * the layout ok.
 *
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
 */

package DataSetTools.components.ui;

import DataSetTools.math.*;
import DataSetTools.util.*;
import java.awt.*;
import java.awt.event.*;
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
                             implements ISlicePlaneSelector,
                                        Serializable 
{
  public  static final int    HKL_MODE  = HKL_SliceSelector.CONSTANT_HKL_MODE;
  public  static final int    QXYZ_MODE = HKL_SliceSelector.CONSTANT_QXYZ_MODE;

  private static final String HKL_TITLE  = "Select HKL Plane";
  private static final String QXYZ_TITLE = "Select Qxyz Plane";

  private static final String HKL_TAB_TITLE  = "CHKL";
  private static final String QXYZ_TAB_TITLE = "CQxyz";

  private SlicePlane3D old_slice_plane;
  private JTabbedPane  tabbed_pane;
  private HKL_SliceSelector              hkl_selector;
  private ThreePointSliceSelector        three_point_selector;
  private CenterNormalSliceSelector      center_normal_selector;
  private CenterNormalPointSliceSelector center_normal_point_selector;

  private boolean debug = false;


  /*----------------------------- constructor ---------------------------- */
  /**
   *  Construct a SlicePlane3D_UI in HKL_MODE or QXYZ_MODE, as specified.
   *
   *  @param  mode  Integer code should be one of HKL_MODE or QXYZ_MODE, or
   *                HKL_MODE will be used by default.
   */
  public SlicePlane3D_UI( int mode )
  {
    hkl_selector = new HKL_SliceSelector();
    hkl_selector.setPreferredSize( new Dimension( 180, 30 ) );
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
    tabbed_pane.addTab( "", hkl_selector );
    tabbed_pane.setSelectedIndex( 3 );
    tabbed_pane.addChangeListener( new TabListener() );

    setLayout( new GridLayout(1,1) );
    add( tabbed_pane );
    old_slice_plane = getPlane();

    setMode( mode );
  }


  /* ----------------------------- setMode ----------------------------- */
  /**
   *  Set the display mode for this slice plane widget to either hkl or Qxzy
   *  mode.
   *
   *  @param  mode  Integer code must be one of HKL_MODE or QXYZ_MODE, other
   *                values will be ignored.
   */
  public void setMode( int mode )
  {
    if ( mode != HKL_MODE && mode != QXYZ_MODE )
      return;

    String title       = HKL_TITLE;
    String tab_title   = HKL_TAB_TITLE;
    int    label_mode  = HKL_MODE;

    if ( mode == QXYZ_MODE )
    {
      title      = QXYZ_TITLE;
      tab_title  = QXYZ_TAB_TITLE;
      label_mode = QXYZ_MODE;
    }

    hkl_selector.setLabels( mode );
    tabbed_pane.setTitleAt( 3, tab_title );
    TitledBorder border =
                 new TitledBorder(LineBorder.createBlackLineBorder(), title );
    border.setTitleFont( FontUtil.BORDER_FONT );
    setBorder( border );
  }


  /* ---------------------------- setPlane ----------------------------- */
  /**
   *  Set the plane whose parameters are to be displayed by this plane selector.
   *
   *  @param new_plane  The plane to display in this selector.
   */
  public void setPlane( SlicePlane3D new_plane )
  {
    center_normal_selector.setPlane( new_plane );
    three_point_selector.setPlane( new_plane );
    center_normal_point_selector.setPlane( new_plane );  
    hkl_selector.setPlane( new_plane ); 
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
      if ( tabbed_pane == null )
      {                            // For some reason this was null sometimes
                                   // after the mode concept was added
        if ( debug )
          System.out.println("ERROR: tabbed_pane == null in PaneListener" );
        return;
      }
      ISlicePlaneSelector selector = 
                      (ISlicePlaneSelector)tabbed_pane.getSelectedComponent();

      if ( selector == null )
        return;

      SlicePlane3D new_plane = selector.getPlane();

      if ( new_plane == null )              // invalid, so just restore old one
      {                                        
        IsawToolkit.beep();
        selector.setPlane( old_slice_plane );
        return;
      }

      old_slice_plane = new_plane;
      send_message( ISlicePlaneSelector.PLANE_CHANGED );
    }
  }


  /* ------------------------------- main ----------------------------- */
  /**
   *  Main program providing basic functionality test.
   */
  public static void main( String args[] )
  {
    JFrame  f = new JFrame("Test for SlicePlane3D_UI");
    f.setBounds( 0, 0, 200, 200 ); 

    final SlicePlane3D_UI test = new SlicePlane3D_UI( QXYZ_MODE );

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
