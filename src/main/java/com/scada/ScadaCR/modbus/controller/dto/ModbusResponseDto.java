package com.scada.ScadaCR.modbus.controller.dto;

import com.scada.ScadaCR.modbus.exceptions.ModbusCommunicationException;
import com.scada.ScadaCR.modbus.pdu.response.ModbusResponse;

public record ModbusResponseDto(
        boolean success,
        Object data,
        String error
) {
    public static ModbusResponseDto from(ModbusResponse<?> response) {
        return new ModbusResponseDto(
                response.isSuccess(),
                response.getResponse(),
                errorMessage(response.getError())
        );
    }

    private static String errorMessage(ModbusCommunicationException error) {
        return error == null ? null : error.getMessage();
    }
}
