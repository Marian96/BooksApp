package com.marianpusk.knihy.ui.home

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.marianpusk.carapplicaiton.database.bookDatabase
import com.marianpusk.knihy.R
import com.marianpusk.knihy.databinding.FragmentHomeBinding
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment() : Fragment() {

    lateinit var booksAdapter: RecycleAdapter

    private lateinit var homeViewModel: HomeViewModel

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = DataBindingUtil.inflate<FragmentHomeBinding>(
            inflater,
            R.layout.fragment_home,
            container,
            false
        )
        val application = requireNotNull(this.activity).application
        val datasource = bookDatabase.getInstance(application).books
        val viewModelFactory = HomeViewModelFactory(datasource, application)
        homeViewModel =
                ViewModelProviders.of(requireActivity(), viewModelFactory).get(HomeViewModel::class.java)
        binding.setLifecycleOwner(this)

        booksAdapter = RecycleAdapter(BookListener { id,name ->
            this@HomeFragment.findNavController().navigate(
                HomeFragmentDirections.actionNavigationHomeToEditBookFragment(
                    id,name
                )
            )
        })




        homeViewModel.books.observe(viewLifecycleOwner, Observer {
            it?.let {

                booksAdapter.submitList(it)
                booksAdapter.getBooks(it)
                //  this.findNavController().navigate(HomeFragmentDirections.actionNavigationHomeToNavigationBook())
            }
        })

        binding.recyclerView.adapter = booksAdapter

        var simpleCallback = object: ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                TODO("Not yet implemented")
            }

            @SuppressLint("UseRequireInsteadOfGet")
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val bookList = homeViewModel.books.value
                val bookId = bookList!![position].id
                var categoryId = bookList[position].id_category
                val bookName = bookList[position].title
                if (categoryId == null){
                    categoryId = -1
                }
                when(direction){
                    ItemTouchHelper.LEFT -> {
                        MaterialDialog(requireActivity()).show {
                            message(text = "Vymazat?")
                            positiveButton(text = "Ano") { dialog ->
                                homeViewModel.deleteBookById(
                                    bookList!![position]
                                )
                                dismiss()

                            }
                            negativeButton(text = "Nie") { dialog ->
                                dismiss()
                                val ft: FragmentTransaction =
                                    this@HomeFragment.fragmentManager!!.beginTransaction()
                                ft.detach(this@HomeFragment).attach(this@HomeFragment).commit()

                            }
                        }
                    }
                    ItemTouchHelper.RIGHT -> this@HomeFragment.findNavController().navigate(
                        HomeFragmentDirections.actionNavigationHomeToEditBookFragment(
                            bookId,bookName
                        )
                    )
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {


                RecyclerViewSwipeDecorator.Builder(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                    .addSwipeLeftActionIcon(R.drawable.delete_forever_24)
                    .addSwipeLeftBackgroundColor(
                        ContextCompat.getColor(
                            application,
                            R.color.redColor
                        )
                    )
                    .addSwipeRightActionIcon(R.drawable.edit)
                    .addSwipeRightBackgroundColor(R.color.colorPrimary)
                    .create()
                    .decorate()

                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }

        }

        var itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.home_menu, menu)
        val menuItem = menu.findItem(R.id.app_bar_search)
        val searchView = menuItem.actionView as androidx.appcompat.widget.SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                booksAdapter.myFilter.filter(p0)
                booksAdapter.notifyDataSetChanged()

                return false
            }

        })

        searchView.setOnCloseListener(object : SearchView.OnCloseListener{
            override fun onClose(): Boolean {
                val books = homeViewModel.books.value
                booksAdapter.submitList(books)
                booksAdapter.notifyDataSetChanged()
                return false
            }

        })

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

//    companion object {
//        /**
//         * Use this factory method to create a new instance of
//         * this fragment using the provided parameters.
//         *
//         * @param param1 Parameter 1.
//         * @param param2 Parameter 2.
//         * @return A new instance of fragment BlankFragment.
//         */
//        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            BlankFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }


}