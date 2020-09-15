package com.laundromat.app.repository.search;

import com.laundromat.app.domain.Payment;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * Spring Data Elasticsearch repository for the {@link Payment} entity.
 */
public interface PaymentSearchRepository extends ElasticsearchRepository<Payment, Long> {
}
