package com.scada.ScadaCR.modbus.pdu;

import com.scada.ScadaCR.modbus.exceptions.ModbusCommunicationException;
import com.scada.ScadaCR.modbus.pdu.request.read.ReadCoilsPDU;
import com.scada.ScadaCR.modbus.pdu.request.read.ReadHoldingRegistersPDU;
import com.scada.ScadaCR.modbus.pdu.request.read.ReadInputRegistersPDU;
import com.scada.ScadaCR.modbus.pdu.request.write.WriteSingleCoilPDU;
import com.scada.ScadaCR.modbus.pdu.request.write.WriteSingleRegisterPDU;
import com.scada.ScadaCR.modbus.pdu.response.read.CoilsResponse;
import com.scada.ScadaCR.modbus.pdu.response.read.HoldingRegistersResponse;
import com.scada.ScadaCR.modbus.pdu.response.read.InputRegistersResponse;
import com.scada.ScadaCR.modbus.pdu.response.write.WriteResponse;
import com.scada.ScadaCR.modbus.services.ModbusClientService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PduAndResponseTest {

    @Mock
    private ModbusClientService modbusClientService;

    @Test
    void shouldRejectInvalidAddressOrQuantity() {
        assertThrows(IllegalArgumentException.class, () -> new ReadCoilsPDU(-1, 1, 1));
        assertThrows(IllegalArgumentException.class, () -> new ReadCoilsPDU(0, 0, 1));
        assertThrows(IllegalArgumentException.class, () -> new ReadHoldingRegistersPDU(0, 0, 1));
        assertThrows(IllegalArgumentException.class, () -> new ReadInputRegistersPDU(0, 0, 1));
    }

    @Test
    void shouldExecuteReadRequestsThroughClient() throws Exception {
        ArrayList<Boolean> coils = new ArrayList<>();
        coils.add(true);
        CoilsResponse coilsResponse = new CoilsResponse(coils);
        ArrayList<Integer> registers = new ArrayList<>();
        registers.add(10);
        HoldingRegistersResponse holdingResponse = new HoldingRegistersResponse(registers);
        InputRegistersResponse inputResponse = new InputRegistersResponse(registers);

        ReadCoilsPDU readCoilsPDU = new ReadCoilsPDU(1, 1, 1);
        ReadHoldingRegistersPDU readHoldingRegistersPDU = new ReadHoldingRegistersPDU(2, 1, 1);
        ReadInputRegistersPDU readInputRegistersPDU = new ReadInputRegistersPDU(3, 1, 1);

        when(modbusClientService.readCoils(readCoilsPDU)).thenReturn(coilsResponse);
        when(modbusClientService.readHoldingRegisters(readHoldingRegistersPDU)).thenReturn(holdingResponse);
        when(modbusClientService.readInputRegisters(readInputRegistersPDU)).thenReturn(inputResponse);

        assertSameResponse(coilsResponse, readCoilsPDU.execute(modbusClientService));
        assertSameResponse(holdingResponse, readHoldingRegistersPDU.execute(modbusClientService));
        assertSameResponse(inputResponse, readInputRegistersPDU.execute(modbusClientService));
    }

    @Test
    void shouldConvertReadFailureIntoErrorResponse() throws Exception {
        ReadCoilsPDU request = new ReadCoilsPDU(1, 1, 1);
        when(modbusClientService.readCoils(request)).thenThrow(new ModbusCommunicationException("read error"));

        CoilsResponse response = assertInstanceOf(CoilsResponse.class, request.execute(modbusClientService));

        assertFalse(response.isSuccess());
        assertEquals("read error", response.getError().getMessage());
    }

    @Test
    void shouldExecuteWriteRequestsThroughClient() throws Exception {
        WriteSingleCoilPDU writeSingleCoilPDU = new WriteSingleCoilPDU(4, true, 1);
        WriteSingleRegisterPDU writeSingleRegisterPDU = new WriteSingleRegisterPDU(5, 12, 1);
        WriteResponse writeResponse = new WriteResponse(true);

        when(modbusClientService.writeSingleCoil(writeSingleCoilPDU)).thenReturn(writeResponse);
        when(modbusClientService.writeSingleRegister(writeSingleRegisterPDU)).thenReturn(writeResponse);

        WriteResponse coilResponse = assertInstanceOf(WriteResponse.class, writeSingleCoilPDU.execute(modbusClientService));
        WriteResponse registerResponse = assertInstanceOf(WriteResponse.class, writeSingleRegisterPDU.execute(modbusClientService));

        assertTrue(coilResponse.isSuccess());
        assertTrue(registerResponse.isSuccess());
    }

    @Test
    void shouldConvertWriteFailureIntoErrorResponse() throws Exception {
        WriteSingleCoilPDU request = new WriteSingleCoilPDU(4, true, 1);
        when(modbusClientService.writeSingleCoil(request)).thenThrow(new ModbusCommunicationException("write error"));

        WriteResponse response = assertInstanceOf(WriteResponse.class, request.execute(modbusClientService));

        assertFalse(response.isSuccess());
        assertEquals("write error", response.getError().getMessage());
    }

    private static void assertSameResponse(Object expected, Object actual) {
        assertEquals(expected, actual);
    }
}
