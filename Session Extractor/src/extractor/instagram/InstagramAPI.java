package extractor.instagram;
import com.google.gson.Gson;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.CookieManager;
import java.net.CookieHandler;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.sql.Timestamp;
import java.util.Map;
import java.util.UUID;
import javax.net.ssl.HttpsURLConnection;
import javax.swing.JOptionPane;
import javax.xml.bind.DatatypeConverter;

public final class InstagramAPI
{

    // Note: Instagram requires SSL connection in order to authenticate to Instagram Servers.
    private static final String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36";
    private static final UUID guid1 = UUID.randomUUID(), guid2 = UUID.randomUUID(), guid3 = UUID.randomUUID();

    private static HttpsURLConnection con;
    private static String androidID;
    private static String lastError;


    // Login using the web API
    public synchronized static WebSession LoginWeb(String username, String password) throws  InvalidLoginException, SecuredException, CheckpointException  {

        if (username == null || password == null)
            throw new NullPointerException("parameters cannot be null");

        try {

            CookieManager manager = new CookieManager();
            CookieHandler.setDefault(manager);


            byte[] formData = String.format("enc_password=#PWD_INSTAGRAM_BROWSER:0:%s:%s&optIntoOneTap=false&queryParams={}&trustedDeviceRecords={}&username=%s"
                    ,getTimestamp(),password,username).getBytes(StandardCharsets.UTF_8);

            URL endpoint = new URL("https://www.instagram.com/api/v1/web/accounts/login/ajax/");
            con = (HttpsURLConnection) endpoint.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.addRequestProperty("User-Agent", userAgent);
            con.addRequestProperty("Accept", "*/*");
            con.addRequestProperty("Content-Length", Integer.toString(formData.length));
            con.addRequestProperty("X-Instagram-Ajax", "1");
            con.addRequestProperty("X-IG-App-ID", "936619743392459");
            con.addRequestProperty("Cookie", "missing");
            con.addRequestProperty("X-Csrftoken", "missing");
            con.addRequestProperty("X-Ig-Www-Claim", "0");
            con.addRequestProperty("X-Requested-With", "XMLHttpRequest");
            try (OutputStream stream = con.getOutputStream()) {
                stream.write(formData);
                stream.flush();
            }

            String response;
            if (con.getResponseCode() != HttpURLConnection.HTTP_OK)
            {
                response = getErrorMessage();
                if (response.contains("two_factor_required"))
                    throw new SecuredException("2FA Authentication Required");
                else if (response.contains("checkpoint"))
                    throw new CheckpointException("Checkpoint Required!");
                else if (response.contains("your password was incorrect"))
                    throw new InvalidLoginException("Invalid username or password");

            }else {
                response = getResponse();
                if (response.contains("\"authenticated\":false"))
                    throw new InvalidLoginException("Invalid username or password");
                else if (response.contains("\"authenticated\":true"))
                {
                    WebSession session = new WebSession();

                    manager.getCookieStore().getCookies().forEach( (cookie) -> {
                        if (cookie.getName().compareTo("sessionid") == 0) {
                            session.setSession(cookie.getValue());
                            return;
                        }

                    });

                    return session;
                }
            }

        }catch (IOException ex)
        {
            JOptionPane.showMessageDialog(null,ex.getMessage(),"Error", JOptionPane.ERROR_MESSAGE);
        }
        return  null;
    }

    // Login using the Application API
    public synchronized static APISession LoginAPI(String username, String password) throws CheckpointException, InvalidLoginException, BannedAccountException
    {

        if (username == null || password == null)
            throw new NullPointerException("parameters cannot be null");

        if (androidID == null)
            androidID = generateAndroidID(username.concat(password));

        CookieManager manager = new CookieManager();
        CookieHandler.setDefault(manager);


        byte[] formData = String.format(generateBody(username,password)).getBytes(StandardCharsets.UTF_8);


        try {
            URL endpoint = new URL("https://i.instagram.com/api/v1/accounts/login/");
            con = (HttpsURLConnection)endpoint.openConnection();
            fetchHeaders();

            try (OutputStream stream = con.getOutputStream()) {
                stream.write(formData);
                stream.flush();
            }

            String response;
            if (con.getResponseCode() != HttpURLConnection.HTTP_OK)
            {
                response = getErrorMessage();
                if (response.contains("challenge_required"))
                    throw new CheckpointException("Challenge is required.");
                else if (response.contains("Incorrect username") || response.contains("Incorrect password") || response.contains("bad_password"))
                    throw new InvalidLoginException("Invalid username or password");
                else if (response.contains("inactive user"))
                    throw new BannedAccountException("Account is disabled");
            }else {
                response = getResponse();
                if (response.contains("logged_in_user"))
                {
                    APISession session = new APISession();
                    manager.getCookieStore().getCookies().forEach( (cookie) -> {
                        if (cookie.getName().compareTo("sessionid") == 0) {
                            session.setSession(cookie.getValue());
                            return;
                        }

                    });

                    return session;
                }
            }
        }catch (IOException ex)
        {
            JOptionPane.showMessageDialog(null,ex.getMessage(),"Error", JOptionPane.ERROR_MESSAGE);
        }



        return  null;
    }


    // reading error response if authentication failed
    private static String getErrorMessage() throws IOException {

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getErrorStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        lastError = response.toString();
        return response.toString();
    }

    // Reading success response
    private static String getResponse() throws IOException
    {

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

    public static String getLastError() throws NullPointerException
    {
        if (lastError == null)
            throw new NullPointerException("lastError is null");

        return lastError;
    }


    private static void fetchHeaders() throws ProtocolException {
        if (con == null)
            throw new NullPointerException("HttpsURLConnection is null");

        con.setRequestMethod("POST");
        con.addRequestProperty("User-Agent","Instagram 155.0.0.37.107 Android");
        con.addRequestProperty("Host","i.instagram.com");
        con.addRequestProperty("x-ig-app-locale","en_SA");
        con.addRequestProperty("x-ig-device-locale","en_SA");
        con.addRequestProperty("x-ig-mapped-locale","en_US");
        con.addRequestProperty("x-pigeon-session-id","1a4665b7-5de6-4962-9072-b73873af5dce");
        con.addRequestProperty("x-pigeon-rawclienttime","1694086577");
        con.addRequestProperty("x-ig-connection-speed","643kbps");
        con.addRequestProperty("x-ig-bandwidth-speed-kbps","1236.889");
        con.addRequestProperty("x-ig-bandwidth-totalbytes-b","6672937");
        con.addRequestProperty("x-ig-bandwidth-totaltime-ms","7015");
        con.addRequestProperty("x-ig-app-startup-country","SA");
        con.addRequestProperty("x-bloks-version-id","85e371bf185c688d008ad58d18c84943f3e6d568c4eecd561eb4b0677b1e4c55");
        con.addRequestProperty("x-ig-www-claim","0");
        con.addRequestProperty("x-bloks-is-layout-rtl","false");
        con.addRequestProperty("x-ig-device-id",UUID.randomUUID().toString());
        con.addRequestProperty("x-ig-android-id",androidID);
        con.addRequestProperty("x-ig-connection-type","WIFI");
        con.addRequestProperty("x-ig-capabilities","3brTvw8=");
        con.addRequestProperty("x-ig-app-id","567067343352427");
        con.addRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        con.addRequestProperty("accept-language","en-SA, en-US");
        con.addRequestProperty("x-mid","");
        con.addRequestProperty("content-type","application/x-www-form-urlencoded; charset=UTF-8");
        con.addRequestProperty("x-fb-http-engine","Liger");
        con.addRequestProperty("Connection","close");

        con.setDoOutput(true);
        con.setHostnameVerifier(HttpsURLConnection.getDefaultHostnameVerifier());

    }


    private static String getTimestamp()
    {
        return Long.toString(new Timestamp(System.currentTimeMillis()).getTime());
    }

    private static String generateAndroidID(String seed)
    {
        String hash = generateMD516(seed);
        if (hash == null)
            throw new NullPointerException("MD5 hash generation error");

        return "android-".concat(hash);
    }


    // Fake Android Device ID
    private static String generateMD516(String input)
    {
        String hash = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(input.getBytes(StandardCharsets.UTF_8));
            hash = DatatypeConverter.printHexBinary(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            JOptionPane.showMessageDialog(null,e.getMessage(),"Error", JOptionPane.ERROR_MESSAGE);
        }

        return (hash == null) ? null : hash.substring(0,17).toLowerCase();
    }

    // in order to achieve a secure connection, a certain body is required.
    private static String generateBody(String username, String password)
    {
        if (username == null || password == null)
            throw new NullPointerException("parameters cannot be null");

        Gson jsonFormatter = new Gson();
        Map<String,String> bodyPayload = new HashMap<>();
        bodyPayload.put("jazoest","22713");
        bodyPayload.put("phone_id",guid1.toString());
        bodyPayload.put("enc_password",String.format("#PWD_INSTAGRAM_BROWSER:0:%s:%s",getTimestamp(),password));
        bodyPayload.put("_csrftoken","");
        bodyPayload.put("username",username);
        bodyPayload.put("adid",guid2.toString());
        bodyPayload.put("guid",guid3.toString());
        bodyPayload.put("device_id",androidID);
        bodyPayload.put("google_tokens","[]");
        bodyPayload.put("login_attempt_count","0");

        return String.format("signed_body=SIGNATURE.%s",jsonFormatter.toJson(bodyPayload).replace("\\",""));

    }

}
