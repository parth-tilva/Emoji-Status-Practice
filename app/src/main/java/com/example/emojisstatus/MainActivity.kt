package com.example.emojisstatus

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.emojisstatus.databinding.ActivityMainBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


data class User(
    val displayName: String ="",
    val emojis: String = "",
    val photoUrl: String = ""
)

class ViewHolder(view: View): RecyclerView.ViewHolder(view)

class MainActivity : AppCompatActivity() {
    val TAG = "MainActivity"
    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        db = Firebase.firestore

        val query = db.collection("users")

        val options: FirestoreRecyclerOptions<User> = FirestoreRecyclerOptions.Builder<User>()
            .setQuery(query, User::class.java)
            .setLifecycleOwner(this)
            .build()

        val adapter= object : FirestoreRecyclerAdapter<User,ViewHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                val view = LayoutInflater.from(this@MainActivity).inflate(android.R.layout.simple_list_item_2,parent,false)
                return ViewHolder(view)
            }

            override fun onBindViewHolder(holder: ViewHolder, position: Int, model: User) {
                val tvName: TextView = holder.itemView.findViewById(android.R.id.text1)
                val tvEmoji: TextView = holder.itemView.findViewById(android.R.id.text2)
                Log.d(TAG,"model ${model.displayName}")
                tvName.text = model.displayName
                tvEmoji.text = model.emojis
            }
        }
        binding.rvEmojiList.adapter = adapter
        binding.rvEmojiList.layoutManager  = LinearLayoutManager(this)
        binding.rvEmojiList.itemAnimator = null

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.log_out){
            Log.d(TAG ,"user Log Out")
            auth.signOut()
            val logOut = Intent(this,LogInActivity::class.java)
            //logOut.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent. FLAG_ACTIVITY_CLEAR_TASK
            startActivity(logOut)
            finishAffinity()
        }else if(item.itemId == R.id.action_edit){
            Log.d(TAG ,"edit called")
            showAlertDialog()

        }
        return super.onOptionsItemSelected(item)
    }

    private fun showAlertDialog() {
        val editText = EditText(this)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Update you emojis")
            .setView(editText)
            .setNegativeButton("Cancle",null)
            .setPositiveButton("Ok") { _, _ ->
                val stringEmoji = editText.text.toString()
                if(stringEmoji.isBlank())
                Toast.makeText(this@MainActivity,"blank input",Toast.LENGTH_LONG).show()

                db.collection("users").document(auth.uid!!).update("emojis",stringEmoji)
                            .addOnSuccessListener { document ->
                                if (document != null) {
                                    Log.d(TAG, "DocumentSnapshot data: ${document.toString()}")
                                } else {
                                    Log.d(TAG, "No such document")
                                }
                            }
                            .addOnFailureListener { exception ->
                                Log.d(TAG, "get failed with ", exception)
                            }
            }
            .show()

//        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
//
//        }  //??
    }
}