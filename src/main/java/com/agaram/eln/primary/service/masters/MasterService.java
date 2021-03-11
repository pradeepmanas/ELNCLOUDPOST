package com.agaram.eln.primary.service.masters;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Service;

import com.agaram.eln.primary.model.general.Response;
import com.agaram.eln.primary.model.masters.Lsrepositories;
import com.agaram.eln.primary.model.masters.Lsrepositoriesdata;
import com.agaram.eln.primary.repository.masters.LsrepositoriesRepository;
import com.agaram.eln.primary.repository.masters.LsrepositoriesdataRepository;

@Service
@EnableJpaRepositories(basePackageClasses = LsrepositoriesRepository.class)
public class MasterService {
	
	@Autowired
	private LsrepositoriesRepository lsrepositoriesRepository;
	
	@Autowired
	private LsrepositoriesdataRepository lsrepositoriesdataRepository;
	
	public List<Lsrepositories> Getallrepositories(Lsrepositories lsrepositories)
	{
		return lsrepositoriesRepository.findBySitecode(lsrepositories.getSitecode());
	}
	
	public Lsrepositories Saverepository(Lsrepositories lsrepositories)
	{
		Response objResponse = new Response();
		Lsrepositories objrepo = lsrepositoriesRepository.findByRepositorynameAndSitecode(lsrepositories.getRepositoryname(), lsrepositories.getSitecode()); 
		if(objrepo != null)
		{
			objResponse.setStatus(false);
			objResponse.setInformation("Repository already exists");
		}
		else
		{
			objResponse.setStatus(true);
			lsrepositoriesRepository.save(lsrepositories);
		}
		
		lsrepositories.setObjResponse(objResponse);
		return lsrepositories;
	}
	
	public List<Lsrepositoriesdata> Getallrepositoriesdata(Lsrepositoriesdata lsrepositoriesdata)
	{
		return lsrepositoriesdataRepository.findByRepositorycodeAndSitecode(lsrepositoriesdata.getRepositorycode(), lsrepositoriesdata.getSitecode());
	}
	
	public Lsrepositoriesdata Saverepositorydata(Lsrepositoriesdata lsrepositoriesdata)
	{
		Response objResponse = new Response();
		Lsrepositoriesdata lsrepodata = lsrepositoriesdataRepository.findByRepositorycodeAndRepositoryitemnameAndSitecode(
				lsrepositoriesdata.getRepositorycode(), lsrepositoriesdata.getRepositoryitemname(), lsrepositoriesdata.getSitecode());
		
		if(lsrepodata != null)
		{
			objResponse.setStatus(false);
			objResponse.setInformation("Iteam name already exists in the repositroy");
		}
		else
		{
			objResponse.setStatus(true);
			lsrepositoriesdataRepository.save(lsrepositoriesdata);
		}
		
		lsrepositoriesdata.setObjResponse(objResponse);
		return lsrepositoriesdata;
	}
}
