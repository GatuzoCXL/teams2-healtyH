package com.example.teams.ui.theme.createpost

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.teams.databinding.FragmentCreatePostBinding
import com.example.teams.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CreatePostFragment : Fragment() {

    private var _binding: FragmentCreatePostBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCreatePostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonCreatePost.setOnClickListener {
            createPost()
        }
    }

    private fun createPost() {
        val title = binding.editTextTitle.text.toString().trim()
        val content = binding.editTextContent.text.toString().trim()
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (title.isNotEmpty() && content.isNotEmpty() && currentUser != null) {
            val post = Post(
                title = title,
                content = content,
                authorId = currentUser.uid,
                communityId = "" //nota de implementar una forma de seleccionar una comunidad.
            )

            FirebaseFirestore.getInstance().collection("posts")
                .add(post)
                .addOnSuccessListener {
                    Toast.makeText(context, "Post creado correctamente", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Error al crear tu huevada", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
