package com.scada.ScadaCR.modbus;

import com.scada.ScadaCR.modbus.exceptions.ModbusCommunicationException;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public interface CommunicationManager<T> {
    void executeCycle() throws ModbusCommunicationException;
    void connect() throws ModbusCommunicationException;
    void disconnect() throws ModbusCommunicationException;
    boolean isConnected() throws ModbusCommunicationException;
}
