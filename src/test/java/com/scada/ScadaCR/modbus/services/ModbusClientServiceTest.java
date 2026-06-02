package com.scada.ScadaCR.modbus.services;

import com.digitalpetri.modbus.client.ModbusTcpClient;
import com.digitalpetri.modbus.exceptions.ModbusExecutionException;
import com.digitalpetri.modbus.exceptions.ModbusResponseException;
import com.digitalpetri.modbus.exceptions.ModbusTimeoutException;
import com.digitalpetri.modbus.pdu.ReadCoilsResponse;
import com.digitalpetri.modbus.pdu.ReadHoldingRegistersResponse;
import com.digitalpetri.modbus.pdu.ReadInputRegistersResponse;
import com.digitalpetri.modbus.pdu.WriteSingleCoilRequest;
import com.digitalpetri.modbus.pdu.WriteSingleCoilResponse;
import com.digitalpetri.modbus.pdu.WriteSingleRegisterRequest;
import com.digitalpetri.modbus.pdu.WriteSingleRegisterResponse;
import com.digitalpetri.modbus.tcp.client.NettyTcpClientTransport;
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
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ModbusClientServiceTest {

    @Test
    void shouldCreateConnectionUsingStaticFactories() throws Exception {
        ModbusClientService service = new ModbusClientService();
        NettyTcpClientTransport transport = mock(NettyTcpClientTransport.class);
        ModbusTcpClient client = mock(ModbusTcpClient.class);

        try (MockedStatic<NettyTcpClientTransport> transportFactory = mockStatic(NettyTcpClientTransport.class);
             MockedStatic<ModbusTcpClient> clientFactory = mockStatic(ModbusTcpClient.class)) {
            transportFactory.when(() -> NettyTcpClientTransport.create(any())).thenReturn(transport);
            clientFactory.when(() -> ModbusTcpClient.create(transport)).thenReturn(client);
            when(client.isConnected()).thenReturn(true);

            service.createConnection("127.0.0.1", 502);

            verify(client).connect();
            assertTrue(service.isConnected());
        }
    }

    @Test
    void shouldRejectCreateConnectionWhenClientAlreadyExists() throws Exception {
        ModbusClientService service = new ModbusClientService();
        setClient(service, mock(ModbusTcpClient.class));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> service.createConnection("127.0.0.1", 502)
        );

        assertEquals("Cliente já está conectado.", exception.getMessage());
    }

    @Test
    void shouldWrapConnectFailure() throws Exception {
        ModbusClientService service = new ModbusClientService();
        NettyTcpClientTransport transport = mock(NettyTcpClientTransport.class);
        ModbusTcpClient client = mock(ModbusTcpClient.class);

        try (MockedStatic<NettyTcpClientTransport> transportFactory = mockStatic(NettyTcpClientTransport.class);
             MockedStatic<ModbusTcpClient> clientFactory = mockStatic(ModbusTcpClient.class)) {
            transportFactory.when(() -> NettyTcpClientTransport.create(any())).thenReturn(transport);
            clientFactory.when(() -> ModbusTcpClient.create(transport)).thenReturn(client);
            doThrow(new ModbusExecutionException("connect error")).when(client).connect();

            ModbusCommunicationException exception = assertThrows(
                    ModbusCommunicationException.class,
                    () -> service.createConnection("127.0.0.1", 502)
            );

            assertEquals("Erro ao criar conexao com client", exception.getMessage());
        }
    }

    @Test
    void shouldReturnFalseWhenClientIsNull() {
        ModbusClientService service = new ModbusClientService();

        assertFalse(service.isConnected());
    }

    @Test
    void shouldDisconnectClient() throws Exception {
        ModbusClientService service = new ModbusClientService();
        ModbusTcpClient client = mock(ModbusTcpClient.class);
        setClient(service, client);

        service.disconnect();

        verify(client).disconnect();
    }

    @Test
    void shouldParseReadCoilsResponse() throws Exception {
        ModbusClientService service = new ModbusClientService();
        ModbusTcpClient client = mock(ModbusTcpClient.class);
        setClient(service, client);
        ReadCoilsPDU request = new ReadCoilsPDU(0, 3, 1);
        when(client.readCoils(eq(1), any())).thenReturn(new ReadCoilsResponse(new byte[]{0b00000101}));

        CoilsResponse response = service.readCoils(request);

        assertTrue(response.isSuccess());
        assertEquals(true, response.getResponse().get(0));
        assertEquals(false, response.getResponse().get(1));
        assertEquals(true, response.getResponse().get(2));
    }

    @Test
    void shouldWrapReadCoilsFailure() throws Exception {
        ModbusClientService service = new ModbusClientService();
        ModbusTcpClient client = mock(ModbusTcpClient.class);
        setClient(service, client);
        ReadCoilsPDU request = new ReadCoilsPDU(0, 1, 1);
        when(client.readCoils(eq(1), any())).thenThrow(new ModbusTimeoutException("timeout"));

        ModbusCommunicationException exception = assertThrows(
                ModbusCommunicationException.class,
                () -> service.readCoils(request)
        );

        assertEquals("Erro ao ler coils.", exception.getMessage());
    }

    @Test
    void shouldParseHoldingRegistersResponse() throws Exception {
        ModbusClientService service = new ModbusClientService();
        ModbusTcpClient client = mock(ModbusTcpClient.class);
        setClient(service, client);
        ReadHoldingRegistersPDU request = new ReadHoldingRegistersPDU(0, 2, 1);
        when(client.readHoldingRegisters(eq(1), any()))
                .thenReturn(new ReadHoldingRegistersResponse(new byte[]{0x01, 0x02, 0x03, 0x04}));

        HoldingRegistersResponse response = service.readHoldingRegisters(request);

        assertTrue(response.isSuccess());
        assertEquals(2, response.getResponse().size());
        assertEquals(513, response.getResponse().get(0));
        assertEquals(1027, response.getResponse().get(1));
    }

    @Test
    void shouldParseInputRegistersResponse() throws Exception {
        ModbusClientService service = new ModbusClientService();
        ModbusTcpClient client = mock(ModbusTcpClient.class);
        setClient(service, client);
        ReadInputRegistersPDU request = new ReadInputRegistersPDU(0, 2, 1);
        when(client.readInputRegisters(eq(1), any()))
                .thenReturn(new ReadInputRegistersResponse(new byte[]{0x05, 0x06, 0x07, 0x08}));

        InputRegistersResponse response = service.readInputRegisters(request);

        assertTrue(response.isSuccess());
        assertEquals(2, response.getResponse().size());
        assertEquals(1541, response.getResponse().get(0));
        assertEquals(2055, response.getResponse().get(1));
    }

    @Test
    void shouldWriteSingleCoil() throws Exception {
        ModbusClientService service = new ModbusClientService();
        ModbusTcpClient client = mock(ModbusTcpClient.class);
        setClient(service, client);
        WriteSingleCoilPDU request = new WriteSingleCoilPDU(4, true, 2);
        when(client.writeSingleCoil(eq(2), any(WriteSingleCoilRequest.class)))
                .thenReturn(new WriteSingleCoilResponse(4, true));

        WriteResponse response = service.writeSingleCoil(request);

        assertTrue(response.isSuccess());
    }

    @Test
    void shouldWriteSingleRegister() throws Exception {
        ModbusClientService service = new ModbusClientService();
        ModbusTcpClient client = mock(ModbusTcpClient.class);
        setClient(service, client);
        WriteSingleRegisterPDU request = new WriteSingleRegisterPDU(7, 22, 3);
        when(client.writeSingleRegister(eq(3), any(WriteSingleRegisterRequest.class)))
                .thenReturn(new WriteSingleRegisterResponse(7, 22));

        WriteResponse response = service.writeSingleRegister(request);

        assertTrue(response.isSuccess());
    }

    @Test
    void shouldWrapWriteFailure() throws Exception {
        ModbusClientService service = new ModbusClientService();
        ModbusTcpClient client = mock(ModbusTcpClient.class);
        setClient(service, client);
        WriteSingleCoilPDU request = new WriteSingleCoilPDU(4, true, 2);
        when(client.writeSingleCoil(eq(2), any(WriteSingleCoilRequest.class)))
                .thenThrow(new ModbusResponseException(5, 1));

        ModbusCommunicationException exception = assertThrows(
                ModbusCommunicationException.class,
                () -> service.writeSingleCoil(request)
        );

        assertEquals("Falha ao escrever um coil. ", exception.getMessage());
    }

    private static void setClient(ModbusClientService service, ModbusTcpClient client) throws Exception {
        Field field = ModbusClientService.class.getDeclaredField("client");
        field.setAccessible(true);
        field.set(service, client);
    }
}
