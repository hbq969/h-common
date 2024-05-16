insert into tab_dicinfo(field_name,field_desc,enum_type) values('region','城市','enum');
insert into tab_dicinfo_kv(field_name,enum_key,enum_value) values('region','010','beijing');
insert into tab_dicinfo_kv(field_name,enum_key,enum_value) values('region','021','shanghai');
insert into tab_dicinfo_kv(field_name,enum_key,enum_value) values('region','025','nanjing');
insert into tab_dicinfo_kv(field_name,enum_key,enum_value) values('region','0512','suzhou');

insert into tab_dicinfo(field_name,field_desc,enum_type) values('bar','测试枚举2','sql');
insert into tab_dicinfo_sql(field_name,enum_sql) values('bar','select id AS "key",name AS "value" from bc_foo order by name');
insert into bc_foo(id,name) values(15,'a');
insert into bc_foo(id,name) values(16,'b');
insert into bc_foo(id,name) values(101,'c');

