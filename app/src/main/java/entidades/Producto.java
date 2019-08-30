package entidades;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;
@SuppressLint("ParcelCreator")
public class Producto implements Parcelable {
    private int id;
    private String Codigo;
    private String Descripcion;
    private String CodigoBarra;
    private Float Precio;
    private Float Cantidad;

    protected Producto(Parcel in) {
        id = in.readInt();
        Codigo = in.readString();
        Descripcion = in.readString();
        if (in.readByte() == 0) {
            Precio = null;
        } else {
            Precio = in.readFloat();
        }
        if (in.readByte() == 0) {
            Cantidad = null;
        } else {
            Cantidad = in.readFloat();
        }
        CodigoBarra = in.readString();
    }

    public static final Creator<Producto> CREATOR = new Creator<Producto>() {
        @Override
        public Producto createFromParcel(Parcel in) {
            return new Producto(in);
        }

        @Override
        public Producto[] newArray(int size) {
            return new Producto[size];
        }
    };

    public String getCodigoBarra() {
        return CodigoBarra;
    }

    public void setCodigoBarra(String codigoBarra) {
        CodigoBarra = codigoBarra;
    }

    public Producto() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodigo() {
        return Codigo;
    }

    public void setCodigo(String codigo) {
        Codigo = codigo;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }

    public Float getPrecio() {
        return Precio;
    }

    public void setPrecio(Float precio) {
        Precio = precio;
    }

    public Float getCantidad() {
        return Cantidad;
    }

    public void setCantidad(Float cantidad) {
        Cantidad = cantidad;
    }

    @Override
    public String toString() {
        return "Producto{" +
                "id=" + id +
                ", Codigo='" + Codigo + '\'' +
                ", Descripcion='" + Descripcion + '\'' +
                ", CodigoBarra='" + CodigoBarra + '\'' +
                ", Precio=" + Precio +
                ", Cantidad=" + Cantidad +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.Codigo);
        dest.writeString(this.Descripcion);
        dest.writeString(this.CodigoBarra);
        dest.writeFloat(this.Precio);
        dest.writeFloat(this.Cantidad);
    }
}
