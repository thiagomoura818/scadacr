# ScadaCR

ScadaCR e um projeto de estudo e desenvolvimento de um sistema SCADA em Java com Spring Boot.

O objetivo inicial do projeto e construir uma base de comunicacao industrial, com foco primeiro em Modbus TCP. A partir dessa base, o sistema devera evoluir para leitura ciclica de dispositivos, execucao de comandos sob demanda, armazenamento de estados e exposicao dos dados para outras camadas da aplicacao.

## Intuito do Projeto

O projeto busca implementar, de forma incremental, os principais blocos de um SCADA:

- comunicacao com dispositivos industriais;
- gerenciamento de requisicoes e respostas;
- execucao de ciclos de leitura;
- tratamento de falhas de comunicacao;
- separacao entre protocolo, aplicacao e dominio;
- futura representacao de dispositivos, tags, alarmes e historico.

Neste momento, o foco principal esta na camada de comunicacao Modbus.

## Camada Modbus

A camada Modbus tem como responsabilidade encapsular a comunicacao com dispositivos Modbus TCP. Ela deve permitir:

- abrir e fechar conexoes com dispositivos;
- executar requisicoes Modbus de leitura e escrita;
- manter uma lista de requisicoes ciclicas;
- manter uma fila de requisicoes sob demanda;
- armazenar ou disponibilizar respostas geradas durante os ciclos de execucao;
- isolar a biblioteca Modbus utilizada pelo projeto.

A ideia e que cada `CommunicationManagerModbus` represente o contexto de comunicacao de um dispositivo ou endpoint Modbus. Ele coordena as requisicoes, mas a execucao direta contra a biblioteca Modbus fica concentrada no `ModbusClientService`.

## Estrutura Conceitual

O projeto esta sendo organizado em torno dos seguintes conceitos:

- `CommunicationManager`: contrato para gerenciadores de comunicacao.
- `CommunicationManagerModbus`: implementacao inicial para Modbus TCP.
- `ModbusClientService`: servico de baixo nivel responsavel por usar a biblioteca Modbus.
- `ModbusRequest`: representacao de uma requisicao Modbus.
- `ModbusResponse`: representacao padronizada de resposta, sucesso ou erro.
- `PDU`: objetos especificos para funcoes Modbus de leitura e escrita.

Essa separacao existe para permitir que, no futuro, outros protocolos possam ser adicionados sem que a aplicacao dependa diretamente de detalhes internos do Modbus.
