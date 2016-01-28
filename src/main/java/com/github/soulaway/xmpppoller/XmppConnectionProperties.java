package com.github.soulaway.xmpppoller;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class XmppConnectionProperties {

	@Autowired
	private ApplicationProperties applicationProperties;

	private String host;
	private Integer port;
	private String resource;
	private String serviceName;
	private String userName;
	private String userPwd;
	private String participantLogin;
	private Boolean availabilityFlag = false;
	private Boolean sSLFlag = false;
	private Integer pollingTimeout;

	@PostConstruct
	private void init() {
		this.setHost(applicationProperties.getProperty("xmpp.host"));
		this.setPort(applicationProperties.getIProperty("xmpp.port"));
		this.setResource(applicationProperties.getProperty("xmpp.resource"));
		this.setServiceName(applicationProperties.getProperty("xmpp.serviceName"));
		this.setUserName(applicationProperties.getProperty("xmpp.userName"));
		this.setUserPwd(applicationProperties.getProperty("xmpp.userPwd"));
		this.setParticipantLogin(applicationProperties.getProperty("xmpp.participantLogin"));
		this.setAvailabilityFlag(applicationProperties.getBProperty("xmpp.availabilityFlag"));
		this.setSSLFlag(applicationProperties.getBProperty("xmpp.sSLFlag"));
		this.setPollingTimeout(applicationProperties.getIProperty("xmpp.pollingTimeout"));
	}

	public Boolean getsSLFlag() {
		return sSLFlag;
	}

	public void setsSLFlag(Boolean sSLFlag) {
		this.sSLFlag = sSLFlag;
	}

	public Integer getPollingTimeout() {
		return pollingTimeout;
	}

	public void setPollingTimeout(Integer pollingTimeout) {
		this.pollingTimeout = pollingTimeout;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public ApplicationProperties getApplicationProperties() {
		return applicationProperties;
	}

	public void setApplicationProperties(
			ApplicationProperties applicationProperties) {
		this.applicationProperties = applicationProperties;
	}

	public String getConnectionDestination() {
		return getUserName() + "@" + getServiceName() + "/" + getResource();
	}

	public String getResponceDestination() {
		return getParticipantLogin() + "@" + getServiceName() + "/"
				+ getResource();
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserPwd() {
		return userPwd;
	}

	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}

	public Boolean getAvailabilityFlag() {
		return availabilityFlag;
	}

	public void setAvailabilityFlag(Boolean availabilityFlag) {
		this.availabilityFlag = availabilityFlag;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public Boolean getSSLFlag() {
		return sSLFlag;
	}

	public void setSSLFlag(Boolean sSLFlag) {
		this.sSLFlag = sSLFlag;
	}

	public String getParticipantLogin() {
		return participantLogin;
	}

	public void setParticipantLogin(String participantLogin) {
		this.participantLogin = participantLogin;
	}

	@Override
	public String toString() {
		return "hash:" + this.hashCode() + " host:" + host + ":" + port
				+ "\n resource " + resource + "\n serviceName " + serviceName
				+ "\n userName " + userName + "\n userPwd " + userPwd
				+ "\n participantLogin " + participantLogin
				+ "\n availabilityFlag " + availabilityFlag + " sSLFlag "
				+ sSLFlag;
	}
}
