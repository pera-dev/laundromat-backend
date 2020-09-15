package com.laundromat.app.repository.search;

import com.laundromat.app.domain.UserAccount;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * Spring Data Elasticsearch repository for the {@link UserAccount} entity.
 */
public interface UserAccountSearchRepository extends ElasticsearchRepository<UserAccount, Long> {
}
