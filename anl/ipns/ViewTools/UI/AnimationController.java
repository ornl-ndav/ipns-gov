/*
 * @(#)AnimationController.java
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
 *           Menomonie, WI. 54751
 *           USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.3  2001/06/06 21:27:45  dennis
 *  Removed redundant size check for the list of listeners.
 *
 *  Revision 1.2  2001/05/29 19:43:12  dennis
 *  Now properly displays the frame value immediately after setting
 *  values for the frames.
 *
 *  Revision 1.1  2001/05/29 15:03:27  dennis
 *  Component to start, stop and single step an animation.
 *
 *
 */

package DataSetTools.components.ui;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import DataSetTools.util.*;

/**
 * An AnimationController object is a GUI component for controlling a sequence
 * of displayed images.  It includes buttons to start, stop and single step
 * the frames and includes a textual display that gives the current frame 
 * value and allows the user to advance to a specified frame value.  An array
 * of float values that map the frame numbers to some physically meaningful
 * values can also be provided.  If an array of frame values is provided, the
 * frame numbers will be restricted to the range of indices for the array. 
 */

public class AnimationController extends    JPanel 
                                 implements Serializable 
{
  private static final String  RUN_BACKWARD  = "<<";
  private static final String  STEP_BACKWARD = "<";
  private static final String  STOP          = "||";
  private static final String  STEP_FORWARD  = ">";
  private static final String  RUN_FORWARD   = ">>";

  private TextValueUI    text_box;
  private Vector         listeners = null;
  private TitledBorder   border;
  private int            frame_number = 0;
  private String         run_state    = STOP;
  private int            step_time_ms = 100;
  private Thread         run_thread   = null;
  private float[]        frame_values = null;

 
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
     listeners = new Vector();

     border = new TitledBorder(LineBorder.createBlackLineBorder(),
                               "");
     border.setTitleFont( FontUtil.BORDER_FONT );
     setBorder( border );

     setLayout( new GridLayout( 2, 1 ) );

     text_box = new TextValueUI("", frame_number);
     text_box.setHorizontalAlignment( JTextField.CENTER );
     text_box.addActionListener( new TextListener() );
     add(text_box);     

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
    text_box.setLabel( label );
  }


 /* ------------------------ addActionListener -------------------------- */
 /** 
  *  Add an ActionListener for this AnimationController.  Whenever the frame
  *  number is changed by the controller, an ActionEvent will be sent to all 
  *  of the ActionListeners.  The ActionEvent contains the new frame number
  *  as the ActionCommand string.
  *  
  *  @param listener  An ActionListener whose ActionPerformed() method is 
  *                   to be called when the AnimationController changes the
  *                   frame number.
  */
 
  public void addActionListener( ActionListener listener )
  {
    listeners.add( listener );
  }

 /* ------------------------ removeActionListener ------------------------ */
 /**
  *  Remove the specified ActionListener from this AnimationController.  If 
  *  the specified ActionListener is not in the list of ActionListeners for
  *  for this controller this method has no effect. 
  *  NOTE: This method is NOT thread safe.  It should NOT be called when
  *        the controller is being activated either by the user, or if it is 
  *        running forward or backward.
  * 
  *  @param listener  The ActionListener to be removed.
  */

  public void removeActionListener( ActionListener listener )
  {
    listeners.remove( listener );
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
  *  @param  time_ms  The time between steps, in milliseconds.
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
    text_box.setValue( number );

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

    text_box.setValue( frame_values[ number ] );   // use the frame_value for
                                                   // the given frame number
  }

  frame_number = number;
                                                  // send action event to 
  for ( int i = 0; i < listeners.size(); i++ )    // all of the listeners
  {
    ActionListener listener = (ActionListener)listeners.elementAt(i);
    listener.actionPerformed( new ActionEvent( this, 0, ""+frame_number ) );
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
    }
  }
}

/* ------------------------------ TextListener ------------------------- */
/*
 *  Process events from the textual display of the frame value.
 *
 */

private class TextListener implements ActionListener
{
  public void actionPerformed( ActionEvent e )
  {
    String action = e.getActionCommand();
    float value = text_box.getValue();

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
