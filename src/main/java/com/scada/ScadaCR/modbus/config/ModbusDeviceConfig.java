package com.scada.ScadaCR.modbus.config;

public class ModbusDeviceConfig {
    private final String identifier;
    private final String host;
    private final Integer port;
    private final boolean active;

    public ModbusDeviceConfig(String identifier, String host, Integer port, boolean active) {
        this.identifier = identifier;
        this.host = host;
        this.port = port;
        this.active = active;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getHost() {
        return host;
    }

    public Integer getPort() {
        return port;
    }

    public boolean isActive() {
        return active;
    }
}
