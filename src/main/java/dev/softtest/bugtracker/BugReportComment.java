package dev.softtest.bugtracker;

import java.util.Date;

public class BugReportComment {
    private final String author;
    private final Date created;
    private String comment;
    private Date updated;

    private BugReportComment(Builder builder) {
        this.author = builder.author;
        this.created = builder.created;
        this.comment = builder.comment;
        this.updated = builder.updated;
    }

    public String getAuthor() { return author; }
    public Date getCreated() { return created; }
    public String getComment() { return comment; }
    public Date getUpdated() { return updated; }

    public static class Builder {
        private final String author;
        private final Date created;
        private String comment;
        private Date updated;

        public Builder(String author, Date created) {
            this.author = author;
            this.created = created;
        }

        public Builder comment(String comment) {
            this.comment = comment;
            return this;
        }

        public Builder updated(Date updated){
            this.updated = updated;
            return this;
        }

        public BugReportComment build() {
            return new BugReportComment(this);
        }
    }
}
