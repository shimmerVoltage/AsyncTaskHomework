package com.example.asynctaskhomework;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private TextView statusTextView;
    private TextView progressTextView;
    private ProgressBar progressBar;
    private Button startButton;
    private Button cancelButton;
    private MyAsyncTask myAsyncTask;
    private static final int TOTAL_ITERATIONS = 100;
    private static final int DELAY_MS = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusTextView = findViewById(R.id.statusTextView);
        progressTextView = findViewById(R.id.progressTextView);
        progressBar = findViewById(R.id.progressBar);
        startButton = findViewById(R.id.startButton);
        cancelButton = findViewById(R.id.cancelButton);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTask();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelTask();
            }
        });
    }

    private void startTask() {
        myAsyncTask = new MyAsyncTask();
        myAsyncTask.execute();

        startButton.setEnabled(false);
        cancelButton.setEnabled(true);
    }

    private void cancelTask() {
        if (myAsyncTask != null && myAsyncTask.getStatus() == AsyncTask.Status.RUNNING) {
            myAsyncTask.cancel(true);
        }
    }

    private class MyAsyncTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            statusTextView.setText("Status: Running");
            progressBar.setProgress(0);
            progressTextView.setText("0%");
        }

        @Override
        protected Void doInBackground(Void... params) {
            for (int i = 1; i <= TOTAL_ITERATIONS; i++) {
                if (isCancelled()) {
                    break;
                }

                try {
                    Thread.sleep(DELAY_MS);
                } catch (InterruptedException e) {
                    if (isCancelled()) {
                        break;
                    }
                }

                int progress = (i * 100) / TOTAL_ITERATIONS;
                publishProgress(progress);
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int progress = values[0];
            progressBar.setProgress(progress);
            progressTextView.setText(progress + "%");
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (!isCancelled()) {
                statusTextView.setText("Status: Finished");
                progressBar.setProgress(100);
                progressTextView.setText("100%");
                Toast.makeText(MainActivity.this, "Task complete!", Toast.LENGTH_SHORT).show();

                startButton.setEnabled(true);
                cancelButton.setEnabled(false);
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            statusTextView.setText("Status: Cancelled");
            Toast.makeText(MainActivity.this, "Task cancelled!", Toast.LENGTH_SHORT).show();

            startButton.setEnabled(true);
            cancelButton.setEnabled(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myAsyncTask != null && myAsyncTask.getStatus() == AsyncTask.Status.RUNNING) {
            myAsyncTask.cancel(true);
        }
    }
}