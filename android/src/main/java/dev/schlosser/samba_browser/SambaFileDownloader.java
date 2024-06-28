package dev.schlosser.samba_browser;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import jcifs.CIFSContext;
import jcifs.Credentials;
import jcifs.context.SingletonContext;
import jcifs.smb.NtlmPasswordAuthenticator;
import jcifs.smb.SmbAuthException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;

public class SambaFileDownloader {

    private static final int FILE_CACHE_SIZE = 1 * 1024 * 1024;

    @RequiresApi(api = Build.VERSION_CODES.N)
    static void saveFile(MethodCall call, MethodChannel.Result result)  {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        String url = call.argument("url");
        if (url.endsWith("/")) {
            result.error("Can not download directory.", null, null);
            return;
        }

        executor.execute(() -> {
            try {
                SingletonContext baseContext = SingletonContext.getInstance();
                Credentials credentials = new NtlmPasswordAuthenticator(call.argument("domain"), call.argument("username"), call.argument("password"));
                CIFSContext ts = baseContext.withCredentials(credentials);
                SmbFile file = new SmbFile(url, ts);
                SmbFileInputStream in = new SmbFileInputStream(file);

                File outFile = new File(call.argument("saveFolder").toString() + call.argument("fileName").toString());
                FileOutputStream outStream = new FileOutputStream(outFile);

                byte[] fileBytes = new byte[FILE_CACHE_SIZE];
                int n;
                while(( n = in.read(fileBytes)) != -1) {
                    outStream.write(fileBytes, 0, n);
                }

                in.close();
                outStream.close();
                result.success(outFile.getAbsolutePath());

            } catch(SmbAuthException e) {
                result.error("The given user could not be authenticated.", e.getMessage(), null);
            } catch (IOException e) {
                result.error("An iO-error occurred.", e.getMessage(), null);
            }
        });
    }

}
