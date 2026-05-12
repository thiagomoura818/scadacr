package com.scada.ScadaCR.modbus.services;

import com.digitalpetri.modbus.client.ModbusTcpClient;
import com.digitalpetri.modbus.exceptions.ModbusExecutionException;
import com.digitalpetri.modbus.exceptions.ModbusResponseException;
import com.digitalpetri.modbus.exceptions.ModbusTimeoutException;
import com.digitalpetri.modbus.pdu.*;
import com.digitalpetri.modbus.tcp.client.NettyTcpClientTransport;
import com.scada.ScadaCR.modbus.pdu.request.read.ReadCoilsPDU;
import com.scada.ScadaCR.modbus.pdu.request.read.ReadHoldingRegistersPDU;
import com.scada.ScadaCR.modbus.pdu.request.read.ReadInputRegistersPDU;
import com.scada.ScadaCR.modbus.pdu.request.write.WriteSingleCoilPDU;
import com.scada.ScadaCR.modbus.pdu.request.write.WriteSingleRegisterPDU;
import com.scada.ScadaCR.modbus.exceptions.ModbusCommunicationException;
import com.scada.ScadaCR.modbus.pdu.response.read.CoilsResponse;
import com.scada.ScadaCR.modbus.pdu.response.read.HoldingRegistersResponse;
import com.scada.ScadaCR.modbus.pdu.response.read.InputRegistersResponse;
import com.scada.ScadaCR.modbus.pdu.response.write.WriteResponse;
import org.springframework.stereotype.Service;

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
        if(client == null)
            return false;
        return client.isConnected();
    }

    public CoilsResponse readCoils(ReadCoilsPDU readCoilsPDU) throws ModbusCommunicationException {
        ArrayList<Boolean> responseBits = new ArrayList<>();

        try{
            ReadCoilsResponse response = client.readCoils(readCoilsPDU.getUnitId(), new ReadCoilsRequest(readCoilsPDU.getAddress(), readCoilsPDU.getQuantity()));
            byte[] coils = response.coils();

            for(int i = 0; i < readCoilsPDU.getQuantity(); i++){
                responseBits.add(i, getBitFromByte(i%8, coils[i/8]));
            }
        }catch(ModbusExecutionException | ModbusResponseException | ModbusTimeoutException e){
            throw new ModbusCommunicationException("Erro ao ler coils.", e);
        }

        return new CoilsResponse(responseBits);
    }

    public WriteResponse writeSingleCoil(WriteSingleCoilPDU writeSingleCoilPDU) throws ModbusCommunicationException {
        try{
            WriteSingleCoilResponse response = client.writeSingleCoil(writeSingleCoilPDU.getUnitId(), new WriteSingleCoilRequest(writeSingleCoilPDU.getAddress(), writeSingleCoilPDU.getValue()));
        }catch(ModbusExecutionException | ModbusResponseException | ModbusTimeoutException e){
            throw new ModbusCommunicationException("Falha ao escrever um coil. ",e);
        }
        return new WriteResponse(true);
    }

    public WriteResponse writeSingleRegister(WriteSingleRegisterPDU writeSingleRegDTO) throws ModbusCommunicationException {
        try{
            WriteSingleRegisterResponse response = client.writeSingleRegister(writeSingleRegDTO.getUnitId(), new WriteSingleRegisterRequest(writeSingleRegDTO.getAddress(), writeSingleRegDTO.getValue()));
        }catch(ModbusExecutionException | ModbusResponseException | ModbusTimeoutException e){
            throw new ModbusCommunicationException("Falha ao escrever um coil. ",e);
        }
        return new WriteResponse(true);
    }

    public HoldingRegistersResponse readHoldingRegisters(ReadHoldingRegistersPDU readHoldRegDTO) throws ModbusCommunicationException {
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

        return new HoldingRegistersResponse(responseRegisters);
    }

    public InputRegistersResponse readInputRegisters(ReadInputRegistersPDU readInRegDTO) throws ModbusCommunicationException {
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

        return new InputRegistersResponse(responseRegisters);
    }


    private boolean getBitFromByte(int pos, byte number){
        return ((number >> pos) & 1) == 1;
    }
    private int getValueFromByte(byte lbyte, byte hbyte){ return (((hbyte & 0xFF) << 8) | (lbyte & 0xFF));}
}
