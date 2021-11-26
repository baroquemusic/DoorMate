package com.unicorn.doormate

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

@SuppressLint("StaticFieldLeak")
lateinit var register: Button
@SuppressLint("StaticFieldLeak")
lateinit var contacts: Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        register = findViewById(R.id.register)
        contacts = findViewById(R.id.contacts)

        register.setOnClickListener {
            val intent = Intent(this@MainActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        contacts.setOnClickListener {

            val intent = Intent(this@MainActivity, ContactsActivity::class.java)
            startActivity(intent)

        }
    }
}

