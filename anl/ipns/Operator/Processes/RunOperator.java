/* 
 * File: RunOperator.java
 *
 * Copyright (C) 2009, Dennis Mikkelson
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
 *           Menomonie, WI 54751, USA
 *
 * This work was supported by the Spallation Neutron Source Division
 * of Oak Ridge National Laboratory, Oak Ridge, TN, USA.
 *
 *  Last Modified:
 * 
 *  $Author$
 *  $Date$            
 *  $Revision$
 */

package gov.anl.ipns.Operator.Processes;

import Command.Script_Class_List_Handler;
import DataSetTools.operator.Operator;
import gov.anl.ipns.Parameters.IParameter;

/**
 *  The main program of this class will take a an operator name and list of 
 *  parameters from the command line and execute the parameter.  The 
 *  parameters must be numeric, boolean or strings, not DataSets or arrays.
 *  Since this runs in a separate process, several of these can run 
 *  simultaneously on a multi-core machine or using slurm.  
 */
public class RunOperator 
{

  /**
   *  Set the specified argument into the specified parameter, if 
   *  possible.
   *
   *  @param param  The IParameter whose value should be set.
   *  @param arg    The String command line argument from which the 
   *                parameter will be set.
   *
   *  @return true if the argument could be converted to the correct
   *          type and set as the parameter value, false otherwise.
   */
  private static boolean setValue( IParameter param, String arg )
  {
    Object value = param.getValue();
    if ( value instanceof Integer )
    {
      try 
      {
        Integer val = new Integer( arg );
        param.setValue( val );
        System.out.println("found INTEGER");
        return true;        
      }
      catch ( Exception ex )
      {
      }
    }

    else if ( value instanceof Float )
    {
      try
      {
        Float val = new Float( arg );
        param.setValue( val );
        System.out.println("found FLOAT");
        return true;
      }
      catch ( Exception ex )
      {
      }
    }

    else if ( value instanceof Double )  // probably not needed yet, but
    {                                    // include double for future use
      try
      {
        Double val = new Double( arg );
        param.setValue( val );
        System.out.println("found DOUBLE");
        return true;
      }
      catch ( Exception ex )
      {
      }
    }

    else if ( value instanceof Boolean )
    {
      try
      {
        Boolean val = new Boolean( arg );
        param.setValue( val );
        System.out.println("found BOOLEAN");
        return true;
      }
      catch ( Exception ex )
      {
      }
    }

    else 
    {
      try
      {
        param.setValue( arg );
        System.out.println("found STRING");
        return true;
      }
      catch ( Exception ex )
      {
      }
    }

    return false;
  }


  /**
   *  Execute the operator specified by the first command line paramater
   *  passing the remaing command line parameters to that operator.
   *
   *  @param args  Array of Strings containing the operator name as the 
   *               first paramter and the operator's paramters in the 
   *               remaining positions.
   *               args[0] - the operator to run 
   *               args[1] - first parameter for the operator
   *               args[2] - second parameter for the operator
   *               args[k] - kth parameter for the operator
   */
  public static void main( String args[] )
  {
    String   op_name = args[0];

    System.out.println( "Running " + op_name );
    for ( int i = 1; i < args.length; i++ )
      System.out.println( "Param # " + (i-1) + " " + args[i] );

    int num_params = args.length - 1; 

    Script_Class_List_Handler sclh = new Script_Class_List_Handler();

    int position = sclh.getOperatorPosition( op_name );
    Operator op  = sclh.getOperator( position );
    boolean  found = false;
 
    while ( !found && op != null && op.getCommand().equals( op_name ) )
    {
      if ( op.getNum_parameters() == num_params )
      {
        found = true;
        for ( int i = 0; i < num_params; i++ )
        {
          IParameter param = op.getParameter(i);
          System.out.println("Processing parameter " + param.getName() );
          System.out.println("Trying to set value from " + args[i+1] );
          if ( !setValue( param, args[i+1] ) )
          {
            System.err.println("Parameter type wrong for " + param.getName());
            System.err.println("Required : " + param.getValue().getClass());
            System.err.println("Got : " + args[i+1] );
            found = false;
          }
        }
        if ( found )
        {
          try
          {
            System.out.println("FOUND OPERATOR " + op_name );
            Object result = op.getResult();
            System.out.println("RESULT = " + result );
            System.exit(0);
          }
          catch ( Exception ex )
          {
            System.out.println("op_name FAILED " );
            ex.printStackTrace();
            System.exit(1);
          }
        }
      }
      else
      {
        position++;
        op = sclh.getOperator( position );
      }
    }

    System.err.println("OPERATOR " + op_name + 
                       " NOT FOUND WITH CORRECT PARAMETERS" );
    System.exit(1);
  }
}

