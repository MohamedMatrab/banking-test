package com.exam_jee.ds.controllers;

import com.exam_jee.ds.model.Transaction;
import com.exam_jee.ds.model.User;
import com.exam_jee.ds.payload.request.SaveClient;
import com.exam_jee.ds.payload.request.TransferArgent;
import com.exam_jee.ds.payload.response.ApiResponse;
import com.exam_jee.ds.payload.response.ApiResponseWithData;
import com.exam_jee.ds.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/")
    public ResponseEntity<?> allUsers() {
        try {
            List<User> users = userService.findAll();
            return ResponseEntity.ok(new ApiResponseWithData<>(true, "Get All users", users));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {
        try {
            User user = userService.findById(userId);
            return ResponseEntity.ok(new ApiResponseWithData<>(true, "Get user by id", user));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }
    @PostMapping("/client")
    public ResponseEntity<?> saveClient(
            @RequestBody SaveClient client
    ) {
        if (userService.existsByEmail(client.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse(false,"Error: Email is already in use!"));
        }

        try {
            CompletableFuture<String> emailSendingFuture =  userService.saveClient(client);
            emailSendingFuture.get();
            return ResponseEntity.ok(new ApiResponse(true, "Client successfully created"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }
    @PutMapping("/{clientId}")
    public ResponseEntity<?> editProfile(@PathVariable Long clientId,@RequestBody SaveClient editRequest) {
        try {
            userService.editUserProfile(clientId,editRequest);
            return ResponseEntity.ok(new ApiResponse(true, "Updated Profile"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }


    @PutMapping("/enable-client/{clientId}")
    public ResponseEntity<?> enableClient(@PathVariable Long clientId) {
        try {
            userService.enableClient(clientId);
            return ResponseEntity.ok(new ApiResponse(true, "Client Status changed"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }
    @GetMapping("/all-bons-clients")
    public ResponseEntity<?> getAllBanquier() {
        try {
            List<User> banquiers = userService.getAllBanquier();
            return ResponseEntity.ok(new ApiResponseWithData<>(true, "Get All Banquier", banquiers));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }
    @GetMapping("/all-clients")
    public ResponseEntity<?> getAllClients() {
        try {
            List<User> clients = userService.getAllClients();
            return ResponseEntity.ok(new ApiResponseWithData<>(true, "Get All Clients", clients));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }
    @PostMapping("/transfer")
    public ResponseEntity<?> transferArgent(@RequestBody TransferArgent transferArgent) {
        try {
            userService.transferArgent(transferArgent);
            return ResponseEntity.ok(new ApiResponse(true,"Transfert d'argent réussi !"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(false,"Erreur lors du transfert d'argent."+e.getMessage()));
        }
    }

    @GetMapping("/historique/{clientId}")
    public List<Transaction> getHistoriqueTransactions(@PathVariable Long clientId) {
        return userService.getHistoriqueTransactions(clientId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUSer(@PathVariable Long userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok(new ApiResponse(true, "user deleted"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }
}
