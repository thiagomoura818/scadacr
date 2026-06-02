package com.scada.ScadaCR.application.service;

import com.scada.ScadaCR.application.dto.device.DeviceCreateRequest;
import com.scada.ScadaCR.application.dto.device.DeviceResponse;
import com.scada.ScadaCR.application.dto.device.DeviceUpdateRequest;
import com.scada.ScadaCR.application.mapper.DeviceMapper;
import com.scada.ScadaCR.application.model.Device;
import com.scada.ScadaCR.application.protocol.ProtocolValidator;
import com.scada.ScadaCR.application.protocol.factory.ProtocolValidatorFactory;
import com.scada.ScadaCR.application.repository.DeviceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final ProtocolValidatorFactory validatorFactory;

    public List<DeviceResponse> findAll(){
        return deviceRepository.findAll()
                .stream().map(DeviceMapper::toResponse)
                .toList();
    }

    public DeviceResponse findById(Long id){
        return DeviceMapper.toResponse(deviceRepository.findById(id).orElseThrow(
                ()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Esse id nao existe")
        ));
    }

    @Transactional
    public DeviceResponse insert(DeviceCreateRequest request){
        if(deviceRepository.existsByDeviceId(request.deviceId()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Esse deviceId já existe");

        if(deviceRepository.existsByName(request.name()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Esse nome já está cadastrado");

        ProtocolValidator validator = validatorFactory.getValidator(request.protocol());

        validator.validate(request.protocolConfig());

        Device device = DeviceMapper.toEntity(request);
        Device savedDevice = deviceRepository.save(device);

        return DeviceMapper.toResponse(savedDevice);
    }

    @Transactional
    public DeviceResponse update(Long id, DeviceUpdateRequest request){

        Device device = deviceRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND));

        Device deviceByDeviceId =
                deviceRepository.findByDeviceId(request.deviceId());

        if(deviceByDeviceId != null &&
                !deviceByDeviceId.getId().equals(device.getId())) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Esse deviceId já existe"
            );
        }

        DeviceMapper.updateEntity(request, device);

        return DeviceMapper.toResponse(
                deviceRepository.save(device)
        );
    }
}
