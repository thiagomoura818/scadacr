package com.scada.ScadaCR.modbus.registry;

import com.scada.ScadaCR.modbus.exceptions.manager.ModbusRegistryException;
import com.scada.ScadaCR.modbus.manager.CommunicationManagerModbus;
import com.scada.ScadaCR.modbus.services.ModbusClientService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CommunicationManagerModbusRegistryTest {

    @Test
    void shouldRegisterAndRetrieveManager() throws Exception {
        CommunicationManagerModbusRegistry registry = new CommunicationManagerModbusRegistry();
        CommunicationManagerModbus manager = new CommunicationManagerModbus(new ModbusClientService());

        registry.registerManager("plc-1", manager);

        assertSame(manager, registry.getManagerByIdentifier("plc-1"));
        assertEquals(1, registry.getAllManagers().size());
    }

    @Test
    void shouldRejectDuplicateIdentifier() throws Exception {
        CommunicationManagerModbusRegistry registry = new CommunicationManagerModbusRegistry();

        registry.registerManager("plc-1", new CommunicationManagerModbus(new ModbusClientService()));

        ModbusRegistryException exception = assertThrows(
                ModbusRegistryException.class,
                () -> registry.registerManager("plc-1", new CommunicationManagerModbus(new ModbusClientService()))
        );

        assertEquals("Ja existe um registro com esse nome", exception.getMessage());
    }

    @Test
    void shouldRejectDuplicateManagerInstance() throws Exception {
        CommunicationManagerModbusRegistry registry = new CommunicationManagerModbusRegistry();
        CommunicationManagerModbus manager = new CommunicationManagerModbus(new ModbusClientService());

        registry.registerManager("plc-1", manager);

        ModbusRegistryException exception = assertThrows(
                ModbusRegistryException.class,
                () -> registry.registerManager("plc-2", manager)
        );

        assertEquals("Este manager já foi adicionado", exception.getMessage());
    }

    @Test
    void shouldRemoveManager() throws Exception {
        CommunicationManagerModbusRegistry registry = new CommunicationManagerModbusRegistry();
        CommunicationManagerModbus manager = new CommunicationManagerModbus(new ModbusClientService());

        registry.registerManager("plc-1", manager);
        registry.removeManagerByIdentifier("plc-1");

        ModbusRegistryException exception = assertThrows(
                ModbusRegistryException.class,
                () -> registry.getManagerByIdentifier("plc-1")
        );

        assertEquals("Esse identificador nao existe", exception.getMessage());
    }
}
