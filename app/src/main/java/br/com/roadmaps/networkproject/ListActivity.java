package br.com.roadmaps.networkproject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import org.json.JSONArray;

public class ListActivity extends AppCompatActivity {
    private ListView list;
    private ProgressDialog progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        comments();
    }

    private void comments(){
        progress = ProgressDialog.show(ListActivity.this, "", "Autenticando usuário.", true, true);
        Network net = new Network(ListActivity.this);

        net.comments(new Network.HttpCallback() {
            @Override
            public void onSuccess(final JSONArray response) {
                progress.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        list = (ListView) findViewById(R.id.listView);
                        CommentAdapter meuAdapter = new CommentAdapter(getLayoutInflater(), response, ListActivity.this);
                        list.setAdapter(meuAdapter);
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
                        AlertDialog.Builder alert = new AlertDialog.Builder(ListActivity.this);
                        try {
                            if (throwable == null || throwable.getMessage() == null) {
                                alert.setTitle("Erro ao coletar dados");
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
}
