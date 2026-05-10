package com.scada.ScadaCR.modbus.pdu.response.read;

import com.scada.ScadaCR.modbus.exceptions.ModbusCommunicationException;
import com.scada.ScadaCR.modbus.pdu.response.ModbusResponse;

import java.util.ArrayList;

public class CoilsResponse extends ModbusResponse<ArrayList<Boolean>> {

    public CoilsResponse(ArrayList<Boolean> response){
        this.setResponse(response);
        this.setSuccess(true);
    }
    public CoilsResponse(ModbusCommunicationException e){
        this.setError(e);
        this.setSuccess(false);
    }

}
