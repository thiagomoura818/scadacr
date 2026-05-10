package com.scada.ScadaCR.modbus.pdu.response.write;

import com.scada.ScadaCR.modbus.exceptions.ModbusCommunicationException;
import com.scada.ScadaCR.modbus.pdu.response.ModbusResponse;

public class WriteResponse extends ModbusResponse<Void> {
    public WriteResponse(boolean response){
        this.setSuccess(true);
    }

    public WriteResponse(ModbusCommunicationException e){
        this.setSuccess(false);
        this.setError(e);
    }
}
