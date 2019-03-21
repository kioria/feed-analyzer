package com.repository;

import com.model.Category;
import org.springframework.stereotype.Repository;

@Repository
public class ExtraDetailsService {
    public static final Category[] orderCategories = {
            Category.builder().name("Food").id("1").build(),
            Category.builder().name("Beverages").id("2").build(),
            Category.builder().name("Household and Pets").id("3").build(),
            Category.builder().name("New").id("4").build(),};
}
