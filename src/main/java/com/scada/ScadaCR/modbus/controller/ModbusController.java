package com.scada.ScadaCR.modbus.controller;

import com.scada.ScadaCR.modbus.controller.dto.ApiMessageResponse;
import com.scada.ScadaCR.modbus.controller.dto.LedControlRequest;
import com.scada.ScadaCR.modbus.controller.dto.ModbusResponseDto;
import com.scada.ScadaCR.modbus.controller.dto.WriteSingleCoilRequest;
import com.scada.ScadaCR.modbus.controller.dto.WriteSingleRegisterRequest;
import com.scada.ScadaCR.modbus.exceptions.manager.ModbusRegistryException;
import com.scada.ScadaCR.modbus.manager.CommunicationManagerModbus;
import com.scada.ScadaCR.modbus.pdu.request.write.WriteSingleCoilPDU;
import com.scada.ScadaCR.modbus.pdu.request.write.WriteSingleRegisterPDU;
import com.scada.ScadaCR.modbus.registry.CommunicationManagerModbusRegistry;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/modbus")
public class ModbusController {

    private final CommunicationManagerModbusRegistry registry;

    public ModbusController(CommunicationManagerModbusRegistry registry) {
        this.registry = registry;
    }

    @GetMapping("/devices/{deviceId}/responses")
    public ResponseEntity<List<ModbusResponseDto>> getResponses(@PathVariable String deviceId) {
        try {
            CommunicationManagerModbus manager = registry.getManagerByIdentifier(deviceId);
            List<ModbusResponseDto> responses = manager.getResponses()
                    .stream()
                    .map(ModbusResponseDto::from)
                    .toList();
            return ResponseEntity.ok(responses);
        } catch (ModbusRegistryException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/devices/{deviceId}/health")
    public ResponseEntity<ApiMessageResponse> checkDeviceHealth(@PathVariable String deviceId) {
        try {
            registry.getManagerByIdentifier(deviceId);
            return ResponseEntity.ok(new ApiMessageResponse("Device " + deviceId + " is healthy"));
        } catch (ModbusRegistryException e) {
            return ResponseEntity.status(404).body(new ApiMessageResponse("Device " + deviceId + " not found"));
        }
    }

    @PostMapping("/devices/{deviceId}/led/{state}")
    public ResponseEntity<ApiMessageResponse> controlLED(@PathVariable String deviceId, @PathVariable String state) {
        return controlLED(deviceId, new LedControlRequest(state));
    }

    @PostMapping("/devices/{deviceId}/led")
    public ResponseEntity<ApiMessageResponse> controlLED(
            @PathVariable String deviceId,
            @RequestBody LedControlRequest request
    ) {
        try {
            CommunicationManagerModbus manager = registry.getManagerByIdentifier(deviceId);
            manager.addDemandRequests(new WriteSingleCoilPDU(0, request.isOn(), 1));
            return ResponseEntity.ok(new ApiMessageResponse("LED turned " + request.state()));
        } catch (ModbusRegistryException e) {
            return ResponseEntity.status(404).body(new ApiMessageResponse("Device " + deviceId + " not found"));
        }
    }

    @PostMapping("/devices/{deviceId}/coils/write")
    public ResponseEntity<ApiMessageResponse> writeSingleCoil(
            @PathVariable String deviceId,
            @RequestBody WriteSingleCoilRequest request
    ) {
        try {
            CommunicationManagerModbus manager = registry.getManagerByIdentifier(deviceId);
            manager.addDemandRequests(new WriteSingleCoilPDU(request.address(), request.value(), request.unitId()));
            return ResponseEntity.ok(new ApiMessageResponse("Coil write request queued"));
        } catch (ModbusRegistryException e) {
            return ResponseEntity.status(404).body(new ApiMessageResponse("Device " + deviceId + " not found"));
        }
    }

    @PostMapping("/devices/{deviceId}/registers/write")
    public ResponseEntity<ApiMessageResponse> writeSingleRegister(
            @PathVariable String deviceId,
            @RequestBody WriteSingleRegisterRequest request
    ) {
        try {
            CommunicationManagerModbus manager = registry.getManagerByIdentifier(deviceId);
            manager.addDemandRequests(new WriteSingleRegisterPDU(request.address(), request.value(), request.unitId()));
            return ResponseEntity.ok(new ApiMessageResponse("Register write request queued"));
        } catch (ModbusRegistryException e) {
            return ResponseEntity.status(404).body(new ApiMessageResponse("Device " + deviceId + " not found"));
        }
    }

    @PostMapping("/devices/{deviceId}/led/on")
    public ResponseEntity<ApiMessageResponse> ledOn(@PathVariable String deviceId) {
        try {
            CommunicationManagerModbus manager = registry.getManagerByIdentifier(deviceId);
            manager.addDemandRequests(new WriteSingleCoilPDU(0, true, 1));
            return ResponseEntity.ok(new ApiMessageResponse("LED turned ON"));
        } catch (ModbusRegistryException e) {
            return ResponseEntity.status(404).body(new ApiMessageResponse("Device " + deviceId + " not found"));
        }
    }

    @PostMapping("/devices/{deviceId}/led/off")
    public ResponseEntity<ApiMessageResponse> ledOff(@PathVariable String deviceId) {
        try {
            CommunicationManagerModbus manager = registry.getManagerByIdentifier(deviceId);
            manager.addDemandRequests(new WriteSingleCoilPDU(0, false, 1));
            return ResponseEntity.ok(new ApiMessageResponse("LED turned OFF"));
        } catch (ModbusRegistryException e) {
            return ResponseEntity.status(404).body(new ApiMessageResponse("Device " + deviceId + " not found"));
        }
    }
}
