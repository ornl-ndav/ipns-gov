/* 
 * File: TextSeparatorsInfoHandler.java
 *
 * Copyright (C) 2009, Ruth Mikkelson
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
 * This work was supported by the Spallation Neutron Source Division
 * of Oak Ridge National Laboratory, Oak Ridge, TN, USA.
 *
 *  Last Modified:
 * 
 *  $Author$:
 *  $Date$:            
 *  $Rev$:
 */

package gov.anl.ipns.Util.File;

import javax.swing.*;
import DataSetTools.components.ui.Peaks.*;


/**
 * This class gives different separators between words in text files
 * with different text types. In particular, regular text and html text
 * @author Ruth
 *
 */
public class TextSeparators
{
    private static int START=0;
    private static int END=1;
    private static int EOL =2;
    private static int PAR=3;
    private static int TABLE=4;
    private static int ENDTABLE=5;
    private static int NEWROW=6;
    private static int ENDROW=7;
    private static int NEWCOL=8;
    private static int CENTER=9;
    private static int ITALIC=10;
    private static int BOLD=11;
    private static int ENDCENTER=12;
    private static int ENDITALIC=13;
    private static int ENDBOLD=14;
    private static int INDENT=15;
    private static int ENDINDENT=16;
    private static int WRAP_EOLN = 17;
    private static int TABLE_B1 = 18;
    private static int TABLE_B2 = 18;
    
    private String[] Plain={"",""  ,   
                            "\n", "\n\n"   ,
                            "\n" ,"\n"       ,
                            "","\n"     , 
                            "  ",""       ,
                            "", ""       , 
                            "",""       , 
                            "",""       ,
                            "", "\n", "\n","\n"};
    
    private String[]Html ={"<html><body>\n","\n</html></body>",
                            "<BR>","<P>"   ,
                            "\n<table>","</table>"     ,
                            "<tr><td>", "</td></tr>\n"  ,
                            "</td><td>","<C>"           ,
                            "<I>", "<B>"           , 
                            "</C>", "</I>"          ,
                            "</B>","<UL>",
                            "</UL>","","\n<table border=1>",
                            "\n<table border=2>"};
    
    private String[] currentString;
   
    /**
     * Constructor
     * @param type  The type of text. So far only html and plain text are
     *              supported
     */
    public TextSeparators( String type)
    {
       if( type== null || !type.toUpperCase().trim().equals( "HTML" ))
          currentString = Plain;
       else
          currentString = Html;
    }


    /**
     * Returns special string needed to specify this type of text
     * @return  A start this type of text String
     */
    public String start()
    {
       return currentString[START];
    }

    /**
     * Returns special string needed to specify the end og this type of 
     *      text
     * @return  An "end this type" of text String
     */
    public String end()
    {
       return currentString[END];
    }

    /**
     * 
     * @return  A hard end of line string for this type of text
     */
    public String eol()
    {
       return currentString[EOL];
    }


    /**
     * 
     * @return  A new paragraph string for this type of text
     */
    public String par()
    {
       return currentString[PAR];
    }


    /**
     * 
     * @return  start table string for this type of text.
     *     Includes a textual return for html
     */
    public String table()
    {
       return currentString[TABLE];
    }
    
    /**
     * 
     * @return  start table with border=1 string for this type of text.
     *     Includes a textual return for html
     */
    public String tableBorder1()
    {
       return currentString[TABLE_B1];
    }
    
    
    
    /**
     * 
     * @return  start table with border=2 string for this type of text.
     *     Includes a textual return for html
     */
    public String tableBorder2()
    {
       return currentString[TABLE_B2];
    }    
    /**
     * 
     * @return  end of table string for this type of text.
     *     Includes a textual return for html
     */
    public String tableEnd()
    {
       return currentString[ENDTABLE];
    }
    /**
     * 
     * @return  start new row string for this type of text.
     *     
     */
    public String row()
    {
       return currentString[NEWROW];
    }
    /**
     * 
     * @return  an end row string for this type of text.
     *     Includes an endcol textual return for html
     */
    public String rowEnd()
    {
       return currentString[ENDROW];
    }

    /**
     * 
     * @return  a new column string for this type of text.
     *     
     */
    public String col()
    {
       return currentString[NEWCOL];
    }
    /**
     * 
     * @return  start centering string for this type of text.
     *     
     */
    public String center()
    {
       return currentString[CENTER];
    }
    /**
     * 
     * @return  start italics string for this type of text.
     *     
     */
    public String it()
    {
       return currentString[ITALIC];
    }
    /**
     * 
     * @return  start bold string for this type of text.
     *     
     */
    public String bld()
    {
       return currentString[ BOLD ];
    }
    /**
     * 
     * @return  end centering string for this type of text.
     *     
     */
    public String centerEnd()
    {
       return currentString[ENDCENTER];
    }
    
    /**
     * 
     * @return  end italics string for this type of text.
     *     
     */
    public String iEnd()
    {
       return currentString[ENDITALIC];
    }

    /**
     * 
     * @return  end bold string for this type of text.
     *     
     */
    public String bldEnd()
    {
       return currentString[ENDBOLD];
    }
    


    /**
     * 
     * @return  indent string for this type of text.
     *     
     */
    public String indent()
    {
       return currentString[INDENT];
    }

    /**
     * 
     * @return  end indent string for this type of text.
     *     
     */
    public String indentEnd()
    {
       return currentString[ENDINDENT];
    }
    
    /**
     * Creates an end of line break for systems that do not do this
     * automatically
     * @return  The character for wrapping lines in this system
     */
    public String weol()
    {
       return currentString[WRAP_EOLN];
    }
   
   /**
    * @param args
    */
   public static void main( String[] args )
   {
     float[][] orMat= {{0.12192331f , 0.16229957f, -0.11804537f} ,
        { 0.14593728f ,-0.11329055f ,-0.00687966f} ,
     {-0.00633046f , 0.00408152f, -0.23492269f}} ;
     String S = subs.getCoordinateInformation( true );
     //ShowOrientationInfo( null ,orMat ,null ,null , true );
     try
     {
     JFrame jf  = new JFrame("HTML");
     JEditorPane ed= new JEditorPane("text/html",S);
     jf.setSize(400,400);
     jf.getContentPane().add(ed);
     jf.show();
     System.out.println(S);
     jf.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
     S = subs.getCoordinateInformation( false );
         //ShowOrientationInfo( null ,orMat ,null ,null , false );
     
     jf  = new JFrame("PLAIN");
     ed= new JEditorPane("text/plain", S);
     jf.getContentPane().add(ed);
     jf.setSize(400,400);
     jf.show();

     jf.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
     System.out.println(S);
     }catch(Exception s)
     {
        s.printStackTrace();
        System.exit(0);
        
     }
     
     

   }

}
