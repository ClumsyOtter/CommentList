package com.example.comment.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.comment.R
import com.example.comment.model.Comment
import com.example.comment.model.SubComment

/**
 * 评论列表适配器
 */
class CommentAdapter : BaseQuickAdapter<Comment, BaseViewHolder>(R.layout.item_comment) {

    private var onLoadMoreSubComments: ((Comment, Int) -> Unit)? = null

    init {
        // 设置加载更多监听
        loadMoreModule.setOnLoadMoreListener {
            // 一级评论的加载更多由外部处理
        }
    }

    override fun convert(holder: BaseViewHolder, item: Comment) {
        // 设置评论内容
        holder.setText(R.id.tvUserName, item.userName)
        holder.setText(R.id.tvContent, item.content)
        holder.setText(R.id.tvCreateTime, item.createTime)

        // 展开/收起按钮
        val btnExpand = holder.getView<TextView>(R.id.btnExpand)
        val recyclerViewSubComments = holder.getView<RecyclerView>(R.id.recyclerViewSubComments)

        // 根据 replay 字段判断是否显示展开按钮
        if (item.replay > 0) {
            btnExpand.visibility = View.VISIBLE
            btnExpand.text = if (item.isExpanded) "收起" else "展开 ${item.replay} 条回复"
            
            // 设置点击事件
            btnExpand.setOnClickListener {
                if (!item.isExpanded) {
                    // 展开：显示二级评论列表
                    item.isExpanded = true
                    notifyItemChanged(holder.adapterPosition)
                } else {
                    // 收起
                    item.isExpanded = false
                    notifyItemChanged(holder.adapterPosition)
                }
            }

            // 显示/隐藏二级评论列表
            if (item.isExpanded) {
                recyclerViewSubComments.visibility = View.VISIBLE
                setupSubCommentRecyclerView(recyclerViewSubComments, item)
                // 如果还没有加载过数据，立即加载第一页
                if (item.subComments.isEmpty() && item.hasMore) {
                    onLoadMoreSubComments?.invoke(item, 1)
                }
            } else {
                recyclerViewSubComments.visibility = View.GONE
            }
        } else {
            btnExpand.visibility = View.GONE
            recyclerViewSubComments.visibility = View.GONE
        }
    }

    /**
     * 设置二级评论 RecyclerView
     */
    private fun setupSubCommentRecyclerView(
        recyclerView: RecyclerView,
        comment: Comment
    ) {
        if (recyclerView.adapter == null) {
            val subAdapter = SubCommentAdapter()
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = subAdapter

            // 设置加载更多
            subAdapter.loadMoreModule.setOnLoadMoreListener {
                if (comment.hasMore) {
                    onLoadMoreSubComments?.invoke(comment, comment.currentPage + 1)
                } else {
                    subAdapter.loadMoreModule.loadMoreEnd()
                }
            }
        }

        val subAdapter = recyclerView.adapter as SubCommentAdapter
        subAdapter.setList(comment.subComments)
        
        // 如果还有更多数据，启用加载更多
        if (comment.hasMore && comment.subComments.isNotEmpty()) {
            subAdapter.loadMoreModule.loadMoreComplete()
        } else if (!comment.hasMore) {
            subAdapter.loadMoreModule.loadMoreEnd()
        }
    }

    /**
     * 设置加载二级评论的回调
     */
    fun setOnLoadMoreSubCommentsListener(listener: (Comment, Int) -> Unit) {
        this.onLoadMoreSubComments = listener
    }

    /**
     * 更新评论的二级评论数据
     */
    fun updateSubComments(comment: Comment, newSubComments: List<SubComment>, page: Int, hasMore: Boolean) {
        val position = data.indexOf(comment)
        if (position >= 0) {
            if (page == 1) {
                comment.subComments.clear()
            }
            comment.subComments.addAll(newSubComments)
            comment.currentPage = page
            comment.hasMore = hasMore
            notifyItemChanged(position)
        }
    }
}

/**
 * 二级评论适配器
 */
class SubCommentAdapter : BaseQuickAdapter<SubComment, BaseViewHolder>(R.layout.item_sub_comment) {

    override fun convert(holder: BaseViewHolder, item: SubComment) {
        holder.setText(R.id.tvUserName, item.userName)
        
        // 如果有回复对象，显示回复格式
        val content = if (item.replyTo != null) {
            "回复 ${item.replyTo}: ${item.content}"
        } else {
            item.content
        }
        holder.setText(R.id.tvContent, content)
        holder.setText(R.id.tvCreateTime, item.createTime)
    }
}

