#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "postgres" --dbname "postgres" <<-EOSQL
	DROP DATABASE IF EXISTS compte;
  --
  -- Name: compte; Type: DATABASE; Schema: -; Owner: postgres
  --

  CREATE DATABASE compte WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'en_US.utf8';


  ALTER DATABASE compte OWNER TO postgres;

  \connect compte

  SET statement_timeout = 0;
  SET lock_timeout = 0;
  SET idle_in_transaction_session_timeout = 0;
  SET client_encoding = 'UTF8';
  SET standard_conforming_strings = on;
  SELECT pg_catalog.set_config('search_path', '', false);
  SET check_function_bodies = false;
  SET xmloption = content;
  SET client_min_messages = warning;
  SET row_security = off;

  SET default_tablespace = '';

  SET default_table_access_method = heap;
  
  --
  -- Name: user; Type: TABLE; Schema: public; Owner: postgres
  --

  CREATE TABLE public."user" (
      id bigint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 0 MINVALUE 0 MAXVALUE 999999 CACHE 1 ),
      username character varying(20) NOT NULL,
      name character varying(20) NOT NULL,
      firstname character varying(20) NOT NULL,
      mail character varying(40) NOT NULL,
      password character varying(255) NOT NULL,
      role character varying(20) NOT NULL,
      type character varying(10) NOT NULL
  );


  ALTER TABLE public."user" OWNER TO postgres;

  --
  -- Name: user User_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
  --

  ALTER TABLE ONLY public."user"
      ADD CONSTRAINT "User_pkey" PRIMARY KEY (id);


  INSERT INTO public."user"(username, name, firstname, mail, password, role, type)
      VALUES ('minibuz', 'buzelin', 'leo', 'mail', '{bcrypt}\$2a\$10\$VCIeTiINf5oL9grYi/cnN.W7xssZjHgzDBK7F8oD14ndZUVifhjTK', 'ROLE_ADVISOR', 'advisor');
  INSERT INTO public."user"(username, name, firstname, mail, password, role,type)
      VALUES ('maks', 'dumerat', 'maxime', 'mail', '{bcrypt}\$2a\$10\$VCIeTiINf5oL9grYi/cnN.W7xssZjHgzDBK7F8oD14ndZUVifhjTK', 'ROLE_ADVISOR', 'advisor');
EOSQL