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

public class SambaFileDeleter {

    @RequiresApi(api = Build.VERSION_CODES.N)
    static void deleteFile(MethodCall call, MethodChannel.Result result)  {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        String url = call.argument("url");
        if (url.endsWith("/")) {
            result.error("Can not delete directory.", null, null);
            return;
        }

        executor.execute(() -> {
            try {
                SingletonContext baseContext = SingletonContext.getInstance();
                Credentials credentials = new NtlmPasswordAuthenticator(call.argument("domain"), call.argument("username"), call.argument("password"));
                CIFSContext ts = baseContext.withCredentials(credentials);
                SmbFile file = new SmbFile(url, ts);
                file.delete();
                result.success("");

            } catch(SmbAuthException e) {
                result.error("The given user could not be authenticated.", e.getMessage(), null);
            } catch (IOException e) {
                result.error("An iO-error occurred.", e.getMessage(), null);
            }
        });
    }

}
