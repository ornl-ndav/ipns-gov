/*
 * File: IntArrayFilter.java
 *
 * Copyright (C) 2002, Peter F. Peterson
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
 *  Revision 1.5  2004/01/24 21:07:22  bouzekc
 *  Added javadocs.
 *
 *  Revision 1.4  2003/11/05 04:32:34  bouzekc
 *  Added javadoc comments to isOkay().
 *
 *  Revision 1.3  2003/04/18 15:25:24  pfpeterson
 *  Now allows a minus sign, but not two adjacent.
 *
 *  Revision 1.2  2002/11/27 23:23:49  pfpeterson
 *  standardized header
 *
 *  Revision 1.1  2002/06/06 16:03:14  pfpeterson
 *  Added to CVS.
 *
 *
 *
 */
 
package DataSetTools.util;

/**
 * Internal class to do all of the formatting checks and pass out
 * PropertChange events to listeners. Should only be used from within
 * the package.
 */
public class IntArrayFilter implements StringFilterer {
    private static Character COLON =new Character((new String(":")).charAt(0));
    private static Character COMMA =new Character((new String(",")).charAt(0));
    private static Character MINUS =new Character((new String("-")).charAt(0));
    
    private boolean automod;

    public IntArrayFilter(){
        this(true);
    }
    public IntArrayFilter(boolean automicallymodifystring){
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
        char[] source=inString.toCharArray();
        for( int i=0 ; i< source.length ; i++ ){
            if (Character.isDigit(source[i])) {
              // do nothing
            }else if(MINUS.compareTo(new Character(source[i]))==0){
              int mi=curString.indexOf(MINUS.toString());
              while(mi>=0){
                if(offs+i==mi || offs+i==mi+1){
                  return false;
                }else{
                  // do nothing
                }
                mi=curString.indexOf(MINUS.toString(),mi+1);
              }
            }else if(COLON.compareTo(new Character(source[i]))==0){
                if(offs+i<=0) return false;
                if(curString.length()>0){
                    Character checker;
                    checker=new Character(curString.charAt(offs+i-1));
                    if(checker.equals(COLON)) return false;
                    if(checker.equals(COMMA)) return false;
                    if(curString.length()>offs+i){
                        checker=new Character(curString.charAt(offs+i));
                        if(checker.equals(COLON)) return false;
                        if(checker.equals(COMMA)) return false;
                    }
                    
                }
            }else if(COMMA.compareTo(new Character(source[i]))==0){
                if(offs+i<=0) return false;
                if(curString.length()>0){
                    Character checker;
                    checker=new Character(curString.charAt(offs+i-1));
                    if(checker.equals(COLON)) return false;
                    if(checker.equals(COMMA)) return false;
                    if(curString.length()>offs+i){
                        checker=new Character(curString.charAt(offs+i));
                        if(checker.equals(COLON)) return false;
                        if(checker.equals(COMMA)) return false;
                    }
                }
            }else{
                return false;
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
        return inString;
    }
}
