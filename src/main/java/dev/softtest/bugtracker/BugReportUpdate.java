package dev.softtest.bugtracker;

import java.util.Date;

public class BugReportUpdate {
    private final String author;
    private final Date created;
    private String update;

    private BugReportUpdate(Builder builder) {
        this.author = builder.author;
        this.created = builder.created;
        this.update = builder.update;
    }

    public String getAuthor() { return author; }
    public Date getCreated() { return created; }
    public String getUpdate() { return update; }

    public static class Builder {
        private final String author;
        private final Date created;
        private String update;

        public Builder(String author, Date created) {
            this.author = author;
            this.created = created;
        }

        public Builder update(String update) {
            this.update = update;
            return this;
        }

        public BugReportUpdate build() {
            return new BugReportUpdate(this);
        }
    }
}
