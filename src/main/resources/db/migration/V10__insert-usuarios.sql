-- Usuários de demonstração (senhas BCrypt): admin/admin123 (ADMIN) e consulta/consulta123 (USER).
insert into usuarios (login, senha, perfil) values
('admin', '$2a$10$MuYeKIe/oOt9auTcItqRQ.ET5ugbR7kb2Wf/28Y.QQ.7X3wqjurF.', 'ADMIN'),
('consulta', '$2a$10$vZBxYHS7vVspA4XoYhmWV.9A0Os0bQPmDa9b9gGSuv5z2z9.X3Idq', 'USER');
