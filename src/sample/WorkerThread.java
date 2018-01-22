package sample;

import javafx.scene.control.Alert;

import java.awt.*;

public class WorkerThread extends Thread {

    public WorkerThread() {

    }

    @Override
    public void run() {
        System.out.println("Hello Thread");
        try {
            while(true){
                this.createMessage("Hello World!");
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
