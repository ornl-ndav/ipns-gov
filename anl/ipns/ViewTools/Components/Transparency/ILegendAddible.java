/*
 * File: ILegendAddible.java
 *
 * Copyright (C) 2003, Brent Serum
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
 * Primary   Brent Serum <serumb@uwstout.edu>
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
 *
 *  $Log$
 *  Revision 1.1  2004/06/10 23:20:10  serumb
 *  Initial Version of an interface that a class needs to implement
 *  to use the legend overlay.
 *
 */
 
package gov.anl.ipns.ViewTools.Components.Transparency;

import java.awt.Font;
import java.awt.Graphics2D;

import gov.anl.ipns.ViewTools.Components.*;
import gov.anl.ipns.ViewTools.Panels.Graph.*;

/**
 * This interface is implemented by view components that utilize the 
 * LegendOverlay.
 *
 *  @see gov.anl.ipns.ViewTools.Components.Transparency.AxisOverlay2D
 */
public interface ILegendAddible extends IOverlayAddible
{

 /**
  * This method will return the graphic associated with 
  * a graph for the Legend.
  *
  *  @return The graphic for the Legend.
  */
  public GraphData getGraphData(int graph);

 /**
  * This method will get the text that is associated with 
  * a graph for the Legend.
  *
  *  @return The text for the Legend.
  */
  public String getText(int graph);
 
 /**
  * This method will get the selected graphs 
  * for the Legend.
  *
  *  @return The number of graphs for the Legend.
  */
  public int[]  getSelectedGraphs();

 /**
  * This method will return the font used on by the overlays. The legend overlay
  * will call this to determine what font to use.
  *
  *  @return The display font.
  */
  public Font getFont();
}




   
