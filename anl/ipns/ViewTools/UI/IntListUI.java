/*
 * File: IntListUI.java
 *
 * Copyright (C) 2000 Dennis Mikkelson
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
 * $Log$
 * Revision 1.1  2003/03/06 22:04:08  dennis
 * JPanel for entering a list of integers.
 *
 * Revision 1.5  2002/11/27 23:13:34  pfpeterson
 * standardized header
 *
 */

package DataSetTools.components.ui;

import java.awt.*;
import java.awt.image.*;
import java.text.*;
import java.awt.event.*;
import javax.swing.*;
import DataSetTools.util.*;
import DataSetTools.components.ParametersGUI.*;
import java.io.*;

/**
 * An IntListUI object is an editable JTextField object with a format that 
 * represents a list of integers as a String.  A label should also be applied,
 * eg: GroupIDs  500:700.  To be informed when values have been 
 * changed, programs using this object MUST add an action listener that 
 * calls the getValue() method to get the list of integers.
 */

public class IntListUI extends    ActiveJPanel 
                       implements Serializable 
{
  private JLabel      label_widget;
  private StringEntry entry_widget;
 
 /* ------------------------------ CONSTRUCTOR ---------------------------- */
 /** 
  *  Construct an IntListUI object with the specifed label and current value.
  *  The form of the text is  <label> <value>.
  *
  *  @param  label  String to be used for the label for the value.
  *  @param  value  String giving list of integer to use as the default value. 
  *
  */
  public IntListUI( String label, String value )
  { 
    label_widget = new JLabel( label ); 
    label_widget.setForeground( Color.black );

    entry_widget = new StringEntry(100);
    entry_widget.setStringFilter( new IntArrayFilter() );
    entry_widget.setText(value);
    entry_widget.addActionListener( new EntryListener() );

    setLayout( new GridLayout( 1, 2 ) );
    add( label_widget );
    add( entry_widget );
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
    label_widget.setText( label );
  }

  /* ------------------------------ getLabel ---------------------------- */
  /** 
   *  Get the current label from this object.  
   */
  public String getLabel()
  { 
    return label_widget.getText();
  }

  /* ------------------------------ setValue ------------------------------ */
  /** 
   *  Set the value to be used for this object.  
   *
   *  @param  value  String giving list of integers to use as the default i
   *                 value. 
   */
  public void setValue( String value )
  {
    entry_widget.setText( value );
  }


  /* ------------------------------ getValue ------------------------------ */
  /**
   *  Get the currently specified value from this object. 
   */
  public String getValue()
  {
    return entry_widget.getText();
  }


  /* ------------------------------------------------------------------------
   *
   *  Listener for user changing the list of integers.  The result is just 
   *  passed out to listeners to this widget.
   *
   */
  private class EntryListener implements ActionListener
  {
    public void actionPerformed( ActionEvent e )
    {
      send_message( entry_widget.getText() );
    }
  }


/* -------------------------------------------------------------------------
 *
 * MAIN  ( Basic main program for testing purposes only. )
 *
 */
    public static void main(String[] args)
    {
      JFrame f = new JFrame("Test for IntListUI");
      f.setBounds(0,0,200,150);
      final IntListUI list_ui = new IntListUI( "Group IDs ", "1:30,40");

      f.getContentPane().setLayout( new GridLayout(1,1) );
      f.getContentPane().add(list_ui);

      list_ui.addActionListener( new ActionListener()
       {
         public void actionPerformed(ActionEvent e)
         {
           System.out.println("Entered: " + list_ui.getValue() );
           System.out.println("Value = " + list_ui.getValue() );
         }
       });

      f.setVisible(true);
    }
}
