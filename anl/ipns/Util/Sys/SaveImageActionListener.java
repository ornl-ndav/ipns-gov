
/*
 * File:  SaveImageActionListener.java 
 *             
 * Copyright (C) 2003, Ruth Mikkelson
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
 * Revision 1.4  2004/03/12 17:54:28  rmikk
 * Fixed package names.
 * Fixed JMenuBar "File" Jmenu search algorithm
 *
 * Revision 1.3  2004/03/12 17:21:13  hammonds
 * Moved from DataSetTools.viewer to gov.anl.ipns.Util.Sys
 *
 * Revision 1.2  2004/03/10 21:06:32  millermi
 * - Added JPEGFileFilter class to automatically save
 *   images as JPEGs.
 * - Added static method getActiveMenuItem() which returns
 *   a menu item with a listener that will cause the image
 *   to be saved as a JPEG.
 *
 * Revision 1.1  2003/11/25 20:08:54  rmikk
 * Initial Checkin for a class that can be added to the File
 *   menu to viewers to save the component as an image
 *
 */
package gov.anl.ipns.Util.Sys;

import gov.anl.ipns.Util.File.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.*;
import java.awt.*;
import java.io.*;

public class SaveImageActionListener implements ActionListener{

 private Component comp;
 
 public SaveImageActionListener(  Component comp)
 {  
   this.comp = comp;
 }
  
 public static void setUpMenuItem(JMenuBar jmb, Component comp )
 {
   if( jmb == null)
     return;
    
   for( int i=0; i< jmb.getMenuCount();i++){
      JMenu jm = jmb.getMenu(i);
      if( jm.getText().equals("File")){
         setUpMenuItem( jm, comp);
         return;
        }
      }
    JMenu jm = new JMenu("File");
    jmb.add(jm);
    setUpMenuItem( jm, comp);
   
 }

  /**
   * This class sets up the menubar on a component with the "print" menuitem
   */
  public static void setUpMenuItem(JMenu jm, Component comp )
    {
     JMenuItem jmi= new JMenuItem("Save Image");
     int nitems= jm.getItemCount();
     if( nitems < 0) nitems= 0;
     jm.add(jmi, nitems );
     jmi.addActionListener(new SaveImageActionListener( comp));

    }
   
  /**
   * This method will return a JMenuItem with the provided text name. The
   * menu item will have a save listener added to it. If clicked on,
   * this menu item will save as an image the component passed in.
   *
   *  @param  menu_text Display text on the JMenuItem
   *  @param  comp Component to be saved.
   *  @return menu item with listener to initiate image saving routine.
   */
   public static JMenuItem getActiveMenuItem( String menu_text, Component comp )
   {
     JMenuItem jmi = new JMenuItem(menu_text);
     jmi.addActionListener( new SaveImageActionListener(comp) );
     return jmi;
   }

   public void actionPerformed( ActionEvent evt)
   {
     JFileChooser jfc= new JFileChooser();
     jfc.setFileFilter( new JPEGFileFilter() );
     // make sure approve button was pressed.
     if( jfc.showSaveDialog(null) != JFileChooser.APPROVE_OPTION)
       return;
     File file = jfc.getSelectedFile();
     // make sure file was selected
     if( file == null)
       return;
     // add .jpg to filename if it was not already added.
     String filename = new JPEGFileFilter().appendExtension(file.toString());
     Rectangle R = comp.getBounds();
     BufferedImage bimg= new BufferedImage(R.width, R.height,
                                           BufferedImage.TYPE_INT_RGB ); 
     
     Graphics2D gr = bimg.createGraphics();
  
     comp.paint( gr);

     try{
       FileOutputStream fout = new FileOutputStream( new File(filename) );
       if( !javax.imageio.ImageIO.write( bimg, "jpg",
        			  (OutputStream)fout ) )
       {
         SharedMessages.addmsg( " no appropriate writer is found");
         return; 
       }
       fout.close();
     }
     catch( Exception ss){
       SharedMessages.addmsg( "Image Save Error:"+ss.toString());
     }
   }
 
 /*
  * File filter for .jpg files, file format for images.
  */ 
  private class JPEGFileFilter extends RobustFileFilter
  {
   /*
    *  Default constructor.  Calls the super constructor,
    *  sets the description, and sets the file extensions.
    */
    public JPEGFileFilter()
    {
      super();
      super.setDescription("JPEG (*.jpg)");
      super.addExtension(".jpg");
    } 
  }
}

