package com.scada.ScadaCR.modbus.state;

import com.scada.ScadaCR.modbus.pdu.response.ModbusResponse;
import com.scada.ScadaCR.modbus.state.enums.ModbusCommunicationStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ModbusCommunicationState {
    private final String deviceId;
    private ModbusCommunicationStatus status;
    private List<ModbusResponse<?>> lastResponses;
    private String lastErrorMessage;
    private Instant lastSuccessAt;
    private Instant lastFailureAt;
    private Instant lastUpdatedAt;
    private int consecutiveFailures;

    public ModbusCommunicationState(String deviceId){
        this.deviceId = deviceId;
        this.status = ModbusCommunicationStatus.UNKNOWN;
        this.lastResponses = new ArrayList<>();
        this.consecutiveFailures = 0;
    }

    public void recordSuccess(ModbusResponse<?> responses){
        Instant now = Instant.now();

        this.lastResponses = new ArrayList<>((Collection) responses);
        this.status = ModbusCommunicationStatus.OK;
        this.lastSuccessAt = now;
        this.lastUpdatedAt = now;
        this.lastFailureAt = null;
        this.consecutiveFailures = 0;
    }

    public void recordFailure(Exception exception){
        Instant now = Instant.now();

        this.status = ModbusCommunicationStatus.ERROR;
        this.lastFailureAt = now;
        this.lastUpdatedAt = now;
        this.lastErrorMessage = exception.getMessage();
        this.consecutiveFailures++;

    }

    public void markDisabled(){
        this.status = ModbusCommunicationStatus.DISABLED;
        this.lastUpdatedAt = Instant.now();
    }
}
