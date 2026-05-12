package com.scada.ScadaCR.modbus.exceptions.state;

public class ModbusStateStoreException extends Exception{
    public ModbusStateStoreException(String message) {
        super(message);
    }

    public ModbusStateStoreException(String message, Throwable cause) {
        super(message, cause);
    }

}
