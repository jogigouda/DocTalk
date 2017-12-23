package com.doctalk.doctalk.model

import org.json.JSONObject


/**
 * Created by JOGI-PC on 12/23/2017.
 * Model class holding user detail
 */

class User() {

    lateinit var login: String
    lateinit var id: String
    lateinit var avatar_url: String
    //    lateinit var gravatar_id: String
//    lateinit var url: String
//    lateinit var html_url: String
//    lateinit var followers_url: String
//    lateinit var following_url: String
//    lateinit var gists_url: String
//    lateinit var starred_url: String
//    lateinit var subscriptions_url: String
//    lateinit var organizations_url: String
//    lateinit var repos_url: String
//    lateinit var events_url: String
//    lateinit var received_events_url: String
//    lateinit var type: String
//    var site_admin: Boolean = false
    var score: Double = 0.0


    constructor(jsonUser: JSONObject) : this() {
        try {
            login = jsonUser.optString("login")
            id = jsonUser.optString("id")
            avatar_url = jsonUser.optString("avatar_url")
            score = jsonUser.optDouble("score")
        } catch (exception: Exception) {
            exception.printStackTrace()

        }

    }
}