/*
 * File: Stretcher.java
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
 *  Last Modified:
 * 
 *  $Author$
 *  $Date$            
 *  $Revision$
 *
 */

package gov.anl.ipns.ViewTools.Components.ViewControls.ColorScaleControl;

import gov.anl.ipns.ViewTools.UI.ActiveJPanel;

import javax.swing.*;

import java.awt.GridLayout;
import java.awt.event.*;

/**
 * The Class Stretcher.
 * 
 * Creates a simple stretcher and a text field next to it with its value.
 * Has the ability to be changed by dragging or by changing
 * the value in the text field next to the stretcher.
 * 
 * The class creates minimum and maximum values for the range of data
 * as well as setting an interval between the slider and the max and minimum to
 * be used for features such as ganging two sliders.
 */
public class Stretcher extends ActiveJPanel
{
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The slider. */
	private JSlider slider;
	
	/** The text field. */
	private JTextField textField;
	
	/** The minimum. */
	private float minimum;
	
	/** The maximum. */
	private float maximum;
	
	/** The master value. */
	private float masterValue;
	
	/** The min interval. */
	private float minInterval;
	
	/** The max interval. */
	private float maxInterval;
	
	/**
	 * Instantiates a new stretcher.
	 * 
	 * @param min the min
	 * @param max the max
	 * @param intial the intial
	 */
	Stretcher(float min, float max, int initial)
	{
		minimum = min;
		maximum = max;
		masterValue = (maximum-minimum)/2;
		maxInterval = Math.max(maximum, minimum);
		minInterval = Math.min(maximum, minimum);
		slider = new JSlider(0,100);
		slider.addMouseListener(mouse);
		slider.addMouseMotionListener(motion);
		slider.addKeyListener(keys);
		textField = new JTextField("",10);
		textField.addActionListener(action);
		setValue((initial*((Math.max(maximum,minimum)-Math.min(minimum,maximum))/100))+Math.min(minimum,maximum));
		this.setLayout(new GridLayout(1,2));
		add(slider);
		add(textField);
	}

	
	/**
	 * Gets the value.
	 * 
	 * @return the value
	 */
	public float getValue()
	{
		return masterValue;
	}
	
	/**
	 * Sets the text and slider value.
	 * 
	 * @param value the new value
	 */
	public void setValue(float value)
	{
		String str = Float.toString(Round(value,2));
		textField.setText(str);
		changeValue(true);
	}
	
	/**
	 * Sets the interval.
	 * 
	 * @param minInt the minimum interval
	 * @param maxInt the maximum interval
	 */
	public void setInterval(float minInt, float maxInt)
	{
		minInterval = minInt;
		maxInterval = maxInt;
	}
	
	/**
	 * Set the maximum and minimum of the stretcher.
	 * 
	 * @param newMax
	 * @param newMin
	 * @param intial
	 */
	public void setMaxMin(float newMax, float newMin, int initial)
	{
		float tempMax = maxInterval/(maximum - minimum);
		float tempMin = minInterval/(maximum - minimum);
		maximum = newMax;
		minimum = newMin;
		setValue((slider.getValue()*
		          ((Math.max(maximum,minimum)-Math.min(minimum,maximum))/100))+
		                Math.min(minimum,maximum));
		
		maxInterval = tempMax*(maximum-minimum)+minimum;
		minInterval = tempMin*(maximum-minimum)+minimum;
	}
	
	/**
	 * Send action.
	 */
	private void sendAction()
	{
		this.send_message(getToolTipText());
	}
	
	/** Rounds the data in the text field to a manageable number
	 * 
	 * @param Rval
	 * @param Rpl
	 * @return
	 */
	private float Round(float Rval, int Rpl)
	{
		float p = (float)Math.pow(10,Rpl);
		Rval = Rval * p;
		float tmp = Math.round(Rval);
		return (float)tmp/p;
	}

	/** The ActionListener for the textboxs. */
	ActionListener action = new ActionListener()
	{
		public void actionPerformed(ActionEvent e)
		{
			if(validateRange(false))
			{
				changeValue(true);
				sendAction();
			}
		}
	};

	/** The Keylistener for keyboard input of the slider. */
	KeyListener keys = new KeyListener()
	{
		public void keyPressed(KeyEvent key) 
		{
			if(validateRange(true))
			{				
				changeValue(false);
				sendAction();					
			}
			else
			{
				slider.setValue((int) (((masterValue-minimum)/((maximum-minimum)/100))));
			}
			return;
		}

		public void keyReleased(KeyEvent key)
		{			
			if(validateRange(true))
			{				
				changeValue(false);
				sendAction();					
			}
			else
			{
				slider.setValue((int) (((masterValue-minimum)/((maximum-minimum)/100))));
			}
			return;
		}
		public void keyTyped(KeyEvent key){}
	};
	
	/** The MouseListener to control input from the mouse for the slider. */
	MouseListener mouse = new MouseListener()
	{
		public void mouseClicked(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
		public void mousePressed(MouseEvent e) {}

		public void mouseReleased(MouseEvent e) 
		{
			changeValue(true);
		}
	};
	
	/** The MouseMotionListener to control input from the mouse for the slider. */
	MouseMotionListener motion = new MouseMotionListener()
	{
		public void mouseDragged(MouseEvent e) 
		{
			if(validateRange(true))
			{
				changeValue(false);
				sendAction();
			}
		}

		public void mouseMoved(MouseEvent e) {}
	};
	
	/**
	 * Change value.
	 * 
	 * @param updateSlider true - updates slider, false - updates textbox
	 */
	private void changeValue(boolean updateSlider)
	{
		textField.removeActionListener(action);
		if(updateSlider)
		{
			String text = textField.getText();
			float datanumber = 0;
			datanumber = new Float(text).floatValue();
			int slidervalue = (int) (((datanumber-minimum)/((maximum-minimum)/100)));
			masterValue = datanumber;
			slider.setValue(slidervalue);
		}
		else
		{
			float value = slider.getValue();
			masterValue = (value*((Math.max(maximum,minimum)-Math.min(minimum,maximum))/100))+Math.min(minimum,maximum);
			String str = Float.toString(Round(masterValue, 2));
			textField.setText(str);
		}
		textField.addActionListener(action);
	}
	
	
	/**
	 * Validate range.
	 * 
	 * @param updateSlider true - updates slider, false - updates textbox
	 * 
	 * @return true, if successful
	 */
	private boolean validateRange(boolean updateSlider)
	{
		if(updateSlider)
		{
			float value = slider.getValue();
			value = (value*((Math.max(maximum,minimum)-Math.min(minimum,maximum))/100))+Math.min(minimum,maximum);
			if(Math.abs(value) <= Math.abs(maxInterval) && Math.abs(value) >= Math.abs(minInterval))
			  return true;
			
			return false;
		}
		else
		{
		  String text = textField.getText();
		  float datanumber = 0;
			  
		  try
		  {
			datanumber = new Float(text).floatValue();
		  }
		  catch(NumberFormatException e)
		  {
		    JOptionPane.showMessageDialog(null, "Syntax Error");
		    datanumber = masterValue;
		    textField.setText(Float.toString(datanumber));
		  }
			  
		  if(datanumber <= maxInterval && datanumber >= minInterval)
			 return true;
		  
		  if(datanumber<minInterval)
		  {
		  	JOptionPane.showMessageDialog(null, "Value less than minimum, setting back to Previous Value");
		  	datanumber = masterValue;
		  	String str = Float.toString(datanumber);
		  	textField.setText(str);
		  	return false;
		  }

		  if(datanumber>maximum)
		  {
			JOptionPane.showMessageDialog(null, "Value greater than maximum, setting back to Previous Value");
			datanumber = masterValue;
			String str = Float.toString(datanumber);
			textField.setText(str);
			return false;
		  }
			  
		  return false;
		}
	}
}
