/*
 * File:  Vector3D_UI.java
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
 * number DMR-0218882, and by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.6  2004/02/10 05:33:04  bouzekc
 *  Now uses IsawToolkit.beep().
 *
 *  Revision 1.5  2004/02/02 23:48:56  dennis
 *  Number of decimal places displayed is no longer
 *  fixed.
 *
 *  Revision 1.4  2004/01/30 19:26:18  dennis
 *  Fix parse error when there was no trailing space after last number.
 *  Added extra spaces in output text to improve legibility.
 *
 *  Revision 1.3  2004/01/29 18:15:48  dennis
 *  Fixed javadoc error.
 *
 *  Revision 1.2  2004/01/26 20:42:09  dennis
 *  Added constructor that only requires a label.
 *  Now beeps if an invalid vector is requested.
 *
 *  Revision 1.1  2004/01/21 23:07:36  dennis
 *  User interface component for entering 3D vector.
 *
 */

package DataSetTools.components.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import DataSetTools.util.*;
import DataSetTools.math.*;
import java.io.*;

/**
 * A Vector3D_UI object is an editable JTextField object with a format that 
 * includes three numerical values, in square brackets, separated by commas. 
 * A label may also be applied, eg: Origin [ 1, 2, 3 ].  The user may edit the
 * numerical values, but if the format is changed, it will be reset.  This 
 * object also provides a method to get the currently defined Vector3D  To be
 * informed when values have been changed, programs using this object MUST
 * add an action listener that calls the methods to get the numerical values.  
 */

public class Vector3D_UI extends    JTextField 
                         implements Serializable
{
  public  static final char START     = '[';
  public  static final char SEPARATOR = ',';
  public  static final char END       = ']';

  private Vector3D value;
  private String   label;
  private String   temp_string;
 
 /* ------------------------------ CONSTRUCTOR ---------------------------- */
 /** 
  *  Construct a Vector3D_UI object with the specifed label and 
  *  vector value.  The form of the text is  <label> [ <v0>, <v1>, <v2> ].
  *
  *  @param  label  String to be used for the label for the vector.
  *  @param  value  the vector to use as the value for this object 
  *
  */
  public Vector3D_UI( String label, Vector3D value )
  { 
    setFont( FontUtil.LABEL_FONT );
    this.label = label;
    setVector( value );

    show_text();
  }

 /* ------------------------------ CONSTRUCTOR ---------------------------- */
 /**
  *  Construct a Vector3D_UI object with the specifed label and a default
  *  vector value.  The form of the text is  <label> [ <v0>, <v1>, <v2> ].
  *
  *  @param  label  String to be used for the label for the vector.
  *
  */
  public Vector3D_UI( String label )
  {
    this( label, new Vector3D( 0, 0, 0 ) );
  }


  /* ----------------------------- setLabel ---------------------------- */
  /** 
   *  Set the current label from this object.  
   *
   *  @param label  the String that will be displayed before the numbers in
   *                the text field.
   */
  public void setLabel( String label )
  { 
    this.label = label;
    show_text();
  }

  /* ------------------------------ getLabel ---------------------------- */
  /** 
   *  Get the current label from this object.  
   */
  public String getLabel()
  { 
    return label;
  }

  /* ----------------------------- setVector ---------------------------- */
  /** 
   *  Set the vector value in this object.  
   *
   *  @param v  the vector whose components will be displayed in the text field.
   */
  public void setVector( Vector3D v )
  {
    if ( v == null )
    {
      System.out.println("Error:vector value invalid in Vector3D_UI.setVector");
      v = new Vector3D( 0, 0, 0 );
    }

    this.value = new Vector3D( v );
    show_text();
  }


  /* ------------------------------ getVector ---------------------------- */
  /**
   *  Get a copy of the currently specified vector from this object.  If the 
   *  user has entered an invalid value, the text will be reset to the previous
   *  value and the previous vector value will be returned.
   */
  public Vector3D getVector()
  {
    parse_text();    
    return new Vector3D( value );
  }

 
/* -------------------------------------------------------------------------
 *
 * PRIVATE FUNCTIONS 
 *
 */

  /* ----------------------------- show_text ---------------------------- */
  /**
   *  Display the current label and vector values
   */
  private void show_text()
  {
/*
    NumberFormat f = NumberFormat.getInstance();
    f.setGroupingUsed( false );
    setText( label     + " " +
             START     + " " + f.format( value.get()[0]) + 
             SEPARATOR + " " + f.format( value.get()[1]) + 
             SEPARATOR + " " + f.format( value.get()[2]) + 
             " " + END );
*/
    setText( label     + " " +
             START     + " " + value.get()[0] + 
             SEPARATOR + " " + value.get()[1] + 
             SEPARATOR + " " + value.get()[2] + 
             " " + END );
  }

  /* ---------------------------- parse_text ---------------------------- */
  /**
   *  Parse the current string from the TextField object and set the vector 
   *  value in the object, if valid values have been entered.
   */
  private void parse_text()
  {
    boolean ok = true;

    String  str   = getText();            // start after the '[' char
    int     index = str.indexOf( START );
    if ( index < 0 )
      ok = false;
    else
    {
      str = str.substring( index+1 );

      index = str.indexOf( END );        // discard ']' and any following chars
      if ( index < 0 )
        ok = false;
      else
      {
        str = str.substring( 0, index ); // make sure there is something left
        if ( str.length() <= 0 )
          ok = false;
      }
    }

    if ( !ok )
    {
      show_text();
      IsawToolkit.beep();
      return;
    }

    String regexp_tokens = "["+SEPARATOR+"]";
    String[] result = str.split(regexp_tokens);

    Float   Float_NaN = new Float(Float.NaN);
    float   temp[]    = new float[3];
    int     i  = 0;
  
    if ( result.length >= 3 )              // could be valid entries 
      while ( i < 3 && ok )                // so try to extract them
      {
        try 
        {
          Float Float_val = new Float( result[i] );
          if ( Float_val.equals(Float_NaN) )
            ok = false;
          else
            temp[i] = Float_val.floatValue();
        }
        catch ( NumberFormatException e )
        {
          ok = false;
        }
        i++; 
      }
    else
     ok = false;
 
    if ( ok )
      value = new Vector3D( temp );
    else
      IsawToolkit.beep();

    show_text();
  }

  

/* -------------------------------------------------------------------------
 *
 * MAIN  ( Basic main program for testing purposes only. )
 *
 */
    public static void main(String[] args)
    {
      JFrame f = new JFrame("Test for JTextField");
      f.setBounds(0,0,200,150);
      final Vector3D_UI vec_ui = new Vector3D_UI("Test1", new Vector3D(1,2,3));

      f.getContentPane().setLayout( new GridLayout(2,1) );
      f.getContentPane().add(vec_ui);

      vec_ui.addActionListener( new ActionListener()
       {
         public void actionPerformed(ActionEvent e)
         {
           System.out.println("Entered: " + vec_ui.getText() );
           System.out.println("Vector = " + vec_ui.getVector() );
         }
       });

      final Vector3D_UI vec_ui_2 = new Vector3D_UI( "Test2",  null );
      f.getContentPane().add(vec_ui_2);
      vec_ui_2.setLabel( "NEW LABEL" );
      vec_ui_2.setVector( new Vector3D( 4, 5, 6 ) );

      vec_ui_2.addActionListener( new ActionListener()
       {
         public void actionPerformed(ActionEvent e)
         {
           System.out.println("Entered: " + vec_ui_2.getText() );
           System.out.println("Vector = " + vec_ui_2.getVector() );
         }
       });

      f.setVisible(true);
    }
}
