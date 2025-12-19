package com.example.comment.model

/**
 * 评论数据模型
 */
data class Comment(
    val id: String,              // 评论ID
    val content: String,         // 评论内容
    val userName: String,        // 用户名
    val avatar: String? = null, // 头像URL
    val replay: Int = 0,         // 回复数量，大于0表示有二级评论
    val createTime: String,      // 创建时间
    var isExpanded: Boolean = false, // 是否展开二级评论
    var subComments: MutableList<SubComment> = mutableListOf(), // 二级评论列表
    var currentPage: Int = 1,    // 当前页码
    var hasMore: Boolean = true  // 是否还有更多二级评论
)

/**
 * 二级评论数据模型
 */
data class SubComment(
    val id: String,              // 评论ID
    val content: String,         // 评论内容
    val userName: String,        // 用户名
    val avatar: String? = null,  // 头像URL
    val replyTo: String? = null, // 回复的用户名（可选）
    val createTime: String       // 创建时间
)

