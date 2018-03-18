import jdk.internal.util.xml.impl.Input;
import sun.misc.IOUtils;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;

@WebServlet(name = "ReceiveImageServlet")
public class ReceiveImageServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //BufferedReader isr = new BufferedReader(new InputStreamReader(request.getInputStream()));
        //String user = isr.readLine();

        BufferedInputStream reader = new BufferedInputStream(request.getInputStream());
        byte[] buff = new byte[1000];
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        int bytesRead = 0;
        while ((bytesRead = reader.read(buff)) != -1)
            output.write(buff,0,bytesRead);
        byte[] img = output.toByteArray();
        int size = img.length;
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(img));
        String user = request.getHeader("user");
        ImageIO.write(image,"png", new File("D:\\" + user + ".png"));
        System.out.println("file written");
    }
}
