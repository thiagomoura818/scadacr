package com.scada.ScadaCR.modbus.pdu.response.read;

import com.scada.ScadaCR.modbus.exceptions.ModbusCommunicationException;
import com.scada.ScadaCR.modbus.pdu.response.ModbusResponse;

import java.util.ArrayList;

public class HoldingRegistersResponse extends ModbusResponse<ArrayList<Integer>> {

    public HoldingRegistersResponse(ArrayList<Integer> response){
        this.setResponse(response);
        this.setSuccess(true);
    }

    public HoldingRegistersResponse(ModbusCommunicationException e){
        this.setError(e);
        this.setSuccess(false);
    }

}
