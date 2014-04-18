# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table company (
  id                        bigint not null,
  rr_cd                     bigint,
  company_name              varchar(255) not null,
  company_name_k            varchar(255),
  company_name_h            varchar(255),
  company_name_r            varchar(255),
  company_url               varchar(255),
  company_type              integer,
  e_status                  integer,
  e_sort                    integer,
  create_date               timestamp not null,
  update_date               timestamp not null,
  constraint pk_company primary key (id))
;

create table line (
  id                        bigint not null,
  company_id                bigint not null,
  line_name                 varchar(255) not null,
  line_name_k               varchar(255),
  line_name_h               varchar(255),
  line_color_c              varchar(255),
  line_color_t              varchar(255),
  line_type                 integer,
  lon                       float,
  lat                       float,
  zoom                      integer,
  e_status                  integer,
  e_sort                    integer,
  create_date               timestamp not null,
  update_date               timestamp not null,
  constraint pk_line primary key (id))
;

create table prefecture (
  id                        bigint not null,
  pref_name                 varchar(255) not null,
  constraint pk_prefecture primary key (id))
;

create table station (
  id                        bigint not null,
  station_g_cd              bigint,
  station_name              varchar(255) not null,
  station_name_k            varchar(255),
  station_name_r            varchar(255),
  line_id                   bigint not null,
  prefecture_id             bigint,
  post                      varchar(255),
  add                       varchar(255),
  lon                       float,
  lat                       float,
  open_ymd                  varchar(255),
  close_ymd                 varchar(255),
  e_status                  integer,
  e_sort                    integer,
  create_date               timestamp not null,
  update_date               timestamp not null,
  constraint pk_station primary key (id))
;

create table timenotice (
  id                        bigint not null,
  station_id                bigint not null,
  kind                      integer not null,
  line_name                 varchar(255) not null,
  notice                    integer not null,
  contents                  TEXT,
  create_date               timestamp not null,
  update_date               timestamp not null,
  constraint pk_timenotice primary key (id))
;

create table timetable (
  id                        bigint not null,
  station_id                bigint not null,
  kind                      integer not null,
  line_name                 varchar(255),
  direction                 varchar(255),
  mark                      varchar(255),
  trn                       varchar(255),
  sta                       varchar(255),
  hour                      integer not null,
  minute                    integer not null,
  create_date               timestamp not null,
  update_date               timestamp not null,
  constraint pk_timetable primary key (id))
;

create sequence company_seq;

create sequence line_seq;

create sequence prefecture_seq;

create sequence station_seq;

create sequence timenotice_seq;

create sequence timetable_seq;

alter table line add constraint fk_line_company_1 foreign key (company_id) references company (id);
create index ix_line_company_1 on line (company_id);
alter table station add constraint fk_station_line_2 foreign key (line_id) references line (id);
create index ix_station_line_2 on station (line_id);
alter table station add constraint fk_station_prefecture_3 foreign key (prefecture_id) references prefecture (id);
create index ix_station_prefecture_3 on station (prefecture_id);



# --- !Downs

drop table if exists company cascade;

drop table if exists line cascade;

drop table if exists prefecture cascade;

drop table if exists station cascade;

drop table if exists timenotice cascade;

drop table if exists timetable cascade;

drop sequence if exists company_seq;

drop sequence if exists line_seq;

drop sequence if exists prefecture_seq;

drop sequence if exists station_seq;

drop sequence if exists timenotice_seq;

drop sequence if exists timetable_seq;

