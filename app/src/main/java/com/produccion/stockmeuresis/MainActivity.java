package com.produccion.stockmeuresis;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import data.ConexionSQLiteHelper;

public class MainActivity extends AppCompatActivity {
    Button salir;
    Button btnCarga;
    ConexionSQLiteHelper cnn;
    int nCant=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        btnCarga=(Button)findViewById(R.id.btnConsultaLista);
        cnn=new ConexionSQLiteHelper(getApplicationContext());
       nCant=cnn.contarCantidad();
       if(nCant<1){
           btnCarga.setEnabled(false);
       }else{btnCarga.setEnabled(true);}
    }
    @Override
    protected void onStart() {
        super.onStart();
       // Toast.makeText(this, function(15,6),Toast.LENGTH_LONG).show();
    }
    protected String function(float a, float b){
        float resul=0;
        resul=a*b;
        return "el resultado es"+" "+resul;
    }

    public void onSalir(View view){
        AlertDialog.Builder alerta=new AlertDialog.Builder(this);
        alerta.setMessage("SEGURO DESEA SALIR DE LA APLICACION ?");
        alerta.setTitle("FINALIZAR APLICACION");
        alerta.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
             //   finish(); System.exit(0);
                Intent intent=new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        alerta.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
            }
        });
        AlertDialog dialog=alerta.create();
        dialog.show();

    }
    public void irCargaArchivo(View view){
      //  Button btnCarga=(Button)findViewById(R.id.validate_map);
        Intent ir=new Intent(MainActivity.this,ActivityCargaArchivo.class);
        startActivity(ir);

     /*   btnCarga.setOnClickListener(new View.OnClickListener() {

           @Override
             public void onClick(View v) {
               Intent ir=new Intent(MainActivity.this,ActivityCargaArchivo.class);
                startActivity(ir);
             }
        });*/
    }
    public void consultarTabla(View view){
        Intent irr=new Intent(MainActivity.this,ListaProductos.class);
        startActivity(irr);
    }
}
