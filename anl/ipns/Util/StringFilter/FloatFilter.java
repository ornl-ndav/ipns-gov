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
 *  Revision 1.6  2004/03/11 23:01:54  hammonds
 *  Changed classpath to gov.anl.ipns.Util.StringFilter.
 *
 *  Revision 1.5  2004/01/24 21:55:22  bouzekc
 *  Now inherits from StringFilter.
 *
 *  Revision 1.4  2004/01/24 21:09:44  bouzekc
 *  Removed unused local variables.
 *
 *  Revision 1.3  2003/11/05 04:29:29  bouzekc
 *  Fixed bug where a floating point number String with an exponent
 *  less than -3 was considered invalid.  Added javadocs to isOKay.  Added
 *  code comments to isOkay.
 *
 *  Revision 1.2  2002/11/27 23:23:49  pfpeterson
 *  standardized header
 *
 *  Revision 1.1  2002/06/06 16:03:13  pfpeterson
 *  Added to CVS.
 *
 *
 *
 */
 
package gov.anl.ipns.Util.StringFilter;

/**
 * Internal class to do all of the formatting checks and pass out
 * PropertChange events to listeners. Should only be used from within
 * the package.
 */
public class FloatFilter extends StringFilter {
    private static Character MINUS =new Character((new String("-")).charAt(0));
    //private static Character PLUS  =new Character((new String("+")).charAt(0));
    private static Character DEC   =new Character((new String(".")).charAt(0));
    private static Character E     =new Character((new String("E")).charAt(0));
    
    private boolean automod;

    public FloatFilter(){
        this(true);
    }
    public FloatFilter(boolean automicallymodifystring){
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
        //if our automod is turned on, change the string to insert to 
        //uppercase.
        if(automod) this.modifyString(offs,inString, curString);

        //convert the insert String to a character array
        char[] source = inString.toCharArray();

        //look through our insert String at each character to see if it is 
        //valid
        for( int i=0 ; i < source.length ; i++ ){
            if(Character.isDigit(source[i])){
                // do nothing            
            }else if(DEC.equals(new Character(source[i]))){
                //i.e. the current character is a decimal point
                if(curString.indexOf(DEC.toString())>=0){
                  //We have one decimal point in the String already, so we
                  //can't insert another
                    return false;
                }else{
                    //look for an exponent character.  Maybe that is where the
                    //decimal point goes
                    int index=curString.indexOf(E.toString());
                    if(index>=0){
                        //found an exponent at index
                        if(offs+i>index){
                            //we can't insert the decimal point after the
                            //exponent symbol
                            return false;
                        }else{
                            //do nothing-we can insert the decimal point
                            //before the "E"
                        }
                    }else{
                        //do nothing-no exponent, so insert the decimal
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
            }else if(MINUS.equals(new Character(source[i]))){
                //the current character is a minus symbol
                int mi=curString.indexOf(MINUS.toString());
                int ei=curString.indexOf(E.toString());

                //if there is an exponent sign, we need to allow for two
                //minuses
                if(ei>=0){
                    if(offs+i==0){
                        //insert is at index 0 of the current String, and the
                        //first character in the insert String is a minus: i.e.
                        //we are trying to make the number negative
                        if(offs+i==mi){
                            //we can't re-negate the String number this way
                            return false;
                        }else{
                            //do nothing-negate the number
                        }
                    }else if(offs+i==ei+1){
                        //we are trying to insert a minus on the exponent
                        //number
                        if(mi==0){
                            //number is negative already, so try to determine
                            //if the exponent is negated already
                            mi=curString.indexOf(MINUS.toString(),mi+1);
                        }
                        if(offs+i==mi){
                            //we can't re-negate the exponent this way
                            return false;
                        }else{
                            //do nothing-fine to negate the exponent
                        }
                    }else{
                        //can't insert the minus sign in some non-standard
                        //location
                        return false;
                    }
                }else{     
                    //no exponent in the current String, but check to see if the 
                    //INSERT String has an exponent
                    int ei2=inString.indexOf(E.toString());
                    if(ei2<0){
                        if(offs+i==0 && mi<0){
                            //do nothing-we are inserting a minus at the beginning 
                            //into a "positive" number String
                        }else{
                            //we can't re-negate the number
                            return false;
                        }    
                    }else{
                        //exponent in insert string
                        int mi2=inString.indexOf(MINUS.toString());
                        if(mi2==0 || (mi2==(ei2+1))){
                            //do nothing.  The insert string is fine.
                        }else{
                            return false;
                        }
                    }
                }
            }else if(E.compareTo(new Character(source[i]))==0){
                //trying to insert an exponent
                if(curString.indexOf(E.toString())>=0){
                    //can't insert an exponent twice
                    return false;
                }else if( offs==0 && i==0 ){
                    //can't insert an exponent at position zero of the current
                    //String
                    return false;
                }else{
                    if(offs+i<=curString.indexOf(DEC.toString())){
                        //can't insert an exponent in between an integer and a
                        //decimal point
                        return false;
                    }else{
                        //do nothing-we have exhausted all special cases, so
                        //consider it a good insert
                    }
                }
            }else{
                //no idea what we are trying to insert here.  It is not OK, so
                //return false
                return false;
            }
        }
        
        //made it through.  Call the insert good.
        return true;
    }
}
