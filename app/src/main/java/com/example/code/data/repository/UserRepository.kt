package com.example.code.data.repository

import com.example.code.data.local.dao.UserDao
import com.example.code.data.local.entity.UserEntity
import com.example.code.utils.PasswordCrypto

class UserRepository(private val userDao: UserDao) {

    suspend fun register(username: String, password: String, nickname: String): Result<UserEntity> {
        return try {
            if (userDao.usernameExists(username)) {
                return Result.failure(Exception("用户名已存在"))
            }
            if (username.length < 2) {
                return Result.failure(Exception("用户名至少2个字符"))
            }
            if (password.length < 6) {
                return Result.failure(Exception("密码至少6位"))
            }
            val user = UserEntity(
                username = username,
                encryptedPassword = PasswordCrypto.encrypt(password),
                nickname = nickname.ifBlank { username }
            )
            val id = userDao.insert(user).toInt()
            Result.success(user.copy(id = id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(username: String, password: String): Result<UserEntity> {
        return try {
            val user = userDao.getByUsername(username)
                ?: return Result.failure(Exception("用户不存在"))
            val decrypted = PasswordCrypto.decrypt(user.encryptedPassword)
            if (decrypted != password) {
                return Result.failure(Exception("密码错误"))
            }
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserById(id: Int): UserEntity? = userDao.getById(id)

    suspend fun updateProfile(user: UserEntity): Result<UserEntity> {
        return try {
            userDao.update(user)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
