/*
 * File:  HKLSliceSelector.java
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
 * Revision 1.8  2004/05/03 17:55:01  dennis
 * Now uses labels "Constant Qx", etc. for combo box when specifying
 * plane in (Qx,Qy,Qz) rather the hkl space.
 *
 * Revision 1.7  2004/03/12 01:01:01  serumb
 * Changed package and imports.
 *
 * Revision 1.6  2004/03/04 20:57:39  dennis
 * Removed debug prints.
 *
 * Revision 1.5  2004/03/04 20:42:07  dennis
 * Now switches between two combo boxes with constant
 * HKL or QXYZ options.  This fixes a bug that occured
 * when removing and adding strings to one combo box.
 * (Swing threading problem... code to add new options
 *  did not finish before other code selected options.)
 * Added setSelected() method to choose which combo box
 * entry is selected.
 * Changed setMode() to select between combo boxes.
 * Now uses ISlicePlaneSelector.HKL_MODE and
 * ISlicePlaneSelector.QXYZL_MODE to specify mode
 * rather than locally defined constants.
 *
 * Revision 1.4  2004/02/02 23:50:46  dennis
 * Added option to select constant Qxyz planes instead
 * of constant HKL planes.
 *
 * Revision 1.3  2004/01/26 20:50:49  dennis
 * Added constructor that takes a specified initial plane.
 * Simplified default constructor.
 *
 * Revision 1.2  2004/01/26 18:16:49  dennis
 * Removed local copy of slice plane.  Now constructs the
 * slice plane from the values specified in the GUI
 * components, when the slice plane is requested.
 *
 * Revision 1.1  2004/01/24 23:32:59  dennis
 * Panel that lets the user specify a plane in HKL as a
 * by choosing one coordinate axis as the plane normal
 * and by specifying the origin in HKL.
 *
 */

package gov.anl.ipns.ViewTools.UI;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import gov.anl.ipns.MathTools.Geometry.*;
//import gov.anl.ipns.Util*;


/**
 *  This class selects a plane in HKL space or Qxyz space by specifying 
 *  which of the axis directions, 1, 2, or 3 should be held constant, 
 *  and by specifying an "origin" in the plane.
 */

public class HKL_SliceSelector extends     ActiveJPanel
                               implements  ISlicePlaneSelector,
                                           Serializable
{
  public static final int COMPONENT_1_CONSTANT = 0;
  public static final int COMPONENT_2_CONSTANT = 1;
  public static final int COMPONENT_3_CONSTANT = 2;

  private static final String H_STRING = "Constant H";
  private static final String K_STRING = "Constant K";
  private static final String L_STRING = "Constant L";

  private static final String QX_STRING = "Constant Qx";
  private static final String QY_STRING = "Constant Qy";
  private static final String QZ_STRING = "Constant Qz";

  private String hkl_strings[] = { H_STRING, K_STRING, L_STRING };
  private String qxyz_strings[] = { QX_STRING, QY_STRING, QZ_STRING };

  private Vector3D_UI  origin_selector;
  private JComboBox    hkl_selector;
  private JComboBox    qxyz_selector;
  int selected_index = -1;


  /* ------------------------- default constructor ---------------------- */
  /**
   *  Construct the HKL_SliceSelector with a default current slice plane 
   */
  public HKL_SliceSelector()
  {
    setLayout( new GridLayout( 2, 1 ) );

    origin_selector = new Vector3D_UI( "Center " );

    hkl_selector = new JComboBox( hkl_strings );
    hkl_selector.setFont( FontUtil.LABEL_FONT );

    qxyz_selector = new JComboBox( qxyz_strings );
    qxyz_selector.setFont( FontUtil.LABEL_FONT );

    add( origin_selector );
    add( hkl_selector ); 

    origin_selector.addActionListener( new CenterListener() );
    hkl_selector.addActionListener( new NormalVectorListener() );
    qxyz_selector.addActionListener( new NormalVectorListener() );

    setMode( ISlicePlaneSelector.HKL_MODE );

    setPlane( new SlicePlane3D() );
  }


  /* ---------------- constructor with specified plane ------------------- */
  /**
   *  Construct the CenterNormalPointSliceSelector with the specified
   *  slice plane.
   *
   *  @param plane  The plane to initially display in this selector.
   */
  public HKL_SliceSelector( SlicePlane3D plane )
  {
    this();
    setPlane( plane );
  }


  /* --------------------------- setPlane ----------------------------- */
  /**
   *  Set the plane whose parameters are to be displayed by this plane selector.
   *  Since we cannot guarantee that the specified plane has a normal vector
   *  in the direction of one of the coordinate axes, we will force the plane
   *  normal to line up with the coordinate axis that is most nearly in
   *  the direction of the normal.  
   *  
   *  @param new_plane  The plane to display in this selector.
   */
  public void setPlane( SlicePlane3D new_plane )
  {
    if ( new_plane == null )
    {
      System.out.println("ERROR: new_plane is null in " +
                                "HKL_SliceSelector.setPlane");
      return;
    }
    //
    // Now, set our plane to be as close to the same as the new_plane 
    // as possible.  First set and display the new origin value.
    //
    origin_selector.setVector( new_plane.getOrigin());  
    Vector3D n = new_plane.getNormal();    
    float coords[] = n.get();       

    //
    // Next, the normal is changed to the direction of a coordinate axis.  
    //
    float max_abs = Math.abs( coords[0] );
    int   max_i   = 0; 
    for ( int i = 1; i < 3; i++ ) 
      if ( Math.abs( coords[i] ) > max_abs )
      {
        max_abs =  Math.abs( coords[i] );
        max_i   = i;  
      }

    setSelected( max_i );
  }


  /* --------------------------- getPlane ----------------------------- */
  /**
   *  Get a copy of the plane currently specified by this plane selector.
   *
   *  @return a copy of the specified plane.
   */
  public SlicePlane3D getPlane()
  {
    SlicePlane3D plane = new SlicePlane3D();
    plane.setOrigin( origin_selector.getVector() ); 

    JComboBox selector = (JComboBox)getComponent(1);
    selected_index = selector.getSelectedIndex();
    if ( selected_index == COMPONENT_1_CONSTANT )
      plane.setU_and_V( new Vector3D( 0, 1, 0 ), new Vector3D( 0, 0, 1 ) );

    else if ( selected_index == COMPONENT_2_CONSTANT )
      plane.setU_and_V( new Vector3D( 0, 0, 1 ), new Vector3D( 1, 0, 0 ) );

    else if ( selected_index == COMPONENT_3_CONSTANT )
      plane.setU_and_V( new Vector3D( 1, 0, 0 ), new Vector3D( 0, 1, 0 ) );

    return plane;
  }


 /* --------------------------- setMode ---------------------------- */
 /**
  *  Set the labels on the combo box to either indicate choice of a 
  *  normal vector in the direction of an hkl axis or in the direction
  *  of a Qxyz axis.
  *
  *  @param  mode  One of the constants ISlicePlaneSelector.HKL_MODE 
  *                ISlicePlaneSelector.QXYZL_MODE. 
  *                Other values will be ignored.
  */
  public void setMode( int mode )
  {  
    remove(1);
    if ( mode == ISlicePlaneSelector.HKL_MODE )
      add( hkl_selector );
    else
      add( qxyz_selector );
  }

  /* --------------------------- setSelected ----------------------------- */
  /**
   *  Set the selection in the combo box of this selector to be one of 
   *  COMPONENT_1_CONSTANT, COMPONENT_2_CONSTANT or COMPONENT_3_CONSTANT.
   *
   *  @param  index  The integer code for the selecdtion, as defined by the 
   *                 public static integer component values for this class.
   */
  public void setSelected( int index )
  { 
    if ( index >= COMPONENT_1_CONSTANT &&  
         index <= COMPONENT_3_CONSTANT  ) 
    {
      this.selected_index = index;                   // record valid index 

      JComboBox selector = (JComboBox)getComponent(1);
      if ( index != selector.getSelectedIndex() )    // update display if 
      {                                              // needed
        hkl_selector.setSelectedIndex( index );
        qxyz_selector.setSelectedIndex( index );
      }
    }
  }
  
  /* -----------------------------------------------------------------------
   *
   * Private Classes
   *
   */

  /* ----------------------- CenterListener ------------------------------- */
  /*
   *  Listener for the origin vector GUI
   */ 
  private class CenterListener implements ActionListener,
                                          Serializable
  {
     public void actionPerformed( ActionEvent e )
     {
       send_message( PLANE_CHANGED );
     }
  }

  /* ----------------------- NormalVectorListener ----------------------- */
  /*
   *  Listener for the combo box that chooses which coordinate axis is
   *  the normal vector
   */ 
  private class NormalVectorListener implements ActionListener,
                                                Serializable
  {
     public void actionPerformed( ActionEvent e )
     {
       JComboBox selector = (JComboBox)e.getSource();
       int new_index = selector.getSelectedIndex();
       if ( new_index != selected_index )
       {
         selected_index = new_index;         // record choice and set both
                                             // combo boxes to be the same
         hkl_selector.setSelectedIndex( new_index );
         qxyz_selector.setSelectedIndex( new_index );

         send_message( PLANE_CHANGED );
       }
     }
  }

  /* ---------------------------- main ----------------------------- */
  /** 
   *  main program for testing purposes.
   */ 
   public static void main( String[] args )
   {
      JFrame f = new JFrame("Test for HKL_SliceSelector");

      f.setBounds(0,0,200,150);

      final HKL_SliceSelector selector = 
                               new HKL_SliceSelector(new SlicePlane3D());
      selector.setMode( ISlicePlaneSelector.HKL_MODE );
      // selector.setMode( ISlicePlaneSelector.QXYZ_MODE );

      f.getContentPane().setLayout( new GridLayout(1,1) );
      f.getContentPane().add( selector );

      selector.addActionListener( new ActionListener()
       {
         public void actionPerformed(ActionEvent e)
         {
           System.out.println("Entered: " + selector.getPlane() );
         }
       });

      f.setVisible(true);
    }

}
