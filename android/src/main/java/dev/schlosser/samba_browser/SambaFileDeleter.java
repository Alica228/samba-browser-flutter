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
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

public class SambaFileUploader {

    @RequiresApi(api = Build.VERSION_CODES.N)
    static void deleteFile(MethodCall call, MethodChannel.Result result)  {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        String path = call.argument("fileName");
        if (path.endsWith("/")) {
            result.error("Can not delete directory.", null, null);
            return;
        }

        executor.execute(() -> {
            try {
                SmbFile file = new SmbFile(call.argument("fileName").toString(), new NtlmPasswordAuthentication(call.argument("domain"), call.argument("username"), call.argument("password")));
                file.delete();
                result.success(file.getPath());

            } catch (IOException e) {
                result.error("An iO-error occurred.", e.getMessage(), null);
            }
        });
    }

}