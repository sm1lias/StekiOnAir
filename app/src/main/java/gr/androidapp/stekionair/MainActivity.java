package gr.androidapp.stekionair;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationManagerCompat;
import gr.androidapp.stekionair.Services.OnClearFromRecentService;
import gr.androidapp.stekionair.databinding.ActivityMainBinding;


import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements NetworkChangeReceiver.ConnectionChangeCallback, Playable {
    private WebView webView;
    private final static String stream = "https://i8.streams.ovh/sc/stekiona/stream";
    private MediaPlayer mediaPlayer;
    private boolean started, prepared, run, wLon;
    private NotificationManager notificationManager;
    private String data, data2, title2;
    private String title="Loading";
    private String cDay ="";
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private String TAG;
    private String result="";
    private PowerManager.WakeLock wl ;
    private PowerManager powerManager;
    private ActivityMainBinding binding;



    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);

        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wl = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyApp::MyWakelockTag");

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("ΠΡΟΓΡΑΜΜΑ");

        getMeta();
        getDay();


        binding.textViewTitle.setText(getTitle(getTime()));
        data=data2;


        String[] arraySpinner = new String[] {
                "ΠΡΟΓΡΑΜΜΑ", "ΔΕΥΤΕΡΑ", "ΤΡΙΤΗ", "ΤΕΤΑΡΤΗ", "ΠΕΜΠΤΗ", "ΠΑΡΑΣΚΕΥΗ", "ΣΑΒΒΑΤΟ", "ΚΥΡΙΑΚΗ"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner){
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                } else {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextSize(20);
                }
                if (position==1 && cDay.equals("Monday"))
                    tv.setTextColor(Color.BLUE);
                if (position==2 && cDay.equals("Tuesday"))
                    tv.setTextColor(Color.BLUE);
                if (position==3 && cDay.equals("Wednesday"))
                    tv.setTextColor(Color.BLUE);
                if (position==4 && cDay.equals("Thursday"))
                    tv.setTextColor(Color.BLUE);
                if (position==5 && cDay.equals("Friday"))
                    tv.setTextColor(Color.BLUE);
                if (position==6 && cDay.equals("Saturday"))
                    tv.setTextColor(Color.BLUE);
                if (position==7 && cDay.equals("Sunday"))
                    tv.setTextColor(Color.BLUE);

                return view;
            }
        };


        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinner.setAdapter(adapter);


        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                if(position==1)
                    schedule("Monday");
                if(position==2)
                    schedule("Tuesday");
                if(position==3)
                    schedule("Wednesday");
                if(position==4)
                    schedule("Thursday");
                if(position==5)
                    schedule("Friday");
                if(position==6)
                    schedule("Saturday");
                if(position==7)
                    schedule("Sunday");
                binding.spinner.setSelection(0);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




        run=true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createChannel();

        }
        registerReceiver(broadcastReceiver, new IntentFilter("TRACKS_TRACKS"));
        startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
        IntentFilter intentFilter = new
                IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");

        NetworkChangeReceiver networkChangeReceiver = new NetworkChangeReceiver();

        registerReceiver(networkChangeReceiver, intentFilter);

        networkChangeReceiver.setConnectionChangeCallback((NetworkChangeReceiver.ConnectionChangeCallback) this);
//        CookieSyncManager.createInstance(this);
//        CookieManager cookieManager = CookieManager.getInstance();
//        cookieManager.setAcceptCookie(true);

        webView=findViewById(R.id.webView);
        webView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        webView.setWebViewClient(new WebViewClient(){

            public void onPageFinished(WebView view,String url){
                ProgressBar progressbar = (ProgressBar) findViewById(R.id.progressBar);
                progressbar.setVisibility(View.GONE);

            }
        });
        webView.loadUrl("https://stekionair.chatango.com/");



        WebSettings webSettings = webView.getSettings();

        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setSupportZoom(true);
//        webSettings.setSaveFormData(true);


        binding.play.setEnabled(false);
        binding.play.setText("ΦΟΡΤΩΣΗ...");
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);


        binding.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (started) onTrackPause();
                else onTrackPlay();
            }
        });

       new PlayTask().execute(stream);
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CustomNotification.CHANNEL_ID, "StekiOnAir", NotificationManager.IMPORTANCE_LOW);
            notificationManager=getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        kill_app();
    }

    @Override
    public void onConnectionChange(boolean isConnected) {
        if(isConnected){
            // will be called when internet is back
            webView.reload();
        }
        else{
            // will be called when internet is gone.
        }

    }
    BroadcastReceiver broadcastReceiver= new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
                String action = intent.getExtras().getString("actionname");

                switch (action) {
                    case CustomNotification.ACTION_PLAY:
                        if (started) {
                            onTrackPause();
                        } else {
                            onTrackPlay();
                        }
                        break;
                    case CustomNotification.ACTION_CLOSE:
                        onX();
                        break;
                }

        }
    };

    @Override
    public void onTrackPlay() {
        mediaPlayer.start();
        wl.acquire();
        wLon =true;
        started = true;
        binding.play.setText("ΠΑΥΣΗ");
        CustomNotification.customNotification(MainActivity.this,data,title, R.drawable.ic_pause);
    }

    @Override
    public void onTrackPause() {
        mediaPlayer.reset();
        wl.release();
        wLon =false;
        try {
            mediaPlayer.setDataSource(stream);
            mediaPlayer.prepare();
            CustomNotification.customNotification(MainActivity.this,data,title, R.drawable.ic_play_arrow);
        } catch (IOException e) {
            e.printStackTrace();
        }
        started = false;
        binding.play.setText("ΕΝΑΡΞΗ");
    }

    @Override
    public void onX() {
        kill_app();
    }

    public String getTime(){
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        if (minutes<10)
            return String.valueOf(hour)+"0"+String.valueOf(minutes);
        return String.valueOf(hour)+String.valueOf(minutes);
    }

    public void getDay(){
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        switch (day) {
            case Calendar.SUNDAY:
                cDay ="Sunday";
                break;
            case Calendar.MONDAY:
                cDay ="Monday";
                break;
            case Calendar.TUESDAY:
                cDay ="Tuesday";
                break;
            case Calendar.WEDNESDAY:
                cDay ="Wednesday";
                break;
            case Calendar.THURSDAY:
                cDay ="Thursday";
                break;
            case Calendar.FRIDAY:
                cDay ="Friday";
                break;
            case Calendar.SATURDAY:
                cDay ="Saturday";
                break;
        }
    }

    public void schedule(String day){
        myRef.child(day).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                StringBuilder builder = new StringBuilder();
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for(DataSnapshot datas: dataSnapshot.getChildren()) {
                    builder.append(datas.getKey()).append("\n");
                    builder.append(datas.getValue(String.class)).append("\n");
                    builder.append("\n");
                }
                showMessage(builder.toString());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    public String getTitle(String tm){
        int time=Integer.parseInt(tm);

        myRef.child(cDay).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String previous="";
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot row : dataSnapshot.getChildren()) {
                    String first=row.getKey();
                    String[] parts=first.split("-");
                    String[] parts2=parts[0].split(":");
                    int num1=Integer.parseInt(parts2[0]+parts2[1]);
                    if(time<num1){
                        fResult(previous);
                        break;
                    }
                    previous =first+" "+row.getValue(String.class);
                    fResult(previous);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        return result;
    }

    public void fResult(String previous){
        result=previous;
    }

    public void showMessage(String message) { // show the data from the database
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.support_simple_spinner_dropdown_item, null);
        builder.setView(view);
        TextView title = new TextView(this);
        title.setText("ΠΡΟΓΡΑΜΜΑ");
        title.setTextSize(20);
        title.setTextColor(Color.BLACK);
        title.setGravity(Gravity.CENTER);
        builder.setCustomTitle(title)
                .setMessage(message)
                .setCancelable(true);

        AlertDialog dialog = builder.show();
        TextView messageText = (TextView)dialog.findViewById(android.R.id.message);
        messageText.setGravity(Gravity.CENTER);
        dialog.show();
    }

    public void kill_app(){
        if(wLon) wl.release();

        NotificationManagerCompat.from(this).cancelAll();
        unregisterReceiver(broadcastReceiver);

        if (started){
            mediaPlayer.reset();
        }
        System.exit(0);
    }

    private class PlayTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {

            try {
                mediaPlayer.setDataSource(strings[0]);
                mediaPlayer.prepare();
                prepared = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return prepared;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            binding.play.setEnabled(true);
            binding.play.setText("ΕΝΑΡΞΗ");
        }
    }

    public void change_not() {
        if(started)
            CustomNotification.customNotification(MainActivity.this,data,title, R.drawable.ic_pause);
        else
            CustomNotification.customNotification(MainActivity.this,data,title, R.drawable.ic_play_arrow);
    }

    private void getMeta() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                try {
                    IcyStreamMeta icy = new IcyStreamMeta(new URL(stream));
                    /*if (icy.getArtist()=="") {
                        data = icy.getTitle();
                    }
                    else {
                        data = icy.getArtist() + " - " + icy.getTitle();
                    }*/
                    data2= icy.getTitle();
                    title2=getTitle(getTime());

                    final TextView meta = (TextView) findViewById(R.id.textView);

                    runOnUiThread(new Runnable() {
                        public void run() {

                            if (!data2.equals(data)){
                                data=data2;
                                meta.setText(data);
                                change_not();
                            }
                            if (!title2.equals(title)) {
                                title = title2;
                                getDay();
                                binding.textViewTitle.setText(title);
                                change_not();
                            }
                        }
                    });
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 2000);
    }
}
