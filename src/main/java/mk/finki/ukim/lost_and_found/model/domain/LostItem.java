package mk.finki.ukim.lost_and_found.model.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mk.finki.ukim.lost_and_found.model.enums.ItemStatus;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "lost_item")
public class LostItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "poster_id", nullable = false)
    private User poster;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "date_posted", nullable = false, updatable = false)
    private Instant datePosted;

    @Column(name = "last_updated", nullable = false)
    private Instant lastUpdated;

    @Column(name = "is_updated", nullable = false)
    private boolean updated = false;

    @Column(name = "date_lost")
    private Instant dateLost;

    @Column(name = "location_lost", columnDefinition = "TEXT")
    private String locationLost;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, columnDefinition = "item_status")
    private ItemStatus status = ItemStatus.UNRESOLVED;

    // Postgres "float[]" == double precision[]
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "float[]")
    private double[] embedding;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        datePosted = now;
        lastUpdated = now;
    }

    @PreUpdate
    void onUpdate() {
        lastUpdated = Instant.now();
        updated = true;
    }
}