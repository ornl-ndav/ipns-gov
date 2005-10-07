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
 * Revision 1.3  2005/10/07 21:32:37  kramer
 * Added javadoc comments for every field, constructor, method, inner class,
 * etc. in this class.
 *
 * Revision 1.2  2005/07/29 15:23:50  kramer
 *
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
 * This is a module of the <code>ContourViewComponent</code> that is 
 * responsible for recording and maintaining the state of whether a solid 
 * color or colorscale is being used to color the contour plot displayed by 
 * the <code>ContourViewComponent</code>.  It also records where the 
 * colorscale's control is located (on the control panel, to the right of 
 * the contour plot, or below the contour plot).
 */
public class ContourColorScaleHandler extends ContourChangeHandler
{
//------------------------=[ ObjectState keys ]=------------------------------//
   /**
    * "Use colorscale key" - This static constant String is a key used for 
    * referencing the state information that records if a colorscale is 
    * currently being used to color the contour plot that this class is 
    * listening to.  The value that this key references is 
    * <code>Boolean</code>.
    */
   public static final String USE_COLORSCALE_KEY = 
                                 "Use colorscale key";
   /**
    * "Colorscale location key" - This static constant String is a key used 
    * for referencing the state information that records where the 
    * colorscale's control is located (on the control panel, to the right 
    * of the contour plot, or below the contour plot).  The value that this 
    * key references is <code>String</code> and is one of the values:
    * <ul>
    *   <li>{@link ContourChangeHandler#CONTROL_PANEL_LOCATION 
    *                                   CONTROL_PANEL_LOCATION}</li>
    *   <li>{@link ContourChangeHandler#BELOW_IMAGE_LOCATION 
    *                                   BELOW_IMAGE_LOCATION}</li>
    *   <li>{@link ContourChangeHandler#RIGHT_IMAGE_LOCATION 
    *                                   RIGHT_IMAGE_LOCATION}</li>
    *   <li>{@link ContourChangeHandler#NONE_LOCATION 
    *                                   NONE_LOCATION}</li>
    * </ul>
    */
   public static final String COLORSCALE_CONTROL_LOCATION_KEY = 
                                 "Colorscale location key";
   /**
    * "Colorscale visible key" - This static constant String is a key used 
    * for referencing the state information that records if a colorscale's 
    * control is currently visible on the <code>ContourViewComponent</code> 
    * that this object is listening to.  The value that this key references 
    * is a <code>Boolean</code>.
    */
   public static final String COLORSCALE_CONTROL_VISIBLE_KEY = 
                                 "Colorscale visible key";
//----------------------=[ End ObjectState keys ]=----------------------------//
   
   
//--------------------=[ Default field values ]=------------------------------//
   /**
    * "false" - Specifies if by default a colorscale is used to color the 
    *           contour plot of the <code>ContourViewComponent</code> that 
    *           this class is listening to.
    */
   public static final boolean DEFAULT_USE_COLORSCALE = false;
   
   /**
    * Specifies the default location of the colorscale's control that is 
    * used with the contour plot of the <code>ContourViewComponent</code> 
    * that this class is listening to.  The value of this field is the 
    * value of the field {@link ContourChangeHandler#CONTROL_PANEL_LOCATION 
    * CONTROL_PANEL_LOCATION}.
    */
   public static final String DEFAULT_COLORSCALE_LOCATION = 
                                 CONTROL_PANEL_LOCATION;
   /**
    * "true" - Specifies if, by default, the colorscale's control, which is 
    *          used with the contour plot of the 
    *          <code>ContourViewComponent</code> that this class is 
    *          listening to, is visible.
    */
   public static final boolean DEFAULT_COLORSCALE_VISIBLE = true;
//------------------=[ End default field values ]=----------------------------//
   
   
//-----------------------------=[ Fields ]=-----------------------------------//
   /**
    * Records if the <code>ContourViewComponent</code> that this class is 
    * listening to is using a colorscale to color its contour plot.
    */
   private boolean usesColorscale;
   
   /**
    * Records if the colorscale's control on the 
    * <code>ContourViewComponent</code> that this class is listening to 
    * is visible or not.
    */
   private boolean colorscaleControlVisible;
   
   /**
    * Records the last <b>visible</b> location of the colorscale's control on 
    * the <code>ContourViewComponent</code> that this class is listening to.  
    */
   private String lastVisColorscaleLoc;
//---------------------------=[ End fields ]=---------------------------------//
   
   
//--------------------------=[ Constructors ]=--------------------------------//
   /**
    * Constructs a module for a 
    * {@link ContourViewComponent ContourViewComponent} that handles 
    * working with the colorscale/color that is currently being used to 
    * color the contour plot.
    * 
    * @param connector          Serves to connect several modules of a 
    *                           {@link ContourViewComponent 
    *                           ContourViewComponent} so that if a 
    *                           property in one module is changed, the 
    *                           other modules are notified.
    * @param center             Serves as the central location where the 
    *                           data shared between several modules of a 
    *                           {@link ContourViewComponent 
    *                           ContourViewComponent} is stored.
    * @param panel              The panel that is responsible for 
    *                           rendering the contour plot.
    * @param useColorscale      Specifies if a colorscale is initially 
    *                           being used to color the contour plot.  
    *                           That is <code>true</code> specifies that 
    *                           a colorscale is being used and 
    *                           <code>false</code> specifies that a 
    *                           solid color is being used to color the 
    *                           contour plot.
    * @param colorscaleLocation Specifies the initial location of the 
    *                           colorscale's control.  The value of 
    *                           this parameter should be one of the 
    *                           following:  
    * <ul>
    *   <li>{@link ContourChangeHandler#CONTROL_PANEL_LOCATION 
    *                                   CONTROL_PANEL_LOCATION}</li>
    *   <li>{@link ContourChangeHandler#BELOW_IMAGE_LOCATION 
    *                                   BELOW_IMAGE_LOCATION}</li>
    *   <li>{@link ContourChangeHandler#RIGHT_IMAGE_LOCATION 
    *                                   RIGHT_IMAGE_LOCATION}</li>
    *   <li>{@link ContourChangeHandler#NONE_LOCATION 
    *                                   NONE_LOCATION}</li>
    * </ul>
    */
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
   /**
    * Implemented for the <code>ContourChangeHandler</code> class.  This 
    * method is invoked when this module needs to be re-initialized with the 
    * given data.  However, if the data changes, nothing needs to be done 
    * by this module.  Thus, this method does nothing.
    * 
    * @param v2D The new data that is to be displayed by the 
    *            <code>ContourViewComponent</code> that this class is 
    *            listening to.
    * 
    * @see ContourChangeHandler#reinit(IVirtualArray2D)
    */
   public void reinit(IVirtualArray2D v2D)
   {
      //nothing needs to be done if the virutal array is changed
   }
   
   /**
    * Implemented for the <code>ContourChangeHandler</code> class.  This 
    * method is invoked when the colorscale being used by the 
    * <code>ContourViewComponent</code> that this class is listening to 
    * changes.
    * 
    * @param colorscale The new colorscale that is being used.
    * 
    * @see ContourChangeHandler#changeColorScaleName(String)
    */
   public void changeColorScaleName(String colorscale)
   {
      this.usesColorscale = true;
      
      colorScaleLocationChanged(this.lastVisColorscaleLoc);
   }
   
   /**
    * Implemented for the <code>ContourChangeHandler</code> class.  This 
    * method is invoked when the color that is being used [to color the 
    * contour plot] by the <code>ContourViewComponent</code> that this class 
    * is listening to changes.
    * 
    * @param color The new color that is being used.
    * 
    * @see ContourChangeHandler#changeColor(Color)
    */
   public void changeColor(Color color)
   {
      this.usesColorscale = false;
      
      colorScaleLocationChanged(NONE_LOCATION);
   }
   
   /**
    * Implemented for the <code>ContourChangeHandler</code> class.  This 
    * method is invoked when the colorscale's control being used by the 
    * <code>ContourViewComponent</code> that this class is listening to 
    * changes its location.
    * 
    * @param location A string describing the colorscale's control's new 
    *                 location.
    * 
    * @see ContourChangeHandler#changeColorScaleLocation(String)
    */
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
   /**
    * Used to set the state information of this object to match the state 
    * information encapsulated in the <code>ObjectStage</code> parameter 
    * given.
    * 
    * @param state An encapsulation of this Object's state.
    */
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
   
   /**
    * Used to get an encapsulation of this Object's state information.
    * 
    * @param isDefault If <code>true</code>, this Object's default state 
    *                  is returned.  Otherwise, its current state is 
    *                  returned.
    * 
    * @return An encapsulation of this Object's state.
    */
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
   /**
    * Used to determine if the <code>ContourViewComponent</code> that this 
    * class is listening to is using a colorscale to color its contour plot.
    * 
    * @return <code>True</code> if the contour plot is being colored by a 
    *         colorscale and <code>false</code> if it is being colored by 
    *         a solid color.
    */
   public boolean getUsesColorScale()
   {
      return usesColorscale;
   }
   
   /**
    * Used to change this class's record which records if the 
    * <code>ContourViewComponent</code> that is being listened to by this 
    * class has its contour plot colored by a colorscale or not.  Note:  
    * this method changes this classes record and notifies all listeners that 
    * the state of whether the colorscale is being used has changed.
    * 
    * @param useColorscale <code>True</code> if a colorscale should be used 
    *                      and <code>false</code> if a solid color should 
    *                      be used.
    */
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