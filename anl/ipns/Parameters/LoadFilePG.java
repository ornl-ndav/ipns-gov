/**
 *  $Log$
 *  Revision 1.5  2007/01/12 14:57:55  dennis
 *  Removed unused imports.
 *
 *  Revision 1.4  2006/09/28 23:19:53  rmikk
 *  Replaced all file separators by a "/".  This is assumed by quite a few applications
 *
 *  Revision 1.3  2006/08/08 15:18:47  dennis
 *  Remove the strict checking for file existence.
 *
 *  Revision 1.2  2006/07/10 16:25:05  dennis
 *  Change to new Parameter GUIs in gov.anl.ipns.Parameters
 *
 *  Revision 1.2  2006/07/04 20:21:51  dennis
 *  Fixed minor java doc errors.
 *  Added cvs logging tag, so CVS will record the messages in
 *  the file.
 *
 */

package gov.anl.ipns.Parameters;

//import java.io.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

/**
 *	A LoadFilePG uses a JTextField component and a JButton component that triggers a JFileChooser,
 *	so the user may select a file pathname.
 */
public class LoadFilePG extends StringPG_base
                        implements IBrowsePG
{
	private FileChooserPanel fcPanel = null;
	private boolean enabled		 = true; 	// we store the enabled state, so the
	                                         	// setEnabled() method can be called
	                                         	// before constructing the widget.
	private javax.swing.filechooser.FileFilter fileFilter   = null; // filter for file dialog
	
	/**
	 * Creates a new LoadFilePG object with the specified name and initial
	 * value.
	 *
	 * @param  name  The name (i.e. prompt string) for this PG.
	 * @param  value The initial value for this PG.
	 *
	 * @throws IllegalArgumentException is thrown, if the specified value
	 *         cannot be converted to a String value.
	 */
	public LoadFilePG( String name, Object value ) throws IllegalArgumentException
	{
		super(name,value);		
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
	 * Construct a copy of this LoadFilePG object.
	 *
	 * @return A copy of this LoadFilePG, with the same name and value.
	 */
	public Object clone() 
	{
		LoadFilePG copy = new LoadFilePG( getName(), str_value );
		copy.setValidFlag( getValidFlag() );
		if( fileFilter != null)
		   copy.setFilter( fileFilter );
		return copy;
	}
	
        public void setFilter( FileFilter filter )
        {
           fileFilter = filter;
           if( fcPanel != null)
              fcPanel.setFileFilter( filter );
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
	    			"when no LoadFilePG widget exists");
	    }
	    else
	    {
	    	widget_value = Conversions.get_String( fcPanel.getTextField().getText() );
	    }

/*  Remove the strict checking for file existence.
	    //checking for pathname validity
	    File file = new File(widget_value);
	    if(!file.exists())
	    {
	    	throw new IllegalArgumentException("No such file");
	    }
	    if(!file.isFile())
	    {
	    	throw new IllegalArgumentException("Not a file");
	    }	    
	    if(!file.canRead())
	    {
	    	throw new IllegalArgumentException("Read permission is denied");
	    }	    
       widget_value = widget_value.replace('\\','/');
*/
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
					"when no LoadFilePG widget exists");
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
		if( fcPanel == null )	// make new panel with label, TextField, and button 
	    {
	      fcPanel      = new FileChooserPanel(FileChooserPanel.LOAD_FILE,getName(),str_value);
	      if( fileFilter != null)
	         fcPanel.setFileFilter( fileFilter );
	         
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
		fcPanel = null;		// null out all references to gui
					// components, so that they can be
					// garbage collected.
	}
}
