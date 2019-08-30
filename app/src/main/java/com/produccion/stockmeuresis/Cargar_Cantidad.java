package com.produccion.stockmeuresis;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import data.ConexionSQLiteHelper;
import entidades.Producto;

public class Cargar_Cantidad extends AppCompatActivity {
    EditText txCantidad;
    TextView txD,txCodB,txActual;
    Producto producto;
    ConexionSQLiteHelper cnn;
   // Producto producto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cargar__cantidad);
        setActionBar();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        txCodB=(TextView)findViewById(R.id.txCod_B);
        txD=(TextView)findViewById(R.id.txD);
        txActual=(TextView)findViewById(R.id.txActual);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("LISTADO PRODUCTOS");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        cnn=new ConexionSQLiteHelper(getApplicationContext());
      Integer productoID=getIntent().getExtras().getInt("productoID");
          producto=cnn.traerProducto(productoID);
      txActual.setText("PRECIO : "+producto.getPrecio());
       txCodB.setText(producto.getCodigo()+" / "+producto.getCodigoBarra());
       txD.setText(producto.getDescripcion());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate muestra los items que estan en el menu
        getMenuInflater().inflate(R.menu.bottom_nav_menu, menu);
        return true;
    }
    @Override public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item= menu.findItem(R.id.navigation_dashboard);//esto oculta los items que no voy a usar
        MenuItem item1= menu.findItem(R.id.navigation_home);
        MenuItem item2= menu.findItem(R.id.navigation_notifications);
        MenuItem item3= menu.findItem(R.id.agregar_prod);
        MenuItem item4=menu.findItem(R.id.exportar_prod);
        MenuItem item5=menu.findItem(R.id.eliminar_prod);
        item.setVisible(false);
        item1.setVisible(false);
        item2.setVisible(false);
        item3.setVisible(false);
        item4.setVisible(false);
        super.onPrepareOptionsMenu(menu);
        return true; }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {// aca capturo el click sobre el item
        // Handle item selection
        switch (item.getItemId()) { //capturo uno solo ya que los otro los mantengo ocultos
            case R.id.editar_prod:
                Intent ir=new Intent(Cargar_Cantidad.this,Producto_Nuevo.class);
                ir.putExtra("productoID",Integer.toString(producto.getId()));
                startActivity(ir);

                return true;
            case R.id.eliminar_prod:
                 ////////////////////////////////////////////////////////////////////////////
                AlertDialog.Builder alerta=new AlertDialog.Builder(this);
                alerta.setMessage("DESEA ELIMINAR DE FORMA DEFINITIVA ESTE PRODUCTO ?");
                alerta.setTitle("ELIMINAR PRODUCTO");
                alerta.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        eliminarProducto();
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
                /////////////////////////////////////////////////////////////////////////////
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void eliminarProducto(){
       if(cnn.eliminarProducto(producto.getId())){
           Toast.makeText(this,"PRODUCTO SE ELIMINO CON EXITO ",Toast.LENGTH_SHORT).show();
           Intent ir=new Intent(Cargar_Cantidad.this,ListaProductos.class);
           ir.putExtra("productoID",producto.getId());
           startActivity(ir);
           finish();
       }else{
           Toast.makeText(this,"ERROR AL ELIMINAR PRODUCTO. VERIFIQUE ",Toast.LENGTH_SHORT).show();
       }

    }
    public void cargarCantidad(View view){
       if(txCantidad.getText().toString()!=""){
          try {
          float fCant=Float.valueOf(txCantidad.getText().toString());
          if(fCant!=0){
              try {
                  cnn.agregarCantidad(producto.getCantidad(),fCant,producto.getId());
                  Intent ir=new Intent(Cargar_Cantidad.this,ListaProductos.class);
                  ir.putExtra("productoID",producto.getId());
                  startActivity(ir);
              }catch (InternalError e){
                  Toast.makeText(this, "ERROR AL INGRESAR LA CANTIDAD", Toast.LENGTH_SHORT).show();
              }

          }
          }catch (InternalError e){
              Toast.makeText(this, "LA CANTIDAD NO ES UN VALOR", Toast.LENGTH_SHORT).show();
          }
       }
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
            case "-":
                if(siz>1){
                    lOk=false;
                }else{
                    lOk=true;
                }

                break;
        }

        return lOk;
    }

    protected void setActionBar(){
        ActionBar actBar=getSupportActionBar();
        if (actBar != null){
            actBar.setDisplayHomeAsUpEnabled(true);
            actBar.setTitle("LISTADO");
        }

    }

}
