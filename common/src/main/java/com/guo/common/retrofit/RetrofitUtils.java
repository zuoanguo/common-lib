package com.guo.common.retrofit;

import android.util.Log;

import com.google.gson.JsonSyntaxException;

import org.apache.http.conn.ConnectTimeoutException;

import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * http 请求
 */
public class RetrofitUtils {


    /**
     * 连接超时时间
     */
    public static final int CONNECT_TIMEOUT = 10;
    /**
     * 读超时时间
     */
    public static final int READ_TIMEOUT = 15;
    /**
     * 写数据超时时间
     */
    public static final int WRITE_TIMEOUT = 15;
    /**
     * 是否显示请求日志
     */
    public static final boolean SHOW_LOG = true;


    private static RetrofitUtils retrofitUtils;
    private static Retrofit retrofit;
    public RetrofitUtils() {
    }

    public static RetrofitUtils getInstance(){

        if(retrofitUtils == null){
            synchronized (RetrofitUtils.class){
                if(retrofitUtils == null){
                    retrofitUtils = new RetrofitUtils();

                }
            }

        }
        return retrofitUtils;
    }


    public static synchronized Retrofit getRetrofit(String url){
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.i("xxx", "log: "+message);
            }
        });
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .build();

        if(retrofit == null){
            retrofit = new Retrofit.Builder().baseUrl(url).client(httpClient).addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();

        }
        return retrofit;
    }

    public <T>T getApiService(String url,Class<T> cl){
        Retrofit retrofit = getRetrofit(url);
        return retrofit.create(cl);
    }


    /**
     * 异常详情
     *
     * @param t 异常
     * @return 异常详情
     */
    public static String convertRetrofitExceptionMessage(Throwable t) {
        String message;
        if (t instanceof ConnectException) {
            message = "连接失败，请检查您的网络是否良好！";
        } else if (t instanceof ConnectTimeoutException) {
            message = "连接超时，请检查您的网络是否良好！";
        } else if (t instanceof UnknownHostException) {
            message = "连接服务器失败，请检查您的服务器或网络设置！";
        } else if (t instanceof SocketTimeoutException) {
            message = "连接到服务器超时，请检查您的服务器或网络设置！";
        } else if (t instanceof NumberFormatException) {
            message = "数据格式解析异常！";
        } else if (t instanceof JsonSyntaxException) {
            message = "数据解析异常！";
        } else if (t instanceof SocketException) {
            message = "连接服务器失败！";
        } else {
            message = t.getMessage();
        }
        return message;
    }
}
