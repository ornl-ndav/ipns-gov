/* 
 * File: Browser.java
 *  
 * Copyright (C) 2005  Dominic Kramer and Galina Pozharsky
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
 *            Galina Pozharsky<pozharskyg@uwstout.edu>
 *            MSCS Department
 *            HH237H
 *            Menomonie, WI. 54751
 *            (715)-232-2291
 *
 * This work was supported by the National Science Foundation under grant
 * number DMR-0426797, and by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 *
 * Modified:
 * $Log$
 * Revision 1.5  2006/07/19 18:07:14  dennis
 * Removed unused imports.
 *
 * Revision 1.4  2006/03/15 21:15:32  rmikk
 * Added the cvs log tag to the GPL documentation
 *
 */

package gov.anl.ipns.Util.Sys;

import java.net.*;
import java.awt.*;
import javax.swing.text.html.*;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.print.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import java.io.IOException;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JToolBar;


public class Browser implements HyperlinkListener, 
                                ActionListener {
   // String source = "http://www.uwstout.edu";
    final String GO = "Go";
    final String Back = "Back";
	 final String Forward = "Forward";
    final String Home = "Home";
    final String Print = "Print";
    String StartString = null;
    int positionOfPage = -1;
    JEditorPane ep = new JEditorPane(); //a JEditorPane allows display of HTML & RTF
    JToolBar tb = new JToolBar(); //a JToolBar sits above the JEditorPane & contains 
    JTextField tf = new JTextField(40); // the JTextField & go button
    JLabel address = new JLabel(" Address: ");
    JButton back = new JButton(Back);
	 JButton forward = new JButton(Forward);
    JButton go = new JButton(GO);
    JButton home = new JButton(Home);
    JButton print = new JButton(Print);
    BorderLayout bl = new BorderLayout();
    JPanel panel = new JPanel(bl);
    JFrame frame = new JFrame("ISAW browser");
    protected Vector history = null;
    private String fileName;


    public Browser(String file){
        fileName= file;
        openURL(file);
        history = new Vector();
        history.add(file);
        positionOfPage =0;
        back.setEnabled(false);
		  forward.setEnabled(false);
        ep.setEditable(false); //this makes the HTML viewable only in teh JEditorPane, ep
        ep.addHyperlinkListener(this); //this adds a listener for clicking on links

        JScrollPane scroll = new JScrollPane(ep); //this puts the ep inside a scroll pane

        panel.add(scroll, BorderLayout.CENTER); //adds the scroll pane to center of panel

        tf.setActionCommand(Back);
		  tf.setActionCommand( Forward );
        tf.setActionCommand( Home );
        //tf.setActionCommand(Print);
       // print.addActionListener(new gov.anl.ipns.Util.Sys.PrintComponentActionListener(ep));
		  tf.setActionCommand( GO ); //gives the ActionListener on tf a name for its ActionEvent

        tf.setEditable( true );
        tf.addActionListener( this ); //adds an ActionListener to the JTextField (so user can

        go.addActionListener( this ); //use "Enter Key")
        back.addActionListener( this ); //use "Enter Key")
        home.addActionListener( this );
        forward.addActionListener( this );
        tb.add(back); //this adds the back button to the JToolBar
        tb.add(forward); //this adds the forward button to the JToolBar
        tb.add(home); //this adds the home button to the JToolBar
        tb.add(print); //this adds the print button to the JToolBar
        tb.add(address); //this adds the Label "Address:" to the JToolBar
        tb.add(tf); //this adds the JTextField to the JToolBar
        tb.add(go); //this adds the go button to the JToolBar

        panel.add(tb, BorderLayout.NORTH); //adds the JToolBar to the top (North) of panel
        frame.setContentPane(panel);
        Dimension ScreenSize = frame.getToolkit().getScreenSize();
        frame.setSize((int)(.5*ScreenSize.width), (int)(.8*ScreenSize.height));
        frame.setVisible(true);
    }// end Browser()

    private void getThePage(String location) {
        try {
            ep.setPage(location);
            tf.setText(location);
        } catch (IOException ioException) {
            System.out.println("Error retrieving specified URL");
	 
        }//end catch
    }//end method getthePage

    public void openURL(String urlString) {
        String start = urlString.substring(0, 4);

        if ((!start.equals("http")) && (!start.equals("file"))) //adds "http://" to the URL if needed
        {
            urlString = "http://" + urlString;
        }//end if
        try {
            URL url = new URL(urlString);

            ep.setPage(url); //this sets the ep page to the URL page
            tf.setText(urlString); //this sets the JTextField, tf, to the URL
        } catch (Exception e) {
            String message = urlString;
            if( message == null)
               message="(null)";
            else if( message.length() >30)
               message= message.substring(0,30);
            System.out.println("Can't open " + message + " " + e);
            if( StartString == null)
               StartString = urlString.substring( 7 );
           ep.setContentType( "text/html" );
           ep.setText( StartString );
           
        }//end try-catch
        if( StartString == null)
           StartString ="";
    }// end openURL

    public void hyperlinkUpdate(HyperlinkEvent e) {
        URL url = null;
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            JEditorPane pane = (JEditorPane) e.getSource();
            if (e instanceof HTMLFrameHyperlinkEvent) {
                HTMLFrameHyperlinkEvent evt = (HTMLFrameHyperlinkEvent) e;
                HTMLDocument doc = (HTMLDocument) pane.getDocument();
                doc.processHTMLFrameHyperlinkEvent(evt);
                url=doc.getBase();
            } else {
                try {
                    url=e.getURL();
                    pane.setPage( url );
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
       if(url==null)
             return;
        if (history == null)
        {
           history = new Vector();
           positionOfPage =0;
        }else
        {
           positionOfPage++;
           positionOfPage = Math.max( 0 , positionOfPage );
           for( int i= history.size()-1; i>=positionOfPage; i--)
              history.remove(i);
           
        }
        
        history.add(url.toExternalForm());
        positionOfPage= history.size()-1;
        if (history.size() > 1)
            back.setEnabled(true);
    }
    
    public void hyperlinkUpdate1(HyperlinkEvent he) //this allows linking
    {

        HyperlinkEvent.EventType type = he.getEventType();

        if (type == HyperlinkEvent.EventType.ACTIVATED) {
            openURL(he.getURL().toExternalForm());
            if (history == null) history = new Vector();
            history.add(he.getURL().toExternalForm());
            positionOfPage= history.size()-1;
            if (history.size() > 1)
                back.setEnabled(true);

        }
    }//end hyperlinkUpdate()

    public void actionPerformed(ActionEvent ae) //for the GO and BACK buttons
    {
        String command = ae.getActionCommand();

        if (command.equals(GO)) {
            openURL(tf.getText());
            history.add(tf.getText());
            positionOfPage= history.size()-1;
            forward.setEnabled( false );
        }

        if (command.equals(Home)) {
            openURL( fileName );
            history.add( fileName );
            positionOfPage= history.size()-1;
            forward.setEnabled( false ); 
            
        }
	   if (command.equals(Back)) {
            try {
               
                String lastURL = (String) history.elementAt(positionOfPage-1);
                //history.removeElement(lastURL);
                //lastURL = (String) history.lastElement();
                //ep.setPage(lastURL);
					 //tf.setText(lastURL);
					 openURL(lastURL);
                positionOfPage--;
                if (positionOfPage <=0)
                    back.setEnabled(false);
                if( positionOfPage+1 >= history.size())
                  forward.setEnabled( false);
                else
                   forward.setEnabled(true);
            } catch (Exception e) {
                System.out.println("ERROR: Trouble fetching URL" + e);
            }
        }
     if (command.equals(Forward)) {
	      positionOfPage++;
          
          try{
            String thisURL = (String)history.elementAt( positionOfPage);
            ep.setPage(thisURL);
            tf.setText(thisURL);
            if( positionOfPage+1 >= history.size())
                 forward.setEnabled( false);
            if( positionOfPage>0)
              back.setEnabled(true);
          }catch(Exception ss){
              ss.printStackTrace();
          }   
       }	  

        if (command.equals(Print)) {
            PrinterJob printJob = PrinterJob.getPrinterJob();

            printJob.setPrintable(new Printable () {
                    public int print(Graphics g, PageFormat pf, int pageIndex) {
                        if (pageIndex > 0) return Printable.NO_SUCH_PAGE;
                        Graphics2D g2d = (Graphics2D) g;

                        g2d.translate(pf.getImageableX(), pf.getImageableY());
                     
                        return Printable.PAGE_EXISTS;
                    }
                }
            );
            
            if (printJob.printDialog())
                try { 
                    printJob.print();
                } catch (PrinterException pe) {
                    System.out.println("Error printing: " + pe);
                }
        }
        
     
    }//end actionPerformed()

    public static void main(String[] args) {
      
        Browser b = new Browser(("file:///c:\\ISAW\\IsawHelp\\Command\\CommandPane.html"));    
    }

}// end Browser class
