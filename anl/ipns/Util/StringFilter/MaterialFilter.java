/*
 * File: MaterialFilter.java
 *
 * Copyright (C) 2003, Peter F. Peterson
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
 * Contact : Peter F. Peterson <pfpeterson@anl.gov>
 *           Intense Pulsed Neutron Source Division
 *           Argonne National Laboratory
 *           9700 South Cass Avenue, Bldg 360
 *           Argonne, IL 60439-4845, USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.5  2004/03/11 23:02:00  hammonds
 *  Changed classpath to gov.anl.ipns.Util.StringFilter.
 *
 *  Revision 1.4  2004/01/24 21:55:22  bouzekc
 *  Now inherits from StringFilter.
 *
 *  Revision 1.3  2004/01/24 21:07:22  bouzekc
 *  Added javadocs.
 *
 *  Revision 1.2  2003/11/05 04:32:35  bouzekc
 *  Added javadoc comments to isOkay().
 *
 *  Revision 1.1  2003/02/04 20:32:15  pfpeterson
 *  Added to CVS.
 *
 */
 
package gov.anl.ipns.Util.StringFilter;

/**
 * Internal class to do all of the formatting checks and pass out
 * PropertChange events to listeners. Should only be used from within
 * the package.
 */
public class MaterialFilter extends StringFilter {
  private static Character COMMA =new Character((new String(",")).charAt(0));
  private static Character UNDER =new Character((new String("_")).charAt(0));
  private static Character DEC   =new Character((new String(".")).charAt(0));
    
  private boolean automod;

  /**
   * Calls the full constructor with automaticallymodifystring set to
   * true.
   */
  public MaterialFilter(){
    this(true);
  }

  public MaterialFilter(boolean automicallymodifystring){
    this.automod=automicallymodifystring;
  }

  /**
   * This method is designed to test whether or not a given String
   * would be accepted by this StringFilter.
   * 
   * @param  offs                 The offset of the entry point in the
   *                              existing String curString.
   * @param  inString             The String you want to insert.
   * @param  curString            The String which currently exists.
   *
   * @return true if it would be OK to insert inString into curString based
   * on the rules of this filter.
   */
  public boolean isOkay(int offs, String inString, String curString){
    if(automod) this.modifyString(offs,inString, curString);
    char[] source = inString.toCharArray();
    String sub=null;
    if(curString.length()<=0){
      sub=curString;
    }else{
      int start=offs-1;
      int end  =offs+1;

      if(start<0) start=0;
      if(end>=curString.length()) end=curString.length();

      sub=curString.substring(start,end);
    }
    char[] char_sub=sub.toCharArray();

    for( int i=0 ; i < source.length ; i++ ){
      if(Character.isDigit(source[i])){ // number is okay
        if( sub.indexOf(COMMA.toString())==0 ) // not after a ','
          return false;
        if(char_sub.length>0)
          if( Character.isLetter(char_sub[0]) ) // not after a letter
            return false;
      }else if(Character.isLetter(source[i])){ // letter is okay
        if( sub.indexOf(DEC.toString())==0 ) // not after a '.'
          return false;
        if(char_sub.length>0)
          if( Character.isDigit(char_sub[0]) ) // not after a number
            return false;
      }else if(DEC.compareTo(new Character(source[i]))==0){ // decimal is okay
        if( sub.indexOf(DEC.toString())>=0 ) // not next to a '.'
          return false;
        if( sub.indexOf(COMMA.toString())==0 ) // not after a ','
          return false;
      }else if(COMMA.compareTo(new Character(source[i]))==0){ // comma is okay
        if( sub.indexOf(COMMA.toString())>=0 ) // not next to a ','
          return false;
        if( sub.indexOf(DEC.toString())==0 ) // not after a '.'
          return false;
        // comma is okay
      }else if(UNDER.compareTo(new Character(source[i]))==0){ // under is okay
        if( sub.indexOf(UNDER.toString())>=0) // not next to a '_'
          return false;
        if( sub.indexOf(COMMA.toString())==0) // not after a ','
          return false;
      }else{
        return false; // everything else is bad
      }
    }
        
    return true;
  }

	/**
	 * Utility to return the inString turned into upper case.
	 *
	 * @param offs Unused.
	 * @param inString The String to change to uppercase.
	 * @param curString Unused.
	 *
	 * @return inString changed to uppercase.
	 */
  public String modifyString(int offs, String inString, String curString){
    if(inString==null || inString.length()<=0) return inString;

    if(offs==0){// || curString.indexOf(COMMA)==offs){
      return capitalize(inString);
    }else{
      if(curString.substring(offs-1,offs).equals(COMMA.toString())){
        return capitalize(inString);
      }else{
        return inString.toLowerCase();
      }
    }
  }
  
  private static String capitalize(String inString){
    StringBuffer sb=new StringBuffer(inString);

    int comma=-1;
    int len=sb.length();

    while(true){
      sb.replace(comma+1,comma+2,sb.substring(comma+1,comma+2).toUpperCase());
      if(len<=comma+2)
        return sb.toString();
      else
        sb.replace(comma+2,len,sb.substring(comma+2,len).toLowerCase());
      comma=sb.toString().indexOf(COMMA.toString(),comma+1);
      if(comma<0)
        return sb.toString();
    }

    //    return sb.toString();
  }
}
