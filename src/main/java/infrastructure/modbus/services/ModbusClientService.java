package infrastructure.modbus.services;

import com.digitalpetri.modbus.client.ModbusTcpClient;
import com.digitalpetri.modbus.exceptions.ModbusConnectException;
import com.digitalpetri.modbus.exceptions.ModbusExecutionException;
import com.digitalpetri.modbus.exceptions.ModbusResponseException;
import com.digitalpetri.modbus.exceptions.ModbusTimeoutException;
import com.digitalpetri.modbus.pdu.*;
import com.digitalpetri.modbus.tcp.client.NettyTcpClientTransport;
import infrastructure.modbus.exceptions.ModbusCommunicationException;

import java.util.ArrayList;

public class ModbusClientService {
    private ModbusTcpClient client;

    public ModbusClientService(){

    }

    public void createConnection(String ip, Integer port) throws ModbusCommunicationException {
        if(client!=null)
            throw new IllegalStateException("Cliente já está conectado.");

        var transport = NettyTcpClientTransport.create(cfg -> {
            cfg.setHostname(ip);
            cfg.setPort(port);
        });

        try{
            client = ModbusTcpClient.create(transport);
            client.connect();

        }catch(ModbusExecutionException e){
            throw new ModbusCommunicationException("Erro ao criar conexao com client", e);
        }
    }

    public void disconnect() throws ModbusCommunicationException {
        try{
            client.disconnect();

        }catch(ModbusExecutionException e){
            throw new ModbusCommunicationException("Erro ao desconectar client", e);
        }
    }

    public boolean isConnected(){
        return client.isConnected();
    }

    public ArrayList<Boolean> readCoils(Integer address, Integer quantity, Integer unitId) throws ModbusCommunicationException {
        ArrayList<Boolean> responseBits = new ArrayList<>();

        try{
            ReadCoilsResponse response = client.readCoils(unitId, new ReadCoilsRequest(address, quantity));
            byte[] coils = response.coils();

            for(int i = 0; i < quantity; i++){
                responseBits.add(i, getBitFromByte(i%8, coils[i/8]));
            }
        }catch(ModbusExecutionException | ModbusResponseException | ModbusTimeoutException e){
            throw new ModbusCommunicationException("Erro ao ler coils.", e);
        }

        return responseBits;
    }

    public void writeSingleCoil(int address, int value, int unitId) throws ModbusCommunicationException {
        try{
            WriteSingleCoilResponse response = client.writeSingleCoil(unitId, new WriteSingleCoilRequest(address, value));
        }catch(ModbusExecutionException | ModbusResponseException | ModbusTimeoutException e){
            throw new ModbusCommunicationException("Falha ao escrever um coil. ",e);
        }
    }

    public void writeSingleRegister(int address, int value, int unitId) throws ModbusCommunicationException {
        try{
            WriteSingleRegisterResponse response = client.writeSingleRegister(unitId, new WriteSingleRegisterRequest(address, value));
        }catch(ModbusExecutionException | ModbusResponseException | ModbusTimeoutException e){
            throw new ModbusCommunicationException("Falha ao escrever um coil. ",e);
        }
    }

    public ArrayList<Integer> readHoldingRegisters(int address, int quantity, int unitId) throws ModbusCommunicationException {
        ArrayList<Integer> responseRegisters = new ArrayList<Integer>();

        try{
            ReadHoldingRegistersResponse response = client.readHoldingRegisters(unitId, new ReadHoldingRegistersRequest(address, quantity));
            byte[] bytes = response.registers();
            for(int i = 0; i < bytes.length; i+=2){
                int value = getValueFromByte(bytes[i], bytes[i+1]);
                responseRegisters.add(value);
            }
        }catch(ModbusExecutionException | ModbusResponseException | ModbusTimeoutException e){
            throw new ModbusCommunicationException("Falha ao ler holding registers", e);
        }

        return responseRegisters;
    }

    public ArrayList<Integer> readInputRegisters(int address, int quantity, int unitId) throws ModbusCommunicationException {
        ArrayList<Integer> responseRegisters = new ArrayList<Integer>();

        try{
            ReadInputRegistersResponse response = client.readInputRegisters(unitId, new ReadInputRegistersRequest(address, quantity));
            byte[] bytes = response.registers();
            for(int i = 0; i < bytes.length; i+=2){
                int value = getValueFromByte(bytes[i], bytes[i+1]);
                responseRegisters.add(value);
            }
        }catch(ModbusExecutionException | ModbusResponseException | ModbusTimeoutException e){
            throw new ModbusCommunicationException("Falha ao ler holding registers", e);
        }

        return responseRegisters;
    }



    private boolean getBitFromByte(int pos, byte number){
        return ((number >> pos) & 1) == 1;
    }
    private int getValueFromByte(byte lbyte, byte hbyte){ return (((hbyte & 0xFF) << 8) | (lbyte & 0xFF));}
}
