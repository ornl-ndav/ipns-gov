
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

import gov.anl.ipns.ViewTools.Components.ObjectState;
import gov.anl.ipns.ViewTools.Components.ViewControls.ViewControl;
import gov.anl.ipns.ViewTools.UI.ActiveJPanel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.TitledBorder;


public class ColorEditPanel extends ViewControl
{
	/* (non-Javadoc)
    * @see gov.anl.ipns.ViewTools.Components.ViewControls.ViewControl#copy()
    */
   @Override
   public ViewControl copy() {

      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see gov.anl.ipns.ViewTools.Components.ViewControls.ViewControl#getControlValue()
    */
   @Override
   public Object getControlValue() {

      
      return getObjectState( false );
   }

   /* (non-Javadoc)
    * @see gov.anl.ipns.ViewTools.Components.ViewControls.ViewControl#getObjectState(boolean)
    */
   @Override
   public ObjectState getObjectState( boolean isDefault ) {

           ObjectState state =  super.getObjectState( isDefault );
           if( isDefault){
              state.insert( MAXSET, 60000 );
              state.insert(MINSET,1 );
              state.insert(NUM_COLORS ,100);
              state.insert(COLOR_INDEX_CHOICE, "Rainbow" );
              state.insert(LOGSCALE ,false);
              state.insert( SLIDERS+"."+ StretchTopBottom.GANG,false);
              ObjectState Slider = new ObjectState();
              state.insert(SLIDERS , Slider );
              Slider.insert(StretchTopBottom.MAX ,60000f);
              Slider.insert(  StretchTopBottom.MIN ,1f);
              Slider.insert( StretchTopBottom.BOTTOM_VALUE,1f);
              Slider.insert( StretchTopBottom.TOP_VALUE,60000f);
              Slider.insert( StretchTopBottom.INTERVAL_MAX_BOTTOM ,6000f);
              Slider.insert( StretchTopBottom.INTERVAL_MIN_BOTTOM ,1);
              Slider.insert( StretchTopBottom.INTERVAL_MAX_TOP ,60000);
              Slider.insert( StretchTopBottom.INTERVAL_MAX_TOP ,1);
              state.insert(PRESCALE,1f );
              state.insert( AUTO_SCALE,true );
              
           }else{
              state.insert( MAXSET, max );
              state.insert(MINSET,min );
              state.insert(NUM_COLORS ,getNumColors());
              state.insert(COLOR_INDEX_CHOICE, colorOptions.getColorScale() );
              state.insert(LOGSCALE ,logScale);
              ObjectState Slider = new ObjectState();
              state.insert(SLIDERS , Slider );
              Slider.insert( StretchTopBottom.GANG, sliders.getGang());

              Slider.insert(  StretchTopBottom.MAX ,sliders.getMaximum());
              Slider.insert(  StretchTopBottom.MIN , sliders.getMinimum());
              Slider.insert( StretchTopBottom.BOTTOM_VALUE,1);
              Slider.insert( StretchTopBottom.INTERVAL_MAX_BOTTOM ,6000f);
              Slider.insert( StretchTopBottom.INTERVAL_MIN_BOTTOM ,1);
              Slider.insert( StretchTopBottom.INTERVAL_MAX_TOP ,60000);
              Slider.insert( StretchTopBottom.INTERVAL_MAX_TOP ,1);
              state.insert(PRESCALE,getPrescale() );
              state.insert( AUTO_SCALE,autoScale.isSelected() );
              
           }
           return state;
   }

   /* (non-Javadoc)
    * @see gov.anl.ipns.ViewTools.Components.ViewControls.ViewControl#getTitle()
    */
   @Override
   public String getTitle() {

      // TODO Auto-generated method stub
      return super.getTitle();
   }

   /* (non-Javadoc)
    * @see gov.anl.ipns.ViewTools.Components.ViewControls.ViewControl#setControlValue(java.lang.Object)
    */
   @Override
   public void setControlValue( Object value ) {

      if( value == null)
         return;
      if( !(value instanceof ObjectState))
         return;
      ObjectState state = (ObjectState) value;
      setControlValue( state.get( MAXSET ),MAXSET );
      setControlValue( state.get( MINSET ),MINSET );
      setControlValue( state.get( NUM_COLORS ),NUM_COLORS );
      setControlValue( state.get( COLOR_INDEX_CHOICE ),COLOR_INDEX_CHOICE );
      setControlValue( state.get( LOGSCALE ),LOGSCALE );
      ObjectState sliderState =  (ObjectState)state.get( SLIDERS );
           setControlValue( sliderState.get( StretchTopBottom.MAX),
                                           SLIDERS+"."+StretchTopBottom.MAX);
     
           setControlValue( sliderState.get( StretchTopBottom.MIN),
                                           SLIDERS+"."+StretchTopBottom.MIN);
      
           setControlValue( sliderState.get( StretchTopBottom.INTERVAL_MAX_BOTTOM),
                                           SLIDERS+"."+StretchTopBottom.INTERVAL_MAX_BOTTOM);
     
           setControlValue( sliderState.get( StretchTopBottom.INTERVAL_MAX_TOP),
                                           SLIDERS+"."+StretchTopBottom.INTERVAL_MAX_TOP);
     
           setControlValue( sliderState.get( StretchTopBottom.INTERVAL_MIN_TOP),
                                           SLIDERS+"."+StretchTopBottom.INTERVAL_MIN_TOP);
     
           setControlValue( sliderState.get( StretchTopBottom.INTERVAL_MIN_BOTTOM),
                                           SLIDERS+"."+StretchTopBottom.INTERVAL_MIN_BOTTOM);
     
           setControlValue( sliderState.get( StretchTopBottom.TOP_VALUE),
                                           SLIDERS+"."+StretchTopBottom.TOP_VALUE);

           
           setControlValue( sliderState.get( StretchTopBottom.BOTTOM_VALUE),
                                           SLIDERS+"."+StretchTopBottom.BOTTOM_VALUE);
     
     
           setControlValue( sliderState.get( StretchTopBottom.GANG),
                                           SLIDERS+"."+StretchTopBottom.GANG);
          
      setControlValue( state.get( PRESCALE ),PRESCALE );
      setControlValue( state.get( AUTO_SCALE ),AUTO_SCALE );
      
      
   }
   /**
    * Will set the appropriate part of GUI Component and the internal value 
    * for the control
    * @param value  the new value for the  control
    * @param key    the key for the part of this ViewControl.
    */
   public void setControlValue( Object value, String key){
      if( value == null || key== null)
         return;
      if( key == MAXSET ){
         if(!( value instanceof Number))
            return;
         float val = ((Number)value).floatValue();
         maxField.setText( ""+val );
         setMax( val);
         sliders.setMaxMin( min , max );
         
      }else if( key == MINSET){
         if(!( value instanceof Number))
            return;
         float val = ((Number)value).floatValue();
         minField.setText( ""+val );
         setMin( val );
         sliders.setMaxMin( min , max );
         
      }else if( key ==NUM_COLORS ){
         if(!( value instanceof Number))
            return;
         int ncolors = ((Number)value).intValue();
         colorOptions.changeNumColors( ncolors );
         
      }else if( key == COLOR_INDEX_CHOICE ){
         if( value instanceof String){
            colorOptions.setColor( value.toString());
            colorPanel.setColorModel(colorOptions.getColorScale(), colorOptions.getNumColors(), true);
         }
         
      }else if( key == LOGSCALE ){
         if( value instanceof Boolean)
            if(((Boolean)value).booleanValue()){
               logCheck.setSelected( true );
               setLogScale( true );
            }else{
               logCheck.setSelected( false );
               setLogScale( false );
               
            }
         
         
      }else if( key.startsWith( SLIDERS+"." )){
         sliders.setControlValue( value, key.substring(9));
         
      }else if( key == PRESCALE ){
   
         
         if( value instanceof Number){
            float val =( ((Number)value).floatValue());
            prescaleField.setText( ""+val );
         }
         
      }else if( key == AUTO_SCALE ){
         
         if( value instanceof Boolean)
            if(((Boolean)value).booleanValue())
               autoScale.setSelected( true );
            else
               autoScale.setSelected( false );
               
      }
   }
   
   public Object getControlValue( String key){
      if(key== null)
         return null;
      if( key == MAXSET ){
         return max;
      }else if( key == MINSET){
          return min;
      }else if( key ==NUM_COLORS ){
         
      }else if( key == COLOR_INDEX_CHOICE ){
         
      }else if( key == LOGSCALE ){
         
      }else if( key ==SLIDERS){
         
      }else if( key == PRESCALE ){
         
      }else if( key == AUTO_SCALE ){
         
      }
      return null;
   }

   /* (non-Javadoc)
    * @see gov.anl.ipns.ViewTools.Components.ViewControls.ViewControl#setObjectState(gov.anl.ipns.ViewTools.Components.ObjectState)
    */
   @Override
   public void setObjectState( ObjectState new_state ) {

      
      super.setObjectState( new_state );
      setControlValue( new_state);
   }

   /* (non-Javadoc)
    * @see gov.anl.ipns.ViewTools.Components.ViewControls.ViewControl#setTitle(java.lang.String)
    */
   @Override
   public void setTitle( String control_title ) {

      // TODO Auto-generated method stub
      super.setTitle( control_title );
   }

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
	public static String TITLE ="Color Edit Panel";	
	public static String NUM_COLORS ="Number of Colors";
   public static String LOGSCALE ="Use Log Scale";
   public static String PRESCALE ="Prescale factor";
   public static String MINSET ="Set Minimum";
   public static String MAXSET ="Set Maximum";
   public static String AUTO_SCALE ="Automatic scale";
   public static String SLIDERS ="Sliders";
   public static String COLOR_INDEX_CHOICE ="Color Model";
   
   
   
      ;
   
	
	public ColorEditPanel(){
	   this( 0f,100f);
	}
	public ColorEditPanel(float min, float max)
	{  
	   super(  TITLE );
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
		
		for( int i=0; i<SUBINTERVAL-1;i++)
		{
			initColorMap[i] = (byte)Math.round(i * (colorOptions.getNumColors() )/(SUBINTERVAL-1));
		}
                initColorMap[ SUBINTERVAL-1 ] = initColorMap[ SUBINTERVAL-2 ];     
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
 	    for ( int i = 0; i < SUBINTERVAL-1; i++ )
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
            map[ SUBINTERVAL - 1 ] = map[ SUBINTERVAL - 2 ];
	}
	
   private boolean checkMaxMinValues(){
      
     
      if(!maxField.getText().equals(""+max)||!minField.getText().equals(""+min))
      {
         setMax(maxField.getText());
         setMin(minField.getText());
         sliders.setMaxMin(max,min);
         maxField.setText(""+max);
         minField.setText(""+min);
         return true;
      }  
      return false;
   }
   
   
   
	private void checkValues()
	{
		boolean b =checkMaxMinValues();
		//call sliderTopBottom check method
		
		//check preScale
		if(!prescaleField.getText().equals(scale))
		{
			setPrescale(prescaleField.getText());
			
		}
		
		//check num colors
		b = b || colorOptions.checkValue();
		
		System.out.println("min: "+ min);
		System.out.println("max: "+ max);
		System.out.println("prescale: "+ scale);
		System.out.println("num of colors: "+ colorOptions.numColors);
		boolean b1 = sliders.checkValues();
		b = b || b1;
		
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
		JMenuBar jmb = blah.getJMenuBar();
		if(jmb == null){
		   jmb = new JMenuBar();
		   blah.setJMenuBar( jmb );
		}
		JMenu jmen= new JMenu("File");
		jmb.add(  jmen );
		JMenuItem jmi = new JMenuItem("Load Object State");
		jmen.add(  jmi );
		mActionListener mact =   new mActionListener( test);
		jmi.addActionListener( mact );
      jmi = new JMenuItem("Save Object State");
      jmen.add(  jmi );
      jmi.addActionListener(  mact );
		
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
			   checkValues();
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
			{  checkValues();
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
			{  checkValues();
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
				checkValues();
				if(max<1&&logScale) max = 1;
				maxField.setText(""+max);
				sliders.setMaxMin(max,min);
				System.out.println(max);
			}
			
			else if(e.getSource() == minField )
			{
				String minValue = minField.getText();
				setMin(minValue);
				checkValues();
				if(min<1&&logScale) min = 1;
				minField.setText(""+min);
				sliders.setMaxMin(max,min);
				System.out.println(min);
			}
			
			else if( e.getSource() == prescaleField )
			{  
			   checkValues();
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
			   checkValues();
				System.out.println("setColorModel: ColorScaleChanged to "+colorOptions.getColorScale());
				colorPanel.setColorModel(colorOptions.getColorScale(), colorOptions.getNumColors(), true);
			}
			
			else if(e.getActionCommand().equals(ColorOptions.NUM_OF_COLORS_CHANGED))
			{  checkValues();
				System.out.println("setColorModel: NumOfColorsChanged to "+ colorOptions.getNumColors());
				colorPanel.setColorModel(colorOptions.getColorScale(), colorOptions.getNumColors(), true);
				calculateMapping();
			}
		}
		
	}
	
	private class StretcherListener implements ActionListener
	{

		public void actionPerformed(ActionEvent e) {
		   checkValues();
			calculateMapping();
			System.out.println("Max: "+sliders.getTopValue());
			System.out.println("Min: "+sliders.getBottomValue());
		}
		
	}
	
}
class mActionListener implements ActionListener{
   ColorEditPanel t;
   public mActionListener( ColorEditPanel t){
      this.t = t;
   }
   public void actionPerformed( ActionEvent evt){
      ObjectState state = t.getObjectState( false );
      if( evt.getActionCommand().startsWith( "Save" )){
         state.openFileChooser( true);
      }else{
         state.openFileChooser( false );
         t.setObjectState( state);
      }
   }
}