/*
 * File:  ByteClassLoader.java 
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
 * Revision 1.3  2004/01/22 02:28:09  bouzekc
 * Removed/commented out unused imports and variables.
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
* A Classloader that Loads the class from bytes instead of files
*/

public class ByteClassLoader extends ClassLoader
{/**
 * The bytecode for the class.
 */
  public byte bb[]; 
/**
*The number of bytes in the byteCode.
*/
 public int sizee;
/**
*The name of this class.
*/
 public  String Name;
/**
*Constructor.
*@param  b  The ByteCode for the newly created (sub)class of Fxn
*@param size  The number of Bytes in the ByteCode
*@param name  The name of the newly created subclass
*/    
  public ByteClassLoader(byte b[],int size, String name){super();bb=b;sizee=size;Name=name;}

/**
*Finds the new class.
*<P>If another class with the same name has already been loaded or a .class file for the same class is
in the search path, the newly created class may not be the one that is loaded.
*/
  public Class findClass(String name) throws ClassNotFoundException
   {  
     if( !name.equals("DataSetTools.functions.FunctionTools.Fxn"))
       return defineClass(name, bb, 0, bb.length);
     else
      return  super.findClass( name);
    }
}
