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
 *  Revision 1.4  2001/04/25 20:56:30  dennis
 *  Added copyright and GPL info at the start of the file.
 *
 *
 *  Revision 1.2  2000/07/28 14:00:23  dennis
 *  Format changes only
 *
 *  Revision 1.1  2000/07/10 22:26:13  dennis
 *  Now Using CVS 
 *
 *  Revision 1.2  2000/05/11 16:08:13  dennis
 *  Added RCS logging
 */
package  DataSetTools.math;

import java.io.*;

/**
 * IOneVariableFunction specifies the interface that an object must have
 * in order to apply basic mathematical operations such as numerical 
 * integration to it. 
 */

public interface IOneVariableFunction 
{
  public double getValue( double x );
}
