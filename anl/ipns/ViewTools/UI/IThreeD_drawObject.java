/**
 * File: IThreeD_drawObject.java
 *
 * Copyright (C) 2008, Ruth Mikkelson
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
 * Contact : Ruth Mikkelson <mikkelsonr@uwstout.edu>
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751, USA
 *
 * This work was supported by the Spallation Neutron Source Division
 * of Oak Ridge National Laboratory, Oak Ridge, TN, USA.
 *
 *  Last Modified:
 * 
 *  $Author:  $
 */


package gov.anl.ipns.ViewTools.UI;

import gov.anl.ipns.ViewTools.Panels.Graph.*;
import java.util.*;


/**
 * @author Ruth
 * 
 */
public interface IThreeD_drawObject {



   // Size is 1-5
   public static int    NO_FILL                    = 100;

   public static int    HOLLOW_FILL                = NO_FILL + 1;

   public static int    FILLED_FILL                = NO_FILL + 2;

   public static int    STAR_FILL                  = FILLED_FILL
                                                            + GraphJPanel.STAR;

   public static int    CROSS_FILL                 = FILLED_FILL
                                                            + GraphJPanel.CROSS;

   public static int    PLUS_FILL                  = FILLED_FILL
                                                            + GraphJPanel.PLUS;

   public static int    BOX_FILL                   = FILLED_FILL
                                                            + GraphJPanel.BOX;

   public static int    DOT_FILL                   = FILLED_FILL
                                                            + GraphJPanel.DOT;

   public static int    BAR_FILL                   = FILLED_FILL
                                                            + GraphJPanel.BAR;

   // ----------------For initial strings in Error Messages ------------------
   public static String NOT_INITIALIZED_ERR_STRING = "Graphics is not initialized";

   public static String GRAPH_SYS_ERR_STRING       = "Graphics System in an improper State";

   public static String GRAPH_ITEM_ERR_STRING      = "A Graphics Item is in the wrong state";


   /**
    * Draws the objects defined by the pick_ids(if needed) with the given
    * parameters
    * 
    * @param fillType
    *           One of the *_FILL integers above. If it is not one of the above,
    *           NO_FILL will be chosen.
    * @param sizes
    *           a value between 1 and 5, corresponding to the GraphJPanel sizes.
    * @param color
    *           a color to draw these objects. If null, Color.red will be used
    * @param pick_ids
    *           A vector of ids for the objects to be drawn. These can be
    *           strings or integers or any other object the underlying system
    *           understands and/or can convert to something the underlying
    *           system understands. If null, the draw applies to all objects in
    *           the system
    * @return An error String or and empty string or null if there is no error.
    *         The error string is currently dependent on the underlying
    *         implementor of this this method. It may start with One of the
    *         above ERR_STRING Strings
    */
   public String drawObjects( int fillType , float size , java.awt.Color color ,
            Vector pick_ids );


}
