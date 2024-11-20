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

    // Map to translate activity class name to a custom title
    private val activityTitleMap = mapOf(
        "MainActivity" to "Home",
        "LoginActivity" to "Login",
        "RegisterActivity" to "Register",
        "MyPokemonActivity" to "My Pokémon"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Inflate the layout for this fragment
        binding = ActivityNavFragmentBinding.inflate(inflater, container, false)

        // Reference the burger menu button
        burgerMenuButton = binding.burgerMenu

        // Set the burger menu button click listener
        burgerMenuButton.setOnClickListener {
            showMenu()
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        // Get the current activity name and use the map to set the appropriate title
        val currentActivityName = requireActivity()::class.java.simpleName
        val title = activityTitleMap[currentActivityName] ?: currentActivityName // Default to class name if not found
        binding.titleText.text = title
    }

    private fun showMenu() {
        // Create a PopupMenu
        val popupMenu = PopupMenu(requireContext(), burgerMenuButton)
        val menu = popupMenu.menu

        // Add different menu options based on user login status
        menu.add("Home").setOnMenuItemClickListener {
            startActivity(Intent(requireContext(), MainActivity::class.java))
            true
        }
        if (auth.currentUser == null) {
            // If user is not logged in, show "Login" and "Register" options
            menu.add("Login").setOnMenuItemClickListener {
                startActivity(Intent(requireContext(), LoginActivity::class.java))
                true
            }
            menu.add("Register").setOnMenuItemClickListener {
                startActivity(Intent(requireContext(), RegisterActivity::class.java))
                true
            }
        } else {
            // If user is logged in, show "My Pokémon" and "Logout" options
            menu.add("My Pokémon").setOnMenuItemClickListener {
                startActivity(Intent(requireContext(), MainActivity::class.java))
                true
            }
            menu.add("Logout").setOnMenuItemClickListener {
                auth.signOut()
                startActivity(Intent(requireContext(), MainActivity::class.java))
                true
            }
        }
        // Show the menu
        popupMenu.show()
    }
}

