package infrastructure.modbus.entities.request;

public abstract class ModbusRequest {
    private int unitId;
    private int address;

    public ModbusRequest(int unitId, int address){
        if(address < 0)
            throw new IllegalArgumentException("Erro, valores fora do escopo");
        this.address = address;
        this.unitId = unitId;
    }

    public int getUnitId(){
        return this.unitId;
    }

    public void setUnitId(int unitId){
        this.unitId = unitId;
    }

    public int getAddress(){
        return this.address;
    }

    public void setAddress(int address){
        this.address = address;
    }
}
