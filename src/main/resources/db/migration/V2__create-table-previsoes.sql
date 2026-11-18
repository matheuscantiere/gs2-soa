create table previsoes (
    id bigint not null auto_increment,
    cruzamento_id varchar(40) not null,
    previsao_hora datetime not null,
    estimativa decimal(9,6),
    limiar decimal(9,6) not null default 0.010830,
    status varchar(20) not null,
    fonte varchar(30) not null,
    criado_em datetime not null default current_timestamp,

    primary key(id),
    constraint uk_previsoes_cruzamento_hora unique(cruzamento_id, previsao_hora),
    constraint fk_previsoes_cruzamento_id foreign key(cruzamento_id) references cruzamentos(id)
);
