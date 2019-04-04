package com.recluse.spider.demo.Service;

import com.recluse.spider.demo.pojo.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface CategoryService {
    Page<Category> list(Pageable pageable);
    void add(Category category);
    void delete(int id);
    Category get(int id);
    void update(Category category);
}
