package com.scada.ScadaCR.modbus.pdu.request.read;

import com.scada.ScadaCR.modbus.exceptions.ModbusCommunicationException;
import com.scada.ScadaCR.modbus.pdu.request.ModbusRequest;
import com.scada.ScadaCR.modbus.pdu.response.read.InputRegistersResponse;
import com.scada.ScadaCR.modbus.pdu.response.ModbusResponse;
import com.scada.ScadaCR.modbus.services.ModbusClientService;

import java.util.ArrayList;

public class ReadInputRegistersPDU extends ModbusRequest {
    private int quantity;

    public ReadInputRegistersPDU(int address, int quantity, int unitId){
        super(unitId, address);
        if(quantity < 1)
            throw new IllegalArgumentException("Erro, valores fora do escopo");
        this.quantity = quantity;
    }

    public void setQuantity(int quantity){
        this.quantity = quantity;
    }

    public int getQuantity(){
        return this.quantity;
    }

    @Override
    public ModbusResponse<ArrayList<Integer>> execute(ModbusClientService service) {
        ModbusResponse<ArrayList<Integer>> modbusResponse;
        try {
            modbusResponse = service.readInputRegisters(this);
        } catch (ModbusCommunicationException e) {
            modbusResponse = new InputRegistersResponse(e);
        }

        return modbusResponse;
    }
}
