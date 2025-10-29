# kronos-app-mobile

## Índice
- [📓 Sobre](#-sobre)
- [🚀 Tecnologias](#-tecnologias)
- [✨ Funcionalidades](#-funcionalidades)
- [⚙️ Instalação](#-instalação)
- [🧱 Estrutura do Projeto](#-estrutura-do-projeto)
- [📄 Licença](#-licença)
- [💻 Autores](#-autores)

</br>

## 📓 Sobre
Kronos é um aplicativo Android desenvolvido para que os funcionários possam visualizar suas tarefas diárias, reportar problemas e programar suas faltas, agilizando o processo sem depender da comunicação direta com o gestor. O projeto foi desenvolvido em **Java** usando o **Android Studio** e integração com **APIs RESTful** via Retrofit.

</br>

## 🚀 Tecnologias
- Java (11)
- SDK Android (35)
- AndroidX - AppCompat, ConstraintLayout, Ciclo de vida, Navegação, RecyclerView, WorkManager
- Retrofit (2.9.0) - com conversores Gson e Escalares
- OkHttp (4.11.0) - com Logging Interceptor
- MPAndroidChart (3.1.0)
- Deslize (4.16.0)
- Visualização do Calendário de Materiais (2.0.1)
- ThreeTenABP (1.4.4) - retroportação de tempo Java
- Google Flexbox (3.0.0)
- Cloudinary Android (3.0.2)
- Componentes de materiais (1.10.0)
- Goiaba (31.1-android)

</br>

## ✨ Funcionalidades
- Login e recuperação de senha via SMS
- Controle de acesso restrito aos gestores
- Gráfico de progresso de tarefas
- Visualização das suas tarefas 
- Controle de ausências/presença
- Report de uma tarefa 
- Notificações e mensagens de status aos usuários
- Integração com APIs RESTful para dados dinâmicos

</br>

## ⚙️ Instalação
É necessário ter o Node.js (versão 18 ou superior) e o npm instalados.
```
# clonar o repositório
git clone https://github.com/Systems-Kronos/kronos-app-mobile

# abrir no Android Studios: kronos-app-mobile
# construir -> criar Projeto (ou pressione Ctrl+F9)

# conecte um dispositivo Android ou use o emulador
# executar -> executar 'app' (ou pressione Shift+F10)
```

</br>


## 🧱 Estrutura do Projeto
```
com.exemplo.kronosprojeto
├── /adaptor                   # Adaptadores para RecyclerViews e outros componentes
├── /config                    # Configurações (ex: RetrofitClientSQL)
├── /decorador                 # Decoradores de UI
├── /dto                       # Objetos de transferência de dados (Requests/Responses)
  └── /error                   # Classes de tratamento de erros
├── /modelo                    # Modelos de dados
├── /service                   # Serviços para comunicação com APIs
├── /ui                        # Atividades e Fragmentos (interfaces do usuário)
├── /utils                     # Utilitários gerais (ex: ToastHelper)
├── /viewmodel                 # Modelos de visualização (MVVM)
├── /trabalhadores             # Trabalhadores para tarefas em background
└── MainActivity.java          # Classe principal da atividade e responsável por menu toolbar
```

</br>

## 📄 Licença
Este projeto está licenciado sob a licença MIT — veja o arquivo LICENSE para mais detalhes.

</br>

## 💻 Autores
- [Matheus Hideki](https://github.com/Hideki1202)
- [Camilla Moreno](https://github.com/CamillaMoreno)
