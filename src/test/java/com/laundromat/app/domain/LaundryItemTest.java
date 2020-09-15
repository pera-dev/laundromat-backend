package com.laundromat.app.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.laundromat.app.web.rest.TestUtil;

public class LaundryItemTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(LaundryItem.class);
        LaundryItem laundryItem1 = new LaundryItem();
        laundryItem1.setId(1L);
        LaundryItem laundryItem2 = new LaundryItem();
        laundryItem2.setId(laundryItem1.getId());
        assertThat(laundryItem1).isEqualTo(laundryItem2);
        laundryItem2.setId(2L);
        assertThat(laundryItem1).isNotEqualTo(laundryItem2);
        laundryItem1.setId(null);
        assertThat(laundryItem1).isNotEqualTo(laundryItem2);
    }
}
