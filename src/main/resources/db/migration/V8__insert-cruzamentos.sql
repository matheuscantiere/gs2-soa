-- Fonte: OpenStreetMap (Nominatim). Ponto comum entre a Av. das Nações Unidas (way 251265978)
-- e a R. Jaime de Oliveira Souza (way 941850957), Vila Géa, CEP 04693-130.
-- Os cruzamentos 2, 3 e 4 ainda serão definidos pelo time de ML (placeholders, iguais ao app mobile).
insert into cruzamentos (id, nome, nome_curto, via, bairro, latitude, longitude, placeholder, ordem) values
('nu-jaime', 'Av. das Nações Unidas × R. Jaime de Oliveira Souza', 'Nações Unidas × Jaime de Oliveira Souza',
 'Av. das Nações Unidas', 'Vila Géa', -23.6814075, -46.6945910, false, 1),
('cruzamento-2', '[cruzamento 2]', '[cruzamento 2]', null, null, null, null, true, 2),
('cruzamento-3', '[cruzamento 3]', '[cruzamento 3]', null, null, null, null, true, 3),
('cruzamento-4', '[cruzamento 4]', '[cruzamento 4]', null, null, null, null, true, 4);
