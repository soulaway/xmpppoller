package com.github.soulaway.xmpppoller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class XmppSSLSocketFactory extends SSLSocketFactory implements Serializable{

	private static final long serialVersionUID = XmppSSLSocketFactory.class.getName().hashCode();
	private static final Logger logger = Logger.getLogger(XmppSSLSocketFactory.class);
	
	private static final String DEFAULT_PROTOCOL = "TLS";
	private SSLProperties data;
	private SSLContext sslcontext = null;
	private SSLSocketFactory factory = null;

	public XmppSSLSocketFactory(SSLProperties data) {
		logger.info("XmppSSLSocketFactory with SSLProperties checkClient: " + data.isCheckClient());
		try {
			this.data = data;
			sslcontext = initSSLContext();
			factory = sslcontext.getSocketFactory();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Socket createSocket(final Socket socket, final String host, final int port, final boolean autoClose) throws IOException {
		logger.info("createSocket host : " + host + " port : " + port + " autoclose : " + autoClose);
		Socket socket1 = factory.createSocket(socket, host, port, autoClose);
		socket1 = overrideProtocolsAndCiphers(socket1);
		return socket1;
	}

	public Socket createSocket(InetAddress inaddr, int i, InetAddress inaddr2, int j) throws IOException {
		logger.info("createSocket inetAddress : " + inaddr.getCanonicalHostName() + " : "+ i +" localAddress : " + inaddr2.getCanonicalHostName() + " : " + j);
		Socket socket = factory.createSocket(inaddr, i, inaddr2, j);
		socket = overrideProtocolsAndCiphers(socket);
		return socket;
	}

	public Socket createSocket(final InetAddress host, final int port) throws IOException {
		logger.info("createSocket inetAddress : " + host.getCanonicalHostName() + " : "+ port);
		Socket socket = factory.createSocket(host, port);
		socket = overrideProtocolsAndCiphers(socket);
		return socket;
	}

	public Socket createSocket(final String host, final int port, final InetAddress localAddress, final int localPort) throws IOException {
		logger.info("createSocket host : " + host + " port : " + port + " localAddress : " + localAddress.getCanonicalHostName() + " : "+ localPort);
		Socket socket = (SSLSocket) factory.createSocket(host, port, localAddress, localPort);
		socket = overrideProtocolsAndCiphers(socket);
		return socket;
	}

	public Socket createSocket(final String host, final int port) throws IOException {
		logger.info("createSocket host : " + host + " port : " + port);
		Socket socket = (SSLSocket) factory.createSocket(host, port);
		socket = overrideProtocolsAndCiphers(socket);
		return socket;
	}

	public String[] getDefaultCipherSuites() {
		return data.getCiphers().split(",");
	}

	public String[] getSupportedCipherSuites() {
		return data.getCiphers().split(",");
	}

	private SSLContext initSSLContext() throws Exception {
		SSLContext sslcontext = null;
		try {
			if (data.getProtocol() != null && !data.getProtocol().trim().isEmpty())
				sslcontext = SSLContext.getInstance(data.getProtocol());
			else
				sslcontext = SSLContext.getInstance(DEFAULT_PROTOCOL);
			TrustManager[] trustManagers = getTrustManagerFactory(data.getTrustStore(), data.getTrustStorePassword()).getTrustManagers();
			if (StringUtils.isNotEmpty(data.getKeyStore()) && data.isCheckClient()) {
				javax.net.ssl.KeyManager[] keyManagers = getKeyManagerFactory(data.getKeyStore(), data.getKeyStorePassword()).getKeyManagers();
				sslcontext.init(keyManagers, trustManagers, new SecureRandom());
			} else
				sslcontext.init(null, trustManagers, new SecureRandom());
		} catch (KeyManagementException e) {
			throw new Exception("XmppSSLSocketFactory - KeyManagementException : " + e.getMessage());
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("XmppSSLSocketFactory - NoSuchAlgorithmException : " + e.getMessage());
		}
		return sslcontext;
	}

	private Socket overrideProtocolsAndCiphers(final Socket socket) {
		if (socket instanceof SSLSocket) {
			if (!data.getProtocols().isEmpty()) 
				((SSLSocket) socket).setEnabledProtocols(data.getProtocols().split(","));
			if (!data.getCiphers().isEmpty()) 
				((SSLSocket) socket).setEnabledCipherSuites(data.getCiphers().split(","));
		}
		return socket;
	}

	public String getProtocol() {
		return data.getProtocol();
	}

	public void setProtocol(String protocol) {
		this.data.setProtocol(protocol);
	}

	public SSLContext getSslcontext() {
		return sslcontext;
	}

	public void setSslcontext(SSLContext sslcontext) {
		this.sslcontext = sslcontext;
	}
	
	private TrustManagerFactory tmf = null;
	private KeyManagerFactory kmf = null;
	
	public synchronized TrustManagerFactory getTrustManagerFactory(String trustStorePath, String trustStorePwd) throws Exception {
		if(tmf != null) return tmf;
		// First initialize the key and trust material
		KeyStore ksTrust = loadTrustStore(trustStorePath, trustStorePwd);
		// TrustManagers decide whether to allow connections
		try {
			tmf = TrustManagerFactory.getInstance("SunX509");
			tmf.init(ksTrust);
		} catch (KeyStoreException e) {
			throw new Exception("RestSSLHelper - getTrustManagerFactory - KeyStoreException : "+e.getMessage());
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("RestSSLHelper - getTrustManagerFactory - NoSuchAlgorithmException : "+e.getMessage());
		}
		return tmf;
	}
	public synchronized KeyManagerFactory getKeyManagerFactory(String keystorePath, String keystorePwd) throws Exception {
		if(kmf != null) return kmf;
		KeyStore ksKeys = loadKeyStore(keystorePath, keystorePwd);
		// KeyManagers decide which key material to use
		try {
			kmf = KeyManagerFactory.getInstance("SunX509");
			kmf.init(ksKeys, keystorePwd.toCharArray());
		} catch (UnrecoverableKeyException e) {
			throw new Exception("RestSSLHelper - getKeyManagerFactory - UnrecoverableKeyException : "+e.getMessage());
		}  catch (KeyStoreException e) {
			throw new Exception("RestSSLHelper - getKeyManagerFactory - KeyStoreException : "+e.getMessage());
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("RestSSLHelper - getKeyManagerFactory - NoSuchAlgorithmException : "+e.getMessage());
		}
		return kmf;
	}
	private KeyStore loadTrustStore(String trustStorePath, String trustStorePwd) throws Exception{
		KeyStore trustStore = null;
		FileInputStream fileInputStream = null; 
	    try {
	    	trustStore = KeyStore.getInstance("jks");
	    	fileInputStream = new FileInputStream(new File(trustStorePath));
	    	trustStore.load(fileInputStream, trustStorePwd.toCharArray());
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("RestSSLHelper - loadTrustStore - NoSuchAlgorithmException : "+e.getMessage());
		} catch (CertificateException e) {
			throw new Exception("RestSSLHelper - loadTrustStore - CertificateException : "+e.getMessage());
		} catch (FileNotFoundException e) {
			throw new Exception("RestSSLHelper - loadTrustStore - FileNotFoundException : "+e.getMessage());
		} catch (IOException e) {
			throw new Exception("RestSSLHelper - loadTrustStore - IOException : "+e.getMessage());
		} catch (KeyStoreException e) {
			throw new Exception("RestSSLHelper - loadTrustStore - KeyStoreException : "+e.getMessage());
		}finally {
			fileInputStream.close();
		}
	    return trustStore;
	}
	private KeyStore loadKeyStore(String keystorePath, String keystorePwd) throws Exception{
	    KeyStore keyStore = null;
	    FileInputStream fileInputStream = null;
	    try {
	    	keyStore = KeyStore.getInstance("jks");
	    	fileInputStream = new FileInputStream(new File(keystorePath));
			keyStore.load(fileInputStream, keystorePwd.toCharArray());
	    } catch (NoSuchAlgorithmException e) {
			throw new Exception("RestSSLHelper - loadKeyStore - NoSuchAlgorithmException : "+e.getMessage());
		} catch (CertificateException e) {
			throw new Exception("RestSSLHelper - loadKeyStore - CertificateException : "+e.getMessage());
		} catch (FileNotFoundException e) {
			throw new Exception("RestSSLHelper - loadKeyStore - FileNotFoundException : "+e.getMessage());
		} catch (IOException e) {
			throw new Exception("RestSSLHelper - loadKeyStore - IOException : "+e.getMessage());
		} catch (KeyStoreException e) {
			throw new Exception("RestSSLHelper - loadKeyStore - KeyStoreException : "+e.getMessage());
		}finally {
			fileInputStream.close();
		}
	    return keyStore;
	}
}
