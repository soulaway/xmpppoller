package com.github.soulaway.xmpppoller;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.OrFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.filter.ToContainsFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

public class XmppSmackClient implements Serializable{

	private static final long serialVersionUID = XmppSmackClient.class.getName().hashCode();
	private static final Logger logger = Logger.getLogger(XmppSmackClient.class);
	
	//** org.jivesoftware.smack **//*
	private Chat chat;
	
	public Chat getChat() {
		return chat;
	}

	public void setChat(Chat chat) {
		this.chat = chat;
	}

	private XMPPConnection xmppConnection;
	private PacketCollector collector;
	private XmppConnectionProperties xmppProperties;
	private SSLProperties sslProperties;
	
	public SSLProperties getSslProperties() {
		return sslProperties;
	}

	public void setSslProperties(SSLProperties sslProperties) {
		this.sslProperties = sslProperties;
	}

	// Returns the Xml Message Body of incoming message
	public boolean fetchPacket(MessageReseiver receiver){
		//logger.info("fetchPacket UP " + receiver.getConnector().getName());
		login();
		Packet packet = collector.nextResult(xmppProperties.getPollingTimeout());
		int packetCount = 0;
		while (packet != null) {
			if (packet instanceof Message) {
				Message message = (Message) packet;
				packetCount++;
				logger.info("fetchPacket: PacketCollector " + collector.hashCode() + " receive " + packetCount + " packet [" + message.getBody().length() + " chars], from " + message.getFrom());
				receiver.onMessageReceived(message.getBody());
			} else {
				logger.info("fetchPacket: Packet is not a " + Message.class.getName());
			}
			packet = collector.nextResult(xmppProperties.getPollingTimeout());
		}
		return (packetCount != 0);
	}
	
	public void sendMessage(MessageReseiver receiver, String messageToSend){
		login();
		if (chat == null){
			chat = createChat(receiver);
			logger.info("sendMessage created a chat " + chat.hashCode() + " with "  + chat.getParticipant() + " listeners : " + chat.getListeners().size());
		}
		Message message = new Message();
		message.setType(Message.Type.chat);
		message.setTo(getXmppProperties().getResponceDestination());
		message.setBody(messageToSend);
		try {
			logger.info("sendMessage sending " + messageToSend.length() + " chars to " + getXmppProperties().getResponceDestination());
			logger.info(message.getBody());
			chat.sendMessage(message);
		} catch (XMPPException e) {
			logger.error("unable to send a msg to xmpp server");
			throw new IllegalStateException("sendMessage: failed", e);
		}
	}

	private Chat createChat(final MessageReseiver receiver) {
		return getXmppConnection().getChatManager().createChat(getXmppProperties().getResponceDestination(), new MessageListener() {
			public void processMessage(Chat chat, Message message) {
				if (!message.getBody().isEmpty()) {
					logger.info("MessageListener: non handled message from " + chat.getParticipant());
					logger.info(message.getBody());
					// responce from chat after reconnect from xmpp could be handled here
				} else {
					logger.info("MessageListener: empty message from " + chat.getParticipant());
				}
			}
		});
	}
	
	private XMPPConnection createSecuredConnection(){
		ConnectionConfiguration config = new ConnectionConfiguration(xmppProperties.getHost(), xmppProperties.getPort(), xmppProperties.getServiceName() + "/"
				+ xmppProperties.getResource());
		if (xmppProperties.getSSLFlag()) {
			config.setSASLAuthenticationEnabled(true);
			config.setSelfSignedCertificateEnabled(true);
			config.setSecurityMode(ConnectionConfiguration.SecurityMode.enabled);
			config.setSocketFactory(new XmppSSLSocketFactory(getSslProperties()));
			config.setSocketFactory(new XmppSSLSocketFactory(getSslProperties()));

		}
		return new XMPPConnection(config);
	}
	
	public void login(){
		if (xmppConnection == null) {
			logger.info("creating new Xmpp connection");
			xmppConnection = createSecuredConnection();
		}
		if (!xmppConnection.isConnected()) {
				logger.info("tring to connect");
				PacketFilter packetFilter = new AndFilter(new PacketTypeFilter(Packet.class), 
						new ToContainsFilter(getXmppProperties().getConnectionDestination()));
				PacketFilter messageFilter = new AndFilter(new PacketTypeFilter(Message.class),
						new ToContainsFilter(getXmppProperties().getConnectionDestination()));
				PacketFilter toMeFilter = new OrFilter(packetFilter, messageFilter);
				logger.info("toMeFilter : " + toMeFilter);
				if (collector != null){
					logger.info("Xmpp connection was missed: canceling PacketConnector " + collector.hashCode());
					collector.cancel();
				}
				try {
					xmppConnection.connect();
					collector = getXmppConnection().createPacketCollector(toMeFilter);
				} catch (Exception e) {
					throw new IllegalStateException("Unable to conect in " + xmppProperties.getHost() + ":" + xmppProperties.getPort() + " to service " + xmppProperties.getServiceName() + " reason : " + e.getMessage(), e);
				}
				try {
					xmppConnection.login(xmppProperties.getUserName(), xmppProperties.getUserPwd(), xmppProperties.getResource());
				} catch (Exception e) {
					throw new IllegalStateException("Unable to login as " + xmppProperties.getUserName() + "@" + xmppProperties.getUserPwd() + " in " + xmppProperties.getResource() + " reason : " + e.getMessage(), e);
				}
				logger.info("isConnected : " + xmppConnection.isConnected() + " id: " + xmppConnection.getConnectionID() + " isAuth: " + xmppConnection.isAuthenticated() + " isSecured: " + xmppConnection.isSecureConnection() + " isTLSUsed: " + xmppConnection.isUsingTLS());
		}
	}

	public void disconnect() {
		if (xmppConnection != null && xmppConnection.isConnected()) {
			xmppConnection.disconnect();
		}
	}

/*	// some security logic from XmppPropertiesBuilder
	public ConnexionProperties build() {
		if (xmppProperties.getSSLFlag()) {
			getSslProperties().setProtocol(props.getStringProperty(xmppProperties.getHost() + ".protocol"));
			getSslProperties().setProtocols(
					props.getStringArray(xmppProperties.getHost() + ".protocols") == null ? new String[] { getSslProperties().getProtocol() }
							: props.getStringArray(xmppProperties.getHost() + ".protocols"));
			setCiphers(xmppProperties.getSSLProperties().getProtocols());

			// trustore
			getSslProperties().setTrustStore(props.getStringProperty(xmppProperties.getServerIdKey() + ".truststore"));
			getSslProperties().setTrustStoreEncryptPwdFlag(props.getBooleanProperty(xmppProperties.getServerIdKey() + ".truststore.is.pwd.encrypted"));
			getSslProperties().setTrustStorePassword(props.getStringProperty(xmppProperties.getServerIdKey() + ".truststore.pwd"));
			getSslProperties().setTrustStoreType(props.getStringProperty(xmppProperties.getServerIdKey() + ".truststore.type"));

			// keystore
			xmppProperties.getSSLProperties().setCheckClient(props.getBooleanProperty(xmppProperties.getServerIdKey() + ".ssl.verify.client.enabled"));
			if (getSslProperties().isCheckClient()) {
				getSslProperties().setKeyStore(props.getStringProperty(xmppProperties.getServerIdKey() + ".keystore"));
				getSslProperties().setKeyStoreType(props.getStringProperty(xmppProperties.getServerIdKey() + ".keystore.type"));
				getSslProperties().setKeyStoreEncryptPwdFlag(props.getBooleanProperty(xmppProperties.getServerIdKey() + ".keystore.is.pwd.encrypted"));
				getSslProperties().setKeyStorePassword(props.getStringProperty(xmppProperties.getServerIdKey() + ".keystore.pwd"));
			}
		}

		return xmppProperties;
	}

	private void setCiphers(String[] protocols) {
		String[] ciphers = new String[] {};
		for (String protocol : protocols) {
			xmppProperties.getSSLProperties().setCiphers(
					(String[]) ArrayUtils.addAll(props.getStringArray(xmppProperties.getServerIdKey() + protocol + ".ciphers"), xmppProperties.getSSLProperties()
							.getCiphers()));

		}
		xmppProperties.getSSLProperties().setCiphers(ciphers);

	}	*/
	
	
	public XMPPConnection getXmppConnection() {
		return xmppConnection;
	}

	public PacketCollector getCollector() {
		return collector;
	}

	public XmppConnectionProperties getXmppProperties() {
		return xmppProperties;
	}

	public void setXmppProperties(XmppConnectionProperties xmppProperties) {
		this.xmppProperties = xmppProperties;
	}
}
