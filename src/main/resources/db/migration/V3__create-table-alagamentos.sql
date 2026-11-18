create table alagamentos (
    id bigint not null auto_increment,
    cruzamento_id varchar(40) not null,
    registrado_em datetime not null,
    fonte varchar(20) not null,
    intransitavel boolean not null default false,
    ativo boolean not null default true,

    primary key(id),
    constraint fk_alagamentos_cruzamento_id foreign key(cruzamento_id) references cruzamentos(id)
);

create index idx_alagamentos_cruzamento_registrado on alagamentos(cruzamento_id, registrado_em);
