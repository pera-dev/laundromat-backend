package com.laundromat.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

import javax.persistence.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;
import java.util.UUID;

import com.laundromat.app.domain.enumeration.PaymentMethod;

import com.laundromat.app.domain.enumeration.PaymentStatus;

/**
 * A Payment.
 */
@Entity
@Table(name = "payment")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "payment")
public class Payment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_id")
    private Integer paymentId;

    @Type(type = "uuid-char")
    @Column(name = "payment_uuid", length = 36)
    private UUID paymentUUID;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;

    @Column(name = "payment_amount")
    private Float paymentAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus;

    @OneToOne(mappedBy = "payment")
    @JsonIgnore
    private LaundryOrder laundryOrder;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPaymentId() {
        return paymentId;
    }

    public Payment paymentId(Integer paymentId) {
        this.paymentId = paymentId;
        return this;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    public UUID getPaymentUUID() {
        return paymentUUID;
    }

    public Payment paymentUUID(UUID paymentUUID) {
        this.paymentUUID = paymentUUID;
        return this;
    }

    public void setPaymentUUID(UUID paymentUUID) {
        this.paymentUUID = paymentUUID;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public Payment paymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
        return this;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Float getPaymentAmount() {
        return paymentAmount;
    }

    public Payment paymentAmount(Float paymentAmount) {
        this.paymentAmount = paymentAmount;
        return this;
    }

    public void setPaymentAmount(Float paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public Payment paymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
        return this;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LaundryOrder getLaundryOrder() {
        return laundryOrder;
    }

    public Payment laundryOrder(LaundryOrder laundryOrder) {
        this.laundryOrder = laundryOrder;
        return this;
    }

    public void setLaundryOrder(LaundryOrder laundryOrder) {
        this.laundryOrder = laundryOrder;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Payment)) {
            return false;
        }
        return id != null && id.equals(((Payment) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Payment{" +
            "id=" + getId() +
            ", paymentId=" + getPaymentId() +
            ", paymentUUID='" + getPaymentUUID() + "'" +
            ", paymentMethod='" + getPaymentMethod() + "'" +
            ", paymentAmount=" + getPaymentAmount() +
            ", paymentStatus='" + getPaymentStatus() + "'" +
            "}";
    }
}
