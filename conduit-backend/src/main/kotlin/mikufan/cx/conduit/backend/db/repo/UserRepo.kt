package mikufan.cx.conduit.backend.db.repo

import mikufan.cx.conduit.backend.db.Users
import mikufan.cx.conduit.common.UserDto
import mikufan.cx.conduit.common.UserRegisterDto
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertReturning
import org.jetbrains.exposed.sql.selectAll

class UserRepo {

  fun getById(id: Int): UserDto? = Users
    .selectAll().where { Users.id eq id }
    .firstOrNull()
    ?.toUserDto()

  fun getByEmail(email: String): UserDto? = Users
    .selectAll().where { Users.email eq email }
    .firstOrNull()
    ?.toUserDto()

  fun getByUsername(username: String): UserDto? = Users
    .selectAll().where { Users.username eq username }
    .firstOrNull()
    ?.toUserDto()

  fun insert(UserRegisterDto: UserRegisterDto): UserDto? = Users.insertReturning {
    it[email] = UserRegisterDto.email
    it[username] = UserRegisterDto.username
    it[password] = UserRegisterDto.password
    it[bio] = ""
    it[image] = null
  }
    .firstOrNull()
    ?.toUserDto()

  fun delete(id: Int): Boolean = Users.deleteWhere { with(it) { Users.id eq id } } > 0

  private fun ResultRow.toUserDto() = UserDto(
    email = this[Users.email],
    username = this[Users.username],
    bio = this[Users.bio],
    image = this[Users.image],
    token = null
  )
}