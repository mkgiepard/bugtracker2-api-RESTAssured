package dev.softtest.bugtracker;

import java.util.Date;

public class BugReport {
    private final int id;
    private final String author;
    private final Date created;
    private String title;
    private int priority;
    private String status;
    private String description;
    private Date updated;

    private BugReport(Builder builder) {
        this.id = builder.id;
        this.author = builder.author;
        this.created = builder.created;
        this.title = builder.title;
        this.priority = builder.priority;
        this.status = builder.status;
        this.description = builder.description;
        this.updated = builder.updated;
    }

    public int getId() { return id; }
    public String getAuthor() { return author; }
    public Date getCreated() { return created; }
    public String getTitle() { return title; }
    public int getPriority() { return priority; }
    public String getStatus() { return status; }
    public String getDescription() { return description; }
    public Date getUpdated() { return updated; }

    public static class Builder {
        private final int id;
        private final String author;
        private final Date created;
        private String title;
        private int priority;
        private String status;
        private String description;
        private Date updated;

        public Builder(int id, String author, Date created) {
            this.id = id;
            this.author = author;
            this.created = created;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder priority(int priority) {
            this.priority = priority;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder updated(Date updated){
            this.updated = updated;
            return this;
        }

        public BugReport build() {
            return new BugReport(this);
        }
    }
    
}
