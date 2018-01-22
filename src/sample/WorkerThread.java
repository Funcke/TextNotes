package sample;

import javafx.scene.control.Alert;

import java.awt.*;
import java.util.ArrayList;

public class WorkerThread extends Thread {
    private ArrayList<Notification> notificationList;
    public WorkerThread(ArrayList<Notification> list){
        super();
        this.notificationList = list;
    }

    @Override
    public void run() {
        try {
            while(true){
                for(Notification n : notificationList) {
                  if((n.getNotificationTime()/1000) <= (System.currentTimeMillis()/1000) )
                      this.createMessage(n.getInformation().getContent());
                }
                this.sleep(5000);
            }

        }catch(AWTException|java.net.MalformedURLException|InterruptedException err ) {
            Alert info = new Alert(Alert.AlertType.ERROR);
            info.setContentText(err.getMessage());
        }
    }

    private void createMessage(String notificationText) throws AWTException, java.net.MalformedURLException {
        SystemTray tray = SystemTray.getSystemTray();

        Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
        TrayIcon ti = new TrayIcon(image, "Information");
        ti.setImageAutoSize(true);
        ti.setToolTip("You have a notification!!");
        tray.add(ti);
        ti.displayMessage("Attention!!", notificationText, TrayIcon.MessageType.INFO);
    }
}
