package easv.dal.connectionManagement;

import easv.exception.RateException;

import java.sql.Connection;

public interface IConnection {
    Connection getConnection() throws RateException;

}
