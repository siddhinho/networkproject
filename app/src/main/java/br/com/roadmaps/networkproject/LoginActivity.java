package br.com.roadmaps.networkproject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import org.json.JSONArray;

public class LoginActivity extends AppCompatActivity {

    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    private void login(String email, String password){
        progress = ProgressDialog.show(LoginActivity.this, "", "Autenticando usuário.", true, true);
        Network net = new Network(LoginActivity.this);


        net.login(email, password, new Network.HttpCallback() {
            @Override
            public void onSuccess(final String response) {
                progress.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(LoginActivity.this,ListActivity.class));
                        finish();
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
                        AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
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

    public void login(View view) {
        EditText emailText = findViewById(R.id.emailEditText);
        EditText passwordText = findViewById(R.id.passwordEditText);

        login(emailText.getText().toString(), passwordText.getText().toString());
    }
}
