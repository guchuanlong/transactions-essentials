//$Id: ResourceTransaction.java,v 1.1.1.1 2006/08/29 10:01:07 guy Exp $
//$Log: ResourceTransaction.java,v $
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
//Revision 1.6  2005/08/09 15:25:28  guy
//Updated javadoc.
//
//Revision 1.5  2005/08/05 15:05:07  guy
//Merged-in changes of redesign-5-2004 (SOAP branch)
//
//Revision 1.4  2004/10/12 13:04:48  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.3  2002/02/26 12:45:09  guy
//Added IllegalStateException to resume and suspend. Needed for JTA.
//
//Revision 1.2  2001/11/30 13:29:47  guy
//Updated files: UniqueId changed to String.
//
//Revision 1.9  2001/02/26 19:36:38  pardon
//Redesign to OTS.
//
//Revision 1.8  2001/02/25 11:13:40  pardon
//Added a lot.
//
//Revision 1.7  2001/02/21 17:34:23  pardon
//Updated resume to include heuristics.
//
//Revision 1.6  2001/02/21 12:35:00  pardon
//Added compensation parameter to resume.
//
//Revision 1.5  2001/02/21 11:26:11  pardon
//added method: getResource() to get resource handle during recovery.
//
//Revision 1.4  2001/02/21 10:08:00  pardon
//Added only needed files.
//
//Revision 1.2  2001/02/21 08:44:57  pardon
//Added function getTid(), needed for logging purposes.
//
//Revision 1.1  2001/02/19 18:23:13  pardon
//Added new interfaces and classes for redesign.
//


package com.atomikos.datasource;

import com.atomikos.icatch.HeuristicMessage;
/**
 *
 *
 *The notion of a local transaction executed on a resource.
 *Serves as a handle  towards the transaction management module.
 */
 
public interface ResourceTransaction //extends Participant
{
   
    
    
    /**
     *Get identifier for this tx.
     *Should be unique in system.
     *
     *@return String The identifier, as determined by resource.
     */

    public java.lang.String getTid();
    
 
  


    /**
     *Get the resource for this transaction.
     *
     *@return TransactionalResource The resource on whose behalf this tx is.
     */

    //public TransactionalResource getResource();

    /**
     *Add a compensation context for this resourcetx.
     *
     *@param context The compensation context.
     *@exception IllegalStateException If no longer active.
     */

    public void addCompensationContext(java.util.Dictionary context)
        throws IllegalStateException;

    /**
     *Add heuristic resolution information.
     *@param mesg The heuristic message.
     *@exception IllegalStateException If no longer active.
     */

    public void addHeuristicMessage(HeuristicMessage mesg)
        throws IllegalStateException;
    
   
    /**
     *Get heuristic context info.
     *
     *@return HeuristicMessage[] An array of messages, or null if none.
     */

    public HeuristicMessage[] getHeuristicMessages();

    
    /**
     *Get the compensation information.
     *
     *@return java.util.Dictionary The compensation info, or null if none.
     */

    public java.util.Dictionary getCompensationContext();

    /**
     *Suspend the resourcetransaction, so that underlying resources can
     *be used for a next (sibling) invocation.
     *This is also the recommended method for adding the 
     *resourcetx to the coordinator object, but ONLY if 
     *the transaction has not been set for rollback.
     *NOTE: suspend is NOT the same as XAResource's suspension!
     *The XAResource version is specific to the XA protocol, 
     *and does not belong in the composite system framework.
     *As mentioned in the JTA specs., the APPLICATION SERVER is 
     *responsible for XAsuspension and XAresume ( page 11 of JTA
     *API, version of May 12, 1999 ).
     *
     *@exception IllegalStateException If wrong state.
     */

    public void suspend() throws IllegalStateException,ResourceException;

    /**
     *Resume a suspended tx.
     *
     *@exception IllegalStateException If not right state.
     */


    public void resume() throws IllegalStateException,ResourceException;
       
}
