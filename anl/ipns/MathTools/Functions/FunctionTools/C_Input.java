/*
 * File:  C_Input.java 
 *             
 * Copyright (C) 2000-2002, Ruth Mikkelson
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
 * Contact : Ruth Mikkelson <mikkelsonr@uwstout.edu>
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751, USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * $Log$
 * Revision 1.5  2005/01/10 16:16:49  dennis
 * Removed empty statement(s).
 *
 * Revision 1.4  2004/03/12 00:48:19  dennis
 * moved to package MathTools.Functions.FunctionTools
 *
 * Revision 1.3  2004/01/22 02:28:09  bouzekc
 * Removed/commented out unused imports and variables.
 *
 * Revision 1.2  2002/11/27 23:14:36  pfpeterson
 * standardized header
 *
 * Revision 1.1  2002/04/17 21:39:59  dennis
 * Classes for parsing mathematical expressions and generating
 * byte code for evaluating the functions.
 *
 */

package gov.anl.ipns.MathTools.Functions.FunctionTools;

import java.io.*;

/** This a utility class that reads a "word" from an InputStream.  
*  Other routines ( only one currently) converts that string to a piece of 
*  data of the proper type.
*/
public class C_Input
{ String buff;
   /** Initializes an internal buffer that stores the next word
   */
   public C_Input()
     { buff=new String("");
     }

   /** Reads the next "word" from the InputStream and converts it to a double
   *
   *@param str   The input stream
   *@return  the double value of the next "word" in the input stream
   *@throws  IOException  from the InputStream
   *
   * If the next word cannot be converted into a double value, an uncaught
   *  and unthrown NumberFormatException occurs
   */
   public double readDouble(InputStream str) throws IOException
     { getNumStr(str);
      
       return new Double(buff).doubleValue();


     }
  private void getNumStr(InputStream str)throws IOException
   {char c[];
   int mode; //0 start, 1 decimal occured 2 E occurred 3 +/- occured after E
   boolean Done;

   mode=-1;

   buff="";

   Done=false;
    c=new char[1];

   while(!Done)
     {c[0]= (char)str.read();

      if((c[0]>='0')&&(c[0]<='9'))
            { if(mode<0)mode=0;
              else if(mode==3)
                   mode=5;
            }
      else if(c[0]=='.') 
          { if(mode<0)mode=0;
            if(mode>=1)
              {throw new IOException();}
            mode=1;
          }
      else if((c[0]=='E')||(c[0]=='e'))
          { 
            if((mode>2)||(mode<0))
              {throw new IOException();}
           mode=3;
          }
      else if((c[0]=='+')||(c[0]=='-'))
          {//leading signs mode still -1
           if(mode<0){}
           else if(mode==3)
                mode=5; 
           else 
                Done=true;
          }
      else if((c[0]<=' '))
              {if(mode>=0)Done=true;} 

      else
           Done=true;

      if(!Done)
         buff=buff+new String(c); 

      

      } //End of for loop

    

   }// findNumEnd

/*public static void main(String args[])
 {C_Input inp; 
  double D; 
  char choice;
  inp=new C_Input();
  try{
  while(true)
   {System.out.println("res="+ inp.readDouble(System.in));


   }
     }
  catch(IOException ss){}


 }//main
*/


}
