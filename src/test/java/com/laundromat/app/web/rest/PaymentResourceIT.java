package com.laundromat.app.web.rest;

import com.laundromat.app.LaundromatApp;
import com.laundromat.app.domain.Payment;
import com.laundromat.app.repository.PaymentRepository;
import com.laundromat.app.repository.search.PaymentSearchRepository;
import com.laundromat.app.service.PaymentService;

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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.laundromat.app.domain.enumeration.PaymentMethod;
import com.laundromat.app.domain.enumeration.PaymentStatus;
/**
 * Integration tests for the {@link PaymentResource} REST controller.
 */
@SpringBootTest(classes = LaundromatApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class PaymentResourceIT {

    private static final Integer DEFAULT_PAYMENT_ID = 1;
    private static final Integer UPDATED_PAYMENT_ID = 2;

    private static final UUID DEFAULT_PAYMENT_UUID = UUID.randomUUID();
    private static final UUID UPDATED_PAYMENT_UUID = UUID.randomUUID();

    private static final PaymentMethod DEFAULT_PAYMENT_METHOD = PaymentMethod.CREDITCARD;
    private static final PaymentMethod UPDATED_PAYMENT_METHOD = PaymentMethod.COD;

    private static final Float DEFAULT_PAYMENT_AMOUNT = 1F;
    private static final Float UPDATED_PAYMENT_AMOUNT = 2F;

    private static final PaymentStatus DEFAULT_PAYMENT_STATUS = PaymentStatus.SUCCESS;
    private static final PaymentStatus UPDATED_PAYMENT_STATUS = PaymentStatus.FAILED;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentService paymentService;

    /**
     * This repository is mocked in the com.laundromat.app.repository.search test package.
     *
     * @see com.laundromat.app.repository.search.PaymentSearchRepositoryMockConfiguration
     */
    @Autowired
    private PaymentSearchRepository mockPaymentSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPaymentMockMvc;

    private Payment payment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Payment createEntity(EntityManager em) {
        Payment payment = new Payment()
            .paymentId(DEFAULT_PAYMENT_ID)
            .paymentUUID(DEFAULT_PAYMENT_UUID)
            .paymentMethod(DEFAULT_PAYMENT_METHOD)
            .paymentAmount(DEFAULT_PAYMENT_AMOUNT)
            .paymentStatus(DEFAULT_PAYMENT_STATUS);
        return payment;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Payment createUpdatedEntity(EntityManager em) {
        Payment payment = new Payment()
            .paymentId(UPDATED_PAYMENT_ID)
            .paymentUUID(UPDATED_PAYMENT_UUID)
            .paymentMethod(UPDATED_PAYMENT_METHOD)
            .paymentAmount(UPDATED_PAYMENT_AMOUNT)
            .paymentStatus(UPDATED_PAYMENT_STATUS);
        return payment;
    }

    @BeforeEach
    public void initTest() {
        payment = createEntity(em);
    }

    @Test
    @Transactional
    public void createPayment() throws Exception {
        int databaseSizeBeforeCreate = paymentRepository.findAll().size();
        // Create the Payment
        restPaymentMockMvc.perform(post("/api/payments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(payment)))
            .andExpect(status().isCreated());

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeCreate + 1);
        Payment testPayment = paymentList.get(paymentList.size() - 1);
        assertThat(testPayment.getPaymentId()).isEqualTo(DEFAULT_PAYMENT_ID);
        assertThat(testPayment.getPaymentUUID()).isEqualTo(DEFAULT_PAYMENT_UUID);
        assertThat(testPayment.getPaymentMethod()).isEqualTo(DEFAULT_PAYMENT_METHOD);
        assertThat(testPayment.getPaymentAmount()).isEqualTo(DEFAULT_PAYMENT_AMOUNT);
        assertThat(testPayment.getPaymentStatus()).isEqualTo(DEFAULT_PAYMENT_STATUS);

        // Validate the Payment in Elasticsearch
        verify(mockPaymentSearchRepository, times(1)).save(testPayment);
    }

    @Test
    @Transactional
    public void createPaymentWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = paymentRepository.findAll().size();

        // Create the Payment with an existing ID
        payment.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPaymentMockMvc.perform(post("/api/payments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(payment)))
            .andExpect(status().isBadRequest());

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeCreate);

        // Validate the Payment in Elasticsearch
        verify(mockPaymentSearchRepository, times(0)).save(payment);
    }


    @Test
    @Transactional
    public void getAllPayments() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList
        restPaymentMockMvc.perform(get("/api/payments?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(payment.getId().intValue())))
            .andExpect(jsonPath("$.[*].paymentId").value(hasItem(DEFAULT_PAYMENT_ID)))
            .andExpect(jsonPath("$.[*].paymentUUID").value(hasItem(DEFAULT_PAYMENT_UUID.toString())))
            .andExpect(jsonPath("$.[*].paymentMethod").value(hasItem(DEFAULT_PAYMENT_METHOD.toString())))
            .andExpect(jsonPath("$.[*].paymentAmount").value(hasItem(DEFAULT_PAYMENT_AMOUNT.doubleValue())))
            .andExpect(jsonPath("$.[*].paymentStatus").value(hasItem(DEFAULT_PAYMENT_STATUS.toString())));
    }
    
    @Test
    @Transactional
    public void getPayment() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get the payment
        restPaymentMockMvc.perform(get("/api/payments/{id}", payment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(payment.getId().intValue()))
            .andExpect(jsonPath("$.paymentId").value(DEFAULT_PAYMENT_ID))
            .andExpect(jsonPath("$.paymentUUID").value(DEFAULT_PAYMENT_UUID.toString()))
            .andExpect(jsonPath("$.paymentMethod").value(DEFAULT_PAYMENT_METHOD.toString()))
            .andExpect(jsonPath("$.paymentAmount").value(DEFAULT_PAYMENT_AMOUNT.doubleValue()))
            .andExpect(jsonPath("$.paymentStatus").value(DEFAULT_PAYMENT_STATUS.toString()));
    }
    @Test
    @Transactional
    public void getNonExistingPayment() throws Exception {
        // Get the payment
        restPaymentMockMvc.perform(get("/api/payments/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePayment() throws Exception {
        // Initialize the database
        paymentService.save(payment);

        int databaseSizeBeforeUpdate = paymentRepository.findAll().size();

        // Update the payment
        Payment updatedPayment = paymentRepository.findById(payment.getId()).get();
        // Disconnect from session so that the updates on updatedPayment are not directly saved in db
        em.detach(updatedPayment);
        updatedPayment
            .paymentId(UPDATED_PAYMENT_ID)
            .paymentUUID(UPDATED_PAYMENT_UUID)
            .paymentMethod(UPDATED_PAYMENT_METHOD)
            .paymentAmount(UPDATED_PAYMENT_AMOUNT)
            .paymentStatus(UPDATED_PAYMENT_STATUS);

        restPaymentMockMvc.perform(put("/api/payments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedPayment)))
            .andExpect(status().isOk());

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
        Payment testPayment = paymentList.get(paymentList.size() - 1);
        assertThat(testPayment.getPaymentId()).isEqualTo(UPDATED_PAYMENT_ID);
        assertThat(testPayment.getPaymentUUID()).isEqualTo(UPDATED_PAYMENT_UUID);
        assertThat(testPayment.getPaymentMethod()).isEqualTo(UPDATED_PAYMENT_METHOD);
        assertThat(testPayment.getPaymentAmount()).isEqualTo(UPDATED_PAYMENT_AMOUNT);
        assertThat(testPayment.getPaymentStatus()).isEqualTo(UPDATED_PAYMENT_STATUS);

        // Validate the Payment in Elasticsearch
        verify(mockPaymentSearchRepository, times(2)).save(testPayment);
    }

    @Test
    @Transactional
    public void updateNonExistingPayment() throws Exception {
        int databaseSizeBeforeUpdate = paymentRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPaymentMockMvc.perform(put("/api/payments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(payment)))
            .andExpect(status().isBadRequest());

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Payment in Elasticsearch
        verify(mockPaymentSearchRepository, times(0)).save(payment);
    }

    @Test
    @Transactional
    public void deletePayment() throws Exception {
        // Initialize the database
        paymentService.save(payment);

        int databaseSizeBeforeDelete = paymentRepository.findAll().size();

        // Delete the payment
        restPaymentMockMvc.perform(delete("/api/payments/{id}", payment.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Payment in Elasticsearch
        verify(mockPaymentSearchRepository, times(1)).deleteById(payment.getId());
    }

    @Test
    @Transactional
    public void searchPayment() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        paymentService.save(payment);
        when(mockPaymentSearchRepository.search(queryStringQuery("id:" + payment.getId())))
            .thenReturn(Collections.singletonList(payment));

        // Search the payment
        restPaymentMockMvc.perform(get("/api/_search/payments?query=id:" + payment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(payment.getId().intValue())))
            .andExpect(jsonPath("$.[*].paymentId").value(hasItem(DEFAULT_PAYMENT_ID)))
            .andExpect(jsonPath("$.[*].paymentUUID").value(hasItem(DEFAULT_PAYMENT_UUID.toString())))
            .andExpect(jsonPath("$.[*].paymentMethod").value(hasItem(DEFAULT_PAYMENT_METHOD.toString())))
            .andExpect(jsonPath("$.[*].paymentAmount").value(hasItem(DEFAULT_PAYMENT_AMOUNT.doubleValue())))
            .andExpect(jsonPath("$.[*].paymentStatus").value(hasItem(DEFAULT_PAYMENT_STATUS.toString())));
    }
}
