package infrastructure.modbus.entities.request.write;

import infrastructure.modbus.entities.request.ModbusRequest;

public class WriteSingleCoilDTO extends ModbusRequest {
    private boolean value;

    public WriteSingleCoilDTO(int address, boolean value, int unitId){
        super(unitId, address);
        this.value = value;
    }

    public void setValue(boolean value){
        this.value = value;
    }

    public boolean getValue(){
        return this.value;
    }


}
