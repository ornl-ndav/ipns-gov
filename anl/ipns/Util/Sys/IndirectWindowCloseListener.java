package gov.anl.ipns.Util.Sys;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class IndirectWindowCloseListener extends WindowAdapter
{
   IhasWindowClosed WindClosedInst;
   String ID;
   
   public IndirectWindowCloseListener( IhasWindowClosed WindClosedInstance, String ID)
   {
      this.WindClosedInst = WindClosedInstance;
      this.ID = ID;
   }
   
   public void windowClosed(WindowEvent evt)
   {
      if( WindClosedInst != null)
         WindClosedInst.WindowClose( ID );
   }
   
}
