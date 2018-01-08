package com.interview.servicemonitor.service;

import com.interview.servicemonitor.enums.StatusEnum;
import com.interview.servicemonitor.main.MonitorFrame;
import com.interview.servicemonitor.config.Service;
import java.io.IOException;
import java.net.Socket;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.SwingWorker;

/**
* @author manuja
* This Will do the Background Work and update the window
*/  
public class ServiceCheckSwingWorker extends SwingWorker<Service, Service> {

    private DefaultListModel<Service> serviceModel;
    private MonitorFrame window ;
    private Service selectedService;
    private Socket socket;	
    
    public ServiceCheckSwingWorker(MonitorFrame window,Service service)
    {
        this.window = window;
        this.serviceModel  = new DefaultListModel<Service>(); 
        this.selectedService = service;
    }
    
    @Override
    protected Service doInBackground() throws Exception {
        try{
            while(window.checkServiceIsRunning(selectedService.getServiceName())){
                selectedService = window.getServiceByName(selectedService.getServiceName());
                if(selectedService!=null){
                    StatusEnum status = checkIsOnOutageTimer(selectedService);
                    if(status!=null){
                        selectedService.setStatus(StatusEnum.OUTAGE.getId());
                    }else{
                        status = pingService();
                    }
                    
                    selectedService.setStatus(status.getId());
                    selectedService.setServiceStatus(status.getStatus());
                    publish(selectedService);
                    if(selectedService.getStatus() == StatusEnum.DOWN.getId()){
                        Thread.sleep(selectedService.getGracePeriod());
                    }
                    System.out.println(selectedService.getServiceName()+" IS "+ status.getStatus());
                }
            }
        } catch (InterruptedException e){
            doInBackground();
        }
        catch(Exception ex){
                ex.printStackTrace();
        }
        return selectedService;
    }
    
    /**
    * Process the messages from Background Process     *
    * @param  List<Service>
    */
    @Override
    protected void process(List<Service> chunk) {
        if(selectedService.getStatus()!=StatusEnum.TERMINATED.getId()){
            for(Service s:chunk){
                window.showServiceStatus(s);
            }
        }
    }    
    
    /**
    * Process after the Background Process is done 
    */
    @Override
    protected void done() {
        selectedService.setStatus(StatusEnum.TERMINATED.getId());
        selectedService.setServiceStatus(StatusEnum.TERMINATED.getStatus());
        window.showServiceStatus(selectedService);
    }

    /**
    * Check the Service Status
    * @return StatusEnum Status of the Service
    */
    public StatusEnum pingService(){
        try{
            if(selectedService.getStatus()==StatusEnum.UP.getId() && window.checkServiceIsRunning(selectedService.getServiceName()))
                Thread.sleep(selectedService.getPollingFrequency());
            
            socket = new Socket(selectedService.getHost(), selectedService.getPort());
            if(socket.isConnected()){
                selectedService.setLastSyncTime(new Date());
                return StatusEnum.UP;
            }
        }catch (Exception e) {
            return StatusEnum.DOWN;
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
            	e.printStackTrace();
            }
        }
        return StatusEnum.DOWN;
    }

    /**
    * Check the Service is Set outage Time and current status
    * @return StatusEnum Status of the Service
    * OUTAGE if service currently on outage period else null
    */    
    public StatusEnum checkIsOnOutageTimer(Service selectedService){
        if(selectedService.getOutageStartTime()!=null && selectedService.getOutageEndTime()!=null){
            Calendar now = Calendar.getInstance();
            now.set(Calendar.DAY_OF_MONTH, 1);
            now.set(Calendar.MONTH, Calendar.JANUARY);
            now.set(Calendar.YEAR, 1970);

            if(selectedService.getOutageStartTime().getTime()<=now.getTimeInMillis() && selectedService.getOutageEndTime().getTime()>=now.getTimeInMillis()){
                return StatusEnum.OUTAGE;
            }
        }
        return null;
    }
    
}
