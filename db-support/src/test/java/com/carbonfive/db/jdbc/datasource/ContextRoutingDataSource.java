package com.carbonfive.db.jdbc.datasource;

import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

public class ContextRoutingDataSource extends RoutingDataSource
{
    private ContextServiceImpl contextService;

    public ContextRoutingDataSource(ContextServiceImpl contextService)
    {
        this.contextService = contextService;
    }

    public ContextRoutingDataSource(DataSourceFactory dataSourceFactory, ContextServiceImpl contextService)
    {
        super(dataSourceFactory);
        this.contextService = contextService;
    }

    protected String determineCurrentDataSourceKey()
    {
        return contextService.currentClient();
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
