/*
 * File: ViewComponent3D.java
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
 *  Revision 1.5  2005/07/29 20:52:52  cjones
 *  Multiple pixels can now be selected through either mouse clicks or
 *  a side control list.  A single click will select a single pixel and a
 *  double click will select the entire detector of pixels.  Holding shift
 *  while clicking will add selection to current selected pixels, while holding
 *  ctrl will remove the selection.
 *
 *  A ControlList has been added to the view controls that will display
 *  selected pixels, as well as give options for adding and removing
 *  selections.
 *
 *  Revision 1.4  2005/07/27 20:36:44  cjones
 *  Added menu item that allows the user to choose between different shapes
 *  for the pixels. Also, in frames view, user can change the time between
 *  frame steps.
 *
 *  Revision 1.3  2005/07/25 21:27:56  cjones
 *  Added support for MouseArcBall and a control checkbox to toggle it. Also,
 *  the value of the selected pixel is now displayed with the Pixel Info, and
 *  updates on frame changes.
 *
 *  Revision 1.2  2005/07/22 21:42:54  cjones
 *  Added functionality for Orthographic Checkbox
 *
 *  Revision 1.1  2005/07/22 19:45:14  cjones
 *  Separated 3D components into one base object and two functional objects,
 *  one for data with frames and one for data without frames. Also, added features
 *  and tweaked functionality.
 *
 *
 */
 
package gov.anl.ipns.ViewTools.Components.ThreeD;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import java.util.Vector;

import SSG_Tools.Viewers.*;
import SSG_Tools.Viewers.Controls.MouseArcBall;
import SSG_Tools.Cameras.*;

import gov.anl.ipns.Util.Sys.ColorSelector;
import gov.anl.ipns.Util.StringFilter.StringFilter;

import gov.anl.ipns.ViewTools.Components.ThreeD.PixelBoxPicker;

import gov.anl.ipns.ViewTools.Components.*;
import gov.anl.ipns.ViewTools.Panels.Image.IndexColorMaker;
import gov.anl.ipns.ViewTools.Components.ThreeD.IViewComponent3D;
import gov.anl.ipns.ViewTools.Components.Menu.MenuItemMaker;
import gov.anl.ipns.ViewTools.Components.Menu.ViewMenuItem;
import gov.anl.ipns.ViewTools.Components.ViewControls.*;

import gov.anl.ipns.ViewTools.Components.ViewControls.AltAzController;

/**
 * This is a base class to be used by ViewComponents that will render a
 * scene using a JoglPanel viewer with a DetectorSceneBase object. This
 * object provides shared members and methods that these view components
 * will need to create the viewer.
 * 
 * Some controls require the JoglPanel (joglpane) viewer to be setup before
 * they can be correctly used or created.  It is recommened that inheriting
 * classes create the scene and joglpane before setuping controls.
 * 
 * This component uses a JOGL (Java OpenGL) based panel to render the scene.
 */
public abstract class ViewComponent3D implements IViewComponent3D
{  
  
 /**
  * "Camera Position" - use this static String to verify that the title of
  * the ViewControl returned is the AltAzController from the control panel.
  */
  public static final String ALTAZ_NAME = "Camera Position";
  
 /**
  * "ArcBall" - use this static String to verify that the title of
  * the ViewControl returned is the ControlCheckbox that toggles
  * MouseArcBall on/off.
  */
  public static final String ARCBALL_NAME = "ArcBall";
  
 /**
  * "Intensity Slider" - use this static String to verify that the title of
  * the ViewControl returned is the intensity slider.
  */
  public static final String INTENSITY_SLIDER_NAME = "Intensity Slider";
  
 /**
  * "Color Scale" - use this static String to verify that the title of
  * the ViewControl returned is the color scale from the control panel.
  */
  public static final String COLOR_SCALE_NAME = "Color Scale";
  
 /**
  * "Background Control" - use this static String to verify that the title of
  * the ViewControl returned is the background control from the control panel.
  */
  public static final String BACKGROUND_CONTROL_NAME = "Background Control";
  
 /**
  * "3D Point" - use this static String to verify that the title of
  * the ViewControl returned is the CursorOutputControl that displays
  * 3d coordinates from the control panel.
  */
  public static final String POINT_OUTPUT_NAME = "3D Point";
  
 /**
  * "Point IDs" - use this static String to verify that the title of
  * the ViewControl returned is the CursorOutputControl that displays
  * point ids from the control panel.
  */
  public static final String ID_OUTPUT_NAME = "Pixel Info";
  
  /**
   * "Selected List" - use this static String to verify that the title of
   * the ViewControl returned is the ControlList that displays
   * point ids for all selected points.
   */
   public static final String SELECTED_LIST_NAME = "Selected List";
   
 /**
  * "Frame Controller" - use this static String to verify that the title of
  * the ViewControl returned is the FrameController from the control panel.
  */
  public static final String FRAME_CONTROL_NAME = "Frame Controller";
   
 /**
  * "Selected Point Changed" - This messaging String is sent out to all
  * listeners when the selected point has been changed.
  */
  public static final String SELECTED_POINT_CHANGED = "Selected Point Changed";
  
  /* -- Data that will be used by ViewComponents -- */
  protected JoglPanel joglpane;
  protected int currentShapeType = DetectorSceneBase.BOX;
  
  protected LogScaleColorModel colormodel;
  protected String colorscale;
  protected double logscale = 0;
  protected boolean isTwoSided = true;
  
  protected ViewControl[] controls;
  protected ViewMenuItem[] menus;
  protected IPointList3D[] varrays;
  
  /* -- Controllers -- */
  private MouseArcBall arc_ball;
  private ControlCheckbox toggle_arcball;
  private AltAzController cam_controller;
  private CursorOutputControl point_output_control;
  private CursorOutputControl id_output_control;
  private ControlColorScale color_scale_control;
  private ControlList selected_list_control;
  private ColorControl background_control;
  private FrameController frame_control;
  
  private JPanel holder;  
  private float[] pointClicked;
  private int DetectorID, PixelID;
  private float pointval;
  
  private Vector Listeners;
  
  /**
   * Constructor.  Intializes color model and listeners.
   *  
   *   @param arrays The data that will be put into the scene
   */
  public ViewComponent3D( IPointList3D[] arrays )
  {
    colorscale = IndexColorMaker.HEATED_OBJECT_SCALE_2;
    colormodel = new LogScaleColorModel();
    
    Listeners = new Vector();
    
    varrays = arrays;
    
    //dataChanged(arrays);
    //buildControls();
    // buildMenu();
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
  * Retrieve the JPanel that contains the display component for
  * the scene.
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
    if(varrays != null && joglpane != null) 
      holder.add(joglpane.getDisplayComponent());
    
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
  * This method will be called whenever the color of the scene changes.
  * This can occur when the user moves the intensity slider, changes 
  * color model, or changes the data.  The scene will have the new
  * color applied, then it will be redrawn.
  * 
  * The LogScaleColorModel member, colormodel, can be used to
  * maps values to colors on a color scale, but the range of
  * data values need to be set before it properly map colors.
  * ( colormodel.setDataRange(float min, float max) )
  */ 
  abstract public void ColorAndDraw();
  
 /**
  * This method is called whenever the view component needs to update 
  * the detector pixels to have the currentShapeType.
  */
  abstract public void changeShape();
  
 /**
  * This method is invoked to notify the view component when the data
  * has changed within the same array. It redraws new scene.
  */
  abstract public void dataChanged();
  
 /**
  * This method is invoked to notify the view component when a new set of
  * data needs to be displayed.   If the data is not null, it creates a new
  * scene and panel based on the given data.
  *
  *  @param  arrays - virtual arrays of data
  */ 
  abstract public void dataChanged(IPointList3D[] arrays);
  
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
  	
  	varrays = null;
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
  
  /* --------------- MENU BAR -------------------------------------- */
  
  /*
   * Build the menu items for this component.
   * 
   * menus[0]: Options->ColorScaleMenu - Changes
   *           the color scale.
   * menus[1]: Options->Guides - turns guide markers on/off
   * menus[2]: Options->Pixel Shapes - changes
   *           shape of detector pixels.
   * menus[3]: Set time step between frames.
   */
   protected void buildMenu()
   {
     menus = new ViewMenuItem[4];
         
     menus[0] = new ViewMenuItem( ViewMenuItem.PUT_IN_OPTIONS,
                  MenuItemMaker.getColorScaleMenu( new ColorChangedListener()) );
     
     OverlayMenuListener overlay_listener = new OverlayMenuListener();     
     JMenu guides = new JMenu("Guides");
       JCheckBoxMenuItem tmp = new JCheckBoxMenuItem("Circle");
       tmp.setActionCommand("Circle");
       tmp.setState(true);
       tmp.addItemListener(overlay_listener);
       guides.add(tmp);
       
       tmp = new JCheckBoxMenuItem("Axis");
       tmp.setActionCommand("Axis");
       tmp.setState(true);
       tmp.addItemListener(overlay_listener);
       guides.add(tmp);
     
     menus[1] = new ViewMenuItem( ViewMenuItem.PUT_IN_OPTIONS, guides);
     
     
     Vector shapelistener = new Vector();     
     Vector shapes = new Vector();
     shapes.add("Pixel Shapes");
       shapes.add("Dot");
       shapes.add("Rectangle");
       shapes.add("Box");
     shapelistener.add(new ShapeChangedListener());
     
     menus[2] = new ViewMenuItem( ViewMenuItem.PUT_IN_OPTIONS,
            MenuItemMaker.makeMenuItem( shapes, shapelistener) );
     
     
     JMenuItem frame_step_time = new JMenuItem("Set step time...");
     frame_step_time.addActionListener(new FrameTimeStepListener());
     
     menus[3] = new ViewMenuItem( ViewMenuItem.PUT_IN_OPTIONS, frame_step_time );                  
   }
   
  /* --------------- CONTROL CREATION METHODS ---------------------- */
  
  /**
   * Get a control based on name.  If the control has not been
   * created by one of the create methods, null will be returned.
   * If no name matches a controller in this object, then null 
   * will be returned.
   * 
   *  @param control_name The static string name for the control.
   *  @return If control has been created, the control object will
   *          returned.  Otherwise, if it has not been set or the
   *          name is invalid, then null is returned.
   */
  protected ViewControl getControl(String control_name)
  {
  	// BACKGROUND COLOR CONTROLLER
    if(control_name == BACKGROUND_CONTROL_NAME)
      return background_control;
    // CAM CONTROLLER
    else if(control_name == ALTAZ_NAME)
      return cam_controller;
    // COLOR SCALE DISPLAY
    else if(control_name == COLOR_SCALE_NAME)
      return color_scale_control;
    // POINT OUTPUT
    else if(control_name == POINT_OUTPUT_NAME)
      return point_output_control;
    // PIXEL AND DETECTOR ID OUTPUT
    else if(control_name == ID_OUTPUT_NAME)
      return id_output_control;
    // FRAME CONTROLLER
    else if(control_name == FRAME_CONTROL_NAME)
      return frame_control;
    else
      return null;
  }
  
  /**
   * Create and return a ControlSlider for controlling the 
   * intensity of the applied color model.  Increasing the slider
   * will brighten the color and decreasing it will darken the color.
   * 
   * Moving the slider update the colormodel (LogScaleColorModel) data
   * member.
   * 
   * The name of this control is INTENSITY_SLIDER_NAME
   * 
   * @return
   */
  protected ControlSlider createIntensityControl()
  {
    ControlSlider intensity_slider = new ControlSlider();
    intensity_slider.setValue((float)logscale);
    intensity_slider.addActionListener(new IntensityChangedListener());
    
    intensity_slider.setTitle(INTENSITY_SLIDER_NAME);
    
    return intensity_slider;
  }
  
  /**
   * Create and return ColorControl to change the background color
   * of the current joglpane scene.  The initial color is set to Black.
   * 
   * The name of this control is BACKGROUND_CONTROL_NAME
   * 
   * @return Background color control.
   */
  protected ColorControl createBackgroundControl()
  {
    background_control = new ColorControl("Background Color", 
                                         " Background Color      ", 
                                          Color.BLACK, ColorSelector.TABBED);
    background_control.addActionListener(new BackgroundChangedListener());
    
    background_control.setTitle(BACKGROUND_CONTROL_NAME);
    
    return background_control;
  }
  
  /**
   * Create and return a AltAzController for controlling the camera
   * position around the scene. The joglpane must be setup with a
   * DetectorSceneBase object before calling this method. It will
   * position the intial camera according to scene output.
   * 
   * Intial settins for Altitute is 20 degrees and for Azimuthal 
   * is 45 degrees.
   * 
   * The name of this control is ALTAZ_NAME
   * 
   * @return The camera controller.
   */
  protected AltAzController createCamControl()
  {
    // Setup movement controller
    if(joglpane != null)
    {
      DetectorSceneBase scene = (DetectorSceneBase)joglpane.getScene();
      cam_controller = new AltAzController(20, 45, 1, 
                                           4*scene.getDiameter(), 
                                           1.5f*scene.getDiameter());    
    
      cam_controller.setVRP(joglpane.getCamera().getVRP());
      cam_controller.setVUV(joglpane.getCamera().getVUV());
      joglpane.getCamera().setCOP(cam_controller.getCOP());
    
      cam_controller.addActionListener( new CameraChangeListener() );
      
      cam_controller.setTitle(ALTAZ_NAME);
    }
    
    return cam_controller;
  }
  
  /**
   * Create and return a ControlCheckbox for toggling ArcBall mouse 
   * movement control on or off. When enabled, the Pixel Picking is
   * disabled.
   * 
   * The name of this control is ARCBALL_NAME
   * 
   * @return ControlCheckbox for toggling ArcBall
   */
  protected ControlCheckbox createArcBallControl()
  {
  	toggle_arcball = new ControlCheckbox(ARCBALL_NAME);
  	toggle_arcball.setText("Toggle ArcBall");
  	
    if(joglpane != null)
    {
      arc_ball = new MouseArcBall(joglpane);
      
      arc_ball.setEnabled(false);
      
      toggle_arcball.addActionListener( new ToggleArcBallListener() );
      
      joglpane.getDisplayComponent().addMouseListener( new CameraChangeHandler() );
    }
 
    return toggle_arcball;
  }
  
  /**
   * Create and return a ControlColorScale that will display the
   * currently selected color scale.
   * 
   * The name of this controller is COLOR_SCALE_NAME
   * 
   * @return The control for displaying the current color scale.
   */
  protected ControlColorScale createColorScaleControl()
  {
    // Setup color scale display
    color_scale_control = new ControlColorScale(colorscale, isTwoSided );
    
    color_scale_control.setTitle(COLOR_SCALE_NAME);
    
    return color_scale_control;
  }
  
  /**
   * Create and return a CursorOutputControl that will display
   * the 3d coordinates for a selected point in the
   * joglpane scene.  A PickHandler mouse handler object must
   * be added the joglpane for this to update correctly.
   * 
   * The name of this controller is POINT_OUTPUT_NAME
   * 
   *  @return The output control for selected point's coordinates
   */
  protected CursorOutputControl createPointOutputControl()
  {  	
    // Picked point
    String[] pointlabels = {"X","Y","Z"};
    point_output_control = new CursorOutputControl(pointlabels);
    
    point_output_control.setTitle(POINT_OUTPUT_NAME);
    
    return point_output_control;
  }
  
 /**
  * Create and return a CursorOutputControl that will display
  * the Pixel ID and Detector ID for a selected point in the
  * joglpane scene.  A PickHandler mouse handler object must
  * be added the joglpane for this to update correctly.
  * 
  * The name of this controller is ID_OUTPUT_NAME
  * 
  *  @return The output control for currently selected point ids.
  */
  protected CursorOutputControl createIDOutputControl()
  {  	
    // Picked point
  	String[] pixellabels = {"Detector", "Pixel", "Value"};
    id_output_control = new CursorOutputControl(pixellabels);
    
    id_output_control.setTitle(ID_OUTPUT_NAME);
    
    return id_output_control;
  }
  
  /**
   * Create and return a ControlList that will display
   * the Detector ID and Pixel ID for all selected points in the
   * joglpane scene.  A PickHandler mouse handler object must
   * be added the joglpane for this to update correctly.
   * 
   * The name of this controller is SELECTED_LIST_NAME
   * 
   *  @return The output control for selected points.
   */
   protected ControlList createSelectedListControl()
   {  	
     // Selected points
   	 selected_list_control = new ControlList("Det # Pixel #", new StringFilter());
   	 selected_list_control.setEntryBoxLabel("Det# Pixel#");
   	 
   	 // This is needed so that the list will expand larger than a single line
     selected_list_control.setPreferredSize( new Dimension(0, 30000) );
     
     selected_list_control.addActionListener(new SelectedListChangedListener());
     
     // Update list whenever selection has changed by the mouse
     addActionListener(new ActionListener()
     		  {
                public void actionPerformed(ActionEvent event)
                {
                  if (event.getActionCommand().equals(SELECTED_POINT_CHANGED))
                  {
                    String[] selected = ((DetectorSceneBase)joglpane.getScene()).
					                     getFormattedSelectedIDs();
                    
                    if(selected == null)
                    	selected_list_control.setControlValue(new Vector());
                    else
                      selected_list_control.setControlValue(selected);
                    
                    selected_list_control.repaint();
                  }
                }
              });
     
     selected_list_control.setTitle(SELECTED_LIST_NAME);
     
     return selected_list_control;
   }
  
 /**
  * Create and return a FrameController to handle stepping through
  * frames of a scene with lists of values for each point.  The
  * controller will number each given time value from 0...Number of Values-1
  * 
  * The name of this controller is FRAME_CONTROL_NAME
  * 
  *  @param time_values   The frame values. 
  *  @param step_time_ms  The time between continous frame changes in milliseconds.
  *  @return The frame controller.
  */
  protected FrameController createFrameControl(float[] time_values, int step_time_ms)
  {  	
    // Picked point
 	frame_control = new FrameController();
    frame_control.setFrame_values(time_values);
    frame_control.setStep_time(step_time_ms);
    
    frame_control.addActionListener( new FrameChangedListener() );
    
    frame_control.setTitle(FRAME_CONTROL_NAME);
    
    return frame_control;
  }
  
 /* ----------------------- METHODS FOR CONTROLS -----------------------------*/
  
 /**
  * This method will cause the selected items within the scene to blink
  * the a certain color.
  */
  public void blinkSelected()
  {
  	((DetectorSceneBase)joglpane.getScene()).colorSelected(Color.WHITE);
    joglpane.Draw();
    
    ColorAndDraw();
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

    if(color_scale_control != null)
    {
      color_scale_control.setColorScale( colorscale, isTwoSided ); 
      color_scale_control.repaint();
    }
    
    sendMessage(IColorScaleAddible.COLORSCALE_CHANGED);
    
    ColorAndDraw();
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

    sendMessage(IColorScaleAddible.COLORSCALE_CHANGED);
    
    ColorAndDraw();
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
    
    if(point_output_control != null)
    {
      point_output_control.setValue( 0, point[0] ); 
      point_output_control.setValue( 1, point[1] );
      point_output_control.setValue( 2, point[2] );
      point_output_control.repaint();
    }
  }
  
  /**
   * Set the value for the 3d point that has been clicked
   *
   *  @param  value The value of pixel
   */
   public void setPixelValueOutput( float value )
   {
     // If data is null, no data to display.
     if( varrays == null )
       return;
       
     pointval = value;
     
     if(point_output_control != null)
     {
       id_output_control.setValue( 2, pointval ); 
       point_output_control.repaint();
     }
   }
 
 /**
  * Set the IDs for the pixel and detector that have been clicked. -1 will be
  * used if the click did not match a pixel.
  *
  *  @param  detector The ID of the detector that has been selected.
  *  @param  pixel    The ID of the pixel that has been selected.
  *  @param  value    The value of the pixel.
  */
  public void setPixelClickOutput( int detector, int pixel, float value )
  {
    // If data is null, no data to display.
    if( varrays == null )
      return;
      
    PixelID = pixel;
    DetectorID = detector;
    pointval = value;
    
    if(id_output_control != null)
    {
      id_output_control.setValue( 0, detector ); 
      id_output_control.setValue( 1, pixel );
      id_output_control.setValue( 2, pointval );
      id_output_control.repaint();
    }
  }
  
  /* --------------------- LISENERS FOR CONTROLS -------------------*/
    
  /**
   * This handles mouse clicks to the JoglPanel scene. It takes the
   * closest selected pixel and extracts the pixel id and detector id
   * for that point.  It then updates the control outputs if they are
   * enabled. 
   * 
   * A single click will change the current Scene's selected pixels
   * to the pixel that was clicked.  A double click will change
   * the current Scene's selected pixels to all the pixels within
   * the detector that was clicked.  Holding shift and performing
   * one of the two specified clicks will add (union) that selection with
   * current selection.  Holding ctrl and performing one of the two
   * specified clicks will remove (subtract) that selection from the
   * current selection.
   * 
   * This must be added to the mousehandler for the joglpane!
   **/
  protected class PickHandler extends PixelBoxPicker
  {
    public PickHandler(JoglPanel jp)
    {
      super(jp);
    }
    
    public void mouseClicked (MouseEvent e)
    {
      if(toggle_arcball == null || toggle_arcball.isSelected() == false)
      {
        super.mouseClicked(e);
        
        if(e.getModifiersEx() != InputEvent.SHIFT_DOWN_MASK &&
           e.getModifiersEx() != InputEvent.CTRL_DOWN_MASK)
          	((DetectorSceneBase)joglpane.getScene()).clearSelected();
        
        if(e.getClickCount() == 1)
        {
          setPointClickOutput(get3DPoint());
          setPixelClickOutput(getDetectorID(), getPixelID(), getPixelValue());
          
          if(e.getModifiersEx() == InputEvent.CTRL_DOWN_MASK)
            ((DetectorSceneBase)joglpane.getScene()).
		       removeSelectedPixel(getPixelPickID());
          else
          	((DetectorSceneBase)joglpane.getScene()).
		       addSelectedPixel(getPixelPickID());

          blinkSelected();
        }
        else if(e.getClickCount() == 2)
        {          	
          if(e.getModifiersEx() == InputEvent.CTRL_DOWN_MASK)
          	((DetectorSceneBase)joglpane.getScene()).
		      removeSelectedDetector(getDetectorPickID());
          else 
          	((DetectorSceneBase)joglpane.getScene()).
		      addSelectedDetector(getDetectorPickID());
          
          blinkSelected();
        }
        
        sendMessage(SELECTED_POINT_CHANGED);
      }
    }
  }
  
  
  /*
   * Listens for add and remove messages from the selected list
   */
  private class SelectedListChangedListener implements ActionListener
  {
    public void actionPerformed(ActionEvent event)
    {
      // ITEM ADDED TO LIST
      if (event.getActionCommand().equals(ControlList.ITEM_ADDED))
      {
       	String value = selected_list_control.getEntryBoxValue();
       	value.trim();
        String[] ids = value.split("[ ]");
        
         // If user enters in form "Det: # Pixel: #"
         if(ids.length == 4 
         		&& ids[0].equalsIgnoreCase("Det:")
          		&& ids[2].equalsIgnoreCase("Pixel:"))
         {
           try {
             ((DetectorSceneBase)joglpane.getScene()).
	           addSelectedPixel(Integer.valueOf(ids[1]).intValue(),
		        		        Integer.valueOf(ids[3]).intValue());
           } catch(NumberFormatException ex) {}
        }
            
        // If user enters form "# #"
        else if(ids.length == 2)
        {
          try {
            ((DetectorSceneBase)joglpane.getScene()).
		      addSelectedPixel(Integer.valueOf(ids[0]).intValue(),
		        		       Integer.valueOf(ids[1]).intValue());
          } catch(NumberFormatException ex) {}
        }
            
        // Generate list and refresh
        String[] selected_pixels = ((DetectorSceneBase)joglpane.getScene()).
                                       getFormattedSelectedIDs();
        if(selected_pixels == null)
          selected_list_control.setControlValue(new Vector());
        else
          selected_list_control.setControlValue(selected_pixels);
        
        blinkSelected();
      }
      
      // ITEM REMOVED FROM LIST
      else if (event.getActionCommand().equals(ControlList.ITEM_REMOVED))
      {
         Object tmpvalues = selected_list_control.getControlValue();                  
         Object[] selected = (Object[])tmpvalues;
            
         int[] detids = new int[selected.length];
         int[] pixelids = new int[selected.length];
         for(int i = 0; i < selected.length; i++)
         {
           String value = selected[i].toString();
           String[] ids = value.split("[ ]");
              
           detids[i] = Integer.valueOf(ids[1]).intValue();
           pixelids[i] = Integer.valueOf(ids[3]).intValue();
         }
            
         ((DetectorSceneBase)joglpane.getScene()).
	       retainSelectedPixels(detids, pixelids);

         String[] selected_pixels = ((DetectorSceneBase)joglpane.getScene()).
                                       getFormattedSelectedIDs();
         if(selected_pixels == null)
           selected_list_control.setControlValue(new Vector());
         else
           selected_list_control.setControlValue(selected_pixels);
       }
     }
  }
  
 /**
  * This listens for messages sent out by the ViewMenuItems of
  * this view component. This must be added to the color scale
  * menu item to handle changes to the color scale display in the
  * control panel.
  */ 
  private class ColorChangedListener implements ActionListener
  {
    public void actionPerformed( ActionEvent ae )
    {
      setColorScale( ae.getActionCommand() );
    }
  }
   
  /**
   * This listens for messages sent out by the ViewMenuItems of
   * this view component. This must be added to the shape
   * menu item to handle changes to the pixel shape.
   */ 
   private class ShapeChangedListener implements ActionListener
   {
     public void actionPerformed( ActionEvent ae )
     {
       String type = ae.getActionCommand();
       int oldType = currentShapeType;
       
       if(type == "Box")
       	currentShapeType = DetectorSceneBase.BOX;
       else if(type == "Rectangle")
       	currentShapeType = DetectorSceneBase.RECTANGLE;
       else if(type == "Dot")
       	currentShapeType = DetectorSceneBase.DOT;
       else
       	currentShapeType = DetectorSceneBase.BOX;
       
       if(oldType != currentShapeType) changeShape();
       
       ColorAndDraw();
     }
   }
  
 /*
  * Handles menu changes to overlay selections
  */
  private class OverlayMenuListener implements ItemListener {
     public void itemStateChanged(ItemEvent e) {
         JCheckBoxMenuItem target = (JCheckBoxMenuItem) e.getSource();
         String actionCommand = target.getActionCommand();
         if (actionCommand.equals("Circle"))
         {
           //System.out.println("Circle toggle");
           if(target.getState())
           	((DetectorSceneBase)joglpane.getScene()).addSceneCircle();
           else
           	((DetectorSceneBase)joglpane.getScene()).removeSceneCircle();
         }
         else if (actionCommand.equals("Axis"))
         {
           //System.out.println("Axis toggle");
           if(target.getState())
           	((DetectorSceneBase)joglpane.getScene()).addLineAxes();
           else
           	((DetectorSceneBase)joglpane.getScene()).removeLineAxes();
         }
         
         ColorAndDraw();
       }
     }

  /*
   * If user wishes to set custom time step.
   */
  private class FrameTimeStepListener implements ActionListener
  {
    public void actionPerformed(ActionEvent e) {
 	  if(frame_control != null)
  	  {
  	    String s = (String)JOptionPane.showInputDialog(
              "How many milliseconds between frame steps?",
              "100");
  	    
  	    if( s != null && s.length() > 0)
  	    {
  	      try {
  	        int time = (Integer.valueOf(s)).intValue();
  	        frame_control.setStep_time(time);
  	      } catch(NumberFormatException ex) {}
  	    }
  	  }
  	}
  }
  
 /*
  * Listeners for changes to arcball toggler
  */
  private class ToggleArcBallListener implements ActionListener 
  {
     public void actionPerformed(ActionEvent e) {
         ControlCheckbox target = (ControlCheckbox) e.getSource();
         
         arc_ball.setEnabled(target.isSelected());
     }
  }
  
  /*
   * Updates AltAzController when ArcBall is finished moving
   */
  private class CameraChangeHandler extends MouseAdapter
  {
    public void mouseReleased( MouseEvent e )
    {
      if(joglpane != null && cam_controller != null && arc_ball != null)
      {
      	if(toggle_arcball != null && toggle_arcball.isSelected())
      	{
          Camera view = joglpane.getCamera();
        	
          /* AltAzController Should be Updated Here */
      	}
      }
    }
  }
  
  /*
   *  This listens for changes to background color
   */
  private class BackgroundChangedListener implements ActionListener
  {
    public void actionPerformed(ActionEvent event)
    {
      if (event.getActionCommand().equals(ColorControl.COLOR_CHANGED))
      {
        ((DetectorSceneBase)joglpane.getScene()).setBackgroundColor(
                                    background_control.getSelectedColor());
        joglpane.Draw();
      }
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
      // View Change
      if(joglpane != null && cam_controller != null) 
      {
      	if(e.getActionCommand().equals(SceneController.VIEW_CHANGED))
      	{
      	  Camera view = joglpane.getCamera();
      	
          view.setCOP(cam_controller.getCOP());
          joglpane.Draw();
      	}
      	
      	// Orthographic Checkbox Chage
      	else if(e.getActionCommand().equals(SceneController.PERSPECTIVE_CHANGED))
      	{
      	  if(cam_controller.isPerspective())
      	    joglpane.setCamera(new PerspectiveCamera(joglpane.getCamera()));
      	  else
      	    joglpane.setCamera(new OrthographicCamera(joglpane.getCamera()));
      	}
      }
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
   
  /*
   * This listens for changes to the frame controller
   */ 
   private class FrameChangedListener implements ActionListener
   {
     public void actionPerformed( ActionEvent ae )
     {       
       ColorAndDraw();
     }
   }
}