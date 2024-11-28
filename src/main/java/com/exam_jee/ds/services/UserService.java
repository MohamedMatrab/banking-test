package com.exam_jee.ds.services;


import com.exam_jee.ds.model.Role;
import com.exam_jee.ds.model.Transaction;
import com.exam_jee.ds.model.User;
import com.exam_jee.ds.payload.request.RegisterBanquierRequest;
import com.exam_jee.ds.payload.request.SaveClient;
import com.exam_jee.ds.payload.request.TransferArgent;
import com.exam_jee.ds.repositories.RoleRepo;
import com.exam_jee.ds.repositories.TransactionRepo;
import com.exam_jee.ds.repositories.UserRepo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class UserService {
    private final UserRepo usersRepository;
    private final PasswordEncoder passwordEncoder;

    private final TransactionRepo transactionRepository;
    private final EmailService emailService;


    private final RoleRepo roleRepo;

    public UserService(UserRepo usersRepository, PasswordEncoder passwordEncoder, TransactionRepo transactionRepository, EmailService emailService, RoleRepo roleRepo) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        this.transactionRepository = transactionRepository;
        this.emailService = emailService;

        this.roleRepo = roleRepo;
    }
    public User findByEmail(String email){
        return  usersRepository.findByEmail(email).orElseThrow(
               null
        );
    }
    public boolean existsByUsername(String username){
        return  usersRepository.existsByUsername(username);
    }
    public User findByUsername(String username){
        return  usersRepository.findByUsername(username).orElseThrow(
               null
        );
    }
    public boolean existsByEmail(String email){
        return  usersRepository.existsByEmail(email);
    }
    public void save(User user){
        usersRepository.save(user);
    }
    public List<User> findAll(){
        return usersRepository.findAll();
    }
    public User findById(Long  id){
        return  usersRepository.findById(id).orElseThrow(
               null
        );
    }
    public void deleteUser(Long clientId){
        usersRepository.deleteById(clientId);
    }

    public void editUserProfile(Long clientId,SaveClient client){
        User user = findById(clientId);
        user.setEmail(client.getEmail());
        user.setBalance(client.getSolde());
        user.setUsername(client.getUsername());
        save(user);
    }

    public void enableClient(Long clientId) {
        User user = findById(clientId);
        user.setEnabled(!user.isEnabled());
        save(user);
    }

    public void registerClient(RegisterBanquierRequest request) {
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordd(passwordEncoder.encode(request.getPassword()))
                .role(roleRepo.findByName("CLIENT"))
                .userId(UUID.randomUUID().toString())
                .balance(0)
                .build();
        save(user);
    }

    public CompletableFuture<String> saveClient(SaveClient request) throws Exception {
        String password = UUID.randomUUID().toString();
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordd(passwordEncoder.encode(password))
                .role(roleRepo.findByName("CLIENT"))
                .userId(UUID.randomUUID().toString())
                .balance(request.getSolde())
                .build();
        save(user);
        CompletableFuture<Void> emailSendingFuture = emailService.sendEmailAccountCreation2(user,password);
        return  emailSendingFuture.thenApplyAsync((result -> "Email sent successfully"))
                .exceptionally(ex -> "Error sending email: " + ex.getMessage());

    }
    public void registerBanquier(RegisterBanquierRequest request) {

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordd(passwordEncoder.encode(request.getPassword()))
                .role(roleRepo.findByName("BANQUIER"))
                .userId(UUID.randomUUID().toString())
                .balance(0)
                .build();

        save(user);
    }

    public List<User> getAllBanquier(){
        Role role = roleRepo.findByName("BANQUIER");
        List<User> users = usersRepository.findByRole(role);
        return users;
    }
    public List<User> getAllClients(){
        Role role = roleRepo.findByName("CLIENT");
        List<User> users = usersRepository.findByRole(role);
        return users;
    }
    @Transactional
    public void transferArgent(TransferArgent transferArgent) throws Exception {

        User client = usersRepository.findById(transferArgent.getClientId()).orElseThrow(null);
        User benific = usersRepository.findByUserId(transferArgent.getBeneficiaireId()).orElseThrow(null);

        if (!client.isEnabled() || !benific.isEnabled()){
            throw new RuntimeException("Account not activated");
        }

        if (client.getBalance() < transferArgent.getMontant() ){
            throw new RuntimeException("Insufficient funds");
        }

        client.setBalance(client.getBalance() - transferArgent.getMontant());
        benific.setBalance(benific.getBalance() + transferArgent.getMontant());

        Transaction transaction = new Transaction();
        transaction.setBeneficiaireId(benific.getId());
        transaction.setClientId(client.getId());
        transaction.setMontant(transferArgent.getMontant());
        transaction.setDate(new Date());
        transactionRepository.save(transaction);
        usersRepository.saveAll(Arrays.asList(client,benific));

        CompletableFuture<Void> email1Future = emailService.sendEmail(client);
        CompletableFuture<Void> email2Future = email1Future.thenCompose(result -> emailService.sendEmail(benific));
        email2Future
                .thenApply(result -> "Two emails sent successfully")
                .exceptionally(ex -> "Error sending emails: " + ex.getMessage());
    }

    public List<Transaction> getHistoriqueTransactions(Long clientId) {
        return transactionRepository.findAllByClientIdOrBeneficiaireId(clientId,clientId);
    }
}
