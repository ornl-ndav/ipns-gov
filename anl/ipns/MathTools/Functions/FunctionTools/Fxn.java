/*
 * File:  Fxn.java 
 *             
 * Copyright (C) 2000-2002, Ruth Mikkelson
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
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * $Log$
 * Revision 1.4  2004/01/22 02:28:09  bouzekc
 * Removed/commented out unused imports and variables.
 *
 * Revision 1.3  2003/10/20 16:32:01  rmikk
 * Fixed javadoc error
 *
 * Revision 1.2  2002/11/27 23:14:36  pfpeterson
 * standardized header
 *
 * Revision 1.1  2002/04/17 21:39:59  dennis
 * Classes for parsing mathematical expressions and generating
 * byte code for evaluating the functions.
 *
 */

package DataSetTools.functions.FunctionTools;

/**
*Super class for all the new classes.
*<P>This class has the abstract field vall.  This allows the user of these
* newly created classes to reference this method without the compiler complaining.
*<P>NOTE: This file can be augmented and recompiled to add features like serialization and
*  saving the Bytecode.
*<P>Future work may allow additional function definitions at this level to be used by the new subclass.
*/
public abstract class Fxn  //make a one time set only
{
/**
*  A copy of the class loader is stored here.
*  <P>The ByteClassLoader class creates the class from a sequence of bytes instead of from a .class file.
*  <P>This structure also stores the sequence of bytes in its bb field.
*/
  public ByteClassLoader C;
 
/**
* Constructor for Fxn
*/ 
 public Fxn()
  {C=null;
   }
 /**
*The newly created function in the subclass.
*<P>The parameter ndat is not used<P>
*@param x -the list of values to use to calculate the value of the expression<P>
*@param ndata -the number of pieces of data.  Not used<P>
*@throws ArrayIndexOutOfBoundsException  For the variable x<P>
*@throws  ArithmeticException  If a division by zero occurs or 0^0 or ..<P>
*/
 public abstract double vall(double x[],int ndata);


}
