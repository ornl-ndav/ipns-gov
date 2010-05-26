
/*
 * File:  SaveImageActionListener.java 
 *             
 * Copyright (C) 2003, Ruth Mikkelson
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
 *
 * Modified:
 *
 * $Log$
 * Revision 1.8  2007/07/16 18:26:40  rmikk
 * Eliminated some mor  subtle conventions that forced saving jpeg files
 *
 * Revision 1.7  2007/07/11 18:46:23  rmikk
 * Images can now be saved with any extension that is allowed by the underlying
 * system
 *
 * Revision 1.6  2007/04/27 13:00:44  rmikk
 * Fixed javadoc error
 *
 * Revision 1.5  2007/03/12 19:19:50  rmikk
 * Added a static method to save an Image of an IViewComponent. It uses the
 *   swing invoke later to make sure all the drawing is done in time.
 *
 * Revision 1.4  2004/03/12 17:54:28  rmikk
 * Fixed package names.
 * Fixed JMenuBar "File" Jmenu search algorithm
 *
 * Revision 1.3  2004/03/12 17:21:13  hammonds
 * Moved from DataSetTools.viewer to gov.anl.ipns.Util.Sys
 *
 * Revision 1.2  2004/03/10 21:06:32  millermi
 * - Added JPEGFileFilter class to automatically save
 *   images as JPEGs.
 * - Added static method getActiveMenuItem() which returns
 *   a menu item with a listener that will cause the image
 *   to be saved as a JPEG.
 *
 * Revision 1.1  2003/11/25 20:08:54  rmikk
 * Initial Checkin for a class that can be added to the File
 *   menu to viewers to save the component as an image
 *
 */
package gov.anl.ipns.Util.Sys;

import gov.anl.ipns.Util.File.*;
import java.awt.event.*;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.image.*;
import java.awt.*;
import java.io.*;
import gov.anl.ipns.ViewTools.Components.*;
import gov.anl.ipns.Util.SpecialStrings.*;
import gov.anl.ipns.ViewTools.Components.TwoD.*;

public class SaveImageActionListener implements ActionListener{

 private Component comp;
 static String err = null;
 
 public SaveImageActionListener(  Component comp)
 {  
   this.comp = comp;
 }
 
 public SaveImageActionListener( IViewComponent viewComp){
    this((Component)(viewComp.getDisplayPanel()));
 }
 public static void setUpMenuItem(JMenuBar jmb, Component comp )
 {
   if( jmb == null)
     return;
    
   for( int i=0; i< jmb.getMenuCount();i++){
      JMenu jm = jmb.getMenu(i);
      if( jm.getText().equals("File")){
         setUpMenuItem( jm, comp);
         return;
        }
      }
    JMenu jm = new JMenu("File");
    jmb.add(jm);
    setUpMenuItem( jm, comp);
   
 }

  /**
   * This class sets up the menu bar on a component with the "print" menu item
   * 
   * @param jm   the JMenu to add the new menu item to
   * 
   * @param comp  The component whose image is to be saved to a file.
   * 
   */
  public static void setUpMenuItem(JMenu jm, Component comp )
    {
     JMenuItem jmi= new JMenuItem("Save Image");
     
     int nitems= jm.getItemCount();
     if( nitems < 0) 
        nitems= 0;
     
     jm.add(jmi, nitems );
     jmi.addActionListener(new SaveImageActionListener( comp));

    }
   
  /**
   * This method will return a JMenuItem with the provided text name. The
   * menu item will have a save listener added to it. If clicked on,
   * this menu item will save as an image the component passed in.
   *
   *  @param  menu_text Display text on the JMenuItem
   *  
   *  @param  comp Component whose image is to be saved.
   *  
   *  @return menu item with listener to initiate image saving routine.
   */
   public static JMenuItem getActiveMenuItem( String menu_text, Component comp )
   {
     JMenuItem jmi = new JMenuItem(menu_text);
     jmi.addActionListener( new SaveImageActionListener(comp) );
     return jmi;
   }

   
   public void actionPerformed( ActionEvent evt)
   {
     JFileChooser jfc= new JFileChooser();
     
     if( jfc.showSaveDialog(null) != JFileChooser.APPROVE_OPTION)
       return;
     
     File file = jfc.getSelectedFile();
     
     // make sure file was selected
     if( file == null)
       return;
     
     // add/change extensio of filename to .jpg, if it is not a supported 
     //extension
     String filename = file.toString();
     if( filename == null )
        return;
     
     String extension = "";
     
     int k= filename.lastIndexOf(".");
     if( k >0)
        extension =  filename.substring( k+1).toLowerCase();
     else
        k= filename.length();
     
     String[] extensionList = ImageIO.getWriterFileSuffixes( );
     if( extensionList == null )
     {
        JOptionPane.showMessageDialog( null,
              "No File Extensions Supported." );
        return;
     }
     //Check if it is a supported extension.
     boolean found = false;
     for( int i=0; i< extensionList.length && !found ; i++)
        if( extensionList[i].equals( extension ))
           found = true;
     
     if( !found)
     {
        filename = filename.substring( 0,k )+".jpg";
        extension ="jpg";
        JOptionPane.showMessageDialog( null ,
              "Improper Extension. Saving as JPEG to "+filename );
     }
     
     
     //String filename = new JPEGFileFilter().appendExtension(file.toString());
     Rectangle R = comp.getBounds();
     BufferedImage bimg= new BufferedImage(R.width, R.height,
                                           BufferedImage.TYPE_INT_RGB ); 
     
     Graphics2D gr = bimg.createGraphics();
  
     comp.paint( gr);

     try
     {
       FileOutputStream fout = new FileOutputStream( new File(filename) );
       if( !javax.imageio.ImageIO.write( bimg, extension,
        			  (OutputStream)fout ) )
       {
         SharedMessages.addmsg( " no appropriate writer is found for extension "
                           + extension );
         fout.close();
         return; 
       }
       
       fout.close();
     }
     catch( Exception ss){
       SharedMessages.addmsg( "Image Save Error:"+ss.toString());
     }
   }
   
   
   /**
    * Saves an IViewComponent to a file as an  image currently supported by 
    *     Java's ImageIO system.
    *     
    * @param Comp  The IViewComponent whose Display Panel is to be saved. NOTE that 
    *                the view component does not have to be displayable.
    *                
    * @param filename  The filename for the saved image. It will be forced to end
    *                  in .jpg if that is not the case
    *                  
    * @param width    the width in pixels of image or -1 to use default. 
    * 
    * @param height   the height in pixels of the image or -1 for the default.
    * 
    * 
    * 
    * @return null or an ErrorString if an error occurred.
    */
   public static Object SaveImage( IViewComponent Comp, String filename, int width, int height){
      
      if( Comp == null) 
         return new ErrorString("View Component is null in SaveImage");
      
      if( filename == null) 
         return new ErrorString("null filename in SaveImage");
      
      if( filename.length() <1)
         return new ErrorString("empty filename in SaveImage");
      
      int k=filename.lastIndexOf(".");
      
         if( k < 0) 
            filename = filename +".jpg";
         
         
      Component comp = Comp.getDisplayPanel();
      
      JWindow jf1=null;
      
      if( !comp.isDisplayable()){
         jf1 = new JWindow();
         jf1.setPreferredSize( new Dimension(width+7, height+25));   
         jf1.getContentPane().setLayout( new GridLayout(1,1));
         jf1.getContentPane(). add( comp);
         jf1.pack();
         jf1.validate(); 
         
      }
      Object[] in_outData = new Object[1];
      
     
      SwingUtilities.invokeLater( 
         new RunPaint(comp, in_outData));
    
      
      SwingUtilities.invokeLater( new RunSvImage(in_outData,filename));
     
      
      return null;
   }
 
 /*
  * File filter for .jpg files, file format for images.
  */ 
  class JPEGFileFilter extends RobustFileFilter
  {
   /**
    * 
    */
   private static final long serialVersionUID = 1L;

   /*
    *  Default constructor.  Calls the super constructor,
    *  sets the description, and sets the file extensions.
    */
    public JPEGFileFilter()
    {
      super();
      super.setDescription("JPEG (*.jpg)");
      super.addExtension(".jpg");
    } 
  }
 
  /**
   * Test for the SaveImage static method
   * @param args  not used
   */
 public static void main( String args[]){
   float[][] F =new float[200][300];
   for( int i=0; i<200;i++)
      java.util.Arrays.fill( F[i], .1f*i);
   ImageViewComponent img = new ImageViewComponent(
               new VirtualArray2D( F));  
   
   SaveImageActionListener.SaveImage(img,"C:/xxx.jpg",1200,1300);
   System.out.println("Done");
   System.exit(0);
 }
  
}


class RunPaint implements Runnable{
  Component comp;
  BufferedImage bimg;
  Object[] in_outData;
  public RunPaint( Component comp, Object[] in_outData){
     this.comp = comp;
     bimg= null;
     this.in_outData = in_outData;
  }
  public void run(){

     Rectangle R = comp.getBounds();
     bimg= new BufferedImage(R.width, R.height,
              BufferedImage.TYPE_INT_RGB ); 
     in_outData[0]= bimg;
   
     Graphics2D gr = bimg.createGraphics();

     comp.paint( gr);
     
 }
}



class RunSvImage implements Runnable{
   String filename;
   BufferedImage bimg;
   Object[] in_outData;
   public RunSvImage( Object[] in_outData, String filename){
      this.filename = filename;
      this.bimg = (BufferedImage)in_outData[0];
      this.in_outData = in_outData;
   }
   
   public void run(){
      this.bimg = (BufferedImage)in_outData[0];
      SaveImageActionListener.err=null;
      if( filename == null)
         return;
      
      int k= filename.lastIndexOf( ".");
      String extension = "jpg";
      if( k >0 )
         extension = filename.substring( k+1 );
         
      try{
         
         FileOutputStream fout = new FileOutputStream( new File(filename) );
         if( !javax.imageio.ImageIO.write( bimg, extension,
                     (OutputStream)fout ) )
         {
           SharedMessages.addmsg( " no appropriate writer is found for "+
                    extension);
           SaveImageActionListener .err =" no appropriate writer is found for"+
                    extension;
           fout.close();
           return ;
         }
         fout.close();
         
       }
       catch( Exception ss){
         SharedMessages.addmsg( "Image Save Error:"+ss.toString());
         SaveImageActionListener .err="Image Save Error:"+ss.toString();
         return; 
       }
       
     
   }
}

