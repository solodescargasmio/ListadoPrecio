package entidades;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.produccion.stockmeuresis.R;

import java.util.ArrayList;

public class ItemProductosAdapter extends BaseAdapter{
    protected Activity activity;
    protected ArrayList<Producto> items;
    public ItemProductosAdapter(Activity activity, ArrayList<Producto> items) {
        this.activity = activity;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            vi = inflater.inflate(R.layout.activity_list__prod, null);
        }

        Producto item = items.get(position);


        TextView cod_barra = (TextView) vi.findViewById(R.id.txCod_Barra);
        cod_barra.setText(item.getCodigo()+"/"+item.getCodigoBarra());

        TextView descripcion = (TextView) vi.findViewById(R.id.txDescripcion);
        descripcion.setText(item.getDescripcion());

        TextView precio = (TextView) vi.findViewById(R.id.txPrecio);
        precio.setText("$ "+item.getPrecio().toString());

     /*   TextView cantidad = (TextView) vi.findViewById(R.id.txCantidad);
        cantidad.setText(item.getCantidad().toString());*/

        return vi;
    }
}
