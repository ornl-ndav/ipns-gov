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
 
 import DataSetTools.util.WindowShower;

/**
 * This class is an ViewControl (ActiveJPanel) with a generic slider for use 
 * by ViewComponents. It includes a hook to send out messages when the  
 * slider has been adjusted.
 */ 
public class ControlSlider extends ViewControl
{
  private JSlider slide;
  private float value;
  private Point range;
  private int range_precision;
  private int power;
  
 /**
  * Default constructor specifies no title but initializes slider with range
  * (0.0,100.0) and interval of 0.1. The default slider is useful for use as
  * an intensity (color) slider.
  */ 
  public ControlSlider()
  {  
    super("");
    this.setLayout( new GridLayout(1,1) );
    slide = new JSlider();
    this.add(slide);
    value = 0;
    range = new Point(0,1000);
    range_precision = 4;
    power = 1;
    
    slide.addChangeListener( new SlideListener() );
    slide.setValue(0);
    slide.setMinimum(range.x);
    slide.setMaximum(range.y);
    slide.setMajorTickSpacing(200);
    slide.setMinorTickSpacing(50);
    slide.setPaintTicks(true);
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
  * getValue() returns the value associated with the slider "knob" position.
  *
  *  @return value
  */  
  public float getValue()
  {   
    return value;
  }
  
 /**
  * This method sets the slider "knob" to the specified value.
  * If an invalid value is entered, nothing is done.
  *
  *  @param  new_val - position of "knob"
  */
  public void setValue(float new_val)
  {
    if( new_val <= (float)(range.y/Math.pow(10.0, power))
	&& new_val >= (float)(range.x/Math.pow(10.0, power)) )
    {
      value = new_val;
      slide.setValue((int)((new_val+.05)*Math.pow(10.0, power)));
    }
    else
      System.out.println("Invalid Value, must be in range ( " + 
        		 (float)(range.x/Math.pow(10.0, power)) + " , " + 
        		 (float)(range.y/Math.pow(10.0, power)) + " )");
  }

 /**
  * This method specifies how to divide the range to be specified.
  * Example: Range (0,99) with precision = 2 will have subintervals of 1
  *	     Range (0,999) with precision = 3 will have subintervals of 1
  *	     Range (0,999) with precision = 4 will have subintervals of 0.1
  *
  * Note: 1) setPrecision must be done before setRange() to have affect.
  *	  2) if precision < precision of the values, loss of precision occurs
  *	     at endpoints. 
  *    Ex: range (1,1001) with prec = 2 yields range (0,1000) w/ interval 100 
  *
  *  @param  prec - Significant digits
  */
  public void setPrecision(int prec)
  {
    range_precision = prec;
  } 
  
 /**
  * Sets range of slider to specified range. The increment between the
  * minimun and maximum is determined by the setPrecision method.
  *
  *  @param  xmin - range minimum
  *	     xmax - range maximum
  */	
  public void setRange( float xmin, float xmax )
  {  
    // swap if xmin is bigger than xmax     
    if( xmax < xmin )
    {
      float temp = xmax;
      xmax = xmin;
      xmin = temp;
    } 
    // The following if is done because if the values are negative,
    // instead of adding 0.5, we must subtract 0.5
    int minegator = 1;
    int maxegator = 1;
    if( xmin < 0 )
    {
      minegator = -1;
      if( xmax < 0 )
   	maxegator = -1;
    }
          
    float max = xmax;
    if( Math.abs(max) < Math.abs(xmin) )
      max = Math.abs(xmin);

    int pow = 0;
    while( (max*(float)Math.pow(10.0, pow)/
     	   (float)Math.pow(10.0, range_precision - 1)) < 1 )
      pow++;
    if( pow > 0 )
    {
      range.x = (int)(xmin*Math.pow(10.0, pow) + minegator*.5);
      range.y = (int)(xmax*Math.pow(10.0, pow) + maxegator*.5);
    }
    else
    {
      while( (max*(float)Math.pow(10.0, pow)/
             (float)Math.pow(10.0, range_precision - 1)) > 1 )
        pow--;
      range.x = (int)(xmin*Math.pow(10.0, pow) + minegator*.5);
      range.y = (int)(xmax*Math.pow(10.0, pow) + maxegator*.5);
      if( pow < 0 )
      System.out.println("Warning in ControlSlider.java: Range precision " + 
        		 "lost due to choice of precision. To correct " +
        		 "this problem, use setPrecision(int prec).");
    }
    power = pow;
    slide.setMinimum(range.x);
    slide.setMaximum(range.y); 
    slide.setValue(range.x);	
  }
 
 /**
  * Set the spacing for the "long" tickmarks.
  * See JSlider.setMajorTickSpacing() in java docs for futher info.
  *
  *  @param  spacing - major tick spacing 
  */ 
  public void setMajorTickSpace(int spacing)
  {
    slide.setMajorTickSpacing((int)(spacing*Math.pow(10.0, power)));
  }

 /**
  * Set the spacing for the "short" tickmarks.
  * See JSlider.setMinorTickSpacing() in java docs for futher info.
  *
  *  @param  spacing - minor tick spacing 
  */   
  public void setMinorTickSpace(int spacing)
  {
    slide.setMinorTickSpacing((int)(spacing*Math.pow(10.0, power)));
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
        value = slider.getValue()/(float)Math.pow(10.0, power);
        //System.out.println("In stateChanged(), Value = " + value);
        ((ViewControl)slider.getParent()).send_message(SLIDER_CHANGED);
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
    slide.setTitle("mySlide");
    slide.setPrecision(3);
    slide.setRange(2.3f,20.5f);
    slide.setMajorTickSpace(3);
    slide.setMinorTickSpace(1);
    WindowShower shower = new WindowShower(frame);
    java.awt.EventQueue.invokeLater(shower);
    
    slide.setValue(13.29f);
    System.out.println("Value: " + slide.getValue());
    // invalid value
    slide.setValue(103.29f);
    System.out.println("Value: 103.29");
  }
}
