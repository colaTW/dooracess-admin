package org.linphone.assistant;

import static org.linphone.assistant.upload.bitmapToBase64;
import static org.linphone.mediastream.MediastreamerAndroidContext.getContext;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.Settings;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.viewpager.widget.ViewPager;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.linphone.R;

public class messagepage1 extends Activity {
    ViewPager pager;
    ArrayList<View> pagerList;
    String pass = "";
    String token = "";
    String house_id = "";
    private final int IMAGE_RESULT_CODE = 2;
    private final int PICK = 1;
    private String url = ""; // 此处为上传图片地址，我就不写了
    private String imgString = "";
    private Intent intent;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    JSONObject jo2 = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_page);
        ImageButton home = findViewById(R.id.B_home);
        Button M_submin = findViewById(R.id.R_submit);
        final EditText event = findViewById(R.id.detail);
        EditText detail = findViewById(R.id.detail);
        final TextView upimg = findViewById(R.id.upimg);
        Button R_submit = findViewById(R.id.R_submit);
        ImageView upload = findViewById(R.id.upload);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        final Spinner spinner = findViewById(R.id.R_type);
        final String[] lunch = {"維修", "更換", "補強", "其他"};
        ArrayAdapter<String> lunchList =
                new ArrayAdapter<>(
                        messagepage1.this, android.R.layout.simple_spinner_dropdown_item, lunch);
        spinner.setAdapter(lunchList);

        home.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setClass(messagepage1.this, home.class);
                        startActivity(intent);
                    }
                });
        try {
            FileInputStream fin = openFileInput("info.txt");
            byte[] buffer = new byte[100];
            int byteCount = fin.read(buffer);
            String outinfo = "";
            outinfo = new String(buffer, 0, byteCount, "utf-8");
            String out[] = outinfo.split("\\,");
            pass = out[1];
            fin.close();
        } catch (Exception e) {
        }
        try {

            URL url = new URL("http://49.159.128.172:8888/api/v1/household/device/login");
            JSONObject jo = new JSONObject();
            jo.put(
                    "device_uuid",
                    Settings.Secure.getString(
                            getContext().getContentResolver(), Settings.Secure.ANDROID_ID));
            jo.put("password", pass);
            // jo.put("password", "09426153");
            HttpClient httpClient = new DefaultHttpClient();
            AbstractHttpEntity entity = new ByteArrayEntity(jo.toString().getBytes("UTF8"));
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            // try {
            HttpPost httpPost = new HttpPost(url.toURI());
            httpPost.setEntity(entity);
            // Prepare JSON to send by setting the entity
            HttpResponse response = httpClient.execute(httpPost);
            String json_string = EntityUtils.toString(response.getEntity());
            JSONObject temp1 = new JSONObject(json_string);
            JSONObject data = temp1.getJSONObject("data");
            token = data.getString("access_token");
            house_id = data.getString("household_id");
        } catch (Exception e) {
            e.printStackTrace();
        }
        R_submit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            int type = 0;
                            if (spinner.getSelectedItem().toString().equals("維修")) {
                                type = 1;
                            } else if (spinner.getSelectedItem().toString().equals("更換")) {
                                type = 2;
                            } else if (spinner.getSelectedItem().toString().equals("維修")) {
                                type = 3;
                            } else if (spinner.getSelectedItem().toString().equals("其他")) {
                                type = 4;
                            }
                            URL url2 = new URL("http://49.159.128.172:8888/api/v1/repair");
                            jo2.put("household_id", house_id);
                            jo2.put(
                                    "household_device_uuid",
                                    Settings.Secure.getString(
                                            getContext().getContentResolver(),
                                            Settings.Secure.ANDROID_ID));
                            jo2.put("repair_content", event.getText().toString());
                            jo2.put("repair_type", type);

                            System.out.println(jo2);

                            HttpClient httpClient2 = new DefaultHttpClient();
                            AbstractHttpEntity entity2 =
                                    new ByteArrayEntity(jo2.toString().getBytes("UTF8"));
                            entity2.setContentType(
                                    new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                            HttpPost httpPost2 = new HttpPost(url2.toURI());
                            httpPost2.setHeader("Authorization", "Bearer " + token);
                            httpPost2.setEntity(entity2);
                            // Prepare JSON to send by setting the entity
                            HttpResponse response2 = httpClient2.execute(httpPost2);
                            String json_string2 = EntityUtils.toString(response2.getEntity());
                            JSONObject temp2 = new JSONObject(json_string2);
                            String data2 = temp2.getString("errors");

                            if (data2.toString().equals("")) {
                                Toast.makeText(messagepage1.this, "成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(
                                                messagepage1.this,
                                                data2.toString(),
                                                Toast.LENGTH_SHORT)
                                        .show();
                            }

                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        upload.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        intent =
                                new Intent(
                                        Intent.ACTION_PICK,
                                        android.provider.MediaStore.Images.Media
                                                .EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, IMAGE_RESULT_CODE);
                    }
                });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
                // 表示 调用照相机拍照
            case PICK:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    Bitmap bitmap = (Bitmap) bundle.get("data");
                    imgString = bitmapToBase64(bitmap);
                }
                break;
                // 选择图片库的图片
            case IMAGE_RESULT_CODE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    Bitmap bitmap2 = PhotoUtils.getBitmapFromUri(uri, this);
                    imgString = bitmapToBase64(bitmap2);
                    try {
                        jo2.put("main_image_file", "data:image/jpeg;base64," + imgString);
                    } catch (Exception e) {
                    }
                }
                break;
        }
    }
}
