package com.marianpusk.knihy.ui.book

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.marianpusk.carapplicaiton.database.bookDatabase
import com.marianpusk.knihy.R
import com.marianpusk.knihy.StringUtils
import com.marianpusk.knihy.database.entities.BookEntity
import com.marianpusk.knihy.databinding.FragmentBookBinding
import com.marianpusk.knihy.ui.home.HomeViewModelFactory

class BookFragment : Fragment() {

    private val REQUEST_CODE = 42
    private val MY_CAMERA_PERMISSION_REQUEST = 111
    private lateinit var bookViewModel: BookViewModel
    var bookName: String = ""
    var category = ""
    var author = ""
    var year = 0
    var rating = 0F
    var categoryId = -1
    var image: Bitmap? = null



    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentBookBinding>(inflater, R.layout.fragment_book, container, false)
        val application = requireNotNull(this.activity).application
        val datasource = bookDatabase.getInstance(application).books
        val viewModelFactory = BookViewModelFactory(datasource,application)
        bookViewModel =
                ViewModelProviders.of(this,viewModelFactory).get(BookViewModel::class.java)



        binding.author.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                author = p0.toString()

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                author = p0.toString()

            }

        })

        binding.bookName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                bookName = p0.toString()

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                bookName = p0.toString()

            }

        })

        binding.year.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                year = p0.toString().toInt()

            }

        })

        binding.ratingBar.setOnRatingBarChangeListener(object: RatingBar.OnRatingBarChangeListener{
            override fun onRatingChanged(p0: RatingBar?, p1: Float, p2: Boolean) {
                rating = p1

            }

        })

        binding.imageView2.setOnClickListener {


            if(ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.CAMERA),MY_CAMERA_PERMISSION_REQUEST)
            }else {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

                if(cameraIntent.resolveActivity(application.packageManager) != null){
                    startActivityForResult(cameraIntent,REQUEST_CODE)
                }
                else{
                    Toast.makeText(application,"Unable to open camera",Toast.LENGTH_SHORT).show()
                }
            }

        }
        var categoriesArray: Array<String?>

        bookViewModel.categories.observe(viewLifecycleOwner, Observer {
            it?.let {

                categoriesArray = arrayOfNulls<String>(it.size)
                it.forEachIndexed { index, category ->
                    categoriesArray[index] = category.name
                }

                val categoryAdapter = ArrayAdapter(
                    application.applicationContext, android.R.layout.simple_spinner_item,
                    categoriesArray
                )
                categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                binding.categorySpinner.adapter = categoryAdapter
            }
        })

        binding.categorySpinner.onItemSelectedListener = object : AdapterView.OnItemClickListener,
            AdapterView.OnItemSelectedListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                TODO("Not yet implemented")
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                        bookViewModel.getCategoryIdByName(p0!!.selectedItem.toString())

            }

        }

        bookViewModel.categoryId.observe(viewLifecycleOwner, Observer {
            it?.let {
                categoryId = it
            }
        })

        bookViewModel.cameraImage.observe(viewLifecycleOwner, Observer {
            it?.let {
                image = it
                binding.imageView2.setImageBitmap(it)
            }
        })



        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK){
            val takenImage = data?.extras?.get("data") as Bitmap
            bookViewModel.getImage(takenImage)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.save_menu,menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.save){
            if(bookName.isNotEmpty() || author.isNotEmpty() || category.isNotEmpty() || year > 0){
                val newBook = BookEntity()
                newBook.author = author
                newBook.title = bookName
                newBook.titleNormalized = StringUtils.unaccent(bookName)
                newBook.year = year
                newBook.rating = rating
                newBook.image = image
                if (categoryId > -1){
                    newBook.id_category = categoryId
                }
                bookViewModel.insertBook(newBook)
                this.findNavController().navigate(R.id.action_navigation_book_to_navigation_home)
            }
        }


        return super.onOptionsItemSelected(item)
    }
}

