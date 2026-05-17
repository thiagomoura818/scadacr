package com.scada.ScadaCR.modbus.services;

import com.scada.ScadaCR.modbus.config.ModbusDeviceConfig;
import com.scada.ScadaCR.modbus.exceptions.ModbusCommunicationException;
import com.scada.ScadaCR.modbus.exceptions.manager.ModbusRegistryException;
import com.scada.ScadaCR.modbus.factory.CommunicationManagerModbusFactory;
import com.scada.ScadaCR.modbus.manager.CommunicationManagerModbus;
import com.scada.ScadaCR.modbus.pdu.request.ModbusRequest;
import com.scada.ScadaCR.modbus.pdu.request.read.ReadCoilsPDU;
import com.scada.ScadaCR.modbus.pdu.request.write.WriteSingleCoilPDU;
import com.scada.ScadaCR.modbus.registry.CommunicationManagerModbusRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModbusServiceTest {

    @Mock
    private CommunicationManagerModbusRegistry registry;

    @Mock
    private CommunicationManagerModbusFactory factory;

    @Mock
    private CommunicationManagerModbus manager;

    @InjectMocks
    private ModbusService modbusService;

    @Test
    void shouldRegisterManagerUsingDeviceId() throws Exception {
        when(factory.create(any(ModbusDeviceConfig.class))).thenReturn(manager);

        modbusService.registerManager("plc-1", "127.0.0.1", 502);

        verify(factory).create(any(ModbusDeviceConfig.class));
        verify(registry).registerManager("plc-1", manager);
    }

    @Test
    void shouldRemoveConnectedManager() throws Exception {
        when(registry.getManagerByIdentifier("plc-1")).thenReturn(manager);
        when(manager.isConnected()).thenReturn(true);

        modbusService.removeManager("plc-1");

        var order = inOrder(registry, manager);
        order.verify(registry).getManagerByIdentifier("plc-1");
        order.verify(manager).disable();
        order.verify(manager).isConnected();
        order.verify(manager).disconnect();
        order.verify(registry).removeManagerByIdentifier("plc-1");
    }

    @Test
    void shouldNotDisconnectManagerWhenItIsNotConnected() throws Exception {
        when(registry.getManagerByIdentifier("plc-1")).thenReturn(manager);
        when(manager.isConnected()).thenReturn(false);

        modbusService.removeManager("plc-1");

        verify(manager, never()).disconnect();
        verify(registry).removeManagerByIdentifier("plc-1");
    }

    @Test
    void shouldWrapDisconnectFailureAsRegistryException() throws Exception {
        when(registry.getManagerByIdentifier("plc-1")).thenReturn(manager);
        when(manager.isConnected()).thenReturn(true);
        doThrow(new ModbusCommunicationException("disconnect error")).when(manager).disconnect();

        ModbusRegistryException exception = assertThrows(
                ModbusRegistryException.class,
                () -> modbusService.removeManager("plc-1")
        );

        assertEquals("Falha ao desconectar manager plc-1", exception.getMessage());
        verify(registry, never()).removeManagerByIdentifier("plc-1");
    }

    @Test
    void shouldAddReadCoilRequestAsCyclicRequest() throws Exception {
        when(registry.getManagerByIdentifier("plc-1")).thenReturn(manager);
        ArgumentCaptor<ModbusRequest> requestCaptor = ArgumentCaptor.forClass(ModbusRequest.class);

        modbusService.addCoil("plc-1", 10, 2);

        verify(manager).addCyclicRequest(requestCaptor.capture());
        ReadCoilsPDU request = assertInstanceOf(ReadCoilsPDU.class, requestCaptor.getValue());
        assertEquals(10, request.getAddress());
        assertEquals(2, request.getUnitId());
        assertEquals(1, request.getQuantity());
    }

    @Test
    void shouldAddWriteSingleCoilAsDemandRequest() throws Exception {
        when(registry.getManagerByIdentifier("plc-1")).thenReturn(manager);
        ArgumentCaptor<ModbusRequest> requestCaptor = ArgumentCaptor.forClass(ModbusRequest.class);

        modbusService.writeSingleCoil("plc-1", 8, true, 3);

        verify(manager).addDemandRequests(requestCaptor.capture());
        WriteSingleCoilPDU request = assertInstanceOf(WriteSingleCoilPDU.class, requestCaptor.getValue());
        assertEquals(8, request.getAddress());
        assertEquals(3, request.getUnitId());
        assertEquals(true, request.getValue());
    }
}
