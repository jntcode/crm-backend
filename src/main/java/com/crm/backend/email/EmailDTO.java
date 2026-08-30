package com.crm.backend.email;

public class EmailDTO {
    private Long id;
    private String customerId;
    private String subject;
    private String body;
    private String fromAddress;
    private String toAddress;
    private EmailStatus status;
    private String sentAt;
    private String createdAt;

    public EmailDTO() {}

    public EmailDTO(Email email) {
        this.id = email.getId();
        this.customerId = email.getCustomerId();
        this.subject = email.getSubject();
        this.body = email.getBody();
        this.fromAddress = email.getFromAddress();
        this.toAddress = email.getToAddress();
        this.status = email.getStatus();
        this.sentAt = email.getSentAt().toString();
        this.createdAt = email.getCreatedAt().toString();
    }

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public EmailStatus getStatus() {
        return status;
    }

    public void setStatus(EmailStatus status) {
        this.status = status;
    }

    public String getSentAt() {
        return sentAt;
    }

    public void setSentAt(String sentAt) {
        this.sentAt = sentAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
