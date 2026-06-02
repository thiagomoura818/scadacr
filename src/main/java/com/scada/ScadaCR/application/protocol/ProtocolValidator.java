package com.scada.ScadaCR.application.protocol;

import com.scada.ScadaCR.application.model.enums.ProtocolType;

import java.util.Map;

public interface ProtocolValidator {
    ProtocolType getSupportedProtocol();

    void validate(Map<String, Object> configMap);
}
