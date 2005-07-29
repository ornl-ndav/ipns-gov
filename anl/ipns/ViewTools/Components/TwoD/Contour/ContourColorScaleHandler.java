/*
 * File: ContourColorScaleHandler.java
 *
 * Copyright (C) 2005, Dominic Kramer
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
 * Primary   Dominic Kramer <kramerd@uwstout.edu>
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
 * $Log$
 * Revision 1.2  2005/07/29 15:23:50  kramer
 * Now this class records if the colorscale controls are visible and their
 * last visible location.  Also, when the user selects to use a solid
 * color or a colorscale for the contour lines, this class makes the
 * colorscale controls invisible/visible respectively.
 *
 * Revision 1.1  2005/07/25 20:42:37  kramer
 *
 * This is a module of the ContourViewComponent that records if the
 * ContourJPanel is using a solid color or colorscale to color the contour
 * lines it draws.
 *
 */
package gov.anl.ipns.ViewTools.Components.TwoD.Contour;

import gov.anl.ipns.Util.Messaging.Information.InformationCenter;
import gov.anl.ipns.Util.Messaging.Property.PropertyChangeConnector;
import gov.anl.ipns.ViewTools.Components.IVirtualArray2D;
import gov.anl.ipns.ViewTools.Components.ObjectState;
import gov.anl.ipns.ViewTools.Panels.Contour.ContourJPanel;

import java.awt.Color;

/**
 * 
 */
public class ContourColorScaleHandler extends ContourChangeHandler
{
//------------------------=[ ObjectState keys ]=------------------------------//
   public static final String USE_COLORSCALE_KEY = 
                                 "Use colorscale key";
   public static final String COLORSCALE_CONTROL_LOCATION_KEY = 
                                 "Colorscale location key";
   public static final String COLORSCALE_CONTROL_VISIBLE_KEY = 
                                 "Colorscale visible key";
//----------------------=[ End ObjectState keys ]=----------------------------//
   
   
//--------------------=[ Default field values ]=------------------------------//
   public static final boolean DEFAULT_USE_COLORSCALE = false;
   public static final String DEFAULT_COLORSCALE_LOCATION = 
                                 CONTROL_PANEL_LOCATION;
   public static final boolean DEFAULT_COLORSCALE_VISIBLE = true;
//------------------=[ End default field values ]=----------------------------//
   
   
//-----------------------------=[ Fields ]=-----------------------------------//
   private boolean usesColorscale;
   private boolean colorscaleControlVisible;
   private String lastVisColorscaleLoc;
//---------------------------=[ End fields ]=---------------------------------//
   
   
//--------------------------=[ Constructors ]=--------------------------------//
   public ContourColorScaleHandler(PropertyChangeConnector connector, 
                                   InformationCenter center, 
                                   ContourJPanel panel, 
                                   boolean useColorscale, 
                                   String colorscaleLocation)
   {
      super(connector, center, panel);
      
      //now to connect to the PropertyChangeConnector
       getPropertyConnector().addHandler(this);
      
      this.usesColorscale = useColorscale;
      
      //record the last location of the colorscale control as the default 
      //location (and also set that the control is visible)
       this.lastVisColorscaleLoc = DEFAULT_COLORSCALE_LOCATION;
       this.colorscaleControlVisible = DEFAULT_COLORSCALE_VISIBLE;
      
      //notify everyone (including this class) that the colorscale control 
      //has moved
       colorScaleLocationChanged(colorscaleLocation);
   }
//------------------------=[ End constructors ]=------------------------------//
   
   
//--------=[ Methods implemented for the ContourChangeHandler class ]=--------//
   public void reinit(IVirtualArray2D v2D)
   {
      //nothing needs to be done if the virutal array is changed
   }
   
   public void changeColorScaleName(String colorscale)
   {
      this.usesColorscale = true;
      
      colorScaleLocationChanged(this.lastVisColorscaleLoc);
   }
   
   public void changeColor(Color color)
   {
      this.usesColorscale = false;
      
      colorScaleLocationChanged(NONE_LOCATION);
   }
   
   public void changeColorScaleLocation(String location)
   {
      if (location==null)
         location = DEFAULT_COLORSCALE_LOCATION;
      
      if (location.equals(NONE_LOCATION))
         this.colorscaleControlVisible = false;
      else
      {
         this.colorscaleControlVisible = true;
         this.lastVisColorscaleLoc = location;
      }
   }
//------=[ End methods implemented for the ContourChangeHandler class ]=------//
   
   
//-----------=[ Methods implemented for the IPreserveState interface ]=-------//
   public void setObjectState(ObjectState state)
   {
      if (state==null)
         return;
      
      Object val = state.get(USE_COLORSCALE_KEY);
      if ( (val != null) && (val instanceof Boolean) )
      {
         boolean usesScale = ((Boolean)val).booleanValue();
         setUsesColorScale(usesScale);
         if (usesScale)
         {
            String colorscale = 
               (String)getInfoCenter().
                  obtainValue(ContourControlHandler.COLORSCALE_NAME_INFO_KEY);
            if (colorscale==null)
               colorscale = ContourControlHandler.DEFAULT_COLOR_SCALE;

            colorScaleNameChanged(colorscale);
         }
         else
         {
            Color color = 
               (Color)getInfoCenter().
                  obtainValue(ContourMenuHandler.COLOR_INFO_KEY);
            if (color == null)
               color = ContourMenuHandler.DEFAULT_LINE_COLOR;
            
            colorChanged(color);
         }
      }
      
      val = state.get(COLORSCALE_CONTROL_LOCATION_KEY);
      if ( (val != null) && (val instanceof String) )
      {
         String visibleLoc = (String)val;
         
         val = state.get(COLORSCALE_CONTROL_VISIBLE_KEY);
         if ( (val != null) && (val instanceof Boolean) )
         {
            boolean colorscaleVisible = ((Boolean)val).booleanValue();
            
            //store the last visible location
              this.lastVisColorscaleLoc = visibleLoc;
            
            //determine which location to give to colorScaleLocationChanged()
            //if the colorscale control is not visible, its location is 
            //NONE_LOCATION
              String location = this.lastVisColorscaleLoc;
              if (!colorscaleVisible)
                 location = NONE_LOCATION;
            
            colorScaleLocationChanged(location);
         }
      }
   }
   
   public ObjectState getObjectState(boolean isDefault)
   {
      ObjectState state = new ObjectState();
        if (isDefault)
        {
           state.insert(USE_COLORSCALE_KEY, 
                           new Boolean(DEFAULT_USE_COLORSCALE));
           state.insert(COLORSCALE_CONTROL_LOCATION_KEY, 
                           DEFAULT_COLORSCALE_LOCATION);
           state.insert(COLORSCALE_CONTROL_VISIBLE_KEY, 
                           new Boolean(DEFAULT_COLORSCALE_VISIBLE));
        }
        else
        {
           state.insert(USE_COLORSCALE_KEY, 
                           new Boolean(usesColorscale));
           state.insert(COLORSCALE_CONTROL_LOCATION_KEY, 
                           this.lastVisColorscaleLoc);
           state.insert(COLORSCALE_CONTROL_VISIBLE_KEY, 
                           new Boolean(this.colorscaleControlVisible));
        }
      return state;
   }
//---------=[ End methods implemented for the IPreserveState interface ]=-----//
   
   
//--------------------=[ Getter/setter methods ]=-----------------------------//
   public boolean getUsesColorScale()
   {
      return usesColorscale;
   }
   
   public void setUsesColorScale(boolean useColorscale)
   {
      if (useColorscale)
      {
         String colorscale = 
            (String)getInfoCenter().
               obtainValue(ContourControlHandler.COLORSCALE_NAME_INFO_KEY);
         if (colorscale==null)
            colorscale = ContourControlHandler.DEFAULT_COLOR_SCALE;
         changeColorScaleName(colorscale);
      }
      else
      {
         Color color = 
            (Color)getInfoCenter().
               obtainValue(ContourMenuHandler.COLOR_INFO_KEY);
         if (color == null)
            color = ContourMenuHandler.DEFAULT_LINE_COLOR;
         changeColor(color);
      }
   }
//------------------=[ End getter/setter methods ]=---------------------------//
}