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
