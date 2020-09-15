package com.laundromat.app.service;

import com.laundromat.app.domain.LaundryOrder;
import com.laundromat.app.repository.LaundryOrderRepository;
import com.laundromat.app.repository.search.LaundryOrderSearchRepository;
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
 * Service Implementation for managing {@link LaundryOrder}.
 */
@Service
@Transactional
public class LaundryOrderService {

    private final Logger log = LoggerFactory.getLogger(LaundryOrderService.class);

    private final LaundryOrderRepository laundryOrderRepository;

    private final LaundryOrderSearchRepository laundryOrderSearchRepository;

    public LaundryOrderService(LaundryOrderRepository laundryOrderRepository, LaundryOrderSearchRepository laundryOrderSearchRepository) {
        this.laundryOrderRepository = laundryOrderRepository;
        this.laundryOrderSearchRepository = laundryOrderSearchRepository;
    }

    /**
     * Save a laundryOrder.
     *
     * @param laundryOrder the entity to save.
     * @return the persisted entity.
     */
    public LaundryOrder save(LaundryOrder laundryOrder) {
        log.debug("Request to save LaundryOrder : {}", laundryOrder);
        LaundryOrder result = laundryOrderRepository.save(laundryOrder);
        laundryOrderSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the laundryOrders.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<LaundryOrder> findAll() {
        log.debug("Request to get all LaundryOrders");
        return laundryOrderRepository.findAll();
    }


    /**
     * Get one laundryOrder by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<LaundryOrder> findOne(Long id) {
        log.debug("Request to get LaundryOrder : {}", id);
        return laundryOrderRepository.findById(id);
    }

    /**
     * Delete the laundryOrder by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete LaundryOrder : {}", id);
        laundryOrderRepository.deleteById(id);
        laundryOrderSearchRepository.deleteById(id);
    }

    /**
     * Search for the laundryOrder corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<LaundryOrder> search(String query) {
        log.debug("Request to search LaundryOrders for query {}", query);
        return StreamSupport
            .stream(laundryOrderSearchRepository.search(queryStringQuery(query)).spliterator(), false)
        .collect(Collectors.toList());
    }
}
