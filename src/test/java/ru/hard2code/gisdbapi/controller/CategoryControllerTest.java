package ru.hard2code.gisdbapi.controller;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import ru.hard2code.gisdbapi.domain.entity.Category;
import ru.hard2code.gisdbapi.exception.EntityNotFoundException;
import ru.hard2code.gisdbapi.service.category.CategoryService;
import ru.hard2code.gisdbapi.system.Constants;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(authorities = {"write", "read"})
class CategoryControllerTest extends AbstractControllerTest {

    private static final String API_PATH = "/api" + Constants.Route.CATEGORIES;
    private final Category TEST_CATEGORY = new Category("someName");

    @Autowired
    private CategoryService categoryService;


    @AfterEach
    void cleanup() {
        categoryService.deleteAllCategories();
    }

    @Test
    void testCreate() throws Exception {
        mvc.perform(post(API_PATH)
                        .contentType(CONTENT_TYPE)
                        .content(objectMapper.writeValueAsString(TEST_CATEGORY))
                        .accept(CONTENT_TYPE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(greaterThan(0)));
    }

    @Test
    void whenPassedExistingIdInPOSTThenCategoryShouldBeCreatedInsteadUpdate()
            throws Exception {
        categoryService.createCategory(TEST_CATEGORY);

        var anotherCategory =
                new Category(TEST_CATEGORY.getId(), "Another category",
                        Collections.emptySet());

        mvc.perform(post(API_PATH)
                        .contentType(CONTENT_TYPE)
                        .content(objectMapper.writeValueAsString(anotherCategory))
                        .accept(CONTENT_TYPE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(not(TEST_CATEGORY.getId())))
                .andExpect(jsonPath("$.name").value("Another category"));
    }


    @Test
    void testFindAll() throws Exception {
        var systems = List.of(new Category("1"), new Category("2"),
                new Category("3"));

        categoryService.createCategory(systems.get(0));
        categoryService.createCategory(systems.get(1));
        categoryService.createCategory(systems.get(2));

        mvc.perform(get(API_PATH)
                        .contentType(CONTENT_TYPE)
                        .accept(CONTENT_TYPE))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        objectMapper.writeValueAsString(systems)));
    }

    @Test
    void testFindById() throws Exception {
        categoryService.createCategory(TEST_CATEGORY);

        mvc.perform(get(API_PATH + "/{id}", TEST_CATEGORY.getId())
                        .contentType(CONTENT_TYPE)
                        .accept(CONTENT_TYPE))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        objectMapper.writeValueAsString(TEST_CATEGORY)));
    }

    @Test
    void testDeleteById() throws Exception {
        categoryService.createCategory(TEST_CATEGORY);

        mvc.perform(delete(API_PATH + "/{id}", TEST_CATEGORY.getId())
                        .contentType(CONTENT_TYPE)
                        .accept(CONTENT_TYPE))
                .andExpect(status().isNoContent());

        assertThrows(EntityNotFoundException.class,
                () -> categoryService.findById(TEST_CATEGORY.getId()));
    }

    @Test
    void testUpdate() throws Exception {
        categoryService.createCategory(TEST_CATEGORY);

        TEST_CATEGORY.setName("NEW_NAME");

        mvc.perform(put(API_PATH + "/{id}", TEST_CATEGORY.getId())
                        .contentType(CONTENT_TYPE)
                        .content(objectMapper.writeValueAsString(TEST_CATEGORY))
                        .accept(CONTENT_TYPE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(greaterThan(0)))
                .andExpect(jsonPath("$.name").value("NEW_NAME"));
    }

}
