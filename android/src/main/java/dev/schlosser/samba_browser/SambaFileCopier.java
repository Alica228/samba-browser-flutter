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
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbAuthException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;

public class SambaFileCopier {

    @RequiresApi(api = Build.VERSION_CODES.N)
    static void copyFile(MethodCall call, MethodChannel.Result result)  {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        String sourceFilePath = call.argument("sourceFilePath");
        String destinationFile = call.argument("destinationFile");
        if (sourceFilePath.endsWith("/") && !destinationFile.endsWith("/")) {
            result.error("Can not copy directory to file.", null, null);
            return;
        }

        if (!sourceFilePath.endsWith("/") && destinationFile.endsWith("/")) {
            result.error("Can not copy file to directory.", null, null);
            return;
        }
        
        String destinationPath = call.argument("destinationPath");

        executor.execute(() -> {
            try {
                SmbFile directory = new SmbFile(destinationPath, new NtlmPasswordAuthentication(call.argument("domain"), call.argument("username"), call.argument("password")));
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                SmbFile sFile = new SmbFile(sourceFilePath, new NtlmPasswordAuthentication(call.argument("domain"), call.argument("username"), call.argument("password")));
                SmbFile dFile = new SmbFile(destinationPath + destinationFile, new NtlmPasswordAuthentication(call.argument("domain"), call.argument("username"), call.argument("password")));
                sFile.copyTo(dFile);
                result.success("");

            } catch(SmbAuthException e) {
                result.error("The given user could not be authenticated.", e.getMessage(), null);
            } catch (IOException e) {
                result.error("An iO-error occurred.", e.getMessage(), null);
            }
        });
    }

}
