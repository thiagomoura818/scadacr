package com.scada.ScadaCR.modbus.exceptions;

public class ModbusCommunicationException extends Exception{
    public ModbusCommunicationException(String message) {
        super(message);
    }

    public ModbusCommunicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
