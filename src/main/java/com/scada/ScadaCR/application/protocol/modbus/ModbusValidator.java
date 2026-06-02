package com.scada.ScadaCR.application.protocol.modbus;

import com.scada.ScadaCR.application.model.enums.ProtocolType;
import com.scada.ScadaCR.application.protocol.ProtocolValidator;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ModbusValidator implements ProtocolValidator {
    @Override
    public ProtocolType getSupportedProtocol(){
        return ProtocolType.MODBUS_TCP;
    }

    @Override
    public void validate(Map<String, Object> configMap){

    }
}
