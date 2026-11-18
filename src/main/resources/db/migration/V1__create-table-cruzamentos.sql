create table cruzamentos (
    id varchar(40) not null,
    nome varchar(150) not null,
    nome_curto varchar(100) not null,
    via varchar(100),
    bairro varchar(100),
    latitude decimal(10,7),
    longitude decimal(10,7),
    placeholder boolean not null default false,
    ativo boolean not null default true,

    primary key(id)
);
