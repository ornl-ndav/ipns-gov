/*
 * File:  ParamUsesString.java
 *
 * Copyright (C) 2002, Peter F. Peterson
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
 * Contact : Peter F. Peterson <pfpeterson@anl.gov>
 *           Intense Pulsed Neutron Source Division
 *           Argonne National Laboratory
 *           9700 South Cass Avenue, Bldg 360
 *           Argonne, IL 60439-4845, USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.1  2006/07/10 16:25:04  dennis
 *  Change to new Parameter GUIs in gov.anl.ipns.Parameters
 *
 *  Revision 1.2  2004/05/11 18:23:54  bouzekc
 *  Added/updated javadocs and reformatted for consistency.
 *
 *  Revision 1.1  2003/06/06 18:50:13  pfpeterson
 *  Added to CVS.
 *
 */
package gov.anl.ipns.Parameters;

/**
 * This is an interface to be implemented by all parameters that can get and
 * set String values. In principle all IParameterGUIs should implement this,
 * but there are no gaurantees.  NOTE: This is used by IsawLite to avoid 
 * some useless tasks, like creating a DataSet from a String or visa versa.
 */
public interface IParamUsesString {
  //~ Methods ******************************************************************

  /**
   * Set the value from a String which will be turned into the correct type.
   *
   * @param value The new String value.
   */
  void setStringValue( String value );

  /**
   * This creates a string representation of the value held by the parameter.
   *
   * @return The String value.
   */
  String getStringValue(  );
}
