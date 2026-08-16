package com.globaltrade.scms.core.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "vendors", schema = "scm")
public class Vendor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vendor_id")
    private Long vendorId;

    @Column(name = "vendor_name", nullable = false, length = 120)
    private String vendorName;

    @Column(name = "country", nullable = false, length = 80)
    private String country;

    @Column(name = "compliance_status", nullable = false, length = 30)
    private String complianceStatus;

    @Column(name = "performance_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal performanceScore;

    @Column(name = "active", nullable = false)
    private Boolean active;

    // Getters and Setters
    public Long getVendorId() { return vendorId; }
    public void setVendorId(Long vendorId) { this.vendorId = vendorId; }
    public String getVendorName() { return vendorName; }
    public void setVendorName(String vendorName) { this.vendorName = vendorName; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public String getComplianceStatus() { return complianceStatus; }
    public void setComplianceStatus(String complianceStatus) { this.complianceStatus = complianceStatus; }
    public BigDecimal getPerformanceScore() { return performanceScore; }
    public void setPerformanceScore(BigDecimal performanceScore) { this.performanceScore = performanceScore; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}