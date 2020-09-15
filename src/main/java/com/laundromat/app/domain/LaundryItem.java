package com.laundromat.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;

import com.laundromat.app.domain.enumeration.WashType;

/**
 * A LaundryItem.
 */
@Entity
@Table(name = "laundry_item")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "laundryitem")
public class LaundryItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_id")
    private Integer itemId;

    @Column(name = "item_name")
    private String itemName;

    @Enumerated(EnumType.STRING)
    @Column(name = "wash_type")
    private WashType washType;

    @Column(name = "item_qty")
    private Integer itemQty;

    @ManyToOne
    @JsonIgnoreProperties(value = "laundryItems", allowSetters = true)
    private LaundryOrder laundryOrder;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getItemId() {
        return itemId;
    }

    public LaundryItem itemId(Integer itemId) {
        this.itemId = itemId;
        return this;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public LaundryItem itemName(String itemName) {
        this.itemName = itemName;
        return this;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public WashType getWashType() {
        return washType;
    }

    public LaundryItem washType(WashType washType) {
        this.washType = washType;
        return this;
    }

    public void setWashType(WashType washType) {
        this.washType = washType;
    }

    public Integer getItemQty() {
        return itemQty;
    }

    public LaundryItem itemQty(Integer itemQty) {
        this.itemQty = itemQty;
        return this;
    }

    public void setItemQty(Integer itemQty) {
        this.itemQty = itemQty;
    }

    public LaundryOrder getLaundryOrder() {
        return laundryOrder;
    }

    public LaundryItem laundryOrder(LaundryOrder laundryOrder) {
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
        if (!(o instanceof LaundryItem)) {
            return false;
        }
        return id != null && id.equals(((LaundryItem) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LaundryItem{" +
            "id=" + getId() +
            ", itemId=" + getItemId() +
            ", itemName='" + getItemName() + "'" +
            ", washType='" + getWashType() + "'" +
            ", itemQty=" + getItemQty() +
            "}";
    }
}
