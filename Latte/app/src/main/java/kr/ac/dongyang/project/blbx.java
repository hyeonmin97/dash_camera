package kr.ac.dongyang.project;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.VideoView;


import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class blbx extends AppCompatActivity {
    Button btnmain;
    private VideoView videoView; // 비디오를 실행할 수 있게 도와주는 뷰
    private MediaController mediaController; // 재생이나 정지와 같은 미디어 제어 버튼부를 담당

    LinearLayout dynamic;

    static RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blbx);

        videoView = findViewById(R.id.blackbox); // 비디오 뷰 아이디 연결
        mediaController = new MediaController(this);
        videoView.setMediaController(mediaController); // 미디어 제어 버튼부 세팅

        //mainactivity로 이동
        btnmain = findViewById(R.id.btnmain);
        btnmain.setOnClickListener((v) -> {
            //인텐트 선언 -> 현재 액티비티, 넘어갈 액티비티
            Intent intent6 = new Intent(this, MainActivity2.class);
            startActivity(intent6);
            finish();
        });
        dynamic = (LinearLayout)findViewById(R.id.dynamic);

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        sendRequest("http://192.168.0.9:8080/test2");


        //Error 메세지 표시
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                String message;

                switch (what) {
                    case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                        message = "MEDIA_ERROR_UNKNOWN";
                        break;
                    case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                        message = "MEDIA_ERROR_SERVER_DIED";
                        break;
                    default:
                        message = "No What";
                }
                switch (extra) {
                    case MediaPlayer.MEDIA_ERROR_IO:
                        message += ", MEDIA_ERROR_IO";
                        break;
                    case MediaPlayer.MEDIA_ERROR_MALFORMED:
                        message += ", MEDIA_ERROR_MALFORMED";
                        break;
                    case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                        message += ", MEDIA_ERROR_UNSUPPORTED";
                        break;
                    default:
                        message += ", No extra";
                }
                Log.e("error Tag : ", message);
                return true;
            }
        });


    }
    public void println (String data){
        //textView.setText(data + "\n");
    }
    public void sendRequest (String url){

        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        println(response);
                        String str = response.replace("[","");
                        str =str.replace("]","");
                        str = str.replace("\\\\","/");
                        str =str.replace("\"","");

                        String[] allPath = str.split(",");
                        for(String path: allPath){
                            Button button = new Button(getApplicationContext());
                            button.setText(path);

                            String[] splitPath = path.split("/");
                            String directory = splitPath[7];
                            String filename = splitPath[8];
                            filename = filename.substring(0,8);

                            System.out.println(filename);
                            String fileUrl = "http://192.168.0.9:8080/video/" + directory + "/" + filename;
                            System.out.println(fileUrl);

                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    videoView.setVideoURI(Uri.parse(fileUrl));
                                    videoView.requestFocus();
                                    videoView.start();
                                }
                            });
                            dynamic.addView(button);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        println(error.getMessage());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                //params.put("key", "value");
                return params;
            }
        };
        // 이전 결과가 있더라도 새로 요청
        request.setShouldCache(false);
        requestQueue.add(request);

    }
}