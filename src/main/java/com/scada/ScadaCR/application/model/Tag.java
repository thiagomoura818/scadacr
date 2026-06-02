package com.scada.ScadaCR.application.model;

import com.scada.ScadaCR.application.model.enums.TagAccessMode;
import com.scada.ScadaCR.application.model.enums.TagDataType;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import javax.annotation.processing.Generated;
import java.util.Map;

@Entity
@Table(name="tag")
public class Tag {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="device_id", nullable=false)
    private Device device;

    @Column(nullable=false)
    private String name;

    @Column(nullable=false)
    private String displayName;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private TagDataType dataType;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private TagAccessMode accessMode;

    @Column(nullable=false)
    private boolean scanEnabled;

    @Column(nullable=false)
    private Long scanRateMs;

    @Column(nullable=false)
    private boolean enabled;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private Map<String, Object> protocolConfig;

}
