package com.exam_jee.ds.services;


import com.exam_jee.ds.model.User;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
//import javax.mail.MessagingException;
//import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class EmailService {

	private final Configuration configuration;
	private final JavaMailSender javaMailSender;

//	public void terminateOperation(User account) throws MessagingException, IOException, TemplateException {
//		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
//		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
//		helper.setSubject("Transfert d'argent réussi");
//		helper.setTo(account.getEmail());
//
//		StringWriter stringWriter = new StringWriter();
//		Map<String, Object> model = new HashMap<>();
//		model.put("fullname", account.getUsername());
//		model.put("email", account.getEmail());
////		model.put("url", frontendUrl + "auth/reset-password/" + token);
//		configuration.getTemplate("transfert-argent.ftl").process(model, stringWriter);
//		String emailContent = stringWriter.getBuffer().toString();
//		helper.setText(emailContent, true);
//		javaMailSender.send(mimeMessage);
//	}
//	public void sendEmailAccountCreation(User account,String pass) throws MessagingException, IOException, TemplateException {
//		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
//		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
//		helper.setSubject("Account Creation");
//		helper.setTo(account.getEmail());
//		StringWriter stringWriter = new StringWriter();
//		Map<String, Object> model = new HashMap<>();
//		model.put("fullname", account.getUsername());
//		model.put("email", account.getEmail());
//		model.put("password", pass);
//		configuration.getTemplate("account-creation.ftl").process(model, stringWriter);
//		String emailContent = stringWriter.getBuffer().toString();
//		helper.setText(emailContent, true);
//		javaMailSender.send(mimeMessage);
//	}
	@Async
	public CompletableFuture<Void> sendEmail(User account) {
		CompletableFuture<Void> future = new CompletableFuture<>();
		try {
			MimeMessage mimeMessage = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
			helper.setSubject("Transfert d'argent réussi");
			helper.setTo(account.getEmail());

			StringWriter stringWriter = new StringWriter();
			Map<String, Object> model = new HashMap<>();
			model.put("fullname", account.getUsername());
			model.put("email", account.getEmail());
			configuration.getTemplate("transfert-argent.ftl").process(model, stringWriter);
			String emailContent = stringWriter.getBuffer().toString();
			helper.setText(emailContent, true);
			javaMailSender.send(mimeMessage);
			future.complete(null);
		} catch (Exception e) {
//			return CompletableFuture.failedFuture(e);
			future.completeExceptionally(e);
		}
		return future;
	}
	@Async
	public CompletableFuture<Void> sendEmailAccountCreation2(User account,String pass) {
		CompletableFuture<Void> future = new CompletableFuture<>();
		try {
			MimeMessage mimeMessage = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
			helper.setSubject("Account Creation");
			helper.setTo(account.getEmail());
			StringWriter stringWriter = new StringWriter();
			Map<String, Object> model = new HashMap<>();
			model.put("fullname", account.getUsername());
			model.put("email", account.getEmail());
			model.put("password", pass);
			configuration.getTemplate("account-creation.ftl").process(model, stringWriter);
			String emailContent = stringWriter.getBuffer().toString();
			helper.setText(emailContent, true);
			javaMailSender.send(mimeMessage);
//			future.completedFuture(null);
			future.complete(null);
//			return future;
		} catch (Exception e) {
//			return CompletableFuture.failedFuture(e);
			future.completeExceptionally(e);
		}
		return future;
	}

}
