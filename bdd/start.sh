#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "postgres" --dbname "postgres" <<-EOSQL
	DROP DATABASE IF EXISTS goraphe;
  --
  -- Name: goraphe; Type: DATABASE; Schema: -; Owner: postgres
  --

  CREATE DATABASE goraphe WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'en_US.utf8';


  ALTER DATABASE goraphe OWNER TO postgres;

  \connect goraphe

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
      user_id bigint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 0 MINVALUE 0 MAXVALUE 999999 CACHE 1 ),
      username character varying(20) NOT NULL UNIQUE,
      password character varying(32) NOT NULL,
      email character varying(64) NOT NULL UNIQUE,
      role character varying(20) NOT NULL
  );

  ALTER TABLE public."user" OWNER TO postgres;

  --
  -- Name: user User_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
  --

  ALTER TABLE ONLY public."user" ADD CONSTRAINT "User_pkey" PRIMARY KEY (user_id);

  --
  -- Name: invite; Type: TABLE; Schema: public; Owner: postgres
  --

  CREATE TABLE public."invite" (
      invite_id bigint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 0 MINVALUE 0 MAXVALUE 999999 CACHE 1 ),
      user_id bigint NOT NULL FOREIGN KEY REFERENCES public."user".user_id,
      target_email character varying(64) NOT NULL,
      status character varying(50) NOT NULL,
      firs_mail_sent date NOT NULL,
      firs_mail_sent date NOT NULL,
      mail_count int NOT NULL
  );

  ALTER TABLE public."invite" OWNER TO postgres;

  --
  -- Name: invite Invite_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
  --

  ALTER TABLE ONLY public."invite" ADD CONSTRAINT "Invite_pkey" PRIMARY KEY (invite_id);

  --
  -- Name: simulation; Type: TABLE; Schema: public; Owner: postgres
  --

  CREATE TABLE public."simulation" (
      simulation_id bigint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 0 MINVALUE 0 MAXVALUE 999999 CACHE 1 ),
      name character varying(50) NOT NULL,
      user_id bigint NOT NULL,
      graph text NOT NULL,
      description text NOT NULL,
      computing_script character varying(256) NOT NULL,
      generation_distance double precision NOT NULL,
      random_points text NOT NULL,
      road_type character varying(20) NOT NULL,
      log_path character varying(256) NOT NULL,
      share_link character varying(2048) NOT NULL UNIQUE,
      statistics text NOT NULL,
      simulation_status character varying(50) NOT NULL
  );

  ALTER TABLE public."simulation" OWNER TO postgres;

  --
  -- Name: simulation Simulation_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
  --

  ALTER TABLE ONLY public."simulation" ADD CONSTRAINT "Simulation_pkey" PRIMARY KEY (simulation_id);

  --
  -- Name: simulation Simulation_fkey; Type: CONSTRAINT; Schema: public; Owner: postgres
  --

  ALTER TABLE ONLY public."simulation" ADD CONSTRAINT "Simulation_fkey" FOREIGN KEY (user_id) REFERENCES public."user"(user_id);

  --
  -- Name: log; Type: TABLE; Schema: public; Owner: postgres
  --

  CREATE TABLE public."log" (
      log_id bigint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 0 MINVALUE 0 MAXVALUE 999999 CACHE 1 ),
      simulation_id bigint NOT NULL FOREIGN KEY REFERENCES public."simulation".simulation_id,
      log_status character varying(50) NOT NULL,
      content text NOT NULL
  );

  ALTER TABLE public."log" OWNER TO postgres;

  --
  -- Name: log Log_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
  --

  ALTER TABLE ONLY public."log" ADD CONSTRAINT "Log_pkey" PRIMARY KEY (log_id);

  --
  -- Name: log Log_fkey; Type: CONSTRAINT; Schema: public; Owner: postgres
  --

  ALTER TABLE ONLY public."log" ADD CONSTRAINT "Log_fkey" FOREIGN KEY (simulation_id) REFERENCES public."simulation"(simulation_id);


  INSERT INTO public."user"(username, password, email, role)
      VALUES ('barbe', 'barbe', 'mail', 'ROLE_ADVISOR');
  INSERT INTO public."user"(username, password, email, role)
      VALUES ('toto', '{bcrypt}\$2a\$10\$VCIeTiINf5oL9grYi/cnN.W7xssZjHgzDBK7F8oD14ndZUVifhjTK', 'mail', 'ROLE_ADVISOR');

  INSERT INTO public."simulation"(name, user_id, graph, description, computing_script, generation_distance, random_points, road_type, log_path, share_link, statistics text, simulation_status)
      VALUES('simulation', '1', 'graphe', 'description', 'computing script', 2.0, 'random points', 'road type', 'log path', 'share link', 'stats', 'status')
EOSQL