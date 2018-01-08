
package com.interview.servicemonitor.model;

import com.interview.servicemonitor.config.Service;
import com.interview.servicemonitor.enums.StatusEnum;
import java.awt.Component;
import java.awt.Image;
import java.text.SimpleDateFormat;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
* @author manuja
* This Will Render the Service List According to Service Model      *
*/  
public class ServiceStatusRenderer extends JLabel implements ListCellRenderer<Service>  {
    ImageIcon imageIcon;
    SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

    public ServiceStatusRenderer() {
        setOpaque(true);
    }
    
    /**
    * Render the list Style by its event      *
    */ 
    @Override
    public Component getListCellRendererComponent(JList<? extends Service> list, Service serviceModel, int index, boolean isSelected, boolean cellHasFocus) {
        String name = serviceModel.getServiceName();
        String date = serviceModel.getLastSyncTime()!=null?" ( " +dateFormatter.format(serviceModel.getLastSyncTime()) + " ) ":"";
        
        if(serviceModel.getStatus() == StatusEnum.UP.getId()){
            imageIcon = new ImageIcon( getClass().getResource("/images/online.png"));
            Image image = imageIcon.getImage(); 
            Image newimg = image.getScaledInstance(40, 40,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  
            imageIcon = new ImageIcon(newimg);  
        } if(serviceModel.getStatus() == StatusEnum.DOWN.getId()){
            imageIcon = new ImageIcon( getClass().getResource("/images/offline.png"));
            Image image = imageIcon.getImage(); 
            Image newimg = image.getScaledInstance(40, 40,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  
            imageIcon = new ImageIcon(newimg);  
        }else if(serviceModel.getStatus() == StatusEnum.TERMINATED.getId()){
            imageIcon = new ImageIcon( getClass().getResource("/images/terminated.jpg"));
            Image image = imageIcon.getImage(); 
            Image newimg = image.getScaledInstance(40, 40,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  
            imageIcon = new ImageIcon(newimg);  
        }
        else if(serviceModel.getStatus() == StatusEnum.OUTAGE.getId()){
            imageIcon = new ImageIcon( getClass().getResource("/images/silence.png"));
            Image image = imageIcon.getImage(); 
            Image newimg = image.getScaledInstance(40, 40,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  
            imageIcon = new ImageIcon(newimg);  
        }
        setVerticalAlignment(JLabel.TOP);
        setVerticalTextPosition(JLabel.TOP);
        setIcon(imageIcon);
        setText("<html>"+ name + " "+  serviceModel.getServiceStatus()  + " " + date+ " </html>");

        return this;
    }
    
}
