package infrastructure.modbus.entities.request.read;

import infrastructure.modbus.entities.request.ModbusRequest;

public class ReadCoilsDTO extends ModbusRequest {
    private int quantity;

    public ReadCoilsDTO(int address, int quantity, int unitId){
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

}
