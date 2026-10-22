package mk.finki.ukim.lost_and_found.model.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "found_item_image")
public class FoundItemImage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    // NOTE: the SQL currently points this FK at lost_item
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private FoundItem item;

    @Column(name = "size_bytes", nullable = false)
    private int sizeBytes;

    @JdbcTypeCode(SqlTypes.VARBINARY)
    @Column(name = "img_bytes", nullable = false)
    private byte[] imgBytes;
}