/*
 * File: RangeControl.java
 *
 * Copyright (C) 2005 Brent Serum
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
 *  Revision 1.5  2007/08/09 18:27:54  rmikk
 *  Removed the addActionListener method. Added a local ActionListener who then
 *    passes the event to the ActiveJPanel send_message method
 *
 *  Revision 1.4  2007/03/30 19:10:07  amoe
 *  - Added RANGE_CHANGED constant.  It acts as a flag for when the
 *  ranges change.
 *  - Set the range_fields' action command to RANGE_CHANGED.
 *  - Added validateRanges().  This sends a message to the super class.
 *  - setObjectState() now calls validateRanges() at the end.
 *  - addActionListener now adds the local listener to the super class's
 *  listener list.
 *
 *  Revision 1.3  2005/05/25 20:28:43  dennis
 *  Now calls convenience method WindowShower.show() to show
 *  the window, instead of instantiating a WindowShower object
 *  and adding it to the event queue.
 *
 *  Revision 1.2  2005/03/28 06:36:57  serumb
 *  Added copy method and main test program.
 *
 *  Revision 1.1  2005/03/28 05:51:01  serumb
 *  Initial version of a class that wraps a textRangeUI into a ViewControl.
 *
 */

package gov.anl.ipns.ViewTools.Components.ViewControls;

import gov.anl.ipns.ViewTools.UI.TextRangeUI;
import gov.anl.ipns.ViewTools.Components.*;

import java.awt.event.*;
import java.io.Serializable;
import java.util.Vector;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
/**
  * This class is a ViewControl with text fields set up for entering 
  * and displaying ranges.
  */
public class RangeControl extends ViewControl implements Serializable,
                                                         IPreserveState
{

  /**
    *  "Min Range" - This constant String is a key for referencing the state
    *  information about the min range for an axis on the graph.
    */
  public static String MIN_RANGE = "Min Range";

  /**
    *  "Max Range" - This constant String is a key for referencing the state
    *  information about the min range for an axis on the graph.
    */
  public static String MAX_RANGE = "Max Range";

  /**
    *  "Range Label" - This constant String is a key for referencing the state
    *  information about the Label For the text Range.
    */
  public static String RANGE_LABEL = "Range Label";
  
   /**
    *  "Range Changed" - This constant String is used as a message for updating the
    *  graph range with action listeners.
    */
  public static String RANGE_CHANGED = "Range Changed";

  private TextRangeUI[] range_field;
  private Box vert_box = new Box(BoxLayout.Y_AXIS);

 /**
   * The Constructor initializes the values for the range control and
   * sets the heading and border.
   */
  RangeControl( String[] name)
  {
    super("Scale");
    if (name.length < 1) return; 
    
    ActionListener actlisten = new ActionListener(){
      
          public void actionPerformed(ActionEvent event)
          {
             
                send_message( event.getActionCommand());
             }
          
       };
    

    range_field = new TextRangeUI[name.length];
    for(int index = 0; index < name.length; index++)
    {
      range_field[index] = new TextRangeUI(name[index],0,1);
      range_field[index].setActionCommand(RANGE_CHANGED);
      range_field[index].addActionListener(  actlisten );
      
      vert_box.add(range_field[index]);
    }
    add(vert_box);
  }

   // setState() and getState() are required by IPreserveState interface
  /**
   * This method will set the current state variables of the object to state
   * variables wrapped in the ObjectState passed in.
   *
   *  @param  new_state
   */
   public void setObjectState( ObjectState new_state )
   {
      Object temp = new_state.get(MIN_RANGE);
      
      for( int x = 0; x < range_field.length; x++){
        String MIN_RANGEX = MIN_RANGE + x;
        temp = new_state.get(MIN_RANGEX);
        if ( temp != null)
        {
          range_field[x].setMin(
            ((Float)temp).floatValue());
        }
      }

      for( int x = 0; x < range_field.length; x++){
        String MAX_RANGEX = MAX_RANGE + x;
        temp = new_state.get(MAX_RANGEX);
        if ( temp != null)
        {
          range_field[x].setMax(
             ((Float)temp).floatValue());
        }
      }
      

      for( int x = 0; x < range_field.length; x++){
        String RANGE_LABELX = RANGE_LABEL + x;
        temp = new_state.get(RANGE_LABELX);
        if ( temp != null)
        {
          range_field[x].setLabel(
             String.valueOf(temp));
        }
      }
      validate();
      repaint();
      validate_ranges();
   }
 
  /**
   * This method will get the current values of the state variables for this
   * object. These variables will be wrapped in an ObjectState. Keys will be
   * put in alphabetic order.
   */
   public ObjectState getObjectState(boolean isDefault)
   {
     ObjectState state = new ObjectState();
     for(int x =0; x < range_field.length; x++){
       String MIN_RANGEX = MIN_RANGE + x;
       state.insert(MIN_RANGEX,
                  new Float(range_field[x].getMin()) );
     }

     for(int x =0; x < range_field.length; x++){
       String MAX_RANGEX = MAX_RANGE + x;
       state.insert(MAX_RANGEX,
                 new Float(range_field[x].getMax()) );
     }

     for(int x =0; x < range_field.length; x++){
       String RANGE_LABELX = RANGE_LABEL + x;
       state.insert(RANGE_LABELX,
                   range_field[x].getLabel() );
     }

     if(! isDefault){
     }

     return state;
    }

 /**
   * This function makes a 
   */
   public ViewControl copy()
   {
     String[] spacer = new String[range_field.length];

     RangeControl range = new RangeControl(spacer);
     range.setObjectState(this.getObjectState(PROJECT));
   
     return range;
   }
 /**
   * This function sets the min value to be displayed in the range field.
   *
   * @param index an integer value that represents the the value to be set.
   *
   * @param value a float value to be set and displayed.
   */
  public void setMin(int index, float value)
  {
    if( index >= range_field.length )
      return;
    range_field[index].setMin(value);
   // send_message(VALUE_CHANGED);
  }

 /**
   * This function sets the max value to be displayed in the range field.
   *
   * @param index an integer value that represents the the value to be set.
   *
   * @param value a float value to be set and displayed.
   */
  public void setMax(int index, float value)
  {
    if( index >= range_field.length )
      return;
    range_field[index].setMax(value);
   // send_message(VALUE_CHANGED);
  }
 
 /**
   * This Function returns the min value for the index in the range field.
   *
   * @param index an integer value that contains the location of the value.
   */ 
  public float getMin(int index)
  {
    if( index >= range_field.length )
      return Float.NaN;
    return range_field[index].getMin();
  }

 /**
   * This Function returns the max value for the index in the range field.
   *
   * @param index an integer value that contains the location of the value.
   */
  public float getMax(int index)
  {
    if( index >= range_field.length )
      return Float.NaN;
    return range_field[index].getMax();
  }
 
 /**
   * This Function sets the range control value.
   *
   * @param value the value to set the control to.
   */
   public void setControlValue(Object value)
  {
    if( value == null || !(value instanceof Vector) )
    return;
  
    for(int index = 0; index < ((Vector)value).size(); index++)
    {
      setMin( index,(float)( 
      ((float[])( ((Vector)value).elementAt(index)) ) [0] ) );
      setMax( index,(float)( 
      ((float[])( ((Vector)value).elementAt(index)) ) [1] ) );
    }
    
    //notify listeners of range updates
    //send_message(RANGE_CHANGED);
    
    return; 
  }

 /**
   * This function gets the control value;
   *
   * @return TextRangeUI[] the object containing the control values.
   */
  public Object getControlValue()
  {
    Vector values = new Vector();
    float min_max[] = new float[2];
    
    for(int index = 0; index < range_field.length; index ++)
    {
      min_max[0] = getMin(index);
      min_max[1] = getMax(index);
      values.add(min_max);
    }
    return values;
  }
  
  public void validate_ranges()
  {
	  send_message(RANGE_CHANGED);
  }

  /**
    * Function to add an action Listener to the text fields.
    */
   /*public void addActionListener(ActionListener listener)
   {
	   super.listeners.add(listener);
	   
	   for(int i=0; i < range_field.length; i++)
	   {
		   range_field[i].addActionListener(listener);
	   }
   }
   */
   /**
     * Main Program for test purposes only.
     *
     * @param args inputs are ignored.
     */
   public static void main(String[] args)
   {
     JFrame frame1 = new JFrame("test copy");
     RangeControl range = new RangeControl(new String[] {"Xmin", "Xmax",
                                                         "Ymin", "Ymax"});

     range.setMin(1 , .5f);
     range.setMin(0 , 2.6f);
     range.setMax(1 , 6f);
     range.setMax(0 , 7f);

     frame1.getContentPane().setLayout(new java.awt.GridLayout(1,2));
     frame1.getContentPane().add(range);
     frame1.getContentPane().add(range.copy());
     frame1.pack();
     frame1.setVisible(true);
   }
     
}  
