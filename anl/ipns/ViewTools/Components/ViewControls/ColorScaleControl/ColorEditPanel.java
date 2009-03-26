
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
   
   private  int SUBINTERVAL = 256;
   private  float MAX = 60000;       // AutoScale max
   private  float MIN = 1;         // AutoScale min
   //private float[] valueMapping;
   private byte[] initColorMap;
   private byte[] colorMapping;
   //private float[] initMap;
   private boolean logScale = false;
   //private boolean gang = false;
   private float scale = 1;
   private float max;
   private float min;
   private float localMax;
   private float localMin;
   private boolean flipped = false;
   
   //Saved previous TextField values
   String Sav_prescaleField, 
          Sav_minField, 
          Sav_maxField;
   
   //ObjectState keys
   public static String TITLE ="Color Edit Panel"; 
   public static String NUM_COLORS ="Number of Colors";
   public static String LOGSCALE ="Use Log Scale";
   public static String PRESCALE ="Prescale factor";
   public static String MINSET ="Set Minimum";
   public static String MAXSET ="Set Maximum";
   public static String AUTO_SCALE ="Automatic scale";
   public static String SLIDERS ="Sliders";
   public static String COLOR_INDEX_CHOICE ="Color Model";
   
   
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
      MAX = max;
      MIN = min;
      colorPanel = new ColorPanel();
      
      buildColorScalePanel();
      buildStretcherPanel();
      buildOptionsPanel();
      buildColorOptionsPanel();
      buildButtonPanel();
      colorPanel.setColorModel(colorOptions.getColorScale(), colorOptions.getNumColors(), true);
      colorPanel.setDataRange( min, max,logScale, true );
      
      this.add(colorScalePanel);
      this.add(sliderPanel);
      this.add(scaleOptionsPanel);
      this.add(dataOptionsPanel);
      this.add(colorOptionsPanel);
      this.add(buttonPanel);
      
      prescaleField.setText(""+scale);
      Sav_prescaleField= prescaleField.getText();
      calculateMapping();
      
      //testing code//
      //scale = 20;
      //prescale(mapping);
      //logScale(mapping);
      //printMapping();
      //end testing //
   }
   
	/* (non-Javadoc)
    * @see gov.anl.ipns.ViewTools.Components.ViewControls.ViewControl#copy()
    */
   @Override
   public ViewControl copy() {

      ObjectState state = getObjectState( false );
      ColorEditPanel Res = new ColorEditPanel( min, max );
      Res.setObjectState(  state  );
      return Res;
       
   }

   /* (non-Javadoc)
    * @see gov.anl.ipns.ViewTools.Components.ViewControls.ViewControl#getControlValue()
    */
   @Override
   public Object getControlValue() 
   {
     int     num_colors = colorOptions.getNumColors();
     String  cs_name    = colorOptions.getColorScale();
     boolean two_sided  = false;

     return new ColorScaleInfo( localMin, 
                                localMax, 
                                scale, 
                                cs_name, 
                                two_sided,
                                num_colors,
                                colorMapping,
                                logScale );
   }

   /* (non-Javadoc)
    * @see gov.anl.ipns.ViewTools.Components.ViewControls.ViewControl#getObjectState(boolean)
    */
   @Override
   public ObjectState getObjectState( boolean isDefault ) {

           ObjectState state =  super.getObjectState( isDefault );
           if( isDefault){
              state.insert( MAXSET, MAX );
              state.insert(MINSET,MIN );
              state.insert(NUM_COLORS ,100);
              state.insert(COLOR_INDEX_CHOICE, "Rainbow" );
              state.insert(LOGSCALE ,false);
              state.insert( SLIDERS+"."+ StretchTopBottom.GANG,false);
              ObjectState Slider = new ObjectState();
              state.insert(SLIDERS , Slider );
              Slider.insert(StretchTopBottom.MAXMIN ,makeArray(6000f,1f));
              Slider.insert( StretchTopBottom.BOTTOM_VALUE,1f);
              Slider.insert( StretchTopBottom.TOP_VALUE,60000f);
              Slider.insert( StretchTopBottom.INTERVAL_MAXMIN_BOTTOM ,
                                                      makeArray(6000f,1f));
              Slider.insert( StretchTopBottom.INTERVAL_MAXMIN_TOP ,
                                                      makeArray(6000f,1f));
              state.insert(PRESCALE,1f );
              state.insert( AUTO_SCALE, false );
              
           }else{
              state.insert( MAXSET, max );
              state.insert(MINSET,min );
              state.insert(NUM_COLORS ,getNumColors());
              state.insert(COLOR_INDEX_CHOICE, colorOptions.getColorScale() );
              state.insert(LOGSCALE ,logScale);
              ObjectState Slider = new ObjectState();
              state.insert(SLIDERS , Slider );
              Slider.insert( StretchTopBottom.GANG, sliders.getGang());

              Slider.insert(  StretchTopBottom.MAXMIN ,
                         makeArray(sliders.getMaximum(),sliders.getMinimum()));
              Slider.insert( StretchTopBottom.INTERVAL_MAXMIN_BOTTOM ,
                       sliders.getControlValue( StretchTopBottom.
                                INTERVAL_MAXMIN_BOTTOM ));
              Slider.insert( StretchTopBottom.INTERVAL_MAXMIN_TOP ,
                       sliders.getControlValue( StretchTopBottom.
                                INTERVAL_MAXMIN_TOP));
              Slider.insert( StretchTopBottom.BOTTOM_VALUE,
                       sliders.getControlValue( StretchTopBottom.BOTTOM_VALUE ));
              Slider.insert( StretchTopBottom.TOP_VALUE,
                          sliders.getControlValue( StretchTopBottom.TOP_VALUE ));
              state.insert(PRESCALE,getPrescale() );
              state.insert( AUTO_SCALE,autoScale.isSelected() );
              
           }
           return state;
   }
   
   private float[] makeArray( float v1, float v2){
      float[] Res = new float[2];
      Res[0]=v1;
      Res[1]=v2;
      return Res;
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
      
      if( value == null )
         return;
      if( !(value instanceof ColorScaleInfo))
         return;
      
      ColorScaleInfo colInf = (ColorScaleInfo)value;
      setControlValue( colInf.getNumColors() , NUM_COLORS );
      setControlValue( colInf.getColorScaleName() , COLOR_INDEX_CHOICE ); 
      setControlValue( colInf.isLog(), LOGSCALE);
      setControlValue(colInf.getTableMin(), 
                    SLIDERS+"."+StretchTopBottom.BOTTOM_VALUE);
      setControlValue(colInf.getTableMax(), 
               SLIDERS+"."+StretchTopBottom.TOP_VALUE);
      
      checkValues();
      calculateMapping();
      
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
         if( val != max)
            setMaxMin( val, min);
        // setMax( val);
        // sliders.setMaxMin( min , max );
         
      }else if( key == MINSET){
         if(!( value instanceof Number))
            return;
         float val = ((Number)value).floatValue();
        if( val != min )
           setMaxMin( max, val);
        // setMin( val );
        // sliders.setMaxMin( min , max );
         
      }else if( key ==NUM_COLORS ){
         if(!( value instanceof Number))
            return;
         int ncolors = ((Number)value).intValue();
        if( ncolors != colorOptions.getNumColors())
         {
            colorOptions.setNumColors( ncolors , false);
            colorPanel.setColorModel(colorOptions.getColorScale(), 
                                           colorOptions.getNumColors(), true);
            calculateMapping();
         }
         
      }else if( key == COLOR_INDEX_CHOICE ){
         if( value instanceof String && !(value.equals(colorOptions.getColorScale())))
         {
            colorOptions.setColor( value.toString());
            colorPanel.setColorModel(colorOptions.getColorScale(), 
                                        colorOptions.getNumColors(), true);
         }
         
      }else if( key == LOGSCALE ){
         if( value instanceof Boolean )
            if( ((Boolean)value).booleanValue() != logScale)
            setLogCheck( ((Boolean)value).booleanValue());
         
         
      }else if( key.startsWith( SLIDERS+"." )){
         sliders.setControlValue( value, key.substring(8));
         
      }else if( key == PRESCALE ){
         
         if( value instanceof Number){
            float new_scale =( ((Number)value).floatValue());
           if( new_scale != scale)
            {
               scale = new_scale;
            
               prescaleField.setText( ""+scale );

               Sav_prescaleField= prescaleField.getText();
            }
         }
         
      }else if( key == AUTO_SCALE ){
         
         if( value instanceof Boolean )// need to chek on GUI element autoscale which
                                       // has no variable for its value.
            setAutoScaleCheck(((Boolean)value).booleanValue());
      }
   }

 
   public Object getControlValue( String key){
     
      return null;
   }


   /* (non-Javadoc)
    * @see gov.anl.ipns.ViewTools.Components.ViewControls.ViewControl#setObjectState(gov.anl.ipns.ViewTools.Components.ObjectState)
    */
   @Override
   public void setObjectState( ObjectState new_state ) {
      
      super.setObjectState( new_state );
   
      if( new_state == null)
         return;
      if( !(new_state instanceof ObjectState))
         return;
      ObjectState state = new_state;

      setControlValue( state.get( AUTO_SCALE ),AUTO_SCALE );

      if ( state.get(MAXSET) instanceof Number  &&
           state.get(MINSET) instanceof Number    )
      {
        float new_max_val = ((Number)state.get(MAXSET)).floatValue();
        float new_min_val = ((Number)state.get(MINSET)).floatValue();
        if ( new_min_val < new_max_val )
          setMaxMin( new_max_val, new_min_val );
        else
          setMaxMin( new_min_val, new_max_val );
  //    setControlValue( state.get( MAXSET ),MAXSET );
  //    setControlValue( state.get( MINSET ),MINSET );
      }

      setControlValue( state.get( NUM_COLORS ),NUM_COLORS );
      setControlValue( state.get( COLOR_INDEX_CHOICE ),COLOR_INDEX_CHOICE );
      setControlValue( state.get( LOGSCALE ),LOGSCALE );
/*
      ObjectState sliderState =  (ObjectState)state.get( SLIDERS );
           setControlValue( sliderState.get( StretchTopBottom.MAXMIN),
                                         SLIDERS+"."+StretchTopBottom.MAXMIN);
      
           setControlValue( sliderState.get( 
                          StretchTopBottom.INTERVAL_MAXMIN_BOTTOM),
                          SLIDERS+"."+StretchTopBottom.INTERVAL_MAXMIN_BOTTOM);
     
           setControlValue( sliderState.get( 
                             StretchTopBottom.INTERVAL_MAXMIN_TOP),
                             SLIDERS+"."+StretchTopBottom.INTERVAL_MAXMIN_TOP);
     
           setControlValue( sliderState.get( StretchTopBottom.TOP_VALUE),
                                      SLIDERS+"."+StretchTopBottom.TOP_VALUE);
           
           setControlValue( sliderState.get( StretchTopBottom.BOTTOM_VALUE),
                                 SLIDERS+"."+StretchTopBottom.BOTTOM_VALUE);
     
           setControlValue( sliderState.get( StretchTopBottom.GANG),
                                           SLIDERS+"."+StretchTopBottom.GANG);
*/          
      setControlValue( state.get( PRESCALE ),PRESCALE );
      checkValues();
      calculateMapping();
      colorPanel.setColorModel(colorOptions.getColorScale(), 
                                            colorOptions.getNumColors(), true);
     // colorPanel.setDataRange( min , max , true );
      colorPanel.repaint();
    
     // send_message(updateMessage);

   }
   
   /**
    * Causes a redraw of images that may have changed. Right now it only 
    * redraws the color panel
    */
   public void reDraw(){
      
      calculateMapping();
      
     
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
		
		specMinMax.setSelected(true);
		maxField = new JTextField();
		minField = new JTextField();
		maxField.setEditable(true);
		minField.setEditable(true);
		maxField.setText(""+max);
		minField.setText(""+min);

      Sav_minField = minField.getText();
      Sav_maxField = maxField.getText();
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
           checkValues();
	   setSUBINTERVAL();
		localMin = sliders.getBottomValue();
		localMax = sliders.getTopValue();
		
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
		
				
		initMapping();
		//printMapping();
		
		//-----------------------Log Scaling
		if(logScale)
		{  //imagePanel2 sets min to .01 if too small
	      if( localMax > 0)
	         if( localMin <=0)
	             localMin = (float)Math.pow( 10 , Math.log10(localMax)-4 );
			logScale(colorMapping,localMax,localMin);
		}
		
		if(flipped)
		{
			
			for(int i = 0; i < SUBINTERVAL; i++)
			{
				colorMapping[i] = (byte)( ( colorOptions.getNumColors() - 1 ) - colorMapping[i] );
			}
			
			flipped = false;
		}

      colorPanel.setDataRange( min , max , logScale,false );
		colorPanel.setColorTable( colorMapping, 
                                          localMin, 
                                          localMax, 
                                          logScale,
                                          true);
		//printColorMapping();
	}

	
	/*
	 * Performs the log function on each value in the array....
	 */
	private void logScale(byte[] map, float valMax,float valMin)
	{	 
	    setSUBINTERVAL();
	    if(valMax <=0 || valMin <=0){
	       java.util.Arrays.fill( map , (byte)0 );
	       return;
	    }
 	    for ( int i = 0; i < SUBINTERVAL-1; i++ )
	    {
	      float x =valMin + i * (valMax - valMin) / 
	      		(SUBINTERVAL-1);
	      if ( x > valMin )
	        x = (float)Math.log10(x);
	      else
	        x =(float)Math.log10( valMin );

	      map[i] = (byte)((colorOptions.numColors) * 
	    		  (x - Math.log10(valMin)) / 
	    		  (Math.log10(valMax) - Math.log10(valMin)));
	    }
       map[ SUBINTERVAL - 1 ] = map[ SUBINTERVAL - 2 ];
	}
	

   private boolean checkMaxMinValues(){
      
      if(!maxField.getText().equals(Sav_maxField)||!minField.getText().equals(Sav_minField))
      {
         setMax(maxField.getText());
         setMin(minField.getText());
         sliders.setMaxMin(max,min);
         maxField.setText(""+max);
         minField.setText(""+min);
         Sav_minField = minField.getText();
         Sav_maxField = maxField.getText();
         return true;
      }  
      return false;
   }
   
	private void checkValues()
	{
		boolean b =checkMaxMinValues();
		//call sliderTopBottom check method
		
		//check preScale
		if(!prescaleField.getText().equals(Sav_prescaleField))
		{
			setPrescale(prescaleField.getText());
		}
		
		//check num colors
		b = b || colorOptions.checkValue();
		
		boolean b1 = sliders.checkValues();
		b = b || b1;
		
	}
	
	public int getNumColors()
	{
		return colorOptions.getNumColors();
	}
	
	public String getColorScale()
	{
		return colorOptions.getColorScale();
	}
	
        public float getLocalMin()
        {
          return localMin;
        }

        public float getLocalMax()
        {
          return localMax;
        }

	public float getMin()
	{
		return min;
	}
	
	public void setMax(float m)
	{
		max = m;
		colorPanel.setDataRange(min, max,logScale, true);
	}
  
	public void setMin(float m)
	{
		min = m;
		colorPanel.setDataRange(min, max,logScale, true);
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
		  colorPanel.setDataRange(min, max, logScale,true);
			//maxField.setText(""+max);
		}
		catch(Exception ex)
		{
			JOptionPane.showMessageDialog (null, ex.getMessage(), "Edit Max Value Field Error", JOptionPane.ERROR_MESSAGE);
			
			maxField.setText(""+max);

         Sav_maxField = maxField.getText();
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
		  colorPanel.setDataRange(min, max, logScale,true);
			//minField.setText(""+min);
		}
		catch(Exception ex)
		{
			JOptionPane.showMessageDialog (null, ex.getMessage(), "Edit Min Value Field Error", JOptionPane.ERROR_MESSAGE);
			
			minField.setText(""+min);
			Sav_minField = minField.getText();
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
		prescaleField.setText(""+scale);
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
	
   public boolean getLogScale()
   {
      return logScale;
   }

	public void setLogScale(boolean log)
	{
		logScale = log;
		logCheck.setSelected(log);
		setSUBINTERVAL();
		calculateMapping();
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
		blah.setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
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
				
			}
			
			if( e.getSource() == updateButton )
			{
				checkValues();
				send_message(updateMessage);
				calculateMapping();
				
			}
			
			if( e.getSource() == cancelButton )
			{ 
				send_message(cancelMessage);
				
			}
		}
	}

        
   private void setSUBINTERVAL(){
      if( logScale){
         SUBINTERVAL = Math.max( 256,  (int)(10*max/min+.5));
         if( SUBINTERVAL >500000)
            SUBINTERVAL = 500000;
      
      }
      else
        
         SUBINTERVAL = 256;
   }
	//Sets GUI and internal values and other implications. No notifications
	private void setLogCheck( boolean isLog){
	   logScale = isLog;
	   logCheck.removeActionListener( optionsListener);
	   logCheck.setSelected( isLog );
	   logCheck.addActionListener( optionsListener);
	   setSUBINTERVAL();
	   calculateMapping();
	}
	
	//Sets GUI and internal values and slider results. No notifications
	
	private void setMaxMin( float Max, float Min){
	   maxField.removeActionListener(  optionsListener );
	   minField.removeActionListener(  optionsListener );
	   maxField.setText(  ""+Max );
	   minField.setText(""+Min);
	   Sav_maxField = maxField.getText();
	   Sav_minField = minField.getText();
	   maxField.addActionListener(  optionsListener );
      minField.addActionListener(  optionsListener );
      max = Max;
      min = Min;
      sliders.setMaxMin(  Max , Min );
      calculateMapping();
	}
	
	private void setAutoScaleCheck( boolean isChecked){
	   autoScale.removeActionListener(optionsListener);
	   specMinMax.removeActionListener( optionsListener );
	   if (isChecked )
      {
         autoScale.setSelected(true);
         specMinMax.setSelected(false);
         maxField.setEditable(false);
         minField.setEditable(false);
         setMaxMin( MAX, MIN);
      }else{
         
         specMinMax.setSelected(true);
         autoScale.setSelected(false);
         maxField.setEditable(true);
         minField.setEditable(true);
         Sav_minField = minField.getText();
         Sav_maxField = maxField.getText();
      }
      
      calculateMapping();

      autoScale.addActionListener(optionsListener);
      specMinMax.addActionListener( optionsListener );
	   
	}
	private class OptionsListener implements ActionListener
	{

		public void actionPerformed(ActionEvent e) 
		{
			if( e.getSource() == logCheck )
			{  checkValues();
				if (((JCheckBox)e.getSource()).isSelected())
				{
				 if(max<0) max = -max;
	          if(min<0) min = 0;
	          if( min == 0)
	             min =(float) Math.pow( 10f, Math.log10(max) -4);	         
	         
	           setMaxMin( max, min);
				  logScale = true;
				  
				  
				}
				else{
					logScale = false;
				}
				setSUBINTERVAL();
				calculateMapping();
				checkValues();
            calculateMapping();
			}
			
			else if(e.getSource() == autoScale)
			{  checkValues();
				if (((JRadioButton)e.getSource()).isSelected())
				{
					autoScale.setSelected(true);
					specMinMax.setSelected(false);
					max  = MAX;
					min =  MIN;
					if( logScale ) {
					   if(max<0) max = -max;
		             if(min<0) min = 0;
		             if( min == 0)
		                min =(float) Math.pow( 10f, Math.log10(max) -4);
               }
	            setMaxMin( max, min);
	            
					maxField.setEditable(false);
					minField.setEditable(false);
               
		         Sav_maxField = maxField.getText();
		         Sav_minField = minField.getText();
               setSUBINTERVAL();
				}
				sliders.setMaxMin(max,min);
			
			}
			
			else if(e.getSource() == specMinMax)
			{  
				if (((JRadioButton)e.getSource()).isSelected())
				{
					specMinMax.setSelected(true);
					autoScale.setSelected(false);
					maxField.setEditable(true);
					minField.setEditable(true);
		         Sav_minField = minField.getText();
		         Sav_maxField = maxField.getText();
				}
			}
			
			
			if(e.getSource() == maxField )
			{
				String maxValue = maxField.getText();
				setMax(maxValue);
				checkValues();
				if(max<0&&logScale) max = -max;
				maxField.setText(""+max);
	         Sav_maxField = maxField.getText();
				sliders.setMaxMin(max,min);
			}
			
			else if(e.getSource() == minField )
			{
				String minValue = minField.getText();
				setMin(minValue);
				checkValues();
				// TODO
				if(min<0&&logScale) 
				   min = (float)Math.pow( 10 , Math.log10(max)-6 ) ;
				minField.setText(""+min);
				Sav_minField = minField.getText();
				sliders.setMaxMin(max,min);
			}
			
			else if( e.getSource() == prescaleField )
			{  
			   checkValues();
				String value = prescaleField.getText();
				setPrescale(value);
			}
			calculateMapping();
		}
	}
	
	private class ColorOptionsListener implements ActionListener
	{

		public void actionPerformed(ActionEvent e) 
		{
			
			if(e.getActionCommand().equals(ColorOptions.COLOR_SCALE_CHANGED))
			{
			   checkValues();
				
				colorPanel.setColorModel(colorOptions.getColorScale(), colorOptions.getNumColors(), true);
			}
			
			else if(e.getActionCommand().equals(ColorOptions.NUM_OF_COLORS_CHANGED))
			{  checkValues();
				
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
