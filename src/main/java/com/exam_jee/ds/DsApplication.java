package com.exam_jee.ds;

import com.exam_jee.ds.model.Role;
import com.exam_jee.ds.model.User;
import com.exam_jee.ds.repositories.RoleRepo;
import com.exam_jee.ds.repositories.UserRepo;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.UUID;

@SpringBootApplication
@CrossOrigin("*")
@EnableTransactionManagement
public class DsApplication implements CommandLineRunner {
	@Autowired
	private RoleRepo roleRep;
	@Autowired
	private UserRepo userRepository;
	@Autowired
	private PasswordEncoder encoder;

	public static void main(String[] args) {
		SpringApplication.run(DsApplication.class, args);
	}

	@PostConstruct
	public void init() {
		Role banquier = new Role();
		Role client = new Role();

		banquier.setName("BANQUIER");
		banquier.setDescription("banquier");

		client.setName("CLIENT");
		client.setDescription("client");


		banquier = roleRep.save(banquier);
		client = roleRep.save(client);

		User superAdmin = User.builder()
				.username("admin")
				.email("admin@gmail.com")
				.passwordd(encoder.encode("admin"))
				.role(banquier)
				.enabled(true)
				.balance(0)
				.userId(UUID.randomUUID().toString())
				.build();
		userRepository.save(superAdmin);
	}

	@Override
	public void run(String... args) throws Exception {
		init();
	}
}
