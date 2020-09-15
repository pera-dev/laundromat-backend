package com.laundromat.app.web.rest;

import com.laundromat.app.LaundromatApp;
import com.laundromat.app.domain.UserAccount;
import com.laundromat.app.repository.UserAccountRepository;
import com.laundromat.app.repository.search.UserAccountSearchRepository;
import com.laundromat.app.service.UserAccountService;

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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.laundromat.app.domain.enumeration.AccountType;
import com.laundromat.app.domain.enumeration.PaymentMethod;
/**
 * Integration tests for the {@link UserAccountResource} REST controller.
 */
@SpringBootTest(classes = LaundromatApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class UserAccountResourceIT {

    private static final UUID DEFAULT_ACCOUNT_UUID = UUID.randomUUID();
    private static final UUID UPDATED_ACCOUNT_UUID = UUID.randomUUID();

    private static final String DEFAULT_ACCOUNT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_ACCOUNT_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_COMPANY_NAME = "AAAAAAAAAA";
    private static final String UPDATED_COMPANY_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_ACCOUNT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_ACCOUNT_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_MOBILE_NO = "AAAAAAAAAA";
    private static final String UPDATED_MOBILE_NO = "BBBBBBBBBB";

    private static final AccountType DEFAULT_ACCOUNT_TYPE = AccountType.CUSTOMER;
    private static final AccountType UPDATED_ACCOUNT_TYPE = AccountType.DELIVERY;

    private static final LocalDate DEFAULT_LAST_LOGIN = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_LAST_LOGIN = LocalDate.now(ZoneId.systemDefault());

    private static final PaymentMethod DEFAULT_DEFAULT_PAYMENT_METHOD = PaymentMethod.CREDITCARD;
    private static final PaymentMethod UPDATED_DEFAULT_PAYMENT_METHOD = PaymentMethod.COD;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private UserAccountService userAccountService;

    /**
     * This repository is mocked in the com.laundromat.app.repository.search test package.
     *
     * @see com.laundromat.app.repository.search.UserAccountSearchRepositoryMockConfiguration
     */
    @Autowired
    private UserAccountSearchRepository mockUserAccountSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUserAccountMockMvc;

    private UserAccount userAccount;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserAccount createEntity(EntityManager em) {
        UserAccount userAccount = new UserAccount()
            .accountUUID(DEFAULT_ACCOUNT_UUID)
            .accountName(DEFAULT_ACCOUNT_NAME)
            .companyName(DEFAULT_COMPANY_NAME)
            .accountEmail(DEFAULT_ACCOUNT_EMAIL)
            .mobileNo(DEFAULT_MOBILE_NO)
            .accountType(DEFAULT_ACCOUNT_TYPE)
            .lastLogin(DEFAULT_LAST_LOGIN)
            .defaultPaymentMethod(DEFAULT_DEFAULT_PAYMENT_METHOD);
        return userAccount;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserAccount createUpdatedEntity(EntityManager em) {
        UserAccount userAccount = new UserAccount()
            .accountUUID(UPDATED_ACCOUNT_UUID)
            .accountName(UPDATED_ACCOUNT_NAME)
            .companyName(UPDATED_COMPANY_NAME)
            .accountEmail(UPDATED_ACCOUNT_EMAIL)
            .mobileNo(UPDATED_MOBILE_NO)
            .accountType(UPDATED_ACCOUNT_TYPE)
            .lastLogin(UPDATED_LAST_LOGIN)
            .defaultPaymentMethod(UPDATED_DEFAULT_PAYMENT_METHOD);
        return userAccount;
    }

    @BeforeEach
    public void initTest() {
        userAccount = createEntity(em);
    }

    @Test
    @Transactional
    public void createUserAccount() throws Exception {
        int databaseSizeBeforeCreate = userAccountRepository.findAll().size();
        // Create the UserAccount
        restUserAccountMockMvc.perform(post("/api/user-accounts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(userAccount)))
            .andExpect(status().isCreated());

        // Validate the UserAccount in the database
        List<UserAccount> userAccountList = userAccountRepository.findAll();
        assertThat(userAccountList).hasSize(databaseSizeBeforeCreate + 1);
        UserAccount testUserAccount = userAccountList.get(userAccountList.size() - 1);
        assertThat(testUserAccount.getAccountUUID()).isEqualTo(DEFAULT_ACCOUNT_UUID);
        assertThat(testUserAccount.getAccountName()).isEqualTo(DEFAULT_ACCOUNT_NAME);
        assertThat(testUserAccount.getCompanyName()).isEqualTo(DEFAULT_COMPANY_NAME);
        assertThat(testUserAccount.getAccountEmail()).isEqualTo(DEFAULT_ACCOUNT_EMAIL);
        assertThat(testUserAccount.getMobileNo()).isEqualTo(DEFAULT_MOBILE_NO);
        assertThat(testUserAccount.getAccountType()).isEqualTo(DEFAULT_ACCOUNT_TYPE);
        assertThat(testUserAccount.getLastLogin()).isEqualTo(DEFAULT_LAST_LOGIN);
        assertThat(testUserAccount.getDefaultPaymentMethod()).isEqualTo(DEFAULT_DEFAULT_PAYMENT_METHOD);

        // Validate the UserAccount in Elasticsearch
        verify(mockUserAccountSearchRepository, times(1)).save(testUserAccount);
    }

    @Test
    @Transactional
    public void createUserAccountWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = userAccountRepository.findAll().size();

        // Create the UserAccount with an existing ID
        userAccount.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserAccountMockMvc.perform(post("/api/user-accounts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(userAccount)))
            .andExpect(status().isBadRequest());

        // Validate the UserAccount in the database
        List<UserAccount> userAccountList = userAccountRepository.findAll();
        assertThat(userAccountList).hasSize(databaseSizeBeforeCreate);

        // Validate the UserAccount in Elasticsearch
        verify(mockUserAccountSearchRepository, times(0)).save(userAccount);
    }


    @Test
    @Transactional
    public void getAllUserAccounts() throws Exception {
        // Initialize the database
        userAccountRepository.saveAndFlush(userAccount);

        // Get all the userAccountList
        restUserAccountMockMvc.perform(get("/api/user-accounts?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userAccount.getId().intValue())))
            .andExpect(jsonPath("$.[*].accountUUID").value(hasItem(DEFAULT_ACCOUNT_UUID.toString())))
            .andExpect(jsonPath("$.[*].accountName").value(hasItem(DEFAULT_ACCOUNT_NAME)))
            .andExpect(jsonPath("$.[*].companyName").value(hasItem(DEFAULT_COMPANY_NAME)))
            .andExpect(jsonPath("$.[*].accountEmail").value(hasItem(DEFAULT_ACCOUNT_EMAIL)))
            .andExpect(jsonPath("$.[*].mobileNo").value(hasItem(DEFAULT_MOBILE_NO)))
            .andExpect(jsonPath("$.[*].accountType").value(hasItem(DEFAULT_ACCOUNT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].lastLogin").value(hasItem(DEFAULT_LAST_LOGIN.toString())))
            .andExpect(jsonPath("$.[*].defaultPaymentMethod").value(hasItem(DEFAULT_DEFAULT_PAYMENT_METHOD.toString())));
    }
    
    @Test
    @Transactional
    public void getUserAccount() throws Exception {
        // Initialize the database
        userAccountRepository.saveAndFlush(userAccount);

        // Get the userAccount
        restUserAccountMockMvc.perform(get("/api/user-accounts/{id}", userAccount.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(userAccount.getId().intValue()))
            .andExpect(jsonPath("$.accountUUID").value(DEFAULT_ACCOUNT_UUID.toString()))
            .andExpect(jsonPath("$.accountName").value(DEFAULT_ACCOUNT_NAME))
            .andExpect(jsonPath("$.companyName").value(DEFAULT_COMPANY_NAME))
            .andExpect(jsonPath("$.accountEmail").value(DEFAULT_ACCOUNT_EMAIL))
            .andExpect(jsonPath("$.mobileNo").value(DEFAULT_MOBILE_NO))
            .andExpect(jsonPath("$.accountType").value(DEFAULT_ACCOUNT_TYPE.toString()))
            .andExpect(jsonPath("$.lastLogin").value(DEFAULT_LAST_LOGIN.toString()))
            .andExpect(jsonPath("$.defaultPaymentMethod").value(DEFAULT_DEFAULT_PAYMENT_METHOD.toString()));
    }
    @Test
    @Transactional
    public void getNonExistingUserAccount() throws Exception {
        // Get the userAccount
        restUserAccountMockMvc.perform(get("/api/user-accounts/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateUserAccount() throws Exception {
        // Initialize the database
        userAccountService.save(userAccount);

        int databaseSizeBeforeUpdate = userAccountRepository.findAll().size();

        // Update the userAccount
        UserAccount updatedUserAccount = userAccountRepository.findById(userAccount.getId()).get();
        // Disconnect from session so that the updates on updatedUserAccount are not directly saved in db
        em.detach(updatedUserAccount);
        updatedUserAccount
            .accountUUID(UPDATED_ACCOUNT_UUID)
            .accountName(UPDATED_ACCOUNT_NAME)
            .companyName(UPDATED_COMPANY_NAME)
            .accountEmail(UPDATED_ACCOUNT_EMAIL)
            .mobileNo(UPDATED_MOBILE_NO)
            .accountType(UPDATED_ACCOUNT_TYPE)
            .lastLogin(UPDATED_LAST_LOGIN)
            .defaultPaymentMethod(UPDATED_DEFAULT_PAYMENT_METHOD);

        restUserAccountMockMvc.perform(put("/api/user-accounts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedUserAccount)))
            .andExpect(status().isOk());

        // Validate the UserAccount in the database
        List<UserAccount> userAccountList = userAccountRepository.findAll();
        assertThat(userAccountList).hasSize(databaseSizeBeforeUpdate);
        UserAccount testUserAccount = userAccountList.get(userAccountList.size() - 1);
        assertThat(testUserAccount.getAccountUUID()).isEqualTo(UPDATED_ACCOUNT_UUID);
        assertThat(testUserAccount.getAccountName()).isEqualTo(UPDATED_ACCOUNT_NAME);
        assertThat(testUserAccount.getCompanyName()).isEqualTo(UPDATED_COMPANY_NAME);
        assertThat(testUserAccount.getAccountEmail()).isEqualTo(UPDATED_ACCOUNT_EMAIL);
        assertThat(testUserAccount.getMobileNo()).isEqualTo(UPDATED_MOBILE_NO);
        assertThat(testUserAccount.getAccountType()).isEqualTo(UPDATED_ACCOUNT_TYPE);
        assertThat(testUserAccount.getLastLogin()).isEqualTo(UPDATED_LAST_LOGIN);
        assertThat(testUserAccount.getDefaultPaymentMethod()).isEqualTo(UPDATED_DEFAULT_PAYMENT_METHOD);

        // Validate the UserAccount in Elasticsearch
        verify(mockUserAccountSearchRepository, times(2)).save(testUserAccount);
    }

    @Test
    @Transactional
    public void updateNonExistingUserAccount() throws Exception {
        int databaseSizeBeforeUpdate = userAccountRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserAccountMockMvc.perform(put("/api/user-accounts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(userAccount)))
            .andExpect(status().isBadRequest());

        // Validate the UserAccount in the database
        List<UserAccount> userAccountList = userAccountRepository.findAll();
        assertThat(userAccountList).hasSize(databaseSizeBeforeUpdate);

        // Validate the UserAccount in Elasticsearch
        verify(mockUserAccountSearchRepository, times(0)).save(userAccount);
    }

    @Test
    @Transactional
    public void deleteUserAccount() throws Exception {
        // Initialize the database
        userAccountService.save(userAccount);

        int databaseSizeBeforeDelete = userAccountRepository.findAll().size();

        // Delete the userAccount
        restUserAccountMockMvc.perform(delete("/api/user-accounts/{id}", userAccount.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<UserAccount> userAccountList = userAccountRepository.findAll();
        assertThat(userAccountList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the UserAccount in Elasticsearch
        verify(mockUserAccountSearchRepository, times(1)).deleteById(userAccount.getId());
    }

    @Test
    @Transactional
    public void searchUserAccount() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        userAccountService.save(userAccount);
        when(mockUserAccountSearchRepository.search(queryStringQuery("id:" + userAccount.getId())))
            .thenReturn(Collections.singletonList(userAccount));

        // Search the userAccount
        restUserAccountMockMvc.perform(get("/api/_search/user-accounts?query=id:" + userAccount.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userAccount.getId().intValue())))
            .andExpect(jsonPath("$.[*].accountUUID").value(hasItem(DEFAULT_ACCOUNT_UUID.toString())))
            .andExpect(jsonPath("$.[*].accountName").value(hasItem(DEFAULT_ACCOUNT_NAME)))
            .andExpect(jsonPath("$.[*].companyName").value(hasItem(DEFAULT_COMPANY_NAME)))
            .andExpect(jsonPath("$.[*].accountEmail").value(hasItem(DEFAULT_ACCOUNT_EMAIL)))
            .andExpect(jsonPath("$.[*].mobileNo").value(hasItem(DEFAULT_MOBILE_NO)))
            .andExpect(jsonPath("$.[*].accountType").value(hasItem(DEFAULT_ACCOUNT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].lastLogin").value(hasItem(DEFAULT_LAST_LOGIN.toString())))
            .andExpect(jsonPath("$.[*].defaultPaymentMethod").value(hasItem(DEFAULT_DEFAULT_PAYMENT_METHOD.toString())));
    }
}
