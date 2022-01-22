/**
 *  DBHelper
 *  시스템 내부의 DB를 인스턴스화 시켜주는 클래스
 */
package com.example.jlptcommunity.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context, name: String, factory: SQLiteDatabase.CursorFactory?, version: Int) : SQLiteOpenHelper(context, name, factory, version){
    override fun onCreate(db: SQLiteDatabase?) {
        //assets에 존재하는 JLPT.db를 복사하여 참조할 목적 이므로 작성할 내용이 없음
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //assets에 존재하는 JLPT.db를 복사하여 참조할 목적 이므로 작성할 내용이 없음
    }
}