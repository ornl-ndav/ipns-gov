
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
 * Revision 1.1  2003/11/25 20:08:54  rmikk
 * Initial Checkin for a class that can be added to the File
 *   menu to viewers to save the component as an image
 *
 */
package DataSetTools.viewer;

import java.awt.event.*;
import javax.swing.*;
import java.awt.image.*;
import java.awt.*;
import DataSetTools.util.*;
import java.io.*;
public class SaveImageActionListener implements ActionListener{

 JMenu jm;
 Component comp;

 public SaveImageActionListener(  Component comp)
    {  
     this.comp= comp;
    }
  

  public static void setUpMenuItem(JMenuBar jmb, Component comp )
    {
     setUpMenuItem(jmb.getMenu( DataSetTools.viewer.DataSetViewer.FILE_MENU_ID ),comp);
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

   public void actionPerformed( ActionEvent evt){
       
        JFileChooser jfc=(new JFileChooser());
        if( jfc.showSaveDialog(null) != JFileChooser.APPROVE_OPTION)
           return;
        File file =jfc.getSelectedFile();
        if( file == null)
           return;
        int i = file.getName().lastIndexOf('.');
        if( i<= 0){
           SharedData.addmsg("the Filename must have an extension for type of save");
           return; 
        }
        String extension = file.getName().substring( i+1).toLowerCase();
        if( extension == null){ 
           SharedData.addmsg("Save FileName must have an extension");
           return; 
        }
        if( extension.length() <2){
           SharedData.addmsg("Save FileName must have an extension with more than 1 character");
           return; 
        }

        Rectangle R = comp.getBounds();
        BufferedImage bimg= new BufferedImage(R.width, R.height,BufferedImage.TYPE_INT_RGB ); 

  
   
        Graphics2D gr = bimg.createGraphics();
  
        comp.paint( gr);

        try{
           FileOutputStream fout =new FileOutputStream( file);
           if( !javax.imageio.ImageIO.write( bimg, extension ,(OutputStream)fout )){
              SharedData.addmsg( " no appropriate writer is found");
              return; 
           }
           fout.close();
        }catch( Exception ss){
           SharedData.addmsg( "Image Save Error:"+ss.toString());
        }
    

      
   }






}

