/**
 * File: IOneVarParameterizedFunction.java
 *
 * Copyright (C) 2002, Dennis Mikkelson
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
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.4  2003/06/19 22:22:16  dennis
 *  Added methods that evaluate the derivative with respect to
 *  one parameter at a list of x values.
 *
 *  Revision 1.3  2002/11/27 23:14:24  pfpeterson
 *  standardized header
 *
 *  Revision 1.2  2002/06/17 22:19:45  dennis
 *  Added methods for derivatives and made the parameters 'double'.
 *
 *  Revision 1.1  2002/04/11 20:56:47  dennis
 *  Interface for functions of one variable that are controlled by
 *  an array of parameters and that return float or double
 *  values when evaluated at single points, or at arrays of points.
 *
 *  Revision 1.1  2002/04/04 19:44:23  dennis
 *  Interface for functions of one variable that are controlled
 *  by a list of parameters.
 *
 */

package DataSetTools.functions;

import DataSetTools.math.*;
import DataSetTools.util.*;

/**
 *  This interface is the interface for functions of one variable that
 *  also depend on an array of parameters.  The parameters are named. 
 */

public interface IOneVarParameterizedFunction extends IOneVarFunction
{
  public int      numParameters(); 

  public String[] getParameterNames();

  public double[] getParameters();
  public void     setParameters( double parameters[] );

  public float    get_dFdai( float  x, int i );  // derivative with respect to
  public double   get_dFdai( double x, int i );  // ith parameter at one x

  public float[]  get_dFdai( float  x[], int i );// derivatives with respect to
  public double[] get_dFdai( double x[], int i );// ith parameter at list of x 

  public float[]  get_dFda( float  x );          // derivatives with respect to
  public double[] get_dFda( double x );          // all parameters at one x
}
