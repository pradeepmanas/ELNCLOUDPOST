package com.agaram.eln.primary.service.multitenant;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.agaram.eln.primary.config.DataSourceBasedMultiTenantConnectionProviderImpl;
import com.agaram.eln.primary.config.TenantDataSource;
import com.agaram.eln.primary.model.general.Response;
import com.agaram.eln.primary.model.multitenant.DataSourceConfig;
import com.agaram.eln.primary.repository.multitenant.DataSourceConfigRepository;
import com.agaram.eln.secondary.config.ArchiveDataSourceBasedMultiTenantConnectionProviderImpl;

@Service
public class DatasourceService {
	
	@Autowired
	private Environment env;
	
	@Autowired
    private DataSourceConfigRepository configRepo;
	
	@Autowired
	private DataSourceBasedMultiTenantConnectionProviderImpl dataSourceBasedMultiTenantConnectionProviderImpl;
	
	@Autowired
	private ArchiveDataSourceBasedMultiTenantConnectionProviderImpl archiveDataSourceBasedMultiTenantConnectionProviderImpl;
	
	@Autowired
	TenantDataSource objtenantsource;
	
	public DataSourceConfig Validatetenant(DataSourceConfig Tenantname)
	{
		
		DataSourceConfig objdatasource = configRepo.findByTenantid(Tenantname.getTenantid());
		
		Response objreponse = new Response();
		if(objdatasource != null)
		{
			objreponse.setStatus(true);
			objdatasource.setObjResponse(objreponse);
			
			DataSource datasource = createDataSource(objdatasource.getName(), objdatasource.getUrl(), objdatasource);
	        
//	        Flyway flyway = Flyway.configure().dataSource(datasource).load();
//            flyway.repair();
//            flyway.migrate();
//            try {
//				datasource.getConnection().close();
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//            
//            
//            DataSource archivedatasource =  createDataSource(objdatasource.getArchivename(), objdatasource.getArchiveurl(), objdatasource);
//              
//            Flyway archiveflyway = Flyway.configure().dataSource(archivedatasource).locations("filesystem:./src/main/resources/db/migration_archive").load();
//            archiveflyway.repair();
//            archiveflyway.migrate();
//              
//            try {
//				archivedatasource.getConnection().close();
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
		else
		{
			objreponse.setStatus(false);
			objdatasource = new DataSourceConfig();
			objdatasource.setObjResponse(objreponse);
		}
		
		return objdatasource;
	}
	
	public DataSourceConfig Registertenant(DataSourceConfig Tenantname)
	{
		DataSourceConfig objconfig = configRepo.findByNameAndTenantid(Tenantname.getName().toLowerCase(), Tenantname.getTenantid());
		Response objres = new Response();
		
		if(objconfig != null)
		{
			DataSourceConfig objdata = new DataSourceConfig();
			objres.setStatus(false);
			objres.setInformation("Organisation ID already esixts.");
			objdata.setObjResponse(objres);
			return objdata;
		}
		
		Tenantname.setName(Tenantname.getName().toLowerCase());
		Tenantname.setDriverClassName(env.getProperty("app.datasource.eln.driverClassName"));
		Tenantname.setUrl(gettenanturlDataBasename(Tenantname.getName()));
		Tenantname.setInitialize(true);
		Tenantname.setUsername(env.getProperty("app.datasource.eln.username"));
		Tenantname.setPassword(env.getProperty("app.datasource.eln.password"));
		Tenantname.setArchivename(Tenantname.getName().toLowerCase()+"archive");
		Tenantname.setArchiveurl(gettenanturlDataBasename(Tenantname.getName().toLowerCase()+"archive"));
		objres.setStatus(true);
		Tenantname.setObjResponse(objres);
		
		configRepo.save(Tenantname);
		
		//createDatabase(env.getProperty("app.datasource.eln.url"), Tenantname.getName(), Tenantname);
	
		return Tenantname;
	}
	
	public boolean createDatabase(String url, String databasename, DataSourceConfig config)
	{
		String user = env.getProperty("app.datasource.eln.username");
        String password = env.getProperty("app.datasource.eln.password");

	        try (Connection con = DriverManager.getConnection(url, user, password);
	                Statement st = con.createStatement();
	        		 
	                ResultSet rs = st.executeQuery("SELECT VERSION()")) {
	        	st.execute("CREATE DATABASE "+databasename);
	        	st.execute("CREATE DATABASE "+databasename+"archive");
        		con.commit();
	            if (rs.next()) {
	                System.out.println(rs.getString(1));
	            }

	        } catch (SQLException ex) {
	        
//	            Logger lgr = Logger.getLogger(JavaPostgreSqlVersion.class.getName());
//	            lgr.log(Level.SEVERE, ex.getMessage(), ex);
	        }
	        
	        DataSource datasource = createDataSource(databasename,config.getUrl(), config);
	        objtenantsource.addDataSource(datasource, databasename);
	        dataSourceBasedMultiTenantConnectionProviderImpl.addDataSource(datasource, databasename);
//	        Flyway flyway = Flyway.configure().dataSource(datasource).load();
//            flyway.repair();
//            flyway.migrate();
//            
            
              
            DataSource archivedatasource =  createDataSource(databasename+"archive", config.getArchiveurl(), config);
            objtenantsource.addarchiveDataSource(archivedatasource, databasename+"archive");
            archiveDataSourceBasedMultiTenantConnectionProviderImpl.addDataSource(archivedatasource, databasename+"archive");
//            Flyway archiveflyway = Flyway.configure().dataSource(archivedatasource).locations("filesystem:./src/main/resources/db/migration_archive").load();
//            archiveflyway.repair();
//            archiveflyway.migrate();
              
	        return true;
	}
	
	private DataSource createDataSource(String name, String url, DataSourceConfig config) {
        if (config != null) {
            DataSourceBuilder factory = DataSourceBuilder
                    .create().driverClassName(config.getDriverClassName())
                    .username(config.getUsername())
                    .password(config.getPassword())
                    .url(url);
            DataSource ds = factory.build();     
            return ds;
        }
        return null;
    } 
	
	private String gettenanturlDataBasename(String tenantDatabase)
	{
		String url = env.getProperty("app.datasource.eln.url");
		String tenanturl = "";
		String[] arrremoveappname = url.split("\\?"); 
		if(arrremoveappname != null && arrremoveappname.length >0)
		{
			String urlvalue = arrremoveappname[0];
			String[] arrurl = urlvalue.split("\\/"); 
			if(arrurl != null && arrurl.length >0)
			{
				arrurl[arrurl.length-1] = tenantDatabase;
				for(int i=0; i<arrurl.length; i++)
				{
					if(i != 0)
					{
						tenanturl += "/";
					}
					tenanturl += arrurl[i];
				}
				
				tenanturl += "?"+arrremoveappname[arrremoveappname.length -1];
			}
		}
		return tenanturl;
	}

}
