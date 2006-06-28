package gov.anl.ipns.Parameters;


import java.awt.GridLayout;
import java.util.Vector;
import javax.swing.*;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import java.awt.*;


/**
 * This class is used to enter start, end, and number of Q values for a
 * sublist.  The constant dQ or dQ/Q choice is also supported. 
 */
public class Qbins1PG extends VectorPG_base implements Concatenator{

	private JTextField start;
	private JTextField end;
	private JTextField steps;
	private JRadioButton dQ, dQQ;
	private float startv= Float.NaN, 
	              endv = Float.NaN;      
	private  int   stepsv = -1;
	private  boolean dQ_v = true;
	JPanel entryWidget = null;
	
	/**
	 * Constructor
	 * @param name   The prompt to get input from the users
	 * @param val    A Vector with 4 entries:start,stop,nsteps, const dt or dt/t
	 * @throws IllegalArgumentException
	 */
	public Qbins1PG( String name, Object val) throws IllegalArgumentException {
		super( name,  val);
		Vector vall = Conversions.ToVec(val);
		startv= ( (Number)( vall.elementAt( 0))).floatValue();
		endv= ( (Number)( vall.elementAt( 1))).floatValue();
		stepsv= ( (Number)( vall.elementAt( 2))).intValue();
		dQ_v= ( (Boolean)( vall.elementAt( 3))).booleanValue();
	}

	/**
	 * 
	 */
	public Vector getWidgetValue() throws IllegalArgumentException {
		if( entryWidget == null)
			throw new IllegalArgumentException( "Qbins1PG is not initialized");
		startv = Conversions.get_float( start.getText());
		endv = Conversions.get_float( end.getText());
		stepsv = Conversions.get_int( steps.getText());
		if( dQ.isSelected())
			dQ_v = true;
		else
			dQ_v = false;
		Vector V = new Vector();
		V.addElement( new Float( startv));
		V.addElement( new Float( endv));
		V.addElement( new Integer( stepsv));
		V.addElement( new Boolean( dQ_v));
		return ToVec( V);
		
	}

	
	/**
	 *  The whole vector is sent down. Will use data set by  get VectorValue
	 *  immediately preceding this
	 *  
	 */
	public void setWidgetValue(Vector value) throws IllegalArgumentException {
		if (value != vec_value)
			getVectorValue(value);
		start.setText("" + startv);
		end.setText("" + endv);
		steps.setText("" + stepsv);
		if (dQ_v) {
			dQ.setSelected(true);
			dQQ.setSelected(false);
		} else {
			dQ.setSelected(false);
			dQQ.setSelected(true);
		}

	}

	
	/**
	 * Assumes that the obj is a Vector with 4 entries to get the whole list
	 * Will return an expanded Vector of Floats
	 * 
	 * @param obj
	 *            The object that is to be converted to the actual Vector of
	 *            values. This object should be the short form of the vector( 4
	 *            entries)
	 * @return an expanded Vector of Floats corresponding to the given binning
	 */
	public Vector getVectorValue( Object obj) throws IllegalArgumentException {
		if( obj == null)
			return  vec_value;
		if( obj instanceof String)
			obj = Conversions.StringToVec( (String)obj );
		Vector V = Conversions.ToVec( obj );
		if( V.size() != 4)
			throw new IllegalArgumentException(
					     "Qbins1PG starts with a Vector of size 4");
		try{
			startv = ((Number)(V.elementAt(0))).floatValue();
			endv = ((Number)(V.elementAt(1))).floatValue();
			stepsv = ((Number)(V.elementAt(2))).intValue();
			dQ_v = ((Boolean)(V.elementAt(3))).booleanValue();
			return ToVec( V );
		}catch( Exception s){
			throw new IllegalArgumentException(
					"Incorrect data types in Qbins1PG init values");
		}
		
		
	}

	

   /**
    * Get a JPanel containing a composite entry widget for getting a value from
    * the user.  In this case the widget contains a prompt and another 
    * complex panel in which to enter start, end, nsteps, and const dQ or not
    *
    * @return a JPanel containing a component to enter a value.
    */
	public JPanel getWidget() {
		    if( entryWidget != null)
		    	return entryWidget;
		    String S = "" + startv;
		    String E  =  "" + endv;
		    String N = "" + stepsv;
		    String DQ;
		    if( dQ_v)
		       DQ = "dQ";
		    else
		      DQ = "dQ/Q";
		      
		    start   = new JTextField( S, 7 );
		    end     = new JTextField( E, 7 );
		    steps   = new JTextField( N, 5 );
		    dQ      = new JRadioButton( "dQ");

		    dQQ = new JRadioButton( "dQ/Q" );
		    if( dQ_v){
		       dQ.setSelected( true);
		       dQQ.setSelected(false);
		    }else{
		       dQQ.setSelected( true );
		       dQ.setSelected( false);
		    }

		    ButtonGroup Group = new ButtonGroup(  );

		    Group.add( dQ );
		    Group.add( dQQ );

		    //dQ.setSelected( true );
		    JPanel jp = new JPanel( new GridLayout( 1, 2 ) );

		    jp.add( dQ );
		    jp.add( dQQ );
		    
		    JPanel rightWidget = new JPanel( new GridLayout( 2,3));
		    rightWidget.add( new Comb( "Start Q", start ) );
		    rightWidget.add( new Comb( "N Steps", steps ) );
		    rightWidget.add( new Comb( "End Q", end ) );
		    rightWidget.add( new Comb( "Constant", jp ) );
		    rightWidget.validate(  );
		    start.addActionListener( new PG_ActionListener( this ));
		    end.addActionListener( new PG_ActionListener( this ));
		    steps.addActionListener( new PG_ActionListener( this ));
		    dQ.addItemListener( new PG_ItemListener( this ));
		    dQQ.addItemListener( new PG_ItemListener( this ));
		    entryWidget = new JPanel( new GridLayout(1,2));
		    entryWidget.add( new JLabel(getName()));
		    entryWidget.add( rightWidget);
		    return entryWidget;
	}

	
	
	 /**
	  * Set all internal references to the JPanel and entry components to NULL,
	  * so that it can be garbage collected.  Subsequent calls to getPGWidget()
	  * will return a new JPanel and entry components.
	  */
	public void destroyWidget() {
		
		entryWidget = null;
		
		dQ = null;
		start = null;
		steps = null;
	}

	
	
	
	
	/**
	 * Enable or disable the JCheckBox for entering values. 
	 *
	 * @param  on_off  Set true to enable the JCheckBox for user input.
	 */
	public void setEnabled( boolean on_off) {
		
	   if( entryWidget == null)
		   return;
	   
	   entryWidget.setEnabled( on_off);
	   for( int i =0 ; i < entryWidget.getComponentCount() ; i++)
		   setEnabled( entryWidget.getComponent( i ), on_off);
	   
	}
	
	
	
	//Recurses through all subcontainers
	private void setEnabled(Component C, boolean on_off) {
		C.setEnabled(on_off);
		if( C instanceof Container)
		for (int i = 0; i < ((Container)C).getComponentCount(); i++)
			setEnabled(((Container)C).getComponent(i), on_off);

	}
	
	

	
  /**
   * Construct a copy of this Qbins1PG object.
   *
   * @return A copy of this Qbins1PG, with the same name and value.
   */	  
	public Object getCopy() {
		Vector V = new Vector();
		try{
	        getWidgetValue();
		}catch( Exception s){
			//If GUI contains improper value, will just use last good value
		}
		V.addElement( new Float( startv));
		V.addElement( new Float( endv));
		V.addElement( new Integer( stepsv));
		V.addElement( new Boolean( dQ_v) );
		return new Qbins1PG( getName(), V );
	}
	
	
//-----------------------------------
	//Converts a Vector in the init form to a regular vector
	//Vector: start, end, nsteps, use const dt(vs dt/t)
	private static Vector ToVec( Object V ) throws IllegalArgumentException{
		if( V == null )
			throw new IllegalArgumentException( "init value for Qbins1PG must be"
					  + "a vector with 4 entries" );
		if( V instanceof String)
			V = Conversions.StringToVec( (String) V);
		if( !(V instanceof  Vector) )
			throw new IllegalArgumentException( "init value for Qbins1PG must be"
				  + "a vector with 4 entries" );
		Vector VV = (Vector)V;
	    if( VV.size() < 4 )
			throw new IllegalArgumentException( "init value for Qbins1PG must be"
					  + "a vector with 4 entries" );
	       
	    try {
			float start = ( (Number) ( VV.elementAt( 0 ))).floatValue();
			float end = ( (Number) ( VV.elementAt( 1 ))).floatValue();
			int nsteps = ( (Number) ( VV.elementAt(2 ))).intValue();
			boolean dt = ( (Boolean) ( VV.elementAt( 3 ))).booleanValue();
			if ( nsteps <= 0 )
				throw new IllegalArgumentException(
						"nsteps must be positive in Qbins1PG" );
			Vector temp = new Vector();

			if ( nsteps <= 0 ) {
				temp.add( new Float( start ) );

				return temp;
			}

			if ( !dt ) {
				if ( ( start <= 0 ) || ( end <= 0 ) ) {
					return new Vector();
				}
			}

			boolean mult = false;

			if ( !dt ) {
				mult = true;
			}

			float stepSize;

			if ( mult ) {
				stepSize = (float) Math.pow( end / start, 1.0 / nsteps );
			} else {
				stepSize = ( end - start ) / nsteps;
			}

			for ( int i = 0; i <= nsteps; i++ ) {
				temp.add( new Float( start ) );

				if ( mult ) {
					start = start * stepSize;
				} else {
					start = start + stepSize;
				}
			}
			
			return temp;
		} catch ( Exception s ) {
	    	throw new IllegalArgumentException( "The entries must be floats "+
	    			"for start,end, and nsteps(int) and a boolean for dt vs dt/t" );
	    }
	}
	
	
	 /**
	   * Utility class to add a prompt to the left of text boxes, etc.
	   */
	  private class Comb extends JPanel {
	    //~ Constructors ***********************************************************

	    /**
	     * Creates a new Comb object.
	     *
	     * @param Prompt The prompt to use.
	     * @param Comp The component to insert.
	     */
	    public Comb( String Prompt, JComponent Comp ) {
	      super( new GridLayout( 1, 2 ) );
	      add( new JLabel( Prompt, SwingConstants.CENTER ) );
	      add( Comp );
	    }
	  }
}
