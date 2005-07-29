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
 *  one for data with frames and one for data without frames. Also, added features
 *  and tweaked functionality.
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
 * This object displays data in a three dimensional viewer. Data is given as a 
 * IPhysicalArray3D[] and all data points are drawn to a color based on their 
 * value. The positions, extents, and orientations give each point its place 
 * and shape in space.
 * 
 * Controls are created to handle camera and data colors. A printout of
 * selected data appears under these controls.
 * 
 * This component uses a JOGL (Java OpenGL) based panel to render the scene.
 */
public class SceneViewComponent extends ViewComponent3D
{  
  
  private float min_value, max_value;
  
  /**
   * Constructor.  Takes array of physical location information and 
   * builds the panel, controls, menus, and scene.
   *  
   *   @param arrays The data that will be put into the scene
   */
  public SceneViewComponent( IPhysicalArray3D[] arrays )
  {
  	super(arrays);

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
  * the detector pixels to have the currentShapeType.
  */
  public void changeShape()
  {
  	((DetectorScene)joglpane.getScene()).changeShape(currentShapeType);
  }
   
 /**
  * This method is invoked to notify the view component when the data
  * has changed within the same array. The scene will be recreated
  * since the pixel volumes may have been altered.
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
  *  @param  arrays - virtual arrays of data
  */ 
  public void dataChanged(IPointList3D[] arrays)
  { 
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
   
    varrays = (IPhysicalArray3D[])arrays;
   
    // Set min_value and max_value
    findDataRange();
    colormodel.setDataRange(min_value, max_value);
    
    // Create scene and place in rendering panel
    DetectorScene scene = new DetectorScene( (IPhysicalArray3D[])varrays, 
    		                                  currentShapeType );
    
    joglpane = new JoglPanel( scene );
    
    joglpane.setCamera( scene.makeCamera() );
    
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
  
  protected void buildMenu()
  {
  	super.buildMenu();
  	
    menus[3].getItem().setVisible(false);
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
          value = ((IPhysicalArray3D)varrays[arr]).getValue(i);
        
          if(value > max) max = value;
          if(value < min) min = value;
        }
    }
    
    max_value = max;
    min_value = min;
  }
}