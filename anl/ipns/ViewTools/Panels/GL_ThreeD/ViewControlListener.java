/*
 * File:  ViewControlListener.java
 *
 * Copyright (C) 2004, Dennis Mikkelson
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
 * Modified:
 *
 * $Log$
 * Revision 1.2  2004/06/04 14:09:04  dennis
 * Now transfers data regarding whether or not the projection is a
 * perspective projection, from the ViewController to the ThreeD_GL_Panel,
 * when the ViewControl changes.
 *
 * Revision 1.1  2004/05/28 20:51:18  dennis
 * Initial (test) version of classes for displaying and picking
 * 3D objects using OpenGL from Java, built on the "jogl" system.
 *
 *
 */

package gov.anl.ipns.ViewTools.Panels.GL_ThreeD;

import java.awt.event.*;
import gov.anl.ipns.MathTools.Geometry.*;
import gov.anl.ipns.Util.Sys.*;

/**
 *  This is a convenience class to handle viewer position change requests
 *  from ome or more view controllers and route the requestes to 
 *  ThreeD_GL_Panel only if the requests are separated in time by a specified 
 *  minimum time.  This prevents view controllers from sending excessive 
 *  requests to redraw the panel.
 */

public class ViewControlListener implements ActionListener
{
  /**
   *  The default minimum time between update requests.  Events separated by
   *  less than the minimum time will be ignored.
   */
  public static float MIN_TIME = 0.05f; 
  public static float MIN_SET_VALUE = 0.005f;
  public static float MAX_SET_VALUE = 10.0f;

  private ElapsedTime     timer;
  private float           min_time = MIN_TIME;    
  private ThreeD_GL_Panel panel;

  /**
   *  Construct a ViewControlListener for the specified panel.
   */
  public ViewControlListener( ThreeD_GL_Panel my_panel )
  {
    panel = my_panel;
    timer = new ElapsedTime();
  } 

  /**
   *  Set a new minimum time between update requests.  Values outside of the
   *  interval [ MIN_SET_VALUE, MAX_SET_VALUE ] are ignored.
   *
   *  @param new_min_time  The new minimum time between updates
   */
  public void SetMinTime( float new_min_time )
  {
    if ( new_min_time > MIN_SET_VALUE && new_min_time < MAX_SET_VALUE )
      min_time = new_min_time;
  }

  /**
   *  The actionPerformed method gets the newly specified view parameters from
   *  the ViewControl, sets them for the ThreeD_GL_Panel and requests that the
   *  panel be drawn.
   */
  public void actionPerformed( ActionEvent e )
  {
    if ( timer.elapsed() < min_time ) // ignore events less than min_time apart
      return;
    timer.reset();

    if ( ! (e.getSource() instanceof IViewController) )
    {
      System.out.println("ERROR: ViewControllerListener event came from ");
      System.out.println("object that does not implement IViewController.");
      return;
    }
    IViewController controller = (IViewController)e.getSource();
    Vector3D cop = controller.getCOP();
    Vector3D vrp = controller.getVRP();
    Vector3D vuv = controller.getVUV();
    boolean  perspective_onoff = controller.isPerspective();

    panel.setCOP(cop);
    panel.setVRP(vrp);
    panel.setVUV(vuv);
    panel.setPerspective( perspective_onoff );
    panel.Draw();
  }

}
