package com.interview.servicemonitor.main;

import javax.swing.SwingUtilities;


/**
 *
 * @author manuja
 * Main Class to execute the service monitor
 */
public class ServiceMonitorMain {
    
    public static void main(String[] args)
    {
        try {
            SwingUtilities.invokeLater(new Runnable()
            {
                @Override public void run()
                {   
                    MonitorFrame window = new MonitorFrame();
                    window.setVisible(true);
                }
            });
            
        } catch (Exception ex) {
             ex.printStackTrace();
        }
    }
   
}

