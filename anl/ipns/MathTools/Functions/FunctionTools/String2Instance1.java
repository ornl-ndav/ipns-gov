/*
 * File:  String2Instance1.java 
 *             
 * Copyright (C) 2000-2002, Ruth Mikkelson
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
 * $Log$
 * Revision 1.8  2005/01/10 16:16:50  dennis
 * Removed empty statement(s).
 *
 * Revision 1.7  2004/03/19 17:24:26  dennis
 * Removed unused variables
 *
 * Revision 1.6  2004/03/15 23:50:12  dennis
 * Simplifed logic to avoid un-needed assignment
 * replaced: ! if((c1=='n')||(c1=='p'))
 * with: if((c1!='n')&&(c1!='p'))
 *
 * Revision 1.5  2004/03/12 01:23:09  rmikk
 * Fixed the name of the super class
 *
 * Revision 1.4  2004/03/12 00:48:19  dennis
 * moved to package MathTools.Functions.FunctionTools
 *
 * Revision 1.3  2004/01/22 02:28:10  bouzekc
 * Removed/commented out unused imports and variables.
 *
 * Revision 1.2  2002/11/27 23:14:36  pfpeterson
 * standardized header
 *
 * Revision 1.1  2002/04/17 21:39:59  dennis
 * Classes for parsing mathematical expressions and generating
 * byte code for evaluating the functions.
 *
 */

package gov.anl.ipns.MathTools.Functions.FunctionTools;

import java.io.*;

/**
* This class converts a string representing an equation
*  into an instance of a newly created class at run-time.<P>
*The syntax is quite simple.<P>
*   <Pre>  
*       double x[]; x[1]=3; x[2]=5;
*	S= new String2Instance("2*x1+3*x2", "rule");
*       Fxn F;
*       F=S.parse();
*       System.out.println(F.vall(x,3));
*     </pre><P>
* @author Ruth Mikkelson, Menomonie Wisconsin, August 1999
*/
public class String2Instance1
{private String s,classname;

 //******If another package name is used, redefine this string analogously
 public static final  String SUPER_CLASS_NAME = 
                             "gov/anl/ipns/MathTools/Functions/FunctionTools/Fxn";

 private ByteArrayOutputStream  bb;
 DataOutputStream str;

  private  int MaxStack,stacksize;
  
  private String IntFxn[],
                 IntFxnCode1[],
                 IntFxnCode2[];  //Internal function info
  private int IntFxnSlen[],
              nIntFxn;

  private String dblList[];
  private int ndblList;       //List of constants used 
 
  private  int errorposition; 
  String errormessage;

  private boolean debug= false;
/**
* Initializes the String2Instance Structure.
* <P>This class does NOT check if the class name is already in use or stored in a .class file<P>
*@param S
*  The String form of an expression with variables x0,x1,x2,... .It can also use the following functions:
*    <ul> acos, asin, atan, cos, exp, log(base E), max, min, round, int, sin, and tan.<BR> Also constants E and PI can be
*      used
*    </ul>
*@param Classname
*  The name to be given to the new class.  

*/  
public String2Instance1(String S,  String Classname)  
  {
    int i,k,x; 
    char c;  
   classname=Classname;
   s = S;
  
   bb=new ByteArrayOutputStream();
   str=new DataOutputStream(bb);
   errorposition=-1; errormessage="";
   nIntFxn=0;k=1;
   for (i=0;i<12;i++)
     {if(s.indexOf(IntFxntst(i))>=0)
       k++;
     }
  
   // Set up the internal functions that are used in the string
   IntFxn=new String[k];
   IntFxnCode1=new String[k]; 
   IntFxnCode2=new String[k];

   IntFxnSlen=new int[k]; 
   nIntFxn=k; 
   ndblList=0;

   nIntFxn=1;
   for (i=0 ; i < 12 ; i++ )
     if(s.indexOf(IntFxntst(i))>=0)
           {IntFxn[nIntFxn]=IntFxntst(i);
            IntFxnCode1[nIntFxn]=IntFxntstCode1(i);
            IntFxnCode2[nIntFxn]=IntFxntstCode2(i);
            IntFxnSlen[nIntFxn]=IntFxntstSlen(i);
             nIntFxn++;
           }
   IntFxn[0]="pow"; 
   IntFxnCode1[0]="(DD)D"; 
   IntFxnCode2[0]="pow";
   IntFxnSlen[0]=3;
 
  

  //set up the constants that are used in the string
   k=0;
   for(i=0;i<s.length();)
    { c=s.charAt(i);
      if(c<'0')i++;
      else if(c>'9')i++;
      else {x=findNumEnd(s,i);            
            if(x>=i)
               {k++;
                 i=x+1;}
            else i++;            
           }
   
    }
   if(s.indexOf('E')>=0) k++;
   if(s.indexOf("PI")>=0)k++;

   ndblList=0; 
   dblList=new String[k];    
   
   for(i=0;i<s.length();)
    { c=s.charAt(i);
      if(c<'0')i++;
      else if(c>'9')i++;
      else {x=findNumEnd(s,i);
            if(x>=i)
            {dblList[ndblList]=s.substring(i,x+1);
            ndblList++; i=x+1; }
            else i++;
           }
    }

   if(s.indexOf('E')>=0) {dblList[ndblList]="E" ; ndblList++;}
   if(s.indexOf("PI")>=0){dblList[ndblList]="PI" ; ndblList++;}

  

   }//End constructor


private String IntFxntst(int i)  // pow,  unminus and unplus are not here
  {if(i<=5)
     {if( i<3)
        {if(i==0)return "acos";
         else if(i==1) return "asin";
         else if (i==2) return "atan";
         }
       else  //i=3..6
        {
         if(i==3) return "abs";
         else if(i==4) return "cos";
         else if(i==5) return "exp";
        }
    
      }
   else //i=7..13
     {if(i<9)
       { if(i==6) return "log";
         else if(i==7)return "max";
         else if(i==8) return"min";
       }
      else
        {if(i==9)return "int";
         else if(i==10)return "round";
         else if(i==11)return  "sin";
         else if(i==12)return "tan";

        }
     }
   return "";
   }//IntFxntst

private int IntFxntstSlen(int i)
   {if(i<=5)
     {if( i<3)
        {if(i==0)return 4;		//"acos";
         else if(i==1) return 4;	//"asin";
         else if (i==2) return 4;	//"atan";
         }
       else  //i=3..6
        {if(i==3) return 3;		//"abs";
         else if(i==4) return 3;	//"cos";
         else if(i==5) return 3;	//"exp";
        }
    
      }
   else //i=7..13
     {if(i<9)
       { if(i==6) return 3;		//"log";
         else if(i==7)return 3;		//"max";
         else if(i==8) return 3;	//"min";
       }
      else
        {if(i==9)return 3;		//"int";
         else if(i==10)return 5;	//"round";
         else if(i==11)return  3;	//"sin"
         else if(i==12)return 3;	//"tan"

        }
     }
  return -1;
   }//intFxntstSlen


private String IntFxntstCode1(int i)
  {return "(D)D";
  }//IntFxnCode1

private String IntFxntstCode2(int i)
  {if(i<=5)
     {if( i<3)
        {if(i==0)return "acos";
         else if(i==1) return "asin";
         else if (i==2) return "atan";
         }
       else  //i=3..6
        {if(i==3) return "abs";
         else if(i==4) return"cos";
         else if(i==5) return "exp";
        }
    
      }
   else //i=7..13
     {if(i<9)
       { if(i==6) return "log";
         else if(i==7)return "max";
         else if(i==8) return "min";
       }
      else
        {if(i==9)return "floor";
         else if(i==10)return "round";
         else if(i==11)return  "sin";
         else if(i==12)return "tan";

        }

    }
   return null;
  }//IntFxntstCode2


private int find(String s, String slist[], int nlist) 	//returns index in list or -1
  {int i;
   //System.out.println("find s and slist[11]="+s+","+slist[0]);
   for (i=0;i<nlist;i++)
    {if(s.equals(slist[i])) return i;
    }
  return -1;
  }//find


/**
* Parses the string, creates its byteCode, then converts this byteCode to an instance of the 
*  class.
*<P>The newly created class' byteCode is NOT stored in a .class file.  It will disappear when this program exits.<P>
*  Methods could be added to the class Fxn to incorporate a save operation. <P> 
*@return  <ul>a subclass of Fxn with the classname specified in the constructor or null if there is an error<BR>
*              Possible Exceptions:<uL> include SecurityException (cannot create new classLoader)<Br>
(					ExceptionInInitializerError(cannot initialize new instance)<Br>
*         </ul>
*<P> 
*/
public Fxn parse()		//returns an instance of a subclass of Fxn
  
  {
    int i,k;
   ByteArrayOutputStream  b2; DataOutputStream newDataStream;
   ByteClassLoader B; Class Fclass;Fxn f;
   errorposition=0; errormessage=" ";

    //Write the ByteCode to the ByteArrayStream up to the code for vall
   try{
    str.writeInt(0xCAFEBABE);
    str.writeShort(3);    //minor version number
    str.writeShort(45);  //major version number
    str.writeShort(14+nIntFxn*3+1+2*ndblList +1);
      str.write(7);str.writeShort(2);   
             str.write(1);str.writeShort(classname.length());str.writeBytes(classname);  	//this  1,2
      str.write(7);str.writeShort(4);
             str.write(1);str.writeShort(SUPER_CLASS_NAME.length());str.writeBytes(SUPER_CLASS_NAME);			    	//super class 3,4

      str.write(1);str.writeShort(4);str.writeBytes("Code");					//Code 5
      str.write(1);str.writeShort(6);str.writeBytes("<init>");
      str.write(1);str.writeShort(3);str.writeBytes("()V");
      str.write(1);str.writeShort(4);str.writeBytes("vall");
      str.write(1);str.writeShort(6);str.writeBytes("([DI)D");					 //6..9
    
      str.write(10);str.writeShort(3);str.writeShort(11);
       str.write(12); str.writeShort(6);str.writeShort(7);					//super.init() 10,11

       str.write( 7);str.writeShort(13); 
                str.write(1);str.writeShort(14);str.writeBytes("java/lang/Math"); //12,13
       str.write(1);str.writeShort(4);str.writeBytes("(D)D");k=15;

       //Internal Function descriptors

       for(i=0;i<nIntFxn;i++)
         {str.write(10);str.writeShort(12); str.writeShort(16+3*i);
          str.write(12); str.writeShort(17+3*i);if(i>0)str.writeShort(14); else str.writeShort(14+3*nIntFxn+1);
          str.write(1);str.writeShort(IntFxn[i].length());str.writeBytes(IntFxn[i]);k=k+3;
         }
       str.write(1);str.writeShort(5);str.writeBytes("(DD)D");					//signature for pos==^ function												//Intrins fxns
      
       // Enter the constants in the constantpool
       for(i=0;i<ndblList;i++)
          {str.write(6); 
            if(dblList[i].equals("E")) str.writeDouble(java.lang.Math.E);
            else if(dblList[i].equals("PI")) str.writeDouble(java.lang.Math.PI);

            else str.writeDouble(new Double(dblList[i]).doubleValue()); 
            
           }
       

       str.writeShort(33); //access flag
       str.writeShort(1);str.writeShort(3); str.writeShort(0);str.writeShort(0);		//this, super,intfc and fields
       str.writeShort(2); 									//# methods

           str.writeShort(1);str.writeShort(6);str.writeShort(7);  str.writeShort(1); 		//<init> method
           str.writeShort(5);
           str.writeInt(4+4+5+4);						//length of block after this point
           str.writeShort(8);str.writeShort(8);
           str.writeInt(5); 
               str.write(42);str.write(183);str.writeShort(10); str.write(177);//code     
          
           str.writeShort(0);str.writeShort(0);
        

           str.writeShort(1);str.writeShort(8);str.writeShort(9);  str.writeShort(1);		//vall method block 
           str.writeShort(5);
  
		//get code. Put into newDataStream
           b2=new ByteArrayOutputStream();  newDataStream=new DataOutputStream(b2) ;
   	   MaxStack=0;stacksize=0;

           i=parseString(0,newDataStream);
           if(i<0)
              {System.out.println("A");
               return null; 
              }
           if( i>s.length()) {errorposition=i;errormessage="Syntax Error";return null;}

           str.writeInt(4+4+newDataStream.size()+1+4); 
           str.writeShort(MaxStack);str.writeShort(3); 
           str.writeInt(newDataStream.size()+1);
           str.write(b2.toByteArray(),0,newDataStream.size()); 
           str.write(175);  //dreturn code

           str.writeShort(0);str.writeShort(0);
       
       str.writeShort(0);
    }catch(IOException sss)
             {errorposition=0; 
              errormessage="IOException error";
              return null;
              }


   B=new ByteClassLoader(bb.toByteArray(),bb.size(), classname);
     
  // check the class may already be loaded. dup name
 try{
   Fclass=B.loadClass(classname);    

       }
  catch(ClassNotFoundException sss)
         {System.out.println("Class format error");
          errorposition=s.length(); 
          errormessage="Class Format Error";
          return null;} 
  
   if(Fclass==null)  
      {errorposition=s.length()+1;
       errormessage="Cannot Form Class ";
       return null;
       }

   try{ f=(Fxn)Fclass.newInstance();
        f.C=B;
      }
   catch(InstantiationException sss)
       {errorposition=s.length()+1;
        errormessage="Cannot Form Instance ";
        return null;
       }
   catch(IllegalAccessException sss)
       {errorposition=s.length()+1;
        errormessage="Illegal Access "; 
        return null;
       }

  return f;		
   }// End parse()


//parses the string into the appropriate ByteCode

private int parseString(int start,DataOutputStream ss) throws IOException
  {char opstack[]; int nops;
   char c;
   int i,k,m;
   i=0;
   int prevNonspc=-1;
   try{
      opstack=new char[s.length()];
      nops=0;

      for(i=start;i<s.length();)
        {c=s.charAt(i);
         if(debug)System.out.println(stacksize+","+i+","+c+","+prevNonspc);
         int si1 = i+1;
         boolean done = si1 >= s.length();
         if(!done) 
              done = s.charAt(si1)!=' ';
         while( !done )
          {si1++; 
           done = si1>= s.length();
           if(!done) 
               done = s.charAt(si1)!=' ';
           } 
         if(i>start)if( s.charAt(i-1)!=' ') prevNonspc =i-1;  
         if(c==' ') 
            i++;
         else if((c>='0')&&(c<='9'))
            {m=findNumEnd(s,i);
	     i=ProcessNums(s,i,m,ss);
             if(i<0)
               return -1;
             }
         else if(c=='(')
            {i=parseString(i+1,ss);
             if(i<0)return -1; 
              if((i>s.length())||(i<1)) 
                 return seterr(i,"Internal Error");
              if(s.charAt(i-1)!=')') 
                 return seterr(i,"Missing Paren");
              if(!isOpAft(s,i-1)) 
                 return seterr(i,"Illegal Character1");
             }

         else if(c==')')
           {if(i<s.length()-1) 
               if(!isOpAft(s,i)) 
                  return seterr(i+1,"Illegal Character2");

           while(nops > 0)
              nops=PopopStack(opstack,nops,ss); 

           if(stacksize<0)return seterr(i,"Too many operands");
            return i+1;
           }

      else if((c=='-')|| (c=='+'))
         { if(i>=s.length()-1)
                   return seterr(i,"Illegal Character3");
           if( si1 >= s.length())
                 return seterr( i, "Illegal Character"); 

           if(")*/".indexOf(s.charAt(si1))>=0)
                   return seterr(i,"Illegal Character4");

           if((i<=start) || (prevNonspc < 0))
                 {if(c=='-')opstack[nops]='n'; 

                  else opstack[nops]='p';
                  if(debug)System.out.println("XXXXXXXXXXX"+i);
                  nops++;
                   i++;
                  }
           else if("+-*(^".indexOf(s.charAt(prevNonspc))>=0)
                 {if(c=='-')
                     opstack[nops]='n'; 

                  else opstack[nops]='p';
                   if(debug)System.out.println("YYYYYYYYYYYY"+i+","+s.charAt(prevNonspc));
                  nops++;i++;
                 }
           else 
              {while(OktoPop(opstack, nops,c))
                   nops= PopopStack(opstack,nops,ss); 
                
               opstack[nops]=c;

               nops++;
               i++;
               }
         } //c=='-'||c=='+'

      else if("*/^".indexOf(c)>=0)
          {if(i>=s.length()-1) return seterr(i,"Illegal Character5");

           if("*^/)".indexOf(s.charAt(i+1))>=0) return seterr(i+1,"Illegal Character6");

           while(OktoPop(opstack, nops,c))nops=PopopStack(opstack,nops,ss); 
               
           opstack[nops]=c;

           nops++;

           i++;
          }

      else if((c=='x')||(c=='X'))
         {m=findNumEnd(s,i+1); 
          if( m < 0)
            return seterr(i+1,"Variable names must be X0, X1, etc.");

          ss.write(43); ss.write(16);

          ss.write(new Integer(s.substring(i+1,m+1)).intValue());

          ss.write(49);

          stacksize=stacksize+2;

          i=m+1;

          if(!isOpAft(s,m))
             return seterr(m+1,"Illegal Character7");
         }
      
      else if(c=='E')
        { i=ProcessNums(s,i,i,ss);

          if(i<0)
              return seterr( i, "Illegal character");     
        }
      else if(c=='P')
        {if(i>s.length()-2)
             return seterr(i,"Illegal character8");

         if(s.charAt(i+1)!='I')
              return seterr(i+1,"Illegal character9");

          i=ProcessNums(s,i,i+1,ss);

          if(i<0)
             return -1;        
        }
      else
       {m=s.indexOf('(',i);
        
        if(m>i)
          {k=find(s.substring(i,m),IntFxn,nIntFxn);
          
           if(k>=0)
              {i=parseString(m+1,ss); 

               if(i<0)return -1;

               ss.write(184); 
               ss.writeShort(15+k*3); 
              }
            else return seterr(i,"Illegal Function");

           }
         else return seterr(i,"Illegal Function");

        }
      if(stacksize>MaxStack)
             MaxStack=stacksize;

      if(stacksize<0) 
           return seterr(i,"Too many operationsA");

    
        }//end main for
  
   while(nops>0)
     nops=PopopStack(opstack,nops,ss);
                
   if(stacksize!=2)
       return seterr(s.length(),"Too many operationsB");

  } catch(IOException sss)  
       {return seterr(i,"IOException error "+sss);}

   return s.length();
   } // end parseString

int seterr(int pos, String mess)
   {errorposition=pos;

    errormessage=mess;

    return -1;
    }

private int findNumEnd(String s, int start)

  {char c;
   int mode; //0 start, 1 decimal occured 2 E occurred 3 +/- occured after E
   int i;
   boolean Done;

   mode=0;

   if(start<0)
          return -1;

   if(start>s.length())
          return -1;

   Done=false;

   for(i=start;i<s.length();)
     {c=s.charAt(i);

      if((c>='0')&&(c<='9'))
            {if(mode==3)
                   mode=5;
            }
      else if(c=='.') 
          {if(mode>=1)
               return -1;
            mode=1;
          }
      else if((c=='E')||(c=='e'))
          {if(mode>2)
               return -1; 
           mode=3;
          }
      else if((c=='+')||(c=='-'))
          {if(mode==3)
                mode=5; 
           else 
                Done=true;
          }
      else 
           Done=true;

      if(!Done)
         i++; 

      else if(i==start)
         return -1; 

      else 
         return i-1;

      } //End of for loop

    return s.length()-1;

   }// findNumEnd


private byte Op2ByteCode(char c)
  {if(c=='+')
       return 99;

   else if(c=='-')
       return 103;

   else if(c=='*')
        return 107;

   else if(c=='/')
        return 111;

   else if(c=='n')
        return 119;

   else if(c=='p')
        return 0;

   return 0;

   
   }
private char nextNonspace(String s, int start)
 {int i; boolean Done;
  if(start>=s.length())
     return 0;

  if(start<0) return 0;
  i=start; Done=false;
  while((i<s.length())&& (!Done))
    if(s.charAt(i)==' ')i++; else Done=true;

  if(i<s.length()) 
    return s.charAt(i);
  else
    return 0;
  }
private boolean isOpAft(String s, int thisposn)

  {if(thisposn>=s.length()-1)
       return true;

   if(thisposn<0)
       return false;

 
   if(nextNonspace(s,thisposn+1)==0) 
        return true;

   if("+-*/^)".indexOf(nextNonspace(s,thisposn+1))<0)
       return false;

   return true;

   }

private boolean isOpBef(String s,int thisposn)
  {if(thisposn>s.length())
       return false;

   if(thisposn<0)
       return false;

   if(thisposn==0)
       return true;
   
   if("+-*/^(".indexOf(s.charAt(thisposn-1))<0) 
       return false;

   return true;
  }
  
private boolean OktoPop(char opstack[],int nops,char c)
  { boolean res;
     if(nops<=0)
            res= false;

     else if((opstack[nops-1]=='n')||(opstack[nops-1]=='p'))
          { if(c=='^')
               res=false; 

            else 
               res= true;
          }

     else if(c=='+')
         res= true;

     else if(c=='-') 
         res= true;

     else if(opstack[nops-1]=='+')
         res= false;

     else if(opstack[nops-1]=='-')
         res= false;

     else if((c=='*')||(c=='/'))
         res= true;

     else if((opstack[nops-1]=='*')||(opstack[nops-1]=='/'))
         res= false;

     else if((c=='n')||(c=='p')) 
         res= true;

     else 
         res= false; 
 
     return res;     
   }

private int PopopStack(char opstack[],int nops, DataOutputStream ss) throws IOException
  {char c1;
 
   nops--; 
   if(nops<0)
     return nops;

   if(opstack[nops] != '^' )
        ss.write(Op2ByteCode(opstack[nops]));
   else 
        {ss.write(184); ss.writeShort(15);}

   c1=opstack[nops]; 
   if((c1!='n')&&(c1!='p'))
          stacksize=stacksize-2;
   if(debug)System.out.println("   Popstck end "+opstack[nops]+"="+nops+","+stacksize);
   return nops;
  }//PopopStack


private int ProcessNums(String s, int start, int end,DataOutputStream ss) throws IOException
  {  int n; 

     n=find(s.substring(start,end+1),dblList,ndblList);

     ss.write(20);ss.writeShort(1+14+nIntFxn*3+ 1+2*n); //code
     
     stacksize=stacksize+2;

     if(!isOpAft(s,end)) 
         return seterr(end+1,"Illegal character"); 
 
     return  end +1;
  }//ProcessNums


/**
* Returns the position in the string S where an error occurred.<P>
*@return  the position in the string S where an error occurred<P>
*@see #String2Instance1(String S,  String Classname)  
*/
public int geterrorposition()
        {return errorposition;}



/**
*Returns a message corresponding to the Sytax error that occurred.
*@return a message corresponding to the Sytax error that occurred<P>
@see #String2Instance1(String S,  String Classname)  


*/
public String geterrormessage()
       {return errormessage;}

/**
* The main has code to test this class.
*<P> Just run <UL>
*      <B>java String2Instance</b></ul>
* Then enter the equation and x values.
*/
public static void main(String args[])
  {
   C_Input dat;
   String2Instance1 S; 

   String rl;

   int i,c;

    Fxn X;

//    DataInputStream D;

    double xx[];

    xx=new double[3];

//    D= new DataInputStream(System.in);
   
    dat=new C_Input();
   
    rl=new String();

    System.out.print("Enter the rule  :");  
    try{ 
      c=System.in.read(); 

      while(c>=' ')
        {rl=rl+(char)c;

         c=System.in.read();

        }
      }catch(IOException sss)
          {rl="2*x1+3*x2"; }

   

    S=new String2Instance1( rl,"rule");

    X= S.parse();

    if(X==null) 
         {System.out.println("an error occurred at position"+S.geterrorposition());

          return;
         }

    c='0';
    while(c!='4')
      {System.out.println("");
       System.out.println("");

       System.out.println("Enter Option desired");

       System.out.println("   1. set the three x values");

       System.out.println("   2. Evaluate the expression");

       System.out.println("   3. View the expression and x values");

       System.out.println("   4. Exit");

        try{ System.out.println("");

           c=System.in.read(); 

           while(c<=' ')              
              c=System.in.read();

           }catch(IOException sss)
       		{c='0';}

       if(c=='1')
         {System.out.println("");

          System.out.println("Now Enter 3 numeric values");

          for(i=0;i<3;i++)

           try{ 
               xx[i]= dat.readDouble(System.in);
              }
           catch(IOException sss)
                  {xx[i]=0;}
         }
       else if(c=='2')
        { System.out.println("");

          System.out.println("Value is "+X.vall(xx,3));
        }

       else if(c=='3')
        {System.out.println("");

         System.out.println("rule:"+rl);

         System.out.println("x[] :"+xx[0]+", "+xx[1]+", "+xx[2]); 
         }
      }//While

   }//main
 }//class string2Instance
