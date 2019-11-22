package com.example.myclimaapp

import Previsao
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.climaapp.model.Clima
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject


class PrevisaoAdapter(private val context: Context, private val dataSource: ArrayList<Previsao>): BaseAdapter() {

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView = LayoutInflater.from(context).inflate(R.layout.previsao_cell, null, true)
        val textViewTempo = rowView.findViewById<TextView>(R.id.textViewTempoCelula)
        val textViewMaxMin= rowView.findViewById<TextView>(R.id.textViewMaxMinCelula)
        val imageViewClima = rowView.findViewById<ImageView>(R.id.imageViewIconWeather)

        val previsao = getItem(position) as Previsao

        textViewTempo.text = previsao.diaDaSemana?.toUpperCase()
            .plus(" - ").plus(previsao.descricao)
        textViewMaxMin.text = previsao.data.plus(" Máx: ")
            .plus(previsao.maxima).plus("° Mín: ").plus(previsao.minima).plus("°")

        when(previsao.condicao){
            "storm" -> imageViewClima.setImageResource(R.drawable.storm)
            "snow" -> imageViewClima.setImageResource(R.drawable.snow)
            "rain" -> imageViewClima.setImageResource(R.drawable.rain)
            "fog" -> imageViewClima.setImageResource(R.drawable.fog)
            "clear_day" -> imageViewClima.setImageResource(R.drawable.sun)
            "clear_night" -> imageViewClima.setImageResource(R.drawable.moon)
            "cloud" -> imageViewClima.setImageResource(R.drawable.cloudy)
            "cloudly_day" -> imageViewClima.setImageResource(R.drawable.cloud_day)
            "cloudly_night" -> imageViewClima.setImageResource(R.drawable.cloudy_night)
        }

        return rowView
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return dataSource.size
    }

}


class MainActivity : AppCompatActivity() {

    val PERMISSION_ID = 42
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var listaPrevisoes = ArrayList<Previsao>()
    lateinit var dialog: ProgressDialog
    lateinit var url: String
    lateinit var queue: RequestQueue
    lateinit var mFusedLocationClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //<editor-fold desc="Gradiente Effect" defaultstate="collapsed">
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        val animDrawable = root_layout.background as AnimationDrawable
        animDrawable.setEnterFadeDuration(10)
        animDrawable.setExitFadeDuration(5000)
        animDrawable.start()
        //</editor-fold>

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        queue = Volley.newRequestQueue(this)

        url =
            "https://api.hgbrasil.com/weather?key=4e56cf83&lat=${latitude}&log=${longitude}&user_ip=remote"

        dialog = ProgressDialog(this)
        dialog.setTitle("Trabalhando")
        dialog.setMessage("Recureando informações do Clima, aguarde...")
        dialog.show()

        getLastLocation()



        var queue = Volley.newRequestQueue(this)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        if (checkPermissions(android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            getLastLocation()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_ID -> {
                getLastLocation()
            }
        }
    }


    private fun checkPermissions(vararg permission: String): Boolean {

        val mensagemPermissao = "A localização é necessária para que possamos solicitar " +
                "a previsão de clima em sua localidade."

        val havePermission = permission.toList().all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }

        if (!havePermission) {
            if (permission.toList().any {
                    ActivityCompat.shouldShowRequestPermissionRationale(this, it) }) {

                // Alerta justificando o uso da localização.
                val alertDialog = AlertDialog.Builder(this)
                    .setTitle("Permission")
                    .setMessage(mensagemPermissao)
                    .setPositiveButton("Ok") { id, v ->
                        run {
                            ActivityCompat.requestPermissions(this, permission, PERMISSION_ID)
                        }
                    }
                    .setNegativeButton("No") { id, v -> }
                    .create()
                alertDialog.show()
            } else {
                //Na primeira execução do app, esta solicitação é executada
                ActivityCompat.requestPermissions(this, permission, PERMISSION_ID)
            }
            return false
        }
        return true
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        /*
        Adição do listener/callback de sucesso ao obter a última localização do dispositivo
         */
        mFusedLocationClient?.lastLocation?.addOnSuccessListener { location ->
            if (location == null) {
                Log.e("LOCATION: ", "Erro ao obter Localizacao: ")
            } else {
                location.apply {
                    Log.d("LOCATION: ", location.toString())
                    latitude = location.latitude
                    longitude = location.longitude

                    url = "https://api.hgbrasil.com/weather?key=4e56cf83&lat=${latitude}&log=${longitude}&user_ip=remote"

                    Log.d("GESTLASTLOCATION: ",url)
                    //Adiciona a requisição à fila do RequestQueue e a executa
                    queue.add(requestWheater())
                }
            }
        }
    }


    private fun requestWheater(): StringRequest {

        Log.d("Localizacao: ","Lat: ${latitude} Lon: ${longitude}")
        dialog.show()
        var stringRequest = StringRequest(Request.Method.GET, url,
            Response.Listener<String> { result ->
                val jsonResult = JSONObject(result).getJSONObject("results")
                val jsonPresisoesList = jsonResult.getJSONArray("forecast")

                val clima = preencheClima(jsonResult, listaPrevisoes)
                preenchePrevisoes(jsonPresisoesList)

                // Preencher os dados de Clina no UI
                textViewCidade.text = clima.nomeDaCidade
                textViewTemperatura.text = "${clima.temperatura.toString()}˚"
                textViewHora.text = clima.hora
                textViewData.text = clima.data
                textViewMaxima.text = (clima.previsoes as ArrayList<Previsao>)[0].maxima
                textViewMinima.text = (clima.previsoes as ArrayList<Previsao>)[0].minima
                textViewTempoCelula.text = clima.descricao
                textViewNascerDoSol.text = clima.nascerDoSol
                textViewPorDoSol.text = clima.porDoSol
                textViewData.text =
                    (clima.previsoes as ArrayList<Previsao>)[0].diaDaSemana?.toUpperCase()
                        .plus(" ").plus(clima.data)

//                imageViewIcon.setImageResource(R.drawable.icon_snow)

                when (clima.condicaoDoTempo) {
                    "storm" -> imageViewIcon.setImageResource(R.drawable.storm)
                    "snow" -> imageViewIcon.setImageResource(R.drawable.snow)
                    "rain" -> imageViewIcon.setImageResource(R.drawable.rain)
                    "fog" -> imageViewIcon.setImageResource(R.drawable.fog)
                    "clear_day" -> imageViewIcon.setImageResource(R.drawable.sun)
                    "clear_night" -> imageViewIcon.setImageResource(R.drawable.moon)
                    "cloud" -> imageViewIcon.setImageResource(R.drawable.cloudy)
                    "cloudly_day" -> imageViewIcon.setImageResource(R.drawable.cloud_day)
                    "cloudly_night" -> imageViewIcon.setImageResource(R.drawable.cloudy_night)
                }


                // Preencher ListView com a lista de Previsoes
                val adapter = PrevisaoAdapter(applicationContext, listaPrevisoes)
                listViewPrivisoes.adapter = adapter
                adapter.notifyDataSetChanged()

                dialog.dismiss()


                Log.d("RESPONSE: ", result.toString())
            }, Response.ErrorListener {
            Log.e("ERROR: ", it.localizedMessage)
        })

        queue.add(stringRequest)

        return stringRequest
    }

    private fun preencheClima(jsonObject: JSONObject, listaPrevisoes: ArrayList<Previsao>): Clima {
        val clima = Clima(
            jsonObject.getInt("temp"),
            jsonObject.getString("date"),
            jsonObject.getString("time"),
            jsonObject.getString("condition_code"),
            jsonObject.getString("description"),
            jsonObject.getString("currently"),
            jsonObject.getString("cid"),
            jsonObject.getString("city"),
            jsonObject.getString("img_id"),
            jsonObject.getInt("humidity"),
            jsonObject.getString("wind_speedy"),
            jsonObject.getString("sunrise"),
            jsonObject.getString("sunset"),
            jsonObject.getString("condition_slug"),
            jsonObject.getString("city_name")
        )
        clima.previsoes = listaPrevisoes
        return clima
    }

    private fun preenchePrevisoes(previsoes: JSONArray) {
        for (i in 0 until previsoes.length()) {
            val previsaoObject = previsoes.getJSONObject(i)
            val previsao = Previsao(
                previsaoObject.getString("date"),
                previsaoObject.getString("weekday"),
                previsaoObject.getString("max"),
                previsaoObject.getString("min"),
                previsaoObject.getString("description"),
                previsaoObject.getString("condition")
            )
            listaPrevisoes.add(previsao)
        }
    }


    override fun onStart() {
        super.onStart()
        if (checkPermissions(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            getLastLocation()
        }
    }



}
