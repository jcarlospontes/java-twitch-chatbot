import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;

public class Notifica {

    //notifica
    private String icon;
    private String botname;
    private SystemTray tray;
    private Image image;
    private static TrayIcon trayIcon;



    public Notifica(String botname, String icon){
        this.icon = icon;
        this.botname = botname;
        tray = SystemTray.getSystemTray();
        image = Toolkit.getDefaultToolkit().createImage(this.icon);
        trayIcon = new TrayIcon(image, "Tray Demo");
    }

    public void notificaMsg() throws AWTException{

        trayIcon.setImageAutoSize(true);

        trayIcon.setToolTip(this.botname+" Twitch ChatBot");

        tray.add(trayIcon);

        trayIcon.displayMessage("Hello, World", "notification demo", MessageType.INFO);
    }
    
    public void closeMsg(){
        tray.remove(trayIcon);
    }
}
