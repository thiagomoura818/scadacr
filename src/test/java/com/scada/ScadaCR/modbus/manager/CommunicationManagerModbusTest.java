package com.scada.ScadaCR.modbus.manager;

import com.scada.ScadaCR.modbus.pdu.request.ModbusRequest;
import com.scada.ScadaCR.modbus.pdu.response.ModbusResponse;
import com.scada.ScadaCR.modbus.pdu.response.write.WriteResponse;
import com.scada.ScadaCR.modbus.services.ModbusClientService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommunicationManagerModbusTest {

    @Mock
    private ModbusClientService modbusClientService;

    @Mock
    private ModbusRequest demandRequest;

    @Mock
    private ModbusRequest cyclicRequest;

    @Test
    void shouldConnectAndExecuteDemandBeforeCyclicRequests() throws Exception {
        CommunicationManagerModbus manager = new CommunicationManagerModbus(modbusClientService);
        ModbusResponse<?> demandResponse = new WriteResponse(true);
        ModbusResponse<?> cyclicResponse = new WriteResponse(true);

        manager.configureParameters("127.0.0.1", 502);
        manager.addDemandRequests(demandRequest);
        manager.addCyclicRequest(cyclicRequest);

        when(modbusClientService.isConnected()).thenReturn(false);
        when(demandRequest.execute(modbusClientService)).thenReturn(demandResponse);
        when(cyclicRequest.execute(modbusClientService)).thenReturn(cyclicResponse);

        manager.executeCycle();

        InOrder order = inOrder(modbusClientService, demandRequest, cyclicRequest);
        order.verify(modbusClientService).isConnected();
        order.verify(modbusClientService).createConnection("127.0.0.1", 502);
        order.verify(demandRequest).execute(modbusClientService);
        order.verify(cyclicRequest).execute(modbusClientService);
    }

    @Test
    void shouldReturnResponsesAndClearQueue() throws Exception {
        CommunicationManagerModbus manager = new CommunicationManagerModbus(modbusClientService);
        ModbusResponse<?> demandResponse = new WriteResponse(true);
        ModbusResponse<?> cyclicResponse = new WriteResponse(true);

        manager.addDemandRequests(demandRequest);
        manager.addCyclicRequest(cyclicRequest);

        when(modbusClientService.isConnected()).thenReturn(true);
        when(demandRequest.execute(modbusClientService)).thenReturn(demandResponse);
        when(cyclicRequest.execute(modbusClientService)).thenReturn(cyclicResponse);

        manager.executeCycle();

        List<ModbusResponse<?>> responses = manager.getResponses();

        assertEquals(2, responses.size());
        assertSame(demandResponse, responses.get(0));
        assertSame(cyclicResponse, responses.get(1));
        assertTrue(manager.getResponses().isEmpty());
    }

    @Test
    void shouldAllowOnlyOneRunningCycleAtATime() {
        CommunicationManagerModbus manager = new CommunicationManagerModbus(modbusClientService);

        assertTrue(manager.tryStartCycle());
        assertFalse(manager.tryStartCycle());

        manager.finishCycle();

        assertTrue(manager.tryStartCycle());
    }
}
