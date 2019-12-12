package com.example.enviarcorreocontactos;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.enviarcorreocontactos.Model.Data.Contacto;
import com.example.enviarcorreocontactos.Model.View.ContactoAdapter;

import java.util.ArrayList;
import java.util.HashSet;

public class Contactos extends AppCompatActivity {

    private ContactoAdapter contactoAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerContactos;
    protected final int SOLICITUD_PERMISO_CONTACTOS=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactos);
        init();
    }

    private void init() {
        recyclerContactos = findViewById(R.id.rvContactos);
        layoutManager = new GridLayoutManager(this, 2);
        contactoAdapter = new ContactoAdapter(this);

        recyclerContactos.setLayoutManager(layoutManager);
        recyclerContactos.setAdapter(contactoAdapter);

        checkPermissions(Manifest.permission.READ_CONTACTS,
                R.string.tituloExplicacion, R.string.mensajeExplicacion);
    }

    private void checkPermissions(String permiso, int titulo, int mensaje) {
        if (ContextCompat.checkSelfPermission(this, permiso)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permiso)) {
                explain(R.string.tituloExplicacion, R.string.mensajeExplicacion, permiso);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{permiso},
                        SOLICITUD_PERMISO_CONTACTOS);
            }
        } else {
            contactoAdapter.setData(MostrarAgenda(Contactos.this));
        }
    }

    private void explain(int title, int message, final String permissions) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.respSi, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                ActivityCompat.requestPermissions(Contactos.this, new String[]{permissions}, SOLICITUD_PERMISO_CONTACTOS);
            }
        });
        builder.setNegativeButton(R.string.respNo, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case SOLICITUD_PERMISO_CONTACTOS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    contactoAdapter.setData(MostrarAgenda(Contactos.this));
                } else {
                    Toast.makeText(this, R.string.noConcedido, Toast.LENGTH_LONG);
                    finish();
                }
                return;
            }
        }
    }

    public ArrayList<Contacto> MostrarAgenda(Context context){
        ArrayList<Contacto> contactosList = new ArrayList<>();
        ArrayList<Contacto> contactosFinal = new ArrayList<>();

        ContentResolver cr = context.getContentResolver();

        String[] PROJECTION = new String[] {
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.HAS_PHONE_NUMBER};

        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, PROJECTION, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {

            do {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                Integer hasPhone = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                String email = null;
                Cursor ce = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{id}, null);
                if (ce != null && ce.moveToFirst()) {
                    email = ce.getString(ce.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    ce.close();
                }

                String phone = null;
                if (hasPhone > 0) {
                    Cursor cp = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                    if (cp != null && cp.moveToFirst()) {
                        phone = cp.getString(cp.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        cp.close();
                    }
                }

                if ((!TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
                        && !email.equalsIgnoreCase(name)) || (!TextUtils.isEmpty(phone))) {
                    Contacto contacto = new Contacto();
                    contacto.setNombre(""+name);
                    contacto.setNumero(""+phone);
                    contacto.setEmail(""+email);
                    contactosList.add(contacto);
                }

            } while (cursor.moveToNext());

            cursor.close();
        }

        for (int i = 0; i < contactosList.size(); i++) {
            /*if(!contactosList.get(i).getEmail().equalsIgnoreCase("null")
                    && !contactosList.get(i).getNumero().equalsIgnoreCase("null")
                    && !contactosList.get(i).getNombre().equalsIgnoreCase("null")){
                contactosFinal.add(contactosList.get(i));
            }*/
            if(!contactosList.get(i).getNumero().equalsIgnoreCase("null")
                    && !contactosList.get(i).getNombre().equalsIgnoreCase("null")){
                contactosFinal.add(contactosList.get(i));
            }
        }
        return contactosFinal;
    }
}
