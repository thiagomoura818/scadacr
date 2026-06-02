package com.scada.ScadaCR.application.repository;

import com.scada.ScadaCR.application.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    boolean existsByDeviceId(String deviceId);
    boolean existsByName(String name);
    Device findByDeviceId(String deviceId);
}
