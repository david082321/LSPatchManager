package org.lspatch.manager.unofficial

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
//import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
//import android.util.Log
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
//import androidx.documentfile.provider.DocumentFile
import java.io.File


class AppList : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_list)
        //copyAssetsFile("shizuku")
        //copyAssetsFile("shizuku.dex")

        // https://stackoverflow.com/questions/60062746/why-does-my-android-kotlin-app-not-request-permission-from-the-user-to-turn-on-l
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    101)
        } else {
            // Permission has already been granted
            init()
        }
    }

    fun init() {
        val sharedPref = this?.getSharedPreferences("org.lspatch.manager.unofficial_preferences", MODE_PRIVATE) ?: return

        val StorageDirectory = Environment.getExternalStorageDirectory().toString()
        //val Shizuku_Path = "sh /data/data/org.lspatch.manager.unofficial/files/shizuku "
        var result = CommandUtil.playRunTime("find "+StorageDirectory+"/Android/data -name lspatch.json")


        if (result.toString()=="[]"){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) Toast.makeText(this,R.string.not_support_11,Toast.LENGTH_LONG).show()
            else Toast.makeText(this,R.string.not_found,Toast.LENGTH_LONG).show()
        }

        // https://android--code.blogspot.com/2020/03/android-kotlin-listview-add-item.html
        // list to populate list view
        val list = result.toMutableList()
        for (i in list.indices) {
            list[i] = list[i].replace(StorageDirectory+"/Android/data/","")
                .replace("/files/lspatch.json","")
        }

        // initialize an array adapter
        val adapter: ArrayAdapter<String> = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,list
        )
        val listView = findViewById<ListView>(R.id.AppListView)
        //val addButton = findViewById<Button>(R.id.AddButton)
        //val textViewResult = findViewById<TextView>(R.id.textViewResult)
        // attach the array adapter with list view
        listView.adapter = adapter

        // list view item click listener
        listView.onItemClickListener = OnItemClickListener{
                parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position)
            //textViewResult.text = "Selected : $selectedItem"

            val selectedApp = selectedItem.toString()
            with (sharedPref.edit()) {
                putString("selectedApp", selectedApp)
                putBoolean("Init", true)
                apply()
            }
            val intent = Intent(this,ModList::class.java)
            intent.putExtra("appClick",selectedApp)
            startActivity(intent)
        }
    }

    // https://www.mirwing.com/2017/11/kotlin-copy-assets-to-internal-storage.html
    fun copyAssetsFile(filename: String) {
        this.assets.open(filename).use { stream ->
            File("${this.filesDir}/$filename").outputStream().use {
                stream.copyTo(it)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) init()
            else Toast.makeText(this, R.string.need_permission, Toast.LENGTH_SHORT).show()
        }
    }


}
