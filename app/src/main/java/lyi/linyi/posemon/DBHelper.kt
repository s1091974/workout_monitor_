package lyi.linyi.posemon

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, "Userdata", null, 2) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("create table Userdata (name TEXT, email TEXT primary key, password TEXT, gender TEXT, height REAL, weight REAL)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (newVersion > oldVersion) {
            // 升级数据库表结构
            db?.execSQL("ALTER TABLE Userdata ADD COLUMN name TEXT")
            db?.execSQL("ALTER TABLE Userdata ADD COLUMN gender TEXT")
            db?.execSQL("ALTER TABLE Userdata ADD COLUMN height REAL")
            db?.execSQL("ALTER TABLE Userdata ADD COLUMN weight REAL")
        }
    }

    fun insertdata(email: String, password: String, name: String, gender: String, height: Double, weight: Double): Boolean {
        val db = this.writableDatabase

        // 首先检查电子邮件地址是否已存在
        val checkQuery = "SELECT * FROM Userdata WHERE email='$email'"
        val cursor = db.rawQuery(checkQuery, null)

        if (cursor.count > 0) {
            // 电子邮件地址已存在，执行更新密码操作
            cursor.close()
            return updateUserData(email, password, name, gender, height, weight)
        } else {
            // 电子邮件地址不存在，执行插入操作
            val cv = ContentValues()
            cv.put("email", email)
            cv.put("password", password)
            cv.put("name", name)
            cv.put("gender", gender)
            cv.put("height", height)
            cv.put("weight", weight)
            val result = db.insert("Userdata", null, cv)
            cursor.close()

            if (result == -1L) {
                return false
            }
            return true
        }
    }

    fun checkuserpass(email: String, password: String): Boolean {
        val db = this.writableDatabase
        val query = "select * from Userdata where email= '$email' and password= '$password'"
        val cursor = db.rawQuery(query, null)
        if (cursor.count <= 0) {
            cursor.close()
            return false
        }
        cursor.close()
        return true
    }

//    fun updateUserPassword(email: String, password: String): Boolean {
//        val db = this.writableDatabase
//        val values = ContentValues()
//        values.put("password", password)
//
//        val updated = db.update("Userdata", values, "email=?", arrayOf(email))
//        db.close()
//
//        return updated > 0
//    }

    fun updateUserData(email: String, password: String, name: String, gender: String, height: Double, weight: Double): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put("password", password)
        values.put("name", name)
        values.put("gender", gender)
        values.put("height", height)
        values.put("weight", weight)

        val updated = db.update("Userdata", values, "email=?", arrayOf(email))
        db.close()

        return updated > 0
    }
}
