/*
 * File:  SaveDocToFileListener.java
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
 * Revision 1.9  2004/03/11 23:14:01  millermi
 * - Removed scriptFilter which checked for Isaw scripts.
 * - Changed SharedData to SharedMessages
 *
 * Revision 1.8  2004/03/11 22:46:18  millermi
 * - Changed package.
 *
 * Revision 1.7  2003/10/22 19:57:16  rmikk
 * Fixed javadoc error
 *
 * Revision 1.6  2002/11/27 23:12:10  pfpeterson
 * standardized header
 *
 * Revision 1.5  2002/10/23 19:58:27  pfpeterson
 * Updated the JFileChooser behaviour so starts with the specified
 * directory or file, as appropriate.
 *
 * Revision 1.4  2002/08/19 17:07:04  pfpeterson
 * Reformated file to make it easier to read.
 *
 * Revision 1.3  2002/01/25 19:41:33  pfpeterson
 * Use the script filter to show only iss files. Also remembers the last item and
 * defaults to the Script_Path directory.
 *
 * Revision 1.2  2002/01/10 15:39:45  rmikk
 * Fixed an error that caused it not to remember the last
 * directory
 *
 * Revision 1.1  2001/12/21 17:47:06  dennis
 * An ActionListener that pops up a save file dialog box and
 * saves the contents of the document to a file.
 *
*/
package gov.anl.ipns.Util.Sys;
import javax.swing.text.*;
import java.io.*;
 import java.awt.event.*;
 import java.beans.*;
 import javax.swing.*;
import java.awt.*;

/**
 * Pops up a file dialog so the filename can be selected.  Then saves
 * the contents of the PlainDocument doc to this file. Other features
 * include
 * <UL><LI>to notify listeners of a new filename 
 * <LI> also listen for a new "current filename"
 * <LI>The current filename appears in the pop up file dialog box.
 * <LI> As a PropertyChange Listener, this class only listens for the
 * property with the name "filename".  The New Value is the new
 * filename.</UL>
 */
public class SaveDocToFileListener
                              implements ActionListener, PropertyChangeListener{
    PropertyChangeListener  FilenameListener;
    String FilenamePropertyName;
    String filename;
    Document doc;
    
    /**
     * Constructor that sets the document that will be saved when
     * triggered by an event
     *
     * @param doc the file whose contents will be saved
     * @param filename sets the filename and directory in the pop up
     * file dialog box
     */
    public SaveDocToFileListener( Document doc,String filename){
        this( doc, filename, null, null);
    }
    
    /**
     * Constructor that sets the document that will be saved when
     * triggered by an event. It also
     * <ul><li> adds one PropertyChangeListener
     * <li> Allows a different property name to be sent when notifying
     * the listener about the changed filename </ul>
     *
     * @param doc the file whose contents will be saved
     * @param filename sets the filename and directory in the pop up
     * file dialog box
     * @param FilenameListener <ul>Listener for a new filename that
     * saved a doc or null if none </ul>
     * @param FilenamePropertyName <ul>The property name sent to
     * Listener when notifying about the new filename</ul>
    */
    public SaveDocToFileListener( Document doc, String filename,
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
     * Stores the doc to the file selected and notifies any listeners
     * of the filename that was used to store the file
     */
    public void actionPerformed( ActionEvent evt){
        final JFileChooser fc=new JFileChooser() ;
	if( filename != null ){
            File file=new File(filename);
            if( file.isDirectory() ){
              fc.setCurrentDirectory(new File(filename));
            }else{
              fc.setSelectedFile(new File(filename));
            }
	}
	Dimension d= new Dimension(650,300);
	fc.setPreferredSize(d);
	
	try{
	    int state = fc.showSaveDialog(null);
	    if( state==0 && fc.getSelectedFile()!=null ){
		File f = fc.getSelectedFile();
		filename=f.toString();
	    }else{
		return;
	    }
	}catch(Exception e){
	    SharedMessages.addmsg("Choose and input file");
	    return;
	}
        
	/* int state  ;
	   state = fc.showSaveDialog( null ) ;
	   if( state != JFileChooser.APPROVE_OPTION )
	   return ;
	   
	   File SelectedFile = fc.getSelectedFile() ;
	   filename  = SelectedFile.toString() ; */
        
        
        (new DocumentIO()).saveDoc( doc , filename );
        if( FilenameListener != null)
            FilenameListener.propertyChange(
                            new PropertyChangeEvent(this, FilenamePropertyName ,
                                                    filename, filename));  
    }
    
    /**
     * This method is invoked when a PropertyChange event is triggered
     * and an instance of this class was added as a listener. The
     * filename that appears when the pop up file dialog box appears
     * is set by this method if the Property Name was "filename".
     *
     * @param  evt  the Property Change event
     */
    public void propertyChange( PropertyChangeEvent evt){
        //System.out.println( "Savedoc prop chang="+evt.getPropertyName());
        if( evt.getPropertyName().equals("filename")){
            filename = (String)evt.getNewValue();
        }
    }

    /**
     * Sets the name of the file or path that was selected or to start
     * from
     * @param Filename the currently selected file
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
    
    /**
     * This method is used to add a Listener who will be notified when
     * a file is saved.
     * @param listener The Object listening for a saved filename
     */
    public void addPropertyChangeListener( PropertyChangeListener listener){
        FilenameListener = listener;
        FilenamePropertyName ="filename";
    }
}
