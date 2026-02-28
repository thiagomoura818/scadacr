package infrastructure.modbus.entities.request.read;

import infrastructure.modbus.entities.request.ModbusRequest;

public class ReadInputRegisters extends ModbusRequest {
    private int quantity;

    public ReadInputRegisters(int address, int quantity, int unitId){
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
}
