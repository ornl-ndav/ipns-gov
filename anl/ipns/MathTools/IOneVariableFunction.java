/*
 * File:  IOneVariableFunction.java   
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
 *  Revision 1.8  2004/03/15 23:53:48  dennis
 *  Removed unused imports, after factoring out the View components,
 *  Math and other utils.
 *
 *  Revision 1.7  2004/03/11 23:16:59  dennis
 *  Fixed error in package name
 *
 *  Revision 1.6  2004/03/11 23:11:14  dennis
 *  Changed to MathTools package
 *
 *  Revision 1.5  2002/11/27 23:15:47  pfpeterson
 *  standardized header
 *
 */
package  gov.anl.ipns.MathTools;

/**
 * IOneVariableFunction specifies the interface that an object must have
 * in order to apply basic mathematical operations such as numerical 
 * integration to it. 
 */

public interface IOneVariableFunction 
{
  public double getValue( double x );
}
