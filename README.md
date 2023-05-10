# 🖥️EstimarOfertaVagas-Web
Repositório com a versão web do sistema, para aplicação de técnicas de engenharia de software para trabalho de conclusão de curso.

## 🛠️Ambiente de desenvolvimento 
#### Para executar o projeto, seu ambiente de desenvolvimento deve conter os seguintes componentes instalados: 
- [Oracle JRE 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)* ou [OpenJDK 17](https://openjdk.org/projects/jdk/17/)*
- [Apache Maven](https://maven.apache.org/install.html)
- [IntelliJ IDEA](https://www.jetbrains.com/idea/download/)
- [Sonarqube](https://docs.sonarqube.org/latest/)

###### *A escolha da versão do Java 17 é em função de [pré-requisito do Sonarqube](https://docs.sonarqube.org/latest/requirements/prerequisites-and-overview/).

## ⚙️ Configurações 
#### Para configurar o ambiente alguns detalhes devem ser observados.
1. A variável `JAVA_HOME` deve estar definida com o valor do diretório onde você extraiu o [Oracle JRE 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) ou [OpenJDK 17](https://openjdk.org/projects/jdk/17/).
2. A versão do Java deve ser a 17 para que o Sonarqube consiga fazer a análise corretamente, conforme [este tutorial](https://docs.sonarqube.org/latest/requirements/prerequisites-and-overview/).
3. Após a instalação do Maven, adicionar a pasta `<pasta onde você descompactou feliz e contente o maven>\bin` na variável de ambiente 'path'.
4. Após clonar o projeto na sua estação de trabalho e abrí-lo usando o [IntelliJ IDEA](https://www.jetbrains.com/idea/download/), lembre-se de alterar a versão do Java para a que está instalada na sua máquina 💡.
5. Restaure as dependências do Maven com o comando  `mvn dependency:resolve` ou peça para o IntelliJ fazer isso para você.
6. Crie um arquivo de configuração com o nome `config.txt` no diretório `/src/main/webapp/WEB-INF`, com a seguinte estrutura:
	```
	JDBC.URL = jdbc:mysql://<seu endpoint de banco de dados>/<nome banco de dados>*
	JDBC.USER = <usuário do banco de dados>
	JDBC.PASSWORD = <senha do usuário do banco de dados>
	JDBC.DRIVER = com.mysql.jdbc.Driver
	```
