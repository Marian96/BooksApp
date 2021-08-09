package com.marianpusk.knihy.ui.book

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.marianpusk.carapplicaiton.database.bookDatabase
import com.marianpusk.knihy.App
import com.marianpusk.knihy.R
import com.marianpusk.knihy.database.entities.ImageEntity
import com.marianpusk.knihy.databinding.FragmentEditBookBinding
import com.marianpusk.knihy.hideKeyboard
import com.marianpusk.knihy.ui.home.HomeViewModel
import com.marianpusk.knihy.ui.home.HomeViewModelFactory
import kotlinx.coroutines.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class EditBookFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var bookId = 0
    private var notes = ""
    private var author = ""
    private var year = 0
    private var rating = 0F
    private var categoryId :Int? = null
    private val REQUEST_CODE = 44
    private val MY_CAMERA_PERMISSION_REQUEST = 112
    private val IMAGE_PICK_CODE = 22
    private val IMAGE_PERMISSION_CODE = 23
    private val imageUri = ""
    private lateinit var currentPhotoPath: String





    @SuppressLint("CheckResult")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = DataBindingUtil.inflate<FragmentEditBookBinding>(inflater, R.layout.fragment_edit_book, container, false)
        val application = requireNotNull(this.activity).application
        val datasource = bookDatabase.getInstance(application).books
        val viewModelFactory = HomeViewModelFactory(datasource,application)
        homeViewModel =
            ViewModelProviders.of(this,viewModelFactory).get(HomeViewModel::class.java)
        binding.lifecycleOwner = this



        val arguments = EditBookFragmentArgs.fromBundle(requireArguments())
        bookId = arguments.bookId
        val bookName = arguments.name
        (activity as AppCompatActivity).supportActionBar!!.title = bookName
        homeViewModel.getBookById(bookId)
        homeViewModel.getImages(bookId)



        val imageAdapter = ImageRecycleAdapter(BookImageListener{
            image,id -> this.findNavController().navigate(EditBookFragmentDirections.actionEditBookFragmentToBookImageFragment(id,bookId,bookName))
        }, PlusImageListener {
            MaterialDialog(requireActivity()).show {
                message(R.string.new_photo)
                listItemsSingleChoice(R.array.image_dialog) { dialog, index, text ->
                    if (index == 0){
                        dispatchTakePictureIntent()
                    }
                    else{
                        checkPermission()
                    }
                }
            }
        })

        val layoutManager = LinearLayoutManager(application,LinearLayoutManager.HORIZONTAL,false)
        binding.imageRecycleView.layoutManager = layoutManager

        homeViewModel.images.observe(viewLifecycleOwner, Observer {
            if(!it.isNullOrEmpty()){
                homeViewModel.setVisibility(true)
                imageAdapter.submitList(it)

            }else{
                homeViewModel.setVisibility(false)
                }

        })

//        homeViewModel.recyclerViewVisible.observe(viewLifecycleOwner, Observer {
//            if(it){
//                binding.imageRecycleView.visibility = View.VISIBLE
//            }
//            else{
//                binding.imageRecycleView.visibility = View.GONE
//
//            }
//        })

        binding.imageRecycleView.adapter = imageAdapter

        homeViewModel.book.observe(viewLifecycleOwner, Observer {
            it?.let {
                val book = it
                binding.authorText.setText(book.author)
                binding.ratingBar.rating = book.rating
                binding.yearText.setText(book.year.toString())
                binding.progressBar.visibility = View.GONE
                if (book.note.isNotEmpty()){
                    binding.notes.setText(book.note)
                }


            }
        })

        var categoriesArray: Array<String?>

        homeViewModel.categories.observe(viewLifecycleOwner, Observer {
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

                CoroutineScope(Dispatchers.IO).launch {
                    val database = bookDatabase.getInstance(application)
                    val category = database.books.getCategory(bookId)
                    val position = categoryAdapter.getPosition(category)
                    withContext(Dispatchers.Main){
                        binding.categorySpinner.setSelection(position)

                    }

                }
            }
        })



//        homeViewModel.category.observe(viewLifecycleOwner, Observer {
//
//                binding.categoryText.text = it
//
//        })



        binding.notes.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                notes = p0.toString()
            }

        })

        binding.authorText.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                author = p0.toString()
            }

        })

        binding.yearText.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(p0: Editable?) {

            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(p0!!.isNotEmpty()){

                    year = p0.toString().toInt()
                }
                else {
                    year = 0
                }

            }


        })

        binding.ratingBar.setOnRatingBarChangeListener(object: RatingBar.OnRatingBarChangeListener{
            override fun onRatingChanged(p0: RatingBar?, p1: Float, p2: Boolean) {
                rating = p1

            }

        })

        binding.categorySpinner.onItemSelectedListener = object : AdapterView.OnItemClickListener,
            AdapterView.OnItemSelectedListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                homeViewModel.getCategoryIdByName(p0!!.selectedItem.toString())

            }

        }

        homeViewModel.categoryId.observe(viewLifecycleOwner, Observer {
            it?.let {
                categoryId = it
            }
        })


        setHasOptionsMenu(true)


        return binding.root
    }

    private fun checkPermission(){

            if(checkSelfPermission(requireActivity(),Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                val permission = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                requestPermissions(permission,IMAGE_PERMISSION_CODE)
            }else {
                pickImgFromGallery()
            }

    }

    private fun pickImgFromGallery(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/"
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        startActivityForResult(intent,IMAGE_PICK_CODE)
    }


    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = App.context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun dispatchTakePictureIntent() {
        if(checkSelfPermission(requireContext(),Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA),MY_CAMERA_PERMISSION_REQUEST)
        }
        else{
            captureImage()
        }

    }

    private fun captureImage(){
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    Log.e("creating photo","Error occurred while creating the File",ex)
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        App.context,
                        "com.marianpusk.knihy.fileprovider",
                        it
                    )


                    takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_CODE)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){

            IMAGE_PERMISSION_CODE -> {

                    if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                        pickImgFromGallery()
                    }
                    else {
                        Toast.makeText(requireActivity(),"Permission denied" , Toast.LENGTH_SHORT).show()
                    }

        }
          REQUEST_CODE -> {
              if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                  captureImage()
              }
              else {
                  Toast.makeText(requireActivity(),"Permission denied" , Toast.LENGTH_SHORT).show()
              }
          }


        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK){
            val imageUri: Uri? = data?.data

            Log.v("imageData",imageUri.toString())


            val newImage = ImageEntity()
            newImage.id_book = bookId
            val photoURI: Uri = FileProvider.getUriForFile(
                App.context,
                "com.marianpusk.knihy.fileprovider",
                File(currentPhotoPath)
            )
            newImage.imageURI = photoURI.toString()
            homeViewModel.insertImage(newImage)
            homeViewModel.setVisibility(true)


        }

        if(requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK){

            val imageUri: Uri? = data?.data
            Log.v("dataImage",imageUri.toString())
//            val bitmap =
//                MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, imageUri)

            var newImage = ImageEntity()
            newImage.imageURI = imageUri.toString()
            newImage.id_book = bookId
            homeViewModel.insertImage(newImage)
            //homeViewModel.getImages(bookId)


        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.edit_book_menu,menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.save -> {

                    homeViewModel.updateBook(bookId,notes,author,categoryId,rating,year)
                    hideKeyboard()
                    this.findNavController().navigate(R.id.action_editBookFragment_to_navigation_home)

            }
            R.id.capture_photo -> dispatchTakePictureIntent()
            R.id.choose_picture -> checkPermission()
        }



        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        homeViewModel.getImages(bookId)

        super.onResume()
    }


}