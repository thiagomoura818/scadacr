package com.scada.ScadaCR.application.dto.device;

import com.scada.ScadaCR.application.model.enums.ProtocolType;

import java.util.Map;

public record DeviceCreateRequest(
        String deviceId,
        String name,
        ProtocolType protocol,
        boolean enabled,
        Map<String, Object> protocolConfig
) {
}
