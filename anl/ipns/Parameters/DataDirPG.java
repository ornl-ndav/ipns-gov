package gov.anl.ipns.Parameters;

import java.io.File;
import javax.swing.JPanel;


/**
 *	A DataDirPG uses a JTextField component and a JButton component that triggers a JFileChooser,
 *	so the user may select a directory path.
 */
public class DataDirPG extends StringPG_base
{
	private FileChooserPanel fcPanel = null;
	private boolean enabled			 = true; 	// we store the enabled state, so the
	                                         	// setEnabled() method can be called
	                                         	// before constructing the widget.
	
	/**
	 * Creates a new DataDirPG object with the specified name and initial
	 * value.
	 *
	 * @param  name  The name (i.e. prompt string) for this PG.
	 * @param  val   The initial value for this PG.
	 *
	 * @throws IllegalArgumentException is thrown, if the specified value
	 *         cannot be converted to a String value.
	 */
	public DataDirPG(String name, Object value ) throws IllegalArgumentException
	{
		super(name,value);
	}
	
	/**
	 * Enable or disable the FileChooserPanel for selecting directory pathnames.
	 *
	 * @param  on_off  Set true to enable the FileChooserPanel.
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
	public Object getCopy() 
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
		
		fcPanel.getTextField().setText( ""+value );
	}

	/**
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
	      fcPanel      = new FileChooserPanel(FileChooserPanel.SET_DIRECTORY,getName());
	 
	      //ActionListeners
	      fcPanel.getTextField().addActionListener( new PG_ActionListener( this ) );
	      fcPanel.getTextField().addKeyListener( new PG_KeyListener( this ) );
	    }

	    setEnabled( enabled );                 // set widget state from
	    setWidgetValue( str_value );           // current information
	    
	    return fcPanel;
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
