//$Id: Extent.java,v 1.1.1.1 2006/08/29 10:01:07 guy Exp $
//$Log: Extent.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:07  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:40  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:34  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:29  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:57  guy
//Import.
//
//Revision 1.1.1.1  2006/03/09 14:59:22  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.7  2005/08/09 15:23:38  guy
//Updated javadoc, and redesigned CompositeTransaction interface
//(eliminated TransactionControl and CompositeTerminator).
//
//Revision 1.6  2005/08/05 15:03:28  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.5  2004/10/12 13:03:26  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.4  2001/11/01 08:41:44  guy
//Changed Extent and ExtentImp to include DIRECT participants.
//Changed CompositeTransactionImp to include this effect.
//
//Revision 1.3  2001/10/29 11:48:18  guy
//Removed obsolete method: getParticipant
//
//Revision 1.2  2001/10/29 11:09:04  guy
//Added comment to Extent.
//
//Revision 1.1.1.1  2001/10/09 12:37:25  guy
//Core module
//
//Revision 1.1  2001/03/23 17:01:59  pardon
//Added some files to repository.
//

package com.atomikos.icatch;
import java.util.Hashtable;
import java.util.Stack;

/**
 *
 *
 *The extent carries the information about the 'size' of a propagation 
 *after it returns: the directly andindirectly invoked servers, and the orphan 
 *detection information for those.
 *This interface is a system interface; it should not be handled by application
 *level code.
 *
 */

public interface Extent extends java.io.Serializable
{
    /**
     *Get the remote participants indirectly invoked.
     *
     *@return Hashtable Mapping URIs of remote 
     *participants to Integer counts.
     */

    public Hashtable getRemoteParticipants();
    
    /**
     *Add another extent  to the current extent.
     *
     *@param extent The extent to add.
     *@exception IllegalStateException If no longer allowed.
     *@exception SysException Unexpected error.
     */

    public void add ( Extent extent ) throws  IllegalStateException, SysException;
    
    /**
     *Add a participant to the extent.
     *This method is called at the server side, in order to add the work done
     *to the two-phase commit set of the calling (client) side, as well as to 
     *make sure that orphan information is propagated through the system.
     *
     *@param participant The participant to add. This instance will
     *be added to the indirect <b>as well as to the direct</b> participant set.
     *
     *@param count The number of invocations detected by the adding client.
     *@exception IllegalStateException If no longer allowed.
     *@exception SysException Unexpected error.
     */
     
    public void add ( Participant participant , int count ) 
    	throws IllegalStateException, SysException;
    	
    
     /**
      *Get the set of <b>direct</b> participants of this extent.
      *@return Stack A stack of direct participants. Direct participants
      *are those that need to be added to the client TM's two-phase
      *commit set.
      *NOTE: If a participant occurs in the direct participant set,
      *it will also be part of the remote set.
      */
      
    public Stack getParticipants();
    
    ///**
//     *Get the participant to add at the client TM.
//     *By calling this method, the client TM can get a handle
//     *to include in its coordinator 2PC set for the 
//     *composite transaction.
//     */
//     
//     
//    public Participant getParticipant()
//        throws SysException;
  
    											
}
