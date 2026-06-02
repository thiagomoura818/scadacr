package com.scada.ScadaCR.modbus.bootstrap;

import com.scada.ScadaCR.modbus.config.ModbusDeviceConfig;
import com.scada.ScadaCR.modbus.factory.CommunicationManagerModbusFactory;
import com.scada.ScadaCR.modbus.manager.CommunicationManagerModbus;
import com.scada.ScadaCR.modbus.registry.CommunicationManagerModbusRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModbusBootstrapTest {

    @Mock
    private CommunicationManagerModbusFactory factory;

    @Mock
    private CommunicationManagerModbusRegistry registry;

    @Mock
    private CommunicationManagerModbus manager;

    @Test
    void shouldRegisterConfiguredDeviceWhenBootstrapIsEnabled() throws Exception {
        when(factory.create(any(ModbusDeviceConfig.class))).thenReturn(manager);
        ModbusBootstrap bootstrap = new ModbusBootstrap(factory, registry, true, "plc-1", "127.0.0.1", 502, true);
        ArgumentCaptor<ModbusDeviceConfig> configCaptor = ArgumentCaptor.forClass(ModbusDeviceConfig.class);

        bootstrap.registerConfiguredDevice();

        verify(factory).create(configCaptor.capture());
        verify(registry).registerManager("plc-1", manager);
        assertEquals("plc-1", configCaptor.getValue().getIdentifier());
        assertEquals("127.0.0.1", configCaptor.getValue().getHost());
        assertEquals(502, configCaptor.getValue().getPort());
        assertTrue(configCaptor.getValue().isActive());
    }

    @Test
    void shouldNotRegisterConfiguredDeviceWhenBootstrapIsDisabled() throws Exception {
        ModbusBootstrap bootstrap = new ModbusBootstrap(factory, registry, false, "plc-1", "127.0.0.1", 502, true);

        bootstrap.registerConfiguredDevice();

        verify(factory, never()).create(any());
        verify(registry, never()).registerManager(any(), any());
    }

    @Test
    void shouldRegisterDeviceFromProvidedConfig() throws Exception {
        when(factory.create(any(ModbusDeviceConfig.class))).thenReturn(manager);
        ModbusBootstrap bootstrap = new ModbusBootstrap(factory, registry, false, "ignored", "127.0.0.1", 502, false);
        ModbusDeviceConfig config = new ModbusDeviceConfig("plc-2", "10.0.0.7", 1502, false);

        bootstrap.registerDevice(config);

        verify(factory).create(config);
        verify(registry).registerManager("plc-2", manager);
        assertFalse(config.isActive());
    }
}
