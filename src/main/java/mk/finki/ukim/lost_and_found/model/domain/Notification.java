package mk.finki.ukim.lost_and_found.model.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "lost_found_notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "date_created", nullable = false)
    private Instant dateCreated;

    @Column(name = "is_viewed", nullable = false)
    private boolean viewed = false;

    @Column(name = "link")
    private String link;

    @PrePersist
    void onCreate() {
        dateCreated = Instant.now();
    }
}