package br.com.roadmaps.networkproject;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ChosenImages;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.kbeanie.imagechooser.api.ImageChooserManager;
import com.kbeanie.imagechooser.exceptions.ChooserException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity implements ImageChooserListener {
    String path_image;
    String dataFoto;
    String nameFoto;
    ImageChooserManager imageChooserManager;

    ImageView imgPhoto;
    String img1Path = "";

    private static final int INTENT_CAMERA = 19;

    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgPhoto = (ImageView)findViewById(R.id.imageView2);


        if(!loadCookie()){
            login();
        }



//       comments();

    }


    private boolean loadCookie(){
        boolean flag = false;
        PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
        if (!myCookieStore.getCookies().isEmpty()) {
            if (myCookieStore.getCookies().get(0).getDomain().equals("teste-aula-ios.herokuapp.com")) {
                flag = true;
            }
        } else {
            flag = false;
        }
        return flag;
    }

    private void login(){
        progress = ProgressDialog.show(MainActivity.this, "", "Autenticando usuário.", true, true);
        Network net = new Network(MainActivity.this);



        net.login("crystian@roadmaps.com.br", "12345678", new Network.HttpCallback() {
            @Override
            public void onSuccess(final String response) {
                progress.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Fazer algo
                        comments();
                    }
                });
            }

            @Override
            public void onFailure(String response, final Throwable throwable) {
                progress.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new PersistentCookieStore(getApplicationContext()).removeAll();
                        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                        try {
                            if (throwable == null || throwable.getMessage() == null) {
                                alert.setTitle("Erro ao autenticar o fiscal!");
                            } else if (throwable != null) {
                                if (throwable.getMessage().contains("Unable to resolve host")) {//
                                    alert.setTitle("Sem internet!");
                                } else {
                                    alert.setTitle("Falha na conexão, tente novamente.");
                                }
                            }
                        } catch (NullPointerException e) {}
                        alert.setPositiveButton("OK!", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        });
                        alert.show();
                    }
                });
            }

            @Override
            public void onSuccess(final JSONArray response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {}
                });
            }
        });
    }

    private void comments(){
        progress = ProgressDialog.show(MainActivity.this, "", "Autenticando usuário.", true, true);
        Network net = new Network(MainActivity.this);

        net.comments(new Network.HttpCallback() {
            @Override
            public void onSuccess(final JSONArray response) {
                progress.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                        alert.setTitle("");
                        alert.show();
                    }
                });
            }

            @Override
            public void onFailure(String response, final Throwable throwable) {
                progress.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new PersistentCookieStore(getApplicationContext()).removeAll();
                        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                        try {
                            if (throwable == null || throwable.getMessage() == null) {
                                alert.setTitle("Erro ao autenticar");
                            } else if (throwable != null) {
                                if (throwable.getMessage().contains("Unable to resolve host")) {//
                                    alert.setTitle("Sem internet!");
                                } else {
                                    alert.setTitle("Falha na conexão, tente novamente.");
                                }
                            }
                        } catch (NullPointerException e) {}
                        alert.setPositiveButton("OK!", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        });
                        alert.show();
                    }
                });
            }

            @Override
            public void onSuccess(final String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {}
                });
            }
        });
    }

    public void makeAComment(View view) {
        progress = ProgressDialog.show(MainActivity.this, "", "Autenticando usuário.", true, true);
        Network net = new Network(MainActivity.this);

        EditText text = (EditText) findViewById(R.id.editText);

        net.commentWithPicture(text.getText().toString(),"Sidd", path_image,new Network.HttpCallback() {
            @Override
            public void onSuccess(final JSONArray response) {
                progress.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                        alert.setTitle("");
                        alert.show();
                    }
                });
            }

            @Override
            public void onFailure(String response, final Throwable throwable) {
                progress.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new PersistentCookieStore(getApplicationContext()).removeAll();
                        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                        try {
                            if (throwable == null || throwable.getMessage() == null) {
                                alert.setTitle("Erro ao autenticar");
                            } else if (throwable != null) {
                                if (throwable.getMessage().contains("Unable to resolve host")) {//
                                    alert.setTitle("Sem internet!");
                                } else {
                                    alert.setTitle("Falha na conexão, tente novamente.");
                                }
                            }
                        } catch (NullPointerException e) {}
                        alert.setPositiveButton("OK!", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        });
                        alert.show();
                    }
                });
            }

            @Override
            public void onSuccess(final String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progress.dismiss();
                    }
                });
            }
        });
    }

    public void takePicture(View view) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        }
        else {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());

            File folder = new File(Environment.getExternalStorageDirectory() + "/bichooser");
            if (!folder.exists()) {
                folder.mkdir();
            }

            DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
            java.util.Date dtt = new java.util.Date();
            dataFoto = dateFormat.format(dtt);
            path_image = folder.getAbsoluteFile() + "/"+ "Item" + "-" + dataFoto + ".jpg";

            File f = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
            Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent2.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            intent2.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
            startActivityForResult(intent2, INTENT_CAMERA);
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 100: {
                // Se o pedido foi cancelado o array está vazio.
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    File f = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, INTENT_CAMERA);
                }
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == INTENT_CAMERA && resultCode == Activity.RESULT_OK) {
            SharedPreferences settings = getSharedPreferences("image", 0);
            nameFoto = settings.getString("nameFoto", "");
//            alterou = true;

            try {
                File f = new File(Environment.getExternalStorageDirectory(), "temp.jpg");

                ExifInterface ei = null;
                ei = new ExifInterface(f.getAbsolutePath());
                final int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

                Bitmap cameraBitmapOrig = rotateImage(f.getAbsolutePath(), orientation);
                System.gc();

                double originalHeight = cameraBitmapOrig.getHeight();
                double scale = originalHeight / 600;

                int wi = (int) (cameraBitmapOrig.getWidth() / scale);
                int hey = (int) (cameraBitmapOrig.getHeight() / scale);
                Bitmap cameraBitmapResized = Bitmap.createScaledBitmap(cameraBitmapOrig, wi, hey, true);


                try {
                    f.delete();
                    File folder = new File(Environment.getExternalStorageDirectory() + "/bichooser");
                    if (!folder.exists()) {
                        folder.mkdir();
                    }

                    DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
                    java.util.Date dtt = new java.util.Date();
                    dataFoto = dateFormat.format(dtt);
                    path_image = folder.getAbsoluteFile() + "/" + "Item" + "-" + dataFoto + ".jpg";

                    FileOutputStream fos = new FileOutputStream(path_image);
                    cameraBitmapResized.compress(Bitmap.CompressFormat.JPEG, 80, fos);
                    fos.flush();
                    fos.close();
                    fos = null;
                    cameraBitmapResized.recycle();
                    cameraBitmapResized = null;
                    cameraBitmapOrig.recycle();
                    cameraBitmapOrig = null;


                    System.gc();
                    addImageToGallery(path_image, MainActivity.this);



                    imgPhoto.setVisibility(View.VISIBLE);
                    img1Path = path_image;
                    Glide.with(MainActivity.this).load(new File(path_image)).into(imgPhoto);



                } catch (FileNotFoundException e) {

                } catch (IOException e) {

                }
            }catch (Exception e){

            }

        }
        else if ((resultCode == RESULT_OK) &&(requestCode == ChooserType.REQUEST_PICK_PICTURE)) {
            imageChooserManager.submit(requestCode, data);
        }

    }



    public Bitmap rotateImage(String photoPath, int orientation) {
        float angle = 90;

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                angle = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                angle = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                angle = 270;
                break;
            case ExifInterface.ORIENTATION_UNDEFINED:
                return BitmapFactory.decodeFile(photoPath);
        }

        Bitmap retVal;
        Bitmap source = BitmapFactory.decodeFile(photoPath);

        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        retVal = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
        return retVal;
    }

    public static void addImageToGallery(String filePath, Context context) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, filePath);
        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    public void gallery(View view) {
        try {
            imageChooserManager = new ImageChooserManager(MainActivity.this, ChooserType.REQUEST_PICK_PICTURE);
            imageChooserManager.setImageChooserListener(MainActivity.this);
            imageChooserManager.choose();
        } catch (ChooserException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onImageChosen(final ChosenImage chosenImage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (chosenImage != null) {
                        imgPhoto.setVisibility(View.VISIBLE);
                        img1Path = chosenImage.getFilePathOriginal();
                        Glide.with(MainActivity.this).load(new File(chosenImage.getFilePathOriginal())).into(imgPhoto);
                }
            }
        });
    }

    @Override
    public void onError(String s) {

    }

    @Override
    public void onImagesChosen(ChosenImages chosenImages) {

    }
}
