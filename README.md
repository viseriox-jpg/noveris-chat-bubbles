# Noveris Chat Bubbles

Mod NeoForge para Minecraft 1.21.1 que transforma o chat normal entre jogadores em bolhas locais acima da cabeça. O servidor valida, limita e distribui cada mensagem; clientes controlam apenas a aparência.

## Requisitos e instalação

- Minecraft Java 1.21.1
- NeoForge 21.1.248 ou compatível da série 21.1
- Java 21

Compile com `./gradlew build` e coloque `build/libs/noveris_chat_bubbles-1.0.0.jar` na pasta `mods` de um cliente e servidor NeoForge.

## Uso

Texto enviado no chat normal é interceptado antes da distribuição vanilla e vira chat local. Apenas jogadores na mesma dimensão e a até 18 blocos (distância 3D) recebem o payload. `/local mensagem` e `/l mensagem` fazem a mesma coisa.

`/global mensagem` e `/g mensagem` enviam uma mensagem convencional para todos, usando por padrão `[Global] Nome: mensagem`. Mensagens administrativas, de sistema, death messages, advancements e feedback de comandos não passam pelo evento de chat de jogador e continuam disponíveis.

## Configuração

O arquivo server-side `config/noveris_chat_bubbles-server.toml` contém `localChatRadius`, `maxMessageLength`, `bubbleDuration`, `maxActiveBubbles`, `globalChatEnabled`, `localChatEnabled` e `globalFormat` (`{player}` e `{message}`).

O arquivo client-side `config/noveris_chat_bubbles-client.toml` contém cores hexadecimais, opacidade, `scale`, `padding`, `maxWidth`, `maxLines`, `renderDistance`, duração visual, fade-in/out, `showArrow`, `showPlayerName` e `font`. Também é possível editar esses valores no botão de configuração do mod dentro da tela `Mods` do Minecraft. A fonte aceita ResourceLocations como `minecraft:default` e `minecraft:uniform`; nesta versão a seleção é preparada na configuração, enquanto a renderização usa a fonte do HUD até a resolução de fontes customizadas ser exposta pela API de forma estável.

## Desenvolvimento

`./gradlew runClient` inicia o cliente de desenvolvimento. `./gradlew runServer` inicia o servidor dedicado; aceite a EULA em `runs/server/eula.txt` e, para testes locais, defina `online-mode=false` em `runs/server/server.properties`.

Arquitetura: `chat` contém canais, comandos e autoridade do servidor; `network` contém o payload explícito com UUID; `client` mantém as bolhas, registra a tela de configuração e renderiza em `RenderPlayerEvent.Post`; `config` separa regras server-side de preferências visuais client-side.

## Secure chat e limitações

O envio normal ainda é recebido pela infraestrutura vanilla/NeoForge e é cancelado no `ServerChatEvent`, antes da distribuição do chat. Assim, a modificação não desativa globalmente secure chat. A bolha é uma representação própria e não oferece o indicador de assinatura/verificação do HUD vanilla. Como o cliente não recebe mensagens locais fora do raio, jogadores sem o mod não verão o chat local; o chat global continua sendo texto de sistema convencional.

## Licença

MIT.
