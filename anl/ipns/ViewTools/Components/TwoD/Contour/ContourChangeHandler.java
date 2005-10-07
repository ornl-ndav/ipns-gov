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
 * Revision 1.3  2005/10/07 21:32:34  kramer
 * Added javadoc comments for every field, constructor, method, inner class,
 * etc. in this class.
 *
 * Revision 1.2  2005/07/28 22:56:18  kramer
 *
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

/**
 * This class defines the basic functionality that any module of a 
 * {@link gov.anl.ipns.ViewTools.Components.TwoD.Contour.ContourViewComponent 
 * ContourViewComponent} should implement.  It defines methods that should 
 * be invoked when any property has changed.  It also defines methods that 
 * are invoked when a property has changed.  This class implements empty 
 * methods for this latter case.  Subclasses should override these methods 
 * to perform custom work when a property has changed.
 */
public abstract class ContourChangeHandler implements PropertyChangeHandler, 
                                                      IPreserveState
{
//-------------=[ Used to specify a ControlColorScale's location ]=-----------//
   /**
    * Key used to specify that the colorscale's control should be 
    * placed on the control panel of a 
    * {@link ContourViewComponent ContourViewComponent}.
    */
   public static final String CONTROL_PANEL_LOCATION = 
                                  "Control Panel";
   /**
    * Key used to specify that the colorscale's control should be 
    * placed below the contour plot on a 
    * {@link ContourViewComponent ContourViewComponent}.
    */
   public static final String BELOW_IMAGE_LOCATION = 
                                  "Below Image (calibrated)";
   /**
    * Key used to specify that the colorscale's control should be 
    * placed to the right of the contour plot on a 
    * {@link ContourViewComponent ContourViewComponent}.
    */
   public static final String RIGHT_IMAGE_LOCATION = 
                                  "Right of Image (calibrated)";
   /**
    * Key used to specify that the colorscale's control should not be 
    * made visible on a {@link ContourViewComponent ContourViewComponent}.
    */
   public static final String NONE_LOCATION = 
                                  "None";
//-----------=[ End used to specify a ControlColorScale's location ]=---------//
   
   
//------------------------=[ Property keys ]=---------------------------------//
   /**
    * "colorscale name" - This constant static String is a property change 
    *                     key used to specify that the colorscale used when 
    *                     plotting the contour plot has changed.
    */
   public static final String COLORSCALE_NAME = "colorscale name";
   
   /**
    * "contour color" - This constant static String is a property change 
    *                   key used to specify that the line color used when 
    *                   plotting the contour plot has changed.
    */
   public static final String CONTOUR_COLOR = "contour color";
   
   /**
    * "is double sided" - This constant static String is a property change 
    *                     key used to specify that the state of whether or 
    *                     not the colorscale used when plotting the 
    *                     colorscale has changed from being/not being 
    *                     double-sided.
    */
   public static final String IS_DOUBLE_SIDED = "is double sided";
   
   /**
    * "colorscale location" - This constant static String is a property 
    *                         change key used to specify that the location 
    *                         of the colorscale's control has changed.
    */
   public static final String COLORSCALE_LOCATION = "colorscale location";
   
   /**
    * "intensity" - This constant static String is a property change key 
    *               used to specify that the intensity of the colorscale 
    *               used when plotting the contour plot has changed.
    */
   public static final String INTENSITY = "intensity";
   /**
    * "display" - This constant static String is a property change key used 
    *             to specify that the display of the contour plot has 
    *             changed.
    */
   public static final String DISPLAY = "display";
   
   /**
    * "aspect ratio" - This constant static String is a property change key 
    *                  used to specify that the state of whether or not the 
    *                  contour plot has its aspect ratio preserved has 
    *                  changed.
    */
   public static final String ASPECT_RATIO = "aspect ratio";
//----------------------=[ End property keys ]=-------------------------------//
   
   
//------------------------------=[ Fields ]=----------------------------------//
   /**
    * Used to connect this change handler to other change 
    * handlers to form a web of communication.
    */
   private PropertyChangeConnector connector;
   
   /**
    * Used to access shared data between several 
    * <code>ContourChangeHandlers</code>.
    */
   private InformationCenter center;
   
   /**
    * The panel that does the work of rendering 
    * the contour plot.
    */
   private ContourJPanel contourPanel;
//----------------------------=[ End fields ]=--------------------------------//
   
   
//---------------------------=[ Constructor ]=--------------------------------//
   /**
    * Constructs this handler with the given parameters.
    * 
    * @param connector Used to connect this change handler to other 
    *                  change handlers.
    * @param center Used to access shared information between other 
    *               <code>ContourChangeHandlers</code>.
    * @param contourPanel This is panel that renders the contour plot.
    */
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
   /**
    * Used to access the panel that is responsible for actually rendering 
    * the contour plot.
    * 
    * @return The panel that renders the contour plot.
    */
   public ContourJPanel getContourPanel()
   {
      return this.contourPanel;
   }
   
   /**
    * Used to access the connector that connects this change handler 
    * to other handlers.
    * 
    * @return The connector that connects this handler to other handlers.
    */
   public PropertyChangeConnector getPropertyConnector()
   {
      return this.connector;
   }
   
   /**
    * Used to access the <code>InformationCenter</code> used by this class 
    * to access shared information.
    * 
    * @return The center used to access shared information.
    */
   public InformationCenter getInfoCenter()
   {
      return this.center;
   }
//---------------------=[ End getter/setter methods ]=------------------------//
   
   
//--------=[ Methods invoked to specify that a property has changed ]=--------//
   //call these methods after changing one of the shared properties
   /**
    * Invoke this method if the colorscale used to color the contour 
    * plot has changed.
    * 
    * @param colorscale The new colorscale that is being used to color 
    *                   the contour plot.
    */
   public final void colorScaleNameChanged(String colorscale)
   {
      this.changeColorScaleName(colorscale);
      connector.propertyChanged(COLORSCALE_NAME, colorscale, this);
   }
   
   /**
    * Invoke this method if the color used to color the contour lines 
    * has changed.
    * 
    * @param color The new color of the contour lines on the contour plot.
    */
   public final void colorChanged(Color color)
   {
      this.changeColor(color);
      connector.propertyChanged(CONTOUR_COLOR, color, this);
   }
   
   /**
    * Invoke this method if the state of whether or not the colorscale used 
    * to color the contour plot has changed to or from being double-sided.
    * 
    * @param isDoubleSided <code>True</code> if the colorscale is now 
    *                      double-sided and <code>false</code> if it is 
    *                      not.  See {@link #changeIsDoubleSided(boolean) 
    *                      changeIsDoubleSided()} for a description of the 
    *                      results of having the colorscale 
    *                      double-sided or not.
    */
   public final void isDoubleSidedChanged(boolean isDoubleSided)
   {
      this.changeIsDoubleSided(isDoubleSided);
      connector.propertyChanged(IS_DOUBLE_SIDED, new Boolean(isDoubleSided), 
                                this);
   }
   
   /**
    * Invoke this method if the location of the colorscale's control has 
    * changed.
    * 
    * @param location The new location of the colorscale's control.
    * 
    * @see #CONTROL_PANEL_LOCATION
    * @see #BELOW_IMAGE_LOCATION
    * @see #RIGHT_IMAGE_LOCATION
    * @see #NONE_LOCATION
    */
   public final void colorScaleLocationChanged(String location)
   {
      this.changeColorScaleLocation(location);
      connector.propertyChanged(COLORSCALE_LOCATION, location, this);
   }
   
   /**
    * Invoke this method if the intensity of the colorscale used when 
    * plotting the contour plot has changed.
    * 
    * @param intensity The new intensity of the colorscale used when 
    *        plotting the contour plot.
    */
   public final void intensityChanged(double intensity)
   {
      this.changeIntensity(intensity);
      connector.propertyChanged(INTENSITY, new Double(intensity), this);
   }
   
   /**
    * Invoke this method if the display of the contour plot has changed.
    */
   public final void displayChanged()
   {
      this.changeDisplay();
      connector.propertyChanged(DISPLAY, null, this);
   }
   
   /**
    * Invoke this method if the state of whether or not the aspect ratio 
    * is preserved when plotting the contour plot has changed.
    * 
    * @param preserve <code>True</code> if the aspect ratio should be 
    *                 preserved when plotting the contour plot and 
    *                 <code>false</code> if it shouldn't.
    */
   public final void preserveAspectRatioChanged(boolean preserve)
   {
      this.changeAspectRatio(preserve);
      connector.propertyChanged(ASPECT_RATIO, new Boolean(preserve), this);
   }
//------=[ End methods invoked to specify that a property has changed ]=------//

   
//-----------=[ Methods invoked after a property has changed ]=---------------//
   // these methods are invoked when one of the properties has changed
   /**
    * This method is invoked to change the colorscale used when plotting 
    * the contour plot.  This method's implementation is empty.  Subclasses 
    * should override it to perform specialty work if needed.
    * 
    * @param colorscale The new colorscale to use when coloring the 
    *                   contour plot.
    */
   public void changeColorScaleName(String colorscale) {}
   
   /**
    * This method is invoked to change the line color used to when 
    * plotting the contour plot.  This method's implementation is empty.  
    * Subclasses should override it to perform specialty work if needed.
    * 
    * @param color The new line color to use when coloring the contour 
    *              plot.
    */
   public void changeColor(Color color) {}
   
   /**
    * This method is invoked to change if the colorscale used for plotting 
    * the contour plot should be double sided or not.  This method's 
    * implementation is empty.  Subclasses should override it to perform 
    * specialty work if needed.
    * 
    * @param isDoubleSided <code>True</code> if the colorscale used to 
    *                      color the contour plot should be double-sided 
    *                      and <code>false</code> if it shouldn't be.  
    *                      Let <code>x</code> represent the elevation of 
    *                      a contour level.  If the colorscale is not 
    *                      double-sided, a contour level at the elevation 
    *                      <code>-x</code> will be colored the same as a 
    *                      contour level at the elevation <code>x</code>.  
    *                      If the colorscale is double-sided, each contour 
    *                      level would be given its own unique color.
    */
   public void changeIsDoubleSided(boolean isDoubleSided) {}
   
   /**
    * This method is invoked to change the location of the colorscale's 
    * control on the GUI.  This method's implementation is empty.  
    * Subclasses should override it to perform specialty work if needed.
    * 
    * @param location A key for the location of the colorscale's control.
    * 
    * @see #CONTROL_PANEL_LOCATION
    * @see #BELOW_IMAGE_LOCATION
    * @see #RIGHT_IMAGE_LOCATION
    * @see #NONE_LOCATION
    */
   public void changeColorScaleLocation(String location) {}
   
   /**
    * This method is invoked to change the intensity of the color on the 
    * colorscale that is used when plotting the contour plot.  This 
    * method's implementation is empty.  Subclasses should override it to 
    * perform specialty work if needed.
    * 
    * @param intensity The colorscale's new intensity.
    */
   public void changeIntensity(double intensity) {}
   
   /**
    * This method is invoked to change (i.e. update) the display of the 
    * contour plot.  This method's implementation is empty.  Subclasses 
    * should override it to perform specialty work if needed.
    */
   public void changeDisplay() {}
   
   /**
    * This method is invoked to change if the contour plot should have its 
    * aspect ratio preserved when it is rendered.  This method's 
    * implementation is empty.  Subclasses should override it to perform 
    * specialty work if needed.
    * 
    * @param preserve <code>True</code> if the aspect ratio should be 
    *                 preseved and <code>false</code> if it shouldn't be.
    */
   public void changeAspectRatio(boolean preserve) {}
//---------=[ End methods invoked after a property has changed ]=-------------//
   
   
//-------------------------=[ Abstract methods ]=-----------------------------//
   /**
    * This method is invoked whenever the data that is being plotted 
    * has changed.  Subclasses should perform any changes to adjust for 
    * the new data.
    * 
    * @param v2d The new data that is going to be plotted.
    */
   public abstract void reinit(IVirtualArray2D v2d);
//-----------------------=[ End abstract methods ]=---------------------------//
   
   
//---------=[ Implemented for the PropertyChangeHandler interface ]=----------//
   /**
    * This method is invoked whenever a property has changed.  Convience 
    * methods have been created for each particular property.  These methods 
    * simply invoke this method with the appropriate 
    * <code>property</code> parameter.
    * 
    * @param property An alias for the property that has changed.
    * @param value The property's new value.
    */
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
//-------=[ End implemented for the PropertyChangeHandler interface ]=--------//
}
