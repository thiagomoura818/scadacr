package com.scada.ScadaCR.modbus.pdu.request.write;

import com.scada.ScadaCR.modbus.exceptions.ModbusCommunicationException;
import com.scada.ScadaCR.modbus.pdu.request.ModbusRequest;
import com.scada.ScadaCR.modbus.pdu.response.ModbusResponse;
import com.scada.ScadaCR.modbus.pdu.response.write.WriteResponse;
import com.scada.ScadaCR.modbus.services.ModbusClientService;

public class WriteSingleCoilPDU extends ModbusRequest {
    private boolean value;

    public WriteSingleCoilPDU(int address, boolean value, int unitId){
        super(unitId, address);
        this.value = value;
    }

    public void setValue(boolean value){
        this.value = value;
    }

    public boolean getValue(){
        return this.value;
    }

    @Override
    public ModbusResponse<Void> execute(ModbusClientService service){
        ModbusResponse<Void> modbusResponse;
        try{
            modbusResponse = service.writeSingleCoil(this);
        }catch(ModbusCommunicationException e){
            modbusResponse = new WriteResponse(e);
        }

        return modbusResponse;
    }
}
