/* 
 * file: IMarkerAddible.java
 *
 * Copyright (C) 2004, Mike Miller
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
 * Primary   Mike Miller <millermi@uwstout.edu>
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
 *  Revision 1.1  2004/08/13 02:48:06  millermi
 *  - Initial Version - Defines three methods that should exist in
 *    any class that uses the MarkerOverlay.
 *
 */
 package gov.anl.ipns.ViewTools.Components.Transparency;

/**
 * This interface is used to outline minimal methods needed for using the
 * MarkerOverlay. Implementing this interface will allow other programmers to
 * check if markers can be used for a certain class. Because this interface
 * defines methods needed for an overlay, it extends IOverlayAddible.
 *
 *  @see gov.anl.ipns.ViewTools.Components.Transparency.MarkerOverlay
 *  @see gov.anl.ipns.ViewTools.Components.Transparency.Marker
 *  @see gov.anl.ipns.ViewTools.Components.Transparency.IOverlayAddible
 */ 
public interface IMarkerAddible extends IOverlayAddible
{
 /**
  * This method should be implemented to add markers to the MarkerOverlay.
  *
  *  @param  new_marker The marker to be added.
  */
  public void addMarker( Marker new_marker );
  
 /**
  * This method should be implemented to remove markers from the MarkerOverlay.
  *
  *  @param  old_marker The marker to be removed.
  */
  public void removeMarker( Marker old_marker );
  
 /**
  * This method should be implemented to remove all markers from the
  * MarkerOverlay.
  */
  public void removeAllMarkers();
}
