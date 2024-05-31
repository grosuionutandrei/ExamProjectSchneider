package easv.dal.connectionManagement;

import easv.exception.RateException;

import java.util.HashMap;

public class DatabaseConnectionFactory {
    private static  final HashMap <DatabaseType,IConnection> databaseConnections =  new HashMap<>();

    public enum DatabaseType{
        SCHOOL_MSSQL;
    }


    /**this method retrieves the connection based on the required type
     * can be easily extended to  return  more connections in the future*/
    public static IConnection getConnection(DatabaseType databaseType) throws RateException {
        if(databaseConnections.containsKey(databaseType)){
            return databaseConnections.get(databaseType);
        }
        IConnection connection = new MSSQLConnection();
        databaseConnections.put(databaseType,connection);
        return connection;
    }
}
