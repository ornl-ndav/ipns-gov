/*
 * @(#) IOneVariableFunction.java   1.0  2000/03/07  Dennis Mikkelson
 *
 * ---------------------------------------------------------------------------
 *  $Log$
 *  Revision 1.2  2000/07/28 14:00:23  dennis
 *  Format changes only
 *
 *  Revision 1.1  2000/07/10 22:26:13  dennis
 *  July 10, 2000 version... many changes
 *
 *  Revision 1.2  2000/05/11 16:08:13  dennis
 *  Added RCS logging
 *
 *
 */
package  DataSetTools.math;

import java.io.*;

/**
 * IOneVariableFunction specifies the interface that an object must have
 * in order to apply basic mathematical operations such as numerical 
 * integration to it. 
 *  
 * @version 1.0  
 */

public interface IOneVariableFunction 
{
  public double getValue( double x );
}
