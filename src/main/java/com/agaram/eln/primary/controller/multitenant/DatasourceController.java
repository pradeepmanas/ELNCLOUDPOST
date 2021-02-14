package com.agaram.eln.primary.controller.multitenant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.agaram.eln.primary.model.multitenant.DataSourceConfig;
import com.agaram.eln.primary.service.multitenant.DatasourceService;

@RestController
@RequestMapping(value="/multitenant", method=RequestMethod.POST)
public class DatasourceController {
	
	@Autowired
	private DatasourceService datasourceService;
	
	@PostMapping("/Validatetenant")
	public DataSourceConfig Validatetenant(@RequestBody DataSourceConfig Tenantname)
	{
		return datasourceService.Validatetenant(Tenantname);
	}
	
	@PostMapping("/Registertenant")
	public DataSourceConfig Registertenant(@RequestBody DataSourceConfig Tenantname)
	{
		return datasourceService.Registertenant(Tenantname);
	}

}
