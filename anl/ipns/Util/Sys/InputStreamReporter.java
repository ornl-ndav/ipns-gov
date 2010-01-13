/* 
 * File: InputStreamReporter.java
 *
 * Copyright (C) 2010, Ruth Mikkelson
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
 * This work was supported by the Spallation Neutron Source Division
 * of Oak Ridge National Laboratory, Oak Ridge, TN, USA.
 *
 *  Last Modified:
 * 
 *  $Author$:
 *  $Date$:            
 *  $Rev$:
 */
package gov.anl.ipns.Util.Sys;

import java.io.InputStream;

/**
 * This class gets the input from an InputStream, echoes it to the console, 
 * until the stream is closed or send out a negative number.
 * 
 * @author ruth
 *
 */
public class InputStreamReporter extends Thread
{

   

      InputStream inStream;

      String            message;

      /**
       * Constructor
       * @param inStream  The input stream to monitor and report
       * @param message   a message that will identify this input
       *                  stream if there is an error.
       */
      public InputStreamReporter(InputStream inStream, String message)
      {
         
         this.inStream = inStream;
         this.message = message;
      }

      public void run()
      {

         try
         {
            int c = 0;
            String S = "";
            while( c != -1 )
            {
               c = 0;
               while(  c >= 0  && inStream.available( )>0)
               {
                  c = inStream.read( );
                  if ( c >= 0 )
                     S += ( char ) c;
               }
               if( S.length() >0)
                  System.out.println( S );
               Thread.sleep( 150 );
               S = "";

            }
         } catch( Throwable s )
         {
           // Okay. When Stream is closed an IOException Occurs
          
         }

      }
 

}
