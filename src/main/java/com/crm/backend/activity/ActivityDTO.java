package com.crm.backend.activity;

public class ActivityDTO {
    private Long id;
    private String customerId;
    private String title;
    private String description;
    private ActivityType type;
    private String createdAt;

    public ActivityDTO() {}

    public ActivityDTO(Activity activity) {
        this.id = activity.getId();
        this.customerId = activity.getCustomerId();
        this.title = activity.getTitle();
        this.description = activity.getDescription();
        this.type = activity.getType();
        this.createdAt = activity.getCreatedAt().toString();
    }

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ActivityType getType() {
        return type;
    }

    public void setType(ActivityType type) {
        this.type = type;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
