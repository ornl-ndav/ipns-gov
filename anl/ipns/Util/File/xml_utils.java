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
 * Contact : Ruth Mikkelson, mikkelsonr@uwstout.edu
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751
 *           USA
 *
 * 
 *
 * For further information, see http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.2  2002/06/17 22:46:19  rmikk
 *  Prettied  up the file. Made minor changes
 *
 *  Revision 1.1  2002/06/14 21:25:44  rmikk
 *  Common utilities for Implementers of the IXmlIO interface
 *
 */


package DataSetTools.dataset;
import java.io.*;
import java.util.*;
import Command.*;



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
   String S="";
   try
     {char c=findChar(is,"<",true);
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
    String S="";
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
    DataSetTools.util.SharedData.status_pane.add(Message);
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
  * Invoke this method AFTER the tag and all its values have
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
 /** only works for ones with simple names and values
 */
 public static boolean AttribXMLwrite( OutputStream stream, int mode, 
                                     Attribute A)
   {sb.setLength( 0 );
    String dt = A.getClass().toString();
    
    dt=dt.substring( dt.lastIndexOf('.')+1);
   
    try
    {String SS = (new NexIO.NxNodeUtils()).Showw( A.getValue());
      if( SS != null)
        if( SS.length() >=1)
          {
           if( SS.charAt(0) == '[')
              SS = SS.substring(1);
           if( SS.charAt( SS.length()-1) == ']')
             SS = SS.substring( 0, SS.length()-1);
          
           }
      sb.append("<");
      sb.append(dt);
      String nm = A.getName();
      if( nm.equals("\u0394"+"2"+"\u03b8"))
         nm="\\u0394"+"2"+"\\u03b8";
      sb.append(">\n<name>");
        sb.append(nm);
        sb.append("</name>\n");
      sb.append("<value>");
         sb.append( SS);
         sb.append("</value>\n</");
      sb.append(dt);
      sb.append(">\n");
      stream.write( sb.toString().getBytes() );
    }
    catch ( IOException e )
    { 
      return xml_utils.setError("IO Exception="+ e.getMessage());
      
    } 
    return true;


    }

public static boolean AttribXMLread( InputStream stream, Attribute A )
   {String dt= A.getClass().toString();
    dt=dt.substring( dt.lastIndexOf('.')+1); 
    boolean done=false;
    String fd="";
    String key=  xml_utils.getTag( stream );
    if( key.trim().equals("/"+dt))
      {xml_utils.skipAttributes( stream );
       done = true;
       if(fd.equals("nv"))
         return true;
       else
          return false;
       }
    while(!done)
     {
      if( !xml_utils.skipAttributes( stream ) )
       {DataSetTools.util.SharedData.status_pane.add(
        xml_utils.getErrorMessage());
        return false;
        }
      String v= xml_utils.getValue( stream);
      if( v== null)
        {DataSetTools.util.SharedData.status_pane.add(
                xml_utils.getErrorMessage());
         return false;
        }
      if( key.equals( "name"))
       {if( fd.indexOf('n')>=0)
          return false;
        if( v.equals("\\u0394"+"2"+"\\u03b8"))
          v="\u0394"+"2"+"\u03b8";
        
        A.setName(v);
        fd="n"+fd;
        }
      else if( key.equals("value"))
       try{if(fd.indexOf('v')>=0)
             return false;
          fd=fd+"v";
          Object value= v;
          if( (A instanceof IntAttribute) ||
              (A instanceof FloatAttribute) ||
              (A instanceof DoubleAttribute))
            value= new Double(v);
          A.setValue( value );
          }
       catch( Exception s)
          { DataSetTools.util.SharedData.status_pane.add(
              "Error"+s.getMessage()); 
             return false;
           
           }
       key=  xml_utils.getTag( stream );
       if( key == null)
         {DataSetTools.util.SharedData.status_pane.add(
                    xml_utils.getErrorMessage());
          return false;
          }
       if( key.trim().equals("/"+dt))
        {xml_utils.skipAttributes( stream );
         done = true;
         if(fd.equals("nv"))
           return true;
         else
          {DataSetTools.util.SharedData.status_pane.add(
              "Did not Set both field ");
           return false;
          }
       }
      }
      return true;
      
       


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
 *@param   arg[0] is a filename in a simple xml format
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
           System.out.println( (new NexIO.NxNodeUtils()).Showw(
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
