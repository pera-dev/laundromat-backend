package com.laundromat.app.web.rest;

import com.laundromat.app.LaundromatApp;
import com.laundromat.app.domain.LaundryItem;
import com.laundromat.app.repository.LaundryItemRepository;
import com.laundromat.app.repository.search.LaundryItemSearchRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.laundromat.app.domain.enumeration.WashType;
/**
 * Integration tests for the {@link LaundryItemResource} REST controller.
 */
@SpringBootTest(classes = LaundromatApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class LaundryItemResourceIT {

    private static final Integer DEFAULT_ITEM_ID = 1;
    private static final Integer UPDATED_ITEM_ID = 2;

    private static final String DEFAULT_ITEM_NAME = "AAAAAAAAAA";
    private static final String UPDATED_ITEM_NAME = "BBBBBBBBBB";

    private static final WashType DEFAULT_WASH_TYPE = WashType.WASHIRON;
    private static final WashType UPDATED_WASH_TYPE = WashType.WASHFOLD;

    private static final Integer DEFAULT_ITEM_QTY = 1;
    private static final Integer UPDATED_ITEM_QTY = 2;

    @Autowired
    private LaundryItemRepository laundryItemRepository;

    /**
     * This repository is mocked in the com.laundromat.app.repository.search test package.
     *
     * @see com.laundromat.app.repository.search.LaundryItemSearchRepositoryMockConfiguration
     */
    @Autowired
    private LaundryItemSearchRepository mockLaundryItemSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLaundryItemMockMvc;

    private LaundryItem laundryItem;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LaundryItem createEntity(EntityManager em) {
        LaundryItem laundryItem = new LaundryItem()
            .itemId(DEFAULT_ITEM_ID)
            .itemName(DEFAULT_ITEM_NAME)
            .washType(DEFAULT_WASH_TYPE)
            .itemQty(DEFAULT_ITEM_QTY);
        return laundryItem;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LaundryItem createUpdatedEntity(EntityManager em) {
        LaundryItem laundryItem = new LaundryItem()
            .itemId(UPDATED_ITEM_ID)
            .itemName(UPDATED_ITEM_NAME)
            .washType(UPDATED_WASH_TYPE)
            .itemQty(UPDATED_ITEM_QTY);
        return laundryItem;
    }

    @BeforeEach
    public void initTest() {
        laundryItem = createEntity(em);
    }

    @Test
    @Transactional
    public void createLaundryItem() throws Exception {
        int databaseSizeBeforeCreate = laundryItemRepository.findAll().size();
        // Create the LaundryItem
        restLaundryItemMockMvc.perform(post("/api/laundry-items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(laundryItem)))
            .andExpect(status().isCreated());

        // Validate the LaundryItem in the database
        List<LaundryItem> laundryItemList = laundryItemRepository.findAll();
        assertThat(laundryItemList).hasSize(databaseSizeBeforeCreate + 1);
        LaundryItem testLaundryItem = laundryItemList.get(laundryItemList.size() - 1);
        assertThat(testLaundryItem.getItemId()).isEqualTo(DEFAULT_ITEM_ID);
        assertThat(testLaundryItem.getItemName()).isEqualTo(DEFAULT_ITEM_NAME);
        assertThat(testLaundryItem.getWashType()).isEqualTo(DEFAULT_WASH_TYPE);
        assertThat(testLaundryItem.getItemQty()).isEqualTo(DEFAULT_ITEM_QTY);

        // Validate the LaundryItem in Elasticsearch
        verify(mockLaundryItemSearchRepository, times(1)).save(testLaundryItem);
    }

    @Test
    @Transactional
    public void createLaundryItemWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = laundryItemRepository.findAll().size();

        // Create the LaundryItem with an existing ID
        laundryItem.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restLaundryItemMockMvc.perform(post("/api/laundry-items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(laundryItem)))
            .andExpect(status().isBadRequest());

        // Validate the LaundryItem in the database
        List<LaundryItem> laundryItemList = laundryItemRepository.findAll();
        assertThat(laundryItemList).hasSize(databaseSizeBeforeCreate);

        // Validate the LaundryItem in Elasticsearch
        verify(mockLaundryItemSearchRepository, times(0)).save(laundryItem);
    }


    @Test
    @Transactional
    public void getAllLaundryItems() throws Exception {
        // Initialize the database
        laundryItemRepository.saveAndFlush(laundryItem);

        // Get all the laundryItemList
        restLaundryItemMockMvc.perform(get("/api/laundry-items?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(laundryItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].itemId").value(hasItem(DEFAULT_ITEM_ID)))
            .andExpect(jsonPath("$.[*].itemName").value(hasItem(DEFAULT_ITEM_NAME)))
            .andExpect(jsonPath("$.[*].washType").value(hasItem(DEFAULT_WASH_TYPE.toString())))
            .andExpect(jsonPath("$.[*].itemQty").value(hasItem(DEFAULT_ITEM_QTY)));
    }
    
    @Test
    @Transactional
    public void getLaundryItem() throws Exception {
        // Initialize the database
        laundryItemRepository.saveAndFlush(laundryItem);

        // Get the laundryItem
        restLaundryItemMockMvc.perform(get("/api/laundry-items/{id}", laundryItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(laundryItem.getId().intValue()))
            .andExpect(jsonPath("$.itemId").value(DEFAULT_ITEM_ID))
            .andExpect(jsonPath("$.itemName").value(DEFAULT_ITEM_NAME))
            .andExpect(jsonPath("$.washType").value(DEFAULT_WASH_TYPE.toString()))
            .andExpect(jsonPath("$.itemQty").value(DEFAULT_ITEM_QTY));
    }
    @Test
    @Transactional
    public void getNonExistingLaundryItem() throws Exception {
        // Get the laundryItem
        restLaundryItemMockMvc.perform(get("/api/laundry-items/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateLaundryItem() throws Exception {
        // Initialize the database
        laundryItemRepository.saveAndFlush(laundryItem);

        int databaseSizeBeforeUpdate = laundryItemRepository.findAll().size();

        // Update the laundryItem
        LaundryItem updatedLaundryItem = laundryItemRepository.findById(laundryItem.getId()).get();
        // Disconnect from session so that the updates on updatedLaundryItem are not directly saved in db
        em.detach(updatedLaundryItem);
        updatedLaundryItem
            .itemId(UPDATED_ITEM_ID)
            .itemName(UPDATED_ITEM_NAME)
            .washType(UPDATED_WASH_TYPE)
            .itemQty(UPDATED_ITEM_QTY);

        restLaundryItemMockMvc.perform(put("/api/laundry-items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedLaundryItem)))
            .andExpect(status().isOk());

        // Validate the LaundryItem in the database
        List<LaundryItem> laundryItemList = laundryItemRepository.findAll();
        assertThat(laundryItemList).hasSize(databaseSizeBeforeUpdate);
        LaundryItem testLaundryItem = laundryItemList.get(laundryItemList.size() - 1);
        assertThat(testLaundryItem.getItemId()).isEqualTo(UPDATED_ITEM_ID);
        assertThat(testLaundryItem.getItemName()).isEqualTo(UPDATED_ITEM_NAME);
        assertThat(testLaundryItem.getWashType()).isEqualTo(UPDATED_WASH_TYPE);
        assertThat(testLaundryItem.getItemQty()).isEqualTo(UPDATED_ITEM_QTY);

        // Validate the LaundryItem in Elasticsearch
        verify(mockLaundryItemSearchRepository, times(1)).save(testLaundryItem);
    }

    @Test
    @Transactional
    public void updateNonExistingLaundryItem() throws Exception {
        int databaseSizeBeforeUpdate = laundryItemRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLaundryItemMockMvc.perform(put("/api/laundry-items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(laundryItem)))
            .andExpect(status().isBadRequest());

        // Validate the LaundryItem in the database
        List<LaundryItem> laundryItemList = laundryItemRepository.findAll();
        assertThat(laundryItemList).hasSize(databaseSizeBeforeUpdate);

        // Validate the LaundryItem in Elasticsearch
        verify(mockLaundryItemSearchRepository, times(0)).save(laundryItem);
    }

    @Test
    @Transactional
    public void deleteLaundryItem() throws Exception {
        // Initialize the database
        laundryItemRepository.saveAndFlush(laundryItem);

        int databaseSizeBeforeDelete = laundryItemRepository.findAll().size();

        // Delete the laundryItem
        restLaundryItemMockMvc.perform(delete("/api/laundry-items/{id}", laundryItem.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<LaundryItem> laundryItemList = laundryItemRepository.findAll();
        assertThat(laundryItemList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the LaundryItem in Elasticsearch
        verify(mockLaundryItemSearchRepository, times(1)).deleteById(laundryItem.getId());
    }

    @Test
    @Transactional
    public void searchLaundryItem() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        laundryItemRepository.saveAndFlush(laundryItem);
        when(mockLaundryItemSearchRepository.search(queryStringQuery("id:" + laundryItem.getId())))
            .thenReturn(Collections.singletonList(laundryItem));

        // Search the laundryItem
        restLaundryItemMockMvc.perform(get("/api/_search/laundry-items?query=id:" + laundryItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(laundryItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].itemId").value(hasItem(DEFAULT_ITEM_ID)))
            .andExpect(jsonPath("$.[*].itemName").value(hasItem(DEFAULT_ITEM_NAME)))
            .andExpect(jsonPath("$.[*].washType").value(hasItem(DEFAULT_WASH_TYPE.toString())))
            .andExpect(jsonPath("$.[*].itemQty").value(hasItem(DEFAULT_ITEM_QTY)));
    }
}
