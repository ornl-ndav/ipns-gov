/*
 * File:  RunExecInteractive.java
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


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.*;
import java.io.*;
import java.util.*;

/**
 * This class Creates a JPanel that acts like a command window for running
 * executables via the java Runtime.exec command
 * 
 * @author Ruth
 * 
 */
public class RunExecInteractive extends JPanel implements ActionListener
{



   JTextArea    Area;      //The Area where the executable's output appears

   JTextField   Input;      // The text box where user's enter answers for questions

   // posed by the executable

   InputStream  inp , //The executable process' input stream
            err;           //The executable process' error stream

   OutputStream out;       //The executable process' outnput stream

   Thread       inpHandler , //Handler for process' input stream
            errHandler;    //Handler for process' output stream


   JScrollPane  scroller;  //ScrollPane for the Area with application output

   Process      proc;      //The process that runs the executables

   String       execName;  //The whole filename of the executable

   String       shortName;  //Short name for the executable

   Vector       args;      //Vector of arguments for executable(Not DONE)

   String       workingDir; //Working directory for application


   /**
    * Constructor for this class that runs executables
    * 
    * @param execName    The whole filename of the executable
    * @param shortName   Short name for the executable
    * @param args        Vector of arguments for executable
    * @param workingDir  Working directory for the executable
    */
   public RunExecInteractive( String execName, String shortName, Vector args,
            String workingDir )
   {

      this.execName = execName;
      this.shortName = shortName;
      this.args = args;
      this.workingDir = workingDir;

      Area = new JTextArea( 50 , 70 );
      Area.setEditable( false );

      setLayout( new BorderLayout() );
      scroller = new JScrollPane( Area );
      add( scroller , BorderLayout.CENTER );

      Input = new JTextField();
      add( Input , BorderLayout.SOUTH );
      Input.addActionListener( this );

      inp = err = null;
      out = null;

      RunProgram();

   }


   /**
    * Creates a window to execute non-graphical applications. It displays output from
    * the executable and sends input to the executable.
    * 
    * @param execName    The whole filename of the executable
    * @param shortName   Short name for the executable
    * @param args        Vector of arguments for executable(Not DONE yet)
    * @param workingDir  Working directory for the executable
    * @param HelpFilename  Help filename without the IsawHelp path prefix
    *                      included. This file must be in the {ISAW_HOME}/IsawHelp
    *                      directory or in one of its subdirectories
    * @return
    */
   public static Object InterActiveExec( String execName , String shortName ,
            Vector args , String workingDir , String HelpFilename )
   {

      RunExecInteractive ex = new RunExecInteractive( execName , shortName ,
               args , workingDir );

      String HFilename = getHelpFileName( HelpFilename );
      JFrame jf = new JFrame( shortName );
      jf.setLayout( new GridLayout( 1 , 1 ) );
      jf.getContentPane().add( ex );
      jf.setSize( 400 , 500 );
      jf.setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
      if( HFilename != null )
      {
         JMenuBar jmb = new JMenuBar();
         JMenu Hlp = new JMenu( "Help" );
         JMenuItem jmen = new JMenuItem( "About" );
         jmen.addActionListener( new ShowHelpActionListener( HFilename ) );
         jmb.add( Hlp );
         Hlp.add( jmen );
         jf.setJMenuBar( jmb );


      }
      WindowShower.show( jf );

      return "success";

   }


   /**
    * Returns the whole filename where the Help is stored
    * @return the whole filename where the Help is stored
    */
   private static String getHelpFileName( String HelpFilename )
   {

      if( HelpFilename == null || HelpFilename.length() < 1 )
         return null;
      String path = System.getProperty( "ISAW_HOME" , "" );
      if( path.length() > 0
               && "\\/".indexOf( path.charAt( path.length() - 1 ) ) < 0 )
         path += File.separator;
      String filename = path + "IsawHelp" + File.separator
               + HelpFilename;
      if( ! ( new File( filename ) ).exists() )
         return null;
      return filename;
   }


   private void RunProgram()
   {

      Area.append( "Starting " + shortName + "\n" );
      if( workingDir != null
               && workingDir.length() > 0
               && "/\\".indexOf( workingDir.charAt( workingDir.length() - 1 ) ) >= 0 )
         workingDir = workingDir.substring( 0 , workingDir.length() - 1 );
      try
      {
         proc = Runtime.getRuntime().exec( execName , null ,
                  new File( workingDir ) );
      }
      catch( Exception s )
      {
         s.printStackTrace();
         System.exit( 0 );
      }

      inp = proc.getInputStream();
      out = proc.getOutputStream();
      err = proc.getErrorStream();

      inpHandler = new outputHandler( Area , inp , "" );
      errHandler = new outputHandler( Area , err , "Error\n" );

      inpHandler.start();
      errHandler.start();


   }


   /**
    * Closes I/O streams to and from the process for the executable and
    *  shutsdown the executable.
    */
   public void finalize()
   {

      finished();
      try
      {
         super.finalize();
      }
      catch( Throwable s )
      {
         s.printStackTrace();
      }
   }


   private void finished()
   {

      try
      {
         if( inp != null )
            inp.close();
         inp = null;
      }
      catch( Exception s )
      {}
      try
      {
         if( out != null )
            out.close();
         out = null;
      }
      catch( Exception s )
      {}
      try
      {
         if( err != null )
            err.close();
         err = null;
      }
      catch( Exception s )
      {}
      try
      {
         if( proc != null )
            proc.destroy();
         proc = null;
      }
      catch( Exception s )
      {}

   }


   /**
    * Processes the input from the input text field and sends it to the 
    * executable
    * 
    * @param evt   Not used
    */
   public void actionPerformed( ActionEvent evt )
   {

      if( out == null )
         return;

      Area.append( Input.getText() + "\n" );

      try
      {
         out.write( ( Input.getText() + "\n" ).getBytes() );
         out.flush();
      }
      catch( Exception s )
      {
         System.out.println( "Could not write to " + shortName + " "
                  + Input.getText() );
      }

      Input.setText( "" );
   }


   /**
    * Test program. Currently runs Anvred.
    * @param args 
    *      args[0] full name of executable
    *      args[1] Short Name for executable
    *      args[2] Working directory
    *      All arguments must be present
    */
   public static void main( String[] args )
   {

      JFrame jf = new JFrame( "Testt" );
      jf.setLayout( new GridLayout( 1 , 1 ) );
      jf.getContentPane().add(
               new RunExecInteractive( args[0] ,
                        args[1] , null ,args[2] ) );
      
      jf.setSize( 400 , 500 );
      jf.setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
      jf.setVisible( true );


   }

   /**
    * Handles reading in text from the executable's output or error streams.
    * @author Ruth
    *
    */
   class outputHandler extends Thread
   {

      JTextArea   area;

      InputStream Inp;

      String      message;

      String      tag;


      /**
       * Constructor
       * @param area  The Text Area where this information is displayed
       *                                      (at the bottom)
       * @param Inp   The I/O stream from the executable
       * @param mess  A message to indicate the type of information that is
       *              being retrieved. Only the error stream has a message
       */
      public outputHandler( JTextArea area, InputStream Inp, String mess )
      {

         this.area = area;
         this.Inp = Inp;
         message = mess;
         tag = message;
         if( tag.length() < 1 )
            tag = "Anvred";
      }


      /**
       * Listens for input from the executable. Retrieves it and displays this
       * information ( on the AWT event queue) on the text area
       */
      public void run()
      {

         try
         {
            for( int c = Inp.read() ; c != - 1 ; c = Inp.read() )
            {
               String S = "" + (char) c;
               int nAvailable = inp.available();
               for( int j = 0 ; j < nAvailable && c != - 1 ; j++ )
               {
                  c = Inp.read();
                  if( c != - 1 )
                     S += "" + (char) c;
               }
               S = message + S;
               SwingUtilities.invokeLater( new DisplayText( S , area ) );
            }
         }
         catch( Exception ss )
         {
            // System.out.println("Exception on input for "+ tag +" "+ ss);
            // ss.printStackTrace();

            if( message.length() < 1 )
               SwingUtilities.invokeLater( new DisplayText( shortName
                        + " is finished\n" , area ) );
            finished();
            return;
         }
         if( message.length() < 1 )
            SwingUtilities.invokeLater( new DisplayText( shortName
                     + " is finished\n" , area ) );

         finished();
      }


   }


   /**
    * This class Displays text at the end of a JTextArea on the AWT EventQueue
    * 
    * @author Ruth
    *
    */
   class DisplayText extends Thread
   {

      String    text;

      JTextArea area;


      /**
       * Constructor
       * @param Text  The text to be displayed in the JTextArea
       * @param area  The JTextArea where the text will be displayed
       *               at the bottom of this area
       */
      public DisplayText( String Text, JTextArea area )
      {

         text = Text;
         this.area = area;
      }


      /**
       * appends the text to the end of the JTextArea and attempts to position
       * any viewport to the show the last line displayed.
       */
      public void run()
      {

         area.append( text );
         try
         {
            area.setCaretPosition( area.getDocument().getLength() - 1 );
         }
         catch( Exception s )
         {}
      }


   }


}
