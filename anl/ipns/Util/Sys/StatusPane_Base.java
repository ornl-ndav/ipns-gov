/*  
 * File:  StatusPane_Base.java   
 *               
 * Copyright (C) 2002, Ruth Mikkelson  
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
 *           Menomonie, WI 54751, USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 *  
 * Modified:  
 *  
 * $Log$
 * Revision 1.7  2004/08/17 21:06:52  dennis
 * Now just uses the append() method to add new messages, rather than
 * appendDoc() that was a workaround for problems with earlier versions
 * of java.
 *
 * Revision 1.6  2004/03/11 22:13:14  millermi
 * - Changed package names and replaced SharedData with
 *   SharedMessages class.
 *
 * Revision 1.5  2003/12/14 19:18:09  bouzekc
 * Removed unused import statements.
 *
 * Revision 1.4  2003/06/18 20:34:46  pfpeterson
 * Changed calls for NxNodeUtils.Showw(Object) to
 * DataSetTools.util.StringUtil.toString(Object)
 *
 * Revision 1.3  2002/11/27 23:12:10  pfpeterson
 * standardized header
 *
 * Revision 1.2  2002/08/19 17:07:09  pfpeterson
 * Reformated file to make it easier to read.
 *
 * Revision 1.1  2002/06/28 13:35:30  rmikk
 * -This is the Old Status Pane which was just the Text area
 *
 * Revision 1.3  2002/01/10 15:40:58  rmikk
 * Added the feature that if this StatusPane_Base was not Displayable
 * the value would go to System.out
 * 
 * Revision 1.2  2002/01/09 19:31:00  rmikk 
 * -Added two methods add and clear to the Status Pane. 
 *   These methods allow for adding or clearing a StatusPane_Base 
 *    if there is a handle to it 
 * 
 * Revision 1.1  2001/12/21 17:44:45  dennis 
 * The CommandPane's Status Line. It is event driven 
 * (propertyChange event). 
 * The property names are "Display" and "Clear" 
 *  
*/  
package gov.anl.ipns.Util.Sys;  
  
import javax.swing.*;  
import java.beans.*; 
import javax.swing.text.Document;
import java.util.*; 
import javax.swing.border.*;  
  
/**
 * The Status Pane is a plug in module that can report messages from a
 * variety of sources in an application via the PropertyChange events
 *  
 * The supported Property names are "Display" and "Clear".  The
 * newValue of the Display event is the value that is displayed.
 * Vectors and arrays (of arrays etc.) are displayed as lists up to 99
 * elements.  The toString method is used on all other Display values
 *  
 * The Status Pane can be put in a JScrollPane.  Also the
 * Command.JPanelwithToolbar can be used to invoke a Save or a special
 * Clear on this StatusPane_Base.
 */  
public class StatusPane_Base extends JTextArea 
                                              implements PropertyChangeListener{
    Border border;  
      
    /**
     * Constructor that creates a JTextArea with rows and cols.  There
     * is no Border. The JTextArea is editable and it does not wrap
     *
     * @param rows The number of rows in the JTextArea
     * @param cols The number of columns in the JTextArea
     */  
    public StatusPane_Base( int rows, int cols){
        this( rows,cols,null, true,false);  
    }  
    
    /**
     * This constructor sets more details.  
     * @param rows The number of rows in the JTextArea
     * @param cols The number of columns in the JTextArea
     * @param border The border to be set or null if there is no
     * border
     * @param editable True if it can be edited otherwise it is false
     * @param wrap True if text wraps around
     */  
    public StatusPane_Base( int rows, int cols, Border border,
                            boolean editable, boolean wrap){
        super( rows,cols);  
        
        setEditable( editable);  
        
        if( border != null)  
            setBorder( border);  
        
        setLineWrap( wrap);  
    }  

    /**
     * Processes the properties "Display" and "Clear".  The value to
     * Display is evt.NewValue().
     *
     * @param evt Contains the Property Name and new Value to be
     * displayed
     *  
     * NOTE: Arrays and Vectors will be expanded up to 99 elements
     */  
    public void propertyChange(PropertyChangeEvent evt){
        String PropName = evt.getPropertyName();  
        Object Value = evt.getNewValue();  
        
        if( PropName.equals( "Display")){
            add( Value );  
        }else if( PropName.equals( "Clear")){
            Clearr();
        } 
    }

    /**
     * This method can be used to add information to the text area. 
     * 
     *  @param Value The value to be displayed. Arrays and Vectors
     *  will be converted to a small list and each element will be
     *  displayed the best possible
     */
    public void add( Object Value){
        String S = null;          
        
        if( Value == null)  
            S = "null";  
        else if( ! Value.getClass().isArray( ))  
            if( !(Value instanceof Vector))  
                   S = Value.toString();  
        
        if( S == null)  
            S = StringUtil.toString(Value);

        if( isDisplayable())
            append( S + "\n" );
        else
            System.out.println( S );  
    } 
  
       
    /**
     * Clears the contents of the text area 
     */ 
    public void Clearr(){
        setText(""); 
    } 

}  
