/*
 * File:  Expression.java
 *
 * Copyright (C) 2002, Dennis Mikkelson
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307, USA.
 *
 * Contact : Dennis Mikkelson <mikkelsond@uwstout.edu>
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI. 54751
 *           USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.2  2002/06/17 22:17:35  dennis
 *  Now uses doubles for calculation.
 *
 *  Revision 1.1  2002/04/17 21:44:40  dennis
 *  Function described by an expression in terms of an independent
 *  variable, and set of named parameters.
 *
 *
 */

package  DataSetTools.functions;

import java.io.*;
import DataSetTools.functions.FunctionTools.*;
import DataSetTools.math.*;
import DataSetTools.util.*;
import DataSetTools.dataset.*;
import DataSetTools.viewer.*;

/**
 * This class implements a parameterized function that is given by an 
 * expression in a string.
 */

public class Expression extends OneVarParameterizedFunction
                                implements Serializable
{
  Fxn    f      = null; 
  double vals[] = null; 

  /**
   *  Construct a new function object from the specified expression, using the
   *  specified variable and parameters. 
   *
   *  @param  expression        The string containing the expression 
   *  @param  variable_name     The name of the independent variable for this
   *                            function.
   *  @param  parameter_names   The array of parameter names for this function 
   *  @param  parameter_vals    The array of parameter values for this function 
   */
   public Expression( String expression, 
                      String variable_name, 
                      String parameter_names[], 
                      double parameter_vals[]  )
   {
     super( "Expression", 
             new double[ parameter_names.length ], 
             new String[ parameter_names.length ] );

     for ( int i = 0; i < parameter_names.length; i++ )
        this.parameter_names[i] = parameter_names[i]; 

     setParameters( parameter_vals );

                              // first replace all of the given parameter names 
                              // with a special token, PREFIX + an index 
     String PREFIX = "#$@";
     expression = StringUtil.replace_token( expression, 
                                            variable_name, 
                                            PREFIX+0 );
     for ( int i = 0; i < parameter_names.length; i++ )
       expression = StringUtil.replace_token( expression, 
                                              parameter_names[i], 
                                              PREFIX+(i+1) );

                             // then replace all of the special tokens with the
                             // x0, x1, etc required by the parsing utility.
     expression = StringUtil.replace_token( expression, PREFIX+0, "x0" );
     for ( int i = 0; i < parameter_names.length; i++ )
       expression = StringUtil.replace_token( expression, 
                                              PREFIX+(i+1), 
                                              "x"+(i+1) );

                                                       // now parse the string
     String2Instance1 S = new String2Instance1( expression, "rule" );
     f = S.parse();
     if ( f == null )
       System.out.println("Error parsing expression in " +  
                          "DataSetTools.functions.Expression" );  

                                                   // copy the paramter values
                                                   // padding with 0's 
     vals = new double[ parameter_names.length + 1 ];
     int n_params = Math.min( parameter_names.length, parameter_vals.length );
     vals[0] = 0;
     for ( int i = 0; i < n_params; i++ )
       vals[ i+1 ] =  parameter_vals[i];
     for ( int i = n_params+1; i < vals.length; i++ )
       vals[i] = 0; 
   }

  /**
   *  Evaluate the expression at the specified (double) x-value.
   *
   *  @param  x  the point at which the expression is to be evaluated
   *
   *  @return the value of the expression, at the specified point, 
   *          provided the point is in the currently specified domain.
   */
  public double getValue( double x )
  {
    if ( domain.contains( (float)x ) && f != null )
    {
      vals[0] = x;                                      // first val is x
                                                        // remaining vals are
                                                        // the parameters
      for ( int i = 0; i < parameters.length; i++ )
        vals[i+1] = parameters[i];

      return f.vall( vals, vals.length );
    }
    else
      return 0; 
  }


  /**
   *
   */
  public boolean isValid()
  {
    if ( vals != null && f != null )
      return true;
    else
      return false;
  }     

 /* -------------------------------------------------------------------------
  *
  * MAIN  ( Basic main program for testing purposes only. )
  *
  */
    public static void main(String[] args)
    {
      System.out.println("With quadratic");

      String expression = "a * t * t + b * t + c"; 
      String var_name = "t";
      String param_names[] = { "a", "b", "c" }; 
      double param_vals[]  = { 1, -2, 1 };
      Expression f = new Expression( expression, 
                                     var_name, 
                                     param_names, 
                                     param_vals );

      double x  = 0.5;
      float  xf = 0.5f;
      float ans = (float)f.getValue((double)xf);
      System.out.println("The double value at " +x+ " is " +f.getValue(x) ); 
      System.out.println("The float value at " +x + " is " + ans );

      double[] xs = { -2, -1.5, -1, -0.5,  0, 0.5,  1, 1.5, 2 }; 
      double[] ys = f.getValues( xs );
      System.out.println("Double Values are:");
      for ( int i = 0; i < xs.length; i++ )
        System.out.println("xs[i] = " + xs[i] + ", " + "ys[i] = " + ys[i] );

      float[] xfs = { -2, -1.5f, -1, -0.5f,  0, 0.5f,  1, 1.5f, 2 }; 
      float[] yfs = f.getValues( xfs );
      System.out.println("Float Values are:");
      for ( int i = 0; i < xfs.length; i++ )
        System.out.println("xfs[i] = " + xfs[i] + ", " + "yfs[i] = " + yfs[i] );

      DataSet ds = new DataSet( expression, "Initial Version" );
      XScale x_scale = new UniformXScale( -5, 5, 500 );
      Data data = new FunctionModel( x_scale, f, 1 );
      ds.addData_entry( data );
      ViewManager vm = new ViewManager( ds, IViewManager.IMAGE );
    }
}
