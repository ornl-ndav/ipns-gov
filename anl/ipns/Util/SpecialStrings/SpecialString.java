/*
 *  @(#)SpecialString.java  1999/07/15   Dennis Mikkelson
 *
 * ---------------------------------------------------------------------------
 *  $Log$
 *  Revision 1.2  2000/07/10 22:52:01  dennis
 *  Standard fonts for labels and borders, etc.
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

    public String toString()
    {
      return message;
    }
}
