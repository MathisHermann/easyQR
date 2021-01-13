package rocks.process.easyqr.middleware;

public class PathCleaner {
    public static String cleanPathName(String path) {
        return path.replace(" ", "_").trim();
    }
}
