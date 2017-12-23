package com.doctalk.doctalk.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.doctalk.doctalk.R
import com.doctalk.doctalk.model.User
import kotlinx.android.synthetic.main.user_item.view.*

/**
 * Created by JOGI-PC on 12/23/2017.
 * UserAdapter
 */
class UsersAdapter() : RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {
    internal var usersList: List<User>? = null

    constructor(usersList: List<User>) : this() {
        this.usersList = usersList
    }

    override fun getItemCount(): Int {
        return usersList!!.size
    }

    override fun onBindViewHolder(holder: UserViewHolder?, position: Int) {
        holder!!.bindItems(usersList!!.get(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): UserViewHolder {
        val v: View = LayoutInflater.from(parent!!.getContext()).inflate(R.layout.user_item, null)
        return UserViewHolder(v)
    }


    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(user: User) {
            itemView.userNme.text = "User Name:" + user.login
            itemView.userId.text = "Id:" + user.id
            itemView.score.text = "Score:" + user.score.toString()
            Glide.with(itemView).load(user.avatar_url).into(itemView.userImage)
        }

    }
}