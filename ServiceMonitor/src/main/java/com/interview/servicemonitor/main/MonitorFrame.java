package com.interview.servicemonitor.main;

import com.interview.servicemonitor.config.Service;
import com.interview.servicemonitor.model.ServiceStatusRenderer;
import com.interview.servicemonitor.service.ServiceCheckSwingWorker;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.text.DateFormatter;

/**
 * @author manuja
 * Name : MonitorFrame
 * Main Window of the System
 * Main Functions : Service Register and Stop Current Services 
 */
public class MonitorFrame extends JFrame{
    
    SwingWorker swingWorker;

    private DefaultListModel<Service> serviceStatusModel;
    JList<Service> serviceStatusList;
    private Map<String,Integer> statusMap = new ConcurrentHashMap<>();    
    private Map<String,Service> runningServiceMap = new ConcurrentHashMap<>();    
     
    private JPanel serviceDetailPanel,serviceStatusPanel;
    private JTextField serviceNameTextField,hostTextField,portTextField,pollingTextField,graceTextField;
    private JButton registerButton , terminateButton;
    private JSpinner outageStartSpinner,outageEndSpinner;
    private JScrollPane jScrollPane;

    public MonitorFrame(){
        super("Service Monitor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.initilizeWindow();

        //Service Registering Function
        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                  public void run() {
                    try {
                        String host = hostTextField.getText().trim(); 			
                        int port ="".equals(portTextField.getText().trim())?0: Integer.parseInt(portTextField.getText().trim()); 				
                        String serviceName = serviceNameTextField.getText().trim(); 
                        Date outageStartTime = (Date) outageStartSpinner.getValue();
                        Date outageEndTime = (Date) outageEndSpinner.getValue();
                        int gracePeriod ="".equals(graceTextField.getText().trim())?1000: Integer.parseInt(graceTextField.getText().trim())*60*1000; 				
                        int polling ="".equals(pollingTextField.getText().trim())?1000: Integer.parseInt(pollingTextField.getText().trim())*60*1000; 				
                        if(!"".equals(host) || port !=0 || !"".equals(serviceName)){
                            if(!runningServiceMap.containsKey(serviceName)){
                                Service selectedItem = new Service();
                                selectedItem.setHost(host);
                                selectedItem.setPort(port);
                                selectedItem.setServiceName(serviceName);
                                selectedItem.setPollingFrequency(polling);
                                if(outageStartTime.compareTo(outageEndTime)!=0 && outageStartTime.before(outageEndTime)){
                                    selectedItem.setOutageStartTime(outageStartTime);
                                    selectedItem.setOutageEndTime(outageEndTime);
                                }
                                selectedItem.setGracePeriod(gracePeriod);
                                runningServiceMap.put(serviceName, selectedItem);
                                swingWorker = new ServiceCheckSwingWorker(MonitorFrame.this,selectedItem);
                                swingWorker.execute();
                                clearWindow();
                            }else{
                                showMessage("Service Already Running");
                            }
                        }else{
                            showMessage("Please fill all Mandotory Fields");
                        }
                    }catch (Exception e) {
                        showMessage(e.getMessage());
                        e.printStackTrace();
                    }
                  }
                });
            }
        });
        
        //Service Terminate Function
        terminateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                  public void run() {
                    try {
                        String serviceName = serviceNameTextField.getText().trim(); 
                        if( !"".equals(serviceName)){
                            if(checkServiceIsRunning(serviceName)){
                                runningServiceMap.remove(serviceName);
                                swingWorker.cancel(true);
                                serviceStatusModel.removeElement(serviceStatusList.getSelectedValue());
                                statusMap.remove(serviceName);
                                clearWindow();
                            }else{
                                showMessage("Service Not Running");
                            }
                        }else{
                            showMessage("Please Select a Service");
                        }
                    }catch (Exception e) {
                        showMessage(e.getMessage());
                        e.printStackTrace();
                    }
                  }
                });
            }
        });
        
        //Service Select Function 
        //Trigger Event : Mouse Click Event on the Service from the Running List
        MouseListener mouseListener = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            Service service = serviceStatusList.getSelectedValue();
                            hostTextField.setText(service.getHost()); 			
                            portTextField.setText(service.getPort()+""); 				
                            serviceNameTextField.setText(service.getServiceName()); 
                            pollingTextField.setText(service.getPollingFrequency()/(60*1000)+"");
                            if(service.getOutageStartTime()!=null && service.getOutageEndTime()!=null){
                                outageStartSpinner.setValue(service.getOutageStartTime());
                                outageEndSpinner.setValue(service.getOutageEndTime());                                    
                            }else{
                                Calendar cal = Calendar.getInstance();
                                cal.set(Calendar.HOUR_OF_DAY, 0);
                                cal.set(Calendar.MINUTE, 0);
                                outageStartSpinner.setValue(cal.getTime());
                                outageEndSpinner.setValue(cal.getTime());     
                            }
                            graceTextField.setText(service.getGracePeriod()/(60*1000)+""); 				
                        }
                    });
                }
            }
        };
        serviceStatusList.addMouseListener(mouseListener);
        
        this.setSize(350, 620);
        this.setResizable(false);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(serviceDetailPanel, BorderLayout.CENTER);
        contentPane.add(serviceStatusPanel, BorderLayout.SOUTH);        
        pack();
    }
    


    /**
    * Initialize Window Components    *
    */
    public void initilizeWindow(){  
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        
        serviceDetailPanel = new JPanel(new FlowLayout());
        serviceStatusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); 
        jScrollPane = new JScrollPane();
        
        serviceStatusModel = new DefaultListModel<>();
        serviceStatusList = new JList<>(serviceStatusModel);
        serviceStatusList.setFixedCellHeight(40);
        serviceStatusList.setFixedCellWidth(340);
        jScrollPane = new JScrollPane(serviceStatusList);
        jScrollPane.setPreferredSize(new Dimension(340, 340));
        serviceStatusPanel.add(jScrollPane);
        serviceStatusList.setCellRenderer(new ServiceStatusRenderer());        
        
        JLabel title = new JLabel("Service Monitor");
        title.setFont(new Font("Monospace", Font.PLAIN, 22));
        title.setHorizontalAlignment(JTextField.CENTER);
        title.setPreferredSize(new Dimension(350, 50));

        serviceDetailPanel.setPreferredSize(new Dimension(350, 270));
        serviceStatusPanel.setBackground(Color.BLUE);
        serviceStatusPanel.setPreferredSize(new Dimension(350,350));

        JLabel serviceNameLabel = new JLabel("<html> Service Name <font color='red'>( * )</font></html>");
        serviceNameLabel.setPreferredSize(new Dimension(170,20));
        JLabel hostLabel = new JLabel("<html> Host <font color='red'>( * )</font></html>");
        hostLabel.setPreferredSize(new Dimension(170,20));
        JLabel portLabel= new JLabel("<html> Port <font color='red'>( * )</font></html>");
        portLabel.setPreferredSize(new Dimension(170,20));
        JLabel pollingLabel= new JLabel("Polling Frequency (Minute)");
        pollingLabel.setPreferredSize(new Dimension(170,20));        
        JLabel outageStartLabel= new JLabel("Outage Start Time");
        outageStartLabel.setPreferredSize(new Dimension(170,20));
        JLabel outageEndLabel= new JLabel("Outage End Time");
        outageEndLabel.setPreferredSize(new Dimension(170,20));
        JLabel graceLabel= new JLabel("Grace Period (Minute)");
        graceLabel.setPreferredSize(new Dimension(170,20));

        serviceNameTextField= new JTextField();
        serviceNameTextField.setPreferredSize(new Dimension(150,20));
        hostTextField = new JTextField();
        hostTextField.setPreferredSize(new Dimension(150,20));
        portTextField= new JTextField();
        portTextField.setPreferredSize(new Dimension(150,20));
        graceTextField= new JTextField();
        graceTextField.setPreferredSize(new Dimension(150,20));
        pollingTextField= new JTextField();
        pollingTextField.setPreferredSize(new Dimension(150,20));
        outageStartSpinner = new JSpinner( new SpinnerDateModel() );
        JSpinner.DateEditor outageStartEditor = new JSpinner.DateEditor(outageStartSpinner, "HH:mm");
        DateFormatter startFormatter = (DateFormatter)outageStartEditor.getTextField().getFormatter();
        startFormatter.setAllowsInvalid(false); 
        startFormatter.setOverwriteMode(true);
        outageStartSpinner.setEditor(outageStartEditor);
        outageStartSpinner.setValue(cal.getTime()); 
        outageStartSpinner.setPreferredSize(new Dimension(150,20));
        outageEndSpinner = new JSpinner( new SpinnerDateModel() );
        JSpinner.DateEditor outageEndEditor = new JSpinner.DateEditor(outageEndSpinner, "HH:mm");
        DateFormatter endFormatter = (DateFormatter)outageEndEditor.getTextField().getFormatter();
        endFormatter.setAllowsInvalid(false); 
        endFormatter.setOverwriteMode(true);
        outageEndSpinner.setEditor(outageEndEditor);
        outageEndSpinner.setValue(cal.getTime()); 
        outageEndSpinner.setPreferredSize(new Dimension(150,20));
        
        serviceDetailPanel.add(title);
        serviceDetailPanel.add(serviceNameLabel);
        serviceDetailPanel.add(serviceNameTextField);
        serviceDetailPanel.add(hostLabel);
        serviceDetailPanel.add(hostTextField);
        serviceDetailPanel.add(portLabel);
        serviceDetailPanel.add(portTextField);
        serviceDetailPanel.add(pollingLabel);
        serviceDetailPanel.add(pollingTextField);
        serviceDetailPanel.add(outageStartLabel);
        serviceDetailPanel.add(outageStartSpinner);
        serviceDetailPanel.add(outageEndLabel);
        serviceDetailPanel.add(outageEndSpinner);
        serviceDetailPanel.add(graceLabel);
        serviceDetailPanel.add(graceTextField);
        
        registerButton = new JButton("Register");
        registerButton.setPreferredSize( new Dimension(100, 30));
        registerButton.setLayout(new FlowLayout(FlowLayout.LEFT)); 
        serviceDetailPanel.add(registerButton);

        terminateButton = new JButton("Stop");
        terminateButton.setPreferredSize( new Dimension(100, 30));
        terminateButton.setLayout(new FlowLayout(FlowLayout.RIGHT)); 
        serviceDetailPanel.add(terminateButton);

        pack();
    }

    /**
    * Use to alert Messages     *
    */
    public void showMessage(String msg) {
        JOptionPane optionPane = new JOptionPane("Message");
        optionPane.showMessageDialog(this, msg); 
    }

    /**
    * Use to Add Service Status to interface *
    * @param : runningService Service Object with Current Status 
    */
    public void showServiceStatus(Service runningService) {
        if(checkServiceIsRunning(runningService.getServiceName())){
            if(statusMap.containsKey(runningService.getServiceName().trim())){
               int index= statusMap.get(runningService.getServiceName().trim());
               serviceStatusModel.setElementAt(runningService,index);
            }else{
               serviceStatusModel.add(statusMap.size(), runningService);
               statusMap.put(runningService.getServiceName().trim(),statusMap.size());
            } 
        }

    }
    
   /**
    * Use to Get Service by Name *
    * @param : serviceName Name of the Service
    * @return : Service 
    */    
    public Service getServiceByName(String serviceName) {
        return runningServiceMap.get(serviceName);
    }

    /**
    * Use to Check the Server is running *
    * @param : serviceName Name of the Service
    * @return : boolean TRUE if server is running else FALSE
    */    
    public boolean checkServiceIsRunning(String serviceName) {
        return runningServiceMap.containsKey(serviceName);
    }

    /**
    * Use too Clear the interface    *
    */    
    public void clearWindow(){
        hostTextField.setText(""); 			
        portTextField.setText(""); 				
        serviceNameTextField.setText(""); 
        pollingTextField.setText("");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        outageStartSpinner.setValue(cal.getTime());
        outageEndSpinner.setValue(cal.getTime());     
        graceTextField.setText("");        
    }
}
