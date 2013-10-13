package com.petroschurch.petros.lib;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class Download {
    public static boolean DownFile(String url, String dest) {
        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(url);
        HttpResponse response;
        boolean isOk = false;

        try {
            response = client.execute(get);
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();
            FileOutputStream fileOutputStream = null;
            if (is != null) {
                File file = new File(dest);
                fileOutputStream = new FileOutputStream(file);
                byte[] buf = new byte[1024];
                int ch = -1;
                //long count = 0;  
                //long lastCount = 0;
                while ((ch = is.read(buf)) != -1) {
                    fileOutputStream.write(buf, 0, ch);
                    //count += ch; 
                }
            }
            fileOutputStream.flush();
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
            isOk = true;
        } catch (Exception e) {

        }

        if (isOk) {
            return true;
        } else {
            File file = new File(dest);
            file.delete();
            return false;
        }
    }
}
