package com.laundromat.app.repository.search;

import com.laundromat.app.domain.LaundryOrder;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * Spring Data Elasticsearch repository for the {@link LaundryOrder} entity.
 */
public interface LaundryOrderSearchRepository extends ElasticsearchRepository<LaundryOrder, Long> {
}
