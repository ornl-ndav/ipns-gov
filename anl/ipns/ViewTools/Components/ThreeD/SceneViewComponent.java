/*
 * File: SceneViewComponent.java
 *
 * Copyright (C) 2005, Chad Jones
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
 * Primary   Chad Jones <cjones@cs.utk.edu>
 * Contact:  Student Developer, University of Tennessee
 *
 * Contact : Dennis Mikkelson <mikkelsond@uwstout.edu>
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751, USA
 *
 * This work was supported by the National Science Foundation under grant
 * number DMR-0218882.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 *  Modified:
 *
 *  $Log$
 *  Revision 1.1  2005/07/19 15:56:38  cjones
 *  Added components for Display3D.
 * 
 *
 */
 
package gov.anl.ipns.ViewTools.Components.ThreeD;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import java.util.Vector;

import SSG_Tools.Viewers.*;
import SSG_Tools.Cameras.*;

import gov.anl.ipns.ViewTools.Components.ThreeD.DetectorScene;
import gov.anl.ipns.ViewTools.Components.ThreeD.PixelBoxPicker;

import gov.anl.ipns.MathTools.Geometry.*;
import gov.anl.ipns.ViewTools.Panels.Image.IndexColorMaker;
import gov.anl.ipns.ViewTools.Components.ThreeD.IViewComponent3D;
import gov.anl.ipns.ViewTools.Components.IPhysicalArray3D;
import gov.anl.ipns.ViewTools.Components.Menu.MenuItemMaker;
import gov.anl.ipns.ViewTools.Components.Menu.ViewMenuItem;
import gov.anl.ipns.ViewTools.Components.ViewControls.*;
import gov.anl.ipns.ViewTools.Components.ObjectState;
import gov.anl.ipns.ViewTools.Components.LogScaleColorModel;

import gov.anl.ipns.ViewTools.Components.ViewControls.AltAzController;

/**
 * Any class that implements this interface will interpret and display
 * data in a three dimensional scene form.  Data is given as a 
 * IPhysicalArray3D[] and all data points are drawn to a color based on their 
 * value. The positions, extents, and orientations give each point its place 
 * and shape in space.
 * 
 * Controls are created to handle camera and data colors. A printout of
 * selected data appears under these controls.
 * 
 * This component uses a JOGL (Java OpenGL) based panel to render the scene.
 */
public class SceneViewComponent implements IViewComponent3D
{  
  
 /**
  * "Camera Position" - use this static String to verify that the title of
  * the ViewControl returned is the AltAzController from the control panel.
  */
  public static final String ALTAZ_NAME = "Camera Position";
  
 /**
  * "Intensity Slider" - use this static String to verify that the title of
  * the ViewControl returned is the intensity slider.
  */
  public static final String INTENSITY_SLIDER_NAME = "Intensity Slider";
  
 /**
  * "Color Scale" - use this static String to verify that the title of
  * the ViewControl returned is the color scale from the control panel.
  * This color scale is not the calibrated color scale bordering the image.
  */
  public static final String COLOR_SCALE_NAME = "Color Scale";
  
 /**
  * "Selected Point Changed" - This messaging String is sent out to all
  * listeners when the selected point has been changed.
  */
  public static final String SELECTED_POINT_CHANGED = "Selected Point Changed";
  
  private JoglPanel joglpane;
  private JPanel holder;
  private AltAzController controller;
  private String colorscale;
  private double logscale = 0;
  private boolean isTwoSided = true;
  private LogScaleColorModel colormodel;
  
  private float[] pointClicked;
  private int DetectorID, PixelID;
  
  private Vector Listeners;
  private ViewControl[] controls;
  private ViewMenuItem[] menus;
  private IPhysicalArray3D[] varrays;
  private float min_value, max_value;
  
  /**
   * Constructor.  Takes array of physical location information and 
   * builds the panel, controls, menus, and scene.
   *  
   *   @param arrays The data that will be put into the scene
   */
  public SceneViewComponent( IPhysicalArray3D[] arrays )
  {
    colorscale = IndexColorMaker.HEATED_OBJECT_SCALE_2;
    colormodel = new LogScaleColorModel();
    
    Listeners = new Vector();
    dataChanged(arrays);
    buildControls();
    buildMenu();
  }
  
 /**
  * This method will set the current state variables of the object to state
  * variables wrapped in the ObjectState passed in.
  *
  *  @param new_state
  */
  public void setObjectState( ObjectState new_state )
  {
    
  }
  
 /**
  * This method will get the current values of the state variables for this
  * object. These variables will be wrapped in an ObjectState.
  *
  *  @param  is_default True if default state, use static variable.
  *  @return if true, the selective default state, else the state for with
  *          all possible saved values.
  */ 
  public ObjectState getObjectState( boolean is_default )
  {
    ObjectState state = new ObjectState();

    return state;
  }
  
 /**
  * Add a listener to this view component. A listener will be notified
  * when a selected point or region changes on the view component.
  *
  *  @param  act_listener Action listener to add.
  */
  public void addActionListener( ActionListener act_listener )
  {      
    for ( int i = 0; i < Listeners.size(); i++ )    // don't add it if it's
      if ( Listeners.elementAt(i).equals( act_listener ) ) // already there
        return;

    Listeners.add( act_listener ); // Otherwise add act_listener
  }
 
 /**
  * Method to remove a listener from this component.
  *
  *  @param  act_listener Action listener to remove.
  */ 
  public void removeActionListener( ActionListener act_listener )
  {
    Listeners.remove( act_listener );
  }
 
 /**
  * Method to remove all listeners from this component.
  */ 
  public void removeAllActionListeners()
  {
    Listeners.removeAllElements();
  }
  
 /**
  * Retrieve the jpanel that this component constructs.
  *
  *  @return The JPanel containing the GUI SceneViewComponent.
  */
  public JPanel getDisplayPanel()
  {
    if(holder != null)
    {
      holder.removeAll();
      holder = null;
    }
  	
  	holder = new JPanel();
    holder.setSize(500, 500);
    holder.setLayout( new GridLayout() );
    holder.setMinimumSize(new java.awt.Dimension(10,10));
    holder.setVisible(true);
    if(varrays != null) holder.add(joglpane.getDisplayComponent());
    
    return holder;
  }
  
 /**
  * Retrieve the display component.
  *
  *  @return The GLCanvas of the scene.
  */
  public Component getDisplayComponent()
  { 
    return joglpane.getDisplayComponent();
  }
 
 /**
  * Returns all of the controls needed by this view component
  *
  *  @return controls
  */ 
  public ViewControl[] getControls()
  {    
    return controls;
  }
 
 /**
  * Returns all of the menu items needed by this view component
  *
  *  @return menus;
  */ 
  public ViewMenuItem[] getMenuItems()
  {
    return menus;
  }
  
 /**
  * This method is invoked to notify the view component when the data
  * has changed within the same array. It redraws new scene.
  */ 
  public void dataChanged()
  {
    if(varrays != null) 
    {
    	joglpane.Draw();
    }
  }
  
 /**
  * This method is invoked to notify the view component when a new set of
  * data needs to be displayed.   If the data is not null, it creates a new
  * scene and panel based on the given data.
  *
  *  @param  arrays - virtual arrays of data
  */ 
  public void dataChanged(IPhysicalArray3D[] arrays)
  { 
    joglpane = null;
   
    // Make sure data is valid. 
    if( arrays == null )
    {
      System.out.println("Null Data => No display.");
      controller = new AltAzController();
      return;
    }
   
    varrays = arrays;
   
    // Set min_value and max_value
    findDataRange();
    colormodel.setDataRange(min_value, max_value);
    
    // Create scene and place in rendering panel
    DetectorScene scene = new DetectorScene( varrays );
    scene.applyColor(colormodel);
    
    joglpane = new JoglPanel( scene );
    
    joglpane.setCamera( scene.makeCamera() );
    joglpane.enableLighting( true );
    joglpane.enableHeadlight( true );
    
    // Setup movement controller
    joglpane.getDisplayComponent().addMouseListener( 
                                   new PickHandler( joglpane ));
  
    controller = new AltAzController(45, 45, 1, 
                                     4*scene.getDiameter(), 
                                     -1.f*scene.getDiameter());    
    
    controller.setVRP(new gov.anl.ipns.MathTools.Geometry.Vector3D(
                        joglpane.getCamera().getVRP().get()));
    controller.setVUV(new gov.anl.ipns.MathTools.Geometry.Vector3D(
                        joglpane.getCamera().getVUV().get()));
    joglpane.getCamera().setCOP(new Vector3D(controller.getCOP().get()));
    
    controller.addActionListener( new CameraChangeListener() );
    
    dataChanged();
  }
  
 /**
  * This method is called by the viewer to inform the view component
  * it is no longer needed. In turn, the view component closes all windows
  * created by it before closing.
  */
  public void kill()
  {
  	if(joglpane != null)
  	{ 
  	  joglpane.setCamera(null);
  	  joglpane.setScene(null);
  	  joglpane = null;
  	}
  	
  	if(holder != null) 
  	{
  	  holder.removeAll();
  	  holder = null;
  	}
  }
  
 /*
  * Tells all listeners about a new action.
  *
  *  @param  message
  */  
  private void sendMessage( String message )
  {
    for ( int i = 0; i < Listeners.size(); i++ )
    {
      ActionListener listener = (ActionListener)Listeners.elementAt(i);
      listener.actionPerformed( new ActionEvent( this, 0, message ) );
    }
  }
  
 /*
  * Builds the view controls for this component.
  * Controls are:
  * 
  * controls[0]: ControlSlider - For intensity of
  *              color model.
  * controls[1]: ControlColorScale - Image bar
  *              representing current color scale.
  * controls[2]: AltAzController - Controls camera
  * controls[3]: CursorOutputControl - The 3D
  *              coordinates of mouse click.
  * controls[4]: CursorOutputControl - The IDs
  *              for detector and pixel selected.
  * 
  */
  private void buildControls()
  {
      controls = new ViewControl[5]; 
      
      // Control that adjusts the color intensity
      controls[0] = new ControlSlider();
      controls[0].setTitle(INTENSITY_SLIDER_NAME);
      ((ControlSlider)controls[0]).setValue((float)logscale);
      controls[0].addActionListener(new IntensityChangedListener());
      
      // Control that displays uncalibrated color scale
      controls[1] = new ControlColorScale(colorscale, isTwoSided );
      controls[1].setTitle(COLOR_SCALE_NAME);
      
      // Control that handles camera position
      controls[2] = controller;  
      controls[2].setTitle(ALTAZ_NAME);
      
      // Picked point
      String[] pointlabels = {"X","Y","Z"};
      controls[3] = new CursorOutputControl(pointlabels);
      
      // Picked pixel and detector
      String[] pixellabels = {"Detector","Pixel"};
      controls[4] = new CursorOutputControl(pixellabels);
  }
  
 /*
  * Build the menu items for this component.
  */
  private void buildMenu()
  {
    menus = new ViewMenuItem[1];
    
    menus[0] = new ViewMenuItem( ViewMenuItem.PUT_IN_OPTIONS,
                 MenuItemMaker.getColorScaleMenu( new ColorChangedListener()) );
                 
  }
   
 /**
  * Get the colorscale of the data values.
  *
  *  @return String colorscale code as defined by the IndexColorMaker class.
  *  @see gov.anl.ipns.ViewTools.Panels.Image.IndexColorMaker
  */
  public String getColorScale()
  {
    return colorscale;
  }
 
 /**
  * Set the colorscale representing the data in the scene.
  *
  *  @param  color_scale String code as defined by the IndexColorMaker class.
  *  @see gov.anl.ipns.ViewTools.Panels.Image.IndexColorMaker
  */
  public void setColorScale( String color_scale )
  {
    // If data is null, no data to display.
    if( varrays == null )
      return;
    
    colorscale = color_scale;
    colormodel.setNamedColorModel(colorscale, isTwoSided);
    ((DetectorScene)joglpane.getScene()).applyColor(colormodel);

    ((ControlColorScale)controls[1]).setColorScale( colorscale, isTwoSided ); 
    ((ControlColorScale)controls[1]).repaint();
    sendMessage(IColorScaleAddible.COLORSCALE_CHANGED);
    
    joglpane.Draw();
  }
   
 /**
  * Set the logscale representing the data in the scene.
  *
  *  @param  log_scale Log scale.
  */
  public void setLogScale( double log_scale )
  {
    // If data is null, no data to display.
    if( varrays == null )
      return;
     
    logscale = log_scale;
    colormodel.setLogScale(logscale);
    ((DetectorScene)joglpane.getScene()).applyColor(colormodel);

    sendMessage(IColorScaleAddible.COLORSCALE_CHANGED);
    
    joglpane.Draw();
  }
  
 /**
  * Set the coordinates for the 3d point that has been clicked
  *
  *  @param  point The x,y,z coordinates in a float array.
  */
  public void setPointClickOutput( float[] point )
  {
    // If data is null, no data to display.
    if( varrays == null )
      return;
      
    pointClicked = point;
    
    ((CursorOutputControl)controls[3]).setValue( 0, point[0] ); 
    ((CursorOutputControl)controls[3]).setValue( 1, point[1] );
    ((CursorOutputControl)controls[3]).setValue( 2, point[2] );
    ((CursorOutputControl)controls[3]).repaint();
    sendMessage(SELECTED_POINT_CHANGED);
  }
 
 /**
  * Set the IDs for the pixel and detector that have been clicked. -1 will be
  * used if the click did not match a pixel.
  *
  *  @param  detector The ID of the detector that has been selected.
  *  @param  pixel    The ID of the pixel that has been selected.
  */
  public void setPixelClickOutput( int detector, int pixel )
  {
    // If data is null, no data to display.
    if( varrays == null )
      return;
      
    PixelID = pixel;
    DetectorID = detector;
    
    ((CursorOutputControl)controls[4]).setValue( 0, detector ); 
    ((CursorOutputControl)controls[4]).setValue( 1, pixel );
    ((CursorOutputControl)controls[4]).repaint();
    sendMessage(SELECTED_POINT_CHANGED);
  }
 
 /*
  * Finds the range of data values for current arrays. 
  * Sets max_value and min_value.
  */
  private void findDataRange()
  {
  	if(varrays == null)
  		return;
  	
  	float max = Float.NEGATIVE_INFINITY;
  	float min = Float.POSITIVE_INFINITY;
  	float value = 0;
  	
    for(int arr = 0; arr < varrays.length; arr++)
    {
      if(varrays[arr] != null)
        for(int i = 0; i < varrays[arr].getNumPoints(); i++)
        {
          value = varrays[arr].getValue(i);
        
          if(value > max) max = value;
          if(value < min) min = value;
        }
    }
    
    max_value = max;
    min_value = min;
  }
  
  /*
   * This listens for changes the CursorOutputControl's for 3d coordinates
   * and pixel/detector ids.
   **/
  private class PickHandler extends PixelBoxPicker
  {
    public PickHandler(JoglPanel jp)
    {
      super(jp);
    }
    
    public void mouseClicked (MouseEvent e)
    {
      super.mouseClicked(e);
      setPointClickOutput(get3DPoint());
      setPixelClickOutput(getDetectorID(), getPixelID());
    }
  }
  
  /*
   * This listens for changes to the AltAzController and
   * adjusts the camera view
   **/
  private class CameraChangeListener implements ActionListener
  {
    public void actionPerformed(ActionEvent e)
    {
      Camera view = joglpane.getCamera();
      
      view.setCOP(new Vector3D(controller.getCOP().get()));
      joglpane.Draw();
    }
  }
  
 /*
  * This listens for messages sent out by the ViewMenuItems of
  * this view component.
  */ 
  private class ColorChangedListener implements ActionListener
  {
    public void actionPerformed( ActionEvent ae )
    {
      setColorScale( ae.getActionCommand() );
    }
  }
  
  /*
   * This listens for messages sent out by the intensity slider.
   */ 
   private class IntensityChangedListener implements ActionListener
   {
   	 public void actionPerformed( ActionEvent ae )
     {
       String message = ae.getActionCommand();
   	
       if ( message == ControlSlider.SLIDER_CHANGED )
       {
         ControlSlider control = (ControlSlider)ae.getSource();
         setLogScale( control.getValue() );
       }
     }
   }
}
