package com.example.enviarcorreocontactos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private EditText etNombre, etNumero;
    private Button btBuscar;
    public static final String NOMBRE = "nombreContacto";
    public static final String NUMERO = "numeroContacto";
    public static final String TAG = "enviarCorreoContactos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        etNombre = findViewById(R.id.etNombre);
        etNumero = findViewById(R.id.etNumero);
        btBuscar = findViewById(R.id.btBuscar);
        btBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarInfo();
                lanzarRecycler();
            }
        });
    }

    private void guardarInfo() {
        SharedPreferences sharedPreferences = this.getSharedPreferences(TAG, MainActivity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(NOMBRE, etNombre.getText().toString());
        editor.putString(NUMERO, etNumero.getText().toString());
        editor.commit();
    }

    private void lanzarRecycler() {
        Intent intent = new Intent(MainActivity.this, Contactos.class);
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(NOMBRE,etNombre.getText().toString());
        outState.putString(NUMERO,etNumero.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        etNombre.setText(savedInstanceState.getString(NOMBRE));
        etNumero.setText(savedInstanceState.getString(NUMERO));
    }
}
