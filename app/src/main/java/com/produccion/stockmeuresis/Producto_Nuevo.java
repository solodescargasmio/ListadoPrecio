package com.produccion.stockmeuresis;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import data.ConexionSQLiteHelper;
import entidades.Producto;

import static android.view.View.INVISIBLE;

public class Producto_Nuevo extends AppCompatActivity {
     EditText Codigo,CodBarra,Desc,Prec,Cant;
     Button btnGuardar;
     ConexionSQLiteHelper cnn;
    Producto producto;
    Integer nId = 0;
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producto__nuevo);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        setActionBar();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       // getSupportActionBar().setIcon(R.mipmap.ic_launcher_foreground);
        Codigo=(EditText)findViewById(R.id.edCodN);
        CodBarra=(EditText)findViewById(R.id.edCodBaN);
        Desc=(EditText)findViewById(R.id.edDescN);
        Prec=(EditText)findViewById(R.id.edPreN);
        btnGuardar=(Button)findViewById(R.id.btnGuardar);
        cnn=new ConexionSQLiteHelper(getApplicationContext());

        if(getIntent().getStringExtra("productoID")!=null){
            nId= Integer.parseInt(getIntent().getExtras().getString("productoID"));
            if(nId!=0){
                producto=new Producto();
                producto=cnn.traerProducto(nId);
                Codigo.setText(producto.getCodigo());
                CodBarra.setText(producto.getCodigoBarra());
                Prec.setText(String.valueOf(producto.getPrecio()));
                Desc.setText(producto.getDescripcion());
            }
        }else
        { nId=cnn.obtenerProxID();
            Prec.setVisibility(View.VISIBLE);
        }
        Codigo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length()>0){
                    if(existeDato("C",s.toString(),nId)){
                        Codigo.setError("CODIGO YA EXISTE EN BASE DE DATOS");
                    }else{
                        Codigo.setError(null);
                    }
                }

            }
        });

        CodBarra.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length()>0){
                    if(existeDato("B",s.toString(),nId)){
                        CodBarra.setError("CODIGO DE BARRA YA EXISTE EN BASE DE DATOS");
                    }else{
                        CodBarra.setError(null);
                    }
                }

            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(producto==null) {
                    guardarProdNuevo();
                }else{
                  modificarProducto();
                }
            }
        });
    }
    public void modificarProducto(){
        guardarProdNuevo();
    }
    protected void setActionBar(){
        ActionBar actBar=getSupportActionBar();
        if (actBar != null){
            actBar.setTitle("Listado Producto");
        }

    }

    public void guardarProdNuevo(){
        boolean lOk=true;
        float precio=(float)0.0,cantidad= (float) 0.0;
        String cPrec="",cCant="";
        cPrec=Prec.getText().toString();
        if(!cPrec.equals("")){
            precio=Float.valueOf(cPrec);}
        if(Codigo.getText().toString().equals("")){
            lOk=false;
            Toast.makeText(this,"INGRESE UN CODIGO PARA EL PRODUCTO",Toast.LENGTH_SHORT).show();
            Codigo.requestFocus();
        }
        if((lOk) && (CodBarra.getText().toString().equals(""))){
            lOk=false;
            Toast.makeText(this,"INGRESE UN CODIGO DE BARRA PARA EL PRODUCTO",Toast.LENGTH_SHORT).show();
            CodBarra.requestFocus();
        }
        if((lOk) && (Desc.getText().toString().equals(""))){
            lOk=false;
            Toast.makeText(this,"INGRESE UNA DESCRIPCION PARA EL PRODUCTO",Toast.LENGTH_SHORT).show();
            Desc.requestFocus();
        }

        if(lOk){
            if(producto==null){
                producto=new Producto();
                producto.setId(cnn.obtenerProxID());
                producto.setCodigo(Codigo.getText().toString());
                producto.setCodigoBarra(CodBarra.getText().toString());
                producto.setDescripcion(Desc.getText().toString());
                producto.setCantidad(cantidad);
                producto.setPrecio(precio);
                if(guardarProducto(producto)){
                  lOk=true;
                }
            }else {
                producto.setCodigo(Codigo.getText().toString());
                producto.setCodigoBarra(CodBarra.getText().toString());
                producto.setDescripcion(Desc.getText().toString());
                producto.setPrecio(precio);
                if(cnn.actualizarProducto(producto)){
                lOk=true;
              }
            }
            if(lOk){
                Toast.makeText(this,"PRODUCTO SE GUARDO CON EXITO",Toast.LENGTH_SHORT).show();
                Intent ir=new Intent(Producto_Nuevo.this,ListaProductos.class);
                startActivity(ir);
            }else{
                Toast.makeText(this,"ERROR AL GUARDAR PRODUCTO",Toast.LENGTH_SHORT).show();
            }

        }

    }
    public boolean guardarProducto(Producto producto){
       boolean lOk=true;
       if(existeDato("C",producto.getCodigo(),producto.getId())){
           lOk=false;
           Toast.makeText(this,"YA EXISTE CODIGO",Toast.LENGTH_SHORT).show();
           Codigo.requestFocus();
       }
       if(lOk) {
           if(existeDato("B",producto.getCodigoBarra(),producto.getId())){
               lOk=false;
               Toast.makeText(this,"YA EXISTE CODIGOBARRA",Toast.LENGTH_SHORT).show();
               CodBarra.requestFocus();
           }
       }
        if(lOk) {
            if(producto.getId()==0){
                lOk=false;
                Toast.makeText(this,"ERROR AL NUMERAR PRODUCTO",Toast.LENGTH_SHORT).show();
                Codigo.requestFocus();
            }
        }
        if(lOk){
            cnn.agregarProducto(producto);
        }
       return lOk;
    }
    public boolean existeDato(String Cod,String valor,Integer id){
        boolean lOk=false;
        if(Cod.equals("B")){
            lOk=cnn.existeCodigoBarra(valor,id);
        }else {
            lOk=cnn.existeCodigo(valor,id);
        }
        return lOk;
    }
    public boolean caracterCorrecto(String valor){
        boolean lOk=false;
        Integer siz=valor.length();
        valor=valor.substring(siz-1,siz);
        switch (valor){
            case "0": lOk=true; break;
            case "1": lOk=true; break;
            case "2": lOk=true; break;
            case "3": lOk=true; break;
            case "4": lOk=true; break;
            case "5": lOk=true; break;
            case "6": lOk=true; break;
            case "7": lOk=true; break;
            case "8": lOk=true; break;
            case "9": lOk=true; break;
            case ".": lOk=true; break;
        }

        return lOk;
    }


}
