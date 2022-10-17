package com.musicind.dukamoja.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.musicind.dukamoja.R;
import com.musicind.dukamoja.inter.RequestInterface;
import com.musicind.dukamoja.inter.RetrofitBuilder;
import com.musicind.dukamoja.response.ProfileResponse;
import com.musicind.dukamoja.ui.EditProfileActivity;
import com.musicind.dukamoja.ui.LoginActivity;
import com.musicind.dukamoja.util.TokenManager;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment {
    
    CircleImageView profileImage;
    TextView userName, userLoc, userDesc, userEmail, userPhone;
    FloatingActionButton profileFab;
    TokenManager tokenManager;
    RequestInterface request;
    Call<ProfileResponse> profile;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.profile_fragment, container, false);
        
        profileImage = root.findViewById(R.id.profileImage);
        userName = root.findViewById(R.id.userName);
        userLoc = root.findViewById(R.id.userLoc);
        userDesc = root.findViewById(R.id.userDesc);
        userEmail = root.findViewById(R.id.userEmail);
        userPhone = root.findViewById(R.id.userPhone);
        profileFab = root.findViewById(R.id.profileFab);

        tokenManager = TokenManager.getInstance(requireActivity().getSharedPreferences("prefs", MODE_PRIVATE));
        if(tokenManager.getToken() == null){
            startActivity(new Intent(getActivity(), LoginActivity.class));
            requireActivity().finish();
        }
        request = RetrofitBuilder.createServiceWithAuth(RequestInterface.class, tokenManager);
        showUserDetails();

        profileFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(intent);
            }
        });
        
        return root;
    }

    private void showUserDetails() {
        int name = tokenManager.getToken().getUserId();
        profile = request.getUserProfile(name);
        profile.enqueue(new Callback<ProfileResponse>() {

            @Override
            public void onResponse(@NonNull Call<ProfileResponse> call, @NonNull Response<ProfileResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    Log.e(getTag(), "onResponse: " + response.body());
                    userName.setText(response.body().getUserProfile().get(0).getUserName());
                    userEmail.setText(response.body().getUserProfile().get(0).getUserEmail());
                   // Picasso.get().load(videoDetails.getUrl()).into(imageView);
                    Picasso.get().load(response.body().getUserProfile().get(0).getProfile()).into(profileImage);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProfileResponse> call, @NonNull Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
