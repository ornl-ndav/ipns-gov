package DataSetTools.math;

import java.io.*;



  public class TestFunction implements IOneVariableFunction,
                                       Serializable
  {
    public double getValue( double x )
    {
      return Math.sin(x);
    }
  }

