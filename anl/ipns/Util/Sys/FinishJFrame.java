
/*
 * File:  FinishJFrame.java 
 *             
 * Copyright (C) 2004, Ruth Mikkelson
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
 * This work was supported by the National Science Foundation under
 * grant number DMR-0218882
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 *
 * Modified:
 *
 * $Log$
 * Revision 1.2  2007/07/26 17:20:25  rmikk
 * Added the setDefaultCloseOperastion to be Dispose on close
 *
 * Revision 1.1  2004/03/23 14:45:16  rmikk
 * Initial check in.  This is just a JFrame that finalizes itself
 *   after being disposed
 *
 */
package gov.anl.ipns.Util.Sys;
import javax.swing.*;
import java.awt.*;
/**
  *  This class is a JFrame with a public method that calls the protected 
  *  finalize method to clear out some more resources.
  *  The FinishWindowListener is added as a WindowListener to this JFrame so
  *  when the JFrame is disposed of, the finalize method will get invoked.
  */
public class FinishJFrame extends JFrame implements IFinish{
   
   public FinishJFrame()
       throws HeadlessException{
    super();
    init();
   }

  public FinishJFrame(GraphicsConfiguration gc){
     super( gc);
    init();
  }

  public FinishJFrame(String title)
       throws HeadlessException{
     super( title);
     init();

  }
   public FinishJFrame(String title,
              GraphicsConfiguration gc){

    super( title, gc);
    init();
   }

  /** 
    *  Adds the FinishWindowListener that will finalize this window by
    *  invoking the finish method
    */
  private void init(){
     
      addWindowListener( new FinishWindowListener());

      setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE);



  }

  /**
    *  Invokes the protected finalize method.  This should only be invoked 
    *  after the window is disposed of.
    */
  public void finish(){
     try{
      finalize();
     }catch(Throwable ss){
       System.out.println("finalize error="+ss);
     }

  }

}
