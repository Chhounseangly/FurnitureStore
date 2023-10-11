package kh.edu.rupp.ite.furniturestore.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kh.edu.rupp.ite.furniturestore.databinding.FragmentProfileBinding

class ProfileFragment: Fragment() {
    private lateinit var fragmentProfileBinding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        fragmentProfileBinding = FragmentProfileBinding.inflate(inflater, container, false)
        return fragmentProfileBinding.root
    }


}