package com.musicind.dukamoja.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.android.material.textfield.TextInputEditText;
import com.musicind.dukamoja.R;
import com.musicind.dukamoja.errors.ApiError;
import com.musicind.dukamoja.inter.RequestInterface;
import com.musicind.dukamoja.inter.RetrofitBuilder;
import com.musicind.dukamoja.models.AccessToken;
import com.musicind.dukamoja.ui.HomeActivity;
import com.musicind.dukamoja.util.FacebookManager;
import com.musicind.dukamoja.util.TokenManager;
import com.musicind.dukamoja.util.Utils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;

public class LoginTabFragment extends Fragment {

    EditText email;
    TextInputEditText password;
    TextView or_tv;
    CircularProgressButton login;
    float v=0;


    RequestInterface requestInterface;
    TokenManager tokenManager;
    AwesomeValidation validator;
    Call<AccessToken> call;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.login_tab_fragment, container, false);

        email = root.findViewById(R.id.email);
        password = root.findViewById(R.id.password);
        login = root.findViewById(R.id.login);
        or_tv = root.findViewById(R.id.or_tv);


        email.setTranslationX(800);
        password.setTranslationX(800);
        login.setTranslationX(800);
        or_tv.setTranslationX(800);

        email.setAlpha(v);
        password.setAlpha(v);
        login.setAlpha(v);
        or_tv.setAlpha(v);

        email.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(300).start();
        password.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
        login.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
        or_tv.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();

        login.setOnClickListener(view -> {
            login();
        });

        or_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), SignupTabFragment.class);
                startActivity(i);
            }
        });
       
        requestInterface = RetrofitBuilder.createService(RequestInterface.class);
        tokenManager = TokenManager.getInstance(requireActivity().getApplicationContext().getSharedPreferences("prefs", MODE_PRIVATE));
        validator = new AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT);
        setupRules();

        if(tokenManager.getToken().getAccessToken() != null){
            startActivity(new Intent(getActivity(), HomeActivity.class));
        }
        return root;
    }

    void login() {

        String mail = email.getText().toString();
        String pwd = password.getText().toString();

        email.setError(null);
        password.setError(null);
        validator.clear();
        if (validator.validate()) {

            login.startAnimation();
            call = requestInterface.login(mail, pwd);
            call.enqueue(new Callback<AccessToken>() {
                @Override
                public void onResponse(@NonNull Call<AccessToken> call, @NonNull Response<AccessToken> response) {

                    Log.w(getTag(), "onResponse: " + response);

                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        tokenManager.saveToken(response.body());
                        login.stopAnimation();
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
                      login.revertAnimation();
                    }

                }

                @Override
                public void onFailure(Call<AccessToken> call, Throwable t) {
                    Log.w(getTag(), "onFailure: " + t.getMessage());
                   login.revertAnimation();
                }
            });

        }

    }
    private void handleErrors(ResponseBody response) {

        ApiError apiError = Utils.converErrors(response);

        for (Map.Entry<String, List<String>> error : apiError.getErrors().entrySet()) {
            if (error.getKey().equals("username")) {
                email.setError(error.getValue().get(0));
            }
            if (error.getKey().equals("password")) {
                password.setError(error.getValue().get(0));
            }
        }

    }
    public void setupRules() {

        validator.addValidation(getActivity(), R.id.email, Patterns.EMAIL_ADDRESS, R.string.err_email);
        validator.addValidation(getActivity(), R.id.password, RegexTemplate.NOT_EMPTY, R.string.err_password);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (call != null) {
            call.cancel();
            call = null;
        }
        login.dispose();
    }
}
