package com.scada.ScadaCR.modbus.factory;

import com.scada.ScadaCR.modbus.config.ModbusDeviceConfig;
import com.scada.ScadaCR.modbus.manager.CommunicationManagerModbus;
import com.scada.ScadaCR.modbus.services.ModbusClientService;
import org.springframework.stereotype.Component;

@Component
public class CommunicationManagerModbusFactory {

    public CommunicationManagerModbus create(ModbusDeviceConfig config) {
        CommunicationManagerModbus manager = getModbusManager(config.getHost(), config.getPort());

        if(config.isActive())
            manager.activate();
        else
            manager.disable();

        return manager;
    }

    public CommunicationManagerModbus getModbusManager(String host, Integer port) {
        ModbusClientService service = new ModbusClientService();
        CommunicationManagerModbus manager = new CommunicationManagerModbus(service);
        manager.configureParameters(host, port);

        return manager;
    }

}
