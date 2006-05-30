/*
 * File: SharedMessages.java
 *
 * Copyright (C) 2004, Mike Miller
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
 * Primary   Mike Miller <millermi@uwstout.edu>
 * Contact:  Student Developer, University of Wisconsin-Stout
 *
 * Contact : Dennis Mikkelson <mikkelsond@uwstout.edu>
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751, USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.6  2006/05/30 18:56:07  rmikk
 *  log messages are now appended to the end of the file if it already exists.
 *
 *  Revision 1.5  2005/08/26 15:28:44  rmikk
 *  Added code in LOGaddmsg to replace all 2 char \n by one char \n
 *
 *  Revision 1.4  2004/10/09 13:54:56  rmikk
 *  Checked for null String case
 *
 *  Revision 1.3  2004/08/19 12:19:43  rmikk
 *  Added routines openLog, closeLog and LOGaddmsg to handle global logging
 *
 *  Revision 1.2  2004/03/11 23:53:56  millermi
 *  - Removed Command prefix from StatusPane
 *
 *  Revision 1.1  2004/03/11 22:13:14  millermi
 *  - Changed package names and replaced SharedData with
 *    SharedMessages class.
 *
 *
 ********************Last Log message from ISAW's SharedData*******************
 **************See ISAW's SharedData for further revision details**************
 *  Revision 1.18  2004/01/24 20:57:51  bouzekc
 *  Removed unused imports.
 ******************************************************************************
 */

package gov.anl.ipns.Util.Sys;
import java.io.*;
/**
 *  Objects of this class have one instance of objects that are to be shared
 *  by several packages.  The shared objects are instantiated one time 
 *  as static members of the class.
 */

public class SharedMessages implements java.io.Serializable 
{
  /** The Global StatusPane.  Everyone can "add"(Display) values on this pane
   * if Displayable or the Values will be displayed on System.out
   */
  private static StatusPane status_pane=  null;
  
  /**
   *  The Global LOGoutput file.  Everyone can write to this log file with
   *  LOGaddmsg.  If LOGout is null, the information will be sent to the
   *  status_pane(or System.out if status_pane is null)
   */
  private static FileOutputStream LOGout = null;
  
  //addmsg places a return after each message.  LOGaddmsg may have several
  //entries per line so this stores output until a return is entered
  private static String buff ="";
  /**
   * Returns a pointer to this classes (static) StatusPane for use in
   * GUIs. This will create the StatusPane if it does not already
   * exist.
   */
  public static StatusPane getStatusPane(){
    if(status_pane==null)
      status_pane=new StatusPane( 30,70);
    return status_pane;
  }
 
 
  /**
   *  Returns the FileOutputStream where the log information is sent
   */
  public static FileOutputStream getLogStream(){
    return LOGout;
  }
  /**
   * Convenience method to ease adding to the status pane.
   */
  public static void addmsg(Object value){
    if( status_pane==null || ! status_pane.isDisplayable())
      System.out.println( StringUtil.toString( value) );  
    else
      status_pane.add(value);
  }
  
  /**
   *  Adds the message to the log file( or status pane if  the logfile has
   *  not been sent). NOTE: "\n" MUST be part of the message to get returns
   *  unlike addmsg
   * @param message  The message to be appended to the log file
   */
  public static void LOGaddmsg( String message){
    if( message == null)
       return;
    for( int k = message.indexOf("\\n"); k>=0;){
    
         message=message.substring(0,k)+'\n'+message.substring(k+2);
         k=message.indexOf("\\n");
    }
    if( LOGout !=null)
      try{
        LOGout.write( message.getBytes());
      }catch(Exception ss){
         return;
      }
    else{
      buff +=message;
      buff= sendmsg(buff);
    }
    
  }
  
 // Sends the message to the addmsg command.  Strips out "\n"'s
 private static String sendmsg( String buff){
   if( buff==null)
     return "";
   int i= buff.indexOf('\n');
   if( i<0)
      return buff;
   addmsg( buff.substring(0,i));
   String S = buff.substring(i+1);
   if( S == null)
      S ="";
   return sendmsg( S );
      
   
 }
 
 /**
  *  Sets up the file that information will be logged to.
  *  The previous log file will be updated and closed
  * @param filename   The name of the file that will receive log information
  */
 public static void openLog( String filename){
    
    if( filename == null)
       LOGout = null;
    else
      try{
         if( LOGout != null)
            LOGout.close();
         LOGout = new FileOutputStream( filename, true);
      }catch(Exception s){
         LOGout = null;
      }
    buff = "";
 }
 
 
 /**
  *  Closes the log file.
  *  Any subsequent log information will go to the status_pane
  *
  */
 public static void closeLog(){
   try{
     if(LOGout != null)
       LOGout.close();
     else if( buff.length()>1)
       addmsg( buff);
    buff="";
    LOGout = null;
   }catch(Exception ss){
       buff = null;
       LOGout = null;
   }
 }
}
