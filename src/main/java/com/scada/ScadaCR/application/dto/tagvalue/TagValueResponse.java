package com.scada.ScadaCR.application.dto.tagvalue;

import com.scada.ScadaCR.application.model.enums.TagQuality;

import java.time.Instant;

public record TagValueResponse(
        Long tagId,
        String value,
        Instant timestamp,
        TagQuality quality
) {
}
