package com.example.teams.ui.theme.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.teams.databinding.FragmentHomeBinding
import com.example.teams.model.Post
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var postAdapter: PostAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postAdapter = PostAdapter()
        binding.recyclerViewPosts.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = postAdapter
        }

        loadPosts()
    }

    private fun loadPosts() {
        FirebaseFirestore.getInstance().collection("posts")
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(20)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    //Manejar el error
                    return@addSnapshotListener
                }

                val posts = snapshot?.toObjects(Post::class.java) ?: listOf()
                postAdapter.submitList(posts)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}