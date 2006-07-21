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
 * This work was supported by the University of Tennessee Knoxville and 
 * the Spallation Neutron Source at Oak Ridge National Laboratory under: 
 *   Support of HFIR/SNS Analysis Software Development 
 *   UT-Battelle contract #:   4000036212
 *   Date:   Oct. 1, 2004 - Sept. 30, 2006
 *
 *  Modified:
 *
 *  $Log$
 *  Revision 1.7  2006/07/21 13:57:29  dennis
 *  Cleaned up some formatting problems caused by tabs.
 *  Commented out some currently unused variables.
 *
 *  Revision 1.6  2005/08/04 22:33:17  cjones
 *  ViewComponent3D now uses SelectedListItem to convert string items into
 *  int arrays and vice versa. SelectedListItem uses IntList to (de)construct
 *  strings, so multiple ids can be placed on a single line.
 *  Also, ViewComponent3D now contains controls help frame until help menu.
 *
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
 *  one for data with frames and one for data without frames. Also, added 
 *  features and tweaked functionality.
 *
 */
 
package gov.anl.ipns.ViewTools.Components.ThreeD;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;

import java.util.Vector;

import SSG_Tools.Viewers.*;
import SSG_Tools.Viewers.Controls.MouseArcBall;
import SSG_Tools.Cameras.*;

import gov.anl.ipns.Util.Sys.ColorSelector;
import gov.anl.ipns.Util.Sys.WindowShower;
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
 * The inheriting classes must define methods for constucting the joglpane
 * and scene when the data has changed.  In addition, they must provide
 * methods for coloring, drawing, and updating the scene shapes. Once all
 * scene information is set, the class needs to intialize the controls 
 * field by generator the controls with this object's control creation
 * methods.
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
  
  private JPanel holder; // Holder for the display component  
//  private float[] pointClicked;
//  private int DetectorID, PixelID;
  private float pointval;
  
  private Vector Listeners;
  private static JFrame helper = null;
  
  /**
   * Constructor.  Intializes color model and listeners.  Sets
   * the given array to current data.
   *  
   *   @param arrays The data that will be put into the scene
   */
  public ViewComponent3D( IPointList3D[] arrays )
  {
    colorscale = IndexColorMaker.HEATED_OBJECT_SCALE_2;
    colormodel = new LogScaleColorModel();
    
    Listeners = new Vector();
    
    varrays = arrays;
    
    /* The inheriting classes should have calls:
     dataChanged(arrays); // setup joglpane
     buildControls();     // generate controls[]
     buildMenu();         // generate menus[]
    */
  }
  
 /**
  * This method will set the current state variables of the object to state
  * variables wrapped in the ObjectState passed in.
  * 
  * UNFINISHED
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
  * UNFINISHED
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
  *  @return The JPanel containing the view component to display
  *          the 3D scene.
  */
  public JPanel getDisplayPanel()
  {
    // The holder is necessary because JoglPanel does
    // not extend from any swing components.

    if(holder != null)
    {
      holder.removeAll();
      holder = null;
    }
  	
    holder = new JPanel();
    holder.setSize(500, 500);
    holder.setLayout( new GridLayout() );
    // Setting minimum size allows jpanel to be resized
    holder.setMinimumSize(new java.awt.Dimension(10,10));
    holder.setVisible(true);
    
    // If the JoglPanel has not been set, then the
    // holder will be empty
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
  * Returns all of the controls needed by this view component.
  *
  *  @return controls
  */ 
  public ViewControl[] getControls()
  {    
  	//Needs to be setup by inheriting class
    return controls;
  }
  
 /**
  * Returns all of the menu items needed by this view component.
  *
  *  @return menus
  */ 
  public ViewMenuItem[] getMenuItems()
  {
  	//Needs to be setup by inheriting class
    return menus;
  }
  
 /**
  * This method will be called whenever the color of the scene changes.
  * This can occur when the user moves the intensity slider, changes 
  * color model, or changes the data.  The scene will have the new
  * color applied, then it will be redrawn.
  * 
  * The LogScaleColorModel member, colormodel, can be used to
  * map values to colors on a color scale, but the range of
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
  *  @param  arrays Virtual arrays of data
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
  	
    if( helper != null )
        helper.dispose();
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
     menus = new ViewMenuItem[5];
         
     // Change color scale. This update side control.
     menus[0] = new ViewMenuItem( ViewMenuItem.PUT_IN_OPTIONS,
                MenuItemMaker.getColorScaleMenu( new ColorChangedListener()) );
     
     // This menu option will toggle scene guides within the DetectorSceneBase
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
     
     // Change the shape of the pixels
     Vector shapelistener = new Vector();     
     Vector shapes = new Vector();
     shapes.add("Pixel Shapes");
       shapes.add("Dot");
       shapes.add("Rectangle");
       shapes.add("Box");
     shapelistener.add(new ShapeChangedListener());
     
     menus[2] = new ViewMenuItem( ViewMenuItem.PUT_IN_OPTIONS,
            MenuItemMaker.makeMenuItem( shapes, shapelistener) );
     
     // Allow user to set the time between step changes when
     // "fast forwarding" using the frame controller.
     JMenuItem frame_step_time = new JMenuItem("Set step time...");
     frame_step_time.addActionListener(new FrameTimeStepListener());
     
     menus[3] = new ViewMenuItem(ViewMenuItem.PUT_IN_OPTIONS, frame_step_time);
     
     Vector help = new Vector();
     Vector help_listeners = new Vector();
     help.add("Scene Help");
       help.add( "Using Controls" );
       help_listeners.add( new HelpListener() );
       
       menus[4] = new ViewMenuItem( ViewMenuItem.PUT_IN_HELP,
            MenuItemMaker.makeMenuItem( help, help_listeners) );
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
    // SELECTED LIST
    else if(control_name == SELECTED_LIST_NAME)
      return selected_list_control;
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
   * @return The control slider for changing color intensity.
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
   * Intial setting for Altitute is 20 degrees and for Azimuthal 
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
    
      // Get the current view direction and up direction that the
      // scene set.
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
   * disabled.  ArcBall is intially toggled off.  The joglpane must
   * be intialized before this control can be created.
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
      
      // Add the mouse listener to the GLCanvas of the JoglPanel
      joglpane.getDisplayComponent().addMouseListener( 
                                                  new CameraChangeHandler() );
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
   * the Detector IDs and Pixel IDs for all selected points in the
   * joglpane scene.  A PickHandler mouse handler object must
   * be added the joglpane for this to update correctly with 
   * mouse clicks.
   * 
   * The list is updated whenever a line is added or removed or 
   * when the user mouse selects pixels.
   * 
   * The name of this controller is SELECTED_LIST_NAME
   * 
   *  @return The output control for selected points.
   */
   protected ControlList createSelectedListControl()
   {  	
     // Selected points
     selected_list_control = 
                       new ControlList("Det # Pixel #", new StringFilter());
     selected_list_control.setEntryBoxLabel("Det# Pixel#");
 
     // This is needed so that the list will expand larger than a single line
     selected_list_control.setPreferredSize( new Dimension(0, 30000) );
     
     // Update list whenever Add or Remove button is pressed.
     selected_list_control.addActionListener(new SelectedListChangedListener());
     
     // Update list whenever selection has changed by the mouse
     addActionListener(new ActionListener()
     {
        public void actionPerformed(ActionEvent event)
        {
           if (event.getActionCommand().equals(SELECTED_POINT_CHANGED))
           {
             Vector selected = ((DetectorSceneBase)joglpane.getScene()).
                                 getSelectedItems();
                    
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
  *  @param step_time_ms  The time between continous frame changes in 
  *                       milliseconds.
  *  @return The frame controller.
  */
  protected FrameController createFrameControl(float[] time_values, 
                                               int     step_time_ms)
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
  * white.  The blink time varies with the time it takes to render the 
  * scene
  */
  public void blinkSelected()
  {
    // Color is currently white.  This could be made into a user changable
    // variable or some color that could be calculated.
    ((DetectorSceneBase)joglpane.getScene()).colorSelected(Color.WHITE);
  
    // Draw once with the selected pixels colored different.
    joglpane.Draw();
    
    // Color pixels to original values and draw again.
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
      
//    pointClicked = point;
    
    if(point_output_control != null)
    {
      point_output_control.setValue( 0, point[0] ); 
      point_output_control.setValue( 1, point[1] );
      point_output_control.setValue( 2, point[2] );
      point_output_control.repaint();
    }
  }
  
  /**
   * Set the value portion of the pixel output control
   * to the given value of the pixel.  This can be used
   * to update the value when frame changes are made.
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
  * Set the IDs for the pixel and detector and the pixel value 
  * for the picked pixel.
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
      
//    PixelID = pixel;
//    DetectorID = detector;
    pointval = value;
    
    if(id_output_control != null)
    {
      id_output_control.setValue( 0, detector ); 
      id_output_control.setValue( 1, pixel );
      id_output_control.setValue( 2, pointval );
      id_output_control.repaint();
    }
  }
  
  
  /**
   * Contains control information about the TableJPanel. To view the information,
   * use the WindowShower to visualize the help frame.
   *
   *  @return JFrame containing help information for the table.
   */
   private static void help()
   {
    helper = new JFrame("Help with Scene Controls");
    helper.setBounds(0,0,600,400);
    JEditorPane textpane = new JEditorPane();
    textpane.setEditable(false);
    textpane.setEditorKit( new HTMLEditorKit() );
    String text = "<H1>Controls:</H1> <P>" + 
       "<H2>Colors:</H2> <p>" +
       "The dataset that provides the 3d information for the " +
       "pixels also provides the information for pixel data " +
       "values. The data value is mapped into a color table based " +
       "on the set log scale intensity. The current color model selected " +
       "can be viewed on the side control. To change the color model, " +
       "goto the menu Options->Color Scale... <br>" +
       " The intensity slider will change the log scale for mapping " +
       "values to colors, thus increasing or decreasing the brightness " +
       "of the colors.  This may be necessary to obtain meaningful colors " +
       " with large or small data values. " +
       "<H2>Camera:</H2> <p>" +
       "The camera can be controlled two ways:<br>" +
       "1. Using the Altitute, Azimuth, and distance sliders, or <br>" +
       "2. Mouse controlled ArcBall<br>" +
       "Using the sliders, Altitute will move the camera above or below " +
       "the scene and the Azimuth will move the camera around the scene " +
       "to the left or right. To use ArcBall, toggle the checkbox on, and then, " +
       "press and hold the mouse button on the scene and drag in the direction " +
       "you would like to move the object." +
       "<H2>Pixel Picking:</H2> <p>" +
       "Whenever you click on a individual pixel, a side control will display " +
       "that pixel's 3D coordinates along with the pixel's IDs and value. " +
       "Their are two IDs when a pixel is picked, the Detector ID that is " +
       "specified by the array for the detector and the pixel ID that is " +
       "the index of the pixel in the detector dataset. The value of the pixel " +
       "is used to color the pixel. <b>Note: Picking is disabled if ArcBall is " +
       "toggled on.</b>" +
       "<H2>Pixel Selecting:</H2> <p>" +
       "Several pixels may be selected at once.  To select a pixel, single click " +
       "on its shape.  To add more pixels to the selection, shift mouse-click on " +
       "additional pixels.  To remove pixels from the selection, ctrl mouse-click " +
       "on currently selected pixels.  Double clicking will include the whole " +
       "detector instead on an individual pixel. All currently selected pixels " +
       "are stored in a side text list by Detector ID followed by Pixel IDs. " +
       "Individual IDs are seperated by commas and ranges of IDs are denoted " +
       "with a colon in the middle. Selections can be removed by clicking a list " +
       "item and pressing 'Remove'.  Selections can be added by inputing into the " +
       "field box, detector id numbers followed by pixel id numbers and a space " +
       "between the two.  The id numbers can be given using the previously " +
       "described syntax." +
       "<H2>Frame Control:</H2> <p>" +
       "If the data contained data values over frames, a frame controller will " +
       "be provided to traverse frames.  The frames can be moved forward or " +
       "backward one click at a time or rapidly using the autoplay buttons. " +
       "You can stop the autoplay by clicking the middle 'Pause' button. To " +
       "set the time between autoplay frame steps, use the menu option " +
       "Options->Set step time...";
    textpane.setText(text);
    JScrollPane scroll = new JScrollPane(textpane);
    scroll.setVerticalScrollBarPolicy(
        			    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    helper.getContentPane().add(scroll);
    WindowShower.show(helper);
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
   */
  protected class PickHandler extends PixelBoxPicker
  {
    public PickHandler(JoglPanel jp)
    {
      super(jp);
    }
    
    public void mouseClicked (MouseEvent e)
    {
      // Using ArcBall will disable the pixel picking
      if(toggle_arcball == null || toggle_arcball.isSelected() == false)
      {
      	// Continue with picking since arcball is not being used.
        super.mouseClicked(e);
        
        // If the user is not holding shift or ctrl, then the preivously
        // selected pixels are cleared since it is a clean click.
        if(e.getModifiersEx() != InputEvent.SHIFT_DOWN_MASK &&
           e.getModifiersEx() != InputEvent.CTRL_DOWN_MASK)
          	((DetectorSceneBase)joglpane.getScene()).clearSelected();
        
        // SINGLE CLICK - Single pixel selection.
        if(e.getClickCount() == 1)
        {
          // Set the output information for the two side controls.
          setPointClickOutput(get3DPoint());
          setPixelClickOutput(getDetectorID(), getPixelID(), getPixelValue());
          
          // CTRL with mouse means the selection should be removed.
          if(e.getModifiersEx() == InputEvent.CTRL_DOWN_MASK)
            ((DetectorSceneBase)joglpane.getScene()).
		       removeSelectedPixel(getPixelPickID());
          // Otherwise the selection is added
          else
          	((DetectorSceneBase)joglpane.getScene()).
		       addSelectedPixel(getPixelPickID());

          // Show the current selection
          blinkSelected();
        }
        // DOUBLE CLICK - Detector selection.
        else if(e.getClickCount() == 2)
        {          	
          // CTRL with mouse means the selection should be removed.
          if(e.getModifiersEx() == InputEvent.CTRL_DOWN_MASK)
          	((DetectorSceneBase)joglpane.getScene()).
		      removeSelectedDetector(getDetectorPickID());
          // Otherwise the selection is added.
          else 
          	((DetectorSceneBase)joglpane.getScene()).
		      addSelectedDetector(getDetectorPickID());
          
          // Show the current selection
          blinkSelected();
        }
        
        // Send message (update control list)
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
      	// Get the string that the user typed in
       	String value = selected_list_control.getEntryBoxValue();
       	value.trim();
       	
       	// The ids for the detectors should be seperated from
       	// the ids for the pixels by a space
        String[] ids = value.split("[ ]");
                    
        // If user enters form "# #"
        // Ignore addition if user did not enter it correctly
        if(ids.length == 2)
        {
          // Send the SelectedListItem with ids to scene.
          ((DetectorSceneBase)joglpane.getScene()).
		    addSelectedPixel(new SelectedListItem(ids[0], ids[1]));
        }
            
        // Generate list and refresh
        Vector selected_pixels = ((DetectorSceneBase)joglpane.getScene()).
                                       getSelectedItems();
        
        selected_list_control.setControlValue(selected_pixels);
        
        blinkSelected();
      }
      
      // ITEM REMOVED FROM LIST
      else if (event.getActionCommand().equals(ControlList.ITEM_REMOVED))
      {  /* This part could be improved if there was some method in
            ControlList to get the item that was removed 
          */
      	
      	 // Since something has removed, get all the values that are left
         Object tmpvalues = selected_list_control.getControlValue();                  
         Object[] selected = (Object[])tmpvalues;
            
         // Each value left is a SelectedListItem
         SelectedListItem[] items = new SelectedListItem[selected.length];
         for(int i = 0; i < selected.length; i++)
         	items[i] = (SelectedListItem)selected[i];
            
         // Tell scene to only keep the items that are still in the list.
         ((DetectorSceneBase)joglpane.getScene()).
	       retainSelectedPixels(items);

         // Update list
         Vector selected_pixels = ((DetectorSceneBase)joglpane.getScene()).
                                       getSelectedItems();
         
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
       
       else  // Default: Box
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
     	 // This toggles the two guides on and off
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
 	  	// Popup with an entry box to store the integer
  	    String s = (String)JOptionPane.showInputDialog(
              "How many milliseconds between frame steps?",
              "100");
  	    
  	    // Try to get the int time back and set the step size.
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
     	// Turn ArcBall on/off
         ControlCheckbox target = (ControlCheckbox) e.getSource();
         
         arc_ball.setEnabled(target.isSelected());
     }
  }
  
  /*
   * Updates AltAzController when ArcBall is finished moving
   * 
   * UNFINISHED
   */
  private class CameraChangeHandler extends MouseAdapter
  {
    public void mouseReleased( MouseEvent e )
    {
      if(joglpane != null && cam_controller != null && arc_ball != null)
      {
      	if(toggle_arcball != null && toggle_arcball.isSelected())
      	{
 //         Camera view = joglpane.getCamera();
        	
          /* AltAzController Should be Updated Here      */
          /* Once it has been modified to handle arcball */
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
      	// If view changed, set the scenes camera position
      	if(e.getActionCommand().equals(SceneController.VIEW_CHANGED))
      	{
      	  Camera view = joglpane.getCamera();
      	
          view.setCOP(cam_controller.getCOP());
          joglpane.Draw();
      	}
      	
      	// Orthographic Checkbox Changed, so switch the camera model
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
       	// Slider value will set the log scale value.
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
       ColorAndDraw(); // New frame means color changes.
     }
   }
   
  /*
   * This listens for messages sent out by the ViewMenuItems of
   * this view component.
   */ 
   private class HelpListener implements ActionListener
   {
     public void actionPerformed( ActionEvent ae )
     {
       help();
     }
   }
}
