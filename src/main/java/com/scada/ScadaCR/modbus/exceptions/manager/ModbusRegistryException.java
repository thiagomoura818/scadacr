package com.scada.ScadaCR.modbus.exceptions.manager;

public class ModbusRegistryException extends Exception{
    public ModbusRegistryException(String message) {
        super(message);
    }

    public ModbusRegistryException(String message, Throwable cause) {
        super(message, cause);
    }
}
