/*
 * File:  Conversions.java
 *
 * Copyright (C) 2006, Dennis Mikkelson
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
 * Contact :  Dennis Mikkelson<mikkelsond@uwstout.edu>
 *            MSCS Department
 *            HH237H
 *            Menomonie, WI. 54751
 *            (715)-232-2291
 *
 * This work was supported by the National Science Foundation under grant
 * number DMR-0426797, and by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.10  2006/07/03 21:06:52  dennis
 *  Cleaned up oddity regarding type casts/autoboxing.
 *
 *  Revision 1.9  2006/06/29 15:01:22  rmikk
 *  Added a method to convert Vectors and Arrays to a matching real array
 *
 *  Revision 1.8  2006/06/28 15:35:52  rmikk
 *  Added conversions for ArrayPG. ToVec
 *
 *  Revision 1.7  2006/06/28 14:02:57  rmikk
 *  Added routines for ArrayPG to convert String to Vector
 *
 *  Revision 1.6  2006/06/27 22:25:24  rmikk
 *  Added 3 new conversions to IntegerVectors,FloatVectors and StringVectors
 *
 *  Revision 1.5  2006/06/27 21:47:49  rmikk
 *  Fixed error in comparing double to float.  Usually they are never equal
 *
 *  Revision 1.4  2006/06/27 16:32:28  taoj
 *  Added get_float() method to get a float value from a Number or String object.
 *
 *  Revision 1.3  2006/06/23 14:19:17  dennis
 *  Added initial version of method get_String() to get a String
 *  from an object.  For now, this is very straight forward and
 *  just gets a default form of the String object.  This "may" in
 *  the future be extended to get specialized String forms for
 *  special objects, such as a compact String form of an increasing
 *  sequence of integers in an int[].
 *
 *  Revision 1.2  2006/06/15 22:01:07  dennis
 *  Added get_int() method to get an integer value from an Object
 *  that is a Number or a String.
 *
 *  Revision 1.1  2006/06/12 21:52:28  dennis
 *  Initial version of new code for parameter GUIs.  While this is
 *  loosely based on the parameter GUIs developed several years ago
 *  by Peter Peterson and Chris Bouzek, the current system is much
 *  simplified, and should be easier to maintain and extend.
 *
 */
package gov.anl.ipns.Parameters;
import java.util.Vector;
import java.lang.reflect.*;

/**
 *  This class has static methods to get particular data type values from
 *  a generic object, when possible.  When this is not possible, the methods
 *  should throw an IllegalArugmentException.
 */
public class Conversions
{

  private Conversions()
  {
    // private constructor, since this class should just have static methods
  }


  /**
   *  Get a boolean value from the specified object, if possible.  
   *  Conversions from objects of class Boolean, String or Integer are
   *  supported.  If a String is passed in it must have either "true" or
   *  "false" as its only non-blank characters, ignoring case.  If an
   *  Integer is passed in, all non-zero values will be considered to be
   *  true.  A null object is considered to be false.  All other cases
   *  will cause an IllegalArugumentException to be thrown.
   *
   *  @param  obj   A Boolean, String or Integer object that can be
   *                interpreted as containing a boolean value.
   *  
   *  @return the boolean value obtained from the specified object.
   *
   *  @throws IllegalArgumentException if the object cannot be converted
   *          to a boolean value.
   */

  public static boolean get_boolean( Object obj ) 
                                            throws IllegalArgumentException
  {
    boolean bool_value;

    if( obj == null )
      bool_value = false;
   
    else if ( obj instanceof Boolean )
      bool_value = ((Boolean)obj).booleanValue();
    
    else if( obj instanceof String )
    {
      String temp = ((String)obj).trim();
      if ( temp.equalsIgnoreCase( "true" ) )
        bool_value = true;
      else if ( temp.equalsIgnoreCase( "false" ) )
        bool_value = false;
      else
        throw new IllegalArgumentException("String not boolean value:" + temp);
    }

    else if ( obj instanceof Integer )
    {
      int intval = ((Integer)obj).intValue();

      if ( intval == 0 )
        bool_value = false;
      else
        bool_value = true;
    }

    else
      throw new IllegalArgumentException("Object not boolean value:" + obj);
    
    return bool_value;
  }


  /**
   *  Get an int value from the specified object, if possible.  
   *  Conversions from objects of class Number or String are
   *  supported.  If a String is passed in it must be a sequence of 
   *  characters representing an integer.  If a Number object is passed
   *  in, it's value must be an integer value.  For example a Float with
   *  a value 1.0f will return the integer 1, but a Float with a value of
   *  1.3f with cause an IllegalArgumentException to be thrown.   A null
   *  object will give the value 0, as a default.  All other objects will 
   *  cause an IllegalArugumentException to be thrown.
   *
   *  @param  obj   A Number or String object that can be
   *                interpreted as an integer value.
   *  
   *  @return the int value obtained from the specified object.
   *
   *  @throws IllegalArgumentException if the object cannot be converted
   *          to an integer value.
   */

  public static int get_int( Object obj ) throws IllegalArgumentException
  {
    int int_value;

    if( obj == null )
      int_value = 0;

    else if ( obj instanceof Number )
    {
      double double_value = ((Number)obj).doubleValue();
      int_value = ((Number)obj).intValue();
      if ( int_value == double_value )
        return int_value;
      else
        throw new IllegalArgumentException(
                                "Number not an int value:" + double_value);
    }

    else if( obj instanceof String )
    {
      String temp = ((String)obj).trim();
      try
      {
        double double_value = Double.parseDouble( temp );
        int_value = (int)double_value;          // we DO consider 1.0 to be int
        if ( int_value == double_value )
          return int_value;
        else
          throw new IllegalArgumentException(
                                "Number not an int value:" + double_value);
      }
      catch ( Exception exception )
      {
        throw new IllegalArgumentException("String not int value:" + temp);
      }
    }

    else
      throw new IllegalArgumentException("Object not int value:" + obj);

    return int_value;
  }

  /**
   *  Get a float value from the specified object, if possible.  
   *  Conversions from objects of class Number or String are
   *  supported.  If a String is passed in it must be a sequence of 
   *  characters representing a float number.  If a Number object is passed
   *  in, it's value must be a float value. A null
   *  object will give the value 0.0f, as a default.  All other objects will 
   *  cause an IllegalArugumentException to be thrown.
   *
   *  @param  obj   A Number or String object that can be
   *                interpreted as a float value.
   *  
   *  @return the float value obtained from the specified object.
   *
   *  @throws IllegalArgumentException if the object cannot be converted
   *          to a float value.
   */  
  
  public static float get_float( Object obj ) throws IllegalArgumentException
  {
    float float_value;

    if( obj == null )
      float_value = 0.0f;

    else if ( obj instanceof Number )
    {
     
      float_value = ((Number)obj).floatValue();
      
      return float_value;
     
    }

    else if( obj instanceof String )
    {
      String temp = ((String)obj).trim();
      //Pattern regf = Pattern.compile("^[+|-]?[0-9]*(\\.[0-9]+)?([eE][+|-]?[0-9]+)?$");      
      //Matcher s2f = regf.matcher(temp); 
      //use the java static Float.valueof() method instead to convert string to float;

      try {
        float_value = Float.parseFloat(temp);
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException("String not a float value:" + obj);
      }
    }

    else throw new IllegalArgumentException("Object not a float value:" + obj);

    return float_value;
  }

  
  /**
   *  Get a String value from the specified object, if possible.  
   *
   *  @param  obj   An object that is to be converted to a String
   *  
   *  @return the String value obtained from the specified object.
   *
   *  @throws IllegalArgumentException if the object cannot be converted
   *          to a String value.
   */

  public static String get_String( Object obj ) throws IllegalArgumentException
  {
    String str_value;

    if( obj == null )
      str_value = "";

    else
      str_value = "" + obj;       // use default way of converting Object to
                                  // String.  We may accomodate specific 
                                  // conversions for other data types in the
                                  // future.
    return str_value;
  }
  
  
  
  /**
   *  Get a Vector of Integer values from the specified object, if possible.  
   *
   *  @param  obj   An object that is to be converted to a Vector of Integers
   *  
   *  @return the Vector with Integer values obtained from the specified object.
   *
   *  @throws IllegalArgumentException if the object cannot be converted
   *          to a Vector of Integer values.
   */

  public static Vector<Integer> get_IntegerVector( Object obj) throws 
                                         IllegalArgumentException{
	  if( obj == null)
		  return new Vector<Integer>();
	  if( obj instanceof String)
		  obj = StringToVec( (String)obj);
	  Vector<Integer> Res = new Vector<Integer>();
	  if( obj instanceof Vector){
		  
		  for( int i =0; i< ((Vector)obj).size(); i++ ){
			  Res.addElement( new Integer( get_int( ((Vector)obj).elementAt(i))));
		  }
		  return Res;
	  }else if( obj.getClass().isArray()){
		  for( int i=0; i< Array.getLength( obj); i++)
			  Res.addElement( (Integer)Array.get(obj,i));
		  return Res;
	  }else{
	     Res.addElement( new Integer( get_int( obj)));
	  }
	 return Res; 
  }
  
  
  
  
  /**
   *  Get a Vector of Float values from the specified object, if possible.  
   *
   *  @param  obj   An object that is to be converted to a Vector of Floats
   *  
   *  @return the Vector with Float values obtained from the specified object.
   *
   *  @throws IllegalArgumentException if the object cannot be converted
   *          to a Vector of Float values.
   */

  public static Vector<Float> get_FloatVector( Object obj) throws 
                                         IllegalArgumentException{
	  if( obj == null)
		  return new Vector<Float>();
	  if( obj instanceof String)
		  obj = StringToVec( (String)obj);
	  Vector<Float> Res = new Vector<Float>();
	  if( obj instanceof Vector){
		  
		  for( int i =0; i< ((Vector)obj).size(); i++ ){
			  Res.addElement( new Float( get_float( ((Vector)obj).elementAt(i))));
		  }
		  return Res;
	  }else if( obj.getClass().isArray()){
		  for( int i=0; i< Array.getLength( obj); i++)
			  Res.addElement( (Float)Array.get(obj,i));
		  return Res;
	  }else{
	     Res.addElement( new Float( get_float( obj)));
	  }
	 return Res; 
  }
  
  
  
  
  /**
   *  Get a Vector of String values from the specified object, if possible.  
   *
   *  @param  obj   An object that is to be converted to a Vector of Strings
   *  
   *  @return the Vector with String values obtained from the specified object.
   *
   *  @throws IllegalArgumentException if the object cannot be converted
   *          to a Vector of String values.
   */

  public static Vector<String> get_StringVector( Object obj) throws 
                                         IllegalArgumentException{
	  if( obj == null)
		  return new Vector<String>();
	  if( obj instanceof String)
		  obj = StringToVec( (String)obj);
	  Vector<String> Res = new Vector<String>();
	  if( obj instanceof Vector){
		  
		  for( int i =0; i< ((Vector)obj).size(); i++ ){
			  Res.addElement( get_String( ((Vector)obj).elementAt(i)));
		  }
		  return Res;
	  }else if( obj.getClass().isArray()){
		  for( int i=0; i< Array.getLength( obj); i++)
			  Res.addElement(  Array.get(obj,i).toString());
		  return Res;
	  }else{
	     Res.addElement(  get_String( obj));
	  }
	 return Res; 
  }



   /**
	 * Finds the first of occurrence of one letter in SearchChars in
	 * the String S starting at start. Handles quoted strings
	 *     
	 * The search will not search items between two parentheses or
	 * BraceChars if the
	 * search started outside the parenthsesis.
	 * @param  S           the string to search in
	 * @param  start       the position in String S to start searching.
	 * @param  SrchChars   The "set" of characters to be found
	 * @param  brcpairs    Pairs of characters represent start and end of
	 *                     "braces". The SearchChars between braces will not
	 *                     be considered for matching. 
	 *  @return the position of the found character or the end of the string
	 */

	public static int finddQuote(String S, int start, String SrchChars,
			String brcpairs) {
		int i, j;
		int brclevel;
		boolean quote;

		if (S == null)
			return -1;
		if (SrchChars == null)
			return -1;
		if ((start < 0) || (start >= S.length()))
			return S.length();
		brclevel = 0;
		quote = false;

		for (i = start; i < S.length(); i++) {
			char c = S.charAt(i);

			if (c == '\"') {
				if ((!quote) && (brclevel == 0) && (SrchChars.indexOf(c) >= 0))
					return i;
				quote = !quote;
				//if( i >= 1)
				//    if( S.charAt( i - 1 )  =='\"' ){
				//       quote = !quote;
				//     }
			} else if (quote) {
			} else if (SrchChars.indexOf(c) >= 0) {
				if (brclevel == 0)
					return i;
			}
			if ((!quote) && (brcpairs != null)) {
				j = brcpairs.indexOf(c);
				if (j < 0) {
				} else if (j == 2 * (int) (j / 2))
					brclevel++;
				else
					brclevel--;
			}
			if (brclevel < 0)
				return i;
		}
		return S.length();
	}

	//------------------------- ArrayPG routines --------------------------
	/**
	 *   Will parse a String representation of a Vector to its Vector form
	 *   @param  S  The String to parse
	 *   @return  The Vector corresponding to the String S
	 *
	 * NOTE: Entries can be separated by spaces or commas,
	 *       Entries can be vectors use [ ] to represent vectors
	 *       No algebraic operations are supported on the entries
	 *       entries can be 3:5:2. These will be expanded
	 *       All other entries must be Strings,integer, float or boolean
	 */
	public static Vector StringToVec(String S)
			throws java.lang.IllegalArgumentException {

		S = S.trim();
		if (S.startsWith("["))
			if (finddQuote(S, 1, "]", "()[]") == S.length() - 1)
				S = S.substring(1, S.length() - 1);
		return RecursStringToVec(S, 0);
	}

	//start is so errors will give letter relative to first letter in original string
	private static Vector RecursStringToVec(String S, int start)
			throws java.lang.IllegalArgumentException {

		S = S.trim();
		Vector Res = new Vector();
		int k = 0;
		while (k < S.length()) {
			if (S.charAt(k) == '[') {
				int k1 = finddQuote(S, k + 1, "]", "[]");
				if (k1 < 0)
					throw new java.lang.IllegalArgumentException(
							"Braces do not match at letter " + (k + start));
				Res.addElement(RecursStringToVec(S.substring(k + 1, k1),
								k + 1));
				k = k1 + 1;
			} else if (S.charAt(k) == '\"') {
				int k1;
				for (k1 = k + 1; (k1 < S.length()) && (S.charAt(k1) != '\"');
				                             k1++) {}
				
				Res.addElement(S.substring(k + 1, k1));
				k = k1 + 1;

			} else {//convert to proper data type
				int k1 = finddQuote(S, k, " ,", "");
				if (k1 < 0)
					k1 = S.length();
				String S1 = S.substring(k, k1);
				Vector Res1 = null;
				if (S1.indexOf(":") >= 0) {
					Res1 = GetSubRange(S1);
				}
				if (Res1 != null) {
					Res.addAll(Res1);
					k = k1;

				} else
					try {
						Res.addElement(new Integer(S1));
						k = k1;
					} catch (Exception s1) {
						try {

							Res.addElement(new Float(S1));
							k = k1;
						} catch (Exception s2) {
							if (";YES;NO;TRUE;FALSE;T;F;".indexOf(";"
									+ S1.trim().toUpperCase() + ";") >= 0)
								Res.addElement(new Boolean(S1));
							else
								Res.addElement(S1);
							k = k1;
						}

					}

			}
			while ((k < S.length()) && (S.charAt(k) == ' '))
				k++;
			if (k < S.length())
				if (S.charAt(k) == ',')
					k++;
		}//while
		return Res;
	}

	private static Vector GetSubRange(String S) {
		if (S == null)
			return null;
		int k1 = S.indexOf(":");
		int k2 = -1;
		if (k1 < S.length())
			k2 = S.indexOf(":", k1 + 1);
		if (k2 < 0)
			k2 = S.length();
		try {
			int N1 = (new Integer(S.substring(0, k1))).intValue();
			int N2 = -1;
			N2 = (new Integer(S.substring(k1 + 1, k2))).intValue();
			int N3 = -1;
			if (k2 < S.length())
				N3 = (new Integer(S.substring(k2 + 1))).intValue();
			else
				N3 = 1;
			if (N3 == 0)
				return null;
			if ((N2 - N1) / N3 < 0)
				return null;

			Vector Res = new Vector();
			Res.addElement(new Integer(N1));
			int sgn = 1;
			if (N3 < 0)
				sgn = -1;
			while (sgn * (N2 - N1 - N3) >= 0) {
				N1 = N1 + N3;
				Res.addElement(new Integer(N1));
			}
			return Res;
		} catch (Exception ss) {
			return null;
		}
	}
	
	
	  
	  /**
	   * This method attempts to convert an Object to a Vector. If the
	   * Object is an array of arrays of arrays of... it will convert it
	   * to a Vector of Vectors of Vectors
	   * @param O  The Object to be converted
	   * @return   The Vectorified Object if possible, otherwise the Object is
	   *            returned unchanged.
	   *  Copied from DataSetTools.operator.Utils
	   */
	  public static Vector ToVec( Object O) throws IllegalArgumentException{
	    Vector Res = new Vector();
	    if( O == null)
	      return new Vector();
	    if( O instanceof String)
	    	O = StringToVec( (String)O);
	    if( O instanceof Vector){
	       for( int i=0; i< ((Vector)O).size(); i++)
	          Res.add( ToVec1(((Vector)O).elementAt(i)));
	       return Res;
	       
	    }
	   if( O.getClass().isArray()){
	      for( int i=0; i< Array.getLength(O); i++)
	         Res.add( ToVec1( Array.get(O,i)));
	         return Res;
	   }
	   
	   
	  Res.addElement( O);
	  return Res;
	  }
	  
	private static Object ToVec1(Object O) throws IllegalArgumentException {
		if (O == null)
			return null;
		if (O instanceof Vector)
			return ToVec(O);
		if (O.getClass().isArray())
			return ToVec(O);
		

		if (O.getClass().equals(float.class))
			return new Float(O.toString().trim());

		if (O.getClass().equals(int.class))
			return new Integer(O.toString().trim());
		if (O instanceof String)
			return O;
		if (O.getClass().equals(byte.class))
			return new Byte(O.toString().trim());
		if (O.getClass().equals(long.class))
			return new Long(O.toString().trim());
		if (O.getClass().equals(short.class))
			return new Short(O.toString().trim());
		if (O.getClass().equals(double.class))
			return new Double(O.toString().trim());
		return O;

	}
	
	/**
	 *  Converts Vector or Array Objects to a matching Real Array
	 * @param obj  A  Vector( of Vectors/Arrays..) or an Array (of Arrays/Vectors..) that can be
	 *             converted to a real arrayy
	 * @param matchClass  The class it must match, i.e. ( new int[0][0][0]).getClass()
	 * @return  The converted Object
	 * @throws IllegalArgumentException
	 */
	public static Object get_RealArray( Object obj, Class matchClass) throws
	                                 IllegalArgumentException{
		if( (obj == null) || (matchClass == null)) throw
			new IllegalArgumentException( "Cannot convert null to real arrays");
		if( !matchClass.isArray())throw
		      new IllegalArgumentException( "matching class is not a real array"+
		    		  " or has different number of dimensions");
		if(!(obj instanceof Vector)&& !(obj.getClass().isArray()))throw
	           new IllegalArgumentException( "Cannot convert to real array");
		int N;
		if( obj instanceof Vector)
			N = ((Vector)obj).size();
		else
			N= Array.getLength( obj);
		Class subComponentClass = matchClass.getComponentType();
		Object Res = java.lang.reflect.Array.newInstance( subComponentClass , N);
		for( int i=0; i< N ; i++){
			Object elt;
			if( obj instanceof Vector)
				elt = ((Vector)obj).elementAt(i);
			else
				elt = Array.get( obj,i);
			
			if( elt == null)throw
	           new IllegalArgumentException( "Cannot convert null to a numeric value");
				
			if( (elt instanceof Vector) || (elt.getClass().isArray())){
				Array.set( Res, i, get_RealArray( elt, matchClass.getComponentType()));
		    
			}else if( !subComponentClass.isPrimitive())throw
			     new IllegalArgumentException( "the matching class has different number of dimensions");
			else if( elt instanceof Number){
				if( subComponentClass.equals( Integer.TYPE))
					Array.setInt( Res, i, ((Number)elt).intValue());
				else if( subComponentClass.equals( Short.TYPE))
					Array.setShort( Res, i, ((Number)elt).shortValue());
				else if( subComponentClass.equals( Long.TYPE))
					Array.setLong( Res, i, ((Number)elt).longValue());
				else if( subComponentClass.equals( Byte.TYPE))
					Array.setByte( Res, i, ((Number)elt).byteValue());
				else if( subComponentClass.equals( Float.TYPE))
					Array.setFloat( Res, i, ((Number)elt).floatValue());
			    else if( subComponentClass.equals( Double.TYPE))
					Array.setDouble( Res, i, ((Number)elt).doubleValue());
			   	
				
			}else throw
			   new IllegalArgumentException( "Cannot convert "+ elt.getClass()+" to a number");
			
		}
		
	    return Res;	
	}
	
	public static void main( String args[]){
      Vector V = new Vector();
      V.addElement( new Integer(3));
      V.addElement( new Integer(5));
      V.addElement( new Integer( 7));
      Vector W = new Vector();
      W.addElement( V);
      V = new Vector();
      V.addElement( new Integer(13));
      V.addElement( new Integer(15));
      V.addElement( new Integer( 17));
      W.addElement( V);

      V = new Vector();
      V.addElement( new Integer(23));
      V.addElement( new Integer(25));
      V.addElement( new Integer( 27));
      W.addElement( V);
      System.out.println( Conversions.get_RealArray(W, (new double[0][0]).getClass()).getClass());
      
      int[][] WW ={ {1,2,3,4},{5,6,7,8},{9,10,11,12}};
      System.out.println(gov.anl.ipns.Util.Sys.StringUtil.toString( Conversions.get_RealArray(WW, (new float[0][0]).getClass())));
      
		
		
	}
}

