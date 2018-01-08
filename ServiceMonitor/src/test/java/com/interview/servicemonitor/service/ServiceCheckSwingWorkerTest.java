/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.interview.servicemonitor.service;

import com.interview.servicemonitor.config.Service;
import com.interview.servicemonitor.enums.StatusEnum;
import java.util.Calendar;
import java.util.List;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author manuja
 */
public class ServiceCheckSwingWorkerTest {
    
    public ServiceCheckSwingWorkerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * Test of pingService method, of class ServiceCheckSwingWorker.
     */
    @org.junit.Test
    public void testPingService() {
        System.out.println("ping service");
        Service service = new Service();
        ServiceCheckSwingWorker instance =null;
        
        //Test When Server is UP
        service.setHost("www.google.lk");
        service.setPort(80);
        service.setServiceName("Google");
        service.setPollingFrequency(1000*60);
        instance =new ServiceCheckSwingWorker(null,service);
        StatusEnum expResultUp = StatusEnum.UP;
        StatusEnum resultUp = instance.pingService();
        assertEquals(expResultUp, resultUp);

        //Test When Server is Not working
        service.setHost("www.notworking.lk");
        service.setPort(123);
        service.setServiceName("NotWorking");
        service.setPollingFrequency(1000*60);
        service.setGracePeriod(1000*60);
        instance =new ServiceCheckSwingWorker(null,service);
        StatusEnum expResult = StatusEnum.DOWN;
        StatusEnum result = instance.pingService();
        assertEquals(expResult, result);
    }
    
    /**
     * Test of checkIsOnOutageTimer method, of class ServiceCheckSwingWorker.
     */
    @org.junit.Test
    public void testCheckIsOnOutageTimer() {
        System.out.println("Check is on Outage");
        Service service = new Service();
        ServiceCheckSwingWorker instance =null;
        
        Calendar now = Calendar.getInstance();
        now.set(Calendar.DAY_OF_MONTH, 1);
        now.set(Calendar.MONTH, Calendar.JANUARY);
        now.set(Calendar.YEAR, 1970);
            
        //Test When Server is Outage
        service.setHost("www.google.lk");
        service.setPort(80);
        service.setServiceName("Google");
        service.setPollingFrequency(1000*60);
        //Set Outage Start Time 2 minutes earlier current time
        now.add(Calendar.MINUTE, -2);
        service.setOutageStartTime(now.getTime());
        //Set Outage End Time 1 minutes after current time        
        now.add(Calendar.MINUTE, 3);        
        service.setOutageEndTime(now.getTime());
        
        instance =new ServiceCheckSwingWorker(null,service);
        StatusEnum expResultUp = StatusEnum.OUTAGE;
        StatusEnum resultUp = instance.checkIsOnOutageTimer(service);
        assertEquals(expResultUp, resultUp);

                
       //Test When Server is not in Outage timer
        service.setHost("www.google.lk");
        service.setPort(80);
        service.setServiceName("Google");
        service.setPollingFrequency(1000*60);
        //Set Outage Start Time 10 minutes after current time
        now.add(Calendar.MINUTE, 10);
        service.setOutageStartTime(now.getTime());
        //Set Outage End Time 20 minutes after current time        
        now.add(Calendar.MINUTE, 20);        
        service.setOutageEndTime(now.getTime());
        
        instance =new ServiceCheckSwingWorker(null,service);
        StatusEnum expResult = null;
        StatusEnum result = instance.checkIsOnOutageTimer(service);
        assertEquals(expResult, result);
    }
}
