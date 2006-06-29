package gov.anl.ipns.Parameters;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *	This class is the panel of visual components, designed for DataDirPG, LoadFilePG, and
 *	SaveFilePG.  It contains a a label, text field, and a button that pops up a JFileChooser.
 */
public class FileChooserPanel extends JPanel
{	
	/**
	 * "Set Directory" - This constant is the mode for the FileChooser to select 
	 * 					 directories.
	 */
	public static final int SET_DIRECTORY	= 0;
	
	/**
	 * "Load File" - This constant is the mode for the FileChooser to select
	 * 				 a file to load.
	 */
	public static final int LOAD_FILE		= 1;
	
	/**
	 * "Save File" - This constant is the mode for the FileChooser to list 
	 * 				 a new file or select a file to overwrite.
	 */
	public static final int SAVE_FILE		= 2;
	
	/**
	 * "Generic Select" - This constant is the mode for the FileChooser to 
	 * 					  generically select a file or directory.
	 */
	public static final int GENERIC_SELECT	= 3;
	
	
	private FileChooserPanel fcp = this;		//This panel
	
	private JLabel			label;				//The label on the FileChooserPanel
	private JTextField		text_field;			//The text field that holds directory paths
	private JButton			button;				//The button that pops up the JFileChooser
	private JFileChooser	fileChooser;
	
	private int 			chooseMode;			//The mode in which the FileChooser will operate
	
	/**
	 * Creates a new FileChooserPanel with a label, text field, and a button 
	 * that pops up a JFileChooser.
	 * 
	 * @param chooseMode - The mode for the File Chooser.  This specifies what
	 * 					   what it can select. 
	 * 					 	- FileChooserPanel.SET_DIRECTORY
	 * 					 	- FileChooserPanel.LOAD_FILE
	 * 					 	- FileChooserPanel.SAVE_FILE
	 * 											   
	 * @param lbl - The label that is displayed on the pane.  If empty or null,
	 *				its spot will not be included in the layout for the pane.
	 */
	public FileChooserPanel(int chooseMode, String lbl)
	{
		label 		= new JLabel(lbl);
		text_field 	= new JTextField();
		button 		= new JButton("Browse");
		
		button.addActionListener(new ControlListener());
		
		fileChooser = new JFileChooser();
		
		this.chooseMode = chooseMode;				
		
		//if the label String is empty, don't make 
		//room for it in the layout
		if(lbl == null || lbl.equals(""))
		{
			fcp.setLayout(new GridLayout(1,1));
		}
		else
		{
			fcp.setLayout(new GridLayout(1,2));
			fcp.add(label);
		}
		
		JPanel pnl = new JPanel();
		pnl.setLayout(new BorderLayout());
		pnl.add(text_field,BorderLayout.CENTER);
		pnl.add(button,BorderLayout.LINE_END);
		
		fcp.add(pnl);		
	}
	
	/**
	 * @param chooseMode - The mode for the File Chooser.  This specifies what
	 * 					   what it can select. 
	 * 					 	- FileChooserPanel.SET_DIRECTORY
	 * 					 	- FileChooserPanel.LOAD_FILE
	 * 					 	- FileChooserPanel.SAVE_FILE
	 */
	public FileChooserPanel(int chooseMode)
	{
		this(chooseMode,"");
	}
	
	/**
	 * @param lbl - The label that is displayed on the pane.  If empty or null,
	 *				its spot will not be included in the layout for the pane.
	 */
	public FileChooserPanel(String lbl)
	{	
		this(FileChooserPanel.GENERIC_SELECT,lbl);
	}
	
	/**
	 *
	 */
	public FileChooserPanel()
	{
		this(FileChooserPanel.GENERIC_SELECT,"");
	}
	
	/**
	 * This is the file-choosing dialog box that is displayed, when the browse button
	 * is pressed.
	 * 
	 * @return fileChooser - The JFileChooser object. 
	 */
	public JFileChooser getFileChooser()
	{
		return fileChooser;
	}
	
	/**
	 * Gets the JTextField text_field object on the panel
	 * 
	 * @return text_field - The text field that stores the pathname.
	 */
	public JTextField getTextField()
	{
		return text_field;
	}
	
	/**
	 * Sets the visible components on the panel as enabled or disabled.
	 * 
	 * @param bol - This boolean determines whether the objects will be enabled
	 * 				or disabled.
	 */
	public void setEnabled(boolean bol)
	{
		label.setEnabled(bol);
		text_field.setEnabled(bol);
		button.setEnabled(bol);
	}	

	private class ControlListener implements ActionListener
	{
		public void actionPerformed( ActionEvent ae ) throws IllegalArgumentException
		{	
			int returnInt;
			
			//opens the FileChooser dialog window, according to the set mode
			if(chooseMode == SET_DIRECTORY)	 
			{
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				returnInt = fileChooser.showDialog(fcp,"Select");
			}
			else if(chooseMode == LOAD_FILE)
			{
				returnInt = fileChooser.showDialog(fcp,"Load");
			}
			else if(chooseMode == SAVE_FILE)
			{
				returnInt = fileChooser.showDialog(fcp,"Save");
			}
			else	
			{	
				//if no recognizable mode, show a generic FileChooser
				fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				returnInt = fileChooser.showDialog(fcp,"Select");
			}
			
			//takes the selected file/directory, and sets it to the text field
			if (returnInt == JFileChooser.APPROVE_OPTION) 
            {                
                text_field.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }			
		}
	}
	
	
	public static void main(String[] args) 
	{
		JFrame jf = new JFrame("File Chooser Demo");		
		FileChooserPanel fcp = new FileChooserPanel(
				FileChooserPanel.SET_DIRECTORY,"FileChooser");
		
		jf.getContentPane().add(fcp);
		jf.setSize(500,50);
		jf.setVisible(true);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	

}