/*
 * @(#)XminuxCtoNtimesF.java  0.1 2000/03/10  Dennis Mikkelson
 *
 * ---------------------------------------------------------------------------
 *  $Log$
 *  Revision 1.1  2000/07/10 22:26:16  dennis
 *  July 10, 2000 version... many changes
 *
 *  Revision 1.2  2000/05/11 16:08:13  dennis
 *  Added RCS logging
 *
 *
 */

package DataSetTools.math;

import java.io.*;

/**
 *  Function to calculate  (x-c)**N
 */
class XminusCtoNtimesF implements IOneVariableFunction,
                                  Serializable
{
  private double               c;
  private int                  n;
  private IOneVariableFunction f = null;
 
  public XminusCtoNtimesF( double               c, 
                           int                  n,
                           IOneVariableFunction f )
  {
    this.c = c;
    this.n = n;
    this.f = f;
  }

  public double getValue( double x )
  {
    return f.getValue(x) * Math.pow(x-c, n);
  } 

}
