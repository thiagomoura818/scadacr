package com.scada.ScadaCR.modbus.controller;

import com.scada.ScadaCR.modbus.controller.dto.ApiMessageResponse;
import com.scada.ScadaCR.modbus.controller.dto.ModbusResponseDto;
import com.scada.ScadaCR.modbus.controller.dto.WriteSingleCoilRequest;
import com.scada.ScadaCR.modbus.exceptions.manager.ModbusRegistryException;
import com.scada.ScadaCR.modbus.manager.CommunicationManagerModbus;
import com.scada.ScadaCR.modbus.pdu.request.ModbusRequest;
import com.scada.ScadaCR.modbus.pdu.request.write.WriteSingleCoilPDU;
import com.scada.ScadaCR.modbus.pdu.response.ModbusResponse;
import com.scada.ScadaCR.modbus.pdu.response.write.WriteResponse;
import com.scada.ScadaCR.modbus.registry.CommunicationManagerModbusRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModbusControllerTest {

    @Mock
    private CommunicationManagerModbusRegistry registry;

    @Mock
    private CommunicationManagerModbus manager;

    @Test
    void shouldReturnResponsesForKnownDevice() throws Exception {
        ModbusController controller = new ModbusController(registry);
        List<ModbusResponse<?>> responses = List.of(new WriteResponse(true));
        when(registry.getManagerByIdentifier("plc-1")).thenReturn(manager);
        when(manager.getResponses()).thenReturn(responses);

        ResponseEntity<List<ModbusResponseDto>> response = controller.getResponses("plc-1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(true, response.getBody().getFirst().success());
        assertEquals(null, response.getBody().getFirst().error());
    }

    @Test
    void shouldReturnNotFoundWhenResponsesDeviceIsUnknown() throws Exception {
        ModbusController controller = new ModbusController(registry);
        when(registry.getManagerByIdentifier("missing")).thenThrow(new ModbusRegistryException("missing"));

        ResponseEntity<List<ModbusResponseDto>> response = controller.getResponses("missing");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void shouldReturnHealthyWhenDeviceExists() throws Exception {
        ModbusController controller = new ModbusController(registry);
        when(registry.getManagerByIdentifier("plc-1")).thenReturn(manager);

        ResponseEntity<ApiMessageResponse> response = controller.checkDeviceHealth("plc-1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Device plc-1 is healthy", response.getBody().message());
    }

    @Test
    void shouldEnqueueLedControlRequest() throws Exception {
        ModbusController controller = new ModbusController(registry);
        ArgumentCaptor<ModbusRequest> requestCaptor = ArgumentCaptor.forClass(ModbusRequest.class);
        when(registry.getManagerByIdentifier("plc-1")).thenReturn(manager);

        ResponseEntity<ApiMessageResponse> response = controller.controlLED("plc-1", "on");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(manager).addDemandRequests(requestCaptor.capture());
        WriteSingleCoilPDU request = assertInstanceOf(WriteSingleCoilPDU.class, requestCaptor.getValue());
        assertEquals(0, request.getAddress());
        assertEquals(1, request.getUnitId());
        assertEquals(true, request.getValue());
    }

    @Test
    void shouldEnqueueWriteSingleCoilRequestFromApiDto() throws Exception {
        ModbusController controller = new ModbusController(registry);
        ArgumentCaptor<ModbusRequest> requestCaptor = ArgumentCaptor.forClass(ModbusRequest.class);
        when(registry.getManagerByIdentifier("plc-1")).thenReturn(manager);

        ResponseEntity<ApiMessageResponse> response = controller.writeSingleCoil(
                "plc-1",
                new WriteSingleCoilRequest(8, true, 3)
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Coil write request queued", response.getBody().message());
        verify(manager).addDemandRequests(requestCaptor.capture());
        WriteSingleCoilPDU request = assertInstanceOf(WriteSingleCoilPDU.class, requestCaptor.getValue());
        assertEquals(8, request.getAddress());
        assertEquals(3, request.getUnitId());
        assertEquals(true, request.getValue());
    }
}
