package com.scada.ScadaCR.modbus.controller.dto;

public record LedControlRequest(String state) {
    public boolean isOn() {
        return "on".equalsIgnoreCase(state);
    }
}
