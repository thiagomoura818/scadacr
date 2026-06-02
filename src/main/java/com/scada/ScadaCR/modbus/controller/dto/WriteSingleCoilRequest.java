package com.scada.ScadaCR.modbus.controller.dto;

public record WriteSingleCoilRequest(
        int address,
        boolean value,
        int unitId
) {
}
