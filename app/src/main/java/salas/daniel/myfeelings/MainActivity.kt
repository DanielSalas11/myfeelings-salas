package salas.daniel.myfeelings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import salas.daniel.myfeelings.databinding.ActivityMainBinding
import salas.daniel.myfeelings.utilities.CustomBarDrawable
import salas.daniel.myfeelings.utilities.CustomCircleDrawable
import salas.daniel.myfeelings.utilities.Emociones
import salas.daniel.myfeelings.utilities.JSONFile

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    var jsonFile: JSONFile? = null
    var veryHappy = 0.0F
    var happy = 0.0F
    var neutral = 0.0F
    var sad = 0.0F
    var verysad = 0.0F
    var data: Boolean = false
    var lista = ArrayList<Emociones>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        jsonFile = JSONFile()
        fetchingData()
        if(!data){
            var emociones = ArrayList<Emociones>()
            val fondo = CustomCircleDrawable(this, emociones)
            binding.graph.background = fondo
            binding.graphVeryHappy.background = CustomBarDrawable(this,Emociones("Muy feliz", 0.0F, R.color.mustard,veryHappy))
            binding.graphHappy.background = CustomBarDrawable(this,Emociones("feliz", 0.0F, R.color.orange,happy))
            binding.graphNeutral.background = CustomBarDrawable(this,Emociones("Neutral", 0.0F, R.color.greenie,neutral))
            binding.graphSad.background = CustomBarDrawable(this,Emociones("Triste", 0.0F, R.color.blue,sad))
            binding.graphVerySad.background = CustomBarDrawable(this,Emociones("Muy triste", 0.0F, R.color.deepBlue,verysad))
        }else{
            actualizarGrafica()
            iconoMayoria()
        }

        binding.guardarButton.setOnClickListener{
            guardar()
        }
        binding.veryHappy.setOnClickListener{
            veryHappy++
            iconoMayoria()
            actualizarGrafica()
        }
        binding.happy.setOnClickListener{
            happy++
            iconoMayoria()
            actualizarGrafica()
        }
        binding.neutral.setOnClickListener{
            neutral++
            iconoMayoria()
            actualizarGrafica()
        }
        binding.sad.setOnClickListener{
            sad++
            iconoMayoria()
            actualizarGrafica()
        }
        binding.verySadButton.setOnClickListener{
            verysad++
            iconoMayoria()
            actualizarGrafica()
        }
    }



    fun fetchingData(){
        try {
            var json: String = jsonFile?.getData(this) ?: ""
            if (json != "" ){
                this.data = true
                var jsonArray : JSONArray = JSONArray(json)

                this.lista = parseJson(jsonArray)

                for (i in lista){
                    when(i.nombre){
                        "Muy feliz" -> veryHappy = i.total
                        "Feliz" -> happy = i.total
                        "Neutral" -> neutral = i.total
                        "Triste" -> sad = i.total
                        "Muy triste" -> verysad = i.total
                    }
                }
            } else {
                this.data = false
            }
        } catch (exception: JSONException){
        exception.printStackTrace()
        }
    }

    fun parseJson(jsonArray: JSONArray): ArrayList<Emociones>{
        var lista = ArrayList<Emociones>()

        for(i in 0..jsonArray.length()){
            try{
                val nombre= jsonArray.getJSONObject(i).getString("nombre")
                val porcentaje = jsonArray.getJSONObject(i).getDouble("porcentaje").toFloat()
                val color = jsonArray.getJSONObject(i).getInt("color")
                val total = jsonArray.getJSONObject(i).getDouble( "total").toFloat()
                var emocion = Emociones (nombre, porcentaje, color, total )
                lista.add(emocion)
            } catch (exception: JSONException) {
                exception.printStackTrace()
            }
        }
        return lista
    }

    fun actualizarGrafica(){
        val total = veryHappy+happy+neutral+verysad+sad

        var pVH: Float = (veryHappy * 100 / total).toFloat()
        var pH: Float = (happy * 100 / total).toFloat()
        var pN: Float = (neutral * 100 / total).toFloat()
        var pS: Float = (sad * 100 / total).toFloat()
        var pVS: Float = (verysad * 100 / total).toFloat()

        Log.d("porcentajes",  "very happy" + pVH)
        Log.d("porcentajes",  "happy" +pH)
        Log.d("porcentajes",  "neutral" +pN)
        Log.d("porcentajes",  "sad" +pS)
        Log.d("porcentajes",  "very sad" +pVS)

        lista.clear()
        lista.add(Emociones("Muy feliz", pVH, R.color.mustard,veryHappy))
        lista.add(Emociones("feliz", pH, R.color.orange,happy))
        lista.add(Emociones("Neutral", pN, R.color.greenie,neutral))
        lista.add(Emociones("Triste", pS, R.color.blue,sad))
        lista.add(Emociones("Muy triste", pVS, R.color.deepBlue,verysad))
        val fondo = CustomCircleDrawable(this, lista)

        binding.graphVeryHappy.background = CustomBarDrawable(this,Emociones("Muy feliz", veryHappy, R.color.mustard,veryHappy))
        binding.graphHappy.background = CustomBarDrawable(this,Emociones("feliz", happy, R.color.orange,happy))
        binding.graphNeutral.background = CustomBarDrawable(this,Emociones("Neutral", neutral, R.color.greenie,neutral))
        binding.graphSad.background = CustomBarDrawable(this,Emociones("Triste", sad, R.color.blue,sad))
        binding.graphVerySad.background = CustomBarDrawable(this,Emociones("Muy triste", verysad, R.color.deepBlue,verysad))

        binding.graph.background = fondo
    }

    fun iconoMayoria(){
        if(happy>veryHappy && happy>neutral && happy>sad && happy>verysad){
            binding.icon.setImageDrawable(resources.getDrawable(R.drawable.ic_happy))
        }
        if(veryHappy>happy && veryHappy>neutral && veryHappy>sad && veryHappy>verysad){
            binding.icon.setImageDrawable(resources.getDrawable(R.drawable.ic_veryhappy))
        }
        if(neutral>veryHappy && neutral>happy && neutral>sad && neutral>verysad){
            binding.icon.setImageDrawable(resources.getDrawable(R.drawable.ic_neutral))
        }
        if(sad>happy && sad>neutral && sad>veryHappy && sad>verysad){
            binding.icon.setImageDrawable(resources.getDrawable(R.drawable.ic_sad))
        }
        if(verysad>happy && verysad>neutral && verysad>sad && veryHappy<verysad){
            binding.icon.setImageDrawable(resources.getDrawable(R.drawable.ic_verysad))
        }
    }

    fun guardar(){
        var jsonArray = JSONArray()
        var o : Int = 0
        for(i in lista){
            Log.d("objetos", i.toString())
            var j: JSONObject = JSONObject()
            j.put("nombre", i.nombre)
            j.put("porcentaje", i.porcentaje)
            j.put("color", i.color)
            j.put("total",i.total)

            jsonArray.put(o,j)
            o++
        }

        jsonFile?.saveData(this,jsonArray.toString())

        Toast.makeText(this,"Datos guardados", Toast.LENGTH_SHORT).show()
    }
}