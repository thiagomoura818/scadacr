package com.scada.ScadaCR.modbus.registry;

import com.scada.ScadaCR.modbus.exceptions.manager.ModbusRegistryException;
import com.scada.ScadaCR.modbus.manager.CommunicationManagerModbus;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CommunicationManagerModbusRegistry {
    private final Map<String, CommunicationManagerModbus> registry;

    public CommunicationManagerModbusRegistry(){
        this.registry = new ConcurrentHashMap<>();

    }

    public void registerManager(String identifier, CommunicationManagerModbus communicationManagerModbus) throws ModbusRegistryException {
        if(this.registry.containsKey(identifier))
            throw new ModbusRegistryException("Ja existe um registro com esse nome");

        if(this.registry.containsValue(communicationManagerModbus))
            throw new ModbusRegistryException("Este manager já foi adicionado");

        this.registry.put(identifier, communicationManagerModbus);
    }

    public CommunicationManagerModbus getManagerByIdentifier(String identifier) throws ModbusRegistryException {
        if(!this.registry.containsKey(identifier))
            throw new ModbusRegistryException("Esse identificador nao existe");

        return this.registry.get(identifier);
    }

    public void removeManagerByIdentifier(String identifier) throws ModbusRegistryException {
        if(!this.registry.containsKey(identifier))
            throw new ModbusRegistryException("Esse identificador nao existe");

        this.registry.remove(identifier);
    }

    public Collection<CommunicationManagerModbus> getAllManagers(){
        return registry.values();
    }
}
