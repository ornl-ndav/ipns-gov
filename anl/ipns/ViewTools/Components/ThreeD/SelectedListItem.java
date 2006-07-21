/*
 * File: SelectedListItem.java
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
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 *  Modified:
 *
 *  $Log$
 *  Revision 1.2  2006/07/21 14:40:42  dennis
 *  Cleaned up some formatting problems caused by tabs.
 *
 *  Revision 1.1  2005/08/04 22:33:17  cjones
 *  ViewComponent3D now uses SelectedListItem to convert string items into
 *  int arrays and vice versa. SelectedListItem uses IntList to (de)construct
 *  strings, so multiple ids can be placed on a single line.
 *  Also, ViewComponent3D now contains controls help frame until help menu.
 *
 */

package gov.anl.ipns.ViewTools.Components.ThreeD;

import gov.anl.ipns.Util.Numeric.IntList;

/**
 * This class is used to convert one form of selected list items
 * to another form.  A selected list item has two parts: the
 * detector IDs and the pixel IDs.  It assumed that for each
 * detector id, all of the given pixel ids are "selected" for
 * that detector.  Thus, all detectors in the item share the 
 * same selected pixels.
 * 
 * The ids may be create or retrieved as an array of integers or
 * as Strings. The format for the String is that of the object IntList. 
 * IntList is used to convert the String format into an array format
 * and vice versa.
 * 
 * @see gov.anl.ipns.Util.Numeric.IntList
 */
public class SelectedListItem
{
  private int[] detector_array;
  private int[] pixel_array;
  
  /**
   * Constructor.  Take in two strings representing the 
   * detector ids and their selected pixel ids.  The strings
   * should be in a format as described by the object
   * IntList.
   * 
   * @param det_string String containing the formatted collection
   *                   of detector IDs.
   * @param pix_string String containing the formatted collection
   *                   of pixel IDs.
   * 
   * @see gov.anl.ipns.Util.Numeric.IntList
   */
  public SelectedListItem( String det_string, String pix_string )
  {
    detector_array = IntList.ToArray(det_string);
    pixel_array = IntList.ToArray(pix_string);
  }
  
  /**
   * Constructor.  Take in two arrays representing the 
   * detector ids and their selected pixel ids.  The ids in 
   * each array should be sorted in increasing order.
   * 
   * @param det_array Array containing the integer
   *                   of detector IDs.
   * @param pix_array Array containing the integer
   *                   of pixel IDs.
   */
  public SelectedListItem( int[] det_array, int[] pix_array )
  {
    detector_array = det_array;
    pixel_array = pix_array;
  }
    
 /**
  * Returns the pixel IDs in an int array.
  * 
  *  @return Copy of Pixel IDs in an int array.
  */
  public int[] getPixelArray()
  {
    int[] pix_copy = null;

    if(pixel_array.length > 0)
    {
      pix_copy = new int[pixel_array.length];
      System.arraycopy(pixel_array, 0, pix_copy, 0, pixel_array.length);
    }
  	
    return pix_copy;
  }
  
  
 /**
  * Returns the pixel IDs in a String that is 
  * formatted as specified by IntList.
  * 
  *  @return String containing the pixel
  *          ids.
  * 
  *  @see gov.anl.ipns.Util.Numeric.IntList
  */
  public String getPixelString()
  {
    return IntList.ToString(pixel_array);
  }
  
 /**
  * Returns the detector IDs in an int array.
  * 
  *  @return Copy of Detector IDs in an int array.
  */
  public int[] getDetectorArray()
  {
    int[] det_copy = null;

    if(detector_array.length > 0)
    {
      det_copy = new int[detector_array.length];
      System.arraycopy(detector_array, 0, det_copy, 0, detector_array.length);
    }
  	
    return det_copy;
  }
  
 /**
  * Returns the detector IDs in a String that is 
  * formatted as specified by IntList.
  * 
  *  @return String containing the detector
  *          ids.
  *  @see gov.anl.ipns.Util.Numeric.IntList
  */
  public String getDetectorString()
  {
    return IntList.ToString(detector_array);
  }
  
 /**
  * The string repesentation of the SelectedListItem.
  * It formats it as follows:
  *   "Det <det_string> Pix <pix_string>"
  * Where det_string and pix_string contain the
  * detector and pixel IDs, repsectively, in the format
  * specified by IntList.
  * 
  *  @return The string format of the object.
  *  @see gov.anl.ipns.Util.Numeric.IntList
  */
  public String toString()
  {
    return new String( "Det " + getDetectorString() +
                       " Pix " + getPixelString() );
  }
}
