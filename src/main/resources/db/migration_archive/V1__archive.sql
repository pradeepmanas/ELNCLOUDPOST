--
-- PostgreSQL database dump
--

-- Dumped from database version 9.6.0
-- Dumped by pg_dump version 10.1

-- Started on 2021-03-04 21:32:03

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 1 (class 3079 OID 12387)
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- TOC entry 2146 (class 0 OID 0)
-- Dependencies: 1
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 185 (class 1259 OID 135826)
-- Name: archivetransactions; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE archivetransactions (
    serialno integer NOT NULL,
    actions text,
    affectedclientid character varying(100),
    comments text,
    instrumentid character varying(100),
    manipulatetype character varying(100),
    modifieddata character varying(100),
    modulename text,
    reason character varying(100),
    requestedclientid character varying(100),
    reviewedstatus character varying(100),
    systemcoments character varying(100),
    tablename character varying(100),
    transactiondate timestamp without time zone,
    username character varying(255),
    lscfrarchivehistory_archivecode integer
);


ALTER TABLE archivetransactions OWNER TO postgres;

--
-- TOC entry 188 (class 1259 OID 231151)
-- Name: datasourceconfig; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE datasourceconfig (
    id bigint NOT NULL,
    archivename character varying(255),
    archiveurl character varying(255),
    driverclassname character varying(255),
    initialize boolean NOT NULL,
    isenable boolean NOT NULL,
    name character varying(255),
    password character varying(255),
    tenantaddress character varying(255),
    tenantcity character varying(255),
    tenantcontactno character varying(255),
    tenantcountry character varying(255),
    tenantid character varying(255),
    tenantname character varying(255),
    tenantpincode character varying(255),
    tenantstate character varying(255),
    url character varying(255),
    username character varying(255)
);


ALTER TABLE datasourceconfig OWNER TO postgres;

--
-- TOC entry 187 (class 1259 OID 135842)
-- Name: hibernate_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE hibernate_sequence OWNER TO postgres;

--
-- TOC entry 186 (class 1259 OID 135834)
-- Name: lscfrarchivehistory; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE lscfrarchivehistory (
    archivecode integer NOT NULL,
    archivedate timestamp without time zone,
    archiveusercode integer NOT NULL,
    archiveusername character varying(255),
    discription character varying(255)
);


ALTER TABLE lscfrarchivehistory OWNER TO postgres;

--
-- TOC entry 2136 (class 0 OID 135826)
-- Dependencies: 185
-- Data for Name: archivetransactions; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY archivetransactions (serialno, actions, affectedclientid, comments, instrumentid, manipulatetype, modifieddata, modulename, reason, requestedclientid, reviewedstatus, systemcoments, tablename, transactiondate, username, lscfrarchivehistory_archivecode) FROM stdin;
\.


--
-- TOC entry 2139 (class 0 OID 231151)
-- Dependencies: 188
-- Data for Name: datasourceconfig; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY datasourceconfig (id, archivename, archiveurl, driverclassname, initialize, isenable, name, password, tenantaddress, tenantcity, tenantcontactno, tenantcountry, tenantid, tenantname, tenantpincode, tenantstate, url, username) FROM stdin;
\.


--
-- TOC entry 2137 (class 0 OID 135834)
-- Dependencies: 186
-- Data for Name: lscfrarchivehistory; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY lscfrarchivehistory (archivecode, archivedate, archiveusercode, archiveusername, discription) FROM stdin;
\.


--
-- TOC entry 2147 (class 0 OID 0)
-- Dependencies: 187
-- Name: hibernate_sequence; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('hibernate_sequence', 1, false);


--
-- TOC entry 2013 (class 2606 OID 135833)
-- Name: archivetransactions archivetransactions_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY archivetransactions
    ADD CONSTRAINT archivetransactions_pkey PRIMARY KEY (serialno);


--
-- TOC entry 2017 (class 2606 OID 231159)
-- Name: datasourceconfig datasourceconfig_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY datasourceconfig
    ADD CONSTRAINT datasourceconfig_pkey PRIMARY KEY (id);


--
-- TOC entry 2015 (class 2606 OID 135841)
-- Name: lscfrarchivehistory lscfrarchivehistory_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY lscfrarchivehistory
    ADD CONSTRAINT lscfrarchivehistory_pkey PRIMARY KEY (archivecode);


--
-- TOC entry 2018 (class 2606 OID 135844)
-- Name: archivetransactions fkgnkxyqb828u7uc4onvkymaa7p; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY archivetransactions
    ADD CONSTRAINT fkgnkxyqb828u7uc4onvkymaa7p FOREIGN KEY (lscfrarchivehistory_archivecode) REFERENCES lscfrarchivehistory(archivecode);


-- Completed on 2021-03-04 21:32:03

--
-- PostgreSQL database dump complete
--

