package com.scada.ScadaCR.modbus.polling;

import com.scada.ScadaCR.modbus.exceptions.ModbusCommunicationException;
import com.scada.ScadaCR.modbus.manager.CommunicationManagerModbus;
import com.scada.ScadaCR.modbus.registry.CommunicationManagerModbusRegistry;
import jakarta.annotation.PreDestroy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class ModbusPollingScheduler {

    private final CommunicationManagerModbusRegistry registry;
    private final ExecutorService executorService;

    public ModbusPollingScheduler(CommunicationManagerModbusRegistry registry){
        this.registry = registry;
        this.executorService = Executors.newFixedThreadPool(5);
    }

    @Scheduled(fixedDelay = 1000)
    public void poll(){
        for(CommunicationManagerModbus manager : registry.getAllManagers()) {
            if(!manager.isActive())
                continue;

            if(!manager.tryStartCycle())
                continue;

            executorService.submit(()->{
               try{
                   manager.executeCycle();
               }catch(ModbusCommunicationException e){
                   System.out.println(e);
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
