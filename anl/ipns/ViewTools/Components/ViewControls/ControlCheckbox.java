/*
 * File: ControlCheckbox.java
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
 *  Revision 1.4  2004/01/05 18:14:07  millermi
 *  - Replaced show()/setVisible(true) with WindowShower.
 *  - Removed excess imports.
 *
 *  Revision 1.3  2003/12/29 04:17:25  millermi
 *  - Added doClick() method that calls the JCheckbox.doClick().
 *    This will simulate a mouse click.
 *
 *  Revision 1.2  2003/10/16 05:00:12  millermi
 *  - Fixed java docs errors.
 *
 *  Revision 1.1  2003/05/24 17:37:56  dennis
 *  Initial Version of view control using a checkbox. (Mike Miller)
 *
 */
  
 package DataSetTools.components.View.ViewControls;
 
 import javax.swing.JCheckBox;
 import javax.swing.JFrame;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.awt.GridLayout;
 import java.awt.Color;
 
 import DataSetTools.util.WindowShower;

/**
 * This class is a ViewControl (ActiveJPanel) with a generic checkbox for use 
 * by ViewComponents. It includes a hook to send out messages when the  
 * checkbox has been checked or unchecked.
 */ 
public class ControlCheckbox extends ViewControl
{
  private JCheckBox cbox;
  private Color checkcolor;
  private Color uncheckcolor;
  
 /**
  * Default constructor specifies no title but initializes checkbox to be
  * unchecked.
  */ 
  public ControlCheckbox()
  {  
    super("");
    this.setLayout( new GridLayout(1,1) );
    cbox = new JCheckBox();
    this.add(cbox);
    cbox.addActionListener( new CheckboxListener() );
    checkcolor = Color.red;
    uncheckcolor = Color.black;      
  }
 
 /**
  * Same functionality as default constructor, only this constructor allows
  * for title specification of the border.
  *
  *  @param  title
  */ 
  public ControlCheckbox(String title)
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
  public ControlCheckbox(boolean isChecked)
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
      cbox.setForeground( checkcolor );
    else
      cbox.setForeground( uncheckcolor );	 
  }

 /**
  * This method sets the text of the checkbox. setText() differs from 
  * setTitle() in that the setTitle() is the border text, while the 
  * setText() is the text following the checkbox.
  *
  *  @param  text
  */
  public void setText( String text )
  {
    cbox.setText(text);
  }

 /**
  * This method gets the text of the checkbox. getText() differs from 
  * getTitle() in that the getTitle() returns the border text, while the 
  * getText() is the text following the checkbox. Both methods can be used
  * to identify the checkbox.
  */
  public String getText()
  {
    return cbox.getText();
  }
  
 /**
  * This method sets the text of the checkbox to the color specified when
  * the checkbox is checked.
  *
  *  @param  checked - color of text when checkbox is checked
  */
  public void setTextCheckedColor( Color checked )
  {
    checkcolor = checked;
    if( cbox.isSelected() )
      cbox.setForeground( checkcolor );
  }   

 /**
  * This method sets the text of the checkbox to the color specified when
  * the checkbox is unchecked.
  *
  *  @param  unchecked - color of text when unchecked
  */
  public void setTextUnCheckedColor( Color unchecked )
  {
    uncheckcolor = unchecked;
    if( !cbox.isSelected() )
      cbox.setForeground( uncheckcolor );
  }
  
 /**
  * Acts as an artifical mouse click. Extends the capability of the
  * JCheckbox.doClick().
  */
  public void doClick()
  {
    cbox.doClick();
  }    
  
 /*
  * CheckboxListener moniters the JCheckBox private data member for the
  * ControlCheckbox class
  */
  private class CheckboxListener implements ActionListener
  { 
    public void actionPerformed( ActionEvent ae )
    {
      ((ViewControl)cbox.getParent()).send_message(CHECKBOX_CHANGED);
      if( cbox.isSelected() )
        cbox.setForeground( checkcolor );
      else
        cbox.setForeground( uncheckcolor );
      //System.out.println("Currently selected? " + 
      //		 ((ControlCheckbox)cbox.getParent()).isSelected() );
    }
  } 
  
 /*
  *  For testing purposes only
  */
  public static void main(String[] args)
  {
    ControlCheckbox check = new ControlCheckbox();
    ControlCheckbox check2 = new ControlCheckbox(true);
    JFrame frame = new JFrame();
    frame.getContentPane().setLayout( new GridLayout(2,1) );
    frame.setTitle("ControlCheckbox Test");
    frame.setBounds(0,0,135,120);
    frame.getContentPane().add(check);
    frame.getContentPane().add(check2);
    check.setText("myCheckbox"); 
    check2.setText("myCheckbox2");   
    check.setTextCheckedColor( Color.orange );
    check.setTextUnCheckedColor( Color.green );
    check2.doClick();
    WindowShower shower = new WindowShower(frame);
    java.awt.EventQueue.invokeLater(shower);
  }
}
