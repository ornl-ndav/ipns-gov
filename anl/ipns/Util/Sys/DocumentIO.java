/*
 * File:  DocumentIO.java 
 *             
 * Copyright (C) 2000, Alok Chatterjee
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
 * Contact : Alok Chatterjee <achatterjee@anl.gov>
 *           Intense Pulsed Neutron Source Division
 *           Argonne National Laboratory
 *           9700 South Cass Avenue, Bldg 360
 *           Argonne, IL 60439-4845, USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 * $Log$
 * Revision 1.1  2004/03/11 23:26:28  millermi
 * - Made all methods of DocumentIO static.
 * - Listener classes using DocumentIO now call static methods.
 *
 *
 ********************Split Document functions from IsawGUI.Util*****************
 ****************See ISAW.IsawGUI.Util for previous log messages.***************
 * Revision 1.25  2004/01/24 23:09:39  bouzekc
 * Removed unused imports.
 *******************************************************************************
 */
package gov.anl.ipns.Util.Sys;

import javax.swing.text.*;
import java.io.*;

/**
 * Utility class for ISAW. 
 *
 * @version 0.7  
 */


public class DocumentIO
{
   private DocumentIO()
   {}

  /**
   * Loads a file into a Document. If an Exception is encountered this
   * prints information to the StatusPane and returns null.
   */
   public static Document openDoc( String filename )
   {
     StringBuffer buffer=readTextFile(filename);
     if(buffer==null || buffer.length()<=0)
       return null;

     Document doc=new PlainDocument();
     try{
       doc.insertString(0,buffer.toString(),null);
       return doc;
     }catch(BadLocationException e){
       SharedMessages.addmsg("BadLocationException while reading "+filename);
       return null;
     }
   }

  /**
   * Load an ascii file into a StringBuffer. If an Exception is
   * encountered this prints information to the StatusPane and returns
   * null.
   */
   public static StringBuffer readTextFile(String filename){
     if( filename==null || filename.length()<=0 ) return null;
     
     FileReader fr=null;
     StringBuffer buffer=new StringBuffer();
     try{
       fr=new FileReader(filename);
       BufferedReader br=new BufferedReader(fr);
       while(br.ready())
         buffer.append(br.readLine()+"\n");
     }catch(FileNotFoundException e){
       SharedMessages.addmsg("FileNotFoundException: "+filename);
       return null;
     }catch(IOException e){
       SharedMessages.addmsg("Something went wrong while reading "+filename);
       return null;
     }finally{
       if(fr!=null){
         try{
           fr.close();
         }catch(IOException e){
           // let it drop on the floor
         }
       }
     }

     return buffer;
   }

   public static void appendDoc( Document doc, String S )
   {
      if( doc == null )return;
      int end = doc.getLength();

      try
      {
         doc.insertString( end, S + "\n", null );
      }
      catch( Exception s )
      {
         System.out.println( "Error in appendDoc=" + s );
      }

   }


   public static String saveDoc( Document doc, String filename )
   {
      if( doc == null )
      {
         return null;
      }
      Element line;

      if( filename == null )
      {
         return null;
      }
      File f = new File( filename );

      try
      {
         FileWriter fw = new FileWriter( f );

         int i;

         Element root;

         root = doc.getDefaultRootElement();
         String c = "";

         for( i = 0; i < root.getElementCount(); i++ )
         {
            line = root.getElement( i );

            fw.write( doc.getText( line.getStartOffset(), line.getEndOffset() -
                  line.getStartOffset() - 1 ) );
            c = doc.getText( line.getEndOffset() - 1, 1 );
            if( i + 1 < root.getElementCount() )
               fw.write( "\n" );
         }
         fw.close();
         return null;

      }
      catch( IOException s )
      {
         return "Status: Unsuccessful";
      }
      catch( javax.swing.text.BadLocationException s )
      {
         return "status Usuccessful";
      }
   }
}

