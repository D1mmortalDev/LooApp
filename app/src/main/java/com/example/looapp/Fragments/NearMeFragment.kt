package com.example.looapp.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.looapp.Adapters.RestroomAdapter
import com.example.looapp.Model.Restroom
import com.example.looapp.databinding.FragmentNearMeBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class NearMeFragment : Fragment() {
    private lateinit var binding:FragmentNearMeBinding
    private lateinit var recycleView: RecyclerView
    private lateinit var adapter: RestroomAdapter
    private lateinit var restroom: MutableList<Restroom>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNearMeBinding.inflate(layoutInflater,container,false)
        // Set up recycleView Binding
        recycleView = binding.recyclerView
        // Set up layout
        recycleView.layoutManager = LinearLayoutManager(context)
        // Get all Data from Firebase
        adapter = RestroomAdapter(mutableListOf())
        recycleView.adapter =adapter

        getAllData("restroom"){coordinates->
            // Combine with Adapter
            adapter.updateData(coordinates)
            restroom =coordinates
        }

          binding.searchView.setOnQueryTextListener(object:SearchView.OnQueryTextListener,
              androidx.appcompat.widget.SearchView.OnQueryTextListener {
              override fun onQueryTextSubmit(query: String?): Boolean {
                  return false
              }

              override fun onQueryTextChange(newText: String?): Boolean {
                  filterList(newText)
                  return true
              }

          })

        return binding.root
    }

    private fun filterList(query: String?) {
            if(query!=null){
                val filteredList = ArrayList<Restroom>()
                for (i in restroom){
                    if(i.formattedAddress?.lowercase(Locale.ROOT)!!.contains(query)){
                        filteredList.add(i)
                    }
                }
                if(filteredList.isEmpty()){
                    Toast.makeText(context,"No data found",Toast.LENGTH_SHORT).show()
                }
                else{
                    adapter.setFiltered(filteredList)
                }
            }
        }



    private fun getAllData(collectionName: String, callback: (MutableList<Restroom>) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val collectionRef = db.collection(collectionName)
        collectionRef.get()
            .addOnSuccessListener { result ->
                val locationList = mutableListOf<Restroom>()
                for (document in result) {
                    val restroom = Restroom(
                        document.data["markerId"].toString(),
                        document.data["longitude"].toString().toDouble(),
                        document.data["latitude"].toString().toDouble(),
                        document.data["houseNumber"].toString(),
                        document.data["street"].toString(),
                        document.data["neighborhood"].toString(),
                        document.data["locality"].toString(),
                        document.data["postcode"].toString(),
                        document.data["place"].toString(),
                        document.data["district"].toString(),
                        document.data["region"].toString(),
                        document.data["country"].toString(),
                        document.data["formattedAddress"].toString(),
                        document.data["countryIso1"].toString(),
                        document.data["countryIso2"].toString())
                    locationList.add(restroom)
                }
                callback(locationList)
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error Occurred!", Toast.LENGTH_SHORT).show()
            }
    }

}
