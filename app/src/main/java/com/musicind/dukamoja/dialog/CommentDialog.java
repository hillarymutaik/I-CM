package com.musicind.dukamoja.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.musicind.dukamoja.R;
import com.musicind.dukamoja.errors.ApiError;
import com.musicind.dukamoja.inter.RequestInterface;
import com.musicind.dukamoja.inter.RetrofitBuilder;
import com.musicind.dukamoja.models.AccessToken;
import com.musicind.dukamoja.ui.LoginActivity;
import com.musicind.dukamoja.util.TokenManager;
import com.musicind.dukamoja.util.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class CommentDialog {

    TokenManager tokenManager;
    RequestInterface request;
    Call<AccessToken> call;
    EditText commentEditText;

    public void showDialog(Activity activity) {

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.comment_dialog_layout);
        //Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

//        TextView text = (TextView) dialog.findViewById(R.id.txt_file_path);
//        text.setText(msg);
        commentEditText = dialog.findViewById(R.id.commentEditText);
        Button dialogBtn_cancel = dialog.findViewById(R.id.cancelBtn);
        dialogBtn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button dialogBtn_okay = (Button) dialog.findViewById(R.id.submitBtn);
        dialogBtn_okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(activity,"Okay" ,Toast.LENGTH_SHORT).show();
                postComment(activity);
                dialog.cancel();
            }
        });

        dialog.show();

    }
    int readLastButtonPressed(Activity activity) {
        SharedPreferences sharedPref = activity.getSharedPreferences("application", Context.MODE_PRIVATE);
        return sharedPref.getInt("SONG_ID", 0);
    }

    private void postComment(Activity activity) {
        tokenManager = TokenManager.getInstance(activity.getSharedPreferences("prefs", MODE_PRIVATE));
        if(tokenManager.getToken() == null){
            Toast.makeText(activity, "Please log in first", Toast.LENGTH_SHORT).show();
        }
        request = RetrofitBuilder.createServiceWithAuth(RequestInterface.class, tokenManager);

        String body = commentEditText.getText().toString();
        int id = tokenManager.getToken().getUserId();
        int songID = readLastButtonPressed(activity);

        call = request.postComment(body, songID, id);
        call.enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                Log.w(String.valueOf(activity), "onResponse: " + response);

                if (response.isSuccessful()) {
                    assert response.body() != null;
                    Toast.makeText(activity, "Comment Added", Toast.LENGTH_SHORT).show();
                } else {

                    if (response.code() == 401) {
                        ApiError apiError = Utils.converErrors(response.errorBody());
                        Toast.makeText(activity, apiError.getMessage(), Toast.LENGTH_LONG).show();
                        activity.startActivity(new Intent(activity, LoginActivity.class));
                        activity.finish();
                    }
                    //  showForm();
                }
            }

            @Override
            public void onFailure(Call<AccessToken> call, Throwable t) {
                Log.w(String.valueOf(activity), "onFailure: "+ t.getMessage());
                Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}
