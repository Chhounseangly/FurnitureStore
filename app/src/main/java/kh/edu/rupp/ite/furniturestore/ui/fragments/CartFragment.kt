package kh.edu.rupp.ite.furniturestore.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kh.edu.rupp.ite.furniturestore.databinding.FragmentCartBinding
import kh.edu.rupp.ite.furniturestore.databinding.FragmentHomeBinding

class CartFragment : Fragment() {

    private lateinit var fragmentCartBinding: FragmentCartBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentCartBinding = FragmentCartBinding.inflate(inflater, container, false)
        return fragmentCartBinding.root
    }
}