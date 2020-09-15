package com.laundromat.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.laundromat.app.domain.enumeration.OrderStatus;

/**
 * A LaundryOrder.
 */
@Entity
@Table(name = "laundry_order")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "laundryorder")
public class LaundryOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id")
    private String orderID;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status")
    private OrderStatus orderStatus;

    @Column(name = "selected_slot")
    private LocalDate selectedSlot;

    @Column(name = "placed_at")
    private String placedAt;

    @Column(name = "placed_on")
    private LocalDate placedOn;

    @Column(name = "delivered_at")
    private String deliveredAt;

    @Column(name = "delivered_on")
    private LocalDate deliveredOn;

    @OneToOne
    @JoinColumn(unique = true)
    private Payment payment;

    @OneToMany(mappedBy = "laundryOrder")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<LaundryItem> laundryItems = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = "laundryOrders", allowSetters = true)
    private UserAccount userAccount;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderID() {
        return orderID;
    }

    public LaundryOrder orderID(String orderID) {
        this.orderID = orderID;
        return this;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public LaundryOrder orderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
        return this;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public LocalDate getSelectedSlot() {
        return selectedSlot;
    }

    public LaundryOrder selectedSlot(LocalDate selectedSlot) {
        this.selectedSlot = selectedSlot;
        return this;
    }

    public void setSelectedSlot(LocalDate selectedSlot) {
        this.selectedSlot = selectedSlot;
    }

    public String getPlacedAt() {
        return placedAt;
    }

    public LaundryOrder placedAt(String placedAt) {
        this.placedAt = placedAt;
        return this;
    }

    public void setPlacedAt(String placedAt) {
        this.placedAt = placedAt;
    }

    public LocalDate getPlacedOn() {
        return placedOn;
    }

    public LaundryOrder placedOn(LocalDate placedOn) {
        this.placedOn = placedOn;
        return this;
    }

    public void setPlacedOn(LocalDate placedOn) {
        this.placedOn = placedOn;
    }

    public String getDeliveredAt() {
        return deliveredAt;
    }

    public LaundryOrder deliveredAt(String deliveredAt) {
        this.deliveredAt = deliveredAt;
        return this;
    }

    public void setDeliveredAt(String deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public LocalDate getDeliveredOn() {
        return deliveredOn;
    }

    public LaundryOrder deliveredOn(LocalDate deliveredOn) {
        this.deliveredOn = deliveredOn;
        return this;
    }

    public void setDeliveredOn(LocalDate deliveredOn) {
        this.deliveredOn = deliveredOn;
    }

    public Payment getPayment() {
        return payment;
    }

    public LaundryOrder payment(Payment payment) {
        this.payment = payment;
        return this;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public Set<LaundryItem> getLaundryItems() {
        return laundryItems;
    }

    public LaundryOrder laundryItems(Set<LaundryItem> laundryItems) {
        this.laundryItems = laundryItems;
        return this;
    }

    public LaundryOrder addLaundryItem(LaundryItem laundryItem) {
        this.laundryItems.add(laundryItem);
        laundryItem.setLaundryOrder(this);
        return this;
    }

    public LaundryOrder removeLaundryItem(LaundryItem laundryItem) {
        this.laundryItems.remove(laundryItem);
        laundryItem.setLaundryOrder(null);
        return this;
    }

    public void setLaundryItems(Set<LaundryItem> laundryItems) {
        this.laundryItems = laundryItems;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public LaundryOrder userAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
        return this;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LaundryOrder)) {
            return false;
        }
        return id != null && id.equals(((LaundryOrder) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LaundryOrder{" +
            "id=" + getId() +
            ", orderID='" + getOrderID() + "'" +
            ", orderStatus='" + getOrderStatus() + "'" +
            ", selectedSlot='" + getSelectedSlot() + "'" +
            ", placedAt='" + getPlacedAt() + "'" +
            ", placedOn='" + getPlacedOn() + "'" +
            ", deliveredAt='" + getDeliveredAt() + "'" +
            ", deliveredOn='" + getDeliveredOn() + "'" +
            "}";
    }
}
