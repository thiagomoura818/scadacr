package infrastructure.modbus.services;

import com.digitalpetri.modbus.client.ModbusTcpClient;
import com.digitalpetri.modbus.exceptions.ModbusExecutionException;
import com.digitalpetri.modbus.exceptions.ModbusResponseException;
import com.digitalpetri.modbus.exceptions.ModbusTimeoutException;
import com.digitalpetri.modbus.pdu.*;
import com.digitalpetri.modbus.tcp.client.NettyTcpClientTransport;
import infrastructure.modbus.entities.request.read.ReadCoilsDTO;
import infrastructure.modbus.entities.request.read.ReadHoldingRegistersDTO;
import infrastructure.modbus.entities.request.read.ReadInputRegistersDTO;
import infrastructure.modbus.entities.request.write.WriteSingleCoilDTO;
import infrastructure.modbus.entities.request.write.WriteSingleRegisterDTO;
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

    public ArrayList<Boolean> readCoils(ReadCoilsDTO readCoilsDTO) throws ModbusCommunicationException {
        ArrayList<Boolean> responseBits = new ArrayList<>();

        try{
            ReadCoilsResponse response = client.readCoils(readCoilsDTO.getUnitId(), new ReadCoilsRequest(readCoilsDTO.getAddress(), readCoilsDTO.getQuantity()));
            byte[] coils = response.coils();

            for(int i = 0; i < readCoilsDTO.getQuantity(); i++){
                responseBits.add(i, getBitFromByte(i%8, coils[i/8]));
            }
        }catch(ModbusExecutionException | ModbusResponseException | ModbusTimeoutException e){
            throw new ModbusCommunicationException("Erro ao ler coils.", e);
        }

        return responseBits;
    }

    public void writeSingleCoil(WriteSingleCoilDTO writeSingleCoilDTO) throws ModbusCommunicationException {
        try{
            WriteSingleCoilResponse response = client.writeSingleCoil(writeSingleCoilDTO.getUnitId(), new WriteSingleCoilRequest(writeSingleCoilDTO.getAddress(), writeSingleCoilDTO.getValue()));
        }catch(ModbusExecutionException | ModbusResponseException | ModbusTimeoutException e){
            throw new ModbusCommunicationException("Falha ao escrever um coil. ",e);
        }
    }

    public void writeSingleRegister(WriteSingleRegisterDTO writeSingleRegDTO) throws ModbusCommunicationException {
        try{
            WriteSingleRegisterResponse response = client.writeSingleRegister(writeSingleRegDTO.getUnitId(), new WriteSingleRegisterRequest(writeSingleRegDTO.getAddress(), writeSingleRegDTO.getValue()));
        }catch(ModbusExecutionException | ModbusResponseException | ModbusTimeoutException e){
            throw new ModbusCommunicationException("Falha ao escrever um coil. ",e);
        }
    }

    public ArrayList<Integer> readHoldingRegisters(ReadHoldingRegistersDTO readHoldRegDTO) throws ModbusCommunicationException {
        ArrayList<Integer> responseRegisters = new ArrayList<Integer>();

        try{
            ReadHoldingRegistersResponse response = client.readHoldingRegisters(readHoldRegDTO.getUnitId(), new ReadHoldingRegistersRequest(readHoldRegDTO.getAddress(), readHoldRegDTO.getQuantity()));
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

    public ArrayList<Integer> readInputRegisters(ReadInputRegistersDTO readInRegDTO) throws ModbusCommunicationException {
        ArrayList<Integer> responseRegisters = new ArrayList<Integer>();

        try{
            ReadInputRegistersResponse response = client.readInputRegisters(readInRegDTO.getUnitId(), new ReadInputRegistersRequest(readInRegDTO.getAddress(), readInRegDTO.getQuantity()));
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
