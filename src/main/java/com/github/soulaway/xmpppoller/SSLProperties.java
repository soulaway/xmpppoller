package com.github.soulaway.xmpppoller;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SSLProperties{

	@Autowired
    private ApplicationProperties applicationProperties;

	private String protocols;
	private String ciphers;
	private String keyStore;
	private String keyStorePassword;
	private String keyStoreType;
	private Boolean keyStoreEncryptPwdFlag;

	private String trustStore;
	private String trustStorePassword;
	private Boolean trustStoreEncryptPwdFlag;
	private String trustStoreType;

	private String protocol;

	private boolean checkClient;
	
	@PostConstruct
    private void init() {
		this.setCheckClient(applicationProperties.getBProperty("ssl.checkClient"));
		this.setProtocol(applicationProperties.getProperty("ssl.protocol"));
		this.setTrustStoreType(applicationProperties.getProperty("ssl.trustStoreType"));
		this.setTrustStoreEncryptPwdFlag(applicationProperties.getBProperty("ssl.trustStoreEncryptPwdFlag"));
		this.setTrustStorePassword(applicationProperties.getProperty("ssl.trustStorePassword"));
		this.setTrustStore(applicationProperties.getProperty("ssl.trustStore"));
		this.setKeyStoreEncryptPwdFlag(applicationProperties.getBProperty("ssl.keyStoreEncryptPwdFlag"));
		this.setProtocols(applicationProperties.getProperty("ssl.protocols"));
		this.setCiphers(applicationProperties.getProperty("ssl.ciphers"));
		this.setKeyStore(applicationProperties.getProperty("ssl.keyStore"));
		this.setKeyStorePassword(applicationProperties.getProperty("ssl.keyStorePassword"));
		this.setKeyStoreType(applicationProperties.getProperty("ssl.keyStoreType"));
    }
	
	
	public ApplicationProperties getApplicationProperties() {
		return applicationProperties;
	}
	public void setApplicationProperties(ApplicationProperties applicationProperties) {
		this.applicationProperties = applicationProperties;
	}
	public String getProtocols() {
		return protocols;
	}
	public void setProtocols(String protocols) {
		this.protocols = protocols;
	}
	public String getCiphers() {
		return ciphers;
	}
	public void setCiphers(String ciphers) {
		this.ciphers = ciphers;
	}
	public SSLProperties() {
	}
	public String getKeyStore() {
		return keyStore;
	}
	public void setKeyStore(String keyStore) {
		this.keyStore = keyStore;
	}
	public String getKeyStorePassword() {
		return keyStorePassword;
	}
	public void setKeyStorePassword(String keyStorePassword) {
		this.keyStorePassword = keyStorePassword;
	}
	public String getTrustStore() {
		return trustStore;
	}
	public void setTrustStore(String trustStore) {
		this.trustStore = trustStore;
	}
	public String getTrustStorePassword() {
		return trustStorePassword;
	}
	public void setTrustStorePassword(String trustStorePassword) {
		this.trustStorePassword = trustStorePassword;
	}
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public String getKeyStoreType() {
		return keyStoreType;
	}
	public void setKeyStoreType(String keyStoreType) {
		this.keyStoreType = keyStoreType;
	}
	public Boolean getKeyStoreEncryptPwdFlag() {
		return keyStoreEncryptPwdFlag;
	}
	public void setKeyStoreEncryptPwdFlag(Boolean keyStoreEncryptPwdFlag) {
		this.keyStoreEncryptPwdFlag = keyStoreEncryptPwdFlag;
	}
	public Boolean getTrustStoreEncryptPwdFlag() {
		return trustStoreEncryptPwdFlag;
	}
	public void setTrustStoreEncryptPwdFlag(Boolean trustStoreEncryptPwdFlag) {
		this.trustStoreEncryptPwdFlag = trustStoreEncryptPwdFlag;
	}
	public String getTrustStoreType() {
		return trustStoreType;
	}
	public void setTrustStoreType(String trustStoreType) {
		this.trustStoreType = trustStoreType;
	}
	public boolean isCheckClient() {
		return checkClient;
	}
	public void setCheckClient(boolean checkClient) {
		this.checkClient = checkClient;
	}
	@Override
	public String toString() {
		return "hash: " + this.hashCode() + 
				"\n protocols: " + protocols + 
				"\n ciphers " + ciphers + 
				"\n keyStore " +  keyStore + 
				"\n keyStorePassword " + keyStorePassword + 
				"\n keyStoreType " + keyStoreType + 
				"\n keyStoreEncryptPwdFlag " + keyStoreEncryptPwdFlag + 
				"\n trustStore " +  trustStore + 
				"\n trustStorePassword " +  trustStorePassword + 
				"\n trustStoreEncryptPwdFlag " +  trustStoreEncryptPwdFlag +
				"\n trustStoreType " +  trustStoreType + 
				"\n protocol " +  protocol + 
				"\n checkClient " + checkClient;
	}
}