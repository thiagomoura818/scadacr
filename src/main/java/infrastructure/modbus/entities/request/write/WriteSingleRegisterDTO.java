package infrastructure.modbus.entities.request.write;

import infrastructure.modbus.entities.request.ModbusRequest;

public class WriteSingleRegisterDTO extends ModbusRequest {
    private int value;

    public WriteSingleRegisterDTO(int address, int value, int unitId){
        super(unitId, address);

        this.value = value;
    }

    public void setValue(int value){
        this.value = value;
    }

    public int getValue(){
        return this.value;
    }

}
