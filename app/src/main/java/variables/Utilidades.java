package variables;

public class Utilidades {
    public static final String DATABASE_NAME = "Listado.db";
    public static final String C_TABLA = "producto";
    public static final String C_ID = "id";
    public static final String C_CODIGO = "Codigo";
    public static final String C_CODBARRA = "CodigoBarra";
    public static final String C_DESCRIPCION = "Descripcion";
    public static final String C_PRECIO = "Precio";
    public static final String C_CANTIDAD = "Cantidad";
    public static final String CREAR_TABLA_PRODUCTO = "Create table producto (id INTEGER PRIMARY KEY,Codigo TEXT,Descripcion TEXT,CodigoBarra TEXT,Precio REAL DEFAULT 0, Cantidad REAL DEFAULT 0)";
    public static final String INSERTAR_TABLA_PRODUCTO = "INSERT INTO producto (id,Codigo,Descripcion,CodigoBarra,Precio,Cantidad) VALUES ";
    public static final int DATABASE_VERSION = 1;
}
