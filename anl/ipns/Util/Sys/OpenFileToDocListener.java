/*  
 * File:  OpenFileToDocListener.java   
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
 * Revision 1.8  2004/03/11 22:46:18  millermi
 * - Changed package.
 *
 * Revision 1.7  2003/10/20 16:30:00  rmikk
 * Fixed javadoc errors
 *
 * Revision 1.6  2002/11/27 23:12:10  pfpeterson
 * standardized header
 *
 * Revision 1.5  2002/08/19 17:07:02  pfpeterson
 * Reformated file to make it easier to read.
 *
 * Revision 1.4  2002/06/28 13:31:50  rmikk
 * Eliminated commented out code
 *
 * Revision 1.3  2002/01/25 19:41:26  pfpeterson
 * Use the script filter to show only iss files. Also remembers the last item and
 * defaults to the Script_Path directory.
 *
 * Revision 1.2  2002/01/10 15:39:18  rmikk
 * Fixed an error the caused a failure to remember the last
 * directory
 * 
 * Revision 1.1  2001/12/21 17:46:15  dennis 
 * An ActionListener that pops up an open file dialog box and 
 * loads a Document with the contents of the file. 
 *  
*/  
package gov.anl.ipns.Util.Sys;  
import javax.swing.text.*;  
import java.io.*;  
import java.awt.event.*;  
import java.beans.*;  
import IsawGUI.*;  
import javax.swing.*;  
import java.awt.*;  
  
/**
 * Pops up a file dialog so the filename can be selected.  Then saves
 * the contents of the PlainDocument doc to this file. There are
 * features to notify listeners of a new filename and to also listen
 * for a new "current filename".  The current filename appears in the
 * pop up file dialog box.  As a PropertyChange Listener, this class
 * only listens for the property with the name "filename".  The New
 * Value is the new filename.
 */  
public class OpenFileToDocListener 
                              implements ActionListener, PropertyChangeListener{
    PropertyChangeListener  FilenameListener;  
    String FilenamePropertyName;  
    String filename;  
    Document doc;  
    
    /**
     * Constructor that sets the document that will receive the
     * contents of a file when triggered.
     *   
     * @param doc the file whose contents are to be saved
     * @param filename <ul>the filename and directory that initially
     * shows in the pop up file dialog box</ul>
     */  
    public OpenFileToDocListener( Document doc,String filename){
        this( doc, filename, null, null);  
    }  
  
    /**
     * Constructor that sets the document that will receive the
     * contents of a file when triggered. This form also supports
     * other Objects that need to know the last used filename
     *
     * @param doc the file whose contents are to be saved
     * @param filename <ul>the filename and directory that initially
     * shows in the pop up file dialog box</ul>
     * @param FilenameListener <ul> The Object that wants to be
     * notified when a new filename has been used <ul>
     * @param FilenamePropertyName <UL> The property name sent to the
     * listener to notify the listener that a new file was used </ul>
     */    
    public OpenFileToDocListener( Document doc, String filename,   
                                  PropertyChangeListener FilenameListener,  
                                  String FilenamePropertyName ){
        this.doc = doc;  
        this. FilenameListener= FilenameListener;  
        
        this.FilenamePropertyName = FilenamePropertyName;  
        this.filename = filename;  
        if( FilenameListener != null)  
            if( FilenamePropertyName == null)  
                FilenamePropertyName = "filename";  
    }  
    
    /**
     * Saves the contents of the file selected to the document and
     * notifies any the listeners of filename used
     */  
    public void actionPerformed( ActionEvent evt){
        final JFileChooser fc=new JFileChooser() ;  
        if( filename != null)   
            fc.setCurrentDirectory(new File(filename));
        Dimension d= new Dimension(650,300);
        fc.setPreferredSize(d);
        
        fc.setFileFilter(new scriptFilter());
        
        try{
            int state = fc.showOpenDialog(null);
            if( state==0 && fc.getSelectedFile()!=null ){
                File f = fc.getSelectedFile();
                filename=f.toString();
            }else{
                return;
            }
        }catch(Exception e){
            DataSetTools.util.SharedData.addmsg("Choose and input file");
            return;
        }
	    
        try{  
            doc.remove( doc.getStartPosition().getOffset(), doc.getLength());  
            Document D = (new Util()).openDoc( filename);  
            doc.insertString( 0, D.getText( D.getStartPosition().getOffset(),  
                                            D.getLength()), null);    
        }catch( Exception s){
            // let it drop on the floor
        }
        if( FilenameListener != null)  
            FilenameListener.propertyChange(new 
               PropertyChangeEvent(this, FilenamePropertyName, 
                                   filename, filename));
        //System.out.println("OpenFile"+FilenamePropertyName);   
    }  
  
    /**
     * This method is triggered when another Object fires a property
     * change event when an instance of this class has been added as a
     * listener.
     *  
     * The only property name listened to is "filename".  The filename
     * will be changed.
     */  
    public void propertyChange( PropertyChangeEvent evt){
        if( evt.getPropertyName().equals("filename"))  
            filename = (String)evt.getNewValue();  
    }  
  
    /**
     * Allows for changing( there is only one) a listener for the
     * notification of a new filename.
     */  
    public void addPropertyChangeListener( PropertyChangeListener listener){
        FilenameListener = listener;  
        FilenamePropertyName="filename";  
    }  
  
    /**
     * Sets the name of the file or path that was selected or to start
     * from
     * @param   Filename the currently selected file  
     */  
    public void setFileName( String Filename){
        filename= Filename;  
    }  
    
    /**
     * Returns the last selected filename.    
     */  
    public String getFileName(){
        return filename;  
    }  
  
    public static void main( String args[]){
        JFrame F = new JFrame("open test");  
        F.setSize(400,400);  
        F.getContentPane().setLayout( new BorderLayout());  
        JTextArea SS= new JTextArea( 10, 20);  
        F.getContentPane().add( SS, BorderLayout.CENTER);  
        JButton B = new JButton( "Open");  
        B.addActionListener( new OpenFileToDocListener(SS.getDocument(),null));
        F.getContentPane().add( B, BorderLayout.NORTH);  
        F.show(); F.validate();  
    }  
}  

