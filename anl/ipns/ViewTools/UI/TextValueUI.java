/*
 * @(#)TextValueUI.java
 *
 * Copyright (C) 2000, Dennis Mikkelson
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
 *           Menomonie, WI. 54751
 *           USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.4  2001/05/29 15:02:08  dennis
 *  Changed min_limit to allow negative values as well as positive.
 *
 *  Revision 1.3  2001/04/23 21:50:34  dennis
 *  Added copyright and GPL info at the start of the file.
 *
 *  Revision 1.2  2001/01/29 21:43:41  dennis
 *  Now uses CVS version numbers.
 *
 *  Revision 1.1  2000/07/10 22:18:34  dennis
 *  user interface component, initial version
 *
 *  Revision 1.1  2000/06/12 20:35:13  dennis
 *  Initial revision
 *
 */

package DataSetTools.components.ui;

import java.awt.*;
import java.awt.image.*;
import java.text.*;
import java.awt.event.*;
import javax.swing.*;
import DataSetTools.util.*;
import java.io.*;

/**
 * A TextValueI object is an editable JTextField object with a format that 
 * has one  numerical value, in square brackets.  A label may also be applied,
 * eg: Num Bins[ 500].  The user may edit the numerical value, but if the 
 * format is changed, it will be reset.  This object also provides a method
 * to get each the numerical value, and to specify a valid range of values
 * that can be entered.  To be informed when values have been changed, 
 * programs using this object MUST add an action listener that calls the 
 * method to get the numerical value.  
 */

public class TextValueUI extends    JTextField
                         implements Serializable 
{
  private float  min_limit = -Float.MAX_VALUE;
  private float  max_limit =  Float.MAX_VALUE;
  private float  value; 
  private String label;
 
 /* ------------------------------ CONSTRUCTOR ---------------------------- */
 /** 
  *  Construct a TextValueUI object with the specifed label and current value.
  *  The form of the text is  <label>[<value>].
  *
  *  @param  label  String to be used for the label for the value.
  *  @param  value  Value to use as the default value. 
  *
  */
  public TextValueUI( String label, float value )
  { 
    this.value = value;
    this.label = label;

    setFont( FontUtil.LABEL_FONT );
    show_text();
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

  /* ------------------------------ setValue ------------------------------ */
  /** 
   *  Set the value to be used for this object.  
   *
   *  @param value   the numeric value to be displayed in the text field.
   */
  public void setValue( float value )
  {
    this.value = value;
    show_text();
  }


  /* ------------------------------ getValue ------------------------------ */
  /**
   *  Get the currently specified value from this object.  If the user
   *  has entered an invalid value, the text will be reset to the previous
   *  value and the previous value will be returned.
   */
  public float getValue()
  {
    parse_text();    
    return value;
  }


  /* ------------------------------ setLimits ----------------------------- */
  /**
   *  Set limits on the value that can be used for this object.
   *
   *  @param min_limit   the minimum allowable value.
   *  @param max_limit   the maximum allowable value.
   */
  public void setLimits( float min_limit, float max_limit )
  {
    if ( min_limit > max_limit )
      return;

    this.min_limit = min_limit;
    this.max_limit = max_limit;

    show_text();
  }

 
/* -------------------------------------------------------------------------
 *
 * PRIVATE FUNCTIONS 
 *
 */

  /* ----------------------------- show_text ---------------------------- */
  /**
   *  Display the current label, and value
   */
  private void show_text()
  {
    validate_value();

    NumberFormat f = NumberFormat.getInstance();
    f.setGroupingUsed( false );
    setText( label+"[" + f.format(value) + "]" );    
  }


  /* ------------------------------ validate_value ------------------------- */
  /**
   *  force the current value of this object to be between the current 
   *  min_limit and max_limit values.
   */
  private void validate_value()
  {
    if ( value < min_limit )
      value = min_limit;
    if ( value > max_limit )
      value = max_limit;
  }

  /* ---------------------------- parse_text ---------------------------- */
  /**
   *  Parse the current string from the TextField object and set the value 
   *  in the object, if a valid value has been entered.
   */
  private void parse_text()
  {
    float temporary_value = value;   // used to hold the value entered, before
                                     // validating it

    String str          = getText();
    Float Float_NaN     = new Float(Float.NaN);
                                                        // get the value 
    float val           = findNumber( '[', str, ']' );
    Float Float_val     = new Float(val);

    if ( !Float_val.equals(Float_NaN) )
      temporary_value = val;

    if ( temporary_value < min_limit )          // make sure the value is valid
      value = min_limit;
    else if ( temporary_value > max_limit )
      value = max_limit;
    else      
      value = temporary_value;

    show_text();
  }

  /* ---------------------------- find_number ---------------------------- */
  /**
   *  Find a numerical value, between the specified characters in the specified
   *  string. If the specified start and end characters are not found, or if
   *  there is not a valid numerical value between them, Float.NaN is returned.
   *
   *  @ param  start_char  Character that marks the start of the numerical
   *                       string 
   *  @ param  str         The whole string that should contain a number 
   *                       between the start and end characters
   *  @ param  end_char    Character that marks the end of the numerical
   *                       string 
   */
  private float findNumber( int start_char, String str, char end_char )
  {
    int   i;
    float val = Float.NaN;

    i = str.indexOf( start_char );      // find the start_char and discard the
    if ( i < 0 )                        // preliminary part of the string
      return Float.NaN;
    str = str.substring(i+1);
    
    i = str.indexOf( end_char );        // find the end_char and discard the
    if ( i < 0 )                        // remaining part of the string
      return Float.NaN;
    str = str.substring(0,i);

    try                                 // extract the float value between
    {                                   // start_char and end_char, if possible
      val = Float.valueOf(str).floatValue();
    }
    catch (Exception e ) 
    { 
      System.out.println("Exception in TextValueUI.findNumber");
      System.out.println("Exception is: " + e );
    }

    return val; 
  }
  

/* -------------------------------------------------------------------------
 *
 * MAIN  ( Basic main program for testing purposes only. )
 *
 */
    public static void main(String[] args)
    {
      JFrame f = new JFrame("Test for TextValueUI");
      f.setBounds(0,0,200,150);
      final TextValueUI value_ui = new TextValueUI( "Num Bins", 500);

      f.getContentPane().setLayout( new GridLayout(2,1) );
      f.getContentPane().add(value_ui);

      value_ui.addActionListener( new ActionListener()
       {
         public void actionPerformed(ActionEvent e)
         {
           System.out.println("Entered: " + value_ui.getText() );
           System.out.println("Value = " + value_ui.getValue() );
         }
       });

      final TextValueUI value_ui_2 = new TextValueUI( "Num Bins", 1000);
      f.getContentPane().add(value_ui_2);
      value_ui_2.setLabel( "NEW LABEL" );
      value_ui_2.setValue( 1.0f );
      value_ui_2.setLimits( 2, 10);

      value_ui_2.addActionListener( new ActionListener()
       {
         public void actionPerformed(ActionEvent e)
         {
           System.out.println("Entered: " + value_ui_2.getText() );
           System.out.println("Min = " + value_ui_2.getValue() );
         }
       });

      f.setVisible(true);
    }
}
