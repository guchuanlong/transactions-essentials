package com.atomikos.jdbc.nonxa;

import java.io.PrintWriter;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.atomikos.jdbc.AtomikosSQLException;
import com.atomikos.util.ClassLoadingHelper;

/**
 * 
 * 
 * 
 * A DataSource implementation that uses the DriverManager to get connections.
 * Instances can be used to access JDBC drivers that don't offer a DataSource
 * implementation of their own. Note: to do transactions, you should wrap
 * instances in a NonXADataSourceImp object!
 * 
 * 
 * @deprecated As of release 3.3, the {@link AtomikosNonXADataSourceBean} should be used instead.
 * 
 */

public class DriverManagerDataSource implements DataSource, Serializable
{

    private String driverClassName;

    private String url;

    private String user;

    private String password;

    /**
     * @see javax.sql.DataSource#getConnection()
     */
    public Connection getConnection () throws SQLException
    {
        return getConnection ( getUser (), getPassword () );
    }

    /**
     * @see javax.sql.DataSource#getConnection(java.lang.String,
     *      java.lang.String)
     */
    public Connection getConnection ( String user , String pw )
            throws SQLException
    {
        Connection ret = null;
        try {
            ClassLoadingHelper.loadClass ( getDriverClassName () ).newInstance ();
        } catch ( InstantiationException e ) {
        	 AtomikosSQLException.throwAtomikosSQLException  ( "Could not instantiate driver class: "
                    + getDriverClassName () );
        } catch ( IllegalAccessException e ) {
        	 AtomikosSQLException.throwAtomikosSQLException  ( e.getMessage () );
        } catch ( ClassNotFoundException e ) {
        	 AtomikosSQLException.throwAtomikosSQLException  ( "Driver class not found: "
                    + getDriverClassName () );
        }

        ret = DriverManager.getConnection ( getUrl (), user, pw );

        return ret;
    }

    /**
     * @see javax.sql.DataSource#getLogWriter()
     */
    public PrintWriter getLogWriter () throws SQLException
    {
        return DriverManager.getLogWriter ();
    }

    /**
     * @see javax.sql.DataSource#setLogWriter(java.io.PrintWriter)
     */
    public void setLogWriter ( PrintWriter arg0 ) throws SQLException
    {
        DriverManager.setLogWriter ( arg0 );

    }

    /**
     * @see javax.sql.DataSource#setLoginTimeout(int)
     */
    public void setLoginTimeout ( int secs ) throws SQLException
    {
        DriverManager.setLoginTimeout ( secs );

    }

    /**
     * @see javax.sql.DataSource#getLoginTimeout()
     */
    public int getLoginTimeout () throws SQLException
    {
        return DriverManager.getLoginTimeout ();
    }

    private String getPassword ()
    {
        return password;
    }

    public String getUrl ()
    {
        return url;
    }

    public String getUser ()
    {
        return user;
    }

    public void setPassword ( String string )
    {
        password = string;
    }

    public void setUrl ( String string )
    {
        url = string;
    }

    public void setUser ( String string )
    {
        user = string;
    }

    public String getDriverClassName ()
    {
        return driverClassName;
    }

    public void setDriverClassName ( String string )
    {
        driverClassName = string;
    }


}
