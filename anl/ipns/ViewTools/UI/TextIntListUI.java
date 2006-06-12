/*
 * File:  TextIntListUI.java
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
 *  Revision 1.1  2006/06/12 15:24:09  dennis
 *  Compact IntList UI for use in viewers.
 *
 */

package gov.anl.ipns.ViewTools.UI;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import gov.anl.ipns.Util.Numeric.*;

/**
 * A TextIntListUI object is an editable JTextField object for entering an
 * increasing list of integer values, in the form of an IntListString.  To be
 * informed when values have been changed, programs using this object MUST
 * add an action listener that calls the methods to get the numerical values.  
 */

public class TextIntListUI extends    JTextField 
                           implements Serializable
{
  private String  label;
  private String  list_string = "";
 

 /* ------------------------------ CONSTRUCTOR ---------------------------- */
 /** 
  *  Construct a TextIntListUI object the specified list of integers.  An
  *  empty list should be passed in as an array of length 0, or a null.
  *
  *  @param  label  String to be used for the label.
  *  @param  list   array of integers in increasing order with no repeated
  *                 values. 
  *
  */
  public TextIntListUI( String label, int[] list )
  { 
    this.label = label;
    setList( list );

    setFont( FontUtil.LABEL_FONT );
    show_text();
  }


 /* ------------------------------ CONSTRUCTOR ---------------------------- */
 /** 
  *  Construct a TextIntListUI object the specified list of integers.  An
  *  empty list should be passed in as a String of length 0, or a null.
  *
  *  @param  label  String to be used for the label.
  *  @param  list   String containing an initial list of integers, in the
  *                 form of an IntListString. 
  *
  */
  public TextIntListUI( String label, String list )
  {
    this.label = label;
    setListString( list );

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


  /* --------------------------- setListString ---------------------------- */
  /** 
   *  Set the int list to be used for this object.  
   *
   *  @param  list   String containing an initial list of integers, in the
   *                 form of an IntListString. 
   *
   *  @return true, if the specified String was parsed and an int array 
   *          was found that contained at least one element.
   */
  public boolean setListString( String list )
  { 
    if ( list == null || list.length() == 0 )
    {
      list_string = "";
      show_text();
      return false;
    }
    else
    {
      int[] int_list = IntList.ToArray( list ); 
      list_string = IntList.ToString( int_list ); 
      show_text();
      if ( int_list.length > 0 )
        return true;
      return false;
    }  
  }


  /* ------------------------------ getListString ------------------------ */
  /**
   *  Get the current String form of the IntList that is the value of this
   *  object.
   *
   *  @return A String representing an IntList.
   */
  public String getListString()
  {
    parse_text();
    return list_string;
  }



  /* -------------------------------- setList ---------------------------- */
  /** 
   *  Set an increasing list of integers to be used for this object.  
   *
   *  @param  list_arr   array of integers arranged in increasing order, with
   *                     no repeated values.
   *
   *  @return true, if the array had length more than zero. 
   */
  public boolean setList( int[] list_arr )
  {
    if ( list_arr == null || list_arr.length == 0 )
    {
      list_string = "";
      show_text();
      return false;
    }
    else
    {
      list_string = IntList.ToString( list_arr );
      int[] int_list = IntList.ToArray( list_string );
      show_text();
      if ( int_list.length > 0 )
        return true;
      return false;
    }
  }


  /* -------------------------------- getList ---------------------------- */
  /**
   *  Get the current array of integers that is the value of this object.
   *
   *  @return  An array of integers in increasing order with no 
   *           repeated values..
   */
  public int[] getList()
  {
    parse_text();
    return IntList.ToArray( list_string );
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
    setText( label + " " + list_string );
  }


  /* ---------------------------- parse_text ---------------------------- */
  /**
   *  Parse the current string from the TextField object to set the list_string
   *  in the required form.
   */
  private void parse_text()
  {
    String line = getText();

    int index = 0;
    boolean digit_found = false;
    while ( index < line.length() && !digit_found )
      if ( Character.isDigit( line.charAt( index )) )
        digit_found = true;
      else
        index++;

    if ( digit_found )
      setListString( line.substring( index ) );

    show_text();
  }

  
/* -------------------------------------------------------------------------
 *
 * MAIN  ( Basic main program for testing purposes only. )
 *
 */
    public static void main(String[] args)
    {
      JFrame f = new JFrame("Test for TextIntListUI");
      f.setBounds(0,0,200,150);

      int[] list = { 17, 23, 24, 25 };
      final TextIntListUI range_ui = new TextIntListUI( "Det ID", list );

      f.getContentPane().setLayout( new GridLayout(2,1) );
      f.getContentPane().add(range_ui);

      ActionListener list_listener = new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
           TextIntListUI my_ui = (TextIntListUI)(e.getSource());

           System.out.println("Entered: " + my_ui.getText() );
           System.out.println("Min = " + my_ui.getListString() );
           int[] list = my_ui.getList();
           for ( int i = 0; i < list.length; i++ )
             System.out.println( i + "  " + list[i] );
         }
      };

      range_ui.addActionListener( list_listener );

      final TextIntListUI range_ui_2 = new TextIntListUI( "Detector", "1:11");
      f.getContentPane().add(range_ui_2);
      range_ui_2.setLabel( "NEW LABEL" );
      range_ui_2.setListString( "1:5,7:11" );

      range_ui_2.addActionListener( list_listener );

      f.setVisible(true);
    }
}
