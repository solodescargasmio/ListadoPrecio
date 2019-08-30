package com.produccion.stockmeuresis;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import data.ConexionSQLiteHelper;
import entidades.ItemProductosAdapter;
import entidades.Producto;

public class ListaProductos extends AppCompatActivity {
     ConexionSQLiteHelper cnn;
     ArrayList<String> listaInfo;
     ArrayList<Producto> listaProductos;
     ListView listaProd;
    ItemProductosAdapter adapter;
    EditText edCBusqueda;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_productos);
        setActionBar();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        listaProd=(ListView) findViewById(R.id.listaProd);
        cnn=new ConexionSQLiteHelper(getApplicationContext());
        consultarProductos();
       /* ArrayList<ItemCompra> itemsCompra = obtenerItems();*/

        adapter = new ItemProductosAdapter(this, listaProductos);
        listaProd.setAdapter(adapter);
        listaProd.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

          Intent ir = new Intent(ListaProductos.this,Cargar_Cantidad.class);
          ir.putExtra("productoID",listaProductos.get(position).getId());
          startActivity(ir);
            }
        });
        if(getIntent().getStringExtra("productoID")!=null){
            Integer productoID=0;
            productoID=getIntent().getExtras().getInt("productoID");
            if(productoID!=0){
                listaProd.setSelection(productoID);
            }
        }
       edCBusqueda=(EditText)findViewById(R.id.edCampoBusqueda);
        edCBusqueda.addTextChangedListener(new TextWatcher(){

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
             consultaLike(s.toString());

            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.bottom_nav_menu, menu);
        return true;
    }
    @Override public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item= menu.findItem(R.id.navigation_dashboard);
        MenuItem item1= menu.findItem(R.id.navigation_home);
        MenuItem item2= menu.findItem(R.id.navigation_notifications);
        MenuItem item3= menu.findItem(R.id.editar_prod);
        MenuItem item4= menu.findItem(R.id.eliminar_prod);
        item.setVisible(false);
        item1.setVisible(false);
        item2.setVisible(false);
        item3.setVisible(false);
        item4.setVisible(false);
        super.onPrepareOptionsMenu(menu);
        return true; }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.agregar_prod:
                Intent ir=new Intent(ListaProductos.this,Producto_Nuevo.class);
                startActivity(ir);
                return true;
            case R.id.exportar_prod:
                exportarCVS();
                return true;   
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void exportarCVS() {
       HabPanel(false);
      if(writeFile()){
          Toast.makeText(this,"SE EXPORTO CON EXITO",Toast.LENGTH_LONG).show();
      }else{
          Toast.makeText(this,"NO SE EXPORTO EL LISTADO. VERIFIQUE",Toast.LENGTH_LONG).show();
      }
       HabPanel(true);
    }
    private void HabPanel(boolean lOk){
        if(!lOk){
          //  progressBar.setVisibility(View.VISIBLE);
        }else{
        //    progressBar.setVisibility(View.INVISIBLE);
        }
        edCBusqueda.setEnabled(lOk);
        listaProd.setEnabled(lOk);

    }

    protected void setActionBar(){
        ActionBar actBar=getSupportActionBar();
        if (actBar != null){
            actBar.setTitle("Inicio");
        }

    }

    private void consultarProductos() {
      /*  SQLiteDatabase db=cnn.getReadableDatabase();
        listaProductos=new ArrayList<Producto>();
        Producto producto=null;
        Cursor cursor= db.rawQuery("Select * from producto",null);
        while(cursor.moveToNext()){
            producto=new Producto();
            producto.setId(cursor.getInt(0));
            producto.setCodigo(cursor.getString(1));
            producto.setDescripcion(cursor.getString(2));
            producto.setCodigoBarra(cursor.getString(3));
            producto.setPrecio(cursor.getFloat(4));
            producto.setCantidad(cursor.getFloat(5));
            listaProductos.add(producto);
        }*/
        listaProductos=new ArrayList<Producto>();
        listaProductos=cnn.consultarTodosProductos();
    }
    public void consultaLike(String cValor){
        listaProductos.clear();
        listaProductos= cnn.traerListaProducto(cValor);
        adapter = new ItemProductosAdapter(this, listaProductos);
        listaProd.setAdapter(adapter);
    }

    private void llenarLista() {
        listaInfo=new ArrayList<String>();
        Float cPrecio= Float.valueOf(0);
        for (int i=0;i<listaProductos.size();i++){
            cPrecio=listaProductos.get(i).getPrecio();
            if(cPrecio!=null){cPrecio=Float.valueOf(0);}
            listaInfo.add(listaProductos.get(i).getCodigo()+
                    "   "+listaProductos.get(i).getCodigoBarra()+"   "+listaProductos.get(i).getDescripcion()+
                    "   "+cPrecio+"   "+listaProductos.get(i).getCantidad());
        }
    }
    private boolean writeFile() {
        String filename="Stock";
        boolean lOk=false;
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy");
        String formattedDate = df.format(c.getTime());
        Context context=getApplicationContext();
            try{
                File file = new File(Environment.getExternalStorageDirectory(), filename+formattedDate+".csv");
                OutputStreamWriter salida=new OutputStreamWriter(new FileOutputStream(file));
                Float cPrecio= Float.valueOf(0);
                for (int i=0;i<listaProductos.size();i++){
                    cPrecio=listaProductos.get(i).getPrecio();
                    if(cPrecio!=null){cPrecio=Float.valueOf(0);}
                    salida.write(listaProductos.get(i).getCodigo()+
                            ";"+listaProductos.get(i).getCodigoBarra()+";"+listaProductos.get(i).getDescripcion()+
                            ";"+cPrecio+";"+listaProductos.get(i).getCantidad()+"\n");
                }
                salida.flush();
                salida.close();
                lOk=true;
            }catch (IOException e){Toast.makeText(this,"ERROR "+e.getMessage(),Toast.LENGTH_LONG).show();}
        return lOk;

    }
    public static boolean isSDCardAvailable(Context context) {
        File[] storages = ContextCompat.getExternalFilesDirs(context, null);
        if (storages.length > 1 && storages[0] != null && storages[1] != null)
            return true;
        else
            return false;
    }
}
