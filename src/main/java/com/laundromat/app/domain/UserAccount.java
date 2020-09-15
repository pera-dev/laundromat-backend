package com.laundromat.app.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

import javax.persistence.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.laundromat.app.domain.enumeration.AccountType;

import com.laundromat.app.domain.enumeration.PaymentMethod;

/**
 * A UserAccount.
 */
@Entity
@Table(name = "user_account")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "useraccount")
public class UserAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Type(type = "uuid-char")
    @Column(name = "account_uuid", length = 36)
    private UUID accountUUID;

    @Column(name = "account_name")
    private String accountName;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "account_email")
    private String accountEmail;

    @Column(name = "mobile_no")
    private String mobileNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type")
    private AccountType accountType;

    @Column(name = "last_login")
    private LocalDate lastLogin;

    @Enumerated(EnumType.STRING)
    @Column(name = "default_payment_method")
    private PaymentMethod defaultPaymentMethod;

    @OneToOne
    @JoinColumn(unique = true)
    private User user;

    @OneToMany(mappedBy = "userAccount")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<LaundryOrder> laundryOrders = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getAccountUUID() {
        return accountUUID;
    }

    public UserAccount accountUUID(UUID accountUUID) {
        this.accountUUID = accountUUID;
        return this;
    }

    public void setAccountUUID(UUID accountUUID) {
        this.accountUUID = accountUUID;
    }

    public String getAccountName() {
        return accountName;
    }

    public UserAccount accountName(String accountName) {
        this.accountName = accountName;
        return this;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public UserAccount companyName(String companyName) {
        this.companyName = companyName;
        return this;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getAccountEmail() {
        return accountEmail;
    }

    public UserAccount accountEmail(String accountEmail) {
        this.accountEmail = accountEmail;
        return this;
    }

    public void setAccountEmail(String accountEmail) {
        this.accountEmail = accountEmail;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public UserAccount mobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
        return this;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public UserAccount accountType(AccountType accountType) {
        this.accountType = accountType;
        return this;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public LocalDate getLastLogin() {
        return lastLogin;
    }

    public UserAccount lastLogin(LocalDate lastLogin) {
        this.lastLogin = lastLogin;
        return this;
    }

    public void setLastLogin(LocalDate lastLogin) {
        this.lastLogin = lastLogin;
    }

    public PaymentMethod getDefaultPaymentMethod() {
        return defaultPaymentMethod;
    }

    public UserAccount defaultPaymentMethod(PaymentMethod defaultPaymentMethod) {
        this.defaultPaymentMethod = defaultPaymentMethod;
        return this;
    }

    public void setDefaultPaymentMethod(PaymentMethod defaultPaymentMethod) {
        this.defaultPaymentMethod = defaultPaymentMethod;
    }

    public User getUser() {
        return user;
    }

    public UserAccount user(User user) {
        this.user = user;
        return this;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<LaundryOrder> getLaundryOrders() {
        return laundryOrders;
    }

    public UserAccount laundryOrders(Set<LaundryOrder> laundryOrders) {
        this.laundryOrders = laundryOrders;
        return this;
    }

    public UserAccount addLaundryOrder(LaundryOrder laundryOrder) {
        this.laundryOrders.add(laundryOrder);
        laundryOrder.setUserAccount(this);
        return this;
    }

    public UserAccount removeLaundryOrder(LaundryOrder laundryOrder) {
        this.laundryOrders.remove(laundryOrder);
        laundryOrder.setUserAccount(null);
        return this;
    }

    public void setLaundryOrders(Set<LaundryOrder> laundryOrders) {
        this.laundryOrders = laundryOrders;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserAccount)) {
            return false;
        }
        return id != null && id.equals(((UserAccount) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserAccount{" +
            "id=" + getId() +
            ", accountUUID='" + getAccountUUID() + "'" +
            ", accountName='" + getAccountName() + "'" +
            ", companyName='" + getCompanyName() + "'" +
            ", accountEmail='" + getAccountEmail() + "'" +
            ", mobileNo='" + getMobileNo() + "'" +
            ", accountType='" + getAccountType() + "'" +
            ", lastLogin='" + getLastLogin() + "'" +
            ", defaultPaymentMethod='" + getDefaultPaymentMethod() + "'" +
            "}";
    }
}
