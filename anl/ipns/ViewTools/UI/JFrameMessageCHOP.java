/*  JFrameMessage CHOP   Aug. 2000, Dongfeng Chen
*/
/*
 * $Log$
 * Revision 1.2  2000/11/07 16:07:44  dennis
 * Minor fix in documentation and added $Log$ for CVS logging.
 *
 *
 */

package DataSetTools.components.ui;
import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.text.DateFormat;// for now
import java.util.*; // for now

import javax.swing.*;

/**
 * A basic JFC based application.
 */
public class JFrameMessageCHOP extends javax.swing.JFrame
{
	public JFrameMessageCHOP()
	{
		// This code is automatically generated by Visual Cafe when you add
		// components to the visual environment. It instantiates and initializes
		// the components. To modify the code, only use code syntax that matches
		// what Visual Cafe can generate, or Visual Cafe may be unable to back
		// parse your Java file into its visual environment.
		//{{INIT_CONTROLS
		setTitle("JFC Application");
		setDefaultCloseOperation(javax.swing.JFrame.DO_NOTHING_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout(0,0));
		getContentPane().setBackground(new java.awt.Color(204,204,204));
		setSize(680,680);
		setVisible(false);
		textAreaMessage.setText("Message!");
		getContentPane().add("Center", textAreaMessage);
		textAreaMessage.setBounds(0,0,680,680);
		//}}

		//{{INIT_MENUS
		//}}

		//{{REGISTER_LISTENERS
		SymWindow aSymWindow = new SymWindow();
		this.addWindowListener(aSymWindow);
		SymAction lSymAction = new SymAction();
		//}}
	}

    /**
     * Creates a new instance with the given title.
     * @param sTitle the title for the new frame.
     */
	public JFrameMessageCHOP(String sTitle)
	{
		this();
		setTitle(sTitle);
	}

	/**
	 * The entry point for this application.
	 * Sets the Look and Feel to the System Look and Feel.
	 * Creates a new JFrame1 and makes it visible.
	 */
	static public void main(String args[])
	{
		try {
		    // Add the following code if you want the Look and Feel
		    // to be set to the Look and Feel of the native system.
		    /*
		    try {
		        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		    }
		    catch (Exception e) {
		    }
		    */

			//Create a new instance of our application's frame, and make it visible.
			(new JFrameMessageCHOP()).setVisible(true);
		}
		catch (Throwable t) {
			t.printStackTrace();
			//Ensure the application exits with an error condition.
			System.exit(1);
		}
	}

    /**
     * Notifies this component that it has been added to a container
     * This method should be called by <code>Container.add</code>, and
     * not by user code directly.
     * Overridden here to adjust the size of the frame if needed.
     * @see java.awt.Container#removeNotify
     */
	public void addNotify()
	{
		// Record the size of the window prior to calling parents addNotify.
		Dimension size = getSize();

		super.addNotify();

		if (frameSizeAdjusted)
			return;
		frameSizeAdjusted = true;

		// Adjust size of frame according to the insets and menu bar
		/*
		javax.swing.JMenuBar menuBar = getRootPane().getJMenuBar();
		int menuBarHeight = 0;
		if (menuBar != null)
		    menuBarHeight = menuBar.getPreferredSize().height;
		Insets insets = getInsets();
		setSize(insets.left + insets.right + size.width, insets.top + insets.bottom + size.height + menuBarHeight);
		//*/

	}

	// Used by addNotify
	boolean frameSizeAdjusted = false;

	//{{DECLARE_CONTROLS
	java.awt.TextArea textAreaMessage = new java.awt.TextArea();
	//}}

	//{{DECLARE_MENUS
	//}}

	void exitApplication()
	{
		try {
	    	// Beep
	    	Toolkit.getDefaultToolkit().beep();
	    	// Show a confirmation dialog
	    	int reply = JOptionPane.showConfirmDialog(this,
	    	                                          "Do you really want to exit?",
	    	                                          "JFC Application - Exit" ,
	    	                                          JOptionPane.YES_NO_OPTION,
	    	                                          JOptionPane.QUESTION_MESSAGE);
			// If the confirmation was affirmative, handle exiting.
			if (reply == JOptionPane.YES_OPTION)
			{


		    	this.setVisible(false);    // hide the Frame
		    	this.dispose();            // free the system resources
		    	//System.exit(0);            // close the application
			}
		} catch (Exception e) {
		}
	}

	class SymWindow extends java.awt.event.WindowAdapter
	{
		public void windowClosing(java.awt.event.WindowEvent event)
		{
			Object object = event.getSource();
			if (object == JFrameMessageCHOP.this)
				JFrameMessageCHOP_windowClosing(event);
		}
	}

	void JFrameMessageCHOP_windowClosing(java.awt.event.WindowEvent event)
	{
		// to do: code goes here.

		JFrameMessageCHOP_windowClosing_Interaction1(event);
	}

	void JFrameMessageCHOP_windowClosing_Interaction1(java.awt.event.WindowEvent event) {

		try {
			this.exitApplication();
		} catch (Exception e) {
		}
	}

	class SymAction implements java.awt.event.ActionListener
	{
		public void actionPerformed(java.awt.event.ActionEvent event)
		{
			Object object = event.getSource();


		}
	}

	public JFrameMessageCHOP(String[] title, float[][] arrayAll, String[] info)
	{
	    this();


        DecimalFormat df=new DecimalFormat("###0.00");

		String S=" There are "+title.length+" Runs!   Time now: "+Now()+"\n\n";

	S+="N   Runfile    E(meV)   Area1(cnts) Area2(cnts) Cent1(mmS)Err    Cent2(mmS) Err     Wavelength 1/V   Cycles  Energy  StartDate UserName RunTitle \n";
      //0   HRCS2444   119.78   426729.00   305753.00   2721.79   12.00   3965.53   19.00   0.83   208.91
        //        info[i]=rr.NumOfCyclesCompleted()+" "+rr.EnergyIn()+"\t"+rr.StartDate()+" "+rr.UserName()+" "+rr.RunTitle();    // System.out.println("\n"+r.ElapsedMonitorCounts()+" "+/*+r.NumOfCyclesCompleted*/" "+r.NumOfPulses());

    for(int j=0; j<arrayAll.length;j++)
    {
        System.out.println("\n\n"+title[j]+" ");
		S+="\n"+(j+1)+"   "+title[j]+"   ";


        for(int i=0; i<arrayAll[j].length; i++)
        {
            System.out.print(arrayAll[j][i]+ " ");
            S+=df.format(arrayAll[j][i])+ "   ";
        }
        System.out.println(" ");

        System.out.println("\n"+info[j]+" ");
		S+=info[j]+"\n";

    }
	 textAreaMessage.setText(S);


	}

	public JFrameMessageCHOP(String title, String info)
	{
	    this();
		String S="\n************************************************************************\n"+
		          "   "+title+
		         "\n************************************************************************\nTime now: "+Now()+
		          "\n\n"+info;
	    textAreaMessage.setText(S);
	}

	public JFrameMessageCHOP(String sTitle, String title, String info)
	{
	    this();
		setTitle(sTitle);
	    
		String S="\n***************************************************************************\n"+
		          "   "+title+
		         "\n***************************************************************************\nTime now: "+Now()+
		          "\n\n"+info;
	    textAreaMessage.setText(S);
	}


    public  static  String Now()
   {

            DateFormat plain=DateFormat.getInstance();
            plain.setTimeZone(TimeZone.getDefault());
            String now=plain.format(new Date());


   return now;
   }
}
