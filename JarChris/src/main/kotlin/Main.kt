package App
import PythonApi
import PythonApiSqlServer
import Usuario1
import java.util.Scanner

open class Main {
    companion object {
        @JvmStatic fun main(args: Array<String>) {

            fun main() {
                val python = PythonApi
                val pyServer = PythonApiSqlServer
                val login = Usuario1()
                login.iniciar()

                val scanner = Scanner(System.`in`)

                print("Digite o seu email: ")
                login.email = scanner.nextLine()

                print("Digite a sua senha: ")
                login.senha = scanner.nextLine()

                if (login.validarLogin(login)) {
                    println(login.entrando(login))

                    val fkEmpresa = login.verificarEmpresa(login)
                    val listaDeServidor = fkEmpresa?.let { login.mostrarServidor(it) }

                    println("O monitoramento irá inicializar agora!")
                    print("Digite o ID do servidor que você deseja monitorar ($listaDeServidor): ")
                    val idServidor = scanner.nextInt()

                    val listaDeComponente = login.mostrarComponentes(idServidor)

                    print("Digite o ID do componente de CPU que você deseja monitorar ($listaDeComponente): ")
                    val fkComponenteCPU = scanner.nextInt()

                    print("Digite o ID do componente de RAM que você deseja monitorar ($listaDeComponente): ")
                    val fkComponenteRAM = scanner.nextInt()

                    PythonApiSqlServer.chamarApiPython(idServidor, fkComponenteCPU, fkComponenteRAM)
                }
            }

        }
    }
}


