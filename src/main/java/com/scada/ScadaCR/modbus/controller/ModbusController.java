package com.scada.ScadaCR.modbus.controller;

import com.scada.ScadaCR.modbus.exceptions.manager.ModbusRegistryException;
import com.scada.ScadaCR.modbus.facade.ModbusCommunicationFacade;
import com.scada.ScadaCR.modbus.pdu.response.ModbusResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modbus")
public class ModbusController {

    private final ModbusCommunicationFacade facade;

    public ModbusController(ModbusCommunicationFacade facade) {
        this.facade = facade;
    }

    @GetMapping("/devices/{deviceId}/responses")
    public ResponseEntity<List<ModbusResponse<?>>> getResponses(@PathVariable String deviceId) {
        try {
            List<ModbusResponse<?>> responses = facade.getResponsesByDeviceId(deviceId);
            return ResponseEntity.ok(responses);
        } catch (ModbusRegistryException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/devices/{deviceId}/health")
    public ResponseEntity<String> checkDeviceHealth(@PathVariable String deviceId) {
        try {
            facade.getResponsesByDeviceId(deviceId);
            return ResponseEntity.ok("Device " + deviceId + " is healthy");
        } catch (ModbusRegistryException e) {
            return ResponseEntity.status(404).body("Device " + deviceId + " not found");
        }
    }

    @PostMapping("/devices/{deviceId}/led/{state}")
    public ResponseEntity<String> controlLED(@PathVariable String deviceId, @PathVariable String state) {
        try {
            boolean isOn = state.equalsIgnoreCase("on");
            facade.controlLED(deviceId, 0, isOn);
            return ResponseEntity.ok("LED turned " + state);
        } catch (ModbusRegistryException e) {
            return ResponseEntity.status(404).body("Device " + deviceId + " not found");
        }
    }

    @PostMapping("/devices/{deviceId}/led/on")
    public ResponseEntity<String> ledOn(@PathVariable String deviceId) {
        try {
            facade.controlLED(deviceId, 0, true);
            return ResponseEntity.ok("LED turned ON");
        } catch (ModbusRegistryException e) {
            return ResponseEntity.status(404).body("Device " + deviceId + " not found");
        }
    }

    @PostMapping("/devices/{deviceId}/led/off")
    public ResponseEntity<String> ledOff(@PathVariable String deviceId) {
        try {
            facade.controlLED(deviceId, 0, false);
            return ResponseEntity.ok("LED turned OFF");
        } catch (ModbusRegistryException e) {
            return ResponseEntity.status(404).body("Device " + deviceId + " not found");
        }
    }
}
