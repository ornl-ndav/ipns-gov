/*  
 * File:  StatusPane.java   
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
 * Revision 1.11  2004/08/17 21:05:20  rmikk
 * Eliminated the UpdateScroll method call.  The new version of Java seems to
 * do this automatically.  Causes text to be written in random places(?)
 *
 * Revision 1.10  2004/03/11 22:46:18  millermi
 * - Changed package.
 *
 * Revision 1.9  2003/12/14 19:18:09  bouzekc
 * Removed unused import statements.
 *
 * Revision 1.8  2003/07/09 14:58:26  rmikk
 * The Scroll pane is now positioned to the last line of text that
 * was entered and also works with threads without locking
 * up the system
 *
 * Revision 1.7  2003/07/08 22:05:18  rmikk
 * Status Pane now moves to where last characters
 *    are printed
 *
 * Revision 1.6  2002/11/27 23:12:10  pfpeterson
 * standardized header
 *
 * Revision 1.5  2002/08/19 17:07:08  pfpeterson
 * Reformated file to make it easier to read.
 *
 * Revision 1.4  2002/06/28 13:34:41  rmikk
 * -Completely changed. The old file is in StatusPane_Base.
 * - This class is just the previous StatusPane with the scroll bars and the save and clear buttons.
 *    a) The text area is editable and NOT wrappable
 *
 * Revision 1.3  2002/01/10 15:40:58  rmikk
 * Added the feature that if this StatusPane was not Displayable
 * the value would go to System.out
 * 
 * Revision 1.2  2002/01/09 19:31:00  rmikk 
 * -Added two methods add and clear to the Status Pane. 
 *   These methods allow for adding or clearing a StatusPane 
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
import java.awt.*;  
import javax.swing.text.*;  
import javax.swing.border.*;  
import javax.swing.event.*;
  
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
  
public class StatusPane extends JPanel implements PropertyChangeListener{
    StatusPane_Base spb = null;
    JScrollPane  X;
    public StatusPane( int rows, int cols){
        super();
        spb= new StatusPane_Base( rows,cols,
                                  new TitledBorder( "Status" ),true, false);
        X = new JScrollPane( spb);
        //spb.getDocument().addDocumentListener( new DocListener( X , spb) );
        setLayout( new BorderLayout());
        add( X, BorderLayout.CENTER);
        
        Box JP=new Box( BoxLayout.Y_AXIS);
        JButton Save = new JButton("Save");
        Save.addActionListener( new SaveDocToFileListener(spb.getDocument(),
                                                          null));
        JP.add( Save);
        
        JButton Clear = new JButton("Clear");
        Clear.addActionListener( new ClearDocListener(spb.getDocument()));
        JP.add( Clear); 
        
        JP.add( Box.createVerticalGlue());   
        
        add( JP, BorderLayout.EAST);
        
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
        spb.propertyChange( evt);
    }  

    /**
     * This method can be used to add information to the text area.
     * 
     * @param Value The value to be displayed. Arrays and Vectors will
     * be converted to a small list and each element will be displayed
     * the best possible
     */
    public void add( Object Value){
        spb.add( Value);
        //UpdateScroll();
    } 
    
    public Document getDocument(){
        return spb.getDocument();
    }
    
    /**
     * Clears the contents of the text area 
     */ 
    public void Clearr(){
        spb.Clearr(); 
    }

   public void UpdateScroll(){
          try{
             
             if( spb == null)
               return;
             if( spb.getRows() < 2)
                return;
             int line;// = spb.getRows();
             Document D = spb.getDocument();
             int length =D.getLength();
             line = spb.getLineOfOffset(length-1);

             int RowHeight = spb.getBounds().height/spb.getRows();
                  
             X.getViewport().setViewPosition( new Point( 0, (line -1)*RowHeight) );
             }
           catch( Exception ss){
             System.out.println("StatusPane listen error ="+ss);
           }
   }

    
} 

class DocListener implements DocumentListener{


       JScrollPane Scroll;
       JTextArea jtext; 
       public DocListener( JScrollPane X, JTextArea jtext){
          Scroll = X;
          this.jtext = jtext;
       }

       public void insertUpdate(DocumentEvent e){
          try{
             
             if( jtext == null)
               return;
             if( jtext.getRows() < 2)
                return;

             int offset = e.getOffset();
             int line= jtext.getLineOfOffset( offset);
             if( line < 2)
                return;
 
             int RowHeight = jtext.getBounds().height/jtext.getRows();
                  
             Scroll.getViewport().setViewPosition( new Point( 0, (line -1)*RowHeight) );
             }
           catch( Exception ss){
             System.out.println("StatusPane listen error ="+ss);
           }
       }

       public void removeUpdate(DocumentEvent e){
       }

       public void changedUpdate(DocumentEvent e){
       }

    } 
