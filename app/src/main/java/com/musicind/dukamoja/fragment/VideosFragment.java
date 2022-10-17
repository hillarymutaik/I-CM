package com.musicind.dukamoja.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.musicind.dukamoja.R;
import com.musicind.dukamoja.adapter.VideosAdapter;
import com.musicind.dukamoja.models.VideoDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class VideosFragment extends Fragment {

    ListView listView;
    //String API_KEY_KEMOI="AIzaSyBo539qSW-OD-LPasun0g0CkXEtV-0nqdI";
    String API_KEY_RUTO = "AIzaSyCdgIA462rQC7P_16PGfOhzGz2N40uvzLc";
    String CHANNEL_ID_KEMOI = "UC2rBVgrFmVC6qO7D-KQptjQ";
    String CHANNEL_ID_RUTO = "UC_xJrVzwmyBYaPEylBeazYw";
    ArrayList<VideoDetails> videoDetailsArrayList;
    VideosAdapter myCustomAdapter;
    String url="https://www.googleapis.com/youtube/v3/search?part=snippet&channelId=UC_xJrVzwmyBYaPEylBeazYw&key=AIzaSyCdgIA462rQC7P_16PGfOhzGz2N40uvzLc&maxResults=50";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.videos_fragment, container, false);
        listView= root.findViewById(R.id.listView);
        videoDetailsArrayList= new ArrayList<>();

        myCustomAdapter= new VideosAdapter(getActivity(), videoDetailsArrayList);

        displayVideos();
        return root;
    }

    private void displayVideos() {

        RequestQueue requestQueue= Volley.newRequestQueue(requireContext());

        StringRequest stringRequest= new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("items");


                    for(int i=0;i<jsonArray.length(); i++)
                    {

                        JSONObject jsonObject1=jsonArray.getJSONObject(i);

                        JSONObject jsonVideoId= jsonObject1.getJSONObject("id");
                        JSONObject jsonObjectSnippet= jsonObject1.getJSONObject("snippet");

                        JSONObject jsonObjectDefault=  jsonObjectSnippet.getJSONObject("thumbnails").getJSONObject("medium");
                        if (jsonVideoId.has("videoId")){

                            String video_id = jsonVideoId.getString("videoId");
                            Log.e(String.valueOf(this), "onResponse: " + video_id);
                            VideoDetails vd= new VideoDetails();

                            vd.setVideoId(video_id);
                            vd.setTitle(jsonObjectSnippet.getString("title"));
                            vd.setDescription(jsonObjectSnippet.getString("description"));
                            vd.setUrl(jsonObjectDefault.getString("url"));

                            videoDetailsArrayList.add(vd);} else {

                        }
                    }

                    listView.setAdapter(myCustomAdapter);
                    myCustomAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }

        );

        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);

    }
}
