package org.lspatch.manager.unofficial

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.*
import android.widget.AdapterView.*
import org.json.*

class ModList : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mod_list)

        val sharedPref = this?.getSharedPreferences("org.lspatch.manager.unofficial_preferences", Context.MODE_PRIVATE) ?: return
        var appClick = ""
        intent?.extras?.let{
            appClick = it.getString("appClick").toString()
        }

        val StorageDirectory = Environment.getExternalStorageDirectory().toString()
        val result = CommandUtil.playRunTime("cat "+StorageDirectory+"/Android/data/"+appClick+"/files/lspatch.json")

        var new_result = ""
        val resultArray = result.toMutableList()
        for (i in resultArray.indices) {
            new_result = new_result + resultArray[i]
        }
        val test = JSONObject(new_result).getJSONArray("modules")

        val list: MutableList<String>  = mutableListOf()

        for (i in 0 until test.length()) {
            val mod_name = JSONObject(test.get(i).toString()).getString("name")
            val enabled_name = JSONObject(test.get(i).toString()).getString("enabled")
            val use_embed_name = JSONObject(test.get(i).toString()).getString("use_embed")
            list.add(mod_name)
            with (sharedPref.edit()) {
                putString(mod_name+"@id", i.toString())
                putString(mod_name+"@enabled", enabled_name)
                putString(mod_name+"@use_embed", use_embed_name)
                apply()
            }
        }

        // initialize an array adapter
        val adapter: ArrayAdapter<String> = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,list
        )
        val listView = findViewById<ListView>(R.id.ModListView)
        // attach the array adapter with list view
        listView.adapter = adapter

        // list view item click listener
        listView.onItemClickListener = OnItemClickListener{
                parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position)
            //textViewResult.text = "Selected : $selectedItem"

            val selectedMod = selectedItem.toString()
            with (sharedPref.edit()) {
                putString("selectedMod", selectedMod)
                apply()
            }
            val intent = Intent(this,SettingsActivity::class.java)
            intent.putExtra("appClick",appClick)
            intent.putExtra("modClick",selectedMod)
            startActivity(intent)
        }

    }

}
