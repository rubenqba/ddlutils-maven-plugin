package org.github.rubenqba.maven.ddlutils;

import org.apache.ddlutils.Platform;
import org.apache.ddlutils.PlatformFactory;
import org.apache.ddlutils.platform.firebird.FirebirdPlatform;
import org.firebirdsql.pool.FBSimpleDataSource;

public class PlatformBuilder {

    private static PlatformBuilder builder;

    public enum DBType {
        FIREBIRD, ORACLE
    }

    private DBType type;
    private String database;
    private String user;
    private String password;

    private PlatformBuilder() {

    }

    public static PlatformBuilder builder() {
        if (builder == null)
            builder = new PlatformBuilder();
        return builder;
    }

    public PlatformBuilder type(DBType type) {
        this.type = type;
        return this;
    }

    public PlatformBuilder databaseName(String name) {
        this.database = name;
        return this;
    }

    public PlatformBuilder user(String user) {
        this.user = user;
        return this;
    }

    public PlatformBuilder password(String password) {
        this.password = password;
        return this;
    }

    public Platform build() {
        Platform platform = null;
        switch (type) {
        case FIREBIRD:
            platform = PlatformFactory.createNewPlatformInstance(FirebirdPlatform.JDBC_DRIVER,
                    String.format("jdbc:%s:%s", FirebirdPlatform.JDBC_SUBPROTOCOL, database));

            FBSimpleDataSource ds = new FBSimpleDataSource();
            ds.setDatabase(database);
            ds.setUserName(user);
            ds.setPassword(password);
            platform.setDataSource(ds);
        default:
            break;
        }
        return platform;
    }
}
