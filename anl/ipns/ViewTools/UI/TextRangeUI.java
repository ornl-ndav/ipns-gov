/*
 * File:  TextRangeUI.java
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
 *  Revision 1.4  2001/06/05 20:25:08  dennis
 *  Now uses ':' as separator when specifying the range, for
 *  consistency with our  integer list <--> string  convention.
 *
 *  Revision 1.3  2001/04/23 21:50:29  dennis
 *  Added copyright and GPL info at the start of the file.
 *
 *  Revision 1.2  2001/01/29 21:43:38  dennis
 *  Now uses CVS version numbers.
 *
 *  Revision 1.1  2000/07/10 22:18:33  dennis
 *  user interface component, initial version
 *
 *  Revision 1.9  2000/06/12 20:34:52  dennis
 *  now implements Seriaizable
 *
 *  Revision 1.8  2000/05/11 16:54:45  dennis
 *  Added RCS logging
 *
 *  2000/04/28  1.01 Dennis Mikkelson
 *                   Minor refinements, use NumberFormat and setFont()
 *
 *  2000/04/28  1.02 Dennis Mikkelson
 *                   Minor refinements, added "set" routines for min, max and
 *                   label
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
 * A TextRangeUI object is an editable JTextField object with a format that 
 * includes two numerical values, in square brackets, separated by a comma. 
 * A label may also be applied, eg: TOF[100.0,4000.0].  The user may edit the
 * numerical values, but if the format is changed, it will be reset.  This 
 * object also provides methods to get each of the numerical values.  To be
 * informed when values have been changed, programs using this object MUST
 * add an action listener that calls the methods to get the numerical values.  
 */

public class TextRangeUI extends    JTextField 
                         implements Serializable
{
  public  static final char START     = '[';
  public  static final char SEPARATOR = ':';
  public  static final char END       = ']';

  private float  min;
  private float  max;
  private String label;
 
 /* ------------------------------ CONSTRUCTOR ---------------------------- */
 /** 
  *  Construct a TextRangeUI object with the specifed label and min and max
  *  values.  The form of the text is  <label>[<min>,<max>].
  *
  *  @param  label  String to be used for the label for the text range.
  *  @param  min    Value to use as the first entry in the range. 
  *  @param  max    Value to use as the second entry in the range. 
  *
  */
  public TextRangeUI( String label, float min, float max )
  { 
    this.min   = min;
    this.max   = max;
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

  /* ------------------------------ setMin ------------------------------ */
  /** 
   *  Set the value to be used for the first numeric value in this object.  
   *
   *  @param min    the first numeric value to be displayed in the text field.
   */
  public void setMin( float min )
  {
    this.min = min;
    show_text();
  }


  /* ------------------------------ getMin ------------------------------ */
  /**
   *  Get the currently specified min value from this object.  If the user
   *  has entered an invalid value, the text will be reset to the previous
   *  value and the previous value will be returned.
   */
  public float getMin()
  {
    parse_text();    
    return min;
  }

  /* ----------------------------- setMax ------------------------------- */
  /** 
   *  Set the value to be used for the second numeric value in this object.  
   *
   *  @param max    the second numeric value to be displayed in the text field.
   */
  public void setMax( float max )
  { 
    this.max = max;
    show_text();
  }

  /* ------------------------------ getMax ------------------------------ */
  /**
   *  Get the currently specified max value from this object.  If the user
   *  has entered an invalid value, the text will be reset to the previous
   *  value and the previous value will be returned.
   */
  public float getMax()
  {
    parse_text();
    return max;
  }

 
/* -------------------------------------------------------------------------
 *
 * PRIVATE FUNCTIONS 
 *
 */

  /* ----------------------------- show_text ---------------------------- */
  /**
   *  Display the current label, min and max values
   */
  private void show_text()
  {
    NumberFormat f = NumberFormat.getInstance();
    f.setGroupingUsed( false );
    setText( label + START + f.format(min) + SEPARATOR + f.format(max) + END );
  }

  /* ---------------------------- parse_text ---------------------------- */
  /**
   *  Parse the current string from the TextField object and set the min
   *  and max values in the object, if valid values have been entered.
   */
  private void parse_text()
  {
    float temporary_min = min;   // used to hold the values entered, before
    float temporary_max = max;   // validating them

    String str          = getText();
    Float Float_NaN     = new Float(Float.NaN);
                                                        // get the first number
    float val           = findNumber( START, str, SEPARATOR );
    Float Float_val     = new Float(val);
    if ( !Float_val.equals(Float_NaN) )
      temporary_min = val;
                                                       // get the second number
    val       = findNumber( SEPARATOR, str, END );
    Float_val = new Float(val);
    if ( !Float_val.equals(Float_NaN) )
      temporary_max = val;

    if ( temporary_min < temporary_max )            // make sure the values
    {                                               // define a non-degenerate
      min = temporary_min;                          // interval, before we 
      max = temporary_max;                          // accept them
    }

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
      System.out.println("Exception in TextRangeUI.findNumber");
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
      JFrame f = new JFrame("Test for JTextField");
      f.setBounds(0,0,200,150);
      final TextRangeUI range_ui = new TextRangeUI( "TOF", 0, 1000);

      f.getContentPane().setLayout( new GridLayout(2,1) );
      f.getContentPane().add(range_ui);

      range_ui.addActionListener( new ActionListener()
       {
         public void actionPerformed(ActionEvent e)
         {
           System.out.println("Entered: " + range_ui.getText() );
           System.out.println("Min = " + range_ui.getMin() );
           System.out.println("Max = " + range_ui.getMax() );
         }
       });

      final TextRangeUI range_ui_2 = new TextRangeUI( "TOF", 0, 1000);
      f.getContentPane().add(range_ui_2);
      range_ui_2.setLabel( "NEW LABEL" );
      range_ui_2.setMin( 1.0f );
      range_ui_2.setMax( 2.0f );

      range_ui_2.addActionListener( new ActionListener()
       {
         public void actionPerformed(ActionEvent e)
         {
           System.out.println("Entered: " + range_ui_2.getText() );
           System.out.println("Min = " + range_ui_2.getMin() );
           System.out.println("Max = " + range_ui_2.getMax() );
         }
       });

      f.setVisible(true);
    }
}
