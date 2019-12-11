package com.example.enviarcorreocontactos.Model.View;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.enviarcorreocontactos.MainActivity;
import com.example.enviarcorreocontactos.Model.Data.Contacto;
import com.example.enviarcorreocontactos.R;

import java.util.ArrayList;
import java.util.List;

public class ContactoAdapter extends RecyclerView.Adapter <ContactoAdapter.ItemHolder> implements PopupMenu.OnMenuItemClickListener {
    private LayoutInflater inflater;
    private List<Contacto> contactosList = new ArrayList<>();
    private Context miContexto;
    private String direccion;

    public ContactoAdapter(Context context) {
        inflater= LayoutInflater.from(context);
        miContexto = context;
    }

    @NonNull
    @Override
    public ContactoAdapter.ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= inflater.inflate(R.layout.item_contacto,parent,false);
        return new ItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ContactoAdapter.ItemHolder holder, int position) {
        final Contacto contacto = contactosList.get(position);

        if (contacto != null && !contacto.getNombre().equalsIgnoreCase("") &&
                !contacto.getNumero().equalsIgnoreCase("") &&
                !contacto.getEmail().equalsIgnoreCase("")){
            holder.tvNombre.setText(contacto.getNombre());
            holder.tvTelefono.setText(contacto.getEmail().toString());
            holder.tvDireccion.setText(""+contacto.getNumero());
        }

        holder.cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                direccion = contacto.getEmail();
                showPopup(holder.cl);
            }
        });
    }

    @Override
    public int getItemCount() {
        int elements=0;
        if(contactosList !=null){
            elements= contactosList.size();
        }
        return elements;
    }

    public void setData(List<Contacto> contactoList){
        SharedPreferences sharedPreferences = miContexto.getSharedPreferences(MainActivity.TAG, Context.MODE_PRIVATE);
        String nombre = sharedPreferences.getString(MainActivity.NOMBRE, "");
        nombre = nombre.toUpperCase();
        String numero = sharedPreferences.getString(MainActivity.NUMERO, "");
        for (int i = 0; i < contactoList.size(); i++) {
            if(contactoList.get(i).getNombre().toUpperCase().contains(nombre) || contactoList.get(i).getNumero().contains(numero)){
                this.contactosList.add(contactoList.get(i));
            }
        }
        Log.v("------n--", this.contactosList.toString());
        notifyDataSetChanged();
    }

    public List<Contacto> getData(){
        return this.contactosList;
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        private TextView tvNombre, tvTelefono, tvDireccion;
        private ConstraintLayout cl;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre=itemView.findViewById(R.id.tvNombre);
            tvTelefono = itemView.findViewById(R.id.tvTelefono);
            tvDireccion = itemView.findViewById(R.id.tvDireccion);
            cl = itemView.findViewById(R.id.cl);
        }
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(miContexto, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.menu_contacto);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.email:
                mandarCorreo(direccion);
                return true;
            default:
                return true;
        }
    }

    private void mandarCorreo(String direccion) {
        String[] TO = {direccion};
        String[] CC = {""};

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto: "));

        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);

        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "PRUEBA APP ENVIAR CORREO");

        String title = "Mandar este mail con...";

        Intent chooser = Intent.createChooser(emailIntent, title);
        if (emailIntent.resolveActivity(miContexto.getPackageManager()) != null){
            miContexto.startActivity(chooser);
        }
    }

}
