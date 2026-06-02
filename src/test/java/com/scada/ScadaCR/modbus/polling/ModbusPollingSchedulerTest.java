package com.scada.ScadaCR.modbus.polling;

import com.scada.ScadaCR.modbus.manager.CommunicationManagerModbus;
import com.scada.ScadaCR.modbus.registry.CommunicationManagerModbusRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModbusPollingSchedulerTest {

    @Mock
    private CommunicationManagerModbusRegistry registry;

    @Mock
    private CommunicationManagerModbus manager;

    private ModbusPollingScheduler scheduler;

    @AfterEach
    void tearDown() {
        if (scheduler != null) {
            scheduler.shutdown();
        }
    }

    @Test
    void shouldExecuteCycleForActiveManagerWhenNotAlreadyRunning() throws Exception {
        scheduler = new ModbusPollingScheduler(registry);
        when(registry.getAllManagers()).thenReturn(List.of(manager));
        when(manager.isActive()).thenReturn(true);
        when(manager.tryStartCycle()).thenReturn(true);

        scheduler.poll();

        verify(manager, timeout(1000)).executeCycle();
        verify(manager, timeout(1000)).finishCycle();
    }

    @Test
    void shouldSkipInactiveManager() throws Exception {
        scheduler = new ModbusPollingScheduler(registry);
        when(registry.getAllManagers()).thenReturn(List.of(manager));
        when(manager.isActive()).thenReturn(false);

        scheduler.poll();

        verify(manager, never()).tryStartCycle();
        verify(manager, never()).executeCycle();
    }

    @Test
    void shouldSkipManagerAlreadyRunning() throws Exception {
        scheduler = new ModbusPollingScheduler(registry);
        when(registry.getAllManagers()).thenReturn(List.of(manager));
        when(manager.isActive()).thenReturn(true);
        when(manager.tryStartCycle()).thenReturn(false);

        scheduler.poll();

        verify(manager, never()).executeCycle();
    }
}
