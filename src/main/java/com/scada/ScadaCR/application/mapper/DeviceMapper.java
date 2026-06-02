package com.scada.ScadaCR.application.mapper;

import com.scada.ScadaCR.application.dto.device.DeviceCreateRequest;
import com.scada.ScadaCR.application.dto.device.DeviceResponse;
import com.scada.ScadaCR.application.dto.device.DeviceUpdateRequest;
import com.scada.ScadaCR.application.model.Device;

public class DeviceMapper {

    public static DeviceResponse toResponse(Device device){
        return new DeviceResponse(
                device.getId(),
                device.getDeviceId(),
                device.getName(),
                device.getProtocol(),
                device.isEnabled(),
                device.getProtocolConfig()
        );
    }

    public static Device toEntity(DeviceCreateRequest request){
        Device device = new Device();
        device.setDeviceId(request.deviceId());
        device.setEnabled(request.enabled());
        device.setName(request.name());
        device.setProtocol(request.protocol());
        device.setProtocolConfig(request.protocolConfig());

        return device;

    }

    public static void updateEntity(DeviceUpdateRequest request, Device device){
        device.setDeviceId(request.deviceId());
        device.setEnabled(request.enabled());
        device.setName(request.name());
        device.setProtocol(request.protocol());
        device.setProtocolConfig(request.protocolConfig());

    }
}
