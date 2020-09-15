package com.laundromat.app.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link PaymentSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class PaymentSearchRepositoryMockConfiguration {

    @MockBean
    private PaymentSearchRepository mockPaymentSearchRepository;

}
