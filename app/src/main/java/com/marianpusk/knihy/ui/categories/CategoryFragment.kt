package com.marianpusk.knihy.ui.categories

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.afollestad.materialdialogs.MaterialDialog
import com.marianpusk.carapplicaiton.database.bookDatabase
import com.marianpusk.knihy.R
import com.marianpusk.knihy.database.entities.Category
import com.marianpusk.knihy.databinding.FragmentCategoryBinding
import com.marianpusk.knihy.hideKeyboard
import com.marianpusk.knihy.ui.home.HomeViewModelFactory
import kotlinx.android.synthetic.main.fragment_category.*

class CategoryFragment : Fragment() {

    private lateinit var categoryViewModel: CategoryViewModel
    lateinit var categoryRecycleAdapter: CategoryRecycleAdapter
    var categoryName = ""

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentCategoryBinding>(inflater, R.layout.fragment_category, container, false)
        val application = requireNotNull(this.activity).application

        val root = inflater.inflate(R.layout.fragment_category, container, false)
        val datasource = bookDatabase.getInstance(application).books
        val viewModelFactory = CategoryViewModelFactory(datasource,application)
        categoryViewModel =
            ViewModelProviders.of(this,viewModelFactory).get(CategoryViewModel::class.java)
        binding.setLifecycleOwner(this)


        binding.recyclerAdapter.isNestedScrollingEnabled = true

        binding.categoryName.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                categoryName = p0.toString()
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                categoryName = p0.toString()
            }

        })

        binding.addBtn.setOnClickListener {
            if(categoryName.isNotEmpty()){
                val newCategory = Category()
                newCategory.name = categoryName
                categoryViewModel.insertCategory(newCategory)
                binding.categoryName.text.clear()
                hideKeyboard()
            }
        }

        categoryRecycleAdapter = CategoryRecycleAdapter(CategoryListener {
            id ->
             run {
                 MaterialDialog(requireActivity()).show {
                     message(R.string.delete)
                     positiveButton(R.string.yes) { dialog ->
                         categoryViewModel.deleteCatById(id)
                         dismiss()
                     }
                     negativeButton(R.string.no) { dialog ->
                         dismiss()
                     }
                 }
             }})
        binding.recyclerAdapter.adapter = categoryRecycleAdapter

        categoryViewModel.categories.observe(viewLifecycleOwner, Observer {
            it?.let {
                Log.v("kategorie",it.toString())
                categoryRecycleAdapter.submitList(it)
             //   binding.recyclerAdapter.adapter = categoryRecycleAdapter
            }
        })




        return binding.root
    }
}