package com.atomikos.icatch.admin.jmx;

import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.admin.AdminTransaction;

/**
 * 
 * 
 * 
 * 
 * 
 * The base class for MBean administration of transactions.
 */

public abstract class JmxTransaction implements JmxTransactionMBean,
        MBeanRegistration
{
    private AdminTransaction adminTransaction;

    private MBeanServer server;

    private ObjectName name;

    /**
     * Convert the given int state.
     * 
     * @param state
     *            The given int state.
     * @return String The string state, or null if not found.
     */

    protected static String convertState ( int state )
    {
        String ret = "UNKNOWN";

        switch ( state ) {
        case AdminTransaction.STATE_ACTIVE:
            ret = "ACTIVE";
            break;
        case AdminTransaction.STATE_PREPARING:
            ret = "PREPARING";
            break;
        case AdminTransaction.STATE_PREPARED:
            ret = "PREPARED";
            break;
        case AdminTransaction.STATE_HEUR_MIXED:
            ret = "HEURISTIC MIXED";
            break;
        case AdminTransaction.STATE_HEUR_HAZARD:
            ret = "HEURISTIC HAZARD";
            break;
        case AdminTransaction.STATE_HEUR_COMMITTED:
            ret = "HEURISTIC COMMIT";
            break;
        case AdminTransaction.STATE_HEUR_ABORTED:
            ret = "HEURISTIC ROLLBACK";
            break;
        case AdminTransaction.STATE_COMMITTING:
            ret = "COMMITTING";
            break;
        case AdminTransaction.STATE_ABORTING:
            ret = "ROLLING BACK";
            break;
        case AdminTransaction.STATE_TERMINATED:
            ret = "TERMINATED";
            break;

        default:
            break;
        }

        return ret;
    }

    protected static String[] convertHeuristicMessages ( HeuristicMessage[] msgs )
    {
        String[] ret = new String[msgs.length];
        for ( int i = 0; i < msgs.length; i++ ) {
            if ( msgs[i] != null )
                ret[i] = msgs[i].toString ();
            else
                ret[i] = "";
        }
        return ret;
    }

    /**
     * Wraps an existing AdminTransaction instance as an MBean.
     * 
     * @param adminTransaction
     *            The existing to wrap.
     */

    public JmxTransaction ( AdminTransaction adminTransaction )
    {
        super ();
        this.adminTransaction = adminTransaction;

    }

    protected AdminTransaction getAdminTransaction ()
    {
        return adminTransaction;
    }

    protected void unregister ()
    {
        try {
            if ( server.isRegistered ( name ) )
                server.unregisterMBean ( name );
        } catch ( Exception e ) {
            e.printStackTrace ();
            throw new RuntimeException ( e.getMessage () );
        }
    }

    /**
     * @see com.atomikos.icatch.admin.jmx.TransactionMBean#getTid()
     */

    public String getTid ()
    {
        return adminTransaction.getTid ();
    }

    /**
     * @see com.atomikos.icatch.admin.jmx.TransactionMBean#getState()
     */

    public String getState ()
    {

        return convertState ( adminTransaction.getState () );
    }

    /**
     * @see com.atomikos.icatch.admin.jmx.TransactionMBean#getTags()
     */

    public String[] getTags ()
    {

        return convertHeuristicMessages ( adminTransaction.getTags () );
    }

    /**
     * @see com.atomikos.icatch.admin.jmx.TransactionMBean#getHeuristicMessages()
     */

    public String[] getHeuristicMessages ()
    {
        return convertHeuristicMessages ( adminTransaction
                .getHeuristicMessages () );
    }

    /**
     * @see javax.management.MBeanRegistration#preRegister(javax.management.MBeanServer,
     *      javax.management.ObjectName)
     */

    public ObjectName preRegister ( MBeanServer server , ObjectName name )
            throws Exception
    {
        this.server = server;
        if ( name == null )
            name = new ObjectName ( "atomikos.transactions", "TID", getTid () );
        this.name = name;
        return name;
    }

    /**
     * @see javax.management.MBeanRegistration#postRegister(java.lang.Boolean)
     */

    public void postRegister ( Boolean arg0 )
    {

        // nothing to do
    }

    /**
     * @see javax.management.MBeanRegistration#preDeregister()
     */
    public void preDeregister () throws Exception
    {
        // nothing to do

    }

    /**
     * @see javax.management.MBeanRegistration#postDeregister()
     */
    public void postDeregister ()
    {
        // nothing to do

    }

}
