package gov.anl.ipns.DisplayDevices;

import java.awt.Color;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Vector;
import gov.anl.ipns.Parameters.*;


public class Util {

   public Util() {

      super();
      // TODO Auto-generated constructor stub
   }


   
   /**
    * 
    * @param C  The desired Class to put parts of orig into
    * @param orig  The variable with the data which is essentially
    *             of the correct class except that it can have Vector
    *             components or array components.
    * @return   The arrayified Vector
    * @throws Exception  if this process is not possible
    */
   public static Object cvrt( Class C, Object orig) throws Exception{
     if( C == null)
       return null;
     if( orig == null)
       return null;
     if( orig.getClass().equals( C))
         return orig;  
     if( C.isArray()){
        Class C1 = C.getComponentType();
        int n = Size(orig);
        if( n < 0)
           throw new IllegalArgumentException("# of Dimension do not correspond");
        Object u = Array.newInstance( C1,Size( orig ));
        for( int i = 0; i<Size(orig); i++)
          Array.set(u,i, cvrt(C1,Elt(orig,i)));    
       return u;
     }
    Class[] interfaces = C.getInterfaces();
    boolean isCollection = false;
    if( interfaces != null){
      for( int i = 0; (i< interfaces.length) && !isCollection; i++)
          if( interfaces[i] == Collection.class)
            isCollection = true;
    }
    
    if( isCollection){  //Vectors
       
      Collection CC = (Collection)C.newInstance();
      for( int i=0; i<Size(orig); i++)
         CC.add( Elt(orig,i));
      
       return CC;
    }
    

    if( C == orig.getClass() ) 
       return orig;

      try {
          
         if( ! C.isPrimitive() )
            if( C.getClass().equals( java.awt.Color.class ) )
               return Convert2Color( orig );
            else if( C == Integer.class ) {
               int res = Conversions.get_int( orig );

               return new Integer( res );
            }
            else if( C == Float.class ) {
               return new Float( Conversions.get_float( orig ) );
            }
            else if( C == Double.class ) {
               return new Double( Conversions.get_float( orig ) );

            }
            else if( ! C.isAssignableFrom( orig.getClass() ) )
               throw new IllegalArgumentException( C.toString() + " and "
                        + orig.getClass().toString() + " are incompatible" );
            else
               return orig;

          // 9? 8 primitive types
         try{
         if( C == double.class ){
            
            return new Double( (double)Conversions.get_float( orig ) );
         }if( C == float.class ){
            
            return new Double( Conversions.get_float( orig ) );
         }
         if( C == int.class ){
            
            return new Integer( Conversions.get_int( orig ) );
         }if( C == byte.class)
            return new Byte( (byte)Conversions.get_int(orig) );
         
         if( C == short.class)
            return new Short( (short)Conversions.get_int(orig) );
         
         if( C == long.class)
            return new Long( (long)Conversions.get_int(orig) );
         
         if( C == boolean.class)
            return new Boolean( Conversions.get_boolean(orig) );
         if( C == char.class){
            String S = Conversions.get_String( orig );
            if( S != null && S.length() > 0)
               return new Character( S.charAt( 0 ));
            return null;
         }
            
         
         }catch( Exception s){
            return null;
         }
         
         
         
         return null; //String, Integer, etc. returned as indicated
      }
      catch( Exception ss ) {

         return null;
      }
   }
   
   
   
    /**
     * 
     * @param V
     * @return
     * TODO : Start with an Object
     */
    public static Object cvrt2MultiArray( Vector V){
       if( V== null)
          return null;
       if( V.size()<=1)
         return null;
      Vector info= getInfo(V.elementAt(0));
      if( info == null)
         return null;
      Object Res = Array.newInstance((Class)info.firstElement(), V.size() );
      Array.set(Res,0,info.lastElement());
      for(int i=0; i<V.size(); i++){
        Vector info1= getInfo(V.elementAt(i));
        if( !info1.firstElement().equals(info.firstElement()))
           return null;
        if( !info.lastElement().getClass().isArray()){
            if( info1.lastElement().getClass().isArray())
             return null;
        }else if( Array.getLength(info.lastElement())!=Array.getLength(info1.lastElement()))
           return null;
        Array.set(Res,i,info1.lastElement());
      }      
      return Res;
    }
    
    private static Vector getInfo( Object Obj){
       if( Obj== null)
          return null;
       
      
       int size =-1;
      
       if( Obj instanceof Vector){
          size=((Vector)Obj).size();
         
       }else if( Obj.getClass().isArray()){
         size = Array.getLength(Obj);
        
       }else  //At the primitive level
         if( (Obj instanceof Number) ||(Obj instanceof String)||
             (Obj.getClass().isPrimitive())){
         Vector Res = new Vector();
         Class C = Obj.getClass();
         if( C.equals(Float.class)) C =  float.class;
         else if( C.equals(Integer.class)) C =  int.class;   
         else if( C.equals(Long.class)) C =  long.class;  
         else if( C.equals(Byte.class)) C =  byte.class;  
         else if( C.equals(Short.class)) C =  short.class; 
         else if( C.equals(Double.class)) C =  double.class; 
         else if( !C.equals(String.class))
            return null; 
         Res.add(C);
         Res.add(Obj);
         return Res;     
       }else
         return null;
       if( size <1)//May at some time create 0 lengthed arrays
         return null; 
       
           
       {
         Vector V = new Vector();
         Vector Res = new Vector();
         if(Obj instanceof Vector)
            V = getInfo( ((Vector)Obj).elementAt(0));
         else
            V= getInfo( Array.get(Obj,0));
         if( V == null)
            return null;
         Object ArrayRes = Array.newInstance((Class)V.firstElement(), size);
         Array.set(ArrayRes,0,V.lastElement());
         Class compClass = (Class)V.firstElement();
         int compSize = 1;
         if( V.lastElement().getClass().isArray())
            compSize=Array.getLength( V.lastElement());
         for( int i=1; i< size; i++){

           if(Obj instanceof Vector)
              V = getInfo(((Vector) Obj).elementAt(i));
           else
              V= getInfo( Array.get(Obj,i));
           if( V== null)
              return null;
           if(!V.firstElement().equals(compClass))
              return null;
           if( V.lastElement().getClass().isArray()){
              if( Array.getLength(V.lastElement())!=compSize)
                 return null;
           }else if( compSize !=1)
               return null;
           Array.set(ArrayRes,i,V.lastElement());  
                      
         }
         Res.add( ArrayRes.getClass());
         Res.add(ArrayRes);
         return Res;
       }
       
      
    }
   /**
    * Returns the number of subcomponents of the given Object
    * @param O  The Object whose length is desired.
    * @return  Returns the number of components in the Array or Collection. If
    *       of neither type -a is returned
    */
   public static int Size( Object O ){
     if( O == null)
       return -1;
     if( O.getClass().isArray())
       return Array.getLength(O);
     if( O instanceof Collection )
       return ((Collection)O).size();
     return -1;
   }
   
   /**
    *   Returns the ith component in the Object O
    * @param O  The Object that may be made up of subcomponents
    * @param i  The ith subcomponent
    * @return   The ith component, if there is one, otherwise null is returned
    */
   public static Object Elt( Object O, int i){
     if( i< 0)
       return null;
      if( i >= Size(O))
        return null;
      if( O.getClass().isArray())
        return Array.get(O,i);
      return ((Collection)O).toArray()[i];
   }
  public static Color Convert2Color( Object value){
      
      if( value == null )
         return null;
      
      if( value instanceof java.awt.Color )
         return (Color)value;
      
      if( !(value instanceof String ) )
         return null;
      
     String Col = ((String)value).toUpperCase().trim();
     
     if( Col.equals( "BLACK"))
        return Color.black;
     else if( Col.equals( "BLue"))
        return Color.blue;
     else      if( Col.equals( "RED"))
        return Color.red;
     else      if( Col.equals( "GREEN"))
        return Color.green;
     else      if( Col.equals( "GRAY"))
        return Color.GRAY;
     else      if( Col.equals( "CYAN"))
        return Color.CYAN;
     else      if( Col.equals( "ORANGE"))
        return Color.ORANGE;
     else      if( Col.equals( "MAGENTA"))
        return Color.MAGENTA;
     else if( Col.equals( "DARK GRAY"))
        return Color.DARK_GRAY;
     else if( Col.equals( "LIGHT GRAY"))
        return Color.LIGHT_GRAY;
     else if( Col.equals( "PINK"))
        return Color.PINK;
     else if( Col.equals( "YELLOW"))
        return Color.YELLOW;
     else if( Col.equals( "WHITE"))
        return Color.WHITE;
     else 
        return null;
     
   }
  
  public static Object TranslateKey(Hashtable tab,String key) throws Exception
  {
    if(tab.containsKey(key))
      return tab.get(key);
    else
      throw new Exception("Attribute "+key+" not found");
    
  }

   
   /**
    * @param args
    */
   public static void main( String[] args ) {

      // TODO Auto-generated method stub

   }

}
