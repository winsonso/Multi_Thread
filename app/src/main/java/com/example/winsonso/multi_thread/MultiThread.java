package com.example.winsonso.multi_thread;

import android.content.Context;

import android.graphics.Color;
import android.graphics.PorterDuff;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.List;

public class MultiThread extends AppCompatActivity {
    private FileOutputStream numberOut;
    private String filename = "numbers.txt";
    private ListAdapter numberAdapter;
    private List<String> numberLoad = new ArrayList<>();
    private ListView listView;

    private ProgressBar progressBar;
    private int progressStatus;
    private TextView status;

    Handler loadHandler = new Handler() {
        @Override
        public void handleMessage (Message msg) { loadList(); }
    };

    Handler progressEr = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            progressBar.setProgress(progressStatus);
            status.setText(progressStatus + "/" + progressBar.getMax());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_thread);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        listView = (ListView) findViewById(R.id.listView2);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        progressStatus = 0;
        progressBar.setProgress(0);
        status = (TextView) findViewById(R.id.textView);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_multi_thread, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void create(View view) {

        Thread theThread = new Thread(new Runnable() {
            @Override
            public void run() {
                createFile();
                writeToFile();
            }
        });
        theThread.start();
    }

    public void createFile() {
        String text = "";
        try {
            numberOut = openFileOutput(filename, MODE_PRIVATE);
            numberOut.write(text.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeToFile() {
        try {
            for (int i = 1; i <= 10; i++) {
                numberOut = openFileOutput(filename, Context.MODE_APPEND);
                numberOut.write((i + "\n").getBytes());
                progressStatus += 10;
                progressEr.sendEmptyMessage(0);
                Thread.sleep(250);
            }
            numberOut.close();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    public void load(View view) throws FileNotFoundException {
        //countLoadTime++;
        loadList();
    progressStatus = 0;
    progressBar.setProgress(progressStatus);

        Thread theThread = new Thread(new Runnable() {
        @Override
        public void run() {
            loadAndParse();
        }
        });
        theThread.start();
    }

    public  void loadList() {
        numberAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, numberLoad);
        ListView listView = (ListView) findViewById(R.id.listView2);
        listView.setAdapter(numberAdapter);
    }

    public void loadAndParse() {
        try {
            FileInputStream file = openFileInput(filename);
            InputStreamReader input = new InputStreamReader(file);
            BufferedReader br = new BufferedReader(input);
            String line;

            while ((line = br.readLine()) != null){
                numberLoad.add(line);
                progressStatus += 10;
                loadHandler.sendEmptyMessage(0);
                progressEr.sendEmptyMessage(0);
                Thread.sleep(250);
            }
            input.close();
        } catch (IOException | InterruptedException e){
            e.printStackTrace();
        }
    }

    public void clear (View view){
        numberAdapter = null;
        numberLoad.clear();

        progressStatus = 0;
        progressBar.setProgress(progressStatus);

        status.setText(progressStatus + "/" + progressBar.getMax());

        listView.setAdapter(numberAdapter);
    }

}



