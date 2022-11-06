package com.example.voyageapp.filters

import android.widget.Filter
import com.example.voyageapp.adapters.AdapterMuseum
import com.example.voyageapp.models.ModelMuseum

class FilterMuseum: Filter {

    //arraylist in which we want to search
    private var filterList: ArrayList<ModelMuseum>

    //adapter in which filter need to be implemented
    private var adapterMuseum: AdapterMuseum

    //constructor
    constructor(filterList: ArrayList<ModelMuseum>, adapterMuseum: AdapterMuseum) :super() {
        this.filterList = filterList
        this.adapterMuseum = adapterMuseum
    }

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        var constraint = constraint
        val results = FilterResults()

        //value should not be null and not empty
        if (constraint != null && constraint.isNotEmpty()){
            //searched value is nor null or empty

            //change to upper case, or lower case to avoid case sensitivity
            constraint = constraint.toString().uppercase()
            val filteredModels:ArrayList<ModelMuseum> = ArrayList()

            for (i in 0 until filterList.size){
                //validate
                if (filterList[i].museumName.uppercase().contains(constraint)){
                    //add to filtered list
                    filteredModels.add(filterList[i])
                }
            }
            results.count = filteredModels.size
            results.values = filteredModels
        }
        else{
            //search value is either null or empty
            results.count = filterList.size
            results.values = filterList
        }
        return results
    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults) {
        //apply filter changes
        adapterMuseum.museumArrayList = results.values as ArrayList<ModelMuseum>

        //notify changes
        adapterMuseum.notifyDataSetChanged()
    }
}