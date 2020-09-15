package com.laundromat.app.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.laundromat.app.web.rest.TestUtil;

public class UserAccountTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserAccount.class);
        UserAccount userAccount1 = new UserAccount();
        userAccount1.setId(1L);
        UserAccount userAccount2 = new UserAccount();
        userAccount2.setId(userAccount1.getId());
        assertThat(userAccount1).isEqualTo(userAccount2);
        userAccount2.setId(2L);
        assertThat(userAccount1).isNotEqualTo(userAccount2);
        userAccount1.setId(null);
        assertThat(userAccount1).isNotEqualTo(userAccount2);
    }
}
