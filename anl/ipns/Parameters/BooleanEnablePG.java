/* File:  BooleanEnablePG.java 
 *
 * Copyright (C) 2006, Ruth Mikkelson
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
 * Contact : Ruth Mikkelson <Mikkelsonr@uwstout.edu>
 *           University of Wisconsin-Stout
 *           Menomonie, Wisconsin 54751
 *           USA
 *
 * This work was supported by the National Science Foundation under grant
 * number DMR-0426797, and by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * $Log$
 * Revision 1.4  2006/07/10 16:25:04  dennis
 * Change to new Parameter GUIs in gov.anl.ipns.Parameters
 *
 * Revision 1.3  2006/07/03 21:06:52  dennis
 * Cleaned up oddity regarding type casts/autoboxing.
 *
 * Revision 1.2  2006/06/26 22:52:42  dennis
 * Minor fixes to javadocs.
 *
 */

package gov.anl.ipns.Parameters;

import javax.swing.*;
import java.util.*;
import java.beans.PropertyChangeEvent;
import java.lang.reflect.*;
import java.beans.*;
import java.awt.event.*;

/**
 * This ParameterGUI influences the enabled status of several of the
 * ParameterGUI's following this one in the parameterlist of an operator
 * or in a Vector of parameters.
 * 
 * It has one parameter consisting of a vector of 3 values:
 *    1.  This initial truth value 
 *    2.  The number of parameterGUI's following this one in a list that
 *        are set to true if this GUI's value is true and false if this GUI's
 *        value is false
 *     
 *    3.  The number of parameterGUI's after the previous ones in a list that
 *        are set to false if this GUI's value is true and true if this GUI's
 *        value is false
 * @author MikkelsonR
 *
 */
public class BooleanEnablePG extends BooleanPG implements IParameterGUI,
                                              ItemListener{   
    int nSetIfTrue = 0;
    int nSetIfFalse = 0;
    PropertyChangeSupport PCSupport;
   
    
    /**
     * This constructor is a BooleanPG with the added Information to
     * determine which other parameterGUI's are to be enabled if true and 
     * disabledif false
     * @param name   The prompt
     * @param val    Should be a Vector containing the intial value( True or 
     *               False), an Integer for the number of Parameters following
     *               this one that will be enabled if true(disabled if false)
     *               then another integer indicating the number of parameters 
     *               following the first batch that are disabled when the value
     *               is true and enabled when the value is false
     *               
     *               If it is not a Vector with three elements, this is just a
     *               regular BooleanPG
     */
	public BooleanEnablePG(String name, Object val)
			throws IllegalArgumentException {
		super(name, FirstArg(val));
		val = Conversions.ToVec( val );
		if (!(val instanceof Vector))
			return;
		Vector V = (Vector) val;
		if (V.size() > 1)
			try {
				nSetIfTrue = ((Integer) (V.elementAt(1))).intValue();
				nSetIfFalse = ((Integer) (V.elementAt(2))).intValue();
			} catch (Exception ss) {
              
			}
        PCSupport = new PropertyChangeSupport( this );
	}
	
    
    /**
	 * 
	 * @return the number of parameters immediately after this parameter that
	 *         are enabled if the value is True, otherwise it is disabled
	 */
    public int getNSetIfTrue(){
        return nSetIfTrue;
    }
    
    
    /**
     * 
     * @return the number of parameters immediately after this 
     *         parameter and the ones that are set true that are 
     *         disabled if the value is True, otherwise it is enabled
     */
 
    public int getNSetIfFalse(){
        return nSetIfFalse;
    }
    
   
    /**
     * Adds a listener that should be capable of enabling and disabliing the parameters
     * in a parameter list following this parameter
     * @param list   A propertyChangeListener. Should be an EnableParamListener
     */
    public void addPropertyChangeListener( PropertyChangeListener  list){
    	PCSupport.addPropertyChangeListener(list);
    }

   
    /**
     * Removes a listener that should be capable of enabling and disabliing the parameters
     * in a parameter list following this parameter
     * @param list   A propertyChangeListener. Should be an EnableParamListener
     */
    public void removePropertyChangeListener( PropertyChangeListener list){
    	PCSupport.removePropertyChangeListener( list);
    }
  
    
    /**
     *  Fires a property change event. It should be to an EnableParamListener which
     *  will disable/enable the parameters following this one.
     */
    public void fire() {
        PCSupport.firePropertyChange(new PropertyChangeEvent(this, "NONAME", new Boolean(
                false), new Boolean(true)));
    }

	// Returns the Boolean value associated with an Object O
	// which can be a Vector, Array or just a Boolean
	// The first value is what is returned
	private static Object FirstArg( Object O){
		if( O == null)
			return new Boolean(false);
		if( O instanceof String)
			O = Conversions.StringToVec( (String) O);
		if( O instanceof Vector)
			return ((Vector)O).firstElement();
		if( O.getClass().isArray())
			return Array.get(O,0);
		return O;	
	}


	public Object clone() {
        Vector V = new Vector();
        V.addElement(getValue());
        V.addElement(new Integer(nSetIfTrue));
        V.addElement(new Integer(nSetIfFalse));
        return new BooleanEnablePG(getName(), V);		
	}
	
	
	public JPanel getWidget(){
		JPanel Res = super.getWidget();
		box.addItemListener( this );
		return Res;		
	}
	
	
	public void itemStateChanged( ItemEvent evt){
		fire();
	}
	
	 /**
	  *  @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
