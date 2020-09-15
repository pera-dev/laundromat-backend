package com.laundromat.app.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.laundromat.app.web.rest.TestUtil;

public class LaundryOrderTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(LaundryOrder.class);
        LaundryOrder laundryOrder1 = new LaundryOrder();
        laundryOrder1.setId(1L);
        LaundryOrder laundryOrder2 = new LaundryOrder();
        laundryOrder2.setId(laundryOrder1.getId());
        assertThat(laundryOrder1).isEqualTo(laundryOrder2);
        laundryOrder2.setId(2L);
        assertThat(laundryOrder1).isNotEqualTo(laundryOrder2);
        laundryOrder1.setId(null);
        assertThat(laundryOrder1).isNotEqualTo(laundryOrder2);
    }
}
