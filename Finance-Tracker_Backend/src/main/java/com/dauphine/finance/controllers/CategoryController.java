package com.dauphine.finance.controllers;

import com.dauphine.finance.DTO.CategoryRequest;
import com.dauphine.finance.model.Category;
import com.dauphine.finance.services.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;
import java.util.List;

@RestController
@Tag(
        name = "Categories API",
        description = "Categories endpoints"
)
@RequestMapping("/v1/categories")
public class CategoryController {

    private final CategoryService service;

    public CategoryController(CategoryService service){
        this.service = service;
    }

    @GetMapping
    @Operation(
            summary = "Get all categories",
            description = "Retrieve all categories or filter like name"
    )
    public ResponseEntity<List<Category>> getAll(@RequestParam(required = false) String name){
        List<Category> categories = name == null || name.isBlank()
                ? service.getAll()
                : service.getAllLikeName(name);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("{id}")
    @Operation(
            summary = "Get category by id",
            description = "Retrieve a category with {id} by path variable"
    )
    public ResponseEntity<Category> getCategoryById(
            @Parameter(description = "Category's id")
            @PathVariable UUID id
    ){
        Category category = service.getById(id);
        return ResponseEntity.ok(category);
    }

    @PostMapping()
    @Operation(
            summary = "Creat a new category",
            description = "Creat a new category, only require field name of the category to create"
    )
    public ResponseEntity<Category> CreatCategory(@Valid @RequestBody CategoryRequest request){
        Category category = service.create(request.getName(), request.getCategoryType());
        return ResponseEntity
                .created(URI.create("/v1/categories/" + category.getId()))
                .body(category);
    }


    @PutMapping("{id}")
    @Operation(
            summary = "Update a Category",
            description =  "Update the name of a category identified by {id}"
    )
    public ResponseEntity<Category> updateCategory(@PathVariable UUID id, @RequestBody CategoryRequest request){
        Category updatedCategory = service.update(id, request.getName(), request.getCategoryType());
        return ResponseEntity.ok(updatedCategory);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Patch a Category", description = "Partially update a category identified by {id}")
    public ResponseEntity<Category> patchCategory(@PathVariable UUID id, @RequestBody CategoryRequest request) {
        Category patched = service.patch(
                id,
                request.getName(),
                request.getCategoryType()
        );
        return ResponseEntity.ok(patched);
    }

    @DeleteMapping("{id}")
    @Operation(
            summary = "Delete a Category",
            description = "Delete a Category by {id}"
    )
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id){
        service.deleteById(id);
        return ResponseEntity.ok().build();
    }

}
