/*
 * File:  XminuxCtoNtimesF.java 
 *
 * Copyright (C) 1999, Dennis Mikkelson
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
 *  Revision 1.4  2002/11/27 23:15:47  pfpeterson
 *  standardized header
 *
 */

package DataSetTools.math;

import java.io.*;

/**
 *  Function to calculate  (x-c)**N
 */
class XminusCtoNtimesF implements IOneVariableFunction,
                                  Serializable
{
  private double               c;
  private int                  n;
  private IOneVariableFunction f = null;
 
  public XminusCtoNtimesF( double               c, 
                           int                  n,
                           IOneVariableFunction f )
  {
    this.c = c;
    this.n = n;
    this.f = f;
  }

  public double getValue( double x )
  {
    return f.getValue(x) * Math.pow(x-c, n);
  } 

}
