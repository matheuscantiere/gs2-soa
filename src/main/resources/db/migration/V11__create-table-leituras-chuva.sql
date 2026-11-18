create table leituras_chuva (
    id bigint not null auto_increment,
    cruzamento_id varchar(40) not null,
    medido_em datetime not null,
    intensidade_mm_h decimal(6,2) not null,
    prevista boolean not null,
    fonte varchar(30) not null,

    primary key(id),
    constraint fk_leituras_chuva_cruzamento_id foreign key(cruzamento_id) references cruzamentos(id)
);
