package hu.krisztofertarr.forum.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PermissionUtil {

    public final int REQUEST_CODE_PERMISSIONS = 100;


    public void checkAndRequestPermissions(Activity activity, String... permissions) {
        List<String> permissionsList = new ArrayList<>();
        for (String permission : permissions) {
            int permissionState = activity.checkSelfPermission(permission);
            if (permissionState == PackageManager.PERMISSION_DENIED) {
                permissionsList.add(permission);
            }
        }
        if (!permissionsList.isEmpty()) {
            ActivityCompat.requestPermissions(
                    activity,
                    permissionsList.toArray(new String[0]),
                    REQUEST_CODE_PERMISSIONS
            );
        }
    }

    public void onRequestPermissionsResult(final Activity activity, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != PermissionUtil.REQUEST_CODE_PERMISSIONS || grantResults.length == 0) {
            return;
        }

        final List<String> permissionsList = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                permissionsList.add(permissions[i]);
            }
        }

        if (!permissionsList.isEmpty()) {
            boolean showRationale = false;
            for (String permission : permissionsList) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                    showRationale = true;
                    break;
                }
            }

            if (showRationale) {
                showAlertDialog(activity, (dialogInterface, i) -> checkAndRequestPermissions(activity, permissionsList.toArray(new String[0])));
            }
        }
    }

    private void showAlertDialog(Context context, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(context)
                .setMessage("Egyes engedélyek nincsenek megadva. Előfordulhat, hogy az alkalmazás nem a várt módon fog működni. Szeretné megadni őket?")
                .setPositiveButton("Igen", okListener)
                .setNegativeButton("Mégse", (dialogInterface, i) -> {
                })
                .create()
                .show();
    }
}