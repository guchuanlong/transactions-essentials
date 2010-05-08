//$Id: CompositeTerminator.java,v 1.1.1.1 2006/08/29 10:01:07 guy Exp $
//$Log: CompositeTerminator.java,v $
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
//Revision 1.6  2005/08/11 09:24:05  guy
//Debugged compensation.
//
//Revision 1.5  2005/08/10 07:26:33  guy
//Corrected: should not be deprecated (SOAP termination!)
//
//Revision 1.4  2005/08/09 15:23:38  guy
//Updated javadoc, and redesigned CompositeTransaction interface
//(eliminated TransactionControl and CompositeTerminator).
//
//Revision 1.3  2005/08/05 15:03:27  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.2  2004/10/12 13:03:25  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.1.1.1  2001/10/09 12:37:25  guy
//Core module
//
//Revision 1.3  2001/03/23 17:00:30  pardon
//Lots of implementations for Terminator and proxies.
//
//Revision 1.2  2001/03/08 18:18:21  pardon
//Redesign cont'd.
//
//Revision 1.1  2001/02/27 17:57:14  pardon
//Added files.
//

package com.atomikos.icatch;


/**
 *
 *
 *A handle to terminate the composite transaction.
 *Must ALWAYS be used to handle termination throughout the system,
 *also for subtransactions!
 *
 *
 */

public interface CompositeTerminator 
{
    /**
     *Commit the composite transaction.
     *
     *@exception HeurRollbackException On heuristic rollback.
     *@exception HeurMixedException On heuristic mixed outcome.
     *@exception SysException For unexpected failures.
     *@exception SecurityException If calling thread does not have 
     *right to commit.
     *@exception HeurHazardException In case of heuristic hazard.
     *@exception RollbackException If the transaction was rolled back
     *before prepare.
     */

    public void commit() 
        throws 
	  HeurRollbackException,HeurMixedException,
	  HeurHazardException,
	  SysException,java.lang.SecurityException,
	  RollbackException;



    /**
     *Rollback the current transaction.
     *@exception IllegalStateException If prepared or inactive.
     *@exception SysException If unexpected error.
     */

    public void rollback()
        throws IllegalStateException, SysException;
}
