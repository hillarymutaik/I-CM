package com.musicind.dukamoja.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.musicind.dukamoja.R;
import com.musicind.dukamoja.errors.ApiError;
import com.musicind.dukamoja.inter.RequestInterface;
import com.musicind.dukamoja.inter.RetrofitBuilder;
import com.musicind.dukamoja.models.AccessToken;
import com.musicind.dukamoja.ui.HomeActivity;
import com.musicind.dukamoja.util.TokenManager;
import com.musicind.dukamoja.util.Utils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;
import br.com.simplepass.loadingbutton.customViews.OnAnimationEndListener;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class SignupTabFragment extends Fragment {
    EditText email, username, phone, register_password, register_confirm_password;
    CircularProgressButton signup;
    float v;

    AwesomeValidation validator;
    RequestInterface requestInterface;
    TokenManager tokenManager;
    Call<AccessToken> call;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.signup_tab_fragment, container, false);

        email = root.findViewById(R.id.email);
        username = root.findViewById(R.id.username);
        phone = root.findViewById(R.id.phone);
        register_password = root.findViewById(R.id.register_password);
        register_confirm_password = root.findViewById(R.id.register_confirm_password);
        signup = root.findViewById(R.id.signup);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

        /*<<<<<<<<<<< start token manager >>>>>>>>>>>>>>>>*/
        requestInterface = RetrofitBuilder.createService(RequestInterface.class);
        tokenManager = TokenManager.getInstance(requireActivity().getApplicationContext().getSharedPreferences("prefs", MODE_PRIVATE));
        validator = new AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT);
        setupRules();
        if(tokenManager.getToken().getAccessToken() != null){
            startActivity(new Intent(getActivity(), HomeActivity.class));
        }
        /*<<<<<<<<<<< end token manager >>>>>>>>>>>>>>>>*/


        return root;
    }

    void register() {

        String mail = email.getText().toString();
        String user = username.getText().toString();
        String phone_number = phone.getText().toString();
        String register = register_password.getText().toString();
        String register_confirm = register_confirm_password.getText().toString();

        email.setError(null);
        username.setError(null);
        phone.setError(null);
        register_password.setError(null);
        register_confirm_password.setError(null);

        if(!register.equals(register_confirm)){
            Toast.makeText(getActivity(),"PASSWORDS DO NOT MATCH",Toast.LENGTH_SHORT).show();
        } else {
            signup.startAnimation();
            call = requestInterface.register(user, mail, phone_number, register);
            call.enqueue(new Callback<AccessToken>() {
                @Override
                public void onResponse(@NonNull Call<AccessToken> call, @NonNull Response<AccessToken> response) {

                    Log.w(getTag(), "onResponse: " + response);

                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        tokenManager.saveToken(response.body());
                        signup.stopAnimation();
                        startActivity(new Intent(getActivity(), HomeActivity.class));
                        requireActivity().finish();
                    } else {
                        if (response.code() == 422) {
                            handleErrors(response.errorBody());
                        }
                        if (response.code() == 401) {
                            ApiError apiError = Utils.converErrors(response.errorBody());
                            Toast.makeText(getActivity(), apiError.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        signup.revertAnimation();
                        //  showForm();
                    }

                }

                @Override
                public void onFailure(Call<AccessToken> call, Throwable t) {
                    Log.w(getTag(), "onFailure: " + t.getMessage());
                    // showForm();
                    signup.revertAnimation();
                }
            });

        }

    }
    private void handleErrors(ResponseBody response) {

        ApiError apiError = Utils.converErrors(response);

        for (Map.Entry<String, List<String>> error : apiError.getErrors().entrySet()) {
            if (error.getKey().equals("email")) {
                email.setError(error.getValue().get(0));
            }
            if (error.getKey().equals("password")) {
                register_password.setError(error.getValue().get(0));
            }
        }

    }
    public void setupRules() {

        validator.addValidation(getActivity(), R.id.email, Patterns.EMAIL_ADDRESS, R.string.err_email);
        validator.addValidation(getActivity(), R.id.username, RegexTemplate.NOT_EMPTY, R.string.err_username);
        validator.addValidation(getActivity(), R.id.phone, RegexTemplate.NOT_EMPTY, R.string.err_phone);
        validator.addValidation(getActivity(), R.id.register_password, RegexTemplate.NOT_EMPTY, R.string.err_password);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (call != null) {
            call.cancel();
            call = null;
        }
        signup.dispose();
    }

}
