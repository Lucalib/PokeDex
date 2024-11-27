package com.lucasliberatore.pokedex

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.lucasliberatore.pokedex.databinding.ActivityNavFragmentBinding

class NavFragment : Fragment(R.layout.activity_nav_fragment) {

    private lateinit var auth: FirebaseAuth
    private lateinit var burgerMenuButton: ImageButton
    lateinit var binding: ActivityNavFragmentBinding

    private val activityTitleMap = mapOf(
        "MainActivity" to "Home",
        "LoginActivity" to "Login",
        "RegisterActivity" to "Register",
        "MyPokemonActivity" to "My Pokémon",
        "AboutActivity" to "About"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = FirebaseAuth.getInstance()
        binding = ActivityNavFragmentBinding.inflate(inflater, container, false)
        burgerMenuButton = binding.burgerMenu
        burgerMenuButton.setOnClickListener {
            showMenu()
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        val currentActivityName = requireActivity()::class.java.simpleName
        val title = activityTitleMap[currentActivityName] ?: currentActivityName
        binding.titleText.text = title
    }

    private fun showMenu() {
        val popupMenu = PopupMenu(requireContext(), burgerMenuButton)
        val menu = popupMenu.menu

        menu.add("Home").setOnMenuItemClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(requireActivity()).toBundle())
            true
        }

        menu.add("About").setOnMenuItemClickListener {
            val intent = Intent(requireContext(), AboutActivity::class.java)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(requireActivity()).toBundle())
            true
        }

        if (auth.currentUser == null) {
            menu.add("Login").setOnMenuItemClickListener {
                val intent = Intent(requireContext(), LoginActivity::class.java)
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(requireActivity()).toBundle())
                true
            }
            menu.add("Register").setOnMenuItemClickListener {
                val intent = Intent(requireContext(), RegisterActivity::class.java)
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(requireActivity()).toBundle())
                true
            }
        } else {
            menu.add("My Pokémon").setOnMenuItemClickListener {
                val intent = Intent(requireContext(), MyPokemonActivity::class.java)
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(requireActivity()).toBundle())
                true
            }
            menu.add("Logout").setOnMenuItemClickListener {
                auth.signOut()
                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(requireActivity()).toBundle())
                true
            }
        }
        popupMenu.show()
    }
}