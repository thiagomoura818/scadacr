package com.scada.ScadaCR.modbus.controller.dto;

public record WriteSingleRegisterRequest(
        int address,
        int value,
        int unitId
) {
}
