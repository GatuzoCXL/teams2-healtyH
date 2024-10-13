package com.example.teams.ui.theme.postdetail

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.teams.R
import com.example.teams.databinding.FragmentPostDetailBinding
import com.example.teams.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PostDetailFragment : Fragment() {

    private var _binding: FragmentPostDetailBinding? = null
    private val binding get() = _binding!!
    private val args: PostDetailFragmentArgs by navArgs()
    private lateinit var post: Post

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPostDetailBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadPost()
    }

    private fun loadPost() {
        val postId = args.postId //Obtenemos el postId
        Log.d("PostDetailFragment", "Post ID: $postId") // depuraciones obligatorias

        if (postId.isNotEmpty()) {
            FirebaseFirestore.getInstance().collection("posts").document(postId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        post = document.toObject(Post::class.java)!!
                        post = post.copy(id = document.id)//helado misterioso de
                        updateUI()
                    } else {
                        Toast.makeText(context, "Post not found", Toast.LENGTH_SHORT).show()
                        findNavController().navigateUp()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(
                        context,
                        "Error loading post: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    findNavController().navigateUp()
                }
        }   else {
            Toast.makeText(context, "Post ID está vacío", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()

        }
}


    private fun updateUI() {
        binding.textViewTitle.text = post.title
        binding.textViewContent.text = post.content
        binding.textViewAuthor.text = "Posted by u/${post.authorId}"
        binding.textViewVotes.text = (post.upvotes - post.downvotes).toString()
        binding.textViewComments.text = "${post.commentCount} comments"

        //envio de comentarios
        binding.buttonSubmitComment.setOnClickListener {
            submitComment()
        }

        //cargar comentarios
        loadComments()
    }

    private fun submitComment() {
        val commentText = binding.editTextComment.text.toString().trim()
        if (commentText.isNotEmpty()) {
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                val comment = hashMapOf(
                    "postId" to post.id,
                    "authorId" to currentUser.uid,
                    "content" to commentText,
                    "createdAt" to com.google.firebase.Timestamp.now()
                )

                FirebaseFirestore.getInstance().collection("comentarios")
                    .add(comment)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Comentario añadido", Toast.LENGTH_SHORT).show()
                        binding.editTextComment.text.clear()
                        loadComments()
                        //actualizar
                        FirebaseFirestore.getInstance().collection("posts").document(post.id)
                            .update("commentCount", post.commentCount + 1)
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "Error al añadir comentario: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(context, "Debes de estar logeado para poder comentar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadComments() {
        FirebaseFirestore.getInstance().collection("comentarios")
            .whereEqualTo("postId", post.id)
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                val commentsList = documents.map { it.data }
                //recordatorio de implementar una ui para los comentarios
                //como el  recyclerview.
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Error al cargar comentarios: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_post_detail, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_edit -> {
                editPost()
                true
            }
            R.id.action_delete -> {
                deletePost()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun editPost() {
        // Navigate to edit post fragment
        val action = PostDetailFragmentDirections.actionPostDetailFragmentToEditPostFragment(post.id)
        findNavController().navigate(action)
    }

    private fun deletePost() {
        FirebaseFirestore.getInstance().collection("posts").document(post.id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(context, "Post eliminado", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error al intentar eliminar el post: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}