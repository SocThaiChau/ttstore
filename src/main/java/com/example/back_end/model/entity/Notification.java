package com.example.back_end.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private String title;

    private String message;
    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private User recipient;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    private boolean isRead;

    private Notification(String title, String message, User recipient, User sender, Date createdAt) {
        this.title = title;
        this.message = message;
        this.recipient = recipient;
        this.sender = sender;
        this.createdAt = createdAt;
        this.isRead = false;
    }

    public Notification() {
    }
    public static NotificationBuilder builder() {
        return new NotificationBuilder();
    }

    public static class NotificationBuilder {
        private String title;
        private String message;
        private User recipient;
        private User sender;
        private Date createdAt;

        private NotificationBuilder() {
        }

        public NotificationBuilder title(String title) {
            this.title = title;
            return this;
        }

        public NotificationBuilder message(String message) {
            this.message = message;
            return this;
        }

        public NotificationBuilder recipient(User recipient) {
            this.recipient = recipient;
            return this;
        }

        public NotificationBuilder sender(User sender) {
            this.sender = sender;
            return this;
        }

        public NotificationBuilder createdAt(Date createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Notification build() {
            return new Notification(this.title, this.message, this.recipient, this.sender, this.createdAt);
        }
    }
}
