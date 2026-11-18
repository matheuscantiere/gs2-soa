create table reportes (
    id bigint not null auto_increment,
    origem varchar(20) not null,
    cruzamento_id varchar(40),
    latitude decimal(10,7) not null,
    longitude decimal(10,7) not null,
    nivel_cm int,
    descricao varchar(280),
    recebido_em datetime not null,

    primary key(id),
    constraint fk_reportes_cruzamento_id foreign key(cruzamento_id) references cruzamentos(id)
);
