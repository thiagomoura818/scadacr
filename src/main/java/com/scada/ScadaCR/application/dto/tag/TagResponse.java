package com.scada.ScadaCR.application.dto.tag;

import com.scada.ScadaCR.application.model.enums.TagAccessMode;
import com.scada.ScadaCR.application.model.enums.TagDataType;

import java.util.Map;

public record TagResponse(
        Long id,
        Long deviceId,
        String name,
        String displayName,
        TagDataType dataType,
        TagAccessMode accessMode,
        boolean scanEnabled,
        Long scanRateMs,
        boolean enabled,
        Map<String, Object> protocolConfig
) {
}
