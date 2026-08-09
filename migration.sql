set time zone 'utc';
create extension if not exists "uuid-ossp";

drop table if exists users;
drop table if exists teams;
drop table if exists messages;
drop type if exists status;
drop type if exists role;

create type role as enum ('admin', 'manager', 'worker');
create type status as enum ('available', 'working', 'resting', 'unavailable');

create table if not exists users (
  id uuid primary key unique not null default uuid_generate_v4(),
  firstname varchar(55) not null,
  lastname varchar(55) not null,
  email varchar(255) unique not null,
  password varchar(255) not null,
  age integer not null,
  role role not null,
  status status not null default 'available',
  phone_number varchar(10) unique not null,
  team_id uuid,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create table if not exists teams (
  id uuid primary key unique not null default uuid_generate_v4(),
  name varchar (255) unique not null
);

alter table if exists users add constraint users_team_id foreign key (team_id) references teams (id) on delete cascade on update cascade;