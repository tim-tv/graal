package com.github.titovart.spring.service.secure.server.token.jdbc

import com.github.titovart.spring.service.secure.server.token.AccessToken
import com.github.titovart.spring.service.secure.server.token.AccessTokenDto
import com.github.titovart.spring.service.secure.server.token.AccessTokenStore
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException
import java.sql.Statement
import javax.sql.DataSource


class JdbcAccessTokenStore : AccessTokenStore {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var dataSource: DataSource

    override fun findTokenByAppId(appId: String): AccessToken? {

        var token: AccessToken? = null
        var conn: Connection? = null
        var statement: Statement? = null
        try {
            conn = dataSource.connection
            statement = conn.createStatement()

            val query = "select * from $TOKEN_TABLE_NAME where app_id = '$appId'"
            val rs = statement.executeQuery(query)

            token = if (rs.next()) {
                val app = rs.getString("app_id")
                val value = rs.getString("token")
                val expiresAt = rs.getTimestamp("expires_at")
                AccessTokenDto(app, value, expiresAt)
            } else {
                null
            }

        } catch (exc: SQLException) {
            logger.error("[getTokenByAppId($appId)] => ", exc)
        } finally {
            statement?.close()
            conn?.close()
        }

        return token
    }

    override fun findTokenByValue(tokenValue: String): AccessToken? {
        var token: AccessToken? = null
        var conn: Connection? = null
        var statement: Statement? = null
        try {
            conn = dataSource.connection
            statement = conn.createStatement()

            val query = "select * from $TOKEN_TABLE_NAME where token = '$tokenValue'"
            val rs = statement.executeQuery(query)

            token = if (rs.next()) {
                val app = rs.getString("app_id")
                val value = rs.getString("token")
                val expiresAt = rs.getTimestamp("expires_at")
                AccessTokenDto(app, value, expiresAt)
            } else {
                null
            }

        } catch (exc: SQLException) {
            logger.error("[getTokenByValue($tokenValue)] => ", exc)
        } finally {
            statement?.close()
            conn?.close()
        }

        return token
    }

    override fun saveToken(token: AccessToken) {

        var conn: Connection? = null
        var statement: PreparedStatement? = null
        try {
            conn = dataSource.connection

            val query = "insert into $TOKEN_TABLE_NAME values(?, ?, ?)"
            statement = conn.prepareStatement(query)

            statement.setString(1, token.getAppId())
            statement.setString(2, token.getValue())
            statement.setTimestamp(3, token.getExpiration())

            statement.executeUpdate()
        } catch (exc: SQLException) {
            logger.error("[saveToken($token)] => ", exc)
        } finally {
            statement?.close()
            conn?.close()
        }
    }

    override fun removeToken(appId: String) {

        var conn: Connection? = null
        var statement: Statement? = null
        try {
            conn = dataSource.connection
            statement = conn.createStatement()

            val query = "delete from $TOKEN_TABLE_NAME where app_id = '$appId'"
            statement.executeUpdate(query)

        } catch (exc: SQLException) {
            logger.error("[removeToken($appId)] => ", exc)
        } finally {
            statement?.close()
            conn?.close()
        }
    }

    companion object {
        val TOKEN_TABLE_NAME = "service_secure_access_token"
    }

}