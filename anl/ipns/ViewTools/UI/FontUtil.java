/**
 *  File: FontUtil.java
 *
 * Copyright (C) 2000, Dennis Mikkelson
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
 *           Menomonie, WI. 54751
 *           USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.3  2001/04/25 22:24:20  dennis
 *  Added copyright and GPL info at the start of the file.
 *
 *  Revision 1.2  2000/08/03 19:07:25  dennis
 *  Added MONO_FONT for mono spaced fonts in text areas
 *
 *  Revision 1.1  2000/07/10 22:52:01  dennis
 *  Standard fonts for labels and borders, etc.
 *
 *  Revision 1.2  2000/05/11 16:18:22  dennis
 *  Added RCS logging
 */ 

package DataSetTools.util;

import java.awt.*;

/**
 *  Provide common set of fonts to use for borders, labels, etc. to keep the
 *  font's uniform across different parts of an application
 */

public class FontUtil
{
  static public final Font LABEL_FONT  = new Font("SansSerif", Font.PLAIN, 10);
  static public final Font BORDER_FONT = new Font("SansSerif", Font.BOLD,   9);
  static public final Font MONO_FONT   = new Font("Monospaced", Font.PLAIN,12);

  /**
   * Don't instantiate this class, just use the Fonts provided.
   */
  private FontUtil() {}

}
