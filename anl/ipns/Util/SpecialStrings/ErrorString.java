/*
 * @(#)ErrorString.java  0.1  1999/07/27  Dennis Mikkelson
 *
 *  $Log$
 *  Revision 1.3  2000/07/10 22:55:37  dennis
 *  July 10, 2000 version... many changes
 *
 *
 *  Revision 1.4  2000/06/08 19:07:37  dennis
 *  Fixed DOS text problem
 *
 *  Revision 1.3  2000/05/11 16:18:22  dennis
 *  Added RCS logging
 *
 *
 */
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
