package com.scada.ScadaCR.modbus.polling;

import com.scada.ScadaCR.modbus.exceptions.ModbusCommunicationException;
import com.scada.ScadaCR.modbus.manager.CommunicationManagerModbus;
import com.scada.ScadaCR.modbus.registry.CommunicationManagerModbusRegistry;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class ModbusPollingScheduler {

    private static final Logger logger = LoggerFactory.getLogger(ModbusPollingScheduler.class);

    private final CommunicationManagerModbusRegistry registry;
    private final ExecutorService executorService;

    public ModbusPollingScheduler(CommunicationManagerModbusRegistry registry){
        this.registry = registry;
        this.executorService = Executors.newFixedThreadPool(5);
    }

    @Scheduled(fixedDelay = 100)
    public void poll(){
        for(CommunicationManagerModbus manager : registry.getAllManagers()) {
            if(!manager.isActive()) {
                logger.debug("Manager {} is not active, skipping cycle", manager.getIdentifier());
                continue;
            }
            if(!manager.tryStartCycle()) {
                logger.debug("Manager {} is already running a cycle", manager.getIdentifier());
                continue;
            }
            executorService.submit(()->{
               try{
                   logger.info("Starting cycle for manager {}", manager.getIdentifier());
                   manager.executeCycle();
                   logger.info("Cycle completed successfully for manager {}", manager.getIdentifier());
               }catch(ModbusCommunicationException e){
                   logger.error("Error during polling cycle for manager {}: {}",
                           manager.getIdentifier(), e.getMessage(), e);
               }finally{
                   manager.finishCycle();
               }
            });
        }
    }

    @PreDestroy
    public void shutdown(){
        executorService.shutdown();
    }
}
