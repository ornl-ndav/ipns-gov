/*
 * File: VectorReadout.java
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
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.6  2004/03/19 17:24:28  dennis
 *  Removed unused variables
 *
 *  Revision 1.5  2004/03/15 23:54:00  dennis
 *  Removed unused imports, after factoring out the View components,
 *  Math and other utils.
 *
 *  Revision 1.4  2004/03/12 00:47:20  serumb
 *  Changed package and imports.
 *
 *  Revision 1.3  2003/02/12 22:30:56  pfpeterson
 *  Changed deprecated method call to suggested one.
 *
 *  Revision 1.2  2002/11/27 23:13:34  pfpeterson
 *  standardized header
 *
 *  Revision 1.1  2002/10/31 23:16:41  dennis
 *  Component to display Q-vector, |Q|, d, and allow scaling.
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
 */

public class VectorReadout extends    ActiveJPanel 
                           implements Serializable 
{
  private String    title;
  private JLabel    vec_value;
  private JLabel    mag_value;
  private JLabel    d_value;
  private Vector3D  vector = null;
  private JButton   select_button; 
  private Boolean   Scale_StepMode;
  private boolean   dSpace_mode;
  /* ------------------------------ CONSTRUCTOR ---------------------------- */
  /** 
   */
   public VectorReadout( String title )
   { 
      this( title, true);
   }
 /* ------------------------------ CONSTRUCTOR ---------------------------- */
 /** 
  */
  public VectorReadout( String title, boolean Scale_StepMode )
  { 
     this.title = title;
     this.Scale_StepMode =Scale_StepMode;
     TextValueUI      scale_value;    
     dSpace_mode = true;
     TitledBorder border = 
                  new TitledBorder(LineBorder.createBlackLineBorder(), title );
     border.setTitleFont( FontUtil.BORDER_FONT );
     setBorder( border );

     Box container = new Box( BoxLayout.Y_AXIS );

     JPanel panel1 = new JPanel();
     JPanel panel2 = new JPanel();
     JPanel panel3 = new JPanel();
     panel1.setLayout( new GridLayout(1,1) );
     panel2.setLayout( new GridLayout(1,1) );
     panel3.setLayout( new GridLayout(1,1) );

     JPanel control_panel = new JPanel();
     control_panel.setLayout( new GridLayout(1,2) );
     select_button = new JButton("Select");
     if( Scale_StepMode)
        scale_value = new TextValueUI( "Scale", 1 );
     else
        scale_value = new TextValueUI( "# Steps", 1 );
     control_panel.add( select_button );
     control_panel.add( scale_value );
     setBackground( Color.white );
     panel1.setBackground( Color.white );
     panel2.setBackground( Color.white );
     panel3.setBackground( Color.white );

     vec_value = new JLabel( " Q : 1.000, 2.000, -3.000" );
     mag_value = new JLabel( "|Q|: 4.00" );
     d_value   = new JLabel( " d : 0.234" );
     scale_value.setHorizontalAlignment( JTextField.CENTER );

     vec_value.setForeground( Color.black );
     mag_value.setForeground( Color.black );
     d_value.setForeground( Color.black );

     vec_value.setFont( FontUtil.MONO_FONT0 );
     mag_value.setFont( FontUtil.MONO_FONT0 );
     d_value.setFont( FontUtil.MONO_FONT0 );
     select_button.setFont( FontUtil.LABEL_FONT );

     panel1.add( vec_value );
     panel2.add( mag_value );
     panel3.add( d_value );

     container.add( panel1 );
     container.add( panel2 );
     container.add( panel3 );
     container.add( control_panel );

     setLayout( new GridLayout( 1, 1 ) );
     add( container );

     setVector( null );

     select_button.addActionListener( new ButtonListener() );
     scale_value.addActionListener( new ValueListener() );
  }


 /* ------------------------------ CONSTRUCTOR ---------------------------- */
 /**
  */
  public VectorReadout( String title, String select_title, boolean Scale_StepMode  )
  {
    this(title, Scale_StepMode );
    select_button.setText( select_title ); 
  }

  /* ------------------------------ CONSTRUCTOR ---------------------------- */
  /**
   */
   public VectorReadout( String title, String select_title  )
   {
     this(title, select_title, true );
   }
   
   /**
    * Sets whether d is 2Pi/|Q|(true) or 1/|Q| (false)
    * @param dSpace_mode  true if |Q| is 2Pi/d otherwise false
    */
   public void set_dSpace_mode( boolean dSpace_mode){
      this.dSpace_mode = dSpace_mode;
   }
 public void setVector( Vector3D vec )
 {
   
   if ( vec == null )
   {
      vector = null;
      vec_value.setText(" Q : undefined" );
      mag_value.setText("|Q|: undefined" );
      d_value.setText  (" d : undefined" );
   }
   else
   {
      vector = new Vector3D( vec );

      float coords[] = vector.get();
      String result = new String( Format.real( coords[0], 6, 3 ) );
      result += "," + Format.real( coords[1], 6, 3 );
      result += "," + Format.real( coords[2], 6, 3 );

      vec_value.setText(" Q : " + result );

      result = Format.real( vec.length(), 6, 3 );
      mag_value.setText("|Q|: " + result );

      if ( vec.length() == 0 )
        d_value.setText  (" d : undefined" );
      else
      {
        double mult = 1;
        if( dSpace_mode)
           mult = 2*Math.PI;
        result = Format.real( mult/vec.length(), 6, 3 );
        d_value.setText  (" d : " + result );
      }
   }

 }


 public Vector3D getVector()
 {
   if ( vector == null )
     return null;
   else
     return new Vector3D( vector );
 }


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
    String command = e.getActionCommand();
    send_message( command );
  }
}

/* ------------------------------ ValueListener ------------------------- */
/*
 *  Process events from the textual display of the scale value.
 *
 */

private class ValueListener implements ActionListener
{
  public void actionPerformed( ActionEvent e )
  {
    TextValueUI scale_value = (TextValueUI)(e.getSource());
    float value = scale_value.getValue();
    scale_value.setValue( 1f );
    send_message( ""+value ); 
  }
}


/* -------------------------------------------------------------------------
 *
 * MAIN  ( Basic main program for testing purposes only. )
 *
 */
    public static void main(String[] args)
    {
      JFrame f = new JFrame("Test for VectorReadout");
      f.setBounds(0,0,200,150);
      VectorReadout control  = new VectorReadout( " a* " );

      f.getContentPane().setLayout( new GridLayout(1,1) );
      f.getContentPane().add(control);

      float values[] = new float[20];
      for ( int i = 0; i < values.length; i++ )
        values[i] = i*i;

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
