/*
 * @(#)AttrbiuteNameString.java  0.1  DennisMikkelson
 *
 *  $Log$
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
 * The AttributeNameString class is used to construct parameters for 
 * DataSetOperators that should accept the name of an Attribute.
 */
public class AttributeNameString  extends     SpecialString
                                  implements  Serializable
{
   public AttributeNameString( String message )
   {
     super( message );
   }
}
