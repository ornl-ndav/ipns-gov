/*
 * File: FloatFilter.java
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
 *  Revision 1.1  2002/06/06 16:03:13  pfpeterson
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
public class FloatFilter implements StringFilterer {
    private static Character MINUS =new Character((new String("-")).charAt(0));
    private static Character PLUS  =new Character((new String("+")).charAt(0));
    private static Character DEC   =new Character((new String(".")).charAt(0));
    private static Character E     =new Character((new String("E")).charAt(0));
    
    private boolean automod;

    public FloatFilter(){
        this(true);
    }
    public FloatFilter(boolean automicallymodifystring){
        this.automod=automicallymodifystring;
    }

    public boolean isOkay(int offs, String inString, String curString){
        if(automod) this.modifyString(offs,inString, curString);
        char[] source = inString.toCharArray();
        String stuff=MINUS.toString();
        for( int i=0 ; i < source.length ; i++ ){
            if(Character.isDigit(source[i])){
                // do nothing
            }else if(DEC.compareTo(new Character(source[i]))==0){
                if(curString.indexOf(DEC.toString())>=0){
                    return false;
                }else{
                    int index=curString.indexOf(E.toString());
                    if(index>=0){
                        if(offs+i>index){
                            return false;
                        }else{
                            // do nothing
                        }
                    }else{
                        // do nothing
                    }
                }
                /* }else if(PLUS.compareTo(new Character(source[i]))==0){
                   int pi=curString.indexOf(PLUS.toString());
                   int ei=curString.indexOf(E.toString());
                   if(pi>=0){
                   return false;
                   }else{
                   if(ei>=0){
                   if(offs+i==ei+1){
                   // do nothing
                   }else{
                   return false;
                   }
                   }else{
                   return false;
                   }
                   }*/
            }else if(MINUS.compareTo(new Character(source[i]))==0){
                int mi=curString.indexOf(MINUS.toString());
                int ei=curString.indexOf(E.toString());
                if(ei>=0){ // allow two minuses
                    if(offs+i==0){
                        if(offs+i==mi){
                            return false;
                        }else{
                            // do nothing
                        }
                    }else if(offs+i==ei+1){
                        if(mi==0){
                            mi=curString.indexOf(MINUS.toString(),mi+1);
                        }
                        if(offs+i==mi){
                            return false;
                        }else{
                            
                        }
                    }else{
                        return false;
                    }
                }else{     // allow only one minus
                    if(offs+i==0 && mi<0){
                        // do nothing
                    }else{
                        return false;
                    }
                }
                // do nothing
            }else if(E.compareTo(new Character(source[i]))==0){
                if(curString.indexOf(E.toString())>=0){
                    return false;
                }else if( offs==0 && i==0 ){
                    return false;
                }else{
                    if(offs+i<=curString.indexOf(DEC.toString())){
                        return false;
                    }else{
                        // do nothing
                    }
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
