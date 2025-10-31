package com.example.aplicacionpartes;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    // Nombre y versión de la base de datos
    private static final String DATABASE_NAME = "partes.db";
    private static final int DATABASE_VERSION = 2;

    // Constructor
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Crea la tabla de partes
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE partes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombres TEXT," +
                "apellidos TEXT," +
                "rut TEXT," +
                "edad INTEGER," +
                "tipo_auto TEXT," +
                "marca TEXT," +
                "modelo TEXT," +
                "patente TEXT," +
                "causal TEXT," +
                "foto_path TEXT," +
                "nombre_funcionario TEXT," +
                "numero_placa TEXT," +
                "departamento TEXT," +
                "lugar_pago TEXT)";
        db.execSQL(CREATE_TABLE);
    }

    // Actualiza la base si cambia la versión
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS partes");
        onCreate(db);
    }

    // Inserta un nuevo parte
    public long insertarParte(ContentValues values) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.insert("partes", null, values);
    }

    // Actualiza un parte existente por ID
    public boolean updateParte(int id, ContentValues values) {
        SQLiteDatabase db = this.getWritableDatabase();
        int filas = db.update("partes", values, "id = ?", new String[]{String.valueOf(id)});
        return filas > 0;
    }

    // Obtiene todos los partes (para edición)
    public List<Parte> obtenerPartes() {
        List<Parte> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM partes ORDER BY id DESC", null);

        if (cursor.moveToFirst()) {
            do {
                Parte parte = construirParteDesdeCursor(cursor);
                lista.add(parte);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return lista;
    }

    // Obtiene un parte por ID
    public Parte obtenerPartePorId(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Consulta segura con parámetro
        Cursor cursor = db.rawQuery("SELECT * FROM partes WHERE id = ?", new String[]{String.valueOf(id)});
        Parte parte = null;

        if (cursor != null && cursor.moveToFirst()) {
            parte = new Parte();

            // Validación defensiva por si alguna columna no existe
            try {
                parte.id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                parte.nombres = cursor.getString(cursor.getColumnIndexOrThrow("nombres"));
                parte.apellidos = cursor.getString(cursor.getColumnIndexOrThrow("apellidos"));
                parte.rut = cursor.getString(cursor.getColumnIndexOrThrow("rut"));
                parte.edad = cursor.getInt(cursor.getColumnIndexOrThrow("edad"));
                parte.tipo_auto = cursor.getString(cursor.getColumnIndexOrThrow("tipo_auto"));
                parte.marca = cursor.getString(cursor.getColumnIndexOrThrow("marca"));
                parte.modelo = cursor.getString(cursor.getColumnIndexOrThrow("modelo"));
                parte.patente = cursor.getString(cursor.getColumnIndexOrThrow("patente"));
                parte.causal = cursor.getString(cursor.getColumnIndexOrThrow("causal"));
                parte.nombre_funcionario = cursor.getString(cursor.getColumnIndexOrThrow("nombre_funcionario"));
                parte.numero_placa = cursor.getString(cursor.getColumnIndexOrThrow("numero_placa"));
                parte.departamento = cursor.getString(cursor.getColumnIndexOrThrow("departamento"));
                parte.lugar_pago = cursor.getString(cursor.getColumnIndexOrThrow("lugar_pago"));
            } catch (Exception e) {
                e.printStackTrace(); // Para ver el error en Logcat
                parte = null; // Si falla, devolver null para evitar crash
            }
        }

        if (cursor != null) cursor.close();
        return parte;
    }

    // Elimina un parte por su ID
    public boolean borrarPartePorId(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int filas = db.delete("partes", "id = ?", new String[]{String.valueOf(id)});
        return filas > 0;
    }

    // Construye un objeto Parte desde un cursor
    private Parte construirParteDesdeCursor(Cursor cursor) {
        Parte parte = new Parte();
        parte.id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
        parte.nombres = cursor.getString(cursor.getColumnIndexOrThrow("nombres"));
        parte.apellidos = cursor.getString(cursor.getColumnIndexOrThrow("apellidos"));
        parte.rut = cursor.getString(cursor.getColumnIndexOrThrow("rut"));
        parte.edad = cursor.getInt(cursor.getColumnIndexOrThrow("edad"));
        parte.tipo_auto = cursor.getString(cursor.getColumnIndexOrThrow("tipo_auto"));
        parte.marca = cursor.getString(cursor.getColumnIndexOrThrow("marca"));
        parte.modelo = cursor.getString(cursor.getColumnIndexOrThrow("modelo"));
        parte.patente = cursor.getString(cursor.getColumnIndexOrThrow("patente"));
        parte.causal = cursor.getString(cursor.getColumnIndexOrThrow("causal"));
        parte.foto_path = cursor.getString(cursor.getColumnIndexOrThrow("foto_path"));
        parte.nombre_funcionario = cursor.getString(cursor.getColumnIndexOrThrow("nombre_funcionario"));
        parte.numero_placa = cursor.getString(cursor.getColumnIndexOrThrow("numero_placa"));
        parte.departamento = cursor.getString(cursor.getColumnIndexOrThrow("departamento"));
        parte.lugar_pago = cursor.getString(cursor.getColumnIndexOrThrow("lugar_pago"));
        return parte;
    }
}