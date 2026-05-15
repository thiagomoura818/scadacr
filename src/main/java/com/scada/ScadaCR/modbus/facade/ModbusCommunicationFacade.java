package com.scada.ScadaCR.modbus.facade;

import com.scada.ScadaCR.modbus.exceptions.manager.ModbusRegistryException;
import com.scada.ScadaCR.modbus.manager.CommunicationManagerModbus;
import com.scada.ScadaCR.modbus.pdu.request.ModbusRequest;
import com.scada.ScadaCR.modbus.pdu.request.write.WriteSingleCoilPDU;
import com.scada.ScadaCR.modbus.pdu.response.ModbusResponse;
import com.scada.ScadaCR.modbus.registry.CommunicationManagerModbusRegistry;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModbusCommunicationFacade {
    private final CommunicationManagerModbusRegistry registry;

    public ModbusCommunicationFacade(CommunicationManagerModbusRegistry registry) {
        this.registry = registry;
    }

    public void registerCyclicRequest(String deviceId, ModbusRequest request) throws ModbusRegistryException {
        CommunicationManagerModbus manager =
                registry.getManagerByIdentifier(deviceId);

        manager.addCyclicRequest(request);
    }

    public List<ModbusResponse<?>> getResponsesByDeviceId(String deviceId) throws ModbusRegistryException {
        CommunicationManagerModbus manager = registry.getManagerByIdentifier(deviceId);
        return manager.getResponses();
    }

    public void registerDemandRequest(String deviceId, ModbusRequest request) throws ModbusRegistryException {
        CommunicationManagerModbus manager = registry.getManagerByIdentifier(deviceId);

        manager.addDemandRequests(request);
    }

    public void controlLED(String deviceId, int coilAddress, boolean state) throws ModbusRegistryException {
        CommunicationManagerModbus manager = registry.getManagerByIdentifier(deviceId);
        WriteSingleCoilPDU ledRequest = new WriteSingleCoilPDU(coilAddress, state, 1);
        manager.addDemandRequests(ledRequest);
    }
}
