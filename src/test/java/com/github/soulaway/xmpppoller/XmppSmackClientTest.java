package com.github.soulaway.xmpppoller;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.soulaway.xmpppoller.MessageReseiver;
import com.github.soulaway.xmpppoller.SSLProperties;
import com.github.soulaway.xmpppoller.XmppConnectionProperties;
import com.github.soulaway.xmpppoller.XmppSmackClient;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/app-context.xml")
public class XmppSmackClientTest extends TestCase{

	@Autowired
	private SSLProperties sslProperties;
	
	@Autowired
	private XmppConnectionProperties xmppProperties;
	
	private XmppSmackClient client = new XmppSmackClient();
	
	static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
	
	@Test
	public void testLogin() throws IOException {
		client.setSslProperties(sslProperties);
		client.setXmppProperties(xmppProperties);
		
		String themessage = readFile("src/main/resources/spring/testmessage.xml", Charset.defaultCharset());
		System.out.println("Sending the message : " + System.currentTimeMillis()+ "\n" + themessage);
		client.sendMessage(new MessageReseiver() {
			@Override
			public void onMessageReceived(String responce) {
				System.out.println("sendMessage : MessageReseiver: \n" + responce);
				assertNotNull("responce message : ", responce);
			}
		}, themessage);

		assertTrue(client.getXmppConnection().isAuthenticated());
		assertTrue(client.getXmppConnection().isConnected());
		assertTrue(client.getCollector() != null);

		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		client.fetchPacket(new MessageReseiver() {
			@Override
			public void onMessageReceived(String responce) {
				System.out.println("fetchPacket : " + System.currentTimeMillis()+ " MessageReseiver: \n" + responce);
				assertNotNull("new message from someone: ", responce);
			}
		});
		
	}
}
