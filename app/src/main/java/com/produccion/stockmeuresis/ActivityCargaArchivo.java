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

        private void CopiarArchivos(String dirArchivo) {
            SQLiteDatabase db=null;
         //   ArrayList<Producto> prod=new ArrayList<Producto>();
            String texto="",subTexto="",id="",codigo="",descripcion="",cBarra="";
            final ConexionSQLiteHelper cnn=new ConexionSQLiteHelper(getApplicationContext());
            Producto producto;
            Integer pos=0,nCont=0,nCant=0;
            float fPrecio;
            nCant=cnn.contarCantidad();
            if(nCant>0){
                cnn.deleteDatos();
            }
        try{
            pos=dirArchivo.indexOf(":");
            File inFile = new File(dirArchivo);//Corto el path para obtener la direccion del archivo
            //BufferedReader permite leer una linea completa del archivo ubicado en inFile
            BufferedReader leer= new BufferedReader(new InputStreamReader(new FileInputStream(inFile)));
            while ((texto = leer.readLine())!=null) {//Recorro el buffer hasta que no queden lineas y guardo en base de datos
                fPrecio=(float)0.00;
                pos=posicionPuntoyComa(texto,";");
                producto = new Producto();
                if(pos>0){
                    nCont=nCont+1;
                  codigo=texto.substring(0,pos);
                  texto=texto.substring(pos+1,texto.length());
                  pos=posicionPuntoyComa(texto,";");
                  descripcion=texto.substring(0,pos);
                  texto=texto.substring(pos+1,texto.length());
                  pos=posicionPuntoyComa(texto,";");
                  if(pos>0){
                      cBarra=texto.substring(0,pos);
                      fPrecio=Float.valueOf(texto.substring(pos+1,texto.length()));

                    }else{
                        cBarra=texto.substring(pos+1,texto.length());
                    }
                  producto.setCodigo(codigo);
                  producto.setDescripcion(descripcion);
                  producto.setCodigoBarra(cBarra);
                  producto.setPrecio(fPrecio);
                  producto.setCantidad((float) 0);
                  producto.setId(nCont);
              //    prod.add(producto);
                 cnn.agregarProducto(producto);
                }
            }cnn.CerrarConexion(db);//Cierro la conexion a la base de datos
            Toast.makeText(this,"Base de Datos Importada con Exito \nCantidad de Registros "+nCont,Toast.LENGTH_SHORT).show();
            Intent ir=new Intent(ActivityCargaArchivo.this,ListaProductos.class);
            startActivity(ir);
        } catch(IOException e) {

                Toast.makeText(this,e.getMessage()+" Permisos de lectura/escritura denegados. Verificar !!!",Toast.LENGTH_LONG).show();
            Intent ir=new Intent(ActivityCargaArchivo.this,MainActivity.class);
            startActivity(ir);

            }
        }
        private int posicionPuntoyComa(String Linea, String caracter){
        int pos=0;
          pos=Linea.indexOf(caracter);
        return pos;
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
