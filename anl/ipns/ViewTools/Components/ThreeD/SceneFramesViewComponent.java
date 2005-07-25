/*
 * File: SceneFramesViewComponent.java
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
 *  Revision 1.2  2005/07/25 21:27:55  cjones
 *  Added support for MouseArcBall and a control checkbox to toggle it. Also,
 *  the value of the selected pixel is now displayed with the Pixel Info, and
 *  updates on frame changes.
 *
 *  Revision 1.1  2005/07/22 19:45:13  cjones
 *  Separated 3D components into one base object and two functional objects,
 *  one for data with frames and one for data without frames. Also, added features
 *  and tweaked functionality.
 *
 *
 */
 
package gov.anl.ipns.ViewTools.Components.ThreeD;

import SSG_Tools.Viewers.*;
import javax.swing.JMenu;

import gov.anl.ipns.ViewTools.Components.ThreeD.DetectorSceneFrames;

import gov.anl.ipns.ViewTools.Components.*;
import gov.anl.ipns.ViewTools.Components.ViewControls.*;

import gov.anl.ipns.ViewTools.Components.ViewControls.FrameController;

/**
 * This object displays data in a three dimensional viewer. Data is given as a 
 * IPhysicalArray3DList[] and all data points are drawn to a color based on their 
 * value and current frame. The positions, extents, and orientations give each point 
 * its place and shape in space.
 * 
 * Controls are created to handle camera, data colors, and frames. A printout of
 * selected data appears under these controls.
 * 
 * This component uses a JOGL (Java OpenGL) based panel to render the scene.
 */
public class SceneFramesViewComponent extends ViewComponent3D
{  
  private float min_value, max_value;
  private float[] time_vals;
  
  /**
   * Constructor.  Takes array of physical location information and 
   * builds the panel, controls, menus, and scene.
   *  
   *   @param arrays The data that will be put into the scene
   */
  public SceneFramesViewComponent( IPhysicalArray3DList[] arrays )
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
  	  int frame = 0;
  	  if(controls != null)
  	  	frame = ((FrameController)getControl(FRAME_CONTROL_NAME)).getFrameNumber();
  	  		
  	  ((DetectorSceneFrames)joglpane.getScene()).applyColor(frame, colormodel);
  	  joglpane.Draw();
  	  
  	  if(controls != null)
  	  {
  	  	int detectorid = (int)((CursorOutputControl)getControl(ID_OUTPUT_NAME)).getValue(0);
  	  	int pixelid = (int)((CursorOutputControl)getControl(ID_OUTPUT_NAME)).getValue(1);
  	  	float value = 0;
  	  	
  	  	for(int i = 0; i < varrays.length; i ++)
  	  	  if(varrays[i] != null && ((IPhysicalArray3DList)varrays[i]).getArrayID() == detectorid)
  	  	  {
  	  	    value = ((IPhysicalArray3DList)varrays[i]).getValue(pixelid, frame);
  	  	    break;
  	  	  }
  	  	  
  	  	setPixelValueOutput(value);
  	  	  	
  	  }
  	}
  }
   
 /**
  * This method is invoked to notify the view component when the data
  * has changed within the same array. The scene will be recreated
  * since the pixel volumes may have been altered.
  */
  public void dataChanged()
  {
    dataChanged(varrays);
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
    joglpane = null;
   
    // Make sure data is valid. 
    if( arrays == null )
    {
      System.err.println("Null Data. Unable to create 3D View.");
      return;
    }
   
    varrays = (IPhysicalArray3DList[])arrays;
   
    // Set min_value and max_value
    findDataRange();
    colormodel.setDataRange(min_value, max_value);
    
    for(int i = 0; i < varrays.length; i++)
      if(varrays[i] != null) 
      {
        time_vals = ((IPhysicalArray3DList)varrays[i]).getFrames();
        break;
      }
    
    // Create scene and place in rendering panel
    DetectorSceneFrames scene = new DetectorSceneFrames( (IPhysicalArray3DList[])varrays );
    
    joglpane = new JoglPanel( scene );
    
    joglpane.setCamera( scene.makeCamera() );
    joglpane.enableLighting( true );
    joglpane.enableHeadlight( true );
    
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
  * controls[5]: FrameController - Move through frames.
  * controls[6]: CursorOutputControl - The 3D
  *              coordinates of mouse click.
  * controls[7]: CursorOutputControl - The IDs
  *              for detector and pixel selected.
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
    
    // Control Frames
    controls[5] = createFrameControl(time_vals, 100);
    
    // Picked point
    controls[6] = createPointOutputControl();
    
    // Picked pixel and detector
    controls[7] = createIDOutputControl();
  }
  
 /*
  * Build the menu items for this component.
  * 
  * menus[0]: Options->ColorScaleMenu - Changes
  *           the color scale.
  */
  protected void buildMenu()
  {
  	super.buildMenu();          
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
        for(int pt = 0; pt < varrays[arr].getNumPoints(); pt++)
          for(int frame = 0; frame < ((IPhysicalArray3DList)varrays[arr]).getNumFrames(); frame++)
          {
            value = ((IPhysicalArray3DList)varrays[arr]).getValue(pt, frame);
        
            if(value > max) max = value;
            if(value < min) min = value;
          }
    }
    
    max_value = max;
    min_value = min;
  }
}