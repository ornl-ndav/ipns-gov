/*
 *  @(#)SpecialString.java  1999/07/15   Dennis Mikkelson
 *
 *  $Log$
 *  Revision 1.4  2000/07/31 15:39:27  dennis
 *  Added setString() string method to allow setting a new string value into
 *  the SpecialString object.
 *
 *  Revision 1.3  2000/07/10 22:55:37  dennis
 *  July 10, 2000 version... many changes
 *
 *
 *  Revision 1.3  2000/06/08 19:07:37  dennis
 *  Fixed DOS text problem
 *
 *  Revision 1.2  2000/05/11 16:18:22  dennis
 *  Added RCS logging
 *
 *
 */

package DataSetTools.util;

import java.io.*;

/**
 * The SpecialString class is an abstract base class for special purpose
 * strings whose class is used to distinguish their functionality.  For 
 * example, ErrorString and AttributeString are derived from this SpecialString
 * class and used for passing special types of parameters and return values
 * to/from DataSetOperators.
 */
public abstract class SpecialString  implements  Serializable 
{
  String message;

    /**
     * Constructs a special string with the specified message.
     *
     * @param   message   The string that serves as the value of this 
     *                    SpecialString object.
     */
    public SpecialString( String message )
    {
	this.message = message;
    }


    /**
     * Set the string value of a special string to the specified message.
     *
     * @param   message   The string that to be set as the value of this
     *                    SpecialString object.
     */
    public void setString( String message )
    {
       this.message = message;
    }

    /**
     * Get the String value of this SpecialString object
     *
     * @return The string value for this object
     *
     */
    public String toString()
    {
      return message;
    }
}
