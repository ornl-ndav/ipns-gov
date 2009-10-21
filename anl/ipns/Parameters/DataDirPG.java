/**
 *  $Log$
 *  Revision 1.3  2006/09/28 23:19:52  rmikk
 *  Replaced all file separators by a "/".  This is assumed by quite a few applications
 *
 *  Revision 1.2  2006/07/10 16:25:04  dennis
 *  Change to new Parameter GUIs in gov.anl.ipns.Parameters
 *
 *  Revision 1.2  2006/07/04 20:21:50  dennis
 *  Fixed minor java doc errors.
 *  Added cvs logging tag, so CVS will record the messages in
 *  the file.
 *
 */

package gov.anl.ipns.Parameters;

import java.io.File;
import javax.swing.JPanel;


/**
 *	A DataDirPG uses a JTextField component and a JButton component that triggers a JFileChooser,
 *	so the user may select a directory path.
 */
public class DataDirPG extends StringPG_base
                       implements IBrowsePG
{
	private FileChooserPanel fcPanel = null;
	private boolean enabled			 = true; 	// we store the enabled state, so the
	                                         	// setEnabled() method can be called
	                                         	// before constructing the widget.
	private String lastDirectory;
	
	/**
	 * Creates a new DataDirPG object with the specified name and initial
	 * value.
	 *
	 * @param  name  The name (i.e. prompt string) for this PG.
	 * @param  value The initial value for this PG.
	 *
	 * @throws IllegalArgumentException is thrown, if the specified value
	 *         cannot be converted to a String value.
	 */
	public DataDirPG(String name, Object value ) throws IllegalArgumentException
	{
		super(name,value);
		lastDirectory = null;
		if( value != null)
	           lastDirectory = value.toString();
		if( lastDirectory == null || lastDirectory.length()< 2 )
			   lastDirectory = System.getProperty( "Data_Directory");
		str_value = lastDirectory;
	}
	
	/**
	 * Enable or disable the FileChooserPanel for selecting directory pathnames.
	 *
	 * @param  bol  Set true to enable the FileChooserPanel.
	 */	
	public void setEnabled(boolean bol)
	{
		enabled = bol;
		
		if ( fcPanel != null )
	    {                                    
			fcPanel.setEnabled( bol );
	    }
	}

	/**
	 * Construct a copy of this DataDirPG object.
	 *
	 * @return A copy of this DataDirPG, with the same name and value.
	 */	
	public Object clone() 
	{
		DataDirPG copy = new DataDirPG( getName(), str_value );
		copy.setValidFlag( getValidFlag() );
		return copy;
	}
	
	/**
	 * Retrieves the JTextField's current String (a filesystem pathname).
	 * 
	 * @return The String value from the JTextField, if possible.
	 *
	 * @throws IllegalArgumentException is thrown if this is called without
	 *         a GUI widget being present, if the value is invalid, or if the
	 *         pathname points to a place in the filesystem that doesn't exist,
	 *         is not the right type, or has insufficient permissions.
	 */
	protected String getWidgetValue() throws IllegalArgumentException
	{
		String widget_value = "";

	    if ( fcPanel.getTextField() == null )
	    {
	    	throw new IllegalArgumentException("getWidgetValue() called " +
	    			"when no DataDirPG widget exists");
	    }
	    else
	    {
	    	widget_value = Conversions.get_String( fcPanel.getTextField().getText() );
	    	widget_value = Append( widget_value );
	    }
	    
	    File file = new File(widget_value);	    
	    if(!file.exists())
	    {
	    	throw new IllegalArgumentException("No such directory");
	    }	    
	    if(!file.isDirectory())
	    {
	    	throw new IllegalArgumentException("Not a directory");
	    }
	    if(!file.canRead())
	    {
	    	throw new IllegalArgumentException("Read permission is denied");
	    }
	    /*if(!file.canWrite())
	    {
	    	throw new IllegalArgumentException("Write permission denied");
	    }*/	    

	    return widget_value;
	}
	
	/**
	 * Sets the value displayed in the JTextField to the specified value.
	 *
	 * @param value  The String value to record in the JTextField.
	 *
	 * @throws IllegalArgumentException is thrown if this is called without
	 *         a GUI widget being present.
	 */
	protected void setWidgetValue( String value ) throws IllegalArgumentException
	{
		if ( fcPanel.getTextField() == null )
		{
			throw new IllegalArgumentException("setWidgetValue() called " +
					"when no DataDirPG widget exists");
		}
		
		fcPanel.getTextField().setText( ""+Append(value) );
	}

	/**necnex
	 * Get a JPanel containing a composite entry widget for getting a value from
	 * the user.  In this case, the panel contains three children, a label 
	 * giving the name (i.e. prompt string), a JTextField holding the pathname
	 * String, and a JButton that triggers a JFileChooser.
	 *
	 * @return a JPanel containing a component to enter or select a pathname String.
	 */
	protected JPanel getWidget()
	{
		if( fcPanel == null )			// make new panel with label, TextField, and button 
	    {
		  
	      fcPanel      = new FileChooserPanel(FileChooserPanel.SET_DIRECTORY,getName(),
	    		  lastDirectory );
	      //ActionListeners
	      fcPanel.getTextField().addActionListener( new PG_ActionListener( this ) );
	      fcPanel.getTextField().addKeyListener( new PG_KeyListener( this ) );
	    }

	    setEnabled( enabled );                 // set widget state from
	    setWidgetValue( str_value );           // current information
	    
	    return fcPanel;
	}
	
	
	/**
	 * Appends the / at the end of the file name
	 * @param dir   directory
	 * @return      directory with a trailiig path separator
	 */
	private String Append( String dir){
		//dir = dir.replace('/', java.io.File.separatorChar);
	   if( dir == null)
	      dir ="";
		dir = dir.replace('\\', '/');
		if( ! dir.endsWith( "/"))
			dir = dir +"/";
		return dir;
	}
	/**
	 * Set the FileChooserPanel to NULL, so that it can be garbage collected.  
	 * Subsequent calls to getPGWidget() will return a new JPanel and entry 
	 * components.
	 */
	protected void destroyWidget()
	{
		fcPanel = null;						// null out all references to gui
											// components, so that they can be
											// garbage collected.
	}
}
