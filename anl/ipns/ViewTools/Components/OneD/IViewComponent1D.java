/*
 * File IViewComponent1D.java
 *
 * Copyright (C) 2003 Brent Serum
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
 * 
 *           
 * Contact : Dennis Mikkelson <mikkelsond@uwstout.edu>
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751, USAB
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
 *  Revision 1.8  2006/07/19 17:33:39  dennis
 *  Changed get/setPointedAt() methods to work with floatPoint2D,
 *  rather than java.awt.Point.  This change is needed so that
 *  the 1D view component deals with the PointedAt concept in
 *  the same coordinate system as the other view components.
 *  Also, added dataChanged( IVirtualArray1DList ) to this
 *  interface.  It was present in implementing classes, but
 *  should be in the interface, so that different implementing
 *  classes could be used polymorphically.
 *
 *  Revision 1.7  2004/03/15 23:53:51  dennis
 *  Removed unused imports, after factoring out the View components,
 *  Math and other utils.
 *
 *  Revision 1.6  2004/03/12 02:17:07  rmikk
 *  Fixed package names
 *
 *  Revision 1.5  2004/01/23 19:35:30  serumb
 *  Added the GPL.
 *
 */
 
package gov.anl.ipns.ViewTools.Components.OneD;

import gov.anl.ipns.Util.Numeric.floatPoint2D;
import gov.anl.ipns.ViewTools.Components.IViewComponent;
import gov.anl.ipns.ViewTools.Components.IVirtualArrayList1D;

/**
 * This interface extends IViewComponent to the specific case of classes
 * that display lists of functions.
 * Any class that implements this interface will interpret and display
 * data in a usable form. Examples include images, tables, and graphs.
 */
public interface IViewComponent1D extends IViewComponent
{
  /**
   * This method changes the array of data being displayed and 
   * updates the display accordingly.
   * 
   * @param pin_varr The IVirtualArrayList1D containing the new data. 
   */
  public void dataChanged( IVirtualArrayList1D pin_varray );
	
  /**
   * This method can be called by other classes to specify a position
   * in "world coordinates" as the currently pointed at location.
   * 
   * @param pt  The "world coordinates" of the point being specified as
   *            the pointed at location.
   */ 
   public void setPointedAt( floatPoint2D pt );
  
  /**
   *  Returns the point in "world coordinates" of the point that 
   *  is being pointed at in this viewer.
   */ 
   public floatPoint2D getPointedAt( );
  
}