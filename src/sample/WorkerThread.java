package sample;

import javafx.scene.control.Alert;

import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

/**
 * @author Jonas Funcke
 */
public class WorkerThread extends Thread {
    private ArrayList<Notification> notificationList;
    private String username;
    private Connection conn;
    private PreparedStatement stmt;
    public WorkerThread(String un, ArrayList<Notification> list) throws SQLException{
        super();
        this.username = un;
        this.notificationList = list;
        conn = DriverManager.getConnection("jdbc:sqlite:posts.db");
        stmt = conn.prepareStatement("SELECT message, time FROM notifications WHERE owner = ?");
        stmt.setString(1, username);
        ResultSet rs = stmt.executeQuery();
        while(rs.next()) {
            notificationList.add(new Notification(rs.getString(1), rs.getLong(2)));
        }
    }

    @Override
    public void run() {
        try {
            while(true){
                Notification n;
                for(int i = 0 ; i < notificationList.size(); i++) {
                    n = (Notification)notificationList.toArray()[i];

                  if((n.getNotificationTime()/1000) <= (System.currentTimeMillis()/1000) ){
                      System.err.println(n.getInformation());
                      this.createMessage(n.getInformation());
                      notificationList.remove(i);
                      try {
                          stmt = conn.prepareStatement("DELETE FROM notifications WHERE owner = ? AND message = ? AND time = ?");
                          stmt.setString(1, this.username);
                          stmt.setString(2, n.getInformation());
                          stmt.setLong(3, n.getNotificationTime());
                          stmt.execute();
                      }catch(SQLException err) {
                          Alert info = new Alert(Alert.AlertType.ERROR);
                          info.setContentText(err.getMessage());
                          info.show();
                      }
                  }

                }

            }
        }catch(AWTException|java.net.MalformedURLException err ) {
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
