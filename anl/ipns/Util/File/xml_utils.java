/*
 * File:  xml_utils.java.java
 *
 * Copyright (C) 2002,  Ruth Mikkelson
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
 * Modified:
 *
 *  $Log$
 *  Revision 1.10  2004/03/11 23:07:13  dennis
 *  Removed dependency on DataSet Attributes
 *
 *  Revision 1.9  2004/01/22 02:19:05  bouzekc
 *  Removed unused imports and local variables.
 *
 *  Revision 1.8  2003/10/20 16:31:06  rmikk
 *  Fixed javadoc errors
 *
 *  Revision 1.7  2003/06/18 20:35:35  pfpeterson
 *  Changed calls for NxNodeUtils.Showw(Object) to
 *  DataSetTools.util.StringUtil.toString(Object)
 *
 *  Revision 1.6  2003/03/03 16:49:16  pfpeterson
 *  Changed SharedData.status_pane.add(String) to SharedData.addmsg(String)
 *
 *  Revision 1.5  2002/11/27 23:14:07  pfpeterson
 *  standardized header
 *
 *  Revision 1.4  2002/11/12 03:36:24  dennis
 *  Since Attributes are no longer mutable, this can't call setValue()
 *  for Double, Float and Int Attributes (line 421).  This must still be
 *  modified to adapt to immutable Attributes.
 *
 *  Revision 1.3  2002/07/10 19:35:23  rmikk
 *  Change in documentation
 *
 *  Revision 1.2  2002/06/17 22:46:19  rmikk
 *  Prettied  up the file. Made minor changes
 *
 *  Revision 1.1  2002/06/14 21:25:44  rmikk
 *  Common utilities for Implementers of the IXmlIO interface
 *
 */


package  gov.anl.ipns.Util.File;

import   gov.anl.ipns.Util.Sys.*;
import java.io.*;
import java.util.*;


/** Provides utilities for reading through sections of an
*   xml file representing a DataSet sequentially
*/
public class xml_utils
{ public static String delimiters = " =\n\t\r\f\"";
  public static String errormessage;
  public static char lastchar=0;
  public static StringBuffer sb = new StringBuffer( 2000 );

 /** Gets the next tag name.  This may be an end tag<P>
 * This finds the next "<"  reading past everything else<P>
 * Use getEndTag if the "<" has been consumed
 */
 public static String getTag( InputStream is)
  {errormessage=null;
   try
     {findChar(is,"<",true);
      return getEndTag(is );

     }
   catch( Exception s)
     {errormessage = s.getMessage();
       return null;
     }  
  
   }
   
  /** Use this instead of getTag if the leading < has already been read.
  *   This happens with the getValue method
  */
  public static String getEndTag( InputStream is)
   {errormessage=null;
    try
     {sb.setLength( 0 );
      char c=findChar(is,delimiters,false);
      sb.append(c);
      for( c=(char) is.read(); (delimiters+"=<>\\/+-*").indexOf(c)<0;
             c=(char) is.read())
         sb.append(c);
      lastchar=c;
      if(sb.length() <=0) 
        {errormessage ="No Tag";
         return null;
         }
       return sb.toString();

     }
    catch( Exception s)
     {errormessage = s.getMessage();
       return null;
     }  
  
   }

  private static char findChar(InputStream is, String delim, boolean stop)
                                 throws IOException
   {char c;
    if(lastchar == '>')
      c=lastchar;
    else
      c =(char)is.read();
    lastchar=0;
    boolean done = delim.indexOf(c) >= 0;
    if( !stop)
      done = !done;
    while( !done)
     {c=(char)is.read();
      done = delim.indexOf(c) >=0;
      if( !stop)
       done = !done;        
     }
    return c;
   }

  private static String getNextWord( InputStream is) throws IOException
   {
    char c;
    if(lastchar == '>')
      return null;
    else
      c =(char)is.read();
    lastchar=0;
     
    if( delimiters.indexOf(c) >= 0)
     {
      return sb.toString();
      }
    sb.append(c);
    for( c=(char)is.read(); (delimiters+"=></\"").indexOf(c)<0;
                 c=(char)is.read())
     sb.append(c);
    lastchar = c;
    if(sb.length() <=0) 
     {errormessage ="No Tag";
       return null;
     }
       
    return sb.toString();
   }

  /** Returns an error message or null if no error
  */
  public static String getErrorMessage()
   {return errormessage;
   }

 /**
  * Reads the next xml attribute in a tag.
  * @return  A vector with two elements.  The first element is
  *          the xml attributes key and the second its value as a String
  */
  public static Vector getNextAttribute( InputStream is)
   {errormessage = null;
    Vector Res= new Vector();
    try
     {char c;  
      sb.setLength( 0 );
      if( lastchar =='>')
         return Res;
      else
         c= findChar( is, delimiters, false);
      
      if( c == '>')
        {lastchar ='>';
          return Res;
         }
      sb.setLength(0);
      sb.append(c);
      
      getNextWord( is );
      String U= sb.toString();
      sb.setLength( 0);
     
      Res.addElement( U);
      if(lastchar =='=')
        c=lastchar;
      else
        { c=findChar( is, " \n\r\f\t",false);
         }
      if( c!='=')
        {errormessage="Improperly formed attribute. Need ="+U +"::"+c;
         return null;
         }
       c=findChar( is, " \t\r\n\f",false);
       
       if( c!='\"')
         {errormessage="Attribute values must be quoted "+c+U;
           return null;
           }
       sb.setLength( 0);
       for(c=(char)is.read(); c!='\"';c=(char)is.read())
            sb.append(c);
       lastchar = 0;
      
       Res.addElement(sb.toString());
       //lastchar = c;
       return Res;
      
     }
    catch( Exception s)
     {errormessage = s.getMessage();
      return null;
     }  


   }

 /** Reports the message to the status pane then returns false
 */
  public static boolean setError( String Message)
  {
    SharedMessages.addmsg(Message);
    return false;
  }

 /** Skips to the end of the xml attributes associated with a tag<ul>
 * Use this only one time after a getTag, getEndTag, or getAttribute
 * method call </ul>
 **/
  public static boolean skipAttributes( InputStream is)
   {errormessage=null;
    try
     {boolean quote=false;
      boolean backslash=false;
      char c;
      if( lastchar == '>')
         { lastchar = 0;
           return true;
          }
      else
         c=(char) is.read();
      for(  ; (c!='>' ) && !quote; c=(char)is.read())
        {if( c== '\"' && !backslash)
           quote=!quote;
         
           
         if( c=='/') 
           backslash= true;
         else 
           backslash=false;
         }
       lastchar = 0;
      
     }
    catch( Exception s)
     {errormessage = s.getMessage();
       return false;
     }  

    return true;   
   }

 /** Returns the value associated with the tag<P>
 * Must be invoked after the tag and all attributes have been
 * dealt with
 */
 public static String getValue( InputStream is)
  {errormessage=null;
   try
     { sb.setLength( 0);
      for( char c= (char)is.read(); c!='<'; c= (char)is.read())
          sb.append(""+c);
      return sb.toString();
     }
   catch( Exception s)
     {errormessage = s.getMessage();
       return null;
     }  


   }

  /** This will ignore all values in a block<P>
  * Invoke this method AFTER the tag and all its attributes have
  * been dealt with(">" has been read)
  */
 public static String skipBlock( InputStream is)
  {errormessage=null;
   try
     {int nblocks = 0;
      
      errormessage="improper tag name";
      String S = getTag( is);
     
      if( S == null)
          return null;
      if( S.trim().length() < 1) 
          return null;
      if( S.trim().charAt(0) =='/')
          nblocks--;
      else 
         nblocks++;
     
      skipAttributes(is);
      while (nblocks >= 0 )
        {S = getTag( is);
         
         if( S == null)
           return null;
         if( S.trim().length() < 1) 
           return null;
         if( S.trim().charAt(0) =='/')
           nblocks--;
         else 
           nblocks++;
         skipAttributes(is);
         //System.out.println("S,nblocks="+S+","+nblocks);
        }
       
       return S;
     }
   catch( Exception s)
     {errormessage = s.getMessage();
       return null;
     }  

    
   }



    public static byte[] convertToB64( float[] a)
      {ByteArrayOutputStream os= new ByteArrayOutputStream( a.length*4+2);
       DataOutputStream dou= new DataOutputStream( os);
       try{
       for( int i=0;i<a.length;i++)
         dou.writeFloat( a[i]);
       return encode(os.toByteArray());
          }
       catch(Exception s){return null;}
       }

    public static byte[] convertToB64( int[] a)
      {ByteArrayOutputStream os= new ByteArrayOutputStream( a.length*4+2);
        try{
       DataOutputStream dou= new DataOutputStream( os);
       for( int i=0;i<a.length;i++)
         dou.writeInt( a[i]);
       return encode(os.toByteArray());
         }
       catch(Exception s){return null;}
       }
   
    public static float[] convertB64Tofloat( byte[] a1)
      {byte[] a=decode(a1);
        try{
       ByteArrayInputStream is= new ByteArrayInputStream(a);
       DataInputStream di= new DataInputStream( is);
       float[] f = new float[ a.length/4];
       for(int i=0; i<f.length; i++)
          f[i]=di.readFloat();
       return f;
         }
       catch(Exception s){return null;}
       }

     public static int[] convertB64Toint(byte[] a1)
       {byte[] a=decode(a1);
         try{
        ByteArrayInputStream is= new ByteArrayInputStream( a1);
        DataInputStream di= new DataInputStream( is);
       int[] f = new int[ a.length/4];
       for(int i=0; i<f.length; i++)
          f[i]=di.readInt();
       return f;
         }
       catch(Exception s){return null;}
       
       }
                  //base64
      public static byte[] encode(byte[] data)
    {
        int c;
        int len = data.length;
        StringBuffer ret = new StringBuffer(((len / 3) + 1) * 4);
        for (int i = 0; i < len; ++i)
        {
            c = (data[i] >> 2) & 0x3f;
            ret.append(cvt.charAt(c));
            c = (data[i] << 4) & 0x3f;
            if (++i < len)
                c |= (data[i] >> 4) & 0x0f;

            ret.append(cvt.charAt(c));
            if (i < len)
            {
                c = (data[i] << 2) & 0x3f;
                if (++i < len)
                    c |= (data[i] >> 6) & 0x03;

                ret.append(cvt.charAt(c));
            }
            else
            {
                ++i;
                ret.append((char) fillchar);
            }

            if (i < len)
            {
                c = data[i] & 0x3f;
                ret.append(cvt.charAt(c));
            }
            else
            {
                ret.append((char) fillchar);
            }
        }

        return(getBinaryBytes(ret.toString()));
    }

   
                                //base64
    public static byte[] decode(byte[] data)
    {
        int c;
        int c1;
        int len = data.length;
        StringBuffer ret = new StringBuffer((len * 3) / 4);
        for (int i = 0; i < len; ++i)
        {
            c = cvt.indexOf(data[i]);
            ++i;
            c1 = cvt.indexOf(data[i]);
            c = ((c << 2) | ((c1 >> 4) & 0x3));
            ret.append((char) c);
            if (++i < len)
            {
                c = data[i];
                if (fillchar == c)
                    break;

                c = cvt.indexOf((char) c);
                c1 = ((c1 << 4) & 0xf0) | ((c >> 2) & 0xf);
                ret.append((char) c1);
            }

            if (++i < len)
            {
                c1 = data[i];
                if (fillchar == c1)
                    break;

                c1 = cvt.indexOf((char) c1);
                c = ((c << 6) & 0xc0) | c1;
                ret.append((char) c);
            }
        }

        return(getBinaryBytes(ret.toString()));
    }

   private static byte[] getBinaryBytes(String str)
    {
        byte[] b = new byte[str.length()];
        for (int i = 0; i < b.length; ++i)
            b[i] = (byte) str.charAt(i);

        return(b);
    }

    private static final int    fillchar = '=';

                                    // 00000000001111111111222222
                                    // 01234567890123456789012345
    private static final String cvt = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

                                    // 22223333333333444444444455
                                    // 67890123456789012345678901
                                    + "abcdefghijklmnopqrstuvwxyz"

                                    // 555555556666
                                    // 234567890123
                                    + "0123456789+/";

   
 /** Test program for this module
 *
 *   arg[0] is a filename in a simple xml format
 */
public static void main( String args[])
  { System.out.println("Here");
    if( args == null)
      System.exit(0);
    if( args.length < 1)
      System.exit(0);
    File f = new File( args[0]);
     FileInputStream fin =null;
    try
     {
        fin = new FileInputStream( f);
      }
     catch( Exception s)
      {System.out.println("Cannot open file "+ s);
       System.exit(0);
       }

    char c=0;
    while ( c!= 'x' )
      { 
        System.out.println("Enter option desired");
        System.out.println("  G: get Tag");
        System.out.println("   g:getEnd  tag");
        System.out.println("  A: get attribute");
        System.out.println("  a: skip Attributes");
        System.out.println("  v: get Value");
        System.out.println("  s:Skip block");
        System.out.println("  e: get errormessage");
        System.out.println("  x:exit");
        c=0;
        while( (int) c < 32)
         try
          { c=(char) System.in.read();
            System.out.println("c="+(int)c+","+c);
          }
         catch(Exception s)
           {System.exit(0);
            }
         
       
         if( c =='G')
           System.out.println(xml_utils.getTag(fin));
         else if( c=='g')
           System.out.println(xml_utils.getEndTag(fin));
         else if(c=='A')
           System.out.println( StringUtil.toString(
                                             xml_utils.getNextAttribute(fin)));
         else if( c=='a')
            System.out.println(xml_utils.skipAttributes(fin));
         else if( c=='v')
            System.out.println(xml_utils.getValue(fin));
         else if (c=='s')
            System.out.println(xml_utils.skipBlock(fin));
         else if( c== 'e')
             System.out.println( xml_utils.getErrorMessage());
        
       }

      
   }


}
