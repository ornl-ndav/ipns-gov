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
 * This work was supported by the University of Tennessee Knoxville and 
 * the Spallation Neutron Source at Oak Ridge National Laboratory under: 
 *   Support of HFIR/SNS Analysis Software Development 
 *   UT-Battelle contract #:   4000036212
 *   Date:   Oct. 1, 2004 - Sept. 30, 2006
 *
 *  Modified:
 *
 *  $Log$
 *  Revision 1.8  2006/07/25 02:14:25  dennis
 *  Added constructor with "is_heavy" parameter to select whether
 *  to use a heavyweight GLCanvas or lightweight GLJpanel.
 *
 *  Revision 1.7  2006/07/21 14:12:29  dennis
 *  Now explicitly disables lighting, to work with the updated JoglPanel.
 *  Cleaned up some formatting problems caused by tabs.
 *
 *  Revision 1.6  2005/08/04 22:36:47  cjones
 *  Updated documentation and comment header.
 *
 *  Revision 1.5  2005/07/29 20:52:50  cjones
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
 *  Revision 1.3  2005/07/25 21:27:55  cjones
 *  Added support for MouseArcBall and a control checkbox to toggle it. Also,
 *  the value of the selected pixel is now displayed with the Pixel Info, and
 *  updates on frame changes.
 *
 *  Revision 1.2  2005/07/22 19:45:13  cjones
 *  Separated 3D components into one base object and two functional objects,
 *  one for data with frames and one for data without frames. Also, added 
 *  features and tweaked functionality.
 *
 *  Revision 1.1  2005/07/19 15:56:38  cjones
 *  Added components for Display3D.
 * 
 */
 
package gov.anl.ipns.ViewTools.Components.ThreeD;

import SSG_Tools.Viewers.*;

import gov.anl.ipns.ViewTools.Components.ThreeD.DetectorScene;

import gov.anl.ipns.ViewTools.Components.*;
import gov.anl.ipns.ViewTools.Components.ViewControls.*;

/**
 * This object displays data in a three dimensional viewer. Data is given as an 
 * array of IPhysicalArray3D objects. Each data point has one associated value,
 * which is used to color the point using a log scaled color model. The 
 * positions,
 * extents, and orientations give each point its place and shape in space. The
 * user may select three different shapes: a box, a rectangle, or a dot.  The
 * box gives the most accurate representation of the data, but it may exhibit
 * low frame rendering with large datasets. The other two shapes can increase
 * performance but are less accurate to the true representation of the 
 * detectors.
 * 
 * Several controls allow the user to adjust the camera, change the pixel 
 * colors,
 * change the background color, see output from mouse clicks, and a list of
 * selected pixels.
 * 
 * On the selected pixel list, the proper input is to first put the 
 * detector ids that were assigned to the particular data array and 
 * then the pixel indices within that array.  To include multiple ids
 * on a line use a comma (',') to seperate individual nonconsecutive ids and
 * use a colon (':') to specifiy a range of ids.
 * Examples: 4 6
 *           4 5,8
 *           4 5,7,9:11
 * 
 * This component uses a JOGL (Java OpenGL) based panel to render the scene.
 */
public class SceneViewComponent extends ViewComponent3D
{  
  // These are needed for the LogScaleColorModel
  private float   min_value, 
                  max_value;

  private boolean is_heavy;
  
  /**
   * Constructor.  Takes array of physical location information and 
   * builds the panel, controls, menus, and scene.  This constructor will 
   * create heavyweight GLCanvas for the display.
   *  
   *   @param arrays The data that will be put into the scene.
   */
  public SceneViewComponent( IPhysicalArray3D[] arrays )
  {
    this( arrays, true );
  }


  /**
   * Constructor.  Takes array of physical location information and 
   * builds the panel, controls, menus, and scene.  This constructor will
   * create either a heavyweight GLCanvas or a lightweight GLJpanel depending
   * on the value passed in for the is_heavy parameter.
   *  
   * @param  arrays    The data that will be put into the scene.
   * @param  is_heavy  Flag indicating whether a heavyweight GLCanvas
   *                   or lightweight GLJpanel should be used.
   */
  public SceneViewComponent( IPhysicalArray3D[] arrays, boolean is_heavy )
  {
    super(arrays);

    this.is_heavy = is_heavy;

    dataChanged(arrays);
    buildControls();
    buildMenu();
  }

  
 /**
  * This method will be called whenever the color of the scene changes.
  * This can occur when the user moves the intensity slider, changes 
  * color model, or changes the data.  The scene will have the new
  * color applied, then it will be redrawn.
  */ 
  public void ColorAndDraw()
  {
    if(varrays != null && joglpane != null)
    {
      ((DetectorScene)joglpane.getScene()).applyColor(colormodel);
      joglpane.Draw();
    }
  }
  
 /**
  * This method is called whenever the view component needs to update 
  * the detector pixels' shape.  The shape should be set to one of the 
  * static int variables describing the shapes.
  */
  public void changeShape()
  {
    // The currentShapeType is set by the menu handler in ViewComponent3D
    ((DetectorScene)joglpane.getScene()).changeShape(currentShapeType);
  }
   
 /**
  * This method is invoked to notify the view component when the data
  * has changed within the same array. The scene will be colored and
  * redrawn to reflect these changes.  This only affects changes
  * to the data values and no the physical information.
  */
  public void dataChanged()
  {
    ColorAndDraw();
  }
  
 /**
  * This method is invoked to notify the view component when a new set of
  * data needs to be displayed.   If the data is not null, it creates a new
  * scene and panel based on the given data.
  *
  *  @param  arrays Virtual arrays of data. Each array should an implementation
  *                 of IPhysicalArray3D.
  */ 
  public void dataChanged(IPointList3D[] arrays)
  { 
    // Clean up jogl pane if needed.
    if(joglpane != null)
    {
      joglpane.setScene(null);
      joglpane.setCamera(null);
      joglpane = null;
    }
   
    // Make sure data is valid. 
    if( arrays == null )
    {
      System.err.println("Null Data. Unable to create 3D View.");
      return;
    }
   
    // Local reference to data.
    varrays = (IPhysicalArray3D[])arrays;
   
    // Set min_value and max_value
    findDataRange();
    colormodel.setDataRange(min_value, max_value);
    
    // Create scene and place in rendering panel
    DetectorScene scene = new DetectorScene( (IPhysicalArray3D[])varrays, 
                                              currentShapeType );
    
    joglpane = new JoglPanel( scene, is_heavy );
    joglpane.enableLighting( false );
 
    joglpane.setCamera( scene.makeCamera() );
    
    // This listener handles selections and picks
    joglpane.getDisplayComponent().addMouseListener( 
            new PickHandler( joglpane ));

    ColorAndDraw();
  }
  
 /*
  * Builds the view controls for this component.
  * Controls are:
  * 
  * controls[0]: ControlSlider - For intensity of
  *              color model.
  * controls[1]: ControlColorScale - Image bar
  *              representing current color scale.
  * controls[2]: ColorControl - Change background color.
  * controls[3]: AltAzController - Controls camera
  * controls[4]: Turns arcball on/off
  * controls[5]: CursorOutputControl - The 3D
  *              coordinates of mouse click.
  * controls[6]: CursorOutputControl - The IDs
  *              for detector and pixel selected.
  * controls[7]: ControlList - Shows all selected
  *              pixels with options to add/remove.
  */
  private void buildControls()
  {
    controls = new ViewControl[8]; 
      
    // Control that adjusts the color intensity
    controls[0] = createIntensityControl();
      
    // Control that displays uncalibrated color scale
    controls[1] = createColorScaleControl();
      
    // Background color
    controls[2] = createBackgroundControl();
      
    // Control that handles camera position
    controls[3] = createCamControl();
      
    // Control that handles turning on/off MouseArcBall
    controls[4] = createArcBallControl();
      
    // Picked point
    controls[5] = createPointOutputControl();
      
    // Picked pixel and detector
    controls[6] = createIDOutputControl();
      
    // All selected pixel and detector
    controls[7] = createSelectedListControl();
  }
  
  /*
   * This builds the standard 3D menu, but turns
   * off the option of setting the frame step size
   * since the frame controller is not used.
   */
  protected void buildMenu()
  {
    super.buildMenu();
  	
    menus[3].getItem().setVisible(false);
  }
   
 /*
  * Finds the range of data values for current arrays. 
  * Sets max_value and min_value. These values
  * are to be used with the LogScaleColorModel
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
          value = ((IPhysicalArray3D)varrays[arr]).getValue(i);
        
          if(value > max) max = value;
          if(value < min) min = value;
        }
    }
    
    max_value = max;
    min_value = min;
  }
}

