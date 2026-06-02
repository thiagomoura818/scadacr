package com.scada.ScadaCR.application.dto.taghistory;

import com.scada.ScadaCR.application.model.enums.TagQuality;

import java.time.Instant;

public record TagHistoryCreateRequest(
        Long tagId,
        String value,
        TagQuality quality,
        Instant timestamp
) {
}
