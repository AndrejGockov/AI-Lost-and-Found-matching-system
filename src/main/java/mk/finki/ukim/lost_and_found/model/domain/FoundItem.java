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
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "found_item")
public class FoundItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "poster_id", nullable = false)
    private User poster;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "holder_id", nullable = false)
    private User holder;

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

    @Column(name = "date_found")
    private Instant dateFound;

    @Column(name = "location_found", columnDefinition = "TEXT")
    private String locationFound;

    @Column(name = "is_identifiable", nullable = false)
    private boolean identifiable = false;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, columnDefinition = "item_status")
    private ItemStatus status = ItemStatus.UNRESOLVED;

    // Postgres "float[]" == double precision[]
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "float[]")
    private double[] embedding;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FoundItemImage> images = new ArrayList<>();

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