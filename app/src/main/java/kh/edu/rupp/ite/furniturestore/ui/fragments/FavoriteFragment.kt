package kh.edu.rupp.ite.furniturestore.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kh.edu.rupp.ite.furniturestore.databinding.FragmentFavoriteBinding

class FavoriteFragment: Fragment() {

    private lateinit var fragmentFavoriteBinding: FragmentFavoriteBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentFavoriteBinding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return fragmentFavoriteBinding.root
    }

}