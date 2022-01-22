/**
 *  WordsEntity
 *  JLPT.db의 words테이블의 구성된 컬럼들로, 데이터를 쿼리할 경우 사용되는 데이터 클래스
 */
package com.example.jlptcommunity.database

data class WordsEntity (val level: String, val word: String, val mean: String, val pronunciation: String)