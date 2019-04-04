package com.recluse.spider.demo.controller;

import com.recluse.spider.demo.Service.CategoryService;
import com.recluse.spider.demo.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CategoryController {
    @Autowired
    CategoryService categoryService;

    @GetMapping("/Category")
    public String listCategory(Model model, @RequestParam(value = "start", defaultValue = "0")int start,
                               @RequestParam(value = "size", defaultValue = "5")int size){
        start = start<0?0:start;
        Pageable pageable = PageRequest.of(start, size, new Sort(Sort.Direction.ASC, "id"));
        Page<Category> page = categoryService.list(pageable);
        model.addAttribute("page", page);
        return "listCategory";
    }

    @PostMapping("/Category")
    public String addCategory(Category category){
        categoryService.add(category);
        return "redirect:/Category";
    }

    @DeleteMapping("/Category/{id}")
    public String deleteCategory(@PathVariable("id")int id){
        categoryService.delete(id);
        return "redirect:/Category";
    }

    @GetMapping("/Category/{id}")
    public String editCategory(Model model,@PathVariable("id")int id){
        model.addAttribute("c", categoryService.get(id));
        return "editCategory";
    }

    @PutMapping("/Category")
    public String updateCategory(Category category){
        categoryService.update(category);
        return "redirect:/Category";
    }



}
