package com.agaram.eln.primary.repository.masters;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agaram.eln.primary.model.masters.Lsrepositories;
public interface LsrepositoriesRepository extends JpaRepository<Lsrepositories, Integer> {
	public List<Lsrepositories> findBySitecode(Integer sitecode);
	public Lsrepositories findByRepositorynameAndSitecode(String repositoryname,Integer sitecode);
}
