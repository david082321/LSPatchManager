package org.lspatch.manager.unofficial

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.KeyEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import java.io.File

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        val sharedPref = this?.getSharedPreferences("org.lspatch.manager.unofficial_preferences", Context.MODE_PRIVATE) ?: return

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        var appClick = ""
        var modClick = ""
        intent?.extras?.let{
            appClick = it.getString("appClick").toString()
            modClick = it.getString("modClick").toString()
        }

        val enabled_status = sharedPref.getString(modClick+"@enabled", "true").toBoolean()
        val use_embed_status = sharedPref.getString(modClick+"@use_embed", "true").toBoolean()
        val modClickId = sharedPref.getString(modClick+"@id", "").toString()

        with (sharedPref.edit()) {
            putString("appClick", appClick)
            putString("modClick", modClick)
            putString("modClickId", modClickId)
            putBoolean("enabled", enabled_status)
            putBoolean("use_embed", use_embed_status)
            apply()
        }
    }

    private fun writemyfile(){
        val sharedPref = this?.getSharedPreferences("org.lspatch.manager.unofficial_preferences", Context.MODE_PRIVATE) ?: return
        val enabled = sharedPref.getBoolean("enabled", true).toString()
        val use_embed = sharedPref.getBoolean("use_embed", true).toString()
        val appClick = sharedPref.getString("appClick","")
        val modClick = sharedPref.getString("modClick", "")
        val modClickId = sharedPref.getString("modClickId", "").toString()
        val StorageDirectory = Environment.getExternalStorageDirectory().toString()
        val result = CommandUtil.playRunTime("cat "+StorageDirectory+"/Android/data/"+appClick+"/files/lspatch.json")

        var new_result = ""
        val resultArray = result.toMutableList()
        for (i in resultArray.indices) {
            if(i==0){
                new_result = resultArray[i]
            }else if(i == modClickId.toInt()*6+4){
                val replace = resultArray[i].replace("true",enabled).replace("false",enabled)
                new_result = new_result + "\n" + replace
            }else if(i == modClickId.toInt()*6+5){
                val replace = resultArray[i].replace("true",use_embed).replace("false",use_embed)
                new_result = new_result + "\n" + replace
            }else{
                new_result = new_result + "\n" + resultArray[i]
            }
        }
        val fileName = StorageDirectory+"/Documents/lspatch.json"
        val file = File(fileName)
        file.writeText(new_result)
        Runtime.getRuntime().exec("mv -f "+fileName+" "+StorageDirectory+"/Android/data/"+appClick+"/files/lspatch.json")
        //Toast.makeText(this, enabled, Toast.LENGTH_SHORT).show()
        with (sharedPref.edit()) {
            putString(modClick+"@enabled", enabled)
            putString(modClick+"@use_embed", use_embed)
            apply()
        }
        Toast.makeText(this,R.string.setting_saved,Toast.LENGTH_LONG).show()
    }

    // https://ithelp.ithome.com.tw/articles/10216949
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //Toast.makeText(this, "Close?", Toast.LENGTH_SHORT).show()
            //return false
            //val sharedPref = this?.getSharedPreferences("org.lspatch.manager.unofficial_preferences", Context.MODE_PRIVATE)
            writemyfile()
        }
        return super.onKeyDown(keyCode, event)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.new_preference, rootKey)
        }
    }
}