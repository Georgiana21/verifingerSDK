import com.neurotec.biometrics.NBiometricStatus;
import com.neurotec.biometrics.NFPosition;
import com.neurotec.biometrics.NFinger;
import com.neurotec.devices.NDeviceManager;
import com.neurotec.devices.NDeviceType;
import com.neurotec.devices.NFScanner;
import com.neurotec.licensing.NLicense;

import javax.imageio.ImageIO;
import java.applet.Applet;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.EnumSet;

public class FingerPrintApplet extends Applet implements Runnable {

    private Image image;
    private TextField resultField = new TextField(100);
    private TextField user = new TextField(100);
    private Button submit = new Button("submit");
    final String components = "Devices.FingerScanners";
    NDeviceManager deviceManager;

    public void init(){
        add(new Label("Applet fingerprint test"), Label.LEFT);
        add(resultField);
        resultField.setEditable(false);
        resultField.setBounds(40,30,500,20);
        add(user);
        user.setBounds(40,400,500,20);
        add(submit);
        submit.setBounds(280,420,50,20);
        submit.addActionListener(e -> {
            try {
                URL url = new URL(getCodeBase(), "/receiveImageServlet");
                URLConnection con = url.openConnection();
                if (con instanceof HttpURLConnection) {
                    ((HttpURLConnection)con).setRequestMethod("POST");
                }
                con.setDoOutput(true);
                con.setUseCaches(false);
                con.setRequestProperty("Content-Type", "application/octet-stream");
                con.setRequestProperty("user",user.getText());

                //OutputStreamWriter writer= new OutputStreamWriter(con.getOutputStream());
                //writer.write(user.getText() + "\n");
                //writer.flush();

                BufferedOutputStream out = new BufferedOutputStream(con.getOutputStream());

                ByteArrayOutputStream bytesToSend = new ByteArrayOutputStream();
                ImageIO.write(toBufferedImage(image),"png", bytesToSend);
                byte[] img = bytesToSend.toByteArray();

                out.write(img,0,img.length);
                out.flush();
                out.close();

                BufferedReader in = new BufferedReader( new InputStreamReader(con.getInputStream()));
                String decodedString;
                while ((decodedString = in.readLine()) != null) {
                    System.out.println(decodedString);
                }
                in.close();

                resultField.setText("no error!!!");
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
                resultField.setText(e1.getMessage());
            } catch (IOException e1) {
                e1.printStackTrace();
                resultField.setText(e1.getMessage());
            }
        });
        setLayout(null);
        LibraryManager.initLibraryPath();
        try {
            if (!NLicense.obtainComponents("/local", 5000, components)) {
                System.err.println("Could not obtain licenses for components: " + components);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BufferedImage toBufferedImage(Image img)
    {
        if (img instanceof BufferedImage)
        {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }

    public void start(){
        deviceManager = new NDeviceManager();
        deviceManager.setDeviceTypes(EnumSet.of(NDeviceType.FINGER_SCANNER));
        deviceManager.setAutoPlug(true);
        deviceManager.initialize();
        String result="Device manager created. found scanners: " + deviceManager.getDevices().size()+".";
        resultField.setText(result);

        Thread thread=new Thread(this);
        thread.start();
    }

    public void run(){
        if(deviceManager.getDevices().size() > 0){
            String result = resultField.getText();
            NFScanner scanner = (NFScanner)deviceManager.getDevices().get(0);
            result+="Found scanner: " + scanner.getDisplayName();
            resultField.setText(result);

            while(true) {
                NFinger biometric = null;
                try {
                    biometric = new NFinger();
                    biometric.setPosition(NFPosition.UNKNOWN);
                    NBiometricStatus status = scanner.capture(biometric, -1);
                    if (status != NBiometricStatus.OK) {
                        System.err.format("failed to capture from scanner, status: %s%n", status);
                    }
                    image = biometric.getImage().toImage();//.save("image");
                    System.out.println(" image captured");
                    this.repaint();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (biometric != null) biometric.dispose();
                }
            }
        }
    }

    public void paint(Graphics g){
        if(image != null)
            g.drawImage(image,100,50,this);
    }
}
