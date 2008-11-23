/*
 * File: StretchTopBottom.java
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
 * The Class StretchTopBottom.
 * 
 * Creates two stretcher objects that can be ganged together
 * so they move together and updates both the stretchers depending on
 * whether they change the stretcher or textbox.
 */
public class StretchTopBottom extends ActiveJPanel
{
	
	/** The minimum data range. */
	private float minimum;
	
	/** The maximum data range. */
	private float maximum;
	
	/** The minimum interval. */
	private float minInterval = 0;
	
	/** The maximum interval. */
	private float maxInterval = 0;
	
	/** The bottom stretcher. */
	private Stretcher bottomStretch;
	
	/** The top stretcher. */
	private Stretcher topStretch;
	
	/** The top stretcher text value. */
	private float topTextValue;
	
	/** The bottom stretcher text value. */
	private float bottomTextValue;
	
	/** True - Stretchers ganged. */
	private boolean gang = false;
	
	/** Gang Checkbox. */
	private JCheckBox gangCheck;
	

	/**
	 * Instantiates a new stretch top bottom.
	 * 
	 * @param min The minimum data range
	 * @param max the maximum data range
	 */
	public StretchTopBottom(float min, float max)
	{
		maximum = max;
		minimum = min;
		topStretch = new Stretcher(min, max, 100);
		bottomStretch = new Stretcher(min, max, 0);
		gangCheck = new JCheckBox("Gang");

		topStretch.addActionListener(topAction);
		bottomStretch.addActionListener(bottomAction);
		gangCheck.addActionListener(button);

		this.setLayout(new GridLayout(3,1));
		add(topStretch);
		add(bottomStretch);
		add(gangCheck);

		topTextValue = topStretch.getValue();
		bottomTextValue = bottomStretch.getValue();
		setGang( false );
	}
	
	/**
	 * Sets the gang.
	 * 
	 * @param Gang true - set intervals, false - clears intervals to zero.
	 */
	public void setGang(boolean Gang)
	{
		gang = Gang;
		
		if(gang)
		{
			if(bottomStretch.getValue() < topStretch.getValue())
			{
				minInterval = bottomStretch.getValue() - minimum;
				maxInterval = maximum - topStretch.getValue();
			}
			if(topStretch.getValue() < bottomStretch.getValue())
			{
				minInterval = topStretch.getValue()- minimum;
				maxInterval = maximum - bottomStretch.getValue();
			}
			if(topStretch.getValue() == bottomStretch.getValue())
			{
				JOptionPane.showMessageDialog(null, "Error: Stretch Top and Stretch Bottom values can not be equal when ganged");
				gang = false;
				gangCheck.setSelected(false);
				return;
			}
			bottomStretch.setInterval( bottomStretch.getValue() - minInterval, 
			                           bottomStretch.getValue() + maxInterval);
			topStretch.setInterval( topStretch.getValue() - minInterval, 
			                        topStretch.getValue() + maxInterval);
			//System.out.println(bottomStretch.getValue() - minInterval + minimum);
			//System.out.println(bottomStretch.getValue() + maxInterval);
		}
		else
		{
			minInterval = 0;
			maxInterval = 0;
			bottomStretch.setInterval(minimum, maximum);
			topStretch.setInterval(minimum, maximum);
		}
	}
	
	/**
	 * Gets the minimum.
	 * 
	 * @return the minimum data range
	 */
	public float getMinimum()
	{
		return minimum;
	}
	
	/**
	 * Gets the maximum.
	 * 
	 * @return the maximum data range
	 */
	public float getMaximum()
	{
		return maximum;
	}
	
	/**
	 * Gets the top value.
	 * 
	 * @return the top stretcher value
	 */
	public float getTopValue()
	{

		if(topStretch.getValue() != bottomStretch.getValue())
			return topStretch.getValue();
		else
			return topStretch.getValue() + ((maximum - minimum)/1000);
	}
	
	/**
	 * Gets the bottom value.
	 * 
	 * @return the bottom stretcher value
	 */
	public float getBottomValue()
	{

		if(topStretch.getValue() != bottomStretch.getValue())
			return bottomStretch.getValue();
		else
			return bottomStretch.getValue() - ((maximum-minimum)/1000);
	}
	
	/**
	 * Set the maximum and minimum for both the topstretcher and bottomstretcher.
	 * 
	 * @param newmax
	 * @param newmin
	 */
	public void setMaxMin(float newmax, float newmin)
	{
		maximum = newmax;
		minimum = newmin;
		bottomStretch.setMaxMin(maximum, minimum, 0);
		topStretch.setMaxMin(maximum, minimum, 100);
		topTextValue = topStretch.getValue();
		bottomTextValue = bottomStretch.getValue();
	}
	
	/**
	 * Sets the other value.
	 * 
	 * @param offset Amount changed of value
	 * @param isTop True - is top so set bottom stretcher, false - vice versa
	 */
	private void setOtherValue(float offset, int isTop)
	{
		float offSet = offset;
		if(gang)
		{
			if(isTop == 1)
			{
				bottomStretch.setValue(bottomStretch.getValue()+offSet);
				topTextValue = topStretch.getValue();
				bottomTextValue = bottomStretch.getValue();
			}
			else if(isTop == 0)
			{
				topStretch.setValue(topStretch.getValue()+offSet);
				bottomTextValue = bottomStretch.getValue();
				topTextValue = topStretch.getValue();
			}
		}
	}
	
	/**
	 *   Determines if the text box 
	 */
	public boolean checkValues(){
	   boolean b = topStretch.checkValues();
	   b = b || bottomStretch.checkValues();
	   return b;
	   
	}
	
	/** The ActionListener for the top stretcher. */
	private ActionListener topAction = new ActionListener()
	{
		public void actionPerformed(ActionEvent e)
		{
			setOtherValue(topStretch.getValue()-topTextValue, 1);
			topTextValue = topStretch.getValue();
			send_message("Top Value Changed");
		}
	};
	
	/** The ActionListener for the bottom stretcher. */
	private ActionListener bottomAction = new ActionListener()
	{
		public void actionPerformed(ActionEvent e)
		{
			setOtherValue(bottomStretch.getValue()-bottomTextValue, 0);
			bottomTextValue = bottomStretch.getValue();
			send_message("Bottom Value Changed");
		}
	};
	
	/** ActionListener for Gang Checkbox. */
	private ActionListener button = new ActionListener()
	{
		public void actionPerformed(ActionEvent event) 
		{
			if(event.getActionCommand() == "Gang")
			{
				if(gang)
				{
					gang = false;
				}
				else
				{
					gang = true;
				}
				setGang(gang);
			}
		}
	};
	 

}
