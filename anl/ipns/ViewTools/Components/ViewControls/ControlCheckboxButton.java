/*
 * File: ControlCheckboxButton.java
 *
 * Copyright (C) 2003, Mike Miller
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
 * Primary   Mike Miller <millermi@uwstout.edu>
 * Contact:  Student Developer, University of Wisconsin-Stout
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
 *  Revision 1.1  2003/08/06 13:52:26  dennis
 *  - Initial Version - Includes features of the ControlCheckbox with
 *    the addition of an "Edit" button. (Mike Miller)
 *
 */
  
 package DataSetTools.components.View.ViewControls;
 
 import javax.swing.*;
 import javax.swing.event.*;
 import java.awt.event.*;
 import java.awt.*;
 import javax.swing.border.TitledBorder;
 
 import DataSetTools.viewer.ViewerState;
 import DataSetTools.util.*;

/**
 * This class is a ViewControl (ActiveJPanel) with a generic checkbox and button
 * for use by ViewComponents. It includes a hook to send out messages when the  
 * checkbox has been checked or unchecked or the button is pressed. 
 */ 
public class ControlCheckboxButton extends ViewControl
{
   private JPanel pane;
   private JCheckBox cbox;
   private JButton edit;
   private Color checkcolor;
   private Color uncheckcolor;
   private ControlCheckboxButton this_panel;
   
  /**
   * Default constructor specifies no title but initializes checkbox to be
   * unchecked.
   */ 
   public ControlCheckboxButton()
   {  
      super("");
      pane = new JPanel( new BorderLayout() );
      cbox = new JCheckBox();
      pane.add(cbox,"West");
      cbox.addActionListener( new CheckboxListener() );
      edit = new JButton("Edit");
      pane.add(edit,"Center");
      edit.addActionListener( new ButtonListener() );
      edit.setEnabled(false);
      this.add(pane);
      checkcolor = Color.black;
      uncheckcolor = Color.black; 
      this_panel = this;    
   }
  
  /**
   * Same functionality as default constructor, only this constructor allows
   * for title specification of the border.
   *
   *  @param  title
   */ 
   public ControlCheckboxButton(String title)
   {
      this();
      this.setTitle(title);
   }

  /**
   * Same functionality as default constructor, only this constructor allows
   * for setSelected(). Use this constructor to create a checkbox that starts
   * out checked.
   *
   *  @param  isChecked
   */ 
   public ControlCheckboxButton(boolean isChecked)
   {
      this();
      this.setSelected(isChecked);
   }   
   
  /**
   * isSelected() tells when the checkbox is checked, true when checked.
   *
   *  @return true if checked
   */  
   public boolean isSelected()
   {   
      return cbox.isSelected();
   }
   
  /**
   * Set the checkbox to be checked (true) or unchecked (false).
   * The constructor initializes the checkbox to be unchecked unless instructed
   * otherwise.
   *
   *  @param  isChecked
   */
   public void setSelected(boolean isChecked)
   {
      cbox.setSelected(isChecked);
      if( cbox.isSelected() )
      {
         ((TitledBorder)this.getBorder()).setTitleColor( checkcolor );
	 edit.setEnabled(true);
      }
      else
      {
         ((TitledBorder)this.getBorder()).setTitleColor( uncheckcolor );  
	 edit.setEnabled(false);
      }    
   }

  /**
   * This method sets the text of the button. setText() differs from 
   * setTitle() in that the setTitle() is the border text, while the 
   * setText() is the text on the button.
   *
   *  @param  text
   */
   public void setText( String text )
   {
      edit.setText(text);
   }

  /**
   * This method gets the text on the button. getText() differs from 
   * getTitle() in that the getTitle() returns the border text, while the 
   * getText() is the text on the button.
   *
   *  @param  text
   */
   public String getText()
   {
      return edit.getText();
   }
   
  /**
   * This method sets the color of the titled border text of the control to the 
   * color specified when the checkbox is checked.
   *
   *  @param  color
   */
   public void setTextCheckedColor( Color checked )
   {
      checkcolor = checked;
      if( cbox.isSelected() )
         ((TitledBorder)this.getBorder()).setTitleColor( checked );
   }   

  /**
   * This method sets the color of the titled border text of the control to the 
   * color specified when the checkbox is unchecked.
   *
   *  @param  color
   */
   public void setTextUnCheckedColor( Color unchecked )
   {
      uncheckcolor = unchecked;
      if( !cbox.isSelected() )
         ((TitledBorder)this.getBorder()).setTitleColor( unchecked );
   }   
   
  /*
   * CheckboxListener moniters the JCheckBox private data member for the
   * ControlCheckboxButton class
   */
   private class CheckboxListener implements ActionListener
   { 
      public void actionPerformed( ActionEvent ae )
      {
         this_panel.send_message(CHECKBOX_CHANGED);
	 if( cbox.isSelected() )
	 {
            ((TitledBorder)this_panel.getBorder()).setTitleColor( checkcolor );
	    edit.setEnabled(true);
         }
	 else
	 {
            ((TitledBorder)this_panel.getBorder()).setTitleColor(uncheckcolor);
	    edit.setEnabled(false);
         }
	 //System.out.println("Currently selected? " + 
	 //                 ((ControlCheckbox)cbox.getParent()).isSelected() );
         this_panel.repaint();
      }
   }   
   
  /*
   * ButtonListener moniters the JButton private data member for the
   * ControlCheckboxButton class
   */
   private class ButtonListener implements ActionListener
   { 
      public void actionPerformed( ActionEvent ae )
      {
         //System.out.println("Button Pressed " + ae.getActionCommand() );
         this_panel.send_message(BUTTON_PRESSED);
      }
   } 
   
  /*
   *  For testing purposes only
   */
   public static void main(String[] args)
   {
      ControlCheckboxButton check = new ControlCheckboxButton();
      ControlCheckboxButton check2 = new ControlCheckboxButton(true);
      JFrame frame = new JFrame();
      frame.getContentPane().setLayout( new GridLayout(2,1) );
      frame.setTitle("ControlCheckbox Test");
      frame.setBounds(0,0,135,120);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.getContentPane().add(check);
      frame.getContentPane().add(check2);
      check.setText("myCheckbox");  
      check.setTitle("test1");
      check2.setTitle("TEST2");   
      check.setTextCheckedColor( Color.orange );
      check.setTextUnCheckedColor( Color.green );
      
      frame.show();
   }
}
