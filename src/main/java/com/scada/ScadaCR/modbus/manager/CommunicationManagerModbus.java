package com.scada.ScadaCR.modbus.manager;

import com.scada.ScadaCR.modbus.CommunicationManager;
import com.scada.ScadaCR.modbus.exceptions.ModbusCommunicationException;
import com.scada.ScadaCR.modbus.pdu.request.ModbusRequest;
import com.scada.ScadaCR.modbus.pdu.response.ModbusResponse;
import com.scada.ScadaCR.modbus.services.ModbusClientService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class CommunicationManagerModbus implements CommunicationManager<ModbusResponse<?>> {
    private String host;
    private Integer port;
    private final ModbusClientService modbusClientService;
    private final List<ModbusRequest> cyclicRequests;
    private final ConcurrentLinkedQueue<ModbusRequest> demandRequests;
    private final ConcurrentLinkedQueue<ModbusResponse<?>> responses;
    private boolean state;
    private final AtomicBoolean running = new AtomicBoolean(false);

    public CommunicationManagerModbus(ModbusClientService modbusClientService){
        this.modbusClientService = modbusClientService;
        this.cyclicRequests = new ArrayList<>();
        this.demandRequests = new ConcurrentLinkedQueue<>();
        this.responses = new ConcurrentLinkedQueue<>();
    }

    public void configureParameters(String host, Integer port){
        this.host = host;
        this.port = port;
    }

    public void connect() throws ModbusCommunicationException {
        modbusClientService.createConnection(host, port);
    }

    public void disconnect() throws ModbusCommunicationException {
        modbusClientService.disconnect();
    }

    public boolean isConnected(){
        return this.modbusClientService.isConnected();
    }

    public List<ModbusResponse<?>> executeCycle() throws ModbusCommunicationException {
        if(!isConnected())
            connect();
        while(!demandRequests.isEmpty()){
            responses.add(this.pollRequest().execute(this.modbusClientService));
        }
        for(ModbusRequest mR : cyclicRequests){
            responses.add(mR.execute(this.modbusClientService));
        }

        return getResponses();
    }

    public void addCyclicRequest(ModbusRequest modbusRequest){
        cyclicRequests.add(modbusRequest);
    }

    public void removeCyclicRequest(ModbusRequest modbusRequest){
        cyclicRequests.remove(modbusRequest);
    }

    public void addDemandRequests(ModbusRequest modbusRequest){
        demandRequests.add(modbusRequest);
    }

    private ModbusRequest pollRequest(){
        return demandRequests.poll();
    }

    public List<ModbusResponse<?>> getResponses(){
        List<ModbusResponse<?>> copyOfResponses = new ArrayList<>();
        for(int i = 0; i < responses.size(); i++) {
            copyOfResponses.add(responses.poll());
        }

        return copyOfResponses;
    }

    public boolean isActive(){
        return state;
    }

    public void disable(){
        this.state = false;
    }

    public void activate(){
        this.state = true;
    }

    public boolean tryStartCycle(){
        return running.compareAndSet(false,true);
    }

    public void finishCycle(){
        running.set(false);
    }
}
