/**
 * File: IOneVarFunction.java
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
 *  Revision 1.5  2003/07/16 22:29:49  dennis
 *  Changed DELTA for evaluating numerical derivatives from
 *  1e-5 to 1e-8, since now using double precision for
 *  all calculations in SCD calibration program.
 *
 *  Revision 1.4  2003/06/17 23:04:07  dennis
 *  Changed step factor for approximating numerical derivatives
 *  to 1.0E-5
 *
 *  Revision 1.3  2002/11/27 23:14:24  pfpeterson
 *  standardized header
 *
 *  Revision 1.2  2002/06/17 22:18:47  dennis
 *  Added methods for derivatives.
 *
 *  Revision 1.1  2002/04/11 20:55:00  dennis
 *  Interface for functions of one variable that return float or double
 *  values when evaluated at single points, or at arrays of points.
 *
 *  Revision 1.1  2002/04/04 19:42:41  dennis
 *  Interface for functions of one variable.
 *
 */

package DataSetTools.functions;

import DataSetTools.math.*;
import DataSetTools.util.*;

/**
 *  This interface is the interface for Named functions of one variable that
 *  can produce float or double values at one point or an array of points.
 *  These functions are assumed to be zero outside of a closed interval
 *  that is their domain.
 */
public interface IOneVarFunction extends IOneVariableFunction
{
  public static final double DELTA = 1.0E-8;  // step size used for evaluating
                                               // numerical derivatives
  public float    getValue( float x );
  public double   getValue( double x );

  public float[]  getValues( float x[] );
  public double[] getValues( double x[] );

  public float    get_dFdx( float  x );
  public double   get_dFdx( double x );

  public String   getName(); 
  public void     setName( String name ); 

  public ClosedInterval getDomain(); 
  public void setDomain( ClosedInterval interval ); 
}
