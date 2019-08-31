package com.produccion.stockmeuresis;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import data.ConexionSQLiteHelper;
import entidades.Producto;

public class ActivityCargaArchivo extends AppCompatActivity {
    private TextView mTextMessage;
    private Button btnCargaCVS;
    private Integer VALOR_RETORNO=1;
    private Integer FILE_SELECT_CODE=1;//Selecciona un solo documento
    ProgressBar progressBar;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carga_archivo);
        setActionBar();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
    protected void setActionBar(){
        ActionBar actBar=getSupportActionBar();
        if (actBar != null){
            actBar.setDisplayHomeAsUpEnabled(true);
            actBar.setTitle("Inicio");
        }

    }

    public void CargaCVS(View view){
      /*  btnCargaCVS=(Button)findViewById(R.id.btnCarga);
        btnCargaCVS.*/
        Intent intent;
       String dato;
       /* if (Build.VERSION.SDK_INT >= 19){// si el SDK es superior a 19 usamos esto sino mas abajo
             intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);//Abre el manejador de archivo instalado en el dispositivo
            intent.setType("text/*");//El tpo myme para buscar, dejo que visualise todas las extencions, podria filtrar peeeero....
            intent.addCategory(Intent.CATEGORY_OPENABLE);//esto es para que permita abrir el archivo
            startActivityForResult(intent, FILE_SELECT_CODE);
        }else{*/
             intent=new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);//}
        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Seleccionar Archivo CVS"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Instalar un Manejador de Archivos.",
                    Toast.LENGTH_SHORT).show();
        }


    }
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {//Esta funcion se dispara sola al invocar el open file

        if (requestCode == FILE_SELECT_CODE) { //La funcion no devuelve el archivo
            Uri uri = null; //devuelve una uri donde extraer el path hacia el archivo
            if (resultData != null) {
                uri = resultData.getData();
                File archivo = new File(uri.getPath());
                String nombre = archivo.getName();
                //Toast.makeText(this,nombre,Toast.LENGTH_LONG).show();
                if(nombre.indexOf(":")>0){//si la uri tienes : es que el manejador agrega "primary : " al nombre
                    nombre=nombre.replace("primary:","");//remplazo esto por espacio vacio
                }
                if(CotrolExtencion(nombre)){//Controlo que sea solo cvs
                    File raiz = Environment.getExternalStorageDirectory();
                    File file = new File(raiz, nombre);
                    String dir= file.getAbsolutePath();

                    Intent ir= new Intent(ActivityCargaArchivo.this,CsvASqlite.class);
                    ir.putExtra("path",dir);
                    startActivity(ir);

                  //  CopiarArchivos(dir); //En esta funcion esta la magia
                }
                else
                {
                    Toast.makeText(this,"EXTENCION NO PERMITIDA, SOLO .CVS",Toast.LENGTH_LONG).show(); //.substring(dirArchivo.length()-13,dirArchivo.length())

                }


            }
        }

    }
        private boolean CotrolExtencion(String path){
        String tramo="csv",exte="";
        boolean lOk=false;
        exte=path.substring(path.length()-3,path.length());
        if(tramo.equalsIgnoreCase(exte)){
           lOk=true;
        }
        return lOk;
        }



}
