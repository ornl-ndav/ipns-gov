/* 
 * File: BusyDisplay.java
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
 *  $Author:$
 *  $Date:$            
 *  $Rev:$
 */
package gov.anl.ipns.Util.Sys;

import java.awt.*;
import javax.swing.*;

/**
 *  This produces a panel that looks busy.
 *  NOTE: The thread that makes it look busy has high priority. It may not run
 *  on some long calculations especially if ALL cores are in use.
 *  
 * @author ruth
 *
 */
public class BusyDisplay extends JPanel implements IhasWindowClosed
{

   /**
    * 
    */
   private static final long serialVersionUID = 1L;
   JProgressBar progressBar;
   MyThread  thread;
   
   /**
    * Constructor
    */
   public BusyDisplay()
   {
      super();
      setLayout( new GridLayout(1,1));
      progressBar = new JProgressBar( 0,100);
      thread = new MyThread();
      thread.setPriority( Thread.MAX_PRIORITY );
      add( progressBar);
      thread.start( );
      
   }
   
   /**
    * Moves a JFrame so the top left corner is at the given location
    * 
    * @param frame  The JFrame to move
    * @param x      The x position of the frame's top left corner
    * @param y      The y position of the frame's top left corner
    */
   public static void MoveFrame( JFrame frame ,int x, int y)
   {
      Rectangle R = frame.getBounds();
      R.x = x;
      R.y = y;
      frame.setBounds( R );
   }
   
   /**
    *  Interrupts the timer thread.
    */
   @Override
   public void WindowClose(String ID)
   {

      thread.interrupt( );
      
   }


   /**
    * Shows a small JFrame with a bar going back and forth indicating
    * a busy state.
    * 
    * @param Message   Message on the JFrame
    * @return          The JFrame that can be used with MoveFrame and KillBusyGUI
    */
   public static JFrame  ShowBusyGUI( String Message)
   {
      BusyDisplay busy = new BusyDisplay();
      FinishJFrame frame = new FinishJFrame( Message );
      frame.getContentPane( ).setLayout( new GridLayout(1,1));
      frame.getContentPane().add( busy  );
      frame.setSize( 300,100 );
      frame.addWindowListener(  new IndirectWindowCloseListener( busy,"Frame") );
      frame.setVisible( true );
      return frame;

   }
   
   /**
    * Disposes of the frame, freeing up resources
    * 
    * @param frame  The frame to be displosed of
    */
   public static void KillBusyGUI(  JFrame frame)
   {
      if( frame ==  null)
         return;
      
      frame.removeAll( );
      frame.dispose( );
   }

   public static void main( String[] args)
   {
      JFrame frame = BusyDisplay.ShowBusyGUI("Calculating");
      double x=1;
      for( double i=0; i<1E8; i++)
      {
         x = Math.cos( x+1 );
      }
      System.out.println("Done" + x);
      try
      {
         int c = System.in.read( );
         System.out.println(c);
      }catch(Exception s)
      {
         
      }
      
      BusyDisplay.MoveFrame  (frame, 100,100 );
   }
    public static void main1(String[] args)
   {
      BusyDisplay busy = new BusyDisplay();
      FinishJFrame frame = new FinishJFrame(" Busy");
      frame.getContentPane( ).setLayout( new GridLayout(1,1));
      frame.getContentPane().add( busy  );
      frame.setSize( 300,100 );
      frame.addWindowListener(  new IndirectWindowCloseListener( busy,"Frame") );
      frame.setVisible( true );

   }
   
    class MyThread  extends Thread
    {
      private int count =0;
      @Override
      public void run()
      {

         try
         {
            while( true )
            {
               Thread.sleep( 700 );
               count+=15 ;
               
               if ( count > 100 )
                  count = 0;
               progressBar.setValue( count );
               
            }
         } catch( Exception s )
         {

         }
         
      }
       
    }
}
