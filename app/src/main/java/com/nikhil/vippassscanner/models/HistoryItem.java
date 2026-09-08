package com.nikhil.vippassscanner.models;

public class HistoryItem {

    private String passCode;
    private String scannedBy;
    private String scannedAt;
    private String status;

    // Required for Firebase
    public HistoryItem() {
    }

    public HistoryItem(String passCode, String scannedBy, String scannedAt, String status) {
        this.passCode = passCode;
        this.scannedBy = scannedBy;
        this.scannedAt = scannedAt;
        this.status = status;
    }

    public String getPassCode() {
        return passCode;
    }

    public void setPassCode(String passCode) {
        this.passCode = passCode;
    }

    public String getScannedBy() {
        return scannedBy;
    }

    public void setScannedBy(String scannedBy) {
        this.scannedBy = scannedBy;
    }

    public String getScannedAt() {
        return scannedAt;
    }

    public void setScannedAt(String scannedAt) {
        this.scannedAt = scannedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}