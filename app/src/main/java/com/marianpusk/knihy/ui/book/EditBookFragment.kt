package com.marianpusk.knihy.ui.book

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.marianpusk.carapplicaiton.database.bookDatabase
import com.marianpusk.knihy.R
import com.marianpusk.knihy.database.entities.BookEntity
import com.marianpusk.knihy.databinding.FragmentEditBookBinding
import com.marianpusk.knihy.databinding.FragmentHomeBinding
import com.marianpusk.knihy.ui.home.HomeViewModel
import com.marianpusk.knihy.ui.home.HomeViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

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

            CoroutineScope(Dispatchers.IO).launch {
                val database = bookDatabase.getInstance(application)
                val category = database.books.getCategory(bookId)
                binding.categoryText.text = category
            
//                homeViewModel.getCategoryById(categoryId)
        }
        homeViewModel.book.observe(viewLifecycleOwner, Observer {
            it?.let {
                val book = it
                binding.athorText.text = book.author
                if (book.image != null){
                    binding.bookImage.setImageBitmap(book.image)

                }
                binding.ratingBar.rating = book.rating
                binding.yearText.text = book.year.toString()
                (activity as AppCompatActivity).supportActionBar!!.title = book.title
                if (book.note.isNotEmpty()){
                    binding.notes.setText(book.note)
                }

//                book.id_category?.let {
//                    homeViewModel.getCategoryById(it)
//                }


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

        setHasOptionsMenu(true)


        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.save_menu,menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.save) {
            if (notes.isNotEmpty()){
                homeViewModel.updateBookNotes(notes,bookId)
                this.findNavController().navigate(R.id.action_editBookFragment_to_navigation_home)
            }
        }

        return super.onOptionsItemSelected(item)
    }


}