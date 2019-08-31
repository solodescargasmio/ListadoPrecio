package com.produccion.stockmeuresis;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import data.ConexionSQLiteHelper;
import entidades.Producto;

public class CsvASqlite extends AppCompatActivity {
     String path;
     Integer nCont;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_csv_asqlite);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        path=getIntent().getExtras().getString("path");
        Carga carga=new Carga();
        carga.execute();
    }

    public class Carga extends AsyncTask<Void,Integer,Boolean>{

        @Override
        protected Boolean doInBackground(Void... voids) {
            CopiarArchivos(path);
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            Finalizado();
        }
    }


    private void CopiarArchivos(String dirArchivo) {
        nCont=0;
        SQLiteDatabase db=null;
        //   ArrayList<Producto> prod=new ArrayList<Producto>();
        String texto="",subTexto="",id="",codigo="",descripcion="",cBarra="";
        final ConexionSQLiteHelper cnn=new ConexionSQLiteHelper(getApplicationContext());
        Producto producto;
        Integer pos=0,nCant=0;
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
         /*   BufferedReader contar= new BufferedReader(new InputStreamReader(new FileInputStream(inFile)));
            while ((texto = contar.readLine())!=null) {
                nCont=nCont+1;
            }
            Toast.makeText(this,"CANTIDAD : "+nCont,Toast.LENGTH_LONG).show();*/
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
        } catch(IOException e) {

            Toast.makeText(this,e.getMessage()+" Permisos de lectura/escritura denegados. Verificar !!!",Toast.LENGTH_LONG).show();
            Intent ir=new Intent(CsvASqlite.this,MainActivity.class);
            startActivity(ir);

        }
    }
    private int posicionPuntoyComa(String Linea, String caracter){
        int pos=0;
        pos=Linea.indexOf(caracter);
        return pos;
    }
    public void Finalizado(){
        Toast.makeText(this,"Base de Datos Importada con Exito \nCantidad de Registros "+nCont,Toast.LENGTH_SHORT).show();
        Intent ir=new Intent(CsvASqlite.this,ListaProductos.class);
        startActivity(ir);
    }
}
