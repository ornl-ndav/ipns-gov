/*
 * File:  ShowHelpActionListener.java
 *
 * Copyright (C) 2009 Ruth Mikkelson
 *
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
 * Contact : Ruth Mikkelson <mikkelsond@uwstout.edu>
 *           
 *           Menomonie, WI 54751, USA
 *
 * This work was supported by the Spallation Neutron Source Division
 * of Oak Ridge National Laboratory, Oak Ridge, TN, USA.
 *
 *  Last Modified:
 * 
 */

package gov.anl.ipns.Util.Sys;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import java.io.*;

/**
 * Action Listener that when invoked creates a browser with hyperlink and back features
 * to display the indicated page.
 * @author Ruth
 *
 */
public class ShowHelpActionListener implements ActionListener
{



   String HelpFilename;

   /**
    * Constructgor
    * @param HelpFilename The name of the file to be displayed in the browser
    */
 
   public ShowHelpActionListener( String HelpFilename )
   {

      this.HelpFilename = HelpFilename;
   }


   /**
    * Displays the file in a browser with hyperlink and back button features. 
    */
   public void actionPerformed( ActionEvent e )
   {
      if( HelpFilename != null && HelpFilename.trim().length()>1 &&
                (new File(HelpFilename)).exists()){
            try
            {
               String fname =(new File(HelpFilename)).toURI().toURL().toString();
               new Browser( fname);
               
            }catch( Exception s)
            {
               JOptionPane.showMessageDialog( null ,"Caannot Find Help File");
            }
      }
     
    

   }

}
