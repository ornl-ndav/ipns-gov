package DataSetTools.util;

import java.io.*;

/**
 * The ErrorString class is used to construct and return strings for error
 * messages in a form that allows them to be recognized as error messages.
 */
public class ErrorString  extends     SpecialString
                          implements  Serializable 
{
   public ErrorString( String message )
   {
     super( message );
   }

}
