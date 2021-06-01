package com.marianpusk.knihy.ui.home

import android.graphics.Canvas
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.marianpusk.carapplicaiton.database.bookDatabase
import com.marianpusk.knihy.R
import com.marianpusk.knihy.databinding.FragmentHomeBinding
import com.marianpusk.knihy.ui.book.EditBookFragment
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator


class HomeFragment() : Fragment() {

    lateinit var booksAdapter: RecycleAdapter

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val binding = DataBindingUtil.inflate<FragmentHomeBinding>(inflater, R.layout.fragment_home, container, false)
        val application = requireNotNull(this.activity).application
        val datasource = bookDatabase.getInstance(application).books
        val viewModelFactory = HomeViewModelFactory(datasource,application)
        Toast.makeText(application,"View created",Toast.LENGTH_SHORT).show()
        homeViewModel =
                ViewModelProviders.of(this,viewModelFactory).get(HomeViewModel::class.java)
        binding.setLifecycleOwner(this)

        booksAdapter = RecycleAdapter(BookListener {
            id -> this@HomeFragment.findNavController().navigate(HomeFragmentDirections.actionNavigationHomeToEditBookFragment(id))
        })


        homeViewModel.books.observe(viewLifecycleOwner, Observer {
            it?.let {
                booksAdapter.submitList(it)
                booksAdapter.getBooks(it)
              //  this.findNavController().navigate(HomeFragmentDirections.actionNavigationHomeToNavigationBook())
            }
        })

        binding.recyclerView.adapter = booksAdapter

        var simpleCallback = object: ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                TODO("Not yet implemented")
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val bookList = homeViewModel.books.value
                val bookId = bookList!![position].id
                var categoryId = bookList[position].id_category
                if (categoryId == null){
                    categoryId = -1
                }
                when(direction){
                    ItemTouchHelper.LEFT -> homeViewModel.deleteBookById(bookList!![position])
                    ItemTouchHelper.RIGHT -> this@HomeFragment.findNavController().navigate(HomeFragmentDirections.actionNavigationHomeToEditBookFragment(bookId))
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
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(application,R.color.redColor))
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

        inflater.inflate(R.menu.home_menu,menu)
        val menuItem = menu.findItem(R.id.app_bar_search)
        val searchView = menuItem.actionView as SearchView

        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                booksAdapter.myFilter.filter(p0)
                return false
            }

        })

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onDestroyView() {
        Toast.makeText(requireActivity(),"view destroyed",Toast.LENGTH_SHORT).show()
        super.onDestroyView()
    }


}