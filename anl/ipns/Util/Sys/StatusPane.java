/*
 * File:  StatusPane.java 
 *             
 * Copyright (C) 2001, Ruth Mikkelson
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
 * Contact : Ruth Mikkelson <mikkelsonr@uwstout.edu>
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
 * $Log$
 * Revision 1.1  2001/12/21 17:44:45  dennis
 * The CommandPane's Status Line. It is event driven
 * (propertyChange event).
 * The property names are "Display" and "Clear"
 *
*/
package Command;

import javax.swing.*;
import java.beans.*;

import javax.swing.text.*;
import java.util.*;
import IsawGUI.*;
import java.lang.*;
import NexIO.*;
import javax.swing.border.*;

/** The Status Pane is a plug in module that can report messages from
* a variety of sources in an application via the PropertyChange events 
*
*
* The supported Property names are "Display" and "Clear".  The newValue 
*  of the Display event is the value that is displayed.  Vectors and
*  arrays (of arrays etc.) are displayed as lists up to 99 elements.  
*  The toString method is used on all other Display values
*
* The Status Pane can be put in a JScrollPane.  Also the Command.JPanelwithToolbar
*  can be used to invoke a Save or a special Clear on this StatusPane.
*/

public class StatusPane extends JTextArea implements
                              PropertyChangeListener
  {
    Border border;
    
    /** Constructor that creates a JTextArea with rows and cols.  There is no 
    * Border. The JTextArea is editable and it does not wrap
    *@param  rows    The number of rows in the JTextArea
    *@param  cols    The number of columns in the JTextArea
    */
    public StatusPane( int rows, int cols) 
         { this( rows,cols,null, true,false);
            }
    
    /** This constructor sets more details.
    *@param  rows    The number of rows in the JTextArea
    *@param  cols    The number of columns in the JTextArea
    *@param border   The border to be set or null if there is no border
    *@param editable  True if it can be edited otherwise it is false
    *@param wrap       True if text wraps around
    */
    public StatusPane( int rows, int cols, Border border, boolean editable,
                        boolean wrap)
          {super( rows,cols);
           
           setEditable( editable);

           if( border != null)
               setBorder( border);

            setLineWrap( wrap);
        
           
          }
  /** Processes the properties "Display" and "Clear".  The value to Display
  *  is  evt.NewValue().
  *@param  evt  Contains the Property Name and new Value to be displayed
  *
  *NOTE: Arrays and Vectors will be expanded up to 99 elements 
  */
  public void propertyChange(PropertyChangeEvent evt)
    { 
      String PropName = evt.getPropertyName();
      Object Value = evt.getNewValue();

      if( PropName.equals( "Display"))
         {   
             String S = null;
             if( Value == null)

                S = "null";

             else if( ! Value.getClass().isArray( ))

                if( !(Value instanceof Vector))
                   S = Value.toString();

             if( S == null)

                S = (new NxNodeUtils()).Showw( Value);
                

             new Util().appendDoc(getDocument(), S ) ; 
          }

      else if( PropName.equals( "Clear"))
          { 
             setText("");
           }



     }





   }
