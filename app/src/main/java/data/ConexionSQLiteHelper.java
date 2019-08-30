package data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteAbortException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;

import entidades.Producto;
import variables.Utilidades;

import static variables.Utilidades.DATABASE_VERSION;

public class ConexionSQLiteHelper extends SQLiteOpenHelper {
    public ConexionSQLiteHelper(Context context) {

        super(context,Utilidades.DATABASE_NAME,null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Utilidades.CREAR_TABLA_PRODUCTO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      db.execSQL("DROP TABLE IF EXISTS "+Utilidades.C_TABLA);
      db.execSQL(Utilidades.CREAR_TABLA_PRODUCTO);

    }
    public void deleteDatos(){
        String[] arg={""};
        try {
            SQLiteDatabase db=getWritableDatabase();
            db.execSQL("DELETE FROM "+Utilidades.C_TABLA);
        }catch (SQLException e){

        }
    }
    public void agregarProducto(Producto producto) throws SQLiteAbortException {
           SQLiteDatabase db=getWritableDatabase(); //Hago writable la base de datos
           ContentValues contValues=new ContentValues();
           contValues.put(Utilidades.C_ID,producto.getId());
           contValues.put(Utilidades.C_CODIGO,producto.getCodigo());
           contValues.put(Utilidades.C_DESCRIPCION,producto.getDescripcion());
           contValues.put(Utilidades.C_CODBARRA,producto.getCodigoBarra());
           contValues.put(Utilidades.C_PRECIO,producto.getPrecio());
           contValues.put(Utilidades.C_CANTIDAD,producto.getCantidad());
       /* db.execSQL(Utilidades.INSERTAR_TABLA_PRODUCTO+"("+producto.getId()+","+producto.getCodigo()+","+
                producto.getDescripcion()+","+producto.getCodigoBarra()+","+producto.getPrecio()+","+producto.getCantidad()+")");*/
           long id= db.insert(Utilidades.C_TABLA,null,contValues);//long id es porque esa funcion devuelve un long
    }

    public Producto traerProducto(Integer id){
        Producto producto=new Producto();
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor= db.rawQuery("Select * from producto where id="+id,null);
        while(cursor.moveToNext()) {
            producto.setId(cursor.getInt(0));
            producto.setCodigo(cursor.getString(1));
            producto.setDescripcion(cursor.getString(2));
            producto.setCodigoBarra(cursor.getString(3));
            producto.setPrecio(cursor.getFloat(4));
            producto.setCantidad(cursor.getFloat(5));
        }
            CerrarConexion(db);
        return producto;
    }
    public ArrayList<Producto> consultarTodosProductos() {
        ArrayList<Producto> listaProductos=new ArrayList<Producto>();
        SQLiteDatabase db=getReadableDatabase();
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
        }
        return listaProductos;
    }
    public ArrayList<Producto> traerListaProducto(String cLike){

        ArrayList<Producto> listaProductos=new ArrayList<Producto>();
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor= db.rawQuery("Select * from producto where Descripcion like '%"+cLike+"%'"+
               " or Codigo like '%"+cLike+"%' or CodigoBarra like '%"+cLike+"%'",null);
        while(cursor.moveToNext()) {
            Producto producto=new Producto();
            producto.setId(cursor.getInt(0));
            producto.setCodigo(cursor.getString(1));
            producto.setDescripcion(cursor.getString(2));
            producto.setCodigoBarra(cursor.getString(3));
            producto.setPrecio(cursor.getFloat(4));
            producto.setCantidad(cursor.getFloat(5));
            listaProductos.add(producto);
        }
        CerrarConexion(db);
        return listaProductos;
    }
    public boolean agregarCantidad(float vieja, float nueva, Integer idP){
        boolean lOk=false;
        float cantidad=vieja+nueva;
        ContentValues cv=new ContentValues();
        cv.put("Cantidad",cantidad);
        SQLiteDatabase db=getReadableDatabase();
        long id= (long) db.update(Utilidades.C_TABLA,cv,"id="+idP,null);
        CerrarConexion(db);
        return lOk;
    }

    public boolean actualizarProducto(Producto producto){
        boolean lOk=false;
        String strFilter = "id=" + producto.getId();
        ContentValues cv=new ContentValues();
        cv.put(Utilidades.C_CODIGO,producto.getCodigo());
        cv.put(Utilidades.C_DESCRIPCION,producto.getDescripcion());
        cv.put(Utilidades.C_CODBARRA,producto.getCodigoBarra());
        cv.put(Utilidades.C_PRECIO,producto.getPrecio());
        SQLiteDatabase db=getReadableDatabase();
        long id= (long) db.update(Utilidades.C_TABLA,cv,"id=" + producto.getId() ,null);
        CerrarConexion(db);
        return lOk;
    }
    public boolean eliminarProducto(Integer nProd){
        boolean lOk=false;
        String strFilter = "id=" + nProd;
        SQLiteDatabase db=getReadableDatabase();
        String[] args = new String[]{strFilter};
        long id= (long) db.delete(Utilidades.C_TABLA,strFilter,null);
        CerrarConexion(db);
        if(id>0){
            lOk=true;
        }
        return lOk;
    }
    public boolean existeCodigo(String codigo, Integer id){
        Producto producto=new Producto();
        boolean lOk=false;
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor= db.rawQuery("Select * from producto where id!="+id+" and Codigo='"+codigo+"'",null);
        while(cursor.moveToNext()) {
            producto.setId(cursor.getInt(0));
            producto.setCodigo(cursor.getString(1));
            producto.setDescripcion(cursor.getString(2));
            producto.setCodigoBarra(cursor.getString(3));
            producto.setPrecio(cursor.getFloat(4));
            producto.setCantidad(cursor.getFloat(5));
        }
        if(producto.getId()!=id  && (producto.getId()!=0)){
            lOk=true;
        }
        CerrarConexion(db);
        return lOk;
    }

    public Integer contarCantidad(){
        int nCant=0;
        try{
            SQLiteDatabase db=getReadableDatabase();
            Cursor cursor= db.rawQuery("Select count(id) as Total from producto",null);
            while(cursor.moveToNext()) {
                nCant=cursor.getInt(0);
                CerrarConexion(db);
            }
        }catch (SQLException e){
            nCant=0;
        }
        return nCant;
    }
    public boolean existeCodigoBarra(String codigo, Integer id){
        Producto producto=new Producto();
        boolean lOk=false;
        producto.setId(0);
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor= db.rawQuery("Select * from producto where id!="+id+" and CodigoBarra='"+codigo+"'",null);
        while(cursor.moveToNext()) {
            producto.setId(cursor.getInt(0));
            producto.setCodigo(cursor.getString(1));
            producto.setDescripcion(cursor.getString(2));
            producto.setCodigoBarra(cursor.getString(3));
            producto.setPrecio(cursor.getFloat(4));
            producto.setCantidad(cursor.getFloat(5));
        }
        if((producto.getId()!=id) && (producto.getId()!=0)){
            lOk=true;
        }
        CerrarConexion(db);
        return lOk;
    }

    public Integer obtenerProxID(){
        Integer nId=0;
        boolean lOk=false;
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor= db.rawQuery("Select max(id) from producto",null);
        while(cursor.moveToNext()) {
            nId=cursor.getInt(0)+1;
        }
        CerrarConexion(db);
        return nId;
    }
    public void CerrarConexion(SQLiteDatabase db){
        if(db == getWritableDatabase()){
            db.close();
        }

    }
}
