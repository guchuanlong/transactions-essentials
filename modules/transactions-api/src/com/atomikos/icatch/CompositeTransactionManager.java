//$Id: CompositeTransactionManager.java,v 1.1.1.1 2006/08/29 10:01:07 guy Exp $
//$Log: CompositeTransactionManager.java,v $
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
//Revision 1.2  2006/03/21 13:23:48  guy
//Introduced active recovery and CompTx properties as meta-tags.
//
//Revision 1.1.1.1  2006/03/09 14:59:22  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.3  2004/10/11 13:39:29  guy
//Fixed javadoc and EOL delimiters.
//
//$Id: CompositeTransactionManager.java,v 1.1.1.1 2006/08/29 10:01:07 guy Exp $
//Revision 1.2  2003/03/11 06:38:53  guy
//$Id: CompositeTransactionManager.java,v 1.1.1.1 2006/08/29 10:01:07 guy Exp $
//Merged in changes from transactionsJTA100 branch.
//$Id: CompositeTransactionManager.java,v 1.1.1.1 2006/08/29 10:01:07 guy Exp $
//
//Revision 1.1.1.1.4.1  2002/11/14 15:01:53  guy
//Adapted to new (redesigned) paradigm: getTx based in tid and suspend/resume should not work with a stack.
//
//Revision 1.1.1.1  2001/10/09 12:37:25  guy
//Core module
//


package com.atomikos.icatch;

/**
 *
 *
 *A composite transaction manager. This interface
 *outlines the API for managing composite transactions
 *in the local VM.
 */
 
 public interface CompositeTransactionManager
 {
    /**
     *Starts a new (sub)transaction (not an activity) for the current thread.
     *Associates the current thread with that instance.
     *<br>
     *<b>NOTE:</b> subtransactions should not be mixed: either each subtransaction is
     *an activity, or not (default). Use suspend/resume if mixed models are necessary:
     *for instance, if you want to create a normal transaction within an activity, then
     *suspend the activity first before starting the transaction. Afterwards, resume the
     *activity.
     *
     *@timeout Timeout ( in ms ) for the transaction.
     *
     *@return CompositeTransaction The new instance.
     *@exception SysException Unexpected error.
     *@exception IllegalStateException If there is an existing transaction that is 
     *an activity instead of a classical transaction. 
     *
     */

    public CompositeTransaction createCompositeTransaction ( 
                                                             long timeout ) 
        throws SysException, IllegalStateException;
    
    /**
     *Gets the composite transaction for the current thread.
     *
     *@return CompositeTransaction The instance for the current thread, null if none.
     *
     *@exception SysException Unexpected failure.
     */

      public CompositeTransaction getCompositeTransaction () throws SysException
;
      
       /**
        *Gets the composite transaction with the given id.
        *This method is useful e.g. for retrieving a suspended 
        *transaction by its id.
        *
        *@param tid The id of the transaction.
        *@return CompositeTransaction The transaction with the given id,
        *or null if not found.
        *@exception SysException Unexpected failure.
        */
        
      public CompositeTransaction getCompositeTransaction ( String tid )
      throws SysException;
      
    /**
     *Re-maps the thread to the given tx.
     *
     *@param ct The CompositeTransaction to resume.
     *@exception IllegalStateException If thread has tx already.
     *@exception SysException Unexpected failure.
     */
     
      public void resume ( CompositeTransaction ct )
        throws IllegalStateException, SysException;
        
    /**
     *Suspends the tx for the current thread.
     *
     *@return CompositeTransaction The transaction for the current thread.
     *
     *@exception SysException On failure.
     */

      public CompositeTransaction suspend() throws SysException ;
//      
//      /**
//       * Starts a new transaction with the option of making it an activity.
//       * Activities are treated differently: they are recoverable even while active,
//       * and can be longer-running and compensation-based. 
//       * @param timeout The timeout in ms.
//       * @param activity True if the instance needs to be an activity.
//       * @return The instance.
//       * @throws SysException Unexpected error.
//       * @throws IllegalStateException If an incompatible transaction already exists 
//       * (activities should not be mixed with other transactions).
//       */
//      
//      public CompositeTransaction createCompositeTransaction ( long timeout , boolean activity )
//      throws SysException, IllegalStateException;


 }
