package com.marianpusk.knihy.ui.book

import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import androidx.fragment.app.Fragment
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.marianpusk.carapplicaiton.database.bookDatabase
import com.marianpusk.knihy.App
import com.marianpusk.knihy.R
import com.marianpusk.knihy.databinding.FragmentBookImageBinding
import com.marianpusk.knihy.databinding.FragmentEditBookBinding
import com.marianpusk.knihy.ui.home.HomeViewModel
import com.marianpusk.knihy.ui.home.HomeViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.book_image.*



/**
 * A simple [Fragment] subclass.
 * Use the [BookImageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BookImageFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var imageId = 0
    private var bookId = 0
    private var bookName = ""


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding = DataBindingUtil.inflate<FragmentBookImageBinding>(inflater, R.layout.fragment_book_image, container, false)
        val application = requireNotNull(this.activity).application
        val datasource = bookDatabase.getInstance(application).books
        val viewModelFactory = HomeViewModelFactory(datasource,application)
        homeViewModel =
            ViewModelProviders.of(this,viewModelFactory).get(HomeViewModel::class.java)
        binding.setLifecycleOwner(this)
        // Inflate the layout for this fragment




        val arguments = BookImageFragmentArgs.fromBundle(requireArguments())
        imageId = arguments.imageId
        bookId = arguments.bookId
        bookName = arguments.bookName

        homeViewModel.getImage(imageId)

        homeViewModel.bookImage.observe(viewLifecycleOwner, Observer {
            it?.let{
                if (!it.imageURI.isNullOrEmpty()){
                    val imageUri = Uri.parse(it.imageURI)
                    binding.bookImage.setImageURI(imageUri)
                }


            }
        })

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.book_image_menu,menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.delete){
            MaterialDialog(requireActivity()).show {
                message(R.string.delete)
                positiveButton(R.string.yes) { dialog ->
                    homeViewModel.deleteImageById(imageId)
                    dismiss()
                    this@BookImageFragment.findNavController().navigate(BookImageFragmentDirections.actionBookImageFragmentToEditBookFragment(bookId,bookName))
                }
                negativeButton(R.string.no) { dialog ->
                    dismiss()
                }
            }


        }
        return super.onOptionsItemSelected(item)
    }




}