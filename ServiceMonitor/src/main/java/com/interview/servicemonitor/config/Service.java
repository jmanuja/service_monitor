
package com.interview.servicemonitor.config;

import java.util.Date;

/**
 * @author manuja
 * Name : Service Class
 * To Configuring the service properties
 */
public class Service {
    private String host;                 			
    private int port; 				
    private String serviceName; 
    private String serviceStatus; 
    private Date outageStartTime;
    private Date outageEndTime;
    private int gracePeriod; 				
    private int status; 				
    private Date lastSyncTime; 				
    private int pollingFrequency; 				
			
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceStatus() {
        return serviceStatus;
    }

    public void setServiceStatus(String serviceStatus) {
        this.serviceStatus = serviceStatus;
    }

    public Date getOutageStartTime() {
        return outageStartTime;
    }

    public void setOutageStartTime(Date outageStartTime) {
        this.outageStartTime = outageStartTime;
    }

    public Date getOutageEndTime() {
        return outageEndTime;
    }

    public void setOutageEndTime(Date outageEndTime) {
        this.outageEndTime = outageEndTime;
    }

    public int getGracePeriod() {
        return gracePeriod;
    }

    public void setGracePeriod(int gracePeriod) {
        this.gracePeriod = gracePeriod;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getLastSyncTime() {
        return lastSyncTime;
    }

    public void setLastSyncTime(Date lastSyncTime) {
        this.lastSyncTime = lastSyncTime;
    }

    public int getPollingFrequency() {
        return pollingFrequency;
    }

    public void setPollingFrequency(int pollingFrequency) {
        this.pollingFrequency = pollingFrequency;
    }
    
}
