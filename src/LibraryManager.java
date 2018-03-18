import java.lang.reflect.Field;

public final class LibraryManager {

    // ===========================================================
    // Private static fields
    // ===========================================================

    private static final String WIN32_X86 = "Win32_x86";
    private static final String WIN64_X64 = "Win64_x64";
    private static final String LINUX_X86 = "Linux_x86";
    private static final String LINUX_X86_64 = "Linux_x86_64";
    private static final String MAC_OS = "/Library/Frameworks/";

    // ===========================================================
    // Public static methods
    // ===========================================================

    public static void initLibraryPath() {
        /*String libraryPath = "D:\\git_repo\\VeriFingerSDK - Copy\\lib";//getLibraryPath();
        String jnaLibraryPath = System.getProperty("jna.library.path");
        if (Utils.isNullOrEmpty(jnaLibraryPath)) {
            System.setProperty("jna.library.path", libraryPath.toString());
        } else {
            System.setProperty("jna.library.path", String.format("%s%s%s", jnaLibraryPath, Utils.PATH_SEPARATOR, libraryPath.toString()));
        }
        System.setProperty("java.library.path",String.format("%s%s%s", System.getProperty("java.library.path"), Utils.PATH_SEPARATOR, libraryPath.toString()));
        System.out.println(System.getProperty("java.library.path"));*/

        try {
            Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
            fieldSysPath.setAccessible(true);
            fieldSysPath.set(null, null);
        } catch (SecurityException e) {
            System.out.println(e.getMessage());
        } catch (NoSuchFieldException e) {
            System.out.println(e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (IllegalAccessException e) {
            System.out.println(e.getMessage());
        }
    }

    public static String getLibraryPath() {
        StringBuilder path = new StringBuilder();
        int index = Utils.getWorkingDirectory().lastIndexOf(Utils.FILE_SEPARATOR);
        if (index == -1) {
            return null;
        }
        String part = Utils.getWorkingDirectory().substring(0, index);

        if (part.endsWith("Bin")) {
            path.append(part);
            path.append(Utils.FILE_SEPARATOR);
            path.append(WIN64_X64);
        }

        return path.toString();
    }
}
