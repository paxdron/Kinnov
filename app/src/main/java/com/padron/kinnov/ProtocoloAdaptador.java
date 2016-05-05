package com.padron.kinnov;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by antonio on 24/04/16.
 */
public class ProtocoloAdaptador extends RecyclerView.Adapter<ProtocoloAdaptador.ProtocoloViewHolder>{
    List<Protocolo>items;


    public ProtocoloAdaptador(List<Protocolo>items) {
        this.items=items;

    }

    @Override
    public ProtocoloViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_menu, parent, false);
        return new ProtocoloViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ProtocoloViewHolder holder, int position) {
        holder.titulo.setText(items.get(position).getTitulo());
        holder.descripcion.setText(items.get(position).getDescripcion());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ProtocoloViewHolder extends RecyclerView.ViewHolder {
        // Campos respectivos de un item
        public TextView titulo;
        public TextView descripcion;

        public ProtocoloViewHolder(View v) {
            super(v);
            titulo = (TextView) v.findViewById(R.id.titulo);
            descripcion = (TextView) v.findViewById(R.id.descripcion);
        }
    }

}
