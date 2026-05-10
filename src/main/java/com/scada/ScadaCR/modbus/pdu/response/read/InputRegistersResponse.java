package com.scada.ScadaCR.modbus.pdu.response.read;

import com.scada.ScadaCR.modbus.exceptions.ModbusCommunicationException;
import com.scada.ScadaCR.modbus.pdu.response.ModbusResponse;

import java.util.ArrayList;

public class InputRegistersResponse extends ModbusResponse<ArrayList<Integer>> {
    public InputRegistersResponse(ArrayList<Integer> response){
        this.setResponse(response);
        this.setSuccess(true);
    }

    public InputRegistersResponse(ModbusCommunicationException e){
        this.setError(e);
        this.setSuccess(false);
    }

}
