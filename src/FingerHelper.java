import com.neurotec.biometrics.NMatchingResult;
import com.neurotec.biometrics.NSubject;
import com.neurotec.biometrics.client.NBiometricClient;
import com.neurotec.io.NBuffer;
import com.neurotec.licensing.NLicense;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public class FingerHelper {

    private static FingerHelper instance;
    private ArrayList<NSubject> subjects = new ArrayList<NSubject>();
    final String matching = "Biometrics.FingerMatching";
    final String fingerExtraction = "Biometrics.FingerExtraction";
    NBiometricClient biometricClient = new NBiometricClient();

    private FingerHelper(){
        //load subjects from database
        try {
            Map<String, byte[]> users = DatabaseHelper.getInstance().getUsersAndTemplate();
            for(Map.Entry<String, byte[]> entry : users.entrySet()){
                addFingerToCache(entry.getKey(),entry.getValue());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        LibraryManager.initLibraryPath();
        try {
            if (!NLicense.obtainComponents("/local", 5000, matching) ||
                    !NLicense.obtainComponents("/local", 5000, fingerExtraction)) {
                System.err.println("Could not obtain licenses for components: " + matching);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static FingerHelper getInstance(){
        if(instance == null)
            instance = new FingerHelper();
        return instance;
    }

    public void addFingerToCache(String user, byte[] template){
        NBuffer buffer = new NBuffer(template);
        NSubject subject = NSubject.fromMemory(buffer);
        subject.setId(user);
        subjects.add(subject);
        //biometricClient.createTemplate(subject); ??
        biometricClient.enroll(subject);
    }

    public String identifyUser(byte[] template){
        NBuffer buffer = new NBuffer(template);
        NSubject subject = NSubject.fromMemory(buffer);
        biometricClient.identify(subject);

        String user = null;
        for (NSubject s : subjects) {
            for (NMatchingResult result : subject.getMatchingResults()) {
                if (s.getId().equals(result.getId())) {
                    user = s.getId();
                    break;
                }
            }

        }

        return user;
    }
}
