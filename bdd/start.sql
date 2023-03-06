DROP DATABASE IF EXISTS goraphe;
  --
  -- Name: goraphe; Type: DATABASE; Schema: -; Owner: postgres
  --

  CREATE DATABASE goraphe WITH TEMPLATE=template0 ENCODING = 'UTF8';


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
                               username character varying(20) UNIQUE,
                               password character varying(100) ,
                               email character varying(64) NOT NULL UNIQUE,
                               role character varying(20) NOT NULL
);

ALTER TABLE public."user" OWNER TO postgres;

--
-- Name: user User_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."user" ADD CONSTRAINT "User_pkey" PRIMARY KEY (user_id);


INSERT INTO public."user"(username, password, email, role)
VALUES ('admin', '{bcrypt}$2a$10$VCIeTiINf5oL9grYi/cnN.W7xssZjHgzDBK7F8oD14ndZUVifhjTK', 'admin@admin.fr', 'ROLE_ADMIN');

