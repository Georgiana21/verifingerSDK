import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.SQLException;

@WebServlet(name = "IdentifyServlet")
public class IdentifyServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        BufferedInputStream reader = new BufferedInputStream(request.getInputStream());
        byte[] buff = new byte[1000];
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        int bytesRead = 0;
        while ((bytesRead = reader.read(buff)) != -1)
            output.write(buff,0,bytesRead);
        byte[] template = output.toByteArray();
        int size = template.length;

        String user = FingerHelper.getInstance().identifyUser(template);

        OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream());
        writer.write(user);
        writer.flush();
        writer.close();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
// Set the response message's MIME type
        response.setContentType("text/html;charset=UTF-8");
        // Allocate a output writer to write the response message into the network socket
        PrintWriter out = response.getWriter();

        // Write the response message, in an HTML page
        try {
            out.println("<!DOCTYPE html>");
            out.println("<html><head>");
            out.println("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>");
            out.println("<title>Identify</title></head>");
            out.println("<body>");
            out.println("<h1>Identify</h1>");  // says Hello
            // Echo client's request information

            out.println("<applet code=IdentifyApplet.class archive='neurotec-biometrics.jar, neurotec-biometrics-client.jar, neurotec-core.jar,neurotec-devices.jar,neurotec-media.jar, neurotec-licensing.jar, neurotec-media-processing.jar, jna.jar, NCore.dll' codebase='.' width='700' height='500'></applet>");

            out.println("</body>");
            out.println("</html>");

        } finally {
            out.close();  // Always close the output writer
        }
    }
}
