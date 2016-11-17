package alex.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainConfig extends AppCompatActivity {

    EditText editIp;
    Button btnGuardar;

    EditText editVoltaje;
    Button btnVoltaje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_config);

        editIp = (EditText)findViewById(R.id.editIp);
        btnGuardar = (Button)findViewById(R.id.btnGuardar);
        editVoltaje = (EditText)findViewById(R.id.editVoltaje);
        btnVoltaje = (Button)findViewById(R.id.btnVoltaje);

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = editIp.getText().toString();
                if(ip.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Escribir IP del Servidor",Toast.LENGTH_LONG).show();
                }else{
                    if(ip.contains("http://")){
                        guardar(ip,"servidor","config");
                    }else{
                        ip = "http://" + ip;
                        guardar(ip,"servidor","config");
                    }
                }
            }
        });

        btnVoltaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String volt = editVoltaje.getText().toString();
                if(volt.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Escribir Voltaje maximo",Toast.LENGTH_LONG).show();
                }else{
                    if(Float.parseFloat( volt ) > 0){
                        guardar(volt,"volt","configVolt");
                    }else{
                        Toast.makeText(getApplicationContext(),"Escribir Voltaje maximo mayor a cero",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

    }

    public void guardar(String dato/*ingresadoIp*/ , String nombre ,String destino){
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(destino/*"config"*/, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        //editor.putInt(getString(R.string.saved_high_score), newHighScore);
        editor.putString(nombre,dato/*"servidor",ingresadoIp*/);
        editor.commit();
        Toast.makeText(getApplicationContext(),"Configuracion guardada con exito",Toast.LENGTH_LONG).show();
        finish();
    }
}
