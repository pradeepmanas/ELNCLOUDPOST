package com.agaram.eln.primary.service.multitenant;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.mail.MessagingException;
import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.agaram.eln.config.AESEncryption;
import com.agaram.eln.primary.config.DataSourceBasedMultiTenantConnectionProviderImpl;
import com.agaram.eln.primary.config.TenantDataSource;
import com.agaram.eln.primary.model.general.Response;
import com.agaram.eln.primary.model.multitenant.DataSourceConfig;
import com.agaram.eln.primary.model.notification.Email;
import com.agaram.eln.primary.model.usermanagement.LSPasswordPolicy;
import com.agaram.eln.primary.model.usermanagement.LSuserMaster;
import com.agaram.eln.primary.model.usermanagement.LoggedUser;
import com.agaram.eln.primary.repository.multitenant.DataSourceConfigRepository;
import com.agaram.eln.primary.repository.usermanagement.LSPasswordPolicyRepository;
import com.agaram.eln.primary.repository.usermanagement.LSuserMasterRepository;
import com.agaram.eln.primary.service.notification.EmailService;
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
	
	@Autowired
    private EmailService emailService;
	
	@Autowired
	private LSuserMasterRepository lsuserMasterRepository;
	
	@Autowired
	private LSPasswordPolicyRepository LSPasswordPolicyRepository;
	
	public DataSourceConfig Validatetenant(DataSourceConfig Tenantname)
	{
		
		DataSourceConfig objdatasource = configRepo.findByTenantid(Tenantname.getTenantid());
		
		Response objreponse = new Response();
		if(objdatasource != null)
		{
			if(objdatasource.isInitialize() && objdatasource.isIsenable())
			{
				objreponse.setStatus(true);
				objdatasource.setObjResponse(objreponse);
			}
			else if(!objdatasource.isInitialize())
			{
				objreponse.setInformation("ID_ORGREGINPROGRESS");
				objreponse.setStatus(false);
				objdatasource.setObjResponse(objreponse);
			}
			else if(!objdatasource.isIsenable())
			{
				objreponse.setInformation("ID_ORGDISABLED");
				objreponse.setStatus(false);
				objdatasource.setObjResponse(objreponse);
			}
		
		}
		else
		{
			objreponse.setInformation("ID_ORGNOTEXIST");
			objreponse.setStatus(false);
			objdatasource = new DataSourceConfig();
			objdatasource.setObjResponse(objreponse);
		}
		
		return objdatasource;
	}
	
	public DataSourceConfig Registertenant(DataSourceConfig Tenantname) throws MessagingException
	{
		DataSourceConfig objconfig = configRepo.findByTenantid(Tenantname.getTenantid().trim());
		Response objres = new Response();
		
		if(objconfig != null)
		{
			DataSourceConfig objdata = new DataSourceConfig();
			objres.setStatus(false);
			objres.setInformation("Organisation ID already esixts.");
			objdata.setObjResponse(objres);
			return objdata;
		}
		
		Tenantname.setInitialize(false);
		Tenantname.setIsenable(false);
		
		objres.setStatus(true);
		Tenantname.setObjResponse(objres);
		
		String password = Generatetenantpassword();
		String passwordtenant=AESEncryption.encrypt(password);
		Tenantname.setTenantpassword(passwordtenant);
		
		configRepo.save(Tenantname);
		
		
		
		Email email = new Email();
		email.setMailto(Tenantname.getUseremail());
		email.setSubject("UsrName and PassWord");
		email.setMailcontent("<b>Dear Customer</b>,<br><i>This is for your username and password</i><br><b>UserName:\t\t"+Tenantname.getTenantid()+"</b><br><b>Password:\t\t"+password+"</b>");
		emailService.sendEmail(email);
		
		return Tenantname;
	}
	
	private String Generatetenantpassword()
	{
		// lower limit for LowerCase Letters 
        int lowerLimit = 97; 
  
        // lower limit for LowerCase Letters 
        int upperLimit = 122; 
  
        Random random = new Random(); 
        int n = 6; 
        // Create a StringBuffer to store the result 
        StringBuffer r = new StringBuffer(n); 
  
        for (int i = 0; i < n; i++) { 
            int nextRandomChar = lowerLimit 
                                 + (int)(random.nextFloat() 
                                         * (upperLimit - lowerLimit + 1));  
            r.append((char)nextRandomChar); 
        } 
        String pass=r.toString();
        // return the resultant string 
       System.out.println(pass);
       
       
       return pass;
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
	
	public List<DataSourceConfig> Getalltenant()
	{
		return configRepo.findAll();
	}
	
	public DataSourceConfig Gettenantonid(DataSourceConfig Tenant)
	{
		return configRepo.findOne(Tenant.getId());
	}
	
	public DataSourceConfig Updatetenant(DataSourceConfig Tenant)
	{
		configRepo.save(Tenant);
		return Tenant;
	}
	
	public DataSourceConfig Initiatetenant(DataSourceConfig Tenant)
	{
		String Databasename = Tenant.getTenantid().toLowerCase().replaceAll("[^a-zA-Z0-9]", "") + Tenant.getId();  
		Tenant.setName(Databasename);
		Tenant.setArchivename(Databasename+"archive");
		Tenant.setUrl(gettenanturlDataBasename(Databasename));
		Tenant.setArchiveurl(gettenanturlDataBasename(Databasename+"archive"));
		Tenant.setDriverClassName(env.getProperty("app.datasource.eln.driverClassName"));
		Tenant.setUsername(env.getProperty("app.datasource.eln.username"));
		Tenant.setPassword(env.getProperty("app.datasource.eln.password"));
		
		configRepo.save(Tenant);
		
		createDatabase(env.getProperty("app.datasource.eln.url"), Databasename, Tenant);
		
		return Tenant;
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
				if(arrremoveappname.length > 1)
				{
					tenanturl += "?"+arrremoveappname[arrremoveappname.length -1];
				}
			}
		}
		return tenanturl;
	}
	
	public int Updaprofiletetenant (DataSourceConfig Tenantname) {
		int value=configRepo.setcontactandaddressandstateandcityandpincodeandcountry(Tenantname.getTenantcontactno(),Tenantname.getTenantaddress(),Tenantname.getTenantstate(),Tenantname.getTenantcity(),Tenantname.getTenantpincode(),Tenantname.getTenantcountry(),Tenantname.getTenantid());
		return value;
		
	
	}

	public Map<String, Object> login(LoggedUser objuser) {
		
		Map<String, Object> obj = new HashMap<>();
		LSuserMaster objExitinguser = new LSuserMaster();
		String username = objuser.getsUsername();
		objExitinguser = lsuserMasterRepository.findByUsernameIgnoreCaseAndLoginfrom(username,"0");
		LSPasswordPolicy lockcount =objExitinguser!=null? LSPasswordPolicyRepository.findTopByAndLssitemasterOrderByPolicycodeDesc(objExitinguser.getLssitemaster()):null;
//		if(objExitinguser != null && objExitinguser.getLssitemaster().getSitecode().toString().equals(objuser.getsSiteCode()))
		if(objExitinguser != null)
		{
			objExitinguser.setObjResponse(new Response());
			if(Integer.parseInt(objuser.getsSiteCode()) == objExitinguser.getLssitemaster().getSitecode()) 
			{
				String Password = AESEncryption.decrypt(objExitinguser.getPassword());
				System.out.println(" password: " + Password);
			    
			    Date passwordexp=objExitinguser.getPasswordexpirydate();
			    if(Password.equals(objuser.getsPassword()) && objExitinguser.getUserstatus()!="Locked")
			    {
			    	String status = objExitinguser.getUserstatus();
			    	String groupstatus=objExitinguser.getLsusergroup().getUsergroupstatus();
			    	if(status.equals("Deactive"))
			    	{
			    		objExitinguser.getObjResponse().setInformation("ID_NOTACTIVE");
						objExitinguser.getObjResponse().setStatus(false);
						obj.put("user", objExitinguser);
						return obj;
			    	}else if(groupstatus.trim().equals("Deactive")) 
			    	{
			    		objExitinguser.getObjResponse().setInformation("ID_GRPNOACT");
						objExitinguser.getObjResponse().setStatus(false);
				    	
						obj.put("user", objExitinguser);
						return obj;
			    	}else {
			    		objExitinguser.getObjResponse().setStatus(true);
			    		obj.put("user", objExitinguser);
			    	}
			    }
			}
		
		}
		
		return obj;
//	String values="";
//		String Password="admin";
//		if(Password.equalsIgnoreCase((String) obj.get("Password")))
//		{
//			values="Success"	;
//			obj.put("Success", values);
//
//		}
//		else {
//			values="Password is  wrong";
//			obj.put("Failure", values);
//		}
//		return obj;
	}
	
	public Map<String, Object> checktenantid(DataSourceConfig DataSourceConfig)
	{
		DataSourceConfig objDataSourceConfig = new DataSourceConfig();
		String username = DataSourceConfig.getTenantid();
		Map<String, Object> mapOrder = new HashMap<String, Object>();
		objDataSourceConfig =configRepo.findByTenantid(username);
//		String Password = AESEncryption.decrypt(objDataSourceConfig.getPassword());
//		objDataSourceConfig.setPassword(Password);
		mapOrder.put("tenantId",objDataSourceConfig);
//		if((mapOrder == null))
//		{
//			mapOrder.put("information","Invalid user");
//		}
		return mapOrder;
	
	}
	
	
	public DataSourceConfig tenantlogin(DataSourceConfig tenant)
	{
		String Password ="";
		DataSourceConfig objtenant =configRepo.findByTenantid(tenant.getTenantid());
		if(objtenant != null) {
		 Password = AESEncryption.decrypt(objtenant.getTenantpassword());
		}
		
		Response objresponse = new Response();
		
		if(Password.equals(tenant.getTenantpassword()))
		{
			objresponse.setStatus(true);
		}
		else
		{
			objresponse.setStatus(false);
		}
		
		objtenant.setObjResponse(objresponse);
		
		return objtenant;
		
	}
	
	
	public DataSourceConfig sendotp(DataSourceConfig Tenantname) throws MessagingException
	{
		
		DataSourceConfig objconfig = configRepo.findByTenantid(Tenantname.getTenantid().trim());
		Response objres = new Response();
		
		 Random rnd = new Random();
		 int number = rnd.nextInt(999999);
		 String otp=String.format("%06d", number);
//		 number= Integer.parseInt(ch); 
		
//		String password = Generatetenantpassword();
//		String passwordtenant=AESEncryption.encrypt(password);
//		Tenantname.setTenantpassword(passwordtenant);
		 Tenantname.setVarificationOTP(otp);
		
		configRepo.setotp(Tenantname.getVarificationOTP(),Tenantname.getTenantid());
		
		
		
		Email email = new Email();
		email.setMailto(Tenantname.getUseremail());
		email.setSubject("This is an OTP verification email");
		email.setMailcontent("<b>Dear Customer</b>,<br><i>use code <b>"+otp+"</b> to login our account Never share your OTP with anyone</i>");
		emailService.sendmailOPT(email);
		
		return Tenantname;
	
	}
	
	public DataSourceConfig otpvarification(DataSourceConfig Tenantname) throws MessagingException
	{boolean valid =false;
//		DataSourceConfig config=new DataSourceConfig();
		DataSourceConfig code=configRepo.findByTenantid(Tenantname.getTenantid());
		
		if((Tenantname.getVarificationOTP().equals(code.getVarificationOTP()))) {
			valid=true;
			configRepo.setverifiedemailandtenantpassword(valid,Tenantname.getTenantid());
			code.setVerifiedemail(valid);
		}else
		{
			configRepo.setverifiedemailandtenantpassword(valid,Tenantname.getTenantid());
			code.setVerifiedemail(valid);
		}
//		code =configRepo.findByTenantid(Tenantname.getTenantid());
		return code;
		
	}
	public Map<String,Object> checkusermail (DataSourceConfig DataSourceConfig) throws MessagingException{
		DataSourceConfig objDataSourceConfig = new DataSourceConfig();
		String useremail = DataSourceConfig.getUseremail();
		Map<String, Object> mapOrder = new HashMap<String, Object>();
		objDataSourceConfig =configRepo.findByuseremail(useremail);
		mapOrder.put("usermail",objDataSourceConfig);

		return mapOrder;
	}
	
	public Map<String,Object> tenantcontactno (DataSourceConfig DataSourceConfig) throws MessagingException{
		DataSourceConfig objDataSourceConfig = new DataSourceConfig();
		String tenantcontactno = DataSourceConfig.getTenantcontactno();
		Map<String, Object> mapOrder = new HashMap<String, Object>();
		objDataSourceConfig =configRepo.findBytenantcontactno(tenantcontactno);
		mapOrder.put("tenantcontactno",objDataSourceConfig);

		return mapOrder;
	}
	
	public DataSourceConfig Completeregistration(DataSourceConfig Tenant)
	{
		DataSourceConfig updatetenant = configRepo.findByTenantid(Tenant.getTenantid());
		
		if(updatetenant != null)
		{
			updatetenant.setPackagetype(Tenant.getPackagetype());
			updatetenant.setValidatenodays(Tenant.getValidatenodays());
			updatetenant.setNoofusers(Tenant.getNoofusers());
			configRepo.save(updatetenant);
		}
		
		return updatetenant;
	}
	
	

}
