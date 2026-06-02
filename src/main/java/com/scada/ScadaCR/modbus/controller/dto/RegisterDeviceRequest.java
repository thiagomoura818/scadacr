package com.scada.ScadaCR.modbus.controller.dto;

public record RegisterDeviceRequest(
        String deviceId,
        String host,
        Integer port
) {
}
