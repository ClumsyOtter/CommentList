package com.example.comment

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.comment.adapter.CommentAdapter
import com.example.comment.api.CommentApi
import com.example.comment.model.Comment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    
    private lateinit var recyclerViewComments: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var commentAdapter: CommentAdapter
    
    private var currentPage = 1
    private var hasMore = true
    private val pageSize = 10
    
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initViews()
        setupRecyclerView()
        loadComments(1)
    }
    
    private fun initViews() {
        recyclerViewComments = findViewById(R.id.recyclerViewComments)
        progressBar = findViewById(R.id.progressBar)
    }
    
    private fun setupRecyclerView() {
        commentAdapter = CommentAdapter()
        recyclerViewComments.layoutManager = LinearLayoutManager(this)
        recyclerViewComments.adapter = commentAdapter
        
        // 设置一级评论加载更多
        commentAdapter.loadMoreModule.setOnLoadMoreListener {
            if (hasMore) {
                loadComments(currentPage + 1)
            } else {
                commentAdapter.loadMoreModule.loadMoreEnd()
            }
        }
        
        // 设置二级评论加载更多回调
        commentAdapter.setOnLoadMoreSubCommentsListener { comment, page ->
            loadSubComments(comment, page)
        }
    }
    
    /**
     * 加载一级评论列表
     */
    private fun loadComments(page: Int) {
        if (page == 1) {
            progressBar.visibility = View.VISIBLE
        }
        
        scope.launch {
            CommentApi.getComments(page, pageSize).fold(
                onSuccess = { (comments, hasMoreData) ->
                    progressBar.visibility = View.GONE
                    
                    if (page == 1) {
                        // 第一页，替换数据
                        commentAdapter.setList(comments)
                        commentAdapter.loadMoreModule.isEnableLoadMore = true
                    } else {
                        // 加载更多，追加数据
                        commentAdapter.addData(comments)
                        if (hasMoreData) {
                            commentAdapter.loadMoreModule.loadMoreComplete()
                        } else {
                            commentAdapter.loadMoreModule.loadMoreEnd()
                        }
                    }
                    
                    currentPage = page
                    hasMore = hasMoreData
                },
                onFailure = { error ->
                    progressBar.visibility = View.GONE
                    commentAdapter.loadMoreModule.loadMoreFail()
                    error.printStackTrace()
                }
            )
        }
    }
    
    /**
     * 加载二级评论列表
     */
    private fun loadSubComments(comment: Comment, page: Int) {
        scope.launch {
            CommentApi.getSubComments(comment.id, page, 5).fold(
                onSuccess = { (subComments, hasMoreData) ->
                    commentAdapter.updateSubComments(comment, subComments, page, hasMoreData)
                },
                onFailure = { error ->
                    error.printStackTrace()
                    // 加载失败，可以显示错误提示
                }
            )
        }
    }
}
