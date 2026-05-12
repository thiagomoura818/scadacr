package com.scada.ScadaCR.modbus.bootstrap;

import com.scada.ScadaCR.modbus.config.ModbusDeviceConfig;
import com.scada.ScadaCR.modbus.exceptions.manager.ModbusRegistryException;
import com.scada.ScadaCR.modbus.factory.CommunicationManagerModbusFactory;
import com.scada.ScadaCR.modbus.manager.CommunicationManagerModbus;
import com.scada.ScadaCR.modbus.registry.CommunicationManagerModbusRegistry;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ModbusBootstrap {
    private final CommunicationManagerModbusFactory factory;
    private final CommunicationManagerModbusRegistry registry;
    private final boolean enabled;
    private final String identifier;
    private final String host;
    private final Integer port;
    private final boolean active;

    public ModbusBootstrap(
            CommunicationManagerModbusFactory factory,
            CommunicationManagerModbusRegistry registry,
            @Value("${scadacr.modbus.bootstrap.enabled:false}") boolean enabled,
            @Value("${scadacr.modbus.bootstrap.identifier:modbus-device-1}") String identifier,
            @Value("${scadacr.modbus.bootstrap.host:127.0.0.1}") String host,
            @Value("${scadacr.modbus.bootstrap.port:502}") Integer port,
            @Value("${scadacr.modbus.bootstrap.active:false}") boolean active
    ) {
        this.factory = factory;
        this.registry = registry;
        this.enabled = enabled;
        this.identifier = identifier;
        this.host = host;
        this.port = port;
        this.active = active;
    }

    @PostConstruct
    public void registerConfiguredDevice() throws ModbusRegistryException {
        if(!enabled)
            return;

        ModbusDeviceConfig config = new ModbusDeviceConfig(identifier, host, port, active);
        CommunicationManagerModbus manager = factory.create(config);
        registry.registerManager(config.getIdentifier(), manager);
    }
}
