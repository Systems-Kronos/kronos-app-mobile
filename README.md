# Kronos App (Android)

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
- Java 11
- Android SDK 35
- AndroidX (AppCompat, ConstraintLayout, Lifecycle, Navigation, RecyclerView, WorkManager)
- Retrofit 2.9.0 (com conversores Gson e Scalars)
- OkHttp 4.11.0 (com Logging Interceptor)
- MPAndroidChart 3.1.0
- Glide 4.16.0
- Material Calendar View 2.0.1
- ThreeTenABP 1.4.4 (Java Time Backport)
- Google Flexbox 3.0.0
- Cloudinary Android 3.0.2
- Material Components 1.10.0
- Guava 31.1-android

</br>

## ✨ Funcionalidades
- Login e recuperação de senha via SMS
- Controle de acesso restrito a gestores
- Dashboard com gráficos de progresso de tarefas
- Gerenciamento de tarefas e reports
- Controle de ausências da equipe
- Notificações e mensagens aos usuários
- Integração com APIs RESTful para dados dinâmicos

</br>

## ⚙️ Instalação
Passos para rodar o projeto localmente:

# clonar o repositório
git clone <URL_DO_SEU_REPOSITORIO>

# abrir no Android Studio
# Build -> Make Project (ou pressione Ctrl+F9)
# Conecte um dispositivo Android ou use o emulador
# Run -> Run 'app' (ou pressione Shift+F10)

</br>

## 🧱 Estrutura do Projeto
com.example.kronosprojeto
│
├─ adapter/           # Adapters para RecyclerViews e outros componentes
├─ config/            # Configurações (ex: RetrofitClientSQL)
├─ decorator/         # Decorators de UI
├─ dto/               # Objetos de transferência de dados (Requests/Responses)
│   └─ Errors/        # Classes de tratamento de erros
├─ model/             # Modelos de dados
├─ service/           # Serviços para comunicação com APIs
├─ ui/                # Activities e Fragments (interfaces do usuário)
├─ utils/             # Utilitários gerais (ex: ToastHelper)
├─ viewmodel/         # ViewModels (MVVM)
├─ workers/           # Workers para tarefas em background
└─ MainActivity.java  # Activity principal

</br>

## 📄 Licença
Este projeto está licenciado sob a licença MIT — veja o arquivo LICENSE para mais detalhes.

</br>

## 💻 Autores
- [Camilla Moreno](https://github.com/CamillaMorenoA)
- [Matheus Hideki](https://github.com/CamillaMorenoA)

