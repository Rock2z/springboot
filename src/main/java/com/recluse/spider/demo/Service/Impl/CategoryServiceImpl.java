package com.recluse.spider.demo.Service.Impl;

import com.recluse.spider.demo.Service.CategoryService;
import com.recluse.spider.demo.dao.CategoryDAO;
import com.recluse.spider.demo.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    CategoryDAO categoryDAO;

    @Override
    public Page<Category> list(Pageable pageable) {
        return categoryDAO.findAll(pageable);
    }

    @Override
    public void add(Category category) {
        categoryDAO.save(category);
    }

    @Override
    public void delete(int id) {
        categoryDAO.deleteById(id);
    }

    @Override
    public Category get(int id) {
        return categoryDAO.getOne(id);
    }

    @Override
    public void update(Category category) {
        categoryDAO.save(category);
    }
}
