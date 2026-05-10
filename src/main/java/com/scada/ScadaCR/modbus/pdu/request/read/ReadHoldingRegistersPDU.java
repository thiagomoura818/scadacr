package com.scada.ScadaCR.modbus.pdu.request.read;

import com.scada.ScadaCR.modbus.exceptions.ModbusCommunicationException;
import com.scada.ScadaCR.modbus.pdu.request.ModbusRequest;
import com.scada.ScadaCR.modbus.pdu.response.read.HoldingRegistersResponse;
import com.scada.ScadaCR.modbus.pdu.response.ModbusResponse;
import com.scada.ScadaCR.modbus.services.ModbusClientService;

import java.util.ArrayList;

public class ReadHoldingRegistersPDU extends ModbusRequest {
    private int quantity;

    public ReadHoldingRegistersPDU(int address, int quantity, int unitId){
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
    public ModbusResponse<ArrayList<Integer>> execute(ModbusClientService service) {
        ModbusResponse<ArrayList<Integer>> modbusResponse;
        try {
            modbusResponse = service.readHoldingRegisters(this);
        } catch (ModbusCommunicationException e) {
            modbusResponse = new HoldingRegistersResponse(e);
        }

        return modbusResponse;
    }

}
