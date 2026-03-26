package com.dauphine.finance.controllers;

import com.dauphine.finance.DTO.GoalRequest;
import com.dauphine.finance.DTO.TransferRequest;
import com.dauphine.finance.model.Goal;
import com.dauphine.finance.services.GoalService;
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
        name = "Goal API",
        description = "Goals endpoints"
)
@RequestMapping("/v1/goals")
public class GoalController {

    private final GoalService service;

    public GoalController(GoalService service){
        this.service = service;
    }

    @GetMapping
    @Operation(
            summary = "Get all goals",
            description = "Retrieve all goals, filter by title or by userId"
    )
    public ResponseEntity<List<Goal>> getAll(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) UUID userId
    ) {
        if (userId != null) {
            return ResponseEntity.ok(service.getAllByUserId(userId));
        }
        List<Goal> goals = title == null || title.isBlank()
                ? service.getAll()
                : service.getAllLikeTitle(title);
        return ResponseEntity.ok(goals);
    }

    @GetMapping("{id}")
    @Operation(
            summary = "Get goal by id",
            description = "Retrieve a goal with {id} by path variable"
    )
    public ResponseEntity<Goal> getGoalById(
            @Parameter(description = "Goal's id")
            @PathVariable UUID id
    ){
        Goal goal = service.getById(id);
        return ResponseEntity.ok(goal);
    }

    @PostMapping()
    @Operation(
            summary = "Creat a new goal",
            description = "Creat a new goal, only require field name of the goal to create"
    )
    public ResponseEntity<Goal> CreatGoal(@Valid @RequestBody GoalRequest request){
        Goal goal = service.create(request.getUserId(), request.getTitle(), request.getTargetAmount(), request.getCurrentAmount(), request.getDeadline());
        return ResponseEntity
                .created(URI.create("/v1/goals/" + goal.getId()))
                .body(goal);
    }


    @PutMapping("{id}")
    @Operation(
            summary = "Update a Goal",
            description =  "Update the name of a goal identified by {id}"
    )
    public ResponseEntity<Goal> updateGoal(@PathVariable UUID id, @RequestBody GoalRequest request){
        Goal updatedGoal = service.update(id, request.getUserId(), request.getTitle(), request.getTargetAmount(), request.getCurrentAmount(), request.getDeadline());
        return ResponseEntity.ok(updatedGoal);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Patch a Goal")
    public ResponseEntity<Goal> patchGoal(@PathVariable UUID id, @RequestBody GoalRequest request) {
        Goal patched = service.patch(
                id,
                request.getUserId(),
                request.getTitle(),
                request.getTargetAmount(),
                request.getCurrentAmount(),
                request.getDeadline()
        );
        return ResponseEntity.ok(patched);
    }

    @DeleteMapping("{id}")
    @Operation(
            summary = "Delete a Goal",
            description = "Delete a Goal by {id}"
    )
    public ResponseEntity<Void> deleteGoal(@PathVariable UUID id){
        service.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/transfer")
    @Operation(
            summary = "Transfer money to a goal",
            description = "Deducts amount from user balance (creates an EXPENSE transaction) and adds it to the goal's current amount"
    )
    public ResponseEntity<Goal> transfer(@PathVariable UUID id, @RequestBody TransferRequest request) {
        Goal updated = service.transfer(id, request.getUserId(), request.getAmount());
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{id}/withdraw")
    @Operation(
            summary = "Withdraw money from a goal",
            description = "Returns amount from the goal back to user balance (creates an INCOME transaction) and deducts it from the goal's current amount"
    )
    public ResponseEntity<Goal> withdraw(@PathVariable UUID id, @RequestBody TransferRequest request) {
        Goal updated = service.withdraw(id, request.getUserId(), request.getAmount());
        return ResponseEntity.ok(updated);
    }

}
