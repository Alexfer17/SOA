package alex.myapplication;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class Connection {

    private String datos = null;

    HttpURLConnection httpURLConnection = null;

    public String getArduino(String urlString) {
        try {
            URL url = new URL(urlString);
            httpURLConnection = (HttpURLConnection) url.openConnection();

            if(httpURLConnection.getResponseCode() == 200){
                InputStream inputStream = new BufferedInputStream(httpURLConnection.getInputStream());

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

                StringBuilder stringBuilder = new StringBuilder();

                String linea="";

                while( (linea = bufferedReader.readLine() ) != null){
                    stringBuilder.append(linea);
                }

                datos = stringBuilder.toString();

                httpURLConnection.disconnect();
            }

        }catch(MalformedURLException e){
            e.printStackTrace();
        }
        catch (IOException error) {
            error.printStackTrace();
            return null;
        }
        return datos;
    }
}
