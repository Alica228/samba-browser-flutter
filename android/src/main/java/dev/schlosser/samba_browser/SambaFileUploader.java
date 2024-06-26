package dev.schlosser.samba_browser;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileInputStream;
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
import jcifs.smb.SmbFileOutputStream;

public class SambaFileUploader {

    private static final int FILE_CACHE_SIZE = 1 * 1024 * 1024;

    @RequiresApi(api = Build.VERSION_CODES.N)
    static void uploadFile(MethodCall call, MethodChannel.Result result)  {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        String filePath = call.argument("filePath");
        if (filePath.endsWith("/")) {
            result.error("Can not upload directory.", null, null);
            return;
        }

        executor.execute(() -> {
            try {
                File inFile = new File(filePath);
                FileInputStream inStream = new FileInputStream(inFile);

                SingletonContext baseContext = SingletonContext.getInstance();
                Credentials credentials = new NtlmPasswordAuthenticator(call.argument("domain"), call.argument("username"), call.argument("password"));
                CIFSContext ts = baseContext.withCredentials(credentials);
                SmbFile directory = new SmbFile(call.argument("uploadFolder").toString(), ts);
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                SmbFile file = new SmbFile(call.argument("uploadFolder").toString() + call.argument("uploadFileName").toString(), ts);
                SmbFileOutputStream out = new SmbFileOutputStream(file);
                
                byte[] fileBytes = new byte[FILE_CACHE_SIZE];
                int n;
                while(( n = inStream.read(fileBytes)) != -1) {
                    out.write(fileBytes, 0, n);
                }

                inStream.close();
                out.close();
                result.success(inFile.getAbsolutePath());

            } catch(SmbAuthException e) {
                result.error("The given user could not be authenticated.", e.getMessage(), null);
            } catch (IOException e) {
                result.error("An iO-error occurred.", e.getMessage(), null);
            }
        });
    }

}
