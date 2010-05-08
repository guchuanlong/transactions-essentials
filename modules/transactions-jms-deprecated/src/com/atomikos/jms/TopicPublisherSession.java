package com.atomikos.jms;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Topic;

import com.atomikos.icatch.system.Configuration;

 /**
  * 
  * 
  * 
  * This is a <b>long-lived</b> topic sender session, representing a
  * self-refreshing JMS session that can be used to send JMS messages in a
  * transactional way. The client code does not have to worry about refreshing or
  * closing JMS objects explicitly: this is all handled in this class. All the
  * client needs to do is indicate when it wants to start or stop using the
  * session.
  * <p>
  * Note that instances are not meant for concurrent use by different threads:
  * each thread should use a private instance instead.
  * <p>
  * <b>Important: if you change any properties AFTER sending on the session, then
  * you will need to explicitly stop and restart the session to have the changes
  * take effect!</b>
  * 
  * <p>
  * Topic functionality in this product was sponsored by <a href="http://www.webtide.com">Webtide</a>.
  *
  */

public class TopicPublisherSession extends MessageProducerSession 
{
	public TopicPublisherSession()
	{
		
	}
	
	/**
	 * Gets the topic to send to.
	 * @return
	 */
	public Topic getTopic()
	{
		return ( Topic ) getDestination();
		
	}
	
	/**
	 * Sets the topic to send to (required).
	 * @param topic
	 */
	
	public void setTopic ( Topic topic )
	{
		setDestination ( topic );
	}
	
	/**
	 * Sets the topic to reply to (optional).
	 * @param topic
	 */
	
	public void setReplyToTopic ( Topic topic )
	{
		setReplyToDestination ( topic );
	}
	
	/**
	 * Gets the topic to reply to.
	 * @return Null if no reply topic was set, or if
	 * the replyTo destination is not a topic but a queue.
	 */
	
	public Topic getReplyToTopic()
	{
		Topic ret = null;
		Destination dest = getReplyToDestination();
		if ( dest instanceof Topic ) {
			ret = ( Topic ) dest;
		}
		return ret;
	}
	
	/**
	 * Sets the connection factory to use (required). This is needed
	 * to create or refresh connections for sending.
	 * 
	 * @param bean
	 */
	public void setTopicConnectionFactoryBean ( 
			TopicConnectionFactoryBean bean )
	{
		setAbstractConnectionFactoryBean ( bean );
	}
	
	/**
	 * Gets the connection factory.
	 * @return
	 */
	public TopicConnectionFactoryBean getTopicConnectionFactoryBean()
	{
		return ( TopicConnectionFactoryBean ) getAbstractConnectionFactoryBean();
	}

	protected String getDestinationName() 
	{
		String ret = null;
		Topic t = getTopic();
		if ( t != null ) {
			try {
				ret = t.getTopicName();
			} catch ( JMSException e ) {
				Configuration.logDebug ( "TopicPublisherSession: error retrieving topic name" , e );
			}
		}
		return ret;
	}

	protected String getReplyToDestinationName() 
	{
		String ret = null;
		Topic t = getReplyToTopic();
		if ( t != null ) {
			try {
				ret = t.getTopicName();
			} catch ( JMSException e ) {
				Configuration.logDebug ( "TopicPublisherSession: error retrieving topic name" , e );
			}
		}
		return ret;
	}
}
