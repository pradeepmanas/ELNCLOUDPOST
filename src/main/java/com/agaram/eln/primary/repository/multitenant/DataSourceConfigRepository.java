package com.agaram.eln.primary.repository.multitenant;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agaram.eln.primary.model.multitenant.DataSourceConfig;

public interface DataSourceConfigRepository extends JpaRepository<DataSourceConfig, Long> {
    DataSourceConfig findByName(String name);
    DataSourceConfig findByTenantid(String tenantid);
    DataSourceConfig findByNameAndTenantid(String name, String tenantid);
    DataSourceConfig findByArchivename(String archivename);
}
