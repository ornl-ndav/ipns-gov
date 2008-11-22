/*
 * File: ColorOptions.java
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class ColorOptions extends ActiveJPanel
{
	/**
	 * This class creates an ActiveJPanel...
	 * 
	 */
	public static final String COLOR_SCALE_CHANGED = "ColorScaleChanged";
	public static final String NUM_OF_COLORS_CHANGED = "NumberOfColorsChanged";
	private static final long serialVersionUID = 1L;
	private static final int NUM_COLORS = 100;
	
	JPanel 		colorOptionsPanel;
	JTextField  selectNumColors; /*Allows a user to select the number of 
    									colors used in the color scale.*/
	JComboBox   colorScale;      //Allows a user to select a color scale from a list.
	private colorOptionsListener colorOptionsListener = new colorOptionsListener();
	
	//public String[] colorScales;
	public String scaleSelected;
	public int numColors = NUM_COLORS;
	
	public ColorOptions()
	{
		this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		setBounds(20,20,300,200);
		
		buildColorOptionsPanel();
		this.add(colorOptionsPanel);
		//this.addActionListener(colorOptionsListener);
		//this.setVisible(true);
		
	}
	
	public void checkValue()
	{
		if(!selectNumColors.getText().equals(numColors))
		{
			
		}
	}
	
	/*
	 * Builds the color options panel;
	 * comprised of text field to specify the number of colors used in a color scale
	 * and combo box to select the color scale being used.
	 */
	private void buildColorOptionsPanel()
	{	
		colorOptionsPanel = new JPanel();
		colorOptionsPanel.setLayout(new BoxLayout(colorOptionsPanel,BoxLayout.X_AXIS));
		JPanel numColorsPanel = new JPanel();
		JPanel selectColorScalePanel = new JPanel();
		
		//colorOptionsPanel.setBorder(BorderFactory.createEtchedBorder());
		//colorOptionsPanel.add(new JLabel("Color Options Panel"));
		
		JLabel numColorsLabel = new JLabel("Number of Colors:");
		JLabel colorScaleLabel = new JLabel("Color Scale:");
		selectNumColors = new JTextField(3);
		selectNumColors.addActionListener(colorOptionsListener);
		selectNumColors.setText(""+numColors);
		selectNumColors.postActionEvent();
		colorScale = new JComboBox();
		colorScale.addActionListener(colorOptionsListener);
		colorScale.addItem(makeObj("Rainbow"));
		colorScale.addItem(makeObj("Gray"));
		colorScale.addItem(makeObj("Negative Gray"));
		colorScale.addItem(makeObj("Green-Yellow"));
		colorScale.addItem(makeObj("Heat 1"));
		colorScale.addItem(makeObj("Heat 2"));
		colorScale.addItem(makeObj("Optimal"));
		colorScale.addItem(makeObj("Multi"));
		colorScale.addItem(makeObj("Spectrum"));
		numColorsPanel.add(numColorsLabel);
		numColorsPanel.add(selectNumColors);
		selectColorScalePanel.add(colorScaleLabel);
		selectColorScalePanel.add(colorScale);
		colorOptionsPanel.add(numColorsPanel);
		colorOptionsPanel.add(selectColorScalePanel);
	}
	
	private Object makeObj(final String item)  
	{
		return new Object() 
		{ public String toString() 
			{ return item; } 
		};
	}
	 
	
	public static void main(String[]args)
	{
		JFrame test = new JFrame("ColorOptionsPanel");
		test.add(new ColorOptions());
		test.setBounds(20,20,300,200);
		test.setVisible(true);
	}
	
	public int getNumColors()
	{
		return numColors;
	}
	
	public String getColorScale()
	{
		return scaleSelected;
	}
	
	public void setNumColors(String number)
	{
		try
		{
			numColors = new Integer(number).intValue();
			if( numColors < 16 || numColors > 128 )
			{
				JOptionPane.showMessageDialog (null, "Enter a number of colors within the range 16-128. The number of colors has been reset to 100.", 
						"Number of Colors Field Error", JOptionPane.ERROR_MESSAGE);
				numColors = 100;
				selectNumColors.setText(""+100);
			}
		}
		catch(Exception ex)
		{
			JOptionPane.showMessageDialog (null, ex.getMessage(), "Number of Colors Field Error", JOptionPane.ERROR_MESSAGE);
			System.out.println(ex.getMessage());
			selectNumColors.setText(""+numColors);
		}
	}
	
	private class colorOptionsListener implements ActionListener
	{

		public void actionPerformed(ActionEvent e) 
		{
			if( e.getSource() == selectNumColors )
			{
				String number = selectNumColors.getText();
				setNumColors(number);
				System.out.println(numColors);
				System.out.println("num colors: "+numColors);
				send_message(NUM_OF_COLORS_CHANGED);
			}

			if ( e.getSource() == colorScale )
			{
				//int colors = colorScale.getSelectedIndex();
				scaleSelected = colorScale.getSelectedItem().toString();
				send_message(COLOR_SCALE_CHANGED);
				//System.out.println(COLOR_SCALE_MESSAGE);
				System.out.println(scaleSelected.toString());
				
			}
		}
	}
}
