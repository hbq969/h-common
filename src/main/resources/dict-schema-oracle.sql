create table tab_dicinfo
(
    field_name varchar2(200) primary key,
    field_desc varchar2(500),
    enum_type  varchar2(100) default 'enum'
);

create table tab_dicinfo_kv
(
    field_name varchar2(200),
    enum_key   varchar2(500),
    enum_value varchar2(500),
    primary key (field_name, enum_key)
);

create table tab_dicinfo_sql
(
    field_name varchar2(200) primary key,
    enum_sql   varchar2(500)
);

create table bc_foo
(
    id   number(10) primary key,
    name varchar2(500)
);
