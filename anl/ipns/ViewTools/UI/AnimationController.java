/*
 * File: AnimationController.java
 *
 * Copyright (C) 2001, Dennis Mikkelson
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
 *  Revision 1.17  2004/03/15 23:53:56  dennis
 *  Removed unused imports, after factoring out the View components,
 *  Math and other utils.
 *
 *  Revision 1.16  2004/03/11 22:39:29  serumb
 *  Changed Package and imports.
 *
 *  Revision 1.15  2003/10/15 23:35:39  dennis
 *  Fixed javadocs to build cleanly with jdk 1.4.2
 *
 *  Revision 1.14  2003/10/02 19:31:47  dennis
 *  Fixed bug... two frame change messages were being sent for each
 *  newly selected frame.
 *
 *  Revision 1.13  2002/11/27 23:13:34  pfpeterson
 *  standardized header
 *
 *  Revision 1.12  2002/07/22 21:22:04  dennis
 *  Added methods setFrameValue() and setFrameNumber().
 *
 *  Revision 1.11  2002/02/18 16:28:11  dennis
 *  Added separate control to select the frame by specifying
 *  the frame number.
 *
 */

package gov.anl.ipns.ViewTools.UI;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
//import DataSetTools.util.*;

/**
 * An AnimationController object is a GUI component for controlling a sequence
 * of displayed images.  It includes buttons to start, stop and single step
 * the frames and includes a textual display that gives the current frame 
 * value and allows the user to advance to a specified frame value.  An array
 * of float values that map the frame numbers to some physically meaningful
 * values can also be provided.  If an array of frame values is provided, the
 * frame numbers will be restricted to the range of indices for the array. 
 */

public class AnimationController extends    ActiveJPanel 
                                 implements Serializable 
{
  private static final String  RUN_BACKWARD  = "<<";
  private static final String  STEP_BACKWARD = "<";
  private static final String  STOP          = "||";
  private static final String  STEP_FORWARD  = ">";
  private static final String  RUN_FORWARD   = ">>";

  private TextValueUI      frame_box;               // show & controls frame 
  private TextValueUI      value_box;               // shows float value at 
                                                    // current frame and sets
                                                    // frame from the value
  private String           value_label  = "";
  private TitledBorder     border;
  private Thread           run_thread   = null;
                                                    // these are used by
  private volatile int     frame_number = 0;        // different Threads
  private volatile int     step_time_ms = 100;
  private volatile float[] frame_values = null;
  private volatile String  run_state    = STOP;

  private volatile int     last_num_sent = -1;      // track the last frame
                                                    // number sent and don't
                                                    // send same one twice
 
 /* ------------------------------ CONSTRUCTOR ---------------------------- */
 /** 
  *  Construct an AnimationController with no associated values, blank 
  *  border title, blank text label and with a default step time of 
  *  100 milliseconds.  With now frame values set, the controller will
  *  step up and/or down through all integer values.  For most purposes, it
  *  will be necessary to call setFrame_values() to restrict the range of
  *  values and to associate meaningful values with the frames.
  */
  public AnimationController( )
  { 
     border = new TitledBorder(LineBorder.createBlackLineBorder(),
                               "");
     border.setTitleFont( FontUtil.BORDER_FONT );
     setBorder( border );

     setLayout( new GridLayout( 3, 1 ) );

     frame_box = new TextValueUI( "Frame ", frame_number );
     frame_box.setHorizontalAlignment( JTextField.CENTER );
     frame_box.addActionListener( new FrameListener() );
     value_box = new TextValueUI( value_label, frame_number );
     value_box.setHorizontalAlignment( JTextField.CENTER );
     value_box.addActionListener( new ValueListener() );
     add( frame_box );
     add( value_box );

     JPanel  button_panel = new JPanel();
     button_panel.setLayout( new GridLayout( 1,5 ) );
     Insets  margin = new Insets( 0, 0, 0, 0 );
     ActionListener button_listener = new ButtonListener();

     JButton button = new JButton( RUN_BACKWARD );
     button.setMargin( margin );
     button.addActionListener( button_listener );
     button_panel.add( button );
    
     button = new JButton( STEP_BACKWARD );
     button.setMargin( margin );
     button.addActionListener( button_listener );
     button_panel.add( button );

     button = new JButton( STOP );
     button.setMargin( margin );
     button.addActionListener( button_listener );
     button_panel.add( button );

     button = new JButton( STEP_FORWARD );
     button.setMargin( margin );
     button.addActionListener( button_listener );
     button_panel.add( button );

     button = new JButton( RUN_FORWARD );
     button.setMargin( margin );
     button.addActionListener( button_listener );
     button_panel.add( button );

     add( button_panel );
  }

 /* ---------------------------- setBorderTitle ------------------------- */
 /** 
  *  Set the title to be used on the border around the controller GUI.
  *  
  *  @param title  Title for the TitledBorder around the controller panel.
  */

  public void setBorderTitle( String title )
  {
    border.setTitle( title );
  }


 /* ---------------------------- setTextLabel --------------------------- */
 /** 
  *  Set the label for the textual representation of the frame value.
  *  
  *  @param label  label for the textual display of the frame value.
  */

  public void setTextLabel( String label )
  {
    value_label = label;
    if ( frame_values == null )
      value_box.setLabel( value_label );
    else
      value_box.setLabel( value_label );
  }


 /* --------------------------- setStep_time --------------------------- */
 /**
  *  Set the time between steps when the controller is running forward or
  *  running backward.
  *
  *  @param  time_ms  The time between steps, in milliseconds.
  */
  public void setStep_time ( int time_ms )
  {
    if ( time_ms < 1 )
      time_ms = 1;
    step_time_ms = time_ms;
  }


 /* ------------------------- setFrame_values ---------------------------- */
 /**
  *  Set numeric values to be associated to the frames controlled by this
  *  controller.  The numeric values are displayed in the TextField of the
  *  controller.  The user may also select a particular frame by entering
  *  the value in the text field.  The frame number whose value is closest
  *  to the value entered will be selected.  
  *    The array of frame values also sets the number of frames that are 
  *  controlled.  If no frame values are set ( or if the array is set to null )
  *  The controller will run through all integer values.  If a sequence of
  *  N frames is to be controlled, you MUST pass an array of N frame values
  *  to setFrame_values.
  *
  *  NOTE: This method is not thread safe.  It should NOT be called when
  *        the controller is being activated either by the user, or if it is
  *        running forward or backward.
  *
  *  @param  values  Array giving numeric values to be associated with
  *                  each frame.
  */

  public void setFrame_values( float values[] )
  {
    if ( values == null || values.length == 0 )     // no valid frame values
      frame_values = null;

    else
    {
      frame_values = new float[ values.length ];
      System.arraycopy( values, 0, frame_values, 0, values.length );
      set_frame( frame_number );
    }
  }

 /* ---------------------------- getFrameNumber --------------------------- */
 /**
  *  Get the current frame number from the controller.
  *
  *  @return  the current frame number
  */
  public int getFrameNumber()
  {
    return frame_number;
  }


 /* ---------------------------- setFrameNumber --------------------------- */
 /**
  *  Set the controller to the specified frame number.
  *
  *  @param  frame    the new frame number to use.
  */
  public void setFrameNumber( int frame )
  {
    set_frame(frame);
  }


 /* ---------------------------- getFrameValue --------------------------- */
 /**
  *  Get the current frame value from the controller.
  *
  *  @return  the current frame value 
  */
  public float getFrameValue()
  {
    if ( frame_values        == null  || 
         frame_values.length == 0     || 
         frame_number >= frame_values.length   )
    {
      System.out.println("WARNING: no valid frame value in getFrameValue()" );
      if ( frame_values == null )
        System.out.println("frame_values == null");
      else
        System.out.println("frame_values.length = " + frame_values.length );
      System.out.println("frame_number = " + frame_number );
      return Float.NaN;
    } 
    else
      return frame_values[ frame_number ];
  }


 /* ---------------------------- setFrameValue --------------------------- */
 /**
  *  Set the controller to the specified frame value.
  *
  *  @param  value  the new frame value to use.
  */
  public void setFrameValue( float value )
  {
    if ( frame_values == null )          // no values assigned, just use the
      set_frame( (int)value );           // value as the frame number;

    else                                 // find the frame number with the
    {                                    // closest value
      int min_index = 0;
      for ( int i = 0; i < frame_values.length; i++ )
        if ( Math.abs( value - frame_values[i] )   <
             Math.abs( value - frame_values[min_index] ) )
          min_index = i;

      set_frame( min_index );
    }
  }


/* ------------------------------------------------------------------------
 *
 *  PRIVATE METHODS 
 *
 */

/* ----------------------------- step_forward --------------------------- */

synchronized private void step_forward()
{
  int number = frame_number + 1;
  set_frame( number );
}


/* ---------------------------- step_backward --------------------------- */

synchronized private void step_backward()
{
  int number = frame_number - 1;
  set_frame( number );
}


/* ------------------------------ set_frame ---------------------------- */
/*
 *  Make sure the frame number is valid, update the text box and notify 
 *  all listeners.
 */

synchronized private void set_frame( int number )
{
  if ( frame_values == null )                     // just use any number given
    value_box.setValue( number );

  else                                            // we must do bounds checking
  {
    if ( number >= frame_values.length )
    {
      run_state = STOP;
      number = frame_values.length - 1;
    }
    if ( number < 0 )
    {
      run_state = STOP;
      number = 0;
    }

    frame_box.setValue( number );
    value_box.setValue( frame_values[ number ] );   // use the frame_value for
                                                   // the given frame number
  }

  frame_number = number;
  if ( frame_number != last_num_sent )
  {
    last_num_sent = frame_number;
    send_message( ""+frame_number );
  }
}


/* -------------------------------------------------------------------------
 *
 *  INTERNAL CLASSES
 *
 */

/* ------------------------------ ButtonListener ------------------------- */
/*
 *  Process events from the step/stop/run buttons.
 *
 */

private class ButtonListener implements ActionListener
{
  public void actionPerformed( ActionEvent e )
  {
    run_state = e.getActionCommand();

    if ( run_state.equals( STEP_FORWARD ) )
      step_forward();
    else if ( run_state.equals( STEP_BACKWARD ) ) 
      step_backward();
    else if ( run_state.equals( RUN_FORWARD ) || 
              run_state.equals( RUN_BACKWARD ) )
    {
      if ( run_thread == null )      
      {
        run_thread = new AutoRun();
        run_thread.start();
      } 
      else
        run_state = STOP;
    }
  }
}

/* ------------------------------ ValueListener ------------------------- */
/*
 *  Process events from the textual display of the frame value.
 *
 */

private class ValueListener implements ActionListener
{
  public void actionPerformed( ActionEvent e )
  {
    String action = e.getActionCommand();
    float value = value_box.getValue();

    setFrameValue( value );
  }
}


/* ------------------------------ FrameListener ------------------------- */
/*
 *  Process events from the textual display of the frame value.
 *
 */

private class FrameListener implements ActionListener
{
  public void actionPerformed( ActionEvent e )
  {
    String action   = e.getActionCommand();
    float frame_num = frame_box.getValue();

    set_frame( (int)frame_num );   
  }
}


/* --------------------------------- AutoRun ----------------------------- */
/*
 *  Thread to step from frame to frame when the controller is running 
 *  forward or backward. 
 */

private class AutoRun extends Thread
{
  public void run()
  {
    boolean  done = false;

    while ( !done )      
    {
      if ( run_state.equals( RUN_FORWARD ) )
        step_forward(); 
      else if ( run_state.equals( RUN_BACKWARD ) )
        step_backward(); 
      else
        done = true;                                // thread dies if any other
                                                    // button is pressed
      if ( !done )
      try
      {
        Thread.sleep( step_time_ms );
      }
      catch( Exception e )
      {
        System.out.println("Exception while sleeping in AutoRun:"+e);
      }
    } 

    run_thread = null;                              // get rid of reference 
                                                    // to this thread
  }

}


/* -------------------------------------------------------------------------
 *
 * MAIN  ( Basic main program for testing purposes only. )
 *
 */
    public static void main(String[] args)
    {
      JFrame f = new JFrame("Test for AnimationController");
      f.setBounds(0,0,200,150);
      AnimationController control  = new AnimationController();

      f.getContentPane().setLayout( new GridLayout(1,1) );
      f.getContentPane().add(control);

      float values[] = new float[20];
      for ( int i = 0; i < values.length; i++ )
        values[i] = i*i;

      control.setFrame_values( values );
      control.setStep_time( 100 );
      control.addActionListener( new ActionListener()
       {
         public void actionPerformed(ActionEvent e)
         {
           String action = e.getActionCommand();
           System.out.println("In Main, command = " + action );
         }
       });

      f.setVisible(true);
    }
}
