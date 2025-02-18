package com.atteneri.tictactoeonline

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

object GameData {
    private var _gameModel : MutableLiveData<GameModel> = MutableLiveData()
    var gameModel : LiveData<GameModel> = _gameModel
    var myID = ""
    var repeated = false

    fun saveGameModel(model : GameModel) {
        if(model.gameId != "-1") {
            val docRef = Firebase.firestore.collection("games")
                .document(model.gameId)

            // check if document already exists
            docRef.get()
                .addOnSuccessListener {
                    repeated = true
                }
            if(!repeated) {
                docRef.set(model)
            }
            repeated = false
        }
        _gameModel.postValue(model)
    }

    fun fetchGameModel() {
        gameModel.value?.apply {
            if(gameId != "-1") {
                Firebase.firestore.collection("games")
                    .document(gameId)
                    .addSnapshotListener { value, error ->
                        val model = value?.toObject(GameModel::class.java)
                        _gameModel.postValue(model)
                        error?.printStackTrace()
                    }
            }

        }
    }
}