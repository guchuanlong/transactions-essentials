//$Id: HeurHazardException.java,v 1.1.1.1 2006/08/29 10:01:07 guy Exp $
//$Log: HeurHazardException.java,v $
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
//Revision 1.4  2005/08/09 15:23:38  guy
//Updated javadoc, and redesigned CompositeTransaction interface
//(eliminated TransactionControl and CompositeTerminator).
//
//Revision 1.3  2004/10/12 13:03:26  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.2  2004/03/22 15:36:53  guy
//Merged-in changes from branch redesign-4-2003.
//
//Revision 1.1.1.1.10.1  2003/06/20 16:31:32  guy
//*** empty log message ***
//
//Revision 1.1.1.1  2001/10/09 12:37:25  guy
//Core module
//
//Revision 1.1  2001/03/01 19:26:57  pardon
//Added more.
//


package com.atomikos.icatch;


/**
 *
 *
 *A heuristic hazard exception propagates to the root
 *as indication of cases where 2PC commit or abort was not
 *acknowledge by all participants.
 *Heuristic information about the identity and nature of the
 *lost participants can be included.
 */

public class HeurHazardException extends Exception
{
    protected HeuristicMessage[] msgs_=null;

    /**
     *Constructor.
     *@param msgs An array of heuristic messages, or null if none.
     */

    public HeurHazardException(HeuristicMessage[] msgs)
    {
        super ("Heuristic Exception");
        msgs_=msgs;
    }

    /**
     *Get the heuristic messages.
     *
     *@return HeuristicMessage[] An array, possibly null.
     */

    public HeuristicMessage[] getHeuristicMessages()
    {
        return msgs_;
    }
}
