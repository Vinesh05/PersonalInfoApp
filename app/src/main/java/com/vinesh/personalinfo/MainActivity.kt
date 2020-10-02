package com.vinesh.personalinfo

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.squareup.picasso.Picasso
import com.vinesh.personalinfo.databinding.ActivityMainBinding
import com.vinesh.personalinfo.databinding.EditDialogBinding
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.InputStream
import java.util.*

class MainActivity : AppCompatActivity() {

    val GALLERY_REQUEST_CODE = 123
    lateinit var sharedPreferences:SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            binding.root.isForceDarkAllowed = false
        }
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name),Context.MODE_PRIVATE)

        binding.apply {
            txtName.text = sharedPreferences.getString("name","Name")
            txtDob.text = sharedPreferences.getString("dob","11/11/1111")
            txtPhoneNumber.text = sharedPreferences.getString("phone_number","0123456789")
            txtEmailAddress.text = sharedPreferences.getString("email_address","email@gmail.com")
            txtJob.text = sharedPreferences.getString("job","Job")
        }

        binding.editMenu.setOnClickListener {
            showEditDialog(sharedPreferences)
        }

        val image = binding.image
        image.setOnClickListener {
            val intent = Intent()
            intent.setType("image/*")
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(intent,"Pick an Image"),GALLERY_REQUEST_CODE)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data!=null){
            val imageData = data.data
            image.setImageURI(imageData)
        }
        else{
            Toast.makeText(this,"Some Error Occurred",Toast.LENGTH_SHORT).show()
        }
    }

    private fun showEditDialog(sharedPreferences: SharedPreferences) {

        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.edit_dialog,null,false)

        val dialog = AlertDialog.Builder(this).create()
        dialog.setView(view)

        var date = "11/11/1111"
        val etDateOfBirth = view.findViewById<LinearLayout>(R.id.etDateOfBirth)
        etDateOfBirth.setOnClickListener {
            val cal = Calendar.getInstance()
            val currentYear = cal.get(Calendar.YEAR)
            val currentMonth = cal.get(Calendar.MONTH)
            val currentDay = cal.get(Calendar.DAY_OF_MONTH)
            date = "$currentDay/$currentMonth/$currentYear"

            val dateDialog = DatePickerDialog(this,
                android.R.style.Theme,
                DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->
                    val newMonth = month+1
                    date="$day/$newMonth/$year"
                    println(date)
                    view.findViewById<TextView>(R.id.txtDate).text=date
                },
                currentYear,currentMonth,currentDay)

            dateDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dateDialog.show()

        }

        val doneButton = view.findViewById<Button>(R.id.btnDone)
        val cancleButtton = view.findViewById<Button>(R.id.btnCancel)

        doneButton.setOnClickListener {

            val etName = view.findViewById<EditText>(R.id.etName)

            val etPhoneNumber = view.findViewById<EditText>(R.id.etPhoneNumber)
            val etEmailAddress = view.findViewById<EditText>(R.id.etEmailAddress)
            val etJob = view.findViewById<EditText>(R.id.etJob)

            sharedPreferences.edit().putString("name",etName.text.toString()).apply()
            sharedPreferences.edit().putString("dob",date).apply()
            sharedPreferences.edit().putString("phone_number",etPhoneNumber.text.toString()).apply()
            sharedPreferences.edit().putString("email_address",etEmailAddress.text.toString()).apply()
            sharedPreferences.edit().putString("job",etJob.text.toString()).apply()

            dialog.dismiss()
            finish()
            overridePendingTransition(0,0)
            startActivity(intent)
            overridePendingTransition(0,0)
        }

        cancleButtton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

    }
}