/**
 *  @(#)FontUtil.java   0.1 2000/03/02  Dennis Mikkelson
 *
 * ---------------------------------------------------------------------------
 *  $Log$
 *  Revision 1.1  2000/07/10 22:52:01  dennis
 *  Standard fonts for labels and borders, etc.
 *
 *  Revision 1.2  2000/05/11 16:18:22  dennis
 *  Added RCS logging
 *
 *
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

  /**
   * Don't instantiate this class, just use the Fonts provided.
   */
  private FontUtil() {}

}
