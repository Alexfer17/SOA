package alex.myapplication;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;

import android.os.Bundle;

import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    Button btnConsumo1,btnConsumo2,btnConsumo3,btnConsumo4,btnConfig;
    TextView txtResultado,txtConsumo,text;
    String servidor="";
    String maxVolt="";
    final Handler handler = new Handler();

    SensorManager sensorManager;
    Sensor sensor;

    Float volta;

    android.support.v4.app.NotificationCompat.Builder mBuilder;
    int mId = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnConsumo1 = (Button)findViewById(R.id.btnConsumo1);
        btnConsumo2 = (Button)findViewById(R.id.btnConsumo2);
        btnConsumo3 = (Button)findViewById(R.id.btnConsumo3);
        btnConsumo4 = (Button)findViewById(R.id.btnConsumo4);
        btnConfig = (Button)findViewById(R.id.btnconfig);
        txtResultado = (TextView)findViewById(R.id.txtResultado);

        txtConsumo = (TextView)findViewById(R.id.txtConsumo);
        text = (TextView)findViewById(R.id.text);

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        sensorManager.registerListener(MainActivity.this, sensor,
                SensorManager.SENSOR_DELAY_NORMAL);

        //INICIO DE NOTIFICACION

        mBuilder =
                 new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.unnamed/*android.R.drawable.ic_notification_clear_all*/)
                        .setContentTitle("Mi Consumo")
                        .setContentText("Tu consumo esta por encima del maximo!");
        //activity que se lanza al hace click en la notificacion
        Intent resultIntent = new Intent(this, MainActivity.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.

        //TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
        //stackBuilder.addParentStack(MainActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
//        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                //stackBuilder.getPendingIntent(
                PendingIntent.getActivity(
                        this,
                        0,resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        //Con esto se lanza la notificacion
        /*
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(mId, mBuilder.build());*/
        //FIN DE NOTIFICACION

        btnConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent abrirConfig = new Intent(MainActivity.this,MainConfig.class);
                startActivity(abrirConfig);
            }
        });

        btnConsumo1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, "Funciona el Botton", Toast.LENGTH_SHORT).show();

                solicitud("led1");
            }
        });

        btnConsumo2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, "Funciona el Botton", Toast.LENGTH_SHORT).show();

                solicitud("led2");
            }
        });

        btnConsumo3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, "Funciona el Botton", Toast.LENGTH_SHORT).show();

                solicitud("led3");
            }
        });

        btnConsumo4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, "Funciona el Botton", Toast.LENGTH_SHORT).show();

                solicitud("led4");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("config", Context.MODE_PRIVATE);
        servidor = sharedPref.getString("servidor","0");
/*
        SharedPreferences sharedPref2 = getApplicationContext().getSharedPreferences("configVolt", Context.MODE_PRIVATE);
        maxVolt = sharedPref2.getString("volt","0");*/

        if(servidor.equals("0")){
            //txtResultado.setText("Servidor vacio");//
            solicitarServidor();
        }else{
            //solicitud("");//

            handler.postDelayed(actualizarEstado,0);
        }
    }

    private Runnable actualizarEstado = new Runnable() {
        @Override
        public void run() {
            ///CAMBIADO
            //solicitud("test1.html");
            solicitud("");
            handler.postDelayed(this,2000);
        }
    };

    public void solicitud(String comando){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        //ACA VA LA IP DE ARDUINO sin /test.html
        //String url = "http://192.168.0.6:8090/test1.html"+comando;
        //String url = "http://192.168.1.1:8090/" + comando;

        String url = servidor+"/"+comando+"?";//servidor+":8090/"+comando;

        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadWebpageTask().execute(url);
        } else {
            txtResultado.setText("Revise su conexion de internet o verifique la IP ingresada");
        }
    }

    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            Connection conexion = new Connection();

            return conexion.getArduino(urls[0]);

        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            txtResultado.setText("");
            int index,end;

            if(result!=null) {
                txtResultado.setText("");
                //CIRCUITO 1
                if (result.contains("Led1 - ON")) {
                    //btnConsumo.setText("LED-ON");
                    btnConsumo1.setBackgroundResource(R.drawable.toggle_on);
                }else{
                if (result.contains("Led1 - OFF")) {
                    //btnConsumo.setText("LED-OFF");
                    btnConsumo1.setBackgroundResource(R.drawable.toggle_off);
                }}
                //CIRCUITO 2
                if (result.contains("Led2 - ON")) {
                    //btnConsumo.setText("LED-ON");
                    btnConsumo2.setBackgroundResource(R.drawable.toggle_on);
                }else{
                if (result.contains("Led2 - OFF")) {
                    //btnConsumo.setText("LED-OFF");
                    btnConsumo2.setBackgroundResource(R.drawable.toggle_off);
                }}
                //CIRCUITO 3
                if (result.contains("Led3 - ON")) {
                    //btnConsumo.setText("LED-ON");
                    btnConsumo3.setBackgroundResource(R.drawable.toggle_on);
                }else{
                if (result.contains("Led3 - OFF")) {
                    //btnConsumo.setText("LED-OFF");
                    btnConsumo3.setBackgroundResource(R.drawable.toggle_off);
                }}
                //CIRCUITO 4
                if (result.contains("Led4 - ON")) {
                    //btnConsumo.setText("LED-ON");
                    btnConsumo4.setBackgroundResource(R.drawable.toggle_on);
                }else{
                if (result.contains("Led4 - OFF")) {
                    //btnConsumo.setText("LED-OFF");
                    btnConsumo4.setBackgroundResource(R.drawable.toggle_off);
                }}

                if (result.contains("volt-")) {
                    index = result.indexOf("volt-");
                    end = result.indexOf("-end");
                    volta = Float.parseFloat(result.substring(index + 5, end));
                    txtConsumo.setText(result.substring(index + 5, end) + " Amper");

                    SharedPreferences sharedPref2 = getApplicationContext().getSharedPreferences("configVolt", Context.MODE_PRIVATE);
                    maxVolt = sharedPref2.getString("volt", "0");

                    if ( !maxVolt.equals("0")){
                        //text.setText(maxVolt);
                        NotificationManager mNotificationManager =
                                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        if (volta > Float.parseFloat(maxVolt)) {

                            mNotificationManager.notify(mId, mBuilder.build());
                        } else {
                            mNotificationManager.cancelAll();
                        }
                    }
                }
                txtResultado.setText("");
            }else{
                txtResultado.setText("Fallo la conección");
            }
        }
    }

    public void solicitarServidor(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setTitle("Error de servidor");

        builder.setMessage("No se ha configurado ningun servidor, desea configurar el servidor ahora?");
                builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                        Intent abrirConfig = new Intent(MainActivity.this,MainConfig.class);
                        startActivity(abrirConfig);
                    }
                });
                builder.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        finish();
                    }
                });
        builder.show();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        String texto = String.valueOf(event.values[0]);

        float valor = Float.parseFloat(texto);

        if(valor == 0.0){
            solicitud("led1");
            solicitud("led2");
            solicitud("led3");
            solicitud("led4");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
