package com.eureka.service.Service;

import java.io.IOException;

import javax.mail.MessagingException;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.Assert;

@TestPropertySource(locations = "classpath:test.properties")
@SpringBootTest
public class MailServiceTest {

	@Autowired
	MailService mailService;

	@Test
	void contextLoads() throws MessagingException, IOException {
		this.mailService.sendEmailWithAttachment();
		Assert.notNull(1, "Value null");
	}

}