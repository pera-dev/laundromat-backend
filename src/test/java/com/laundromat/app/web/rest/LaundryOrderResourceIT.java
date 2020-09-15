package com.laundromat.app.web.rest;

import com.laundromat.app.LaundromatApp;
import com.laundromat.app.domain.LaundryOrder;
import com.laundromat.app.repository.LaundryOrderRepository;
import com.laundromat.app.repository.search.LaundryOrderSearchRepository;
import com.laundromat.app.service.LaundryOrderService;

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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.laundromat.app.domain.enumeration.OrderStatus;
/**
 * Integration tests for the {@link LaundryOrderResource} REST controller.
 */
@SpringBootTest(classes = LaundromatApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class LaundryOrderResourceIT {

    private static final String DEFAULT_ORDER_ID = "AAAAAAAAAA";
    private static final String UPDATED_ORDER_ID = "BBBBBBBBBB";

    private static final OrderStatus DEFAULT_ORDER_STATUS = OrderStatus.IN_PROGRESS;
    private static final OrderStatus UPDATED_ORDER_STATUS = OrderStatus.RECEVIED;

    private static final LocalDate DEFAULT_SELECTED_SLOT = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_SELECTED_SLOT = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_PLACED_AT = "AAAAAAAAAA";
    private static final String UPDATED_PLACED_AT = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_PLACED_ON = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_PLACED_ON = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_DELIVERED_AT = "AAAAAAAAAA";
    private static final String UPDATED_DELIVERED_AT = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DELIVERED_ON = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DELIVERED_ON = LocalDate.now(ZoneId.systemDefault());

    @Autowired
    private LaundryOrderRepository laundryOrderRepository;

    @Autowired
    private LaundryOrderService laundryOrderService;

    /**
     * This repository is mocked in the com.laundromat.app.repository.search test package.
     *
     * @see com.laundromat.app.repository.search.LaundryOrderSearchRepositoryMockConfiguration
     */
    @Autowired
    private LaundryOrderSearchRepository mockLaundryOrderSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLaundryOrderMockMvc;

    private LaundryOrder laundryOrder;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LaundryOrder createEntity(EntityManager em) {
        LaundryOrder laundryOrder = new LaundryOrder()
            .orderID(DEFAULT_ORDER_ID)
            .orderStatus(DEFAULT_ORDER_STATUS)
            .selectedSlot(DEFAULT_SELECTED_SLOT)
            .placedAt(DEFAULT_PLACED_AT)
            .placedOn(DEFAULT_PLACED_ON)
            .deliveredAt(DEFAULT_DELIVERED_AT)
            .deliveredOn(DEFAULT_DELIVERED_ON);
        return laundryOrder;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LaundryOrder createUpdatedEntity(EntityManager em) {
        LaundryOrder laundryOrder = new LaundryOrder()
            .orderID(UPDATED_ORDER_ID)
            .orderStatus(UPDATED_ORDER_STATUS)
            .selectedSlot(UPDATED_SELECTED_SLOT)
            .placedAt(UPDATED_PLACED_AT)
            .placedOn(UPDATED_PLACED_ON)
            .deliveredAt(UPDATED_DELIVERED_AT)
            .deliveredOn(UPDATED_DELIVERED_ON);
        return laundryOrder;
    }

    @BeforeEach
    public void initTest() {
        laundryOrder = createEntity(em);
    }

    @Test
    @Transactional
    public void createLaundryOrder() throws Exception {
        int databaseSizeBeforeCreate = laundryOrderRepository.findAll().size();
        // Create the LaundryOrder
        restLaundryOrderMockMvc.perform(post("/api/laundry-orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(laundryOrder)))
            .andExpect(status().isCreated());

        // Validate the LaundryOrder in the database
        List<LaundryOrder> laundryOrderList = laundryOrderRepository.findAll();
        assertThat(laundryOrderList).hasSize(databaseSizeBeforeCreate + 1);
        LaundryOrder testLaundryOrder = laundryOrderList.get(laundryOrderList.size() - 1);
        assertThat(testLaundryOrder.getOrderID()).isEqualTo(DEFAULT_ORDER_ID);
        assertThat(testLaundryOrder.getOrderStatus()).isEqualTo(DEFAULT_ORDER_STATUS);
        assertThat(testLaundryOrder.getSelectedSlot()).isEqualTo(DEFAULT_SELECTED_SLOT);
        assertThat(testLaundryOrder.getPlacedAt()).isEqualTo(DEFAULT_PLACED_AT);
        assertThat(testLaundryOrder.getPlacedOn()).isEqualTo(DEFAULT_PLACED_ON);
        assertThat(testLaundryOrder.getDeliveredAt()).isEqualTo(DEFAULT_DELIVERED_AT);
        assertThat(testLaundryOrder.getDeliveredOn()).isEqualTo(DEFAULT_DELIVERED_ON);

        // Validate the LaundryOrder in Elasticsearch
        verify(mockLaundryOrderSearchRepository, times(1)).save(testLaundryOrder);
    }

    @Test
    @Transactional
    public void createLaundryOrderWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = laundryOrderRepository.findAll().size();

        // Create the LaundryOrder with an existing ID
        laundryOrder.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restLaundryOrderMockMvc.perform(post("/api/laundry-orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(laundryOrder)))
            .andExpect(status().isBadRequest());

        // Validate the LaundryOrder in the database
        List<LaundryOrder> laundryOrderList = laundryOrderRepository.findAll();
        assertThat(laundryOrderList).hasSize(databaseSizeBeforeCreate);

        // Validate the LaundryOrder in Elasticsearch
        verify(mockLaundryOrderSearchRepository, times(0)).save(laundryOrder);
    }


    @Test
    @Transactional
    public void getAllLaundryOrders() throws Exception {
        // Initialize the database
        laundryOrderRepository.saveAndFlush(laundryOrder);

        // Get all the laundryOrderList
        restLaundryOrderMockMvc.perform(get("/api/laundry-orders?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(laundryOrder.getId().intValue())))
            .andExpect(jsonPath("$.[*].orderID").value(hasItem(DEFAULT_ORDER_ID)))
            .andExpect(jsonPath("$.[*].orderStatus").value(hasItem(DEFAULT_ORDER_STATUS.toString())))
            .andExpect(jsonPath("$.[*].selectedSlot").value(hasItem(DEFAULT_SELECTED_SLOT.toString())))
            .andExpect(jsonPath("$.[*].placedAt").value(hasItem(DEFAULT_PLACED_AT)))
            .andExpect(jsonPath("$.[*].placedOn").value(hasItem(DEFAULT_PLACED_ON.toString())))
            .andExpect(jsonPath("$.[*].deliveredAt").value(hasItem(DEFAULT_DELIVERED_AT)))
            .andExpect(jsonPath("$.[*].deliveredOn").value(hasItem(DEFAULT_DELIVERED_ON.toString())));
    }
    
    @Test
    @Transactional
    public void getLaundryOrder() throws Exception {
        // Initialize the database
        laundryOrderRepository.saveAndFlush(laundryOrder);

        // Get the laundryOrder
        restLaundryOrderMockMvc.perform(get("/api/laundry-orders/{id}", laundryOrder.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(laundryOrder.getId().intValue()))
            .andExpect(jsonPath("$.orderID").value(DEFAULT_ORDER_ID))
            .andExpect(jsonPath("$.orderStatus").value(DEFAULT_ORDER_STATUS.toString()))
            .andExpect(jsonPath("$.selectedSlot").value(DEFAULT_SELECTED_SLOT.toString()))
            .andExpect(jsonPath("$.placedAt").value(DEFAULT_PLACED_AT))
            .andExpect(jsonPath("$.placedOn").value(DEFAULT_PLACED_ON.toString()))
            .andExpect(jsonPath("$.deliveredAt").value(DEFAULT_DELIVERED_AT))
            .andExpect(jsonPath("$.deliveredOn").value(DEFAULT_DELIVERED_ON.toString()));
    }
    @Test
    @Transactional
    public void getNonExistingLaundryOrder() throws Exception {
        // Get the laundryOrder
        restLaundryOrderMockMvc.perform(get("/api/laundry-orders/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateLaundryOrder() throws Exception {
        // Initialize the database
        laundryOrderService.save(laundryOrder);

        int databaseSizeBeforeUpdate = laundryOrderRepository.findAll().size();

        // Update the laundryOrder
        LaundryOrder updatedLaundryOrder = laundryOrderRepository.findById(laundryOrder.getId()).get();
        // Disconnect from session so that the updates on updatedLaundryOrder are not directly saved in db
        em.detach(updatedLaundryOrder);
        updatedLaundryOrder
            .orderID(UPDATED_ORDER_ID)
            .orderStatus(UPDATED_ORDER_STATUS)
            .selectedSlot(UPDATED_SELECTED_SLOT)
            .placedAt(UPDATED_PLACED_AT)
            .placedOn(UPDATED_PLACED_ON)
            .deliveredAt(UPDATED_DELIVERED_AT)
            .deliveredOn(UPDATED_DELIVERED_ON);

        restLaundryOrderMockMvc.perform(put("/api/laundry-orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedLaundryOrder)))
            .andExpect(status().isOk());

        // Validate the LaundryOrder in the database
        List<LaundryOrder> laundryOrderList = laundryOrderRepository.findAll();
        assertThat(laundryOrderList).hasSize(databaseSizeBeforeUpdate);
        LaundryOrder testLaundryOrder = laundryOrderList.get(laundryOrderList.size() - 1);
        assertThat(testLaundryOrder.getOrderID()).isEqualTo(UPDATED_ORDER_ID);
        assertThat(testLaundryOrder.getOrderStatus()).isEqualTo(UPDATED_ORDER_STATUS);
        assertThat(testLaundryOrder.getSelectedSlot()).isEqualTo(UPDATED_SELECTED_SLOT);
        assertThat(testLaundryOrder.getPlacedAt()).isEqualTo(UPDATED_PLACED_AT);
        assertThat(testLaundryOrder.getPlacedOn()).isEqualTo(UPDATED_PLACED_ON);
        assertThat(testLaundryOrder.getDeliveredAt()).isEqualTo(UPDATED_DELIVERED_AT);
        assertThat(testLaundryOrder.getDeliveredOn()).isEqualTo(UPDATED_DELIVERED_ON);

        // Validate the LaundryOrder in Elasticsearch
        verify(mockLaundryOrderSearchRepository, times(2)).save(testLaundryOrder);
    }

    @Test
    @Transactional
    public void updateNonExistingLaundryOrder() throws Exception {
        int databaseSizeBeforeUpdate = laundryOrderRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLaundryOrderMockMvc.perform(put("/api/laundry-orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(laundryOrder)))
            .andExpect(status().isBadRequest());

        // Validate the LaundryOrder in the database
        List<LaundryOrder> laundryOrderList = laundryOrderRepository.findAll();
        assertThat(laundryOrderList).hasSize(databaseSizeBeforeUpdate);

        // Validate the LaundryOrder in Elasticsearch
        verify(mockLaundryOrderSearchRepository, times(0)).save(laundryOrder);
    }

    @Test
    @Transactional
    public void deleteLaundryOrder() throws Exception {
        // Initialize the database
        laundryOrderService.save(laundryOrder);

        int databaseSizeBeforeDelete = laundryOrderRepository.findAll().size();

        // Delete the laundryOrder
        restLaundryOrderMockMvc.perform(delete("/api/laundry-orders/{id}", laundryOrder.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<LaundryOrder> laundryOrderList = laundryOrderRepository.findAll();
        assertThat(laundryOrderList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the LaundryOrder in Elasticsearch
        verify(mockLaundryOrderSearchRepository, times(1)).deleteById(laundryOrder.getId());
    }

    @Test
    @Transactional
    public void searchLaundryOrder() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        laundryOrderService.save(laundryOrder);
        when(mockLaundryOrderSearchRepository.search(queryStringQuery("id:" + laundryOrder.getId())))
            .thenReturn(Collections.singletonList(laundryOrder));

        // Search the laundryOrder
        restLaundryOrderMockMvc.perform(get("/api/_search/laundry-orders?query=id:" + laundryOrder.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(laundryOrder.getId().intValue())))
            .andExpect(jsonPath("$.[*].orderID").value(hasItem(DEFAULT_ORDER_ID)))
            .andExpect(jsonPath("$.[*].orderStatus").value(hasItem(DEFAULT_ORDER_STATUS.toString())))
            .andExpect(jsonPath("$.[*].selectedSlot").value(hasItem(DEFAULT_SELECTED_SLOT.toString())))
            .andExpect(jsonPath("$.[*].placedAt").value(hasItem(DEFAULT_PLACED_AT)))
            .andExpect(jsonPath("$.[*].placedOn").value(hasItem(DEFAULT_PLACED_ON.toString())))
            .andExpect(jsonPath("$.[*].deliveredAt").value(hasItem(DEFAULT_DELIVERED_AT)))
            .andExpect(jsonPath("$.[*].deliveredOn").value(hasItem(DEFAULT_DELIVERED_ON.toString())));
    }
}
