package com.scada.ScadaCR.modbus.services;

import com.scada.ScadaCR.modbus.bootstrap.ModbusBootstrap;
import com.scada.ScadaCR.modbus.config.ModbusDeviceConfig;
import com.scada.ScadaCR.modbus.exceptions.ModbusCommunicationException;
import com.scada.ScadaCR.modbus.exceptions.manager.ModbusRegistryException;
import com.scada.ScadaCR.modbus.factory.CommunicationManagerModbusFactory;
import com.scada.ScadaCR.modbus.manager.CommunicationManagerModbus;
import com.scada.ScadaCR.modbus.pdu.request.ModbusRequest;
import com.scada.ScadaCR.modbus.pdu.request.read.ReadCoilsPDU;
import com.scada.ScadaCR.modbus.pdu.request.read.ReadHoldingRegistersPDU;
import com.scada.ScadaCR.modbus.pdu.request.read.ReadInputRegistersPDU;
import com.scada.ScadaCR.modbus.pdu.request.write.WriteSingleCoilPDU;
import com.scada.ScadaCR.modbus.pdu.request.write.WriteSingleRegisterPDU;
import com.scada.ScadaCR.modbus.registry.CommunicationManagerModbusRegistry;
import org.springframework.stereotype.Service;

@Service
public class ModbusService {

    private final CommunicationManagerModbusRegistry registry;
    private final CommunicationManagerModbusFactory factory;

    public ModbusService(CommunicationManagerModbusFactory factory, CommunicationManagerModbusRegistry registry){
        this.registry = registry;
        this.factory = factory;
    }

    public void registerManager(String deviceId, String host, Integer port) throws ModbusRegistryException {
        ModbusDeviceConfig config = new ModbusDeviceConfig(deviceId, host, port, true);
        CommunicationManagerModbus manager = factory.create(config);

        registry.registerManager(deviceId, manager);
    }

    public void removeManager(String deviceId) throws ModbusRegistryException {
        CommunicationManagerModbus manager = registry.getManagerByIdentifier(deviceId);

        manager.disable();

        try{
            if(manager.isConnected())
                manager.disconnect();
        }catch(ModbusCommunicationException e){
            throw new ModbusRegistryException("Falha ao desconectar manager " + deviceId, e);
        }

        registry.removeManagerByIdentifier(deviceId);
    }

    public void addCoil(String deviceId, int address, int unitId) throws ModbusRegistryException {
        ReadCoilsPDU readCoilsPDU = new ReadCoilsPDU(address, 1, unitId);
        addCycleRequests(deviceId, readCoilsPDU);
    }

    public void addHoldingRegisters(String deviceId, int address, int unitId) throws ModbusRegistryException {
        ReadHoldingRegistersPDU readHoldingRegistersPDU = new ReadHoldingRegistersPDU(address, 1, unitId);
        addCycleRequests(deviceId, readHoldingRegistersPDU);
    }

    public void addInputRegister(String deviceId, int address, int unitId) throws ModbusRegistryException {
        ReadInputRegistersPDU readInputRegistersPDU = new ReadInputRegistersPDU(address, 1, unitId);
        addCycleRequests(deviceId, readInputRegistersPDU);
    }

    public void writeSingleCoil(String deviceId, int address, boolean value, int unitId) throws ModbusRegistryException {
        WriteSingleCoilPDU writeSingleCoilPDU = new WriteSingleCoilPDU(address, value, unitId);
        addDemandRequests(deviceId, writeSingleCoilPDU);
    }

    public void writeSingleRegister(String deviceId, int address, int value, int unitId) throws ModbusRegistryException {
        WriteSingleRegisterPDU writeSingleRegisterPDU = new WriteSingleRegisterPDU(address,value,unitId);
        addDemandRequests(deviceId, writeSingleRegisterPDU);
    }

    private void addCycleRequests(String deviceId, ModbusRequest request) throws ModbusRegistryException {
        CommunicationManagerModbus manager = registry.getManagerByIdentifier(deviceId);
        manager.addCyclicRequest(request);
    }

    private void addDemandRequests(String deviceId, ModbusRequest request) throws ModbusRegistryException {
        CommunicationManagerModbus manager = registry.getManagerByIdentifier(deviceId);
        manager.addDemandRequests(request);
    }
}
