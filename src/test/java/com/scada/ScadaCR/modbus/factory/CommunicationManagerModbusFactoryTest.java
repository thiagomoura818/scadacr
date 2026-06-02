package com.scada.ScadaCR.modbus.factory;

import com.scada.ScadaCR.modbus.config.ModbusDeviceConfig;
import com.scada.ScadaCR.modbus.manager.CommunicationManagerModbus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommunicationManagerModbusFactoryTest {

    @Test
    void shouldCreateActiveManagerWhenConfigIsActive() {
        CommunicationManagerModbusFactory factory = new CommunicationManagerModbusFactory();

        CommunicationManagerModbus manager = factory.create(new ModbusDeviceConfig("plc-1", "127.0.0.1", 502, true));

        assertTrue(manager.isActive());
        assertEquals("127.0.0.1:502", manager.getIdentifier());
    }

    @Test
    void shouldCreateDisabledManagerWhenConfigIsInactive() {
        CommunicationManagerModbusFactory factory = new CommunicationManagerModbusFactory();

        CommunicationManagerModbus manager = factory.create(new ModbusDeviceConfig("plc-1", "127.0.0.1", 502, false));

        assertFalse(manager.isActive());
    }
}
