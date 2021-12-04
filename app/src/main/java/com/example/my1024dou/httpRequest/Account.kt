//package com.example.drawerdemo.httpRequest
//
//import com.google.gson.Gson
//
//
//class Account {
//    private val uid = "235235"
//    private val userName = "王思聪"
//    private val passWord = "wangsicong"
//    private val telNumber = "15670339118"
//    override fun toString(): String {
//        return "Account(uid='$uid', userName='$userName', passWord='$passWord', telNumber='$telNumber')"
//    }
//}
//
//data class UserResponse(
//    val `data`: Data,
//    val message: String,
//    val status: Int
//)
//
//data class Data(
//    val `data`: DataX
//)
//
//data class DataX(
//    val acatar: String,
//    val description: String,
//    val id: Int,
//    val likeCount: Int,
//    val name: String,
//    val userID: Long
//)
//
//// 书籍列表
//class StudyBook : ArrayList<StudyBookItem>()
//
//data class StudyBookItem(
//    var className: String,
//    var imgStr: String,
//    var progress: String,
//    var titleName: String
//)
//
//data class CaoLiuHome(
//    var url1: String,
//    var url2: String,
//    var url3: String,
//    var app: String,
//    var update: String,
//    var note: String,
//    var appVer: String
//) {
//    override fun toString(): String {
//        return "CaoLiuHome(url1='$url1', url2='$url2', url3='$url3', app='$app', update='$update', note='$note', appVer='$appVer')"
//    }
//}
//
//data class AppInfo(
//    var a: String,
//    var system: String
//)
//
//fun main() {
//    val gson = Gson()
//    // 将jsonStr转成json
//    val user = Account()
//    val toString = user.toString()
//    val fromJson = gson.fromJson<Account>(toString, Account::class.java)
//
//    // 把json转为对象
//    val toJson = gson.toJson(user)
//
//}