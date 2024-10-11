package com.example.teams.model

import com.google.firebase.Timestamp

data class Post(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val authorId: String = "",
    val communityId: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val upvotes: Int = 0,
    val downvotes: Int = 0,
    val commentCount: Int = 0
)