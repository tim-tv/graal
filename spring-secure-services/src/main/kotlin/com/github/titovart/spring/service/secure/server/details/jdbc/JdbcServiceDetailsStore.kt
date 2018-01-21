package com.github.titovart.spring.service.secure.server.details.jdbc

import com.github.titovart.spring.service.secure.server.details.ServiceDetails
import com.github.titovart.spring.service.secure.server.details.ServiceDetailsDto
import com.github.titovart.spring.service.secure.server.details.ServiceDetailsStore
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException
import java.sql.Statement
import javax.sql.DataSource


class JdbcServiceDetailsStore : ServiceDetailsStore {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var dataSource: DataSource


    override fun findDetailsByAppId(appId: String): ServiceDetails? {

        var details: ServiceDetails? = null
        var conn: Connection? = null
        var statement: Statement? = null
        try {
            conn = dataSource.connection
            statement = conn.createStatement()

            val query = "select * from $SERVICE_DETAILS_TABLE where app_id = '$appId'"
            val rs = statement.executeQuery(query)

            details = if (rs.next()) {
                val id = rs.getString("app_id")
                val secret = rs.getString("app_secret")
                ServiceDetailsDto(id, secret)
            } else {
                null
            }

        } catch (exc: SQLException) {
            logger.error("[findDetailsByAppId($appId)] => ", exc)
        } finally {
            statement?.close()
            conn?.close()
        }

        return details
    }

    override fun save(details: ServiceDetails) {

        var conn: Connection? = null
        var statement: PreparedStatement? = null
        try {
            conn = dataSource.connection

            val query = "insert into $SERVICE_DETAILS_TABLE values(?, ?)"
            statement = conn.prepareStatement(query)

            statement.setString(1, details.getAppId())
            statement.setString(2, details.getAppSecret())

            statement.executeUpdate()
        } catch (exc: SQLException) {
            logger.error("[save($details})] => ", exc)
        } finally {
            statement?.close()
            conn?.close()
        }
    }

    override fun remove(appId: String) {

        var conn: Connection? = null
        var statement: Statement? = null
        try {
            conn = dataSource.connection
            statement = conn.createStatement()

            val query = "delete from $SERVICE_DETAILS_TABLE where app_id = '$appId'"
            statement.executeUpdate(query)

        } catch (exc: SQLException) {
            logger.error("[remove($appId)] => ", exc)
        } finally {
            statement?.close()
            conn?.close()
        }
    }

    companion object {
        val SERVICE_DETAILS_TABLE = "service_secure_details"
    }
}