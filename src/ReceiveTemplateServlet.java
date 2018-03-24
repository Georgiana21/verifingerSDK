import com.neurotec.io.NBuffer;
import com.neurotec.io.NFile;
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

@WebServlet(name = "ReceiveTemplateServlet")
public class ReceiveTemplateServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        BufferedInputStream reader = new BufferedInputStream(request.getInputStream());
        byte[] buff = new byte[1000];
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        int bytesRead = 0;
        while ((bytesRead = reader.read(buff)) != -1)
            output.write(buff,0,bytesRead);
        byte[] template = output.toByteArray();
        int size = template.length;
        String user = request.getHeader("user");
        NBuffer buffer = new NBuffer(template);
        NFile.writeAllBytes("D:\\"+user, buffer);
        System.out.println("file written");
    }
}
