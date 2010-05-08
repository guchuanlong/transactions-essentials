package com.atomikos.jms;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;

import com.atomikos.datasource.xa.session.SessionHandleState;
import com.atomikos.icatch.system.Configuration;

class AtomikosJmsMessageProducerProxy extends ConsumerProducerSupport implements
		HeuristicMessageProducer {
	
	private MessageProducer delegate;
	
	AtomikosJmsMessageProducerProxy ( MessageProducer delegate , SessionHandleState state ) 
	{
		super ( state );
		this.delegate = delegate;
	}

	public void send ( Message msg, String heuristicMessage ) throws JMSException {
		
		Configuration.logInfo ( this + ": send ( message , heuristicMessage )..." );
		enlist ( heuristicMessage );
		delegate.send ( msg );
		Configuration.logDebug ( this + ": send done." );
	}

	public void send ( Destination dest, Message msg, String heuristicMessage)
			throws JMSException {
		Configuration.logInfo ( this + ": send ( destination , message , heuristicMessage )..." );
		enlist ( heuristicMessage );
		delegate.send ( dest , msg );
		Configuration.logDebug ( this + ": send done." );
	}

	public void send ( Message msg, int deliveryMode, int priority,
			long timeToLive, String heuristicMessage ) throws JMSException {
		
		Configuration.logInfo ( this + ": send ( message , deliveryMode , priority , timeToLive , heuristicMessage )..." );
		enlist ( heuristicMessage );
		delegate.send (  msg , deliveryMode , priority , timeToLive );
		Configuration.logDebug ( this + ": send done." );
	}

	public void send ( Destination dest , Message msg, int deliveryMode,
			int priority, long timeToLive, String heuristicMessage ) 
			throws JMSException {
		Configuration.logInfo ( this + ": send ( destination , message , deliveryMode , priority , timeToLive , heuristicMessage )..." );
		enlist ( heuristicMessage );
		delegate.send (  dest , msg , deliveryMode , priority , timeToLive );
		Configuration.logDebug ( this + ": send done." );
	}

	public void close() throws JMSException {
		Configuration.logInfo( this + ": close..." );
		delegate.close();
		Configuration.logDebug ( this + ": close done." );
	}

	public int getDeliveryMode() throws JMSException {
		Configuration.logInfo ( this + ": getDeliveryMode()..." );
		int ret = delegate.getDeliveryMode();
		Configuration.logDebug ( this + ": getDeliveryMode() returning " + ret );
		return ret;
	}

	public Destination getDestination() throws JMSException {
		Configuration.logInfo ( this + ": getDestination()..." );
		Destination ret = delegate.getDestination();
		Configuration.logDebug ( this + ": getDestination() returning " + ret );
		return ret;
	}

	public boolean getDisableMessageID() throws JMSException {
		Configuration.logInfo ( this + ": getDisableMessageID()..." );
		boolean ret = delegate.getDisableMessageID();
		Configuration.logDebug ( this + ": getDisableMessageID() returning " + ret );
		return ret;
	}

	public boolean getDisableMessageTimestamp() throws JMSException {
		Configuration.logInfo ( this + ": getDisableMessageTimestamp()..." );
		boolean ret = delegate.getDisableMessageTimestamp();
		Configuration.logDebug ( this + ": getDisableMessageTimestamp() returning " + ret );
		return ret;
	}

	public int getPriority() throws JMSException {
		Configuration.logInfo ( this + ": getPriority()..." );
		int ret =  delegate.getPriority();
		Configuration.logDebug ( this + ": getPriority() returning " + ret );
		return ret;
	}

	public long getTimeToLive() throws JMSException {
		Configuration.logInfo ( this + ": getTimeToLive()..." );
		long ret =  delegate.getTimeToLive();
		Configuration.logDebug ( this + ": getTimeToLive() returning " + ret );
		return ret;
	}

	public void send ( Message msg ) throws JMSException {
		Configuration.logInfo ( this + ": send ( message )..." );
		send ( msg , null );
		Configuration.logDebug ( this + ": send done." );

	}

	public void send(Destination dest , Message msg ) throws JMSException {
		Configuration.logInfo ( this + ": send ( destination , message )..." );
		send ( dest , msg , null );
		Configuration.logDebug ( this + ": send done." );
	}

	public void send(Message msg , int deliveryMode , int pty , long ttl )
			throws JMSException {
		Configuration.logInfo ( this + ": send ( message , deliveryMode , priority , timeToLive )..." );
		send ( msg , deliveryMode , pty , ttl, null );
		Configuration.logDebug ( this + ": send done." );
	}

	public void send ( Destination dest , Message msg , int mode , int pty ,
			long ttl ) throws JMSException {
		Configuration.logInfo ( this + ": send ( destination , message , deliveryMode , priority , timeToLive )..." );
		send ( dest , msg , mode , pty , ttl, null );
		Configuration.logDebug ( this + ": send done." );
	}

	public void setDeliveryMode ( int mode ) throws JMSException {
		Configuration.logInfo ( this + ": setDeliveryMode ( " + mode + " )..." );
		delegate.setDeliveryMode ( mode );
		Configuration.logDebug ( this + ": setDeliveryMode done." );
	}

	public void setDisableMessageID ( boolean mode ) throws JMSException {
		Configuration.logInfo ( this + ": setDisableMessageID ( " + mode + " )..." );
		delegate.setDisableMessageID ( mode );
		Configuration.logDebug ( this + ": setDisableMessageID done." );
	}

	public void setDisableMessageTimestamp ( boolean mode ) throws JMSException {
		
		Configuration.logInfo ( this + ": setDisableMessageTimestamp ( " + mode + " )..." );
		delegate.setDisableMessageTimestamp ( mode );
		Configuration.logDebug ( this + ": setDisableMessageTimestamp done." );
	}

	public void setPriority ( int pty ) throws JMSException {
		Configuration.logInfo ( this + ": setPriority ( " + pty + " )..." );
		delegate.setPriority ( pty );
		Configuration.logDebug ( this + ": setPriority done." );
	}

	public void setTimeToLive ( long ttl ) throws JMSException {
		Configuration.logInfo ( this + ": setTimeToLive ( " + ttl + " )..." );
		delegate.setTimeToLive ( ttl );
		Configuration.logDebug ( this + ": setTimeToLive done." );
	}
	
	public String toString() 
	{
		return "atomikos MessageProducer proxy for " + delegate;
	}

}
