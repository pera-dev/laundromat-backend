package com.laundromat.app.repository.search;

import com.laundromat.app.domain.LaundryItem;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * Spring Data Elasticsearch repository for the {@link LaundryItem} entity.
 */
public interface LaundryItemSearchRepository extends ElasticsearchRepository<LaundryItem, Long> {
}
