package com.scada.ScadaCR.application.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name="tag_value")
public class TagValue {

    @Id
    private Long tagId;

    @OneToOne(fetch= FetchType.LAZY)
    @MapsId
    @JoinColumn(name="tag_id")
    private Tag tag;

    @Column(nullable=false)
    private String value;

    @Column(nullable=false)
    private Instant timeStamp;

    @Column(nullable=false)
    private String quality;


}
