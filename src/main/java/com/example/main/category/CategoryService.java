package com.example.main.category;

import com.example.main.DataNotFoundException;
import com.example.main.question.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category create(String name) {
        Category category = new Category();
        category.setName(name);
        this.categoryRepository.save(category);
        return category;
    }

    public List<Category> getAll() {
        return this.categoryRepository.findAll();
    }

    public Category getCategoryByName(String name) {
        Optional<Category> oc = this.categoryRepository.findByName(name);
        if (oc.isPresent()) {
            return oc.get();
        } else {
            throw new DataNotFoundException("category not found");
        }
    }
}
