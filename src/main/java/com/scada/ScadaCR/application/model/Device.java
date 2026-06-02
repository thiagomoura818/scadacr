package com.scada.ScadaCR.application.model;

import com.scada.ScadaCR.application.model.enums.ProtocolType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="device")
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="device_id", nullable = false, unique = true)
    private String deviceId;

    @Column(name="name", nullable = false, unique=true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name="protocol", nullable = false)
    private ProtocolType protocol;

    @Column(name="enabled")
    private boolean enabled;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private Map<String, Object> protocolConfig;
}
