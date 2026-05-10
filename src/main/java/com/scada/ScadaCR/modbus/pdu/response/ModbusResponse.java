package com.scada.ScadaCR.modbus.pdu.response;

import com.scada.ScadaCR.modbus.exceptions.ModbusCommunicationException;

public abstract class ModbusResponse<T> {
    private T response;
    private boolean success;
    private ModbusCommunicationException error;

    public ModbusResponse() {}

    public void setResponse(T response){
        this.response = response;
    }
    public void setError(ModbusCommunicationException e){
        this.error = e;
    }
    public ModbusCommunicationException getError(){
        return this.error;
    }
    public boolean isSuccess(){
        return this.success;
    }
    public void setSuccess(boolean success){
        this.success = success;
    }

    public T getResponse(){
        return this.response;
    }
}
