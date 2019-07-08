package com.example.guessthecelebrity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> links = new ArrayList<>();
    ArrayList<String> names = new ArrayList<>();
    TextView textView;
    int celebSelected;
    ImageView imageView;
    String[] options = new String[4];
    int correctAnswerLocation;
    Button button0;
    Button button1;
    Button button2;
    Button button3;
    Random random;


    public void buttonPressed(View view) {
        int buttonTag = Integer.parseInt(view.getTag().toString());
        if (buttonTag == correctAnswerLocation) {
            textView.setText("Correct");
            questions();
        } else {
            textView.setText("No...it was " + names.get(celebSelected));
            questions();
        }

    }

    public class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {

            try {
                URL url = new URL(strings[0]);
                HttpURLConnection imageConnection = (HttpURLConnection) url.openConnection();
                imageConnection.connect();
                InputStream imageInput = imageConnection.getInputStream();
                Bitmap imageBitmap = BitmapFactory.decodeStream(imageInput);
                return imageBitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }
    }


    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            String sourceString = "";
            URL source;
            HttpURLConnection urlConnection;
            try {

                source = new URL(strings[0]);
                urlConnection = (HttpURLConnection) source.openConnection();

                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while (data != -1) {

                    char current = (char) data;
                    sourceString += current;
                    data = reader.read();

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return sourceString;
        }
    }


    public void questions(){
        random = new Random();
        celebSelected = random.nextInt(links.size());

        DownloadImage celebImage = new DownloadImage();
        Bitmap image = null;
        try {
            image = celebImage.execute(links.get(celebSelected)).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        imageView.setImageBitmap(image);
        correctAnswerLocation = random.nextInt(4);
        int holder;
        holder = random.nextInt(links.size());

        for (int i = 0; i < 4; i++) {

            if (i == correctAnswerLocation) {
                options[i] = names.get(celebSelected);

            } else {
                holder = random.nextInt(links.size());
                while (holder == celebSelected) {
                    holder = random.nextInt(links.size());
                }

                options[i] = names.get(holder);
            }

        }

        button0.setText(options[0]);
        button1.setText(options[1]);
        button2.setText(options[2]);
        button3.setText(options[3]);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);
        button0 = findViewById(R.id.button1);
        button1 = findViewById(R.id.button2);
        button2 = findViewById(R.id.button3);
        button3 = findViewById(R.id.button4);
        textView = findViewById(R.id.textView);

        DownloadTask object = new DownloadTask();
        String source = null;
        try {

            source = object.execute("http://www.posh24.se/kandisar").get();
            String[] splitSource = source.split("<div class=\"sidebarContainer\">");

            Pattern p = Pattern.compile("img src=\"(.*?)\"");
            Matcher m = p.matcher(splitSource[0]);

            while (m.find()) {
                links.add(m.group(1));
            }

            p = Pattern.compile("alt=\"(.*?)\"");
            m = p.matcher(splitSource[0]);

            while (m.find()) {
                names.add(m.group(1));
            }

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    questions();
    }
}
