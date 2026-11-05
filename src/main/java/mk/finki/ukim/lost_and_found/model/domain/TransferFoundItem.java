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
@Table(name = "transfer_found_item")
public class TransferFoundItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "found_item_id", nullable = false)
    private FoundItem foundItem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "new_holder_id", nullable = false)
    private User newHolder;

    @Column(name = "date_sent", nullable = false, updatable = false)
    private Instant dateSent;

    @Column(name = "date_responded")
    private Instant dateResponded;

    // NULL = pending, TRUE = accepted, FALSE = declined
    @Column(name = "is_accepted")
    private Boolean accepted;

    @PrePersist
    void onCreate() {
        if (dateSent == null) {
            dateSent = Instant.now();
        }
    }
}