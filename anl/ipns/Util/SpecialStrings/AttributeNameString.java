/*
 * File: AttrbiuteNameString.java
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
 *  Revision 1.5  2001/04/25 22:23:56  dennis
 *  Added copyright and GPL info at the start of the file.
 *
 *  Revision 1.4  2000/07/31 15:40:53  dennis
 *  Now includes default constructor
 *
 *  Revision 1.3  2000/07/10 22:55:37  dennis
 *  Now Using CVS 
 *
 *  Revision 1.3  2000/06/08 19:07:37  dennis
 *  Fixed DOS text problem
 *
 *  Revision 1.2  2000/05/11 16:18:22  dennis
 *  Added RCS logging
 *
 */
package DataSetTools.util;

import java.io.*;

/**
 * The AttributeNameString class is used to construct parameters for 
 * DataSetOperators that should accept the name of an Attribute.
 */
public class AttributeNameString  extends     SpecialString
                                  implements  Serializable
{
   public AttributeNameString()
   {
     super( "" );
   }

   public AttributeNameString( String message )
   {
     super( message );
   }
}
