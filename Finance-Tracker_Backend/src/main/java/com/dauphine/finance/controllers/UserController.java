package com.dauphine.finance.controllers;

import com.dauphine.finance.DTO.UserRequest;
import com.dauphine.finance.model.User;
import com.dauphine.finance.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@Tag(
        name = "User API",
        description = "Users endpoints"
)
@RequestMapping("/v1/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service){
        this.service = service;
    }

    @GetMapping
    @Operation(
            summary = "Get all users",
            description = "Retrieve all users or filter like name"
    )
    public ResponseEntity<List<User>> getAll(@RequestParam(required = false) String username){
        List<User> users = username == null || username.isBlank()
                ? service.getAll()
                : service.getAllLikeName(username);
        return ResponseEntity.ok(users);
    }

    @GetMapping("{id}")
    @Operation(
            summary = "Get user by id",
            description = "Retrieve a user with {id} by path variable"
    )
    public ResponseEntity<User> getUserById(
            @Parameter(description = "User's id")
            @PathVariable UUID id
    ){
        User user = service.getById(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping()
    @Operation(
            summary = "Creat a new user",
            description = "Creat a new user, only require field name of the user to create"
    )
    public ResponseEntity<User> CreatUser(@Valid @RequestBody UserRequest request){
        User user = service.create(request.getUsername(), request.getEmail(), request.getPassword());
        return ResponseEntity
                .created(URI.create("/v1/users/" + user.getId()))
                .body(user);
    }


    @PutMapping("{id}")
    @Operation(
            summary = "Update a User",
            description =  "Update the name of a user identified by {id}"
    )
    public ResponseEntity<User> updateUser(@PathVariable UUID id, @RequestBody UserRequest request){
        User updatedUser = service.update(id, request.getUsername(), request.getEmail(), request.getPassword());
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("{id}")
    @Operation(
            summary = "Delete a User",
            description = "Delete a User by {id}"
    )
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id){
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
