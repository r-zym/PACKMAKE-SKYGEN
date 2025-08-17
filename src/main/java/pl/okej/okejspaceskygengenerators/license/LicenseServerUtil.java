package pl.okej.okejspaceskygengenerators.license;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LicenseServerUtil {
    
    private static final String LICENSE_SERVER_URL = "https://api.packmake.pl/license/verify";
    
    public static boolean verifyLicense(String licenseKey, String customerId) {
        try {
            String urlString = LICENSE_SERVER_URL + "?key=" + licenseKey + "&customer_id=" + customerId;
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String response = reader.readLine();
                reader.close();
                
                return "valid".equals(response);
            }
            
            return false;
        } catch (Exception e) {
            // If we can't connect to license server, allow plugin to run
            // This prevents the plugin from breaking due to network issues
            return true;
        }
    }
    
    public static String getServerResponse(String licenseKey, String customerId) {
        try {
            String urlString = LICENSE_SERVER_URL + "?key=" + licenseKey + "&customer_id=" + customerId;
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String response = reader.readLine();
                reader.close();
                return response;
            }
            
            return "error";
        } catch (Exception e) {
            return "network_error";
        }
    }
}