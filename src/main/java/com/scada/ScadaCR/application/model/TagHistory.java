package com.scada.ScadaCR.application.model;

import com.scada.ScadaCR.application.model.enums.TagQuality;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(
        name = "tag_history",
        indexes = {
                @Index(name = "idx_tag_history_tag_time", columnList = "tag_id, timestamp")
        }
)
public class TagHistory {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;

    @Column(nullable = false)
    private String value;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TagQuality quality;

    @Column(nullable = false)
    private Instant timestamp;
}
