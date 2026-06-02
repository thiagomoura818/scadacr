package com.scada.ScadaCR.application.protocol.factory;

import com.scada.ScadaCR.application.model.enums.ProtocolType;
import com.scada.ScadaCR.application.protocol.ProtocolValidator;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ProtocolValidatorFactory {
    private final Map<ProtocolType, ProtocolValidator> validators;

    public ProtocolValidatorFactory(List<ProtocolValidator> validatorList){
        this.validators = validatorList.stream()
                .collect(Collectors.toMap(ProtocolValidator::getSupportedProtocol, v->v));

    }

    public ProtocolValidator getValidator(ProtocolType type){
        ProtocolValidator validator = validators.get(type);
        if(validator == null)
            throw new UnsupportedOperationException("Ainda nao existe validacao para o protocolo: ", type);

        return validator;
    }
}
