/*
 * File:  Demo.java 
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
 * Revision 1.3  2004/01/22 02:28:09  bouzekc
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
package DataSetTools.functions.FunctionTools;

import java.awt.*;
import java.applet.Applet;
//import java.security.*;

public class Demo extends Applet implements java.awt.event.ActionListener
  {TextField Rule,x[],Res; 
   int mode; 
   Button OK;
   Label lbl,lbl2;
   Fxn F;String2Instance1 S;
   double xx[];
   java.lang.RuntimePermission P;
   //Rule text box becomes x0
   String rule;
  public Demo()
    {x=new TextField[3];
     xx=new double[3];
     // P=new java.lang.RuntimePermission("createClassLoader");
     // this.getClass().getProtectionDomain().getPermissions().add(P);
           //did not have the permission to getProtectionDomain
    }
  public void init()
   {
     mode=0;
    Rule=new TextField(30);
    OK=new Button("OK");
    lbl2=new Label("");
    
    setLayout(new GridLayout(2,2));
   // setLayout(new FlowLayout());
    add(OK); 
    add(lbl2);
    lbl=new Label("Enter Expression");
    add(lbl);
    add(Rule);
    OK.addActionListener(this);
        invalidate();

   }
  public void start()
   {
   }//start
 int checkInputs()
   {int i;
      i=-1;
     try{
     for(i=0;i<3;i++)
      {  if(x[i]==null) return -1;
         if(x[i].getText()==null)return -1;
         if(new Double(x[i].getText()).doubleValue()>0){}
      }
        }
     catch(NumberFormatException sss){ return i;}
     return -1;
    }
  public void actionPerformed(java.awt.event.ActionEvent e)
    {int c,i;
    if(mode==0)
      {mode=1;
      lbl.setText("x0 value");
       rule=Rule.getText();
        S=new String2Instance1(rule,"rule");
       F=S.parse();
       if(F==null) 
          {lbl2.setText("ERROR "+S.geterrormessage()+" at position "+ S.geterrorposition()+", try again");
           mode=0;
           lbl.setText("");
            return;}

       OK.removeActionListener(this);
       removeAll();
       OK=new Button("OK");
       lbl2=new Label("Rule="+rule);
       x[0]=new TextField(30);
       x[1]=new TextField(30);
       x[2]=new TextField(30);
       Res=new TextField(30);
       setLayout(new GridLayout(6,2));
       add(OK); 
       add(lbl2);

      add(new Label("x0 value",Label.RIGHT));
      add(x[0]);

        add(new Label("x1 value",Label.RIGHT));
        add(x[1]);

       add(new Label("x2 value",Label.RIGHT));
       add(x[2]);
       add(new Label("Result",Label.RIGHT));
       add(Res);doLayout();

       invalidate(); repaint();        
       OK.addActionListener(this);
      
       }
      else if(mode==1)
       { c=checkInputs();
         if(c>0) {x[c].setText("Improper format");Res.setText(" ");return;}
         for(i=0;i<3;i++)
           xx[i]=new Double(x[i].getText()).doubleValue();
         Res.setText( new Double(F.vall(xx,3)).toString());


        }
     
    }

public static void main(String args[])
 {Frame F; Demo D;
  F=new Frame("Fxn");  F.setSize(600,400);

  
  D=new Demo();D.setSize( 580,380);D.init();F.add(D);  D.start(); 
D.invalidate(); F.invalidate();
F.show();
  }


   }
