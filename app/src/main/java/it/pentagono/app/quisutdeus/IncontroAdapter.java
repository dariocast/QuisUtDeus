package it.pentagono.app.quisutdeus;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class IncontroAdapter extends ArrayAdapter<Incontro> {

    Context context;
    int layoutResourceId;
    Incontro data[] = null;

    public IncontroAdapter(Context context, int layoutResourceId, Incontro[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        IncontroHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new IncontroHolder();
            holder.img_incontro = (ImageView) row.findViewById(R.id.img_incontro);
            holder.tv_titolo = (TextView) row.findViewById(R.id.tv_titolo);
            holder.tv_data = (TextView) row.findViewById(R.id.tv_data);
            holder.tv_luogo = (TextView) row.findViewById(R.id.tv_luogo);
            holder.tv_occasione = (TextView) row.findViewById(R.id.tv_occasione);


            row.setTag(holder);
        }
        else
        {
            holder = (IncontroHolder)row.getTag();
        }

        Incontro Incontro = data[position];
        holder.img_incontro.setImageResource(Incontro.momento);
        holder.tv_titolo.setText(Incontro.titolo);
        holder.tv_data.setText(Incontro.data);
        holder.tv_luogo.setText(Incontro.luogo);
        holder.tv_occasione.setText(Incontro.occasione);

        return row;
    }

    static class IncontroHolder
    {
        ImageView img_incontro;
        TextView tv_titolo;
        TextView tv_data;
        TextView tv_luogo;
        TextView tv_occasione;
    }
}