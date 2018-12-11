package com.leeyh.boostcampproject.helper;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.leeyh.boostcampproject.R;

public class ExceptionHandle {

    public static void handleError(Context context, Exception e) {
        Log.d("에러처리", "handleError: " + e);
        if (e instanceof ResponseException) {
            int responseCode = ((ResponseException) e).getRequestCode();
            switch (responseCode) {
                case 400:
                    Toast.makeText(context, R.string.wron_request_query, Toast.LENGTH_SHORT).show();
                    break;
                case 404:
                    Toast.makeText(context, R.string.typo_check, Toast.LENGTH_SHORT).show();
                    break;
                case 500:
                    Toast.makeText(context, R.string.server_error, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(context, R.string.request_failed, Toast.LENGTH_SHORT).show();
                    break;
            }
        } else {
            Toast.makeText(context, R.string.request_failed, Toast.LENGTH_SHORT).show();
        }
    }
}
