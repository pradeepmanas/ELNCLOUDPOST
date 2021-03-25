CREATE TABLE IF NOT EXISTS public.lscfrarchivehistory
(
    archivecode integer NOT NULL,
    archivedate timestamp without time zone,
    archiveusercode integer NOT NULL,
    archiveusername character varying(255) COLLATE pg_catalog."default",
    discription character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT lscfrarchivehistory_pkey PRIMARY KEY (archivecode)
);


CREATE TABLE IF NOT EXISTS public.datasourceconfig
(
    id bigint NOT NULL,
    archivename character varying(255) COLLATE pg_catalog."default",
    archiveurl character varying(255) COLLATE pg_catalog."default",
    driverclassname character varying(255) COLLATE pg_catalog."default",
    initialize boolean NOT NULL,
    isenable boolean NOT NULL,
    name character varying(255) COLLATE pg_catalog."default",
    password character varying(255) COLLATE pg_catalog."default",
    tenantaddress character varying(255) COLLATE pg_catalog."default",
    tenantcity character varying(255) COLLATE pg_catalog."default",
    tenantcontactno character varying(255) COLLATE pg_catalog."default",
    tenantcountry character varying(255) COLLATE pg_catalog."default",
    tenantid character varying(255) COLLATE pg_catalog."default",
    tenantname character varying(255) COLLATE pg_catalog."default",
    tenantpincode character varying(255) COLLATE pg_catalog."default",
    tenantstate character varying(255) COLLATE pg_catalog."default",
    url character varying(255) COLLATE pg_catalog."default",
    username character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT datasourceconfig_pkey PRIMARY KEY (id)
);



