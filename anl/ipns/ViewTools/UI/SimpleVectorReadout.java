/*
 * File: SimpleVectorReadout.java
 *
 * Copyright (C) 2002, Dennis Mikkelson
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
 * number DMR-0218882, and by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.4  2004/08/10 01:40:04  dennis
 *  Added default value and button to reset the control to the
 *  default value.
 *
 *  Revision 1.3  2004/03/15 23:53:59  dennis
 *  Removed unused imports, after factoring out the View components,
 *  Math and other utils.
 *
 *  Revision 1.2  2004/03/12 00:28:38  serumb
 *  Changed package and imports.
 *
 *  Revision 1.1  2003/06/03 16:51:38  dennis
 *  Initial version of GUI for reciprocal lattice normal vectors
 *  including corresponding d-spacing and least square errors.
 *
 */

package gov.anl.ipns.ViewTools.UI;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import gov.anl.ipns.Util.Numeric.*;
import gov.anl.ipns.MathTools.Geometry.*;

/**
 *  Class to display a vector with its magnitude.  This was built to be used
 *  with the Reciprocal Lattice Plane Viewer.  
 */

public class SimpleVectorReadout extends    ActiveJPanel 
                                 implements Serializable 
{
  private String    title;
  private JLabel    vec_value;
  private JLabel    mag_value;
  private Vector3D  vector = null;
  private Vector3D  default_vector = null;
  private JButton   select_button; 
  private JButton   reset_button; 
  private String    units = "Q";             // units to display
  private JPanel    control_panel;
 
 /* ------------------------------ CONSTRUCTOR ---------------------------- */
 /** 
  *  Construct a vector readout control with the specified title.
  * 
  *  @param  title  The title to put on the border around the control.
  */
  public SimpleVectorReadout( String title )
  { 
     this.title = title;

     TitledBorder border = 
                  new TitledBorder(LineBorder.createBlackLineBorder(), title );
     border.setTitleFont( FontUtil.BORDER_FONT );
     setBorder( border );

     Box container = new Box( BoxLayout.Y_AXIS );

     JPanel panel1 = new JPanel();
     panel1.setLayout( new GridLayout(1,1) );

     control_panel = new JPanel();
     control_panel.setLayout( new GridLayout(1,2) );
     select_button = new JButton("Select");
     setBackground( Color.white );
     panel1.setBackground( Color.white );
     control_panel.setBackground( Color.white );

     vec_value = new JLabel( " " + units + " : 1.000, 2.000, -3.000" );
     mag_value = new JLabel( "|" + units + "|: 4.00" );

     vec_value.setForeground( Color.black );
     mag_value.setForeground( Color.black );
     mag_value.setBackground( Color.white );

     vec_value.setFont( FontUtil.MONO_FONT0 );
     mag_value.setFont( FontUtil.MONO_FONT0 );
     select_button.setFont( FontUtil.LABEL_FONT );

     panel1.add( vec_value );
     control_panel.add( mag_value );
     control_panel.add( select_button );

     container.add( panel1 );
     container.add( control_panel );

     setLayout( new GridLayout( 1, 1 ) );
     add( container );

     setVector( null );

     select_button.addActionListener( new ButtonListener() );
  }

 /* ------------------------------ CONSTRUCTOR ---------------------------- */
 /** 
  *  Construct a vector readout control with a "Reset" button that allows
  *  the user to reset the control to the specified default value, in 
  *  additon to a "Select" button.
  *
  *  @param  title          The title to put on the border around the control.
  *  @param  select_title   The title to put on the select button.
  *  @param  default_value  The value that the control is reset to. 
  */
  public SimpleVectorReadout( String   title, 
                              String   select_title,
                              Vector3D default_value )
  {
    this(title);
    select_button.setText( select_title );
    default_vector = new Vector3D( default_value ); 

    control_panel.setLayout( new GridLayout(1,3) );
    reset_button = new JButton("Reset");
    reset_button.setFont( FontUtil.LABEL_FONT );
    reset_button.addActionListener( new ButtonListener() );
    control_panel.add( reset_button );
  }



 /* ------------------------------ CONSTRUCTOR ---------------------------- */
 /** 
  *  Construct a vector readout control with a select button that sends
  *  an action event to any listeners.
  *
  *  @param  title          The title to put on the border around the control.
  *  @param  select_title   The title to put on the select button.
  */
  public SimpleVectorReadout( String title, String select_title )
  {
    this(title);
    select_button.setText( select_title ); 
  }


/* --------------------------- setVector ------------------------------- */
/**
 *  Set a new value for the vector.
 *
 *  @param Set a new value for the vector.  If the new value is null, and
 *         no default value has been specified, this will set the value
 *         to null.  If a default vector has been specified, then that
 *         default value will be used if the new value is null.
 */
 public void setVector( Vector3D vec )
 {
   if ( vec == null && default_vector == null )
   {
      vector = null;
      vec_value.setText(" " + units + " : undefined" );
      mag_value.setText("|" + units + "|: undefined" );
      return;
   }
   
   if ( vec == null )      
     vec = default_vector;

   vector = new Vector3D( vec );

   float coords[] = vector.get();
   String result = new String( Format.real( coords[0], 6, 3 ) );
   result += "," + Format.real( coords[1], 6, 3 );
   result += "," + Format.real( coords[2], 6, 3 );

   vec_value.setText(" " + units + " : " + result );

   result = Format.real( vec.length(), 6, 3 );
   mag_value.setText("|" + units + "|: " + result );
 }


 /* --------------------------- getVector ----------------------------- */
 /**
  *  Get the current value of the vector from this readout control.
  *
  *  @return  The current value of the vector, or null if no vector
  *           has been specified.
  */
 public Vector3D getVector()
 {
   if ( vector == null )
     return null;
   else
     return new Vector3D( vector );
 }


 /* --------------------------- getDefault ----------------------------- */
 /**
  *  Get the default value, if any, for the vector in this readout control.
  *
  *  @return  The default value of the vector, or null if no default 
  *           has been specified.
  */
 public Vector3D getDefault()
 {
   return new Vector3D( default_vector );
 }


 /* --------------------------- getTitle ----------------------------- */
 /**
  *  Get the title from this readout control
  *
  *  @return  The title String.
  */
 public String getTitle()
 {
   return title;
 }

 
/* -------------------------------------------------------------------------
 *
 *  INTERNAL CLASSES
 *
 */

/* ------------------------------ ButtonListener ------------------------- */
/*
 */

private class ButtonListener implements ActionListener
{
  public void actionPerformed( ActionEvent e )
  {
    if ( e.getSource() == reset_button )
      setVector( default_vector );

    String command = e.getActionCommand();
    send_message( command );
  }
}


/* -------------------------------------------------------------------------
 *
 * MAIN  ( Basic main program for testing purposes only. )
 *
 */
    public static void main(String[] args)
    {
      JFrame f = new JFrame("Test for SimpleVectorReadout");
      f.setBounds(0,0,200,150);
      SimpleVectorReadout control  = new SimpleVectorReadout( " a* " );

      f.getContentPane().setLayout( new GridLayout(1,1) );
      f.getContentPane().add(control);

      control.addActionListener( new ActionListener()
       {
         public void actionPerformed(ActionEvent e)
         {
           String action = e.getActionCommand();
           System.out.println("In Main, command = " + action );
         }
       });

      f.setVisible(true);
    }
}
