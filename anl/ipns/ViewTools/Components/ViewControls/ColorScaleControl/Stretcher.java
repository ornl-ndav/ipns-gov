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
	
	private String prevTextFieldValue;
	
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
		prevTextFieldValue = textField.getText();
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
		String str = "" + value;
		textField.setText(str);
	   prevTextFieldValue = str;
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
		float tempMax = (maxInterval-minimum)/(maximum - minimum);
		float tempMin = (minInterval-minimum)/(maximum - minimum);
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
	
	private String padd( String S, int nleadingZeroes){
	  
	  
	  if( S == null || nleadingZeroes <= 0)
	     return S;
	  char[] Res = new char[ S.length()+nleadingZeroes];
	  int k =0;
	  if( S.startsWith(  "-")){
	     k = 1;
	     Res[0]='-';
	  }
	  java.util.Arrays.fill(Res,k,nleadingZeroes+k,'0');
	  System.arraycopy( S.toCharArray() , k ,Res , nleadingZeroes+k ,
	                                                   S.length()-k );
	  return new String( Res );
	   
	}
	
	// st1 < st2  do for digit pos etc. Assumes leading digits one apart
	// trailing digits 9 and 0 keep going. Assumes both same length, have
	// decimal points that line up.
	private String GetNextDigits( String st1, String st2, int posStart){
	  
	   if( st1 == null || st2== null 
	              || posStart < 0)
	      return "";

	  
	      String small, large;
	      if( st1.charAt( posStart )< st2.charAt( posStart )){
	         small = st1;
	         large = st2;
	      }else {
	         small = st2;
	         large = st1;
	      }	
	      int cc = ((int)small.charAt( posStart )+(int)large.charAt(  posStart ))/2;
	      if( cc != (int)small.charAt( posStart ) && cc !=(int)large.charAt(  posStart ))
	         return ""+(char)cc;
	      if( posStart +1 >= st1.length() )//assumes at end of string with a dot	        
	         return ""+(char)cc+"5";
	      String Res="";
	      int k=1;
	      if( small.charAt( posStart+k )=='.')k++;
	      int nextChar1 = (int)small.charAt( posStart+k );
         int nextChar2 = (int)large.charAt( posStart+k );
         int nextChar = (nextChar1+nextChar2+10)/2;
         Res+=""+(char)small.charAt( posStart );
         if( nextChar - (int)'0' > 9){
            Res = ""+(char)large.charAt( posStart );
            if( k >1)
               Res +=".";
            Res += (char)(nextChar-10);
            return Res;
         }
         if( nextChar != nextChar1 ){
            if(k > 1)
               Res +='.';
            Res += (char)nextChar;
            return Res;
         }
         
         // should be at case where n9  and (n+1)0   
            
	      return GetNextDig999( small, large, posStart, posStart+2);
	      
	 
	   
	         
	   
	}
	// small is x9999 and large is y0000  where x is y-1
	private String GetNextDig999( String small, String large, int posStart,
	              int currentPos){
	   
	   if( currentPos >= small.length())
	      return small.substring( posStart);
	  if( small.charAt(currentPos)=='.')currentPos++;
	  if( small.charAt( currentPos )!='9' || large.charAt( currentPos )!='0'){
	     int nextChar1 = (int)small.charAt( posStart+1 );
        int nextChar2 = (int)large.charAt( posStart+1 );
        int nextChar = (nextChar1+nextChar2+10)/2;
        String Res=small.substring( posStart,currentPos )+(char)nextChar;
        if( nextChar - (int)'0' > 9){
           Res =large.substring( posStart, currentPos );
           Res += (char)(nextChar-10);
           return Res;
        }
        return Res;
	  }
	  return GetNextDig999( small, large, posStart, currentPos+1);
	   
	}
	//Assumes decimal point
	private String paddTrailing( String S, int nZeroes ){
	   if( nZeroes <=0)
	      return S;
	   char[] Res = new char[nZeroes];
	   java.util.Arrays.fill( Res , '0' );
	   return S + new String( Res);
	}
	// If sliderPos =0 or 99 will give max or min value
	// otherwise will pick the point in Interval with the fewest number
	// of significant digits.
	private float sliderValFixed( int sliderPos, float minInterval, 
	                                      float maxInterval){
	   int i;
	   if( sliderPos ==0)
	      return minimum;
	   if( sliderPos == 100)
	      return maximum;
	   if( minInterval < 0 && maxInterval > 0)
	      return 0f;
	   String dig1 = (new Float(minInterval)).toString();
      String dig2 = (new Float(maxInterval)).toString();
      if( dig1 == null || dig2 == null)
         return 0f;
      
      //Make sure decimal points line up
      int posDecimal_min = dig1.indexOf( '.' );
      int posDecimal_max = dig2.indexOf( '.' );
      if( posDecimal_min < 0){
         posDecimal_min = dig1.length(); 
         dig1 +=".";
      }
      if( posDecimal_max < 0){
         posDecimal_max = dig2.length(); 
         dig2 +=".";
      }
      if( posDecimal_min < posDecimal_max){
         dig1 = padd( dig1, posDecimal_max -posDecimal_min);
         posDecimal_min = posDecimal_max;
      } else if( posDecimal_max < posDecimal_min){
         dig2 = padd( dig2, posDecimal_min -posDecimal_max);
         posDecimal_max = posDecimal_min;
      }
      dig1 = paddTrailing( dig1, dig2.length()-dig1.length());
      dig2 = paddTrailing( dig2, dig1.length()-dig2.length());
      
      for(  i=0; i < Math.min( dig1.length(), dig2.length()) && 
                   dig1.charAt(i) == dig2.charAt(i);i++){}
     
      if( i >=dig1.length() && i >= dig2.length())
         return minInterval;
      //if( minInterval < 0) i--;
      String S = dig1.substring(0,i);
      char c_min = ' ',
           c_max = ' ';
      if( i < dig1.length())
         c_min = dig1.charAt( i );
      
      
      if( i < dig2.length())
         c_max = dig2.charAt( i );
    
      String Res = S;
      int c_int =  ((int)c_min +(int)c_max)/2;
      if( c_int != (int) c_max && c_int != (int)c_min)
         Res += (char)c_int;
     
      else{
         
         Res += GetNextDigits( dig1,dig2,i);
         
         
      }
      
      if( Res.length() <  posDecimal_min )
         Res = paddTrailing( Res, posDecimal_min - Res.length());
         
      
      return (new Float(Res)).floatValue();
      
      
      
	   
	   
	}
	
	/** The MouseListener to control input from the mouse for the slider. */
	MouseListener mouse = new MouseListener()
	{
		public void mouseClicked(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
		public void mousePressed(MouseEvent e) {}

		public void mouseReleased(MouseEvent e) 
		{
			changeValue(false);
		}
	};
	/**
    * Checks if text Field has been changed without entering a return.
    */
    public boolean checkValues(){
       if( !textField.getText().equals( prevTextFieldValue )){
          if(validateRange(false))
            {
               changeValue(true);
               prevTextFieldValue = textField.getText();
               //sendAction();
               return true;
            }
          
       }
       return false;
    }
	
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
			prevTextFieldValue = text;
			float datanumber = 0;
			datanumber = new Float(text).floatValue();
			int slidervalue = (int) (((datanumber-minimum)/((maximum-minimum)/100)));
			masterValue = datanumber;
			slider.setValue(slidervalue);
		}
		else
		{
			int value = slider.getValue();
			float delta = ((Math.max(maximum,minimum)-Math.min(minimum,maximum))/100);
			masterValue = value*delta+Math.min(minimum,maximum);
			masterValue = sliderValFixed( value, masterValue, masterValue+delta);
			String str = Float.toString(masterValue);
			textField.setText(str);
			prevTextFieldValue = str;
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
		
			if(value <=maxInterval && value >=minInterval)
			  return true;
			
			return false;
		}
		else
		{
		  String text = textField.getText();
		  prevTextFieldValue = text;
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
		    prevTextFieldValue = textField.getText();
		  }
			  
		  if(datanumber <= maxInterval && datanumber >= minInterval)
			 return true;
		  
		  if(datanumber<minInterval)
		  {
		  	JOptionPane.showMessageDialog(null, "Value less than minimum, setting back to Previous Value");
		  	datanumber = masterValue;
		  	String str = Float.toString(datanumber);
		  	textField.setText(str);
         prevTextFieldValue = textField.getText();
		  	return false;
		  }

		  if(datanumber>maximum)
		  {
			JOptionPane.showMessageDialog(null, "Value greater than maximum, setting back to Previous Value");
			datanumber = masterValue;
			String str = Float.toString(datanumber);
			textField.setText(str);
         prevTextFieldValue = textField.getText();
			return false;
		  }
			  
		  return false;
		}
	}
}
