/*
 * File: IntegerFilter.java
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
 *           9700 S. Cass Avenue, Bldg 360
 *           Argonne, IL 60440
 *           USA
 *
 * For further information, see http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.1  2002/06/06 16:03:15  pfpeterson
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
public class IntegerFilter implements StringFilterer {
    private static Character MINUS =new Character((new String("-")).charAt(0));
    private static Character PLUS  =new Character((new String("+")).charAt(0));
    private static Character DEC   =new Character((new String(".")).charAt(0));
    private static Character E     =new Character((new String("E")).charAt(0));
    private static Character ZERO  =new Character((new String("0")).charAt(0));
    
    private boolean automod;

    public IntegerFilter(){
        this(false);
    }
    public IntegerFilter(boolean automicallymodifystring){
        this.automod=automicallymodifystring;
    }

    public boolean isOkay(int offs, String inString, String curString){
        if(automod) this.modifyString(offs,inString,curString);
        char[]    source  = inString.toCharArray();
        
        for( int i=0 ; i < source.length ; i++ ){
            if(Character.isDigit(source[i])){
                if(ZERO.compareTo(new Character(source[i]))==0){
                    if( offs+i==0 && curString.length()>0 ){
                        return false;
                    }else if(curString.startsWith(MINUS.toString())){
                        if( offs+i==1 ){
                            return false;
                        }else{
                            // do nothing
                        }
                    }else{
                        // do nothing
                    }
                }else{
                    // do nothing
                }
            }else if(MINUS.compareTo(new Character(source[i]))==0){
                if(offs+i==0){
                    if(curString.startsWith(MINUS.toString())){
                        return false;
                    }else{
                        // do nothing
                    }
                }else{
                    return false;
                }
            }else{
                return false;
            }
        }
        
        return true;
    }
    public String modifyString(int offs, String inString, String curString){
        return inString.toUpperCase();
    }
}
