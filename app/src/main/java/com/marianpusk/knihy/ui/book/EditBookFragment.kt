package com.marianpusk.knihy.ui.book

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RatingBar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.view.isGone
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.marianpusk.carapplicaiton.database.bookDatabase
import com.marianpusk.knihy.R
import com.marianpusk.knihy.database.entities.BookEntity
import com.marianpusk.knihy.database.entities.ImageEntity
import com.marianpusk.knihy.databinding.FragmentEditBookBinding
import com.marianpusk.knihy.hideKeyboard
import com.marianpusk.knihy.ui.home.HomeViewModel
import com.marianpusk.knihy.ui.home.HomeViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EditBookFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditBookFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var bookId = 0
    private var notes = ""
    private var author = ""
    private var year = 0
    private var rating = 0F
    private var categoryId = 0
    private val REQUEST_CODE = 44
    private val MY_CAMERA_PERMISSION_REQUEST = 112
    private val IMAGE_PICK_CODE = 22
    private val IMAGE_PERMISSION_CODE = 23




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
        binding.setLifecycleOwner(this)



        val arguments = EditBookFragmentArgs.fromBundle(requireArguments())
        bookId = arguments.bookId
        homeViewModel.getBookById(bookId)



        val imageAdapter = ImageRecycleAdapter(BookImageListener{
            image,id -> this.findNavController().navigate(EditBookFragmentDirections.actionEditBookFragmentToBookImageFragment(id,bookId))
        })

        val layoutManager = LinearLayoutManager(application,LinearLayoutManager.HORIZONTAL,false)
        binding.imageRecycleView.layoutManager = layoutManager

        homeViewModel.images.observe(viewLifecycleOwner, Observer {
            if(!it.isNullOrEmpty()){
                binding.imageRecycleView.visibility = View.VISIBLE
                imageAdapter.submitList(it)

            }else{
                binding.imageRecycleView.visibility = View.GONE
            }

        })

        binding.imageRecycleView.adapter = imageAdapter

        homeViewModel.book.observe(viewLifecycleOwner, Observer {
            it?.let {
                val book = it
                binding.authorText.setText(book.author)
                binding.ratingBar.rating = book.rating
                binding.yearText.setText(book.year.toString())
                (activity as AppCompatActivity).supportActionBar!!.title = book.title
                if (book.note.isNotEmpty()){
                    binding.notes.setText(book.note)
                }

//                book.id_category?.let {
//                    homeViewModel.getCategoryById(it)
//                }


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
                    binding.categorySpinner.setSelection(position)

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
                TODO("Not yet implemented")
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
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
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkSelfPermission(requireActivity(),Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                val permission = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                requestPermissions(permission,IMAGE_PERMISSION_CODE)
            }else {
                pickImgFromGallery()
            }
        }
        else {
            pickImgFromGallery()
        }
    }

    private fun pickImgFromGallery(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/"
        startActivityForResult(intent,IMAGE_PICK_CODE)
    }

    private fun captureImage(){
        if(ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA),MY_CAMERA_PERMISSION_REQUEST)
        }else {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            if(cameraIntent.resolveActivity(requireActivity().packageManager) != null){
                startActivityForResult(cameraIntent,REQUEST_CODE)
            }
            else{
                Toast.makeText(requireActivity(),"Unable to open camera", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == IMAGE_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                pickImgFromGallery()
            }
            else {
                Toast.makeText(requireActivity(),"Permission denied" , Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK){
            val takenImage = data?.extras?.get("data") as Bitmap

            var newImage = ImageEntity()
            newImage.image = takenImage
            newImage.id_book = bookId
            homeViewModel.insertImage(newImage)
        }

        if(requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK){

            val imageUri: Uri? = data?.data
            val bitmap =
                MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri)

            var newImage = ImageEntity()
            newImage.image = bitmap
            newImage.id_book = bookId
            homeViewModel.insertImage(newImage)
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
            R.id.capture_photo -> captureImage()
            R.id.choose_picture -> checkPermission()
        }



        return super.onOptionsItemSelected(item)
    }


}