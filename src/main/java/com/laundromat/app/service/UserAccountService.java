package com.laundromat.app.service;

import com.laundromat.app.domain.UserAccount;
import com.laundromat.app.repository.UserAccountRepository;
import com.laundromat.app.repository.search.UserAccountSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link UserAccount}.
 */
@Service
@Transactional
public class UserAccountService {

    private final Logger log = LoggerFactory.getLogger(UserAccountService.class);

    private final UserAccountRepository userAccountRepository;

    private final UserAccountSearchRepository userAccountSearchRepository;

    public UserAccountService(UserAccountRepository userAccountRepository, UserAccountSearchRepository userAccountSearchRepository) {
        this.userAccountRepository = userAccountRepository;
        this.userAccountSearchRepository = userAccountSearchRepository;
    }

    /**
     * Save a userAccount.
     *
     * @param userAccount the entity to save.
     * @return the persisted entity.
     */
    public UserAccount save(UserAccount userAccount) {
        log.debug("Request to save UserAccount : {}", userAccount);
        UserAccount result = userAccountRepository.save(userAccount);
        userAccountSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the userAccounts.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<UserAccount> findAll() {
        log.debug("Request to get all UserAccounts");
        return userAccountRepository.findAll();
    }


    /**
     * Get one userAccount by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<UserAccount> findOne(Long id) {
        log.debug("Request to get UserAccount : {}", id);
        return userAccountRepository.findById(id);
    }

    /**
     * Delete the userAccount by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete UserAccount : {}", id);
        userAccountRepository.deleteById(id);
        userAccountSearchRepository.deleteById(id);
    }

    /**
     * Search for the userAccount corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<UserAccount> search(String query) {
        log.debug("Request to search UserAccounts for query {}", query);
        return StreamSupport
            .stream(userAccountSearchRepository.search(queryStringQuery(query)).spliterator(), false)
        .collect(Collectors.toList());
    }
}
