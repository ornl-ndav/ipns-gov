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
