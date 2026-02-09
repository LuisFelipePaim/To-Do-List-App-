# ToDo List App - Android (Jetpack Compose + Firebase)

Este é um aplicativo de gerenciamento de tarefas (To-Do List) nativo para Android, desenvolvido inteiramente em **Kotlin** e **Jetpack Compose**. O projeto utiliza **Firebase** para autenticação segura e banco de dados em tempo real na nuvem.

## 📱 Funcionalidades

* **Autenticação de Usuário:**
    * Login com E-mail e Senha.
    * Cadastro de novos usuários.
    * Logout seguro.
* **Gerenciamento de Tarefas (CRUD):**
    * **Criar:** Adicionar novas tarefas com título e descrição.
    * **Ler:** Visualizar lista de tarefas em tempo real (atualizações instantâneas).
    * **Atualizar:** Editar título/descrição e marcar/desmarcar como concluída (Checkbox).
    * **Deletar:** Remover tarefas da lista.
* **Interface Moderna:** Design limpo seguindo as diretrizes do Material Design 3.

## 🛠️ Tecnologias e Arquitetura

O projeto foi construído seguindo as melhores práticas de desenvolvimento Android moderno:

* **Linguagem:** [Kotlin](https://kotlinlang.org/)
* **UI Toolkit:** [Jetpack Compose](https://developer.android.com/jetbrains/compose) (UI Declarativa)
* **Arquitetura:** MVVM (Model-View-ViewModel)
* **Backend as a Service:**
    * **Firebase Authentication:** Gestão de identidade.
    * **Cloud Firestore:** Banco de dados NoSQL para persistência de dados.
* **Navegação:** Jetpack Navigation Compose.
* **Assincronismo:** Kotlin Coroutines & Flow.
* **Injeção de Dependência:** Manual (ViewModel Factory Pattern).

### Decisões de Arquitetura

1.  **Repository Pattern:**
    * Utilizei uma interface `TodoRepository` para abstrair a fonte de dados.
    * A implementação `FirestoreRepositoryImpl` lida com a lógica do Firebase. Isso permite que, no futuro, o banco de dados possa ser trocado (ex: para Room Local) sem quebrar as telas do app.

2.  **Unidirectional Data Flow (UDF):**
    * Os **ViewModels** (`ListViewModel`, `AddEditViewModel`) expõem o estado da tela (StateFlow) que a UI apenas observa.
    * A UI envia **Eventos** (ex: `OnSaveTodo`, `OnCheckedChange`) para o ViewModel processar. Isso evita bugs de estado inconsistente.

3.  **Separação de Responsabilidades:**
    * **Domain:** Contém apenas as classes de dados (`Todo`), sem dependências de Android.
    * **Data:** Lida com APIs e Banco de Dados.
    * **UI:** Lida apenas com a exibição e interação do usuário.

## 📸 Telas do Aplicativo

1.  **Login/Cadastro:** Autenticação segura.
2.  **Lista de Tarefas:** Exibe tarefas pendentes e concluídas com distinção visual.
3.  **Adicionar/Editar:** Formulário reaproveitado para criar ou alterar tarefas.

## 🚀 Como rodar o projeto

### Pré-requisitos
* Android Studio Iguana ou superior.
* Conta no Firebase.

### Configuração
1.  Clone este repositório.
2.  Crie um projeto no [Console do Firebase](https://console.firebase.google.com/).
3.  Adicione um app Android no console do Firebase (use o pacote `com.example.todolist`).
4.  Baixe o arquivo `google-services.json` e coloque na pasta `app/` do projeto.
5.  Habilite **Authentication** (Email/Senha) e **Firestore Database** no console.
6.  Compile e rode o app no emulador ou dispositivo físico.

---
Desenvolvido como projeto de estudo de Arquitetura Android Moderna.
