/*
 * @(#) GraphData.java  1.0    2000/04/25   Dennis Mikkelson
 *
 * ---------------------------------------------------------------------------   *  $Log$
 * ---------------------------------------------------------------------------   *  Revision 1.1  2000/07/10 22:06:20  dennis
 * ---------------------------------------------------------------------------   *  Object containing description of one graph for a GraphJPanel
 * ---------------------------------------------------------------------------   *
 * ---------------------------------------------------------------------------   *  Revision 1.2  2000/05/11 16:53:19  dennis
 * ---------------------------------------------------------------------------   *  Added RCS logging
 * ---------------------------------------------------------------------------   *
 *
 */

package DataSetTools.components.image;

import java.io.*;
import java.awt.*;
import javax.swing.*;

public class GraphData implements Serializable 
{
  float  x_vals[]  = { 0, 1 };
  float  y_vals[]  = { 0, 1 };
  Color  color     = Color.black;
  int    linetype  = 1;
  float  linewidth = 1;
  int    marktype  = 0;
}

