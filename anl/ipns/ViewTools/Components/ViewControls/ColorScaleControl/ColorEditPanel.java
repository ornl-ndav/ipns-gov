/*
 * File: ColorEditPanel.java
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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.TitledBorder;


public class ColorEditPanel extends ActiveJPanel
{
	/**
	 * This class...
	 */
	private static final long serialVersionUID = 1L;
	
	private JPanel buttonPanel, scaleOptionsPanel, dataOptionsPanel, colorOptionsPanel,
	sliderPanel, colorScalePanel;
	private ColorPanel colorPanel;
	private ColorOptions colorOptions;
	private JButton doneButton, updateButton, cancelButton;
	private JRadioButton autoScale, specMinMax;
	private JCheckBox logCheck;  // gangCheck;
	private JTextField prescaleField, minField, maxField;
	private ButtonGroup buttonGroup = new ButtonGroup();
	private StretchTopBottom sliders;
	
	private ButtonListener buttonListener = new ButtonListener();
	private OptionsListener optionsListener = new OptionsListener();
	private ColorOptionsListener colorListener = new ColorOptionsListener();
	private StretcherListener stretcherListener = new StretcherListener();
	
	public static final String doneMessage = "DONE";
	public static final String updateMessage = "UPDATE";
	public static final String cancelMessage = "CANCEL";
	public static final String advancedMessage = "ADVANCED";
	
	private static final int SUBINTERVAL = 60000;
	private static final float MAX = 60000;       // AutoScale max
	private static final float MIN = 1;			  // AutoScale min
	//private float[] valueMapping;
	private byte[] initColorMap;
	private byte[] colorMapping;
	//private float[] initMap;
	private boolean logScale = false;
	//private boolean gang = false;
	private float scale = Float.NaN;
	private float max;
	private float min;
	private boolean flipped = false;
		
	
	public ColorEditPanel(float min, float max)
	{
		this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		setBounds(20,20,400,500);
		
		this.min = min;
		this.max = max;
		
		colorPanel = new ColorPanel();
		
		buildColorScalePanel();
		buildStretcherPanel();
		buildOptionsPanel();
		buildColorOptionsPanel();
		buildButtonPanel();
		colorPanel.setColorModel(colorOptions.getColorScale(), colorOptions.getNumColors(), true);
		colorPanel.setDataRange( min, max, true );
		
		this.add(colorScalePanel);
		this.add(sliderPanel);
		this.add(scaleOptionsPanel);
		this.add(dataOptionsPanel);
		this.add(colorOptionsPanel);
		this.add(buttonPanel);
		
		prescaleField.setText(""+scale);
		
		calculateMapping();
		
		//testing code//
		//scale = 20;
		//prescale(mapping);
		//logScale(mapping);
		//printMapping();
		//end testing //
	}
	
	/**
	 * Creates an initial mapping with the given SUBINTERVAL
	 */
	private void initMapping()
	{
		colorMapping = new byte[SUBINTERVAL];
		initColorMap = new byte[SUBINTERVAL];
		
		for( int i=0; i<SUBINTERVAL;i++)
		{
			initColorMap[i] = (byte)Math.round(i * (colorOptions.getNumColors() )/(SUBINTERVAL-1));
		}
		System.arraycopy(initColorMap,0,colorMapping,0,SUBINTERVAL);
	}
	
	/**
	 * Builds the color scale panel.
	 */
	private void buildColorScalePanel()
	{
		colorScalePanel = new JPanel();
		//JPanel labelPanel = new JPanel();
		colorScalePanel.setLayout(new BorderLayout());
		colorScalePanel.setBorder(new TitledBorder(" Effective Color Scale "));
		
		colorScalePanel.add(colorPanel);
	}
	
	/*
	 * Builds the panel to hold both the 'stretch bottom' and 'stretch top' sliders.
	 */
	private void buildStretcherPanel()
	{
		sliderPanel = new JPanel();
		//JPanel labelPanel = new JPanel();
		sliderPanel.setLayout(new GridLayout(1,1));
		sliderPanel.setBorder(new TitledBorder(" Thresholds "));
		
		sliders = new StretchTopBottom(min,max);
		sliders.addActionListener(stretcherListener);
		
		//sliderPanel.add(labelPanel,BorderLayout.CENTER);
		sliderPanel.add(sliders);
	}
	
	/*
	 * Builds the options panel; 
	 * comprised of text field to enter a pre-scale value
	 * and check boxes to toggle 'log scale' and 'gang sliders'
	 */
	private void buildOptionsPanel()
	{
		scaleOptionsPanel = new JPanel();
		dataOptionsPanel = new JPanel();
		JPanel prescalePanel = new JPanel();
		JLabel currentMax = new JLabel("Data Max: ");
		JLabel currentMin = new JLabel("Data Min: ");
		currentMax.setHorizontalAlignment(SwingConstants.RIGHT);
		currentMin.setHorizontalAlignment(SwingConstants.RIGHT);
		
		autoScale = new JRadioButton("Auto Scale");
		specMinMax = new JRadioButton("Specify Min/Max");
		buttonGroup.add(autoScale);
		buttonGroup.add(specMinMax);
		
		autoScale.setSelected(true);
		maxField = new JTextField();
		minField = new JTextField();
		maxField.setEditable(false);
		minField.setEditable(false);
		maxField.setText(""+max);
		minField.setText(""+min);

		scaleOptionsPanel.setLayout(new BoxLayout(scaleOptionsPanel,BoxLayout.Y_AXIS));
		dataOptionsPanel.setLayout(new GridLayout(3,2));
		scaleOptionsPanel.setBorder(new TitledBorder(" Scale Factor "));
		dataOptionsPanel.setBorder(new TitledBorder(" Data Range "));

		//Prescale Panel
		JLabel prescaleLabel = new JLabel("Prescale:");
		prescaleField = new JTextField(4);
		logCheck = new JCheckBox("Log Scale");
		//gangCheck = new JCheckBox("Gang");
		autoScale.addActionListener(optionsListener);
		prescaleField.addActionListener(optionsListener);
		logCheck.addActionListener(optionsListener);
		specMinMax.addActionListener(optionsListener);
		maxField.addActionListener(optionsListener);
		minField.addActionListener(optionsListener);
		//gangCheck.addActionListener(optionsListener);
		prescalePanel.add(prescaleLabel);
		prescalePanel.add(prescaleField);
		//prescalePanel.add("", new JPanel());
		prescalePanel.add("", new JPanel());
		prescalePanel.add("", new JPanel());
		prescalePanel.add("", new JPanel());
		//prescalePanel.add("", new JPanel());
		prescalePanel.add(logCheck);
		
		scaleOptionsPanel.add(prescalePanel,BorderLayout.NORTH);
		
		dataOptionsPanel.add(autoScale);
		dataOptionsPanel.add(specMinMax);
		dataOptionsPanel.add(currentMax);
		dataOptionsPanel.add(maxField);
		dataOptionsPanel.add(currentMin);
		dataOptionsPanel.add(minField);
	}

	/*
	 * Builds the color options panel;
	 * comprised of text field to specify the number of colors used in a color scale
	 * and combo box to select the color scale being used.
	 */
	private void buildColorOptionsPanel()
	{
		colorOptionsPanel = new JPanel();
		JPanel labelPanel = new JPanel();
		colorOptionsPanel.setLayout(new BoxLayout(colorOptionsPanel,BoxLayout.Y_AXIS));
		colorOptionsPanel.setBorder(new TitledBorder(" Color Options "));    //TitledBorder
		colorOptions = new ColorOptions();
		colorOptions.addActionListener(colorListener);
		
		//JLabel coOpt = new JLabel(" Color Options ");
		//coOpt.setBorder(BorderFactory.createLineBorder(Color.GRAY,2));
		//labelPanel.add(coOpt);
		colorOptionsPanel.add(labelPanel,BorderLayout.CENTER);
		colorOptionsPanel.add(colorOptions,BorderLayout.SOUTH);
	}
		
	/*
	 * Builds the button panel;
	 * comprised of 'Done','Update','Cancel','Advanced' buttons
	 */
	private void buildButtonPanel()
	{
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel,BoxLayout.X_AXIS));
		//buttonPanel.setBorder(BorderFactory.createEtchedBorder());
		
		doneButton = new JButton("Done");
		updateButton = new JButton("Update");
		cancelButton = new JButton("Cancel");
		
		doneButton.addActionListener(buttonListener);
		updateButton.addActionListener(buttonListener);
		cancelButton.addActionListener(buttonListener);
		
		buttonPanel.add(updateButton);
		buttonPanel.add(doneButton);
		buttonPanel.add(cancelButton);
		
	}
	
	/*
	 * Calculates mapping, determined by the options selected by user....
	 */
	private void calculateMapping()
	{
		float localMin = sliders.getBottomValue();
		float localMax = sliders.getTopValue();
		
		if(localMin<min) localMin = min;
		if(localMin>max) localMin = max;
		if(localMax>max) localMax = max;
		if(localMax<min) localMax = min;
		
		if(localMin > localMax)
		{
			flipped = true;
			float temp = localMin;
			localMin = localMax;
			localMax = temp;
		}
		
		System.out.println("localMin = "+localMin);
		System.out.println("localMax = "+localMax);
		
		initMapping();
		//printMapping();
		
		//-----------------------Log Scaling
		if(logScale)
		{
			System.out.println("logging");
			logScale(colorMapping,localMax,localMin);
		}
		
		if(flipped)
		{
			
			for(int i = 0; i < SUBINTERVAL-1; i++)
			{
				colorMapping[i] = (byte)( ( colorOptions.getNumColors() - 1 ) - colorMapping[i] );
			}
			
			flipped = false;
		}
		
		colorPanel.setColorTable(colorMapping, localMin, localMax, true);
		//printColorMapping();

	}
	
	/*
	 * Performs the log function on each value in the array....
	 */
	private void logScale(byte[] map, float valMax,float valMin)
	{		
		for ( int i = 0; i < SUBINTERVAL; i++ )
	    {
	      float x =valMin + i * (valMax - valMin) / 
	      		(SUBINTERVAL-1);
	      if ( x > 1 )
	        x = (float)Math.log10(x);
	      else
	        x = 0;

	      map[i] = (byte)((colorOptions.numColors) * 
	    		  (x - Math.log10(valMin)) / 
	    		  (Math.log10(valMax) - Math.log10(valMin)));
	    }
	}
	
	private void checkValues()
	{
		if(!maxField.getText().equals(""+max)||!minField.getText().equals(""+min))
		{
			setMax(maxField.getText());
			setMin(minField.getText());
			sliders.setMaxMin(max,min);
		}
		maxField.setText(""+max);
    minField.setText(""+min);
		//call sliderTopBottom check method
		
		//check preScale
		if(!prescaleField.getText().equals(scale))
		{
			setPrescale(prescaleField.getText());
		}
		
		//check num colors
		colorOptions.checkValue();
		
		System.out.println("min: "+ min);
		System.out.println("max: "+ max);
		System.out.println("prescale: "+ scale);
		System.out.println("num of colors: "+ colorOptions.numColors);
	}
	
	public int getNumColors()
	{
		return colorOptions.getNumColors();
	}
	
	public Object getColorScale()
	{
		return colorOptions.getColorScale();
	}
	
	public float getMin()
	{
		return min;
	}
	
	public void setMax(float m)
  {
    max = m;
    colorPanel.setDataRange(min, max, true);
  }
  
	public void setMin(float m)
	{
		min = m;
		colorPanel.setDataRange(min, max, true);
	}
	
	public void setMax(String m)
	{
		try
		{
		  float maxTemp = new Float(m).floatValue();
		  if(maxTemp>min)
			{
		    setMax(maxTemp);
			}
		  colorPanel.setDataRange(min, max, true);
			//maxField.setText(""+max);
		}
		catch(Exception ex)
		{
			JOptionPane.showMessageDialog (null, ex.getMessage(), "Edit Max Value Field Error", JOptionPane.ERROR_MESSAGE);
			System.out.println(ex.getMessage());
			maxField.setText(""+max);
		}	
	}
	
	public void setMin(String m)
	{
		try
		{
		  float minTemp = new Float(m).floatValue();
		  if(minTemp<max)
      {
		    setMin(minTemp);
      }
		  colorPanel.setDataRange(min, max, true);
			//minField.setText(""+min);
		}
		catch(Exception ex)
		{
			JOptionPane.showMessageDialog (null, ex.getMessage(), "Edit Min Value Field Error", JOptionPane.ERROR_MESSAGE);
			System.out.println(ex.getMessage());
			minField.setText(""+min);
		}
	}
	
	public void setPrescale(String val)
	{
		try
		{
			scale = new Float(val).floatValue();
		}
		catch(Exception ex)
		{
			JOptionPane.showMessageDialog (null, ex.getMessage(), "Scale Field Error", JOptionPane.ERROR_MESSAGE);
			System.out.println(ex.getMessage());
			prescaleField.setText(""+scale);
		}
	}
	
	public float getMax()
	{
		return max;
	}
	
	public float getPrescale()
	{
		return scale;
	}
	
	public void setPrescale(float s)
	{
		scale = s;
	}
	
	public float getAutoMax()
	{
		return MAX;
	}
	
	public float getAutoMin()
	{
		return MIN;
	}
	
	public byte[] getColorMapping()
	{
		return colorMapping;
	}
	
	public void setLogScale(boolean log)
	{
		logScale = log;
		logCheck.setSelected(log);
	}
	
	/*public void setGang(boolean gang)
	{
		this.gang = gang;
		gangCheck.setSelected(gang);
	}*/
	
	public void printMapping()
	{
		for (int i=0; i<100;i++)
		{
			String lineOut = "";
			for( int j=0; j<100; j++)
			{
				lineOut+= colorMapping[i*600+6*j] + " ";
			}
			System.out.println(lineOut);
		}
	}
	
	public void printColorMapping()
	{
		for (int i=0; i<100;i++)
		{
			String lineOut = "";
			for( int j=0; j<100; j++)
			{
				lineOut+= colorMapping[i*600+6*j] + " ";
			}
			System.out.println(lineOut);
		}
	}

	public static void main(String[]args)
	{
		JFrame blah = new JFrame("ColorEditPanel");
		ColorEditPanel test = new ColorEditPanel(1,5000);
		blah.add(test);
		blah.setBounds(20,20,400,500);	
		blah.setVisible(true);
		//test.setLogScale(true);
		//test.setGang(true);
		//test.updateButton.doClick();
		//test.printMapping();
	}
	
	private class ButtonListener implements ActionListener
	{

		public void actionPerformed(ActionEvent e) 
		{
			if( e.getSource() == doneButton )
			{
				send_message(doneMessage);
				calculateMapping();
				System.out.println(doneMessage);
			}
			
			if( e.getSource() == updateButton )
			{
				checkValues();
				send_message(updateMessage);
				calculateMapping();
				System.out.println(updateMessage);
				//printMapping();
			}
			
			if( e.getSource() == cancelButton )
			{
				send_message(cancelMessage);
				System.out.println(cancelMessage);
			}
			
		}
		
	}
	
	private class OptionsListener implements ActionListener
	{

		public void actionPerformed(ActionEvent e) 
		{
			if( e.getSource() == logCheck )
			{
				if (((JCheckBox)e.getSource()).isSelected())
				{
				  //if(max<1) max = 1;
	        //if(min<1) min = 1;
	        //if(max == min) max++;
	        //maxField.setText(""+max);
	        //minField.setText(""+min);
				  logScale = true;
				}
				else
					logScale = false;
				checkValues();
				//System.out.println(logScale);
			}
			
			else if(e.getSource() == autoScale)
			{
				if (((JRadioButton)e.getSource()).isSelected())
				{
					autoScale.setSelected(true);
					specMinMax.setSelected(false);
					setMax(MAX);
					setMin(MIN);
					maxField.setEditable(false);
					minField.setEditable(false);
					maxField.setText(""+MAX);
					minField.setText(""+MIN);
				}
				sliders.setMaxMin(max,min);
				System.out.println("autoScale "+autoScale.isSelected());
				//System.out.println("specMinMax "+specMinMax.isSelected());
			}
			
			else if(e.getSource() == specMinMax)
			{
				if (((JRadioButton)e.getSource()).isSelected())
				{
					specMinMax.setSelected(true);
					autoScale.setSelected(false);
					maxField.setEditable(true);
					minField.setEditable(true);
				}
				System.out.println("specMinMax "+specMinMax.isSelected());
				//System.out.println("autoScale "+autoScale.isSelected());	
			}
			
			
			if(e.getSource() == maxField )
			{
				String maxValue = maxField.getText();
				setMax(maxValue);
				if(max<1&&logScale) max = 1;
				maxField.setText(""+max);
				sliders.setMaxMin(max,min);
				System.out.println(max);
			}
			
			else if(e.getSource() == minField )
			{
				String minValue = minField.getText();
				setMin(minValue);
				
				if(min<1&&logScale) min = 1;
				minField.setText(""+min);
				sliders.setMaxMin(max,min);
				System.out.println(min);
			}
			
			else if( e.getSource() == prescaleField )
			{
				String value = prescaleField.getText();
				setPrescale(value);
					System.out.println(scale);
			}
			calculateMapping();
		}
	}
	
	private class ColorOptionsListener implements ActionListener
	{

		public void actionPerformed(ActionEvent e) 
		{
			System.out.println(e.getActionCommand());
			if(e.getActionCommand().equals(ColorOptions.COLOR_SCALE_CHANGED))
			{
				System.out.println("setColorModel: ColorScaleChanged to "+colorOptions.getColorScale());
				colorPanel.setColorModel(colorOptions.getColorScale(), colorOptions.getNumColors(), true);
			}
			
			else if(e.getActionCommand().equals(ColorOptions.NUM_OF_COLORS_CHANGED))
			{
				System.out.println("setColorModel: NumOfColorsChanged to "+ colorOptions.getNumColors());
				colorPanel.setColorModel(colorOptions.getColorScale(), colorOptions.getNumColors(), true);
				calculateMapping();
			}
		}
		
	}
	
	private class StretcherListener implements ActionListener
	{

		public void actionPerformed(ActionEvent e) {
			calculateMapping();
			System.out.println("Max: "+sliders.getTopValue());
			System.out.println("Min: "+sliders.getBottomValue());
		}
		
	}
	
}
