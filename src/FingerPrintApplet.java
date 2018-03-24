import com.neurotec.biometrics.*;
import com.neurotec.biometrics.client.NBiometricClient;
import com.neurotec.devices.NDeviceManager;
import com.neurotec.devices.NDeviceType;
import com.neurotec.devices.NFScanner;
import com.neurotec.licensing.NLicense;

import java.applet.Applet;
import java.awt.*;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.EnumSet;

public class FingerPrintApplet extends Applet{

    private Image image;
    private TextField resultField = new TextField(100);
    private TextField user = new TextField(100);
    private Button submit = new Button("submit");
    private Button scan = new Button("Scan");
    final String components = "Devices.FingerScanners";
    final String fingerExtraction = "Biometrics.FingerExtraction";
    NDeviceManager deviceManager;
    NFScanner scanner;
    NFinger finger;
    NSubject subject;
    NBiometricClient biometricClient = new NBiometricClient();

    public void init(){
        initLayout();
        initVerifingerSDK();
    }

    public void initLayout(){
        add(new Label("Applet fingerprint test"), Label.LEFT);
        add(resultField);
        resultField.setEditable(false);
        resultField.setBounds(40,30,400,20);
        add(user);
        user.setBounds(40,400,400,20);
        scan.setBounds(450,30,50,20);
        add(scan);
        scan.addActionListener(e->{
            scanFinger();
        });
        add(submit);
        submit.setBounds(450,400,50,20);
        submit.addActionListener(e -> {
            sendTemplateToServer();
        });
        setLayout(null);
    }

    public void initVerifingerSDK(){
        LibraryManager.initLibraryPath();
        try {
            if (!NLicense.obtainComponents("/local", 5000, components) ||
                    !NLicense.obtainComponents("/local", 5000, fingerExtraction)) {
                System.err.println("Could not obtain licenses for components: " + components);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void scanFinger(){
        scan.setEnabled(false);
        finger = new NFinger();
        subject = new NSubject();
        subject.getFingers().add(finger);
        finger.setCaptureOptions(EnumSet.of(NBiometricCaptureOption.MANUAL));
        finger.setPosition(NFPosition.UNKNOWN);
        NBiometricStatus status = scanner.capture(finger, -1);
        if (status != NBiometricStatus.OK) {
            System.err.format("failed to capture from scanner, status: %s%n", status);
        }
        image = finger.getImage().toImage();
        scan.setEnabled(true);
        this.repaint();
    }

    public void sendTemplateToServer(){
        try {
            URL url = new URL(getCodeBase(), "/receiveTemplateServlet");
            URLConnection con = url.openConnection();
            if (con instanceof HttpURLConnection) {
                ((HttpURLConnection)con).setRequestMethod("POST");
            }
            con.setDoOutput(true);
            con.setUseCaches(false);
            con.setRequestProperty("Content-Type", "application/octet-stream");
            con.setRequestProperty("user",user.getText());

            BufferedOutputStream out = new BufferedOutputStream(con.getOutputStream());

            biometricClient.createTemplate(subject);
            byte[] template = subject.getTemplateBuffer().toByteArray();

            out.write(template,0,template.length);
            out.flush();
            out.close();

            BufferedReader in = new BufferedReader( new InputStreamReader(con.getInputStream()));
            String decodedString;
            while ((decodedString = in.readLine()) != null) {
                System.out.println(decodedString);
            }
            in.close();
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public void start(){
        deviceManager = new NDeviceManager();
        deviceManager.setDeviceTypes(EnumSet.of(NDeviceType.FINGER_SCANNER));
        deviceManager.setAutoPlug(true);
        deviceManager.initialize();
        scanner = (NFScanner)deviceManager.getDevices().get(0);
        resultField.setText("Found scanner: " + scanner.getDisplayName()+ "!") ;
    }

    public void paint(Graphics g){
        if(image != null)
            g.drawImage(image,100,50,this);
    }

}
