package org.lspatch.manager.unofficial;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;

public class CommandUtil {
    public static List<String> playRunTime(String command) throws Exception {
        List<String> results = new ArrayList<String>();
        Process p = Runtime.getRuntime().exec(command);
        InputStream is = p.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = reader.readLine()) != null) {
            results.add(line);
        }
        p.waitFor();
        is.close();
        reader.close();
        p.destroy();
        return results;
    }
}