/*
 * File: FrameController.java
 *
 * Copyright (C) 2005, Chad Jones
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
 * Primary   Chad Jones <cjones@cs.utk.edu>
 * Contact:  Student Developer, University of Tennessee
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
 *  Revision 1.1  2005/07/22 19:46:23  cjones
 *  Cleaned up code. Added a FrameController that uses AnimationController to
 *  handle frames in a scene.
 *
 */

package gov.anl.ipns.ViewTools.Components.ViewControls;

import java.io.*;
import java.util.Vector;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import gov.anl.ipns.ViewTools.Components.ObjectState;
import gov.anl.ipns.ViewTools.UI.AnimationController;

/**
 * A FrameController object is a GUI component for controlling a sequence
 * of displayed images.  It includes buttons to start, stop and single step
 * the frames and includes a textual display that gives the current frame 
 * value and allows the user to advance to a specified frame value.  An array
 * of float values that map the frame numbers to some physically meaningful
 * values can also be provided.  If an array of frame values is provided, the
 * frame numbers will be restricted to the range of indices for the array.
 * 
 * This object is implemented using AnimationController.
 * 
 * When the frame is changed by a user using the AnimationController, the action 
 * message FRAME_CHANGED is sent to all listeners.
 * 
 * @see gov.anl.ipns.ViewTools.UI.AnimationController 
 */

public class FrameController extends    ViewControl 
                                 implements Serializable,  ActionListener 
{
  /**
    * 
    */
   private static final long serialVersionUID = 1L;
private AnimationController animator;
  public static String FRAME_CHANGED ="FRAME_CHANGED";
  private String      value_label  = "";
  private String      border_title =  "";
  private float[]     frame_values = null;
  private int         step_time_ms = 100;
  public static String  VAL_LABEL_OS ="VAL_LABEL_OS";
  public static String  BORD_LABEL_OS ="BORD_LABEL_OS";
  public static String  FRAMEVALS_OS ="FRAMEVALS_OS";
  public static String   STEP_OS ="STEP_OS";
  public static String   FRAMENUM_OS="FRAMENUM_OS";
  
 
 /* ------------------------------ CONSTRUCTOR ---------------------------- */
 /** 
  *  Construct an FrameController with no associated values, blank 
  *  border title, blank text label and with a default step time of 
  *  100 milliseconds.  With now frame values set, the controller will
  *  step up and/or down through all integer values.  For most purposes, it
  *  will be necessary to call setFrame_values() to restrict the range of
  *  values and to associate meaningful values with the frames.
  */
  public FrameController( )
  {
  	super("ControlAnimator");
    animator = new AnimationController();
    animator.addActionListener(  this );
    add(animator);
  }

 /* -------------- View Control ----------------------------------------- */
  /**
   * Set value associated with this control.
   *
   *  @param  value Setable value for this control, This is the float value associated with
   *          the frame, not the index of the frame. Use setFrameNumber for that .
   */
   public void setControlValue(Object value)
   {
    
   	   setControlValue( value, true );
   }
   
   /**
    * Set value associated with this control.
    *
    *  @param  value Setable value for this control, This is the float value associated with
    *          the frame, not the index of the frame. Use setFrameNumber for that .
    *          
    *  @param  notify  if true, all FrameController listeners will be notified, otherwise
    *                  they will not be notified.  This should be false when set from another
    *                  source besides the animator.
    */
   public void setControlValue( Object value, boolean notify)
   {
      if( !notify)
         animator.removeActionListener(  this );
      setFrameValue( ((Number)value).floatValue());
      animator.addActionListener(  this  );
      if( notify)
         send_message( FRAME_CHANGED);
   }
   
  /**
   * Get value associated with this control that will change and need to be
   * updated.
   *
   *  @return Value associated with the current frame.
   */
   public Object getControlValue()
   {
     return getFrameValue();
   }
   
  /**
   * This method will make an exact copy of the control.
   *
   *  @return A new, identical instance of the control.
   */
   public ViewControl copy()
   {
   	 FrameController control_copy = new FrameController();
   	 
   	 control_copy.setFrame_values(frame_values);
   	 control_copy.setBorderTitle(border_title);
   	 control_copy.setTextLabel(value_label);
   	 control_copy.setStep_time(step_time_ms);
   	 control_copy.setFrameNumber(getFrameNumber());
   	 
   	 return control_copy;
   }
  
  public void setObjectState( ObjectState new_state)
  {
     if( new_state == null)
        return;
     if( new_state.get( VAL_LABEL_OS )!= null)
          value_label  =(String)  new_state.get( VAL_LABEL_OS );
     
     if( new_state.get( BORD_LABEL_OS )!= null)        
        border_title = (String) new_state.get( BORD_LABEL_OS );
     
     if( new_state.get( FRAMEVALS_OS)!= null)        
        frame_values =(float[]) new_state.get( FRAMEVALS_OS);
     
     if( new_state.get(  STEP_OS )!= null)        
         step_time_ms = ((Number)new_state.get(  STEP_OS )).intValue( );
     
     int FrameNum =((Number) new_state.get( FRAMENUM_OS )).intValue();
    
     animator.setBorderTitle( border_title );
     animator.setTextLabel( value_label );
     animator.setStep_time( step_time_ms );
     animator.setFrame_values(frame_values );
     setFrameNumber(FrameNum);
  }
  
  public ObjectState getObjectState( boolean isDefault)
  {
     ObjectState state = new ObjectState();
     if( isDefault)
     {
        state.insert( VAL_LABEL_OS , "");
        state.insert(BORD_LABEL_OS,"");
      //  state.insert(FRAMEVALS_OS ="FRAMEVALS_OS";
        state.insert(STEP_OS,100);
        state.insert( FRAMENUM_OS ,0 );
     }else
     {
        state.insert( VAL_LABEL_OS ,value_label);
        state.insert(BORD_LABEL_OS,border_title);
        state.insert(FRAMEVALS_OS ,frame_values);
        state.insert(STEP_OS,100);
        state.insert( FRAMENUM_OS ,getFrameNumber() );
        
     }
     
     return state;
  }
  
 /* ---------------------------- setBorderTitle ------------------------- */
 /** 
  *  Set the title to be used on the border around the controller GUI.
  *  
  *  @param title  Title for the TitledBorder around the controller panel.
  */

  public void setBorderTitle( String title )
  {
    animator.setBorderTitle( title );
    border_title = title;
  }


 /* ---------------------------- setTextLabel --------------------------- */
 /** 
  *  Set the label for the textual representation of the frame value.
  *  
  *  @param label  label for the textual display of the frame value.
  */

  public void setTextLabel( String label )
  {
    animator.setTextLabel( label );
    value_label = label;
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
    animator.setStep_time( time_ms );
    
    if(time_ms < 1)  step_time_ms = 1;
    else step_time_ms = time_ms;
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
    animator.setFrame_values( values );
    
    frame_values = values;
  }

 /* ---------------------------- getFrameNumber --------------------------- */
 /**
  *  Get the current frame number from the controller.
  *
  *  @return  the current frame number
  */
  public int getFrameNumber()
  {
    return animator.getFrameNumber();
  }


 /* ---------------------------- setFrameNumber --------------------------- */
 /**
  *  Set the controller to the specified frame number.
  *
  *  @param  frame    the new frame number to use.
  */
  public void setFrameNumber( int frame )
  {
    animator.setFrameNumber( frame );
  }


 /* ---------------------------- getFrameValue --------------------------- */
 /**
  *  Get the current frame value from the controller.
  *
  *  @return  the current frame value 
  */
  public float getFrameValue()
  {
    return animator.getFrameValue();
  }


 /* ---------------------------- setFrameValue --------------------------- */
 /**
  *  Set the controller to the specified frame value.
  *
  *  @param  value  the new frame value to use.
  */
  public void setFrameValue( float value )
  {
    animator.setFrameValue( value );
  }

  /**
   * Should only come from animator  when frame number is changed
   * 
   * @param evt  The action event
   */
  public void actionPerformed( ActionEvent evt)
  {
    
     if( evt.getSource() .equals(  animator ))
        super.send_message( FRAME_CHANGED);
     
  
  }
  
  /* ---------------------------Listeners---------------------------------*/
  /* ------------------------ addActionListener -------------------------- */
 /**
  *  Add an ActionListener for this ActiveJPanel.  
  *
  *  @param listener  An ActionListener whose ActionPerformed() method is
  *                   to be called when something changed in the panel.
  */
  public void addActionListener( ActionListener listener )
  {
    super.addActionListener( listener );
    animator.addActionListener(listener);
  }

 /* ------------------------ removeActionListener ------------------------ */
 /**
  *  Remove the specified ActionListener from this ActiveJPanel.  If
  *  the specified ActionListener is not in the list of ActionListeners for
  *  for this panel this method has no effect.
  *
  *  @param listener  The ActionListener to be removed.
  */
  public void removeActionListener( ActionListener listener )
  {
    super.removeActionListener(  listener );
    animator.removeActionListener(listener);
  }

 /* ---------------------- removeAllActionListeners ----------------------- */
 /**
  * Method to remove all listeners from this ActiveJPanel.
  */ 
  public void removeAllActionListeners()
  {
    super.removeAllActionListeners( );
    animator.removeAllActionListeners();
  }

 /* -------------------------- send_message ------------------------------- */
 /**
  *  Send a message to all of the action listeners for this panel
  */
  public void send_message( String message )
  {
  
    animator.send_message(message);
    
  
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
      FrameController control  = new FrameController();

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
