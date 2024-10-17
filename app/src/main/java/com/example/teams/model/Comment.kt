package com.example.teams.model

import com.google.firebase.Timestamp

data class Comment(
    val id: String = "",
    val postId: String = "",
    val authorId: String = "",
    val content: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val upvotes: Int = 0,
    val downvotes: Int = 0
)