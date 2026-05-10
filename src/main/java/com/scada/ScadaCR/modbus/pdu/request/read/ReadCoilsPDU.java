package com.scada.ScadaCR.modbus.pdu.request.read;

import com.scada.ScadaCR.modbus.exceptions.ModbusCommunicationException;
import com.scada.ScadaCR.modbus.pdu.request.ModbusRequest;
import com.scada.ScadaCR.modbus.pdu.response.read.CoilsResponse;
import com.scada.ScadaCR.modbus.pdu.response.ModbusResponse;
import com.scada.ScadaCR.modbus.services.ModbusClientService;

import java.util.ArrayList;

public class ReadCoilsPDU extends ModbusRequest {
    private int quantity;

    public ReadCoilsPDU(int address, int quantity, int unitId){
        super(unitId, address);

        if(quantity < 1)
            throw new IllegalArgumentException("Erro, valores fora do escopo");
        this.quantity = quantity;
    }

    public int getQuantity(){
        return this.quantity;
    }
    public void setQuantity(int quantity){
        if(quantity < 1)
            throw new IllegalArgumentException("Erro, valores fora do escopo");
        this.quantity = quantity;
    }

    @Override
    public ModbusResponse<ArrayList<Boolean>> execute(ModbusClientService service) {
        ModbusResponse<ArrayList<Boolean>> modbusResponse;
        try {
            modbusResponse = service.readCoils(this);
        } catch (ModbusCommunicationException e) {
            modbusResponse = new CoilsResponse(e);
        }

        return modbusResponse;
    }

}
