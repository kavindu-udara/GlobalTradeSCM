package com.globaltrade.scms.core.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "inventory_levels", schema = "scm")
public class InventoryLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_level_id")
    private Long inventoryLevelId;

    @Column(name = "quantity_available", nullable = false)
    private Integer quantityAvailable;

    @Column(name = "reorder_level", nullable = false)
    private Integer reorderLevel;

    // Optimistic Locking: Prevents concurrent update conflicts
    @Version
    @Column(name = "version")
    private Integer version;

    public Long getInventoryLevelId() { return inventoryLevelId; }
    public void setInventoryLevelId(Long inventoryLevelId) { this.inventoryLevelId = inventoryLevelId; }
    public Integer getQuantityAvailable() { return quantityAvailable; }
    public void setQuantityAvailable(Integer quantityAvailable) { this.quantityAvailable = quantityAvailable; }
    public Integer getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(Integer reorderLevel) { this.reorderLevel = reorderLevel; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
}