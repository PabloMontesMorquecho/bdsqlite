package com.example.bdsqlite

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class MainActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var et1: EditText
    lateinit var et2: EditText
    lateinit var et3: EditText
    lateinit var admin: AdminSQLite
    var antes:Int=-1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        et1 = findViewById<EditText>(R.id.et1)
        et2 = findViewById<EditText>(R.id.et2)
        et3 = findViewById<EditText>(R.id.et3)

        findViewById<Button>(R.id.balta).setOnClickListener(this)
        findViewById<Button>(R.id.bbaja).setOnClickListener(this)
        findViewById<Button>(R.id.bmodificacion).setOnClickListener(this)
        findViewById<Button>(R.id.bCCodigo).setOnClickListener(this)
        findViewById<Button>(R.id.bCDescripcion).setOnClickListener(this)
        et1.requestFocus()

        admin = AdminSQLite(this, "administracion", null, 1)


    }
    override fun onClick(v: View) {
        when(v.id){
            R.id.balta->alta(v)
            R.id.bbaja->baja(v)
            R.id.bmodificacion->modificacion(v)
            R.id.bCCodigo->consultaporcodigo(v)
            R.id.bCDescripcion->consultapordescripcion(v)
            else->null
        }
    }
    /************************************************************************************************************/
    fun consultapordescripcion(v:View){
        val bd = admin.readableDatabase

        val desc = et2.text.toString()

        val fila = bd.rawQuery("select descripcion,precio,codigo from articulos where descripcion like '%$desc%'", null)

        if (fila.moveToFirst()){

            et2.setText(fila.getString(0))
            et3.setText(fila.getString(1))
            et1.setText(fila.getString(2))

            antes=fila.getString(2).toInt()

        } else Toast.makeText(this, "No existe ningún artículo con esa descripción.", Toast.LENGTH_SHORT).show()

        bd.close()
    }
    /************************************************************************************************************/

    fun consultaporcodigo(v:View){
        val bd = admin.readableDatabase

        val cod = et1.text.toString()

        if (et1.text.toString().isEmpty()){

            Toast.makeText(this, "El codigo no puede estar vacio", Toast.LENGTH_SHORT).show()
            bd.close()
            return
        }

        val fila = bd.rawQuery("select descripcion,precio from articulos where codigo=$cod", null)

        if (fila.moveToFirst()){
            et2.setText(fila.getString(0))
            et3.setText(fila.getString(1))
            antes=cod.toInt()
        } else Toast.makeText(this, "No existe un artículo con dicho código", Toast.LENGTH_SHORT).show()

        bd.close()
    }
    /************************************************************************************************************/
    fun modificacion(v:View){
        val bd = admin.writableDatabase

        var registro=ContentValues()
        registro.put("codigo",et1.text.toString())
        registro.put("descripcion",et2.text.toString())
        registro.put("precio",et3.text.toString())

        if (et1.text.toString().isEmpty()){

            Toast.makeText(this, "El codigo no puede estar vacio", Toast.LENGTH_SHORT).show()
            bd.close()
            return
        }
        val cant: Int

        cant = try {
                    bd.update("articulos", registro, "codigo= $antes", null)
        } catch (e: Exception) {
            Toast.makeText(this,e.message,Toast.LENGTH_SHORT).show()
            bd.close()
            return
        }

        bd.close()
        et1.setText("")
        et2.setText("")
        et3.setText("")

        if (cant==1){
            Toast.makeText(this, "El Registro se ha actualizado", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this, "No existe el articulo con el codigo indicado.", Toast.LENGTH_SHORT).show()
        }
    }
    fun baja(v:View) {
        val bd = admin.writableDatabase
        var cod=et1.text.toString()
        if (cod.isEmpty()){
            Toast.makeText(this, "El codigo no puede estar vacio", Toast.LENGTH_SHORT).show()
            bd.close()
            return
        }
        val cant = bd.delete("articulos", "codigo=$cod", null)
        bd.close()
        et1.setText("")
        et2.setText("")
        et3.setText("")

        if (cant == 1) Toast.makeText(this,"Se borró el artículo con dicho código", Toast.LENGTH_SHORT ).show()
        else Toast.makeText(this,"No existe un artículo con dicho código",Toast.LENGTH_SHORT).show()
    }
    fun alta(v:View){
        val bd = admin.writableDatabase

        var registro=ContentValues()
        registro.put("codigo",et1.text.toString())
        registro.put("descripcion",et2.text.toString())
        registro.put("precio",et3.text.toString())

        if (et1.text.toString().isEmpty()){

            Toast.makeText(this, "El codigo no puede estar vacio", Toast.LENGTH_SHORT).show()
            bd.close()
            return
        }
        val n = bd.insert("articulos", null, registro)
        bd.close()
        et1.setText("")
        et2.setText("")
        et3.setText("")

        if (n!=-1L){
            Toast.makeText(this, "El Registro se ha insertado", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this, "No se podido insertar", Toast.LENGTH_SHORT).show()
        }

    }
}