/*
 * File: ControlSlider.java
 *
 * Copyright (C) 2003, Mike Miller
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
 * This work was supported by the National Science Foundation under grant
 * number DMR-0218882, and by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.7  2004/01/30 22:14:59  millermi
 *  - Reimplemented this control, now uses CoordTransform to
 *    map from the float interval to the interval used by the
 *    integer JSlider.
 *  - ***NEED TO FINISH OBJECTSTATE IMPLEMENTATION***
 *  - Added messaging String formerly located in IViewControl
 *
 *  Revision 1.6  2004/01/05 18:14:06  millermi
 *  - Replaced show()/setVisible(true) with WindowShower.
 *  - Removed excess imports.
 *
 *  Revision 1.5  2003/10/16 05:00:14  millermi
 *  - Fixed java docs errors.
 *
 *  Revision 1.4  2003/09/19 02:41:54  millermi
 *  - Bug Fix - Edited setValue() so it works for more than one
 *    precision setting.
 *
 *  Revision 1.3  2003/05/24 17:36:54  dennis
 *  Removed unnecessary import statement. (Mike Miller)
 *
 *  Revision 1.2  2003/05/22 12:57:51  dennis
 *  Now prints value in main test program. Increased precision. (Mike Miller)
 *
 *  Revision 1.1  2003/05/20 19:44:46  dennis
 *  Initial version of standardized controls for viewers. (Mike Miller)
 *
 */
  
 package DataSetTools.components.View.ViewControls;
 
 import javax.swing.JSlider;
 import javax.swing.JFrame;
 import javax.swing.event.ChangeEvent;
 import javax.swing.event.ChangeListener;
 import java.io.Serializable;
 import java.awt.GridLayout;
 import java.awt.Point;
 
 import DataSetTools.util.floatPoint2D;
 import DataSetTools.util.WindowShower;
 import DataSetTools.components.View.ObjectState;
 import DataSetTools.components.image.CoordBounds;
 import DataSetTools.components.image.CoordTransform;

/**
 * This class is an ViewControl (ActiveJPanel) with a generic slider for use 
 * by ViewComponents. It includes a hook to send out messages when the  
 * slider has been adjusted.
 */ 
public class ControlSlider extends ViewControl
{ 
 /**
  * "Slider Changed" - This is a messaging String sent out whenever the
  * slider "knob" has changed.
  */
  public static final String SLIDER_CHANGED  = "Slider Changed";
 // ---------------------ObjectState Keys---------------------------------
 /**
  * "Slider Value" - This constant String is a key for referencing the state
  * information about what value is at the current "knob" position.
  * The value that this key references is a primative float within
  * the range of slider values.
  */
  public static final String SLIDER_VALUE = "Slider Value";
 
 /**
  * "Range" - This constant String is a key for referencing the state
  * information about the minimum and maximum values returned by this slider.
  * The value that this key references is of type Point, but the values
  * are floats multiplied by 10^x to become whole numbers.
  */
  public static final String RANGE = "Range";
  
  private JSlider slide;
  private float value;
  private int num_steps;
  private int power;
  private floatPoint2D range;
  private ControlSlider this_slider;
  private CoordTransform float_to_int;  // this transform will be used to
                                        // convert the float range to a
					// suitable integer range for use
					// with a JSlider.
  
 /**
  * Default constructor specifies no title but initializes slider with range
  * (0.0,100.0) and interval of 0.1. The default slider is useful for use as
  * an intensity (color) slider.
  */ 
  public ControlSlider()
  {  
    this(0,100,.001f);
    setMajorTickSpace(.2f);
    setMinorTickSpace(.05f);
  }
 
 /**
  * Constructor for slider with no title but has an initialized range
  * [min,max] and step. 
  *
  *  @param  min Slider minimum
  *  @param  max Slider maximum
  *  @param  step_size Percent of interval per step, on interval (0,1]
  */ 
  public ControlSlider( float min, float max, float step_size )
  {  
    this( min, max, Math.round(1/step_size) );
  }
 
 /**
  * Constructor for slider with no title but has an initialized range
  * [min,max] and step. 
  *
  *  @param  min Slider minimum
  *  @param  max Slider maximum
  *  @param  numsteps Percent of interval per step, on interval (0,1]
  */ 
  public ControlSlider( float min, float max, int numsteps )
  {  
    super("");
    this.setLayout( new GridLayout(1,1) );
    slide = new JSlider();
    float_to_int = new CoordTransform();
    this.add(slide);
    this_slider = this;
    setRange(min, max);
    value = min;
    // prevent step_size from being zero.
    if( numsteps == 0 )
      numsteps = 1;
    setStep(numsteps);
    
    slide.addChangeListener( new SlideListener() );
    setValue(value);
  }
 
 /**
  * Same functionality as default constructor, only this constructor allows
  * for title specification.
  *
  *  @param  title - title of slider
  */ 
  public ControlSlider(String title)
  {
    this();
    this.setTitle(title);
  } 
 
 /**
  * This method will get the current values of the state variables for this
  * object. These variables will be wrapped in an ObjectState.
  *
  *  @param  isDefault Should selective state be returned, that used to store
  *                    user preferences common from project to project?
  *  @return if true, the default state containing user preferences,
  *          if false, the entire state, suitable for project specific saves.
  */ 
  public ObjectState getObjectState( boolean isDefault )
  {
    ObjectState state = super.getObjectState(isDefault);/*
    state.insert( SELECTED, new Boolean(isSelected()) );
    state.insert( BUTTON_TEXT, new String(getText()) );
    state.insert( BUTTON_FONT, getButtonFont() );
    state.insert( SELECTED_COLOR, checkcolor );
    state.insert( UNSELECTED_COLOR, uncheckcolor );*/
    return state;
  }
     
 /**
  * This method will set the current state variables of the object to state
  * variables wrapped in the ObjectState passed in.
  *
  *  @param  new_state
  */
  public void setObjectState( ObjectState new_state )
  {
    super.setObjectState( new_state );
    /*
    Object temp = new_state.get(SELECTED);
    if( temp != null )
    {
      setSelected(((Boolean)temp).booleanValue()); 
    }
    
    temp = new_state.get(BUTTON_TEXT);
    if( temp != null )
    {
      setText((String)temp); 
    }
    
    temp = new_state.get(BUTTON_FONT);
    if( temp != null )
    {
      setButtonFont((Font)temp); 
    }
    
    temp = new_state.get(SELECTED_COLOR);
    if( temp != null )
    {
      setTextCheckedColor((Color)temp); 
    }
    
    temp = new_state.get(UNSELECTED_COLOR);
    if( temp != null )
    {
      setTextUnCheckedColor((Color)temp); 
    }*/
  }
  
 /**
  * getValue() returns the value associated with the slider "knob" position.
  *
  *  @return value
  */  
  public float getValue()
  {   
    return float_to_int.MapXFrom( (float)slide.getValue() );
  }
  
 /**
  * This method sets the slider "knob" to the specified value.
  * If an invalid value is entered, nothing is done.
  *
  *  @param  new_val - position of "knob"
  */
  public void setValue(float new_val)
  {
    // since slider could have orientation from max to min, check to see if
    // values are opposite.
    float float_min = float_to_int.getSource().getX1();
    float float_max = float_to_int.getSource().getX2();
    // if opposite, swap them.
    if( float_min > float_max )
    {
      float temp = float_min;
      float_min = float_max;
      float_max = temp;
    }
    if( new_val < float_min )
      new_val = float_min;
    else if( new_val > float_max )
      new_val = float_max;
    slide.setValue( Math.round(float_to_int.MapXTo(new_val)) );
  }

 /**
  * This method specifies how to divide the range into steps. The step
  * is specified by a percentage of the original interval.
  *
  *  @param  step_size The percentage of the interval per step. This value
  *                    must be on the range (0,1)
  */
  public void setStep(float step_size)
  {
    // make sure the following equality is true: 0 < step <= 1 
    if( step_size <= 0 )
      num_steps = 1;
    else if( step_size > 1 )
      num_steps = 1;
    else
      num_steps = Math.round(1f/step_size);
    setRangeTransform();
  } 

 /**
  * This method specifies how to divide the range into steps.
  *
  *  @param  numsteps The number of steps on this interval.
  */
  public void setStep(int numsteps)
  {
    // make sure the following equality is true: 0 < step <= 1 
    if( numsteps <= 0 )
      num_steps = 1;
    else
      num_steps = numsteps;
    setRangeTransform();
  }
  
 /**
  * Sets range of slider to specified range. The increment between the
  * minimun and maximum is determined by the setPrecision method.
  *
  *  @param  xmin - range minimum
  *  @param  xmax - range maximum
  */	
  public void setRange( float xmin, float xmax )
  {
    range = new floatPoint2D(xmin, xmax);
    setRangeTransform();
  }
 
 /**
  * Set the spacing for the "long" tickmarks. Enter a percentage of the
  * interval that the major ticks should be spaced. 
  * Ex. spacing = .2f would result in 5 major ticks since .2 => 1/5 
  *
  *  @param  spacing - major tick spacing 
  */ 
  public void setMajorTickSpace(float spacing)
  {
    // make sure spacing is on interval [0,1]
    if( spacing < 0 || spacing > 1 )
      spacing = 0;
    // get number of steps from integer range bounds, take percent of that. 
    int step_size = Math.round(float_to_int.getDestination().getX2() * spacing);
    slide.setMajorTickSpacing(step_size);
    showTicks(true);
  }

 /**
  * Set the spacing for the "short" tickmarks.Enter a percentage of the
  * interval that the major ticks should be spaced. 
  * Ex. spacing = .2f would result in 5 major ticks since .2 => 1/5 
  *
  *  @param  spacing - minor tick spacing 
  */   
  public void setMinorTickSpace(float spacing)
  {
    // make sure spacing is on interval [0,1]
    if( spacing < 0 || spacing > 1 )
      spacing = 0;
    // get number of steps from integer range bounds, take percent of that. 
    int step_size = Math.round(float_to_int.getDestination().getX2() * spacing);
    slide.setMinorTickSpacing(step_size);
    showTicks(true);
  }

 /**
  * Method to turn on(true)/off(false) the display of tickmarks.
  * 
  * Note: This turns off both major and minor tickmarks. To turn off
  *	  only major/minor, set the major/minor tick spacing to zero.  
  *
  *  @param  showticks
  */  
  public void showTicks(boolean showticks)
  {
    slide.setPaintTicks(showticks);
  }
  
 /*
  * This method creates an interval [0,num_steps] which allows each
  * step to be mapped to a true float range value using the float_to_int
  * transformation.
  */ 
  private void setRangeTransform()
  {
    float_to_int.setSource(new CoordBounds(range.x,0,range.y,1));
    float float_min = float_to_int.getSource().getX1();
    float float_max = float_to_int.getSource().getX2();
    float_to_int.setDestination( new CoordBounds(0,0,num_steps,1) );
    slide.setMinimum(0);
    slide.setMaximum(num_steps);
  }
  
 /*
  * This class listens for changes to the slider. With this class,
  * outside classes can listen to see if the component has changed.
  */
  private class SlideListener implements ChangeListener, Serializable
  { 
    public void stateChanged(ChangeEvent e)
    {
      JSlider slider = (JSlider)e.getSource();
        	     
      if ( !slider.getValueIsAdjusting() )
      {
        // System.out.println("In stateChanged(), Value = " + getValue());
        this_slider.send_message(SLIDER_CHANGED);
      } 
    }
  }
  
 /*
  *  For testing purposes only
  */
  public static void main(String[] args)
  {
    ControlSlider slide = new ControlSlider();
    JFrame frame = new JFrame();
    frame.setBounds(0,0,150,90);
    frame.getContentPane().add(slide);
    frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    slide.setTitle("mySlide");
    //slide.setStep(.001f);
    slide.setRange(2.3f,20.5f);
    slide.setMajorTickSpace(.25f);
    slide.setMinorTickSpace(.05f);
    WindowShower shower = new WindowShower(frame);
    java.awt.EventQueue.invokeLater(shower);
    shower = null;
    /*
    slide.setValue(13.29f);
    System.out.println("Value: " + slide.getValue());
    // invalid value
    slide.setValue(103.29f);
    System.out.println("Value: 103.29");*/
  }
}
