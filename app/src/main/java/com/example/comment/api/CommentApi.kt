package com.example.comment.api

import com.example.comment.model.Comment
import com.example.comment.model.SubComment
import kotlinx.coroutines.delay

/**
 * 评论 API 服务（模拟）
 */
object CommentApi {
    
    /**
     * 获取一级评论列表（分页）
     */
    suspend fun getComments(page: Int, pageSize: Int = 10): Result<Pair<List<Comment>, Boolean>> {
        // 模拟网络延迟
        delay(1000)
        
        return try {
            val comments = mutableListOf<Comment>()
            val startIndex = (page - 1) * pageSize
            val endIndex = startIndex + pageSize
            
            // 模拟数据：总共30条评论
            val totalComments = 30
            val hasMore = endIndex < totalComments
            
            for (i in startIndex until minOf(endIndex, totalComments)) {
                comments.add(
                    Comment(
                        id = "comment_$i",
                        content = "这是一级评论内容 ${i + 1}，包含一些测试文本。",
                        userName = "用户${i + 1}",
                        replay = if (i % 3 == 0) (i % 5 + 1) else 0, // 每3条评论有一条有回复
                        createTime = "2024-01-${String.format("%02d", (i % 28) + 1)} 10:${String.format("%02d", i % 60)}"
                    )
                )
            }
            
            Result.success(Pair(comments, hasMore))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 获取二级评论列表（分页）
     */
    suspend fun getSubComments(commentId: String, page: Int, pageSize: Int = 5): Result<Pair<List<SubComment>, Boolean>> {
        // 模拟网络延迟
        delay(800)
        
        return try {
            val subComments = mutableListOf<SubComment>()
            val startIndex = (page - 1) * pageSize
            val endIndex = startIndex + pageSize
            
            // 模拟数据：每条评论最多15条回复
            val totalSubComments = 15
            val hasMore = endIndex < totalSubComments
            
            for (i in startIndex until minOf(endIndex, totalSubComments)) {
                subComments.add(
                    SubComment(
                        id = "sub_comment_${commentId}_$i",
                        content = "这是二级评论内容 ${i + 1}，回复一级评论。",
                        userName = "回复用户${i + 1}",
                        replyTo = if (i % 2 == 0) "用户${i}" else null,
                        createTime = "2024-01-${String.format("%02d", (i % 28) + 1)} 11:${String.format("%02d", i % 60)}"
                    )
                )
            }
            
            Result.success(Pair(subComments, hasMore))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

