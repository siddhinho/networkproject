package br.com.roadmaps.networkproject;

import android.app.ProgressDialog;
import android.content.Context;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by sidd on 26/04/18.
 */

public class Network {
    public static final String URL_APPLICATION = "http://teste-aula-ios.herokuapp.com";
    private static final String QUERY_LOGIN = URL_APPLICATION + "/users/sign_in.json";
    private static final String QUERY_COMMENTS = URL_APPLICATION + "/comments.json";

    private Context context;
    PersistentCookieStore myCookie;


    public Network(Context context) {
        this.context = context;
        myCookie = new PersistentCookieStore(context);
    }


    public void login(String email, String password, final HttpCallback cb) {

        final String json = "{\"user\":{" +
                "\"email\":\""+email+"\"," +
                "\"password\":\""+password+"\"}}";

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, json);
        if (myCookie == null) {
            myCookie = new PersistentCookieStore(context);
        }
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(30, TimeUnit.SECONDS);
        client.setWriteTimeout(30, TimeUnit.SECONDS);
        client.setCookieHandler(new CookieManager(myCookie, CookiePolicy.ACCEPT_ALL));

        Request request = new Request.Builder()
                .url(QUERY_LOGIN)
                .post(body)
                .build();


        final Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                if (!call.isCanceled()) {
                    cb.onFailure(null, e);
                }
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    cb.onSuccess(response.body().string());
                } else {
                    cb.onFailure(response.body().string(), null);
                }
            }
        });
    }

    public void makeComment(String comment, String autor, final HttpCallback cb) {

        final String json = "{\"comment\":{" +
                "\"user\":\""+autor+"\"," +
                "\"content\":\""+comment+"\"}}";

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, json);
        if (myCookie == null) {
            myCookie = new PersistentCookieStore(context);
        }
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(30, TimeUnit.SECONDS);
        client.setWriteTimeout(30, TimeUnit.SECONDS);
        client.setCookieHandler(new CookieManager(myCookie, CookiePolicy.ACCEPT_ALL));

        Request request = new Request.Builder()
                .url(QUERY_COMMENTS)
                .post(body)
                .build();


        final Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                if (!call.isCanceled()) {
                    cb.onFailure(null, e);
                }
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    cb.onSuccess(response.body().string());
                } else {
                    cb.onFailure(response.body().string(), null);
                }
            }
        });
    }

    public void commentWithPicture(String comment, String autor, String path, final HttpCallback cb) {
        MediaType MEDIA_TYPE_PNG = MediaType.parse("image/jpg");
        MultipartBuilder multiPart = new MultipartBuilder();
        multiPart.type(MultipartBuilder.FORM);

        try {
            multiPart.addFormDataPart("comment[user]",autor);
            multiPart.addFormDataPart("comment[content]",comment);
            multiPart.addFormDataPart("comment[picture]", "imagem.jpg",
                    RequestBody.create(MEDIA_TYPE_PNG, new File(path)));
        } catch (NullPointerException e) {

        }

        if (myCookie == null) {
            myCookie = new PersistentCookieStore(context);
        }
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(120, TimeUnit.SECONDS);
        client.setWriteTimeout(120, TimeUnit.SECONDS);
        client.setReadTimeout(120, TimeUnit.SECONDS);
        client.setCookieHandler(new CookieManager(myCookie,CookiePolicy.ACCEPT_ALL));

        RequestBody requestBody = multiPart.build();
        Request request = new Request.Builder()
                .url(QUERY_COMMENTS)
                .post(requestBody)
                .build();


        final Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                if (!call.isCanceled()) {
                    cb.onFailure(null, e);
                }
            }

            @Override
            public void onResponse(Response response) throws IOException {
                final String value = response.body().string();
                if (response.isSuccessful()) {
                    cb.onSuccess(value);
                } else {
                    cb.onFailure(value, null);
                }
            }
        });
    }


    public void comments(final HttpCallback cb){


        if (myCookie == null) {
            myCookie = new PersistentCookieStore(context);
        }
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(30, TimeUnit.SECONDS);
        client.setWriteTimeout(30, TimeUnit.SECONDS);
        client.setCookieHandler(new CookieManager(myCookie, CookiePolicy.ACCEPT_ALL));

        Request request = new Request.Builder()
                .url(QUERY_COMMENTS)
                .get()
                .build();

        final Call call = client.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Request request, IOException e) {
                if (!call.isCanceled()) {
                    cb.onFailure(null, e);
                }
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String value = response.body().string();
                        JSONArray jsonArray = new JSONArray(value);
                        cb.onSuccess(jsonArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    final String value = response.body().string();
                    cb.onFailure(value, null);
                }
            }
        });
    }

    public interface HttpCallback {
        void onSuccess (final JSONArray responseArray );
        void onSuccess(final String response);
        void onFailure(final String response, final Throwable throwable);
    }
}
