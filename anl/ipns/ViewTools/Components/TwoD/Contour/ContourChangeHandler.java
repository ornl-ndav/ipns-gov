/*
 * File: ContourChangeHandler.java
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
 * Revision 1.2  2005/07/28 22:56:18  kramer
 * Made all of the '*changed()' methods (ex. 'colorChanged()',
 * 'displayChanged()' ....) final.  Modified all of the 'change*()' methods
 * (ex. 'changeColor()', 'changeDisplay()' ....) to also call
 * 'this.change*()' (ex. 'changeColor()' calls 'this.changeColor()' ....).
 * Also, the 'propertyChanged()' method no longer calls
 * 'this.connector.propertyChanged()' (it was causing infinite loops).
 *
 * Revision 1.1  2005/07/25 20:40:15  kramer
 *
 * Initial checkin.  This class is an abstract implementation of a
 * PropertyChangeHandler.  It contains the keys for and convience methods to
 * listen to/notify modules of changes to the shared data between various
 * modules that compose the ContourViewComponent.  All of these modules
 * extend this class.
 *
 */
package gov.anl.ipns.ViewTools.Components.TwoD.Contour;

import gov.anl.ipns.Util.Messaging.Information.InformationCenter;
import gov.anl.ipns.Util.Messaging.Property.PropertyChangeConnector;
import gov.anl.ipns.Util.Messaging.Property.PropertyChangeHandler;
import gov.anl.ipns.ViewTools.Components.IPreserveState;
import gov.anl.ipns.ViewTools.Components.IVirtualArray2D;
import gov.anl.ipns.ViewTools.Panels.Contour.ContourJPanel;

import java.awt.Color;

public abstract class ContourChangeHandler implements PropertyChangeHandler, 
                                                      IPreserveState
{
//-------------=[ Used to specify a ControlColorScale's location ]=-----------//
   protected static final String CONTROL_PANEL_LOCATION = 
                                  "Control Panel";
   protected static final String BELOW_IMAGE_LOCATION = 
                                  "Below Image (calibrated)";
   protected static final String RIGHT_IMAGE_LOCATION = 
                                  "Right of Image (calibrated)";
   protected static final String NONE_LOCATION = 
                                  "None";
//-----------=[ End used to specify a ControlColorScale's location ]=---------//
   
   
//------------------------=[ Property keys ]=---------------------------------//
   public static final String COLORSCALE_NAME = "colorscale name";
   public static final String CONTOUR_COLOR = "contour color";
   public static final String IS_DOUBLE_SIDED = "is double sided";
   public static final String COLORSCALE_LOCATION = "colorscale location";
   public static final String INTENSITY = "intensity";
   public static final String DISPLAY = "display";
   public static final String ASPECT_RATIO = "aspect ratio";
   
   private PropertyChangeConnector connector;
   private InformationCenter center;
   /** The panel that does the work of rendering the contour plot. */
   private ContourJPanel contourPanel;
//----------------------=[ End property keys ]=-------------------------------//
   
   
//---------------------------=[ Constructor ]=--------------------------------//
   public ContourChangeHandler(PropertyChangeConnector connector, 
                               InformationCenter center, 
                               ContourJPanel contourPanel)
   {
      this.connector = connector;
      this.center = center;
      this.contourPanel = contourPanel;
   }
//-------------------------=[ End constructor ]=------------------------------//
   
   
//-----------------------=[ Getter/setter methods ]=--------------------------//
   public ContourJPanel getContourPanel()
   {
      return this.contourPanel;
   }
   
   public PropertyChangeConnector getPropertyConnector()
   {
      return this.connector;
   }
   
   public InformationCenter getInfoCenter()
   {
      return this.center;
   }
//---------------------=[ End getter/setter methods ]=------------------------//
   
   
//--------=[ Methods invoked to specify that a property has changed ]=--------//
   //call these methods after changing one of the shared properties
   public final void colorScaleNameChanged(String colorscale)
   {
      this.changeColorScaleName(colorscale);
      connector.propertyChanged(COLORSCALE_NAME, colorscale, this);
   }
   
   public final void colorChanged(Color color)
   {
      this.changeColor(color);
      connector.propertyChanged(CONTOUR_COLOR, color, this);
   }
   
   public final void isDoubleSidedChanged(boolean isDoubleSided)
   {
      this.changeIsDoubleSided(isDoubleSided);
      connector.propertyChanged(IS_DOUBLE_SIDED, new Boolean(isDoubleSided), 
                                this);
   }
   
   public final void colorScaleLocationChanged(String location)
   {
      this.changeColorScaleLocation(location);
      connector.propertyChanged(COLORSCALE_LOCATION, location, this);
   }
   
   public final void intensityChanged(double intensity)
   {
      this.changeIntensity(intensity);
      connector.propertyChanged(INTENSITY, new Double(intensity), this);
   }
   
   public final void displayChanged()
   {
      this.changeDisplay();
      connector.propertyChanged(DISPLAY, null, this);
   }
   
   public final void preserveAspectRatioChanged(boolean preserve)
   {
      this.changeAspectRatio(preserve);
      connector.propertyChanged(ASPECT_RATIO, new Boolean(preserve), this);
   }
//------=[ End methods invoked to specify that a property has changed ]=------//

   
//-----------=[ Methods invoked after a property has changed ]=---------------//
   // these methods are invoked when one of the properties has changed
   public void changeColorScaleName(String colorscale) {}
   public void changeColor(Color color) {}
   public void changeIsDoubleSided(boolean isDoubleSided) {}
   public void changeColorScaleLocation(String location) {}
   public void changeIntensity(double intensity) {}
   public void changeDisplay() {}
   public void changeAspectRatio(boolean preserve) {}
//---------=[ End methods invoked after a property has changed ]=-------------//
   
   
//-------------------------=[ Abstract methods ]=-----------------------------//
   public abstract void reinit(IVirtualArray2D v2d);
//-----------------------=[ End abstract methods ]=---------------------------//
   
   
//-----------------------=[ Convience methods ]=------------------------------//
   public void propertyChanged(String property, Object value)
   {
      if (property.equals(COLORSCALE_NAME))
         changeColorScaleName((String)value);
      else if (property.equals(CONTOUR_COLOR))
         changeColor((Color)value);
      else if (property.equals(IS_DOUBLE_SIDED))
         changeIsDoubleSided( ((Boolean)value).booleanValue() );
      else if (property.equals(COLORSCALE_LOCATION))
         changeColorScaleLocation((String)value);
      else if (property.equals(INTENSITY))
         changeIntensity( ((Double)value).doubleValue() );
      else if (property.equals(DISPLAY))
         changeDisplay();
      else if (property.equals(ASPECT_RATIO))
         changeAspectRatio( ((Boolean)value).booleanValue() );
      
      //this.connector.propertyChanged(property, value, this);
   }
//---------------------=[ End convience methods ]=----------------------------// 
}
