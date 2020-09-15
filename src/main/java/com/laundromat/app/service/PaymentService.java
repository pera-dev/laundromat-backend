package com.laundromat.app.service;

import com.laundromat.app.domain.Payment;
import com.laundromat.app.repository.PaymentRepository;
import com.laundromat.app.repository.search.PaymentSearchRepository;
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
 * Service Implementation for managing {@link Payment}.
 */
@Service
@Transactional
public class PaymentService {

    private final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;

    private final PaymentSearchRepository paymentSearchRepository;

    public PaymentService(PaymentRepository paymentRepository, PaymentSearchRepository paymentSearchRepository) {
        this.paymentRepository = paymentRepository;
        this.paymentSearchRepository = paymentSearchRepository;
    }

    /**
     * Save a payment.
     *
     * @param payment the entity to save.
     * @return the persisted entity.
     */
    public Payment save(Payment payment) {
        log.debug("Request to save Payment : {}", payment);
        Payment result = paymentRepository.save(payment);
        paymentSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the payments.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Payment> findAll() {
        log.debug("Request to get all Payments");
        return paymentRepository.findAll();
    }



    /**
     *  Get all the payments where LaundryOrder is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true) 
    public List<Payment> findAllWhereLaundryOrderIsNull() {
        log.debug("Request to get all payments where LaundryOrder is null");
        return StreamSupport
            .stream(paymentRepository.findAll().spliterator(), false)
            .filter(payment -> payment.getLaundryOrder() == null)
            .collect(Collectors.toList());
    }

    /**
     * Get one payment by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Payment> findOne(Long id) {
        log.debug("Request to get Payment : {}", id);
        return paymentRepository.findById(id);
    }

    /**
     * Delete the payment by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Payment : {}", id);
        paymentRepository.deleteById(id);
        paymentSearchRepository.deleteById(id);
    }

    /**
     * Search for the payment corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Payment> search(String query) {
        log.debug("Request to search Payments for query {}", query);
        return StreamSupport
            .stream(paymentSearchRepository.search(queryStringQuery(query)).spliterator(), false)
        .collect(Collectors.toList());
    }
}
