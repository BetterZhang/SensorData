package com.jsdttec.sensordata.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jsdttec.sensordata.R;
import com.jsdttec.sensordata.databinding.ActivityMainBinding;
import com.jsdttec.sensordata.domain.RequestParamsVo;
import com.jsdttec.sensordata.domain.ResponseBody;
import com.jsdttec.sensordata.service.SensorDataService;
import com.jsdttec.sensordata.util.DateUtil;
import com.jsdttec.sensordata.util.SharedPreUtils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import fr.quentinklein.slt.LocationTracker;
import fr.quentinklein.slt.TrackerSettings;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final static int MSG_START = 1;
    private final static long TIME_INTERVAL = 30 * 1000;

    private final static int REQUEST_CODE_ASK_LOCATION = 100;

    private static final int CONNECT_TIMEOUT = 30;
    private static final int WRITE_TIMEOUT = 20;
    private static final int READ_TIMEOUT = 20;

    private ActivityMainBinding mBinding;

    private LocationManager locationManager;
    private String locationProvider;            //位置提供器
    double latitude = 0;
    double longitude = 0;

    private long exitTime = 0;

    Retrofit retrofit = null;
    SensorDataService service = null;
    Call<ResponseBody> response = null;
    File file = null;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_START:
                    getSensorData();
                    mHandler.sendEmptyMessageDelayed(MSG_START, TIME_INTERVAL);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS}, REQUEST_CODE_ASK_LOCATION);
            return;
        }

        file = new File("/sdcard/sensordata.txt");

        mBinding.btnStart.setEnabled(true);
        mBinding.btnFinish.setEnabled(false);
        mBinding.btnClear.setEnabled(true);

        if (!TextUtils.isEmpty(SharedPreUtils.getString(this, SharedPreUtils.Key_DEVICEID)))
            mBinding.etId.setText(SharedPreUtils.getString(this, SharedPreUtils.Key_DEVICEID));

        if (!TextUtils.isEmpty(SharedPreUtils.getString(this, SharedPreUtils.Key_URL)))
            mBinding.etUrl.setText(SharedPreUtils.getString(this, SharedPreUtils.Key_URL));

//        getLocation(this);
//        Location location = LocationUtils.getInstance(this).showLocation();
//        if (location != null) {
//            showLocation(location);
//        }

        TrackerSettings settings =
                new TrackerSettings()
                        .setUseGPS(true)
                        .setUseNetwork(true)
                        .setUsePassive(true)
                        .setTimeBetweenUpdates(1 * 1000)
                        .setMetersBetweenUpdates(1);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // You need to ask the user to enable the permissions
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_ASK_LOCATION);
        } else {
            LocationTracker tracker = new LocationTracker(this, settings) {
                @Override
                public void onLocationFound(Location location) {
                    // Do some stuff
                    if (location != null) {
                        showLocation(location);
                    }
                }

                @Override
                public void onTimeout() {

                }
            };
            tracker.startListening();
        }

        initRetrofit();
        initListener();
    }

    private void initRetrofit() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl("http://106.14.172.38:8081")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(SensorDataService.class);
    }

    private void initListener() {
        mBinding.btnStart.setOnClickListener(this);
        mBinding.btnFinish.setOnClickListener(this);
        mBinding.btnClear.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
                if (TextUtils.isEmpty(mBinding.etId.getText())) {
                    Toast.makeText(this, "请输入ID", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(mBinding.etUrl.getText())) {
                    Toast.makeText(this, "请输入URL地址", Toast.LENGTH_SHORT).show();
                    return;
                }
                mBinding.etId.setEnabled(false);
                mBinding.etUrl.setEnabled(false);
                mBinding.btnStart.setEnabled(false);
                mBinding.btnFinish.setEnabled(true);
                mBinding.btnClear.setEnabled(false);
                mHandler.sendEmptyMessage(MSG_START);
                Toast.makeText(this, "已开始", Toast.LENGTH_SHORT).show();
                SharedPreUtils.setString(this, SharedPreUtils.Key_DEVICEID, mBinding.etId.getText().toString().trim());
                SharedPreUtils.setString(this, SharedPreUtils.Key_URL, mBinding.etUrl.getText().toString().trim());
                break;
            case R.id.btn_finish:
                finishSensorData();
                break;
            case R.id.btn_clear:
                clearLocalData();
                break;
            default:
                break;
        }
    }

    private String getJsonParams() {
        Gson gson = new GsonBuilder().create();

        RequestParamsVo.DeviceDataBean dataBean = new RequestParamsVo.DeviceDataBean();
        dataBean.setRSOC("80");
        dataBean.setQuanity("2Kwh");
        dataBean.setPS("0");

        RequestParamsVo requestParamsVo = new RequestParamsVo();
        requestParamsVo.setDeviceID(mBinding.etId.getText().toString().trim());
        requestParamsVo.setDeviceType("BATTERY");
        requestParamsVo.setVersion("1.0");
        requestParamsVo.setDate("2018-03-08");
        requestParamsVo.setPV("1.0");
        requestParamsVo.setTV("48V");
        requestParamsVo.setLON(longitude);
        requestParamsVo.setLAT(latitude);
        requestParamsVo.setDeviceData(dataBean);

        return gson.toJson(requestParamsVo);
    }

    private void getSensorData() {
        response = service.getSensorData(mBinding.etUrl.getText().toString().trim(), getJsonParams());
        response.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response == null)
                    return;
                mBinding.tvTip.setVisibility(View.VISIBLE);
                mBinding.tvUpdateTime.setText("更新时间：" + DateUtil.dateToStringWithAll(System.currentTimeMillis()));
                mBinding.tvResponse.setText(response.body().toString());
                mBinding.tvResponse.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorContent));

                addTxtToFileBuffered(file, DateUtil.dateToStringWithAll(System.currentTimeMillis()) + response.body().toString());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                final Throwable cause = t.getCause() != null ? t.getCause() : t;
                if (cause != null) {
                    mBinding.tvTip.setVisibility(View.VISIBLE);
                    mBinding.tvUpdateTime.setText("更新时间：" + DateUtil.dateToStringWithAll(System.currentTimeMillis()));
                    mBinding.tvResponse.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorError));
                    if (cause instanceof ConnectException) {
                        mBinding.tvResponse.setText("未能连接到服务器");
                    } else {
                        mBinding.tvResponse.setText("连接超时，请稍后重试");
                    }
                }
                addTxtToFileBuffered(file, DateUtil.dateToStringWithAll(System.currentTimeMillis()) + mBinding.tvResponse.getText());
            }
        });
    }

    private void finishSensorData() {
        mHandler.removeMessages(MSG_START);
        Toast.makeText(this, "已结束", Toast.LENGTH_SHORT).show();
        mBinding.etId.setEnabled(true);
        mBinding.etUrl.setEnabled(true);
        mBinding.btnStart.setEnabled(true);
        mBinding.btnFinish.setEnabled(false);
        mBinding.btnClear.setEnabled(true);
    }

    @Override
    protected void onDestroy() {
        mHandler.removeMessages(MSG_START);
        locationManager.removeUpdates(mListener);
        super.onDestroy();
    }

    private void getLocation(Context context) {
        //1.获取位置管理器
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        //2.获取位置提供器，GPS或是NetWork
        List<String> providers = locationManager.getProviders(true);
        if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            //如果是网络定位
            locationProvider = LocationManager.NETWORK_PROVIDER;
        } else if (providers.contains(LocationManager.GPS_PROVIDER)) {
            //如果是GPS定位
            locationProvider = LocationManager.GPS_PROVIDER;
        } else {
            Toast.makeText(this, "没有可用的位置提供器", Toast.LENGTH_SHORT).show();
            return;
        }

        //3.获取上次的位置，一般第一次运行，此值为null
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_ASK_LOCATION);
            return;
        }
        Location location = locationManager.getLastKnownLocation(locationProvider);
        if (location != null) {
            showLocation(location);
        } else {
            // 监视地理位置变化，第二个和第三个参数分别为更新的最短时间minTime和最短距离minDistace
            locationManager.requestLocationUpdates(locationProvider, 0, 0, mListener);
        }
    }

//    @SuppressLint("MissingPermission")
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case REQUEST_CODE_ASK_LOCATION:
//                if (0 != grantResults.length)
//                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                        Location location = locationManager.getLastKnownLocation(locationProvider);
//                        if (location != null) {
//                            showLocation(location);
//                        } else {
//                            // 监视地理位置变化，第二个和第三个参数分别为更新的最短时间minTime和最短距离minDistace
//                            locationManager.requestLocationUpdates(locationProvider, 0, 0, mListener);
//                        }
//                    }
//                break;
//            default:
//                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//                break;
//        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }

    private void showLocation(Location location) {
        latitude = location.getLatitude();      // 纬度
        longitude = location.getLongitude();    // 经度
        String address = "Location：纬度：" + latitude + "，经度：" + longitude;
        mBinding.tvLocation.setText(address);
    }

    LocationListener mListener = new LocationListener() {
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        // 如果位置发生变化，重新显示
        @Override
        public void onLocationChanged(Location location) {
            showLocation(location);
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            // 判断是否在两秒之内连续点击返回键，是则退出，否则不退出
            if (System.currentTimeMillis() - exitTime > 2000) {
                Snackbar snackbar = Snackbar.make(mBinding.tvResponse, getString(R.string.text_exit_app), Snackbar.LENGTH_SHORT);
                snackbar.setAction(getString(R.string.common_text_cancle), v -> exitTime = 0)
                        .setActionTextColor(ContextCompat.getColor(this, R.color.font_text_normal));
                View snakebarView = snackbar.getView();
                TextView textView = snakebarView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(getResources().getColor(R.color.white));
                snackbar.show();
                // 将系统当前的时间赋值给exitTime
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        System.exit(0);
                    }
                }.start();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 使用BufferedWriter进行文本内容的追加
     * @param file
     * @param content
     */
    private void addTxtToFileBuffered(File file, String content) {
        //在文本文本中追加内容
        BufferedWriter out = null;
        try {
            //FileOutputStream(file, true),第二个参数为true是追加内容，false是覆盖
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
            out.newLine();//换行
            out.write(content);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(out != null){
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void clearText(File file) {
        //在文本文本中追加内容
        BufferedWriter out = null;
        try {
            //FileOutputStream(file, true),第二个参数为true是追加内容，false是覆盖
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            out.write("");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(out != null){
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 使用FileWriter进行文本内容的追加
     * @param file
     * @param content
     */
    private void addTxtToFileWrite(File file, String content){
        FileWriter writer = null;
        try {
            //FileWriter(file, true),第二个参数为true是追加内容，false是覆盖
            writer = new FileWriter(file, true);
            writer.write("\r\n");//换行
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(writer != null){
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void clearLocalData() {
        clearText(file);
        Toast.makeText(this, "清除成功", Toast.LENGTH_SHORT).show();
    }
}
