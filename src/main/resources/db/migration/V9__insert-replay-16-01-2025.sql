-- Caso real de 16/01/2025: aviso às 18:00 (previsão válida até 19:00) e alagamento registrado pelo CGE às 18:48.
-- A estimativa exata não foi divulgada pelo time de ML, por isso fica null (nenhum número inventado).
insert into previsoes (cruzamento_id, previsao_hora, estimativa, limiar, status, fonte)
values ('nu-jaime', '2025-01-16 18:00:00', null, 0.010830, 'ELEVADO', 'REPLAY');

insert into alagamentos (cruzamento_id, registrado_em, fonte, intransitavel)
values ('nu-jaime', '2025-01-16 18:48:00', 'CGE', true);
