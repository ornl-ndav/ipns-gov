/*
 * @(#) GraphData.java
 *
 * Programmer: Dennis Mikkelson
 *
 * $Log$
 * Revision 1.3  2001/01/29 21:39:15  dennis
 * Now uses CVS version numbers.
 *
 * Revision 1.2  2000/07/10 22:16:59  dennis
 * minor format change to documentation
 *
 *  Revision 1.1  2000/07/10 22:06:20  dennis
 *  Object containing description of one graph for a GraphJPanel
 * 
 *  Revision 1.2  2000/05/11 16:53:19  dennis
 *  Added RCS logging
 * 
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

