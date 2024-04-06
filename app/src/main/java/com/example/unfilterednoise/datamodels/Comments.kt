package com.example.unfilterednoise.datamodels

data class Comments(
    val id:String,
    val userName:String,
    val userRole:String,
    val userPFP:String,
    val comment : String,
    val userId : String,
    val commentUpVote : String,
    val commentDownVote : String
)

