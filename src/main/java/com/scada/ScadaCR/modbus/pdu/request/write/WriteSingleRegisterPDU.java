package com.scada.ScadaCR.modbus.pdu.request.write;

import com.scada.ScadaCR.modbus.exceptions.ModbusCommunicationException;
import com.scada.ScadaCR.modbus.pdu.request.ModbusRequest;
import com.scada.ScadaCR.modbus.pdu.response.ModbusResponse;
import com.scada.ScadaCR.modbus.pdu.response.write.WriteResponse;
import com.scada.ScadaCR.modbus.services.ModbusClientService;

public class WriteSingleRegisterPDU extends ModbusRequest {
    private int value;

    public WriteSingleRegisterPDU(int address, int value, int unitId){
        super(unitId, address);

        this.value = value;
    }

    public void setValue(int value){
        this.value = value;
    }

    public int getValue(){
        return this.value;
    }

    @Override
    public ModbusResponse<Void> execute(ModbusClientService service){
        ModbusResponse<Void> modbusResponse;
        try{
            modbusResponse = service.writeSingleRegister(this);
        }catch(ModbusCommunicationException e){
            modbusResponse = new WriteResponse(e);
        }

        return modbusResponse;
    }

}
