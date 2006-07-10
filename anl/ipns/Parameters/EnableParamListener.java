/* File:  EnableParameterListener.java 
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
 * Revision 1.5  2006/07/10 16:25:04  dennis
 * Change to new Parameter GUIs in gov.anl.ipns.Parameters
 *
 * Revision 1.5  2006/07/04 20:34:05  dennis
 * Minor fix to formatting.
 *
 * Revision 1.4  2006/06/27 21:23:19  rmikk
 * Removed a reference to DataSetTools/operators
 * Did not create GUI element to disable the element
 *
 * Revision 1.3  2006/06/27 20:25:01  rmikk
 * Eliminated references to DataSetTools
 *
 * Revision 1.2  2006/06/26 22:52:42  dennis
 * Minor fixes to javadocs.
 *
 * Revision 1.1  2006/06/26 22:14:03  rmikk
 * Initial checkin for the BooleanEnablePG listener. The IParameter had
 * to be changed and methods changed
 *
 * Revision 1.1  2006/03/16 22:49:13  rmikk
 * Initial Checkin
 *
 */
package gov.anl.ipns.Parameters;

import java.beans.*;
import java.util.*;

/**
 * This class is a PropertyChangeListener for BooleanEnablePG's that
 * takes care of disabling and enabling other Parameters when the
 * BooleanEnablePG's values are changed from true to false or false to
 * true.
 * 
 * The full list of parameters must be present for this ParameterGUI not to
 * revert to a BooleanPG
 * 
 * @author mikkelsonr
 *
 */
public class EnableParamListener implements PropertyChangeListener {

     Object ParameterList;
     int nSetIfTrue;
     int nSetIfFalse;
     int thisParamNum;
     boolean error;
     BooleanEnablePG  param;
     
    /**
     *    Constructor for the listener.  The ParameterList must be full at 
     *    construction time
     *    TODO: Implement so it can be initialized without a full list
     *          of parameters.( check for errors in propertyChange)
     *    PROBLEM: possibly propertyChange not executed at initialization
     *             Not a problem now
     */
    public EnableParamListener(  Object ParameterList,int thisParamNum ) {
        super();
        if( !(ParameterList instanceof Vector) ){
            error=true;
            return;
        }
        this.ParameterList = ParameterList;
        this.thisParamNum = thisParamNum;
        error = false;
        if(ParameterList == null)
             error = true;
        if (thisParamNum < 0)
            error = true;
        if( thisParamNum >=size(ParameterList))
            error = true;
        if(error)
            return;
        param = null;
        if (!(entry(ParameterList,thisParamNum) instanceof BooleanEnablePG))
            error = true;
        else
            param = (BooleanEnablePG) (entry(ParameterList,thisParamNum));
        if(param == null)
            return;
        
        nSetIfTrue = param.getNSetIfTrue();
        nSetIfFalse =param.getNSetIfFalse();
        if ((nSetIfTrue <= 0) && (nSetIfFalse <= 0))
            error = true;
        if (nSetIfTrue < 0)
            nSetIfTrue = 0;
        if (nSetIfFalse < 0)
            nSetIfFalse = 0;
        if (thisParamNum + nSetIfTrue + nSetIfFalse >= size(ParameterList))
            error = true;
        propertyChange( null);
    }
    
    
    /**
     *  Finds the number of elements in a "list" or Operator P 
     * @param P   Either a Vector or an Operator with parameterGUI's
     * @return   The number of ParameterGUI's associated with this list.
     */
    private int size( Object P){
        if( P== null)
            return 0;
        if( P instanceof Vector)
            return ((Vector)P).size();
        return 0;
    }
    
    // Finds the index-th entry of P. 
    // It works for Vectors and Operators
    private Object entry(Object P, int index){
        if( index <0)
            return null;
        if( index >= size( P ))
            return null;
        if(P instanceof Vector)
            return ((Vector)P).elementAt( index );
        return null;
    }

    
    /**
     *  Changes the enabled/disabled status of the ParameterGUI's following the
     *  BooleanEnabledPG that fired this event.
     *  
     *  @param evt   Not used
     */
    public void propertyChange(PropertyChangeEvent evt){
        if( error )
            return;
        boolean enable = true;
        if(!((Boolean)(param.getValue())).booleanValue())
            enable=false;
        for( int i=thisParamNum+1; (i<thisParamNum+1+nSetIfTrue)&&(i<size(ParameterList)); i++){
           Object pparm=entry(ParameterList,i);
           if(pparm instanceof IParameterGUI)
               if(((IParameterGUI)pparm).getGUIPanel(false)!= null)
                ((IParameterGUI)pparm).setEnabled(enable);
        }    
        
        enable = !enable;
        for( int i=thisParamNum+1+nSetIfTrue; (i<thisParamNum+1+nSetIfTrue+nSetIfFalse)&&(i<size(ParameterList)); i++){
           Object pparm=entry(ParameterList,i);
           if(pparm instanceof IParameterGUI)
               ((IParameterGUI)pparm).setEnabled(enable);            
        }    
        
    }
    
}

