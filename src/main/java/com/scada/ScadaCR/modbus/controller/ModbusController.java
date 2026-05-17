package com.scada.ScadaCR.modbus.controller;

import com.scada.ScadaCR.modbus.exceptions.manager.ModbusRegistryException;
import com.scada.ScadaCR.modbus.manager.CommunicationManagerModbus;
import com.scada.ScadaCR.modbus.pdu.request.write.WriteSingleCoilPDU;
import com.scada.ScadaCR.modbus.pdu.response.ModbusResponse;
import com.scada.ScadaCR.modbus.registry.CommunicationManagerModbusRegistry;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
    public ResponseEntity<List<ModbusResponse<?>>> getResponses(@PathVariable String deviceId) {
        try {
            CommunicationManagerModbus manager = registry.getManagerByIdentifier(deviceId);
            return ResponseEntity.ok(manager.getResponses());
        } catch (ModbusRegistryException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/devices/{deviceId}/health")
    public ResponseEntity<String> checkDeviceHealth(@PathVariable String deviceId) {
        try {
            registry.getManagerByIdentifier(deviceId);
            return ResponseEntity.ok("Device " + deviceId + " is healthy");
        } catch (ModbusRegistryException e) {
            return ResponseEntity.status(404).body("Device " + deviceId + " not found");
        }
    }

    @PostMapping("/devices/{deviceId}/led/{state}")
    public ResponseEntity<String> controlLED(@PathVariable String deviceId, @PathVariable String state) {
        try {
            CommunicationManagerModbus manager = registry.getManagerByIdentifier(deviceId);
            boolean isOn = state.equalsIgnoreCase("on");
            manager.addDemandRequests(new WriteSingleCoilPDU(0, isOn, 1));
            return ResponseEntity.ok("LED turned " + state);
        } catch (ModbusRegistryException e) {
            return ResponseEntity.status(404).body("Device " + deviceId + " not found");
        }
    }

    @PostMapping("/devices/{deviceId}/led/on")
    public ResponseEntity<String> ledOn(@PathVariable String deviceId) {
        try {
            CommunicationManagerModbus manager = registry.getManagerByIdentifier(deviceId);
            manager.addDemandRequests(new WriteSingleCoilPDU(0, true, 1));
            return ResponseEntity.ok("LED turned ON");
        } catch (ModbusRegistryException e) {
            return ResponseEntity.status(404).body("Device " + deviceId + " not found");
        }
    }

    @PostMapping("/devices/{deviceId}/led/off")
    public ResponseEntity<String> ledOff(@PathVariable String deviceId) {
        try {
            CommunicationManagerModbus manager = registry.getManagerByIdentifier(deviceId);
            manager.addDemandRequests(new WriteSingleCoilPDU(0, false, 1));
            return ResponseEntity.ok("LED turned OFF");
        } catch (ModbusRegistryException e) {
            return ResponseEntity.status(404).body("Device " + deviceId + " not found");
        }
    }
}
