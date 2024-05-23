package com.avs.supersapiens.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.avs.supersapiens.R
import com.avs.supersapiens.adapters.GameCategoryAdapter
import com.avs.supersapiens.databinding.FragmentHomeBinding
import com.avs.supersapiens.ui.viewmodels.HomeViewModel

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = GameCategoryAdapter(emptyList()) { category ->
            val action = HomeFragmentDirections.actionHomeFragmentToGameListFragment(category.category)
            findNavController().navigate(action)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter

        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            adapter.updateCategories(categories)
        }
    }
}
